package com.ruoyi.system.domain.warehouse;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 库存流水对象 wh_stock_flow
 *
 * @author ruoyi
 */
public class WarehouseStockFlow extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "流水ID", cellType = ColumnType.NUMERIC)
    private Long stockFlowId;

    @Excel(name = "单号")
    private String flowNo;

    @Excel(name = "业务类型", dictType = "warehouse_stock_type")
    private String businessType;

    @Excel(name = "业务单号")
    private String businessNo;

    private Long partId;

    @Excel(name = "配件编码")
    private String partCode;

    @Excel(name = "配件名称")
    private String partName;

    @Excel(name = "规格型号")
    private String specModel;

    @Excel(name = "单位", dictType = "warehouse_part_unit")
    private String unit;

    @Excel(name = "变动数量", scale = 2)
    private BigDecimal changeQuantity;

    @Excel(name = "变动前库存", scale = 2)
    private BigDecimal beforeStock;

    @Excel(name = "变动后库存", scale = 2)
    private BigDecimal afterStock;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "业务时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date businessTime;

    @Excel(name = "经办人")
    private String handlerName;

    private String adjustDirection;

    private String delFlag;

    public Long getStockFlowId()
    {
        return stockFlowId;
    }

    public void setStockFlowId(Long stockFlowId)
    {
        this.stockFlowId = stockFlowId;
    }

    public String getFlowNo()
    {
        return flowNo;
    }

    public void setFlowNo(String flowNo)
    {
        this.flowNo = flowNo;
    }

    public String getBusinessType()
    {
        return businessType;
    }

    public void setBusinessType(String businessType)
    {
        this.businessType = businessType;
    }

    public Long getPartId()
    {
        return partId;
    }

    public String getBusinessNo()
    {
        return businessNo;
    }

    public void setBusinessNo(String businessNo)
    {
        this.businessNo = businessNo;
    }

    public void setPartId(Long partId)
    {
        this.partId = partId;
    }

    public String getPartCode()
    {
        return partCode;
    }

    public void setPartCode(String partCode)
    {
        this.partCode = partCode;
    }

    public String getPartName()
    {
        return partName;
    }

    public void setPartName(String partName)
    {
        this.partName = partName;
    }

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

    public BigDecimal getChangeQuantity()
    {
        return changeQuantity;
    }

    public void setChangeQuantity(BigDecimal changeQuantity)
    {
        this.changeQuantity = changeQuantity;
    }

    public BigDecimal getBeforeStock()
    {
        return beforeStock;
    }

    public void setBeforeStock(BigDecimal beforeStock)
    {
        this.beforeStock = beforeStock;
    }

    public BigDecimal getAfterStock()
    {
        return afterStock;
    }

    public void setAfterStock(BigDecimal afterStock)
    {
        this.afterStock = afterStock;
    }

    public Date getBusinessTime()
    {
        return businessTime;
    }

    public void setBusinessTime(Date businessTime)
    {
        this.businessTime = businessTime;
    }

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
            .append("stockFlowId", getStockFlowId())
            .append("flowNo", getFlowNo())
            .append("businessType", getBusinessType())
            .append("businessNo", getBusinessNo())
            .append("partId", getPartId())
            .append("partCode", getPartCode())
            .append("partName", getPartName())
            .append("specModel", getSpecModel())
            .append("unit", getUnit())
            .append("changeQuantity", getChangeQuantity())
            .append("beforeStock", getBeforeStock())
            .append("afterStock", getAfterStock())
            .append("businessTime", getBusinessTime())
            .append("handlerName", getHandlerName())
            .append("adjustDirection", getAdjustDirection())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
