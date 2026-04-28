package com.ruoyi.system.domain.order;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;

/**
 * 订单完工请求对象
 *
 * @author ruoyi
 */
public class OrderCompleteRequest extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long orderId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date completedTime;

    private BigDecimal paidAmount;

    private Long fundAccountId;

    private String paymentMethod;

    public Long getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Long orderId)
    {
        this.orderId = orderId;
    }

    public Date getCompletedTime()
    {
        return completedTime;
    }

    public void setCompletedTime(Date completedTime)
    {
        this.completedTime = completedTime;
    }

    @DecimalMin(value = "0.00", message = "首笔实收不能小于0")
    @Digits(integer = 10, fraction = 2, message = "首笔实收格式不正确")
    public BigDecimal getPaidAmount()
    {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount)
    {
        this.paidAmount = paidAmount;
    }

    public Long getFundAccountId()
    {
        return fundAccountId;
    }

    public void setFundAccountId(Long fundAccountId)
    {
        this.fundAccountId = fundAccountId;
    }

    @Size(max = 32, message = "支付方式长度不能超过32个字符")
    public String getPaymentMethod()
    {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod)
    {
        this.paymentMethod = paymentMethod;
    }
}
