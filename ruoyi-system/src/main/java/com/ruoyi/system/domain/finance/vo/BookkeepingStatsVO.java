package com.ruoyi.system.domain.finance.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 记账统计视图对象
 *
 * @author ruoyi
 */
public class BookkeepingStatsVO implements Serializable
{
    private static final long serialVersionUID = 1L;

    private BigDecimal todayIncome;

    private BigDecimal monthIncome;

    private BigDecimal monthExpense;

    private BigDecimal currentDebtAmount;

    private List<BookkeepingCategoryStatsVO> incomeCategoryStats;

    private List<BookkeepingCategoryStatsVO> expenseCategoryStats;

    public BigDecimal getTodayIncome()
    {
        return todayIncome;
    }

    public void setTodayIncome(BigDecimal todayIncome)
    {
        this.todayIncome = todayIncome;
    }

    public BigDecimal getMonthIncome()
    {
        return monthIncome;
    }

    public void setMonthIncome(BigDecimal monthIncome)
    {
        this.monthIncome = monthIncome;
    }

    public BigDecimal getMonthExpense()
    {
        return monthExpense;
    }

    public void setMonthExpense(BigDecimal monthExpense)
    {
        this.monthExpense = monthExpense;
    }

    public BigDecimal getCurrentDebtAmount()
    {
        return currentDebtAmount;
    }

    public void setCurrentDebtAmount(BigDecimal currentDebtAmount)
    {
        this.currentDebtAmount = currentDebtAmount;
    }

    public List<BookkeepingCategoryStatsVO> getIncomeCategoryStats()
    {
        return incomeCategoryStats;
    }

    public void setIncomeCategoryStats(List<BookkeepingCategoryStatsVO> incomeCategoryStats)
    {
        this.incomeCategoryStats = incomeCategoryStats;
    }

    public List<BookkeepingCategoryStatsVO> getExpenseCategoryStats()
    {
        return expenseCategoryStats;
    }

    public void setExpenseCategoryStats(List<BookkeepingCategoryStatsVO> expenseCategoryStats)
    {
        this.expenseCategoryStats = expenseCategoryStats;
    }
}
