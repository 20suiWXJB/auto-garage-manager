package com.ruoyi.system.service.finance.impl;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.finance.FundAccount;
import com.ruoyi.system.mapper.finance.FundAccountMapper;
import com.ruoyi.system.service.finance.IFundAccountService;

/**
 * 资金账户服务实现
 *
 * @author ruoyi
 */
@Service
public class FundAccountServiceImpl implements IFundAccountService
{
    @Autowired
    private FundAccountMapper fundAccountMapper;

    @Override
    public List<FundAccount> selectFundAccountList(FundAccount fundAccount)
    {
        return fundAccountMapper.selectFundAccountList(fundAccount);
    }

    @Override
    public List<FundAccount> selectFundAccountOptions()
    {
        return fundAccountMapper.selectFundAccountOptions();
    }

    @Override
    public FundAccount selectFundAccountById(Long fundAccountId)
    {
        return fundAccountMapper.selectFundAccountById(fundAccountId);
    }

    @Override
    public boolean checkAccountNameUnique(FundAccount fundAccount)
    {
        Long fundAccountId = StringUtils.isNull(fundAccount.getFundAccountId()) ? -1L : fundAccount.getFundAccountId();
        FundAccount info = fundAccountMapper.checkAccountNameUnique(fundAccount.getAccountName());
        if (StringUtils.isNotNull(info) && info.getFundAccountId().longValue() != fundAccountId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public int insertFundAccount(FundAccount fundAccount)
    {
        fundAccount.setOpeningBalance(defaultAmount(fundAccount.getOpeningBalance()));
        return fundAccountMapper.insertFundAccount(fundAccount);
    }

    @Override
    public int updateFundAccount(FundAccount fundAccount)
    {
        fundAccount.setOpeningBalance(defaultAmount(fundAccount.getOpeningBalance()));
        return fundAccountMapper.updateFundAccount(fundAccount);
    }

    @Override
    public int deleteFundAccountByIds(Long[] fundAccountIds)
    {
        if (fundAccountMapper.countFundAccountUsed(fundAccountIds) > 0)
        {
            // 已被业务引用的账户只允许停用，不允许物理上从业务视角“消失”。
            throw new ServiceException("资金账户已被记账单使用，不能删除，请改为停用");
        }
        return fundAccountMapper.deleteFundAccountByIds(fundAccountIds);
    }

    private BigDecimal defaultAmount(BigDecimal amount)
    {
        return StringUtils.isNull(amount) ? BigDecimal.ZERO : amount.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
