package com.ruoyi.system.service.finance.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.Seq;
import com.ruoyi.system.domain.finance.Bookkeeping;
import com.ruoyi.system.domain.finance.BookkeepingPayment;
import com.ruoyi.system.domain.finance.FundAccount;
import com.ruoyi.system.domain.finance.vo.BookkeepingStatsVO;
import com.ruoyi.system.mapper.finance.BookkeepingMapper;
import com.ruoyi.system.mapper.finance.FundAccountMapper;
import com.ruoyi.system.service.finance.IBookkeepingService;

/**
 * 记账管理服务实现
 *
 * @author ruoyi
 */
@Service
public class BookkeepingServiceImpl implements IBookkeepingService
{
    private static final String ENTRY_TYPE_INCOME = "1";

    private static final String ENTRY_TYPE_EXPENSE = "2";

    private static final String STATUS_SETTLED = "1";

    private static final String STATUS_PARTIAL = "2";

    private static final String STATUS_PENDING = "3";

    private static final String STATUS_NORMAL = "0";

    private static final String DEL_FLAG_NORMAL = "0";

    @Autowired
    private BookkeepingMapper bookkeepingMapper;

    @Autowired
    private FundAccountMapper fundAccountMapper;

    @Override
    public List<Bookkeeping> selectBookkeepingList(Bookkeeping bookkeeping)
    {
        return bookkeepingMapper.selectBookkeepingList(bookkeeping);
    }

    @Override
    public Bookkeeping selectBookkeepingById(Long bookkeepingId)
    {
        Bookkeeping bookkeeping = bookkeepingMapper.selectBookkeepingById(bookkeepingId);
        if (StringUtils.isNotNull(bookkeeping))
        {
            bookkeeping.setPaymentList(bookkeepingMapper.selectBookkeepingPaymentListByBookkeepingId(bookkeepingId));
        }
        return bookkeeping;
    }

    @Override
    @Transactional
    public int insertBookkeeping(Bookkeeping bookkeeping)
    {
        validateEntryType(bookkeeping.getEntryType());
        // 首次保存时直接把已收/未收/状态一次算完，避免前后端口径不一致。
        fillCreateAmounts(bookkeeping);
        FundAccount fundAccount = resolveFundAccountForSave(bookkeeping.getFundAccountId(), bookkeeping.getEntryType(),
            bookkeeping.getPaidAmount(), null);
        bookkeeping.setEntryNo(generateEntryNo(bookkeeping.getEntryType()));
        fillFundAccountInfo(bookkeeping, fundAccount);
        bookkeeping.setDelFlag(DEL_FLAG_NORMAL);
        int rows = bookkeepingMapper.insertBookkeeping(bookkeeping);
        // 收入单首笔实收、支出单首笔付款都落一条明细，后续核销继续往明细表追加。
        syncPaymentsAfterSave(bookkeeping, null, List.of());
        return rows;
    }

    @Override
    @Transactional
    public int updateBookkeeping(Bookkeeping bookkeeping)
    {
        Bookkeeping existing = requireBookkeeping(bookkeeping.getBookkeepingId());
        if (!StringUtils.equals(existing.getEntryType(), bookkeeping.getEntryType()))
        {
            throw new ServiceException("记账单创建后不允许修改流水类型");
        }
        List<BookkeepingPayment> payments = bookkeepingMapper.selectBookkeepingPaymentListByBookkeepingId(bookkeeping.getBookkeepingId());
        validateEntryType(bookkeeping.getEntryType());
        fillUpdateAmounts(bookkeeping, existing, payments);
        FundAccount fundAccount = resolveFundAccountForSave(bookkeeping.getFundAccountId(), bookkeeping.getEntryType(),
            bookkeeping.getPaidAmount(), existing.getFundAccountId());
        bookkeeping.setEntryNo(existing.getEntryNo());
        fillFundAccountInfo(bookkeeping, fundAccount);
        bookkeeping.setDelFlag(existing.getDelFlag());
        int rows = bookkeepingMapper.updateBookkeeping(bookkeeping);
        // 已有多笔收款时只更新主单，不重写历史收款明细，避免把核销链路改乱。
        syncPaymentsAfterSave(bookkeeping, existing, payments);
        return rows;
    }

