package com.ruoyi.system.mapper.finance;

import java.math.BigDecimal;
import java.util.List;
import com.ruoyi.system.domain.finance.Bookkeeping;
import com.ruoyi.system.domain.finance.BookkeepingPayment;
import com.ruoyi.system.domain.finance.vo.BookkeepingCategoryStatsVO;

/**
 * 记账管理数据层
 *
 * @author ruoyi
 */
public interface BookkeepingMapper
{
    List<Bookkeeping> selectBookkeepingList(Bookkeeping bookkeeping);

    Bookkeeping selectBookkeepingById(Long bookkeepingId);

    int insertBookkeeping(Bookkeeping bookkeeping);

    int updateBookkeeping(Bookkeeping bookkeeping);

    int deleteBookkeepingByIds(Long[] bookkeepingIds);

    List<BookkeepingPayment> selectBookkeepingPaymentListByBookkeepingId(Long bookkeepingId);

    int insertBookkeepingPayment(BookkeepingPayment payment);

    int updateBookkeepingPayment(BookkeepingPayment payment);

    int deleteBookkeepingPaymentByBookkeepingId(Long bookkeepingId);

    int deleteBookkeepingPaymentsByBookkeepingIds(Long[] bookkeepingIds);

    BigDecimal selectTodayIncome();

    BigDecimal selectMonthIncome();

    BigDecimal selectMonthExpense();

    BigDecimal selectCurrentDebtAmount();

    List<BookkeepingCategoryStatsVO> selectIncomeCategoryStats();

    List<BookkeepingCategoryStatsVO> selectExpenseCategoryStats();
}
