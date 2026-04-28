package com.ruoyi.system.domain.order;

import java.math.BigDecimal;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 订单明细对象 ord_order_detail
 *
 * @author ruoyi
 */
public class OrderDetail extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "明细ID", cellType = ColumnType.NUMERIC)
    private Long detailId;

    private Long orderId;

    @Excel(name = "明细类型", dictType = "order_detail_type")
    private String detailType;

    private Integer lineNo;

    @Excel(name = "项目/配件名称")
    private String itemName;

    private Long partId;

    @Excel(name = "项目/配件编码")
    private String partCode;

    private String partName;

    @Excel(name = "规格型号")
    private String specModel;

    @Excel(name = "单位", dictType = "warehouse_part_unit")
    private String unit;

    @Excel(name = "数量", scale = 2, isStatistics = true)
    private BigDecimal quantity;

    @Excel(name = "单价", scale = 2)
    private BigDecimal unitPrice;

    @Excel(name = "金额", scale = 2, isStatistics = true)
    private BigDecimal amount;

    private BigDecimal reservedQuantity;

    public Long getDetailId()
    {
        return detailId;
    }

    public void setDetailId(Long detailId)
    {
        this.detailId = detailId;
    }

    public Long getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Long orderId)
    {
        this.orderId = orderId;
    }

    public String getDetailType()
    {
        return detailType;
    }

    public void setDetailType(String detailType)
    {
        this.detailType = detailType;
    }

    public Integer getLineNo()
    {
        return lineNo;
    }

    public void setLineNo(Integer lineNo)
    {
        this.lineNo = lineNo;
    }

    @Size(max = 100, message = "项目/配件名称长度不能超过100个字符")
    public String getItemName()
    {
        return itemName;
    }

    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }

    public Long getPartId()
    {
        return partId;
    }

    public void setPartId(Long partId)
    {
        this.partId = partId;
    }

    @Size(max = 32, message = "项目/配件编码长度不能超过32个字符")
    public String getPartCode()
    {
        return partCode;
    }

    public void setPartCode(String partCode)
    {
        this.partCode = partCode;
    }

    @Size(max = 100, message = "配件名称长度不能超过100个字符")
    public String getPartName()
    {
        return partName;
    }

    public void setPartName(String partName)
    {
        this.partName = partName;
    }

    @Size(max = 100, message = "规格型号长度不能超过100个字符")
    public String getSpecModel()
    {
        return specModel;
    }

    public void setSpecModel(String specModel)
    {
        this.specModel = specModel;
    }

    public String getUnit()
    {
        return unit;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    @DecimalMin(value = "0.01", message = "数量必须大于0")
    @Digits(integer = 10, fraction = 2, message = "数量格式不正确")
    public BigDecimal getQuantity()
    {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity)
    {
        this.quantity = quantity;
    }

    @DecimalMin(value = "0.00", message = "单价不能小于0")
    @Digits(integer = 10, fraction = 2, message = "单价格式不正确")
    public BigDecimal getUnitPrice()
    {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice)
    {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public BigDecimal getReservedQuantity()
    {
        return reservedQuantity;
    }

    public void setReservedQuantity(BigDecimal reservedQuantity)
    {
        this.reservedQuantity = reservedQuantity;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("detailId", getDetailId())
            .append("orderId", getOrderId())
            .append("detailType", getDetailType())
            .append("lineNo", getLineNo())
            .append("itemName", getItemName())
            .append("partId", getPartId())
            .append("partCode", getPartCode())
            .append("partName", getPartName())
            .append("specModel", getSpecModel())
            .append("unit", getUnit())
            .append("quantity", getQuantity())
            .append("unitPrice", getUnitPrice())
            .append("amount", getAmount())
            .append("reservedQuantity", getReservedQuantity())
            .append("remark", getRemark())
            .toString();
    }
}