    @Override
    @Transactional
    public int deleteBookkeepingByIds(Long[] bookkeepingIds)
    {
        bookkeepingMapper.deleteBookkeepingPaymentsByBookkeepingIds(bookkeepingIds);
        return bookkeepingMapper.deleteBookkeepingByIds(bookkeepingIds);
    }

    @Override
    @Transactional
    public int registerReceipt(BookkeepingPayment payment)
    {
        Bookkeeping bookkeeping = requireBookkeeping(payment.getBookkeepingId());
        if (!isIncome(bookkeeping.getEntryType()))
        {
            throw new ServiceException("支出单不支持登记收款");
        }
        if (safeAmount(bookkeeping.getUnpaidAmount()).compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("该记账单已结清，无需重复收款");
        }

        BigDecimal receiptAmount = safeAmount(payment.getPaymentAmount());
        if (receiptAmount.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("收款金额必须大于0");
        }
        if (receiptAmount.compareTo(safeAmount(bookkeeping.getUnpaidAmount())) > 0)
        {
            throw new ServiceException("收款金额不能超过未收金额");
        }

        FundAccount fundAccount = requireAvailableAccount(payment.getFundAccountId(), null);
        payment.setEntryType(bookkeeping.getEntryType());
        payment.setFundAccountName(fundAccount.getAccountName());
        payment.setDelFlag(DEL_FLAG_NORMAL);
        bookkeepingMapper.insertBookkeepingPayment(payment);

        // 核销后主单金额始终以明细累计结果回写，列表和统计都只读主单汇总字段。
        BigDecimal paidAmount = safeAmount(bookkeeping.getPaidAmount()).add(receiptAmount);
        BigDecimal unpaidAmount = safeAmount(bookkeeping.getTotalAmount()).subtract(paidAmount);
        bookkeeping.setPaidAmount(paidAmount);
        bookkeeping.setUnpaidAmount(normalizeAmount(unpaidAmount));
        bookkeeping.setStatus(resolveStatus(bookkeeping.getEntryType(), bookkeeping.getTotalAmount(), paidAmount));
        bookkeeping.setFundAccountId(payment.getFundAccountId());
        bookkeeping.setFundAccountName(fundAccount.getAccountName());
        bookkeeping.setPaymentMethod(payment.getPaymentMethod());
        bookkeeping.setUpdateBy(payment.getCreateBy());
        return bookkeepingMapper.updateBookkeeping(bookkeeping);
    }

    @Override
    public BookkeepingStatsVO selectStats()
    {
        BookkeepingStatsVO stats = new BookkeepingStatsVO();
        stats.setTodayIncome(defaultAmount(bookkeepingMapper.selectTodayIncome()));
        stats.setMonthIncome(defaultAmount(bookkeepingMapper.selectMonthIncome()));
        stats.setMonthExpense(defaultAmount(bookkeepingMapper.selectMonthExpense()));
        stats.setCurrentDebtAmount(defaultAmount(bookkeepingMapper.selectCurrentDebtAmount()));
        stats.setIncomeCategoryStats(bookkeepingMapper.selectIncomeCategoryStats());
        stats.setExpenseCategoryStats(bookkeepingMapper.selectExpenseCategoryStats());
        return stats;
    }

    private void fillCreateAmounts(Bookkeeping bookkeeping)
    {
        BigDecimal totalAmount = normalizeRequiredAmount(bookkeeping.getTotalAmount(), "总金额必须大于0");
        BigDecimal paidAmount = isIncome(bookkeeping.getEntryType())
            ? normalizeNonNegativeAmount(bookkeeping.getPaidAmount())
            : totalAmount;
        if (paidAmount.compareTo(totalAmount) > 0)
        {
            throw new ServiceException("已收金额不能超过总金额");
        }
        bookkeeping.setTotalAmount(totalAmount);
        bookkeeping.setPaidAmount(paidAmount);
        bookkeeping.setUnpaidAmount(normalizeAmount(totalAmount.subtract(paidAmount)));
        bookkeeping.setStatus(resolveStatus(bookkeeping.getEntryType(), totalAmount, paidAmount));
    }

