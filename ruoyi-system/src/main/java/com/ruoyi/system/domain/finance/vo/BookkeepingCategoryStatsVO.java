package com.ruoyi.system.domain.finance.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 分类汇总视图对象
 *
 * @author ruoyi
 */
public class BookkeepingCategoryStatsVO implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String category;

    private String categoryName;

    private BigDecimal totalAmount;

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getCategoryName()
    {
        return categoryName;
    }

    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
    }

    public BigDecimal getTotalAmount()
    {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount)
    {
        this.totalAmount = totalAmount;
    }
}
