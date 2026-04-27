package com.ruoyi.system.domain.finance;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 收付款明细对象 acct_bookkeeping_payment
 *
 * @author ruoyi
 */
public class BookkeepingPayment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long paymentId;

    @NotNull(message = "记账单ID不能为空")
    private Long bookkeepingId;

    private String entryType;

    @NotNull(message = "收款金额不能为空")
    private BigDecimal paymentAmount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "收款时间不能为空")
    private Date paymentTime;

    @NotNull(message = "资金账户不能为空")
    private Long fundAccountId;

    private String fundAccountName;

    @Size(max = 32, message = "收付款方式长度不能超过32个字符")
    private String paymentMethod;

    private String delFlag;

    public Long getPaymentId()
    {
        return paymentId;
    }

    public void setPaymentId(Long paymentId)
    {
        this.paymentId = paymentId;
    }

    public Long getBookkeepingId()
    {
        return bookkeepingId;
    }

    public void setBookkeepingId(Long bookkeepingId)
    {
        this.bookkeepingId = bookkeepingId;
    }

    public String getEntryType()
    {
        return entryType;
    }

    public void setEntryType(String entryType)
    {
        this.entryType = entryType;
    }

    public BigDecimal getPaymentAmount()
    {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount)
    {
        this.paymentAmount = paymentAmount;
    }

    public Date getPaymentTime()
    {
        return paymentTime;
    }

    public void setPaymentTime(Date paymentTime)
    {
        this.paymentTime = paymentTime;
    }

    public Long getFundAccountId()
    {
        return fundAccountId;
    }

    public void setFundAccountId(Long fundAccountId)
    {
        this.fundAccountId = fundAccountId;
    }

    public String getFundAccountName()
    {
        return fundAccountName;
    }

    public void setFundAccountName(String fundAccountName)
    {
        this.fundAccountName = fundAccountName;
    }

    public String getPaymentMethod()
    {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod)
    {
        this.paymentMethod = paymentMethod;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("paymentId", getPaymentId())
            .append("bookkeepingId", getBookkeepingId())
            .append("entryType", getEntryType())
            .append("paymentAmount", getPaymentAmount())
            .append("paymentTime", getPaymentTime())
            .append("fundAccountId", getFundAccountId())
            .append("fundAccountName", getFundAccountName())
            .append("paymentMethod", getPaymentMethod())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("remark", getRemark())
            .toString();
    }
}