    private void fillUpdateAmounts(Bookkeeping bookkeeping, Bookkeeping existing, List<BookkeepingPayment> payments)
    {
        BigDecimal totalAmount = normalizeRequiredAmount(bookkeeping.getTotalAmount(), "总金额必须大于0");
        BigDecimal paidAmount;
        if (isIncome(bookkeeping.getEntryType()))
        {
            if (payments.size() > 1)
            {
                paidAmount = safeAmount(existing.getPaidAmount());
                if (totalAmount.compareTo(paidAmount) < 0)
                {
                    throw new ServiceException("总金额不能小于已收金额");
                }
            }
            else
            {
                paidAmount = normalizeNonNegativeAmount(bookkeeping.getPaidAmount());
                if (paidAmount.compareTo(totalAmount) > 0)
                {
                    throw new ServiceException("已收金额不能超过总金额");
                }
            }
        }
        else
        {
            paidAmount = totalAmount;
        }
        bookkeeping.setTotalAmount(totalAmount);
        bookkeeping.setPaidAmount(paidAmount);
        bookkeeping.setUnpaidAmount(normalizeAmount(totalAmount.subtract(paidAmount)));
        bookkeeping.setStatus(resolveStatus(bookkeeping.getEntryType(), totalAmount, paidAmount));
    }

    private void syncPaymentsAfterSave(Bookkeeping bookkeeping, Bookkeeping existing, List<BookkeepingPayment> payments)
    {
        if (isExpense(bookkeeping.getEntryType()))
        {
            // 支出单第一版不做应付流程，始终只有一条“已付清”明细。
            syncSinglePayment(bookkeeping, payments);
            return;
        }
        if (payments.size() > 1)
        {
            return;
        }
        if (safeAmount(bookkeeping.getPaidAmount()).compareTo(BigDecimal.ZERO) <= 0)
        {
            if (StringUtils.isNotNull(existing))
            {
                // 编辑后首笔实收改成 0 时，移除旧的首笔收款明细，保留挂账状态。
                bookkeepingMapper.deleteBookkeepingPaymentByBookkeepingId(bookkeeping.getBookkeepingId());
            }
            return;
        }
        syncSinglePayment(bookkeeping, payments);
    }

    private void syncSinglePayment(Bookkeeping bookkeeping, List<BookkeepingPayment> payments)
    {
        BookkeepingPayment payment = buildSinglePayment(bookkeeping);
        if (StringUtils.isEmpty(payments))
        {
            bookkeepingMapper.insertBookkeepingPayment(payment);
        }
        else
        {
            payment.setPaymentId(payments.get(0).getPaymentId());
            bookkeepingMapper.updateBookkeepingPayment(payment);
        }
    }

    private BookkeepingPayment buildSinglePayment(Bookkeeping bookkeeping)
    {
        BookkeepingPayment payment = new BookkeepingPayment();
        payment.setBookkeepingId(bookkeeping.getBookkeepingId());
        payment.setEntryType(bookkeeping.getEntryType());
        payment.setPaymentAmount(bookkeeping.getPaidAmount());
        payment.setPaymentTime(defaultDate(bookkeeping.getBusinessDate()));
        payment.setFundAccountId(bookkeeping.getFundAccountId());
        payment.setFundAccountName(bookkeeping.getFundAccountName());
        payment.setPaymentMethod(bookkeeping.getPaymentMethod());
        payment.setRemark(bookkeeping.getRemark());
        payment.setDelFlag(DEL_FLAG_NORMAL);
        payment.setCreateBy(bookkeeping.getCreateBy());
        payment.setUpdateBy(bookkeeping.getUpdateBy());
        return payment;
    }

