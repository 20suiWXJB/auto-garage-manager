package com.ruoyi.system.domain.warehouse;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 库存操作请求对象
 *
 * @author ruoyi
 */
public class WarehouseStockOperation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long partId;

    private BigDecimal quantity;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date businessTime;

    private String handlerName;

    private String adjustDirection;

    private String businessNo;

    @NotNull(message = "配件不能为空")
    public Long getPartId()
    {
        return partId;
    }

    public void setPartId(Long partId)
    {
        this.partId = partId;
    }

    @NotNull(message = "操作数量不能为空")
    @DecimalMin(value = "0.01", message = "操作数量必须大于0")
    @Digits(integer = 10, fraction = 2, message = "操作数量格式不正确")
    public BigDecimal getQuantity()
    {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity)
    {
        this.quantity = quantity;
    }

    @NotNull(message = "业务时间不能为空")
    public Date getBusinessTime()
    {
        return businessTime;
    }

    public void setBusinessTime(Date businessTime)
    {
        this.businessTime = businessTime;
    }

    @Size(max = 50, message = "经办人长度不能超过50个字符")
    public String getHandlerName()
    {
        return handlerName;
    }

    public void setHandlerName(String handlerName)
    {
        this.handlerName = handlerName;
    }

    public String getAdjustDirection()
    {
        return adjustDirection;
    }

    public void setAdjustDirection(String adjustDirection)
    {
        this.adjustDirection = adjustDirection;
    }

    @Size(max = 32, message = "业务单号长度不能超过32个字符")
    public String getBusinessNo()
    {
        return businessNo;
    }

    public void setBusinessNo(String businessNo)
    {
        this.businessNo = businessNo;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("partId", getPartId())
            .append("quantity", getQuantity())
            .append("businessTime", getBusinessTime())
            .append("handlerName", getHandlerName())
            .append("adjustDirection", getAdjustDirection())
            .append("businessNo", getBusinessNo())
            .append("remark", getRemark())
            .toString();
    }
}
