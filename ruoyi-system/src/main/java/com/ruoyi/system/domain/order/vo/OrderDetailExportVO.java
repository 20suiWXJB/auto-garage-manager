package com.ruoyi.system.domain.order.vo;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;

/**
 * 订单明细导出对象
 *
 * @author ruoyi
 */
public class OrderDetailExportVO
{
    @Excel(name = "订单号")
    private String orderNo;

    @Excel(name = "订单类型", dictType = "order_type")
    private String orderType;

    @Excel(name = "订单状态", dictType = "order_status")
    private String orderStatus;

    @Excel(name = "客户姓名")
    private String customerName;

    @Excel(name = "手机号")
    private String customerPhone;

    @Excel(name = "车牌号")
    private String licensePlate;

    @Excel(name = "车型")
    private String carModel;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "下单时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date orderTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "完工时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date completedTime;

    @Excel(name = "明细类型", dictType = "order_detail_type")
    private String detailType;

    @Excel(name = "项目/配件编码")
    private String itemCode;

    @Excel(name = "项目/配件名称")
    private String itemName;

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

    @Excel(name = "首笔实收", scale = 2, isStatistics = true)
    private BigDecimal paidAmount;

    @Excel(name = "未收金额", scale = 2, isStatistics = true)
    private BigDecimal unpaidAmount;

    @Excel(name = "经办人")
    private String handlerName;

    @Excel(name = "备注")
    private String remark;

    public String getOrderNo()
    {
        return orderNo;
    }

    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
    }

    public String getOrderType()
    {
        return orderType;
    }

    public void setOrderType(String orderType)
    {
        this.orderType = orderType;
    }

    public String getOrderStatus()
    {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus)
    {
        this.orderStatus = orderStatus;
    }

    public String getCustomerName()
    {
        return customerName;
    }

    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }

    public String getCustomerPhone()
    {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone)
    {
        this.customerPhone = customerPhone;
    }

    public String getLicensePlate()
    {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate)
    {
        this.licensePlate = licensePlate;
    }

    public String getCarModel()
    {
        return carModel;
    }

    public void setCarModel(String carModel)
    {
        this.carModel = carModel;
    }

    public Date getOrderTime()
    {
        return orderTime;
    }

    public void setOrderTime(Date orderTime)
    {
        this.orderTime = orderTime;
    }

    public Date getCompletedTime()
    {
        return completedTime;
    }

    public void setCompletedTime(Date completedTime)
    {
        this.completedTime = completedTime;
    }

    public String getDetailType()
    {
        return detailType;
    }

    public void setDetailType(String detailType)
    {
        this.detailType = detailType;
    }

    public String getItemCode()
    {
        return itemCode;
    }

    public void setItemCode(String itemCode)
    {
        this.itemCode = itemCode;
    }

    public String getItemName()
    {
        return itemName;
    }

    public void setItemName(String itemName)
    {
        this.itemName = itemName;
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

    public BigDecimal getQuantity()
    {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity)
    {
        this.quantity = quantity;
    }

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

    public BigDecimal getPaidAmount()
    {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount)
    {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getUnpaidAmount()
    {
        return unpaidAmount;
    }

    public void setUnpaidAmount(BigDecimal unpaidAmount)
    {
        this.unpaidAmount = unpaidAmount;
    }

    public String getHandlerName()
    {
        return handlerName;
    }

    public void setHandlerName(String handlerName)
    {
        this.handlerName = handlerName;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }
}
