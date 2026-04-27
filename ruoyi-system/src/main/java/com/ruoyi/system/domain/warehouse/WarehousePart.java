package com.ruoyi.system.domain.warehouse;

import java.math.BigDecimal;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 配件档案对象 wh_part
 *
 * @author ruoyi
 */
public class WarehousePart extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "配件ID", cellType = ColumnType.NUMERIC)
    private Long partId;

    @Excel(name = "配件编码")
    private String partCode;

    @Excel(name = "配件名称")
    private String partName;

    @Excel(name = "分类", dictType = "warehouse_part_category")
    private String category;

    @Excel(name = "规格型号")
    private String specModel;

    @Excel(name = "单位", dictType = "warehouse_part_unit")
    private String unit;

    @Excel(name = "参考进价", scale = 2)
    private BigDecimal purchasePrice;

    @Excel(name = "销售单价", scale = 2)
    private BigDecimal salePrice;

    @Excel(name = "当前库存", scale = 2)
    private BigDecimal currentStock;

    @Excel(name = "最低库存", scale = 2)
    private BigDecimal minStock;

    @Excel(name = "状态", dictType = "sys_normal_disable")
    private String status;

    private String delFlag;

    public Long getPartId()
    {
        return partId;
    }

    public void setPartId(Long partId)
    {
        this.partId = partId;
    }

    @NotBlank(message = "配件编码不能为空")
    @Size(max = 32, message = "配件编码长度不能超过32个字符")
    public String getPartCode()
    {
        return partCode;
    }

    public void setPartCode(String partCode)
    {
        this.partCode = partCode;
    }

    @NotBlank(message = "配件名称不能为空")
    @Size(max = 100, message = "配件名称长度不能超过100个字符")
    public String getPartName()
    {
        return partName;
    }

    public void setPartName(String partName)
    {
        this.partName = partName;
    }

    @NotBlank(message = "配件分类不能为空")
    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
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

    @NotBlank(message = "单位不能为空")
    public String getUnit()
    {
        return unit;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    @NotNull(message = "参考进价不能为空")
    @DecimalMin(value = "0.00", message = "参考进价不能小于0")
    @Digits(integer = 10, fraction = 2, message = "参考进价格式不正确")
    public BigDecimal getPurchasePrice()
    {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice)
    {
        this.purchasePrice = purchasePrice;
    }

    @NotNull(message = "销售单价不能为空")
    @DecimalMin(value = "0.00", message = "销售单价不能小于0")
    @Digits(integer = 10, fraction = 2, message = "销售单价格式不正确")
    public BigDecimal getSalePrice()
    {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice)
    {
        this.salePrice = salePrice;
    }

    public BigDecimal getCurrentStock()
    {
        return currentStock;
    }

    public void setCurrentStock(BigDecimal currentStock)
    {
        this.currentStock = currentStock;
    }

    @NotNull(message = "最低库存不能为空")
    @DecimalMin(value = "0.00", message = "最低库存不能小于0")
    @Digits(integer = 10, fraction = 2, message = "最低库存格式不正确")
    public BigDecimal getMinStock()
    {
        return minStock;
    }

    public void setMinStock(BigDecimal minStock)
    {
        this.minStock = minStock;
    }

    @NotBlank(message = "状态不能为空")
    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
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
            .append("partId", getPartId())
            .append("partCode", getPartCode())
            .append("partName", getPartName())
            .append("category", getCategory())
            .append("specModel", getSpecModel())
            .append("unit", getUnit())
            .append("purchasePrice", getPurchasePrice())
            .append("salePrice", getSalePrice())
            .append("currentStock", getCurrentStock())
            .append("minStock", getMinStock())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