    private Bookkeeping requireBookkeeping(Long bookkeepingId)
    {
        Bookkeeping bookkeeping = bookkeepingMapper.selectBookkeepingById(bookkeepingId);
        if (StringUtils.isNull(bookkeeping))
        {
            throw new ServiceException("记账单不存在");
        }
        return bookkeeping;
    }

    private FundAccount requireAvailableAccount(Long fundAccountId, Long allowDisabledId)
    {
        FundAccount fundAccount = fundAccountMapper.selectFundAccountById(fundAccountId);
        if (StringUtils.isNull(fundAccount))
        {
            throw new ServiceException("资金账户不存在");
        }
        // 停用账户不能用于新单或新核销，但编辑历史单据时允许保留原来的停用账户。
        if (!StringUtils.equals(STATUS_NORMAL, fundAccount.getStatus())
            && !StringUtils.equals(String.valueOf(fundAccountId), String.valueOf(allowDisabledId)))
        {
            throw new ServiceException("资金账户已停用，不能用于新单录入");
        }
        return fundAccount;
    }

    private FundAccount resolveFundAccountForSave(Long fundAccountId, String entryType, BigDecimal paidAmount, Long allowDisabledId)
    {
        if (fundAccountId == null)
        {
            if (isIncome(entryType) && safeAmount(paidAmount).compareTo(BigDecimal.ZERO) <= 0)
            {
                return null;
            }
            throw new ServiceException("资金账户不能为空");
        }
        return requireAvailableAccount(fundAccountId, allowDisabledId);
    }

    private void fillFundAccountInfo(Bookkeeping bookkeeping, FundAccount fundAccount)
    {
        if (StringUtils.isNull(fundAccount))
        {
            bookkeeping.setFundAccountId(null);
            bookkeeping.setFundAccountName("");
            bookkeeping.setPaymentMethod(null);
            return;
        }
        bookkeeping.setFundAccountName(fundAccount.getAccountName());
    }

    private void validateEntryType(String entryType)
    {
        if (!isIncome(entryType) && !isExpense(entryType))
        {
            throw new ServiceException("不支持的流水类型");
        }
    }

    private String generateEntryNo(String entryType)
    {
        return (isIncome(entryType) ? "SK" : "ZC") + Seq.getId();
    }

    private boolean isIncome(String entryType)
    {
        return StringUtils.equals(ENTRY_TYPE_INCOME, entryType);
    }

    private boolean isExpense(String entryType)
    {
        return StringUtils.equals(ENTRY_TYPE_EXPENSE, entryType);
    }

    private String resolveStatus(String entryType, BigDecimal totalAmount, BigDecimal paidAmount)
    {
        if (isExpense(entryType))
        {
            return STATUS_SETTLED;
        }
        if (paidAmount.compareTo(BigDecimal.ZERO) <= 0)
        {
            return STATUS_PENDING;
        }
        return paidAmount.compareTo(totalAmount) >= 0 ? STATUS_SETTLED : STATUS_PARTIAL;
    }

    private BigDecimal normalizeRequiredAmount(BigDecimal amount, String message)
    {
        BigDecimal normalized = normalizeAmount(amount);
        if (normalized.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException(message);
        }
        return normalized;
    }

    private BigDecimal normalizeNonNegativeAmount(BigDecimal amount)
    {
        BigDecimal normalized = normalizeAmount(amount);
        if (normalized.compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("金额不能为负数");
        }
        return normalized;
    }

    private BigDecimal normalizeAmount(BigDecimal amount)
    {
        return safeAmount(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal safeAmount(BigDecimal amount)
    {
        return StringUtils.isNull(amount) ? BigDecimal.ZERO : amount;
    }

    private BigDecimal defaultAmount(BigDecimal amount)
    {
        return StringUtils.isNull(amount) ? BigDecimal.ZERO : amount;
    }

    private Date defaultDate(Date date)
    {
        return StringUtils.isNull(date) ? new Date() : date;
    }
}
