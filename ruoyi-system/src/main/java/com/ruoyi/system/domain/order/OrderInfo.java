package com.ruoyi.system.domain.order;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 订单主表对象 ord_order
 *
 * @author ruoyi
 */
public class OrderInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "订单ID", cellType = ColumnType.NUMERIC)
    private Long orderId;

    @Excel(name = "订单号")
    private String orderNo;

    @Excel(name = "订单类型", dictType = "order_type")
    private String orderType;

    @Excel(name = "订单状态", dictType = "order_status")
    private String orderStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "下单时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date orderTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "完工时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date completedTime;

    @Excel(name = "客户姓名")
    private String customerName;

    @Excel(name = "手机号")
    private String customerPhone;

    @Excel(name = "车牌号")
    private String licensePlate;

    @Excel(name = "车型")
    private String carModel;

    @Excel(name = "服务金额", scale = 2, isStatistics = true)
    private BigDecimal serviceAmount;

    @Excel(name = "配件金额", scale = 2, isStatistics = true)
    private BigDecimal partAmount;

    @Excel(name = "订单总金额", scale = 2, isStatistics = true)
    private BigDecimal totalAmount;

    @Excel(name = "首笔实收", scale = 2, isStatistics = true)
    private BigDecimal paidAmount;

    @Excel(name = "未收金额", scale = 2, isStatistics = true)
    private BigDecimal unpaidAmount;

    private Long bookkeepingId;

    @Excel(name = "关联财务单号")
    private String bookkeepingEntryNo;

    @Excel(name = "经办人")
    private String handlerName;

    private String delFlag;

    @Valid
    private List<OrderDetail> detailList;

    public Long getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Long orderId)
    {
        this.orderId = orderId;
    }

    @Size(max = 32, message = "订单号长度不能超过32个字符")
    public String getOrderNo()
    {
        return orderNo;
    }

    public void setOrderNo(String orderNo)
    {
        this.orderNo = orderNo;
    }

    @NotBlank(message = "订单类型不能为空")
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

    @NotNull(message = "下单时间不能为空")
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

    @Size(max = 50, message = "客户姓名长度不能超过50个字符")
    public String getCustomerName()
    {
        return customerName;
    }

    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }

    @Size(max = 20, message = "手机号长度不能超过20个字符")
    public String getCustomerPhone()
    {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone)
    {
        this.customerPhone = customerPhone;
    }

    @Size(max = 20, message = "车牌号长度不能超过20个字符")
    public String getLicensePlate()
    {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate)
    {
        this.licensePlate = licensePlate;
    }

    @Size(max = 50, message = "车型长度不能超过50个字符")
    public String getCarModel()
    {
        return carModel;
    }

    public void setCarModel(String carModel)
    {
        this.carModel = carModel;
    }

    public BigDecimal getServiceAmount()
    {
        return serviceAmount;
    }

    public void setServiceAmount(BigDecimal serviceAmount)
    {
        this.serviceAmount = serviceAmount;
    }

    public BigDecimal getPartAmount()
    {
        return partAmount;
    }

    public void setPartAmount(BigDecimal partAmount)
    {
        this.partAmount = partAmount;
    }

    public BigDecimal getTotalAmount()
    {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount)
    {
        this.totalAmount = totalAmount;
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

    public Long getBookkeepingId()
    {
        return bookkeepingId;
    }

    public void setBookkeepingId(Long bookkeepingId)
    {
        this.bookkeepingId = bookkeepingId;
    }

    public String getBookkeepingEntryNo()
    {
        return bookkeepingEntryNo;
    }

    public void setBookkeepingEntryNo(String bookkeepingEntryNo)
    {
        this.bookkeepingEntryNo = bookkeepingEntryNo;
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

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    @NotEmpty(message = "订单明细不能为空")
    public List<OrderDetail> getDetailList()
    {
        return detailList;
    }

    public void setDetailList(List<OrderDetail> detailList)
    {
        this.detailList = detailList;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("orderId", getOrderId())
            .append("orderNo", getOrderNo())
            .append("orderType", getOrderType())
            .append("orderStatus", getOrderStatus())
            .append("orderTime", getOrderTime())
            .append("completedTime", getCompletedTime())
            .append("customerName", getCustomerName())
            .append("customerPhone", getCustomerPhone())
            .append("licensePlate", getLicensePlate())
            .append("carModel", getCarModel())
            .append("serviceAmount", getServiceAmount())
            .append("partAmount", getPartAmount())
            .append("totalAmount", getTotalAmount())
            .append("paidAmount", getPaidAmount())
            .append("unpaidAmount", getUnpaidAmount())
            .append("bookkeepingId", getBookkeepingId())
            .append("bookkeepingEntryNo", getBookkeepingEntryNo())
            .append("handlerName", getHandlerName())
            .append("detailList", getDetailList())
            .append("remark", getRemark())
            .toString();
    }
}
