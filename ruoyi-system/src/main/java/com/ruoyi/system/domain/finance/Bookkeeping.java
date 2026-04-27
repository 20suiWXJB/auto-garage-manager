package com.ruoyi.system.domain.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 记账流水对象 acct_bookkeeping
 *
 * @author ruoyi
 */
public class Bookkeeping extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "流水ID", cellType = ColumnType.NUMERIC)
    private Long bookkeepingId;

    @Excel(name = "单号")
    private String entryNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "业务日期", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date businessDate;

    @Excel(name = "流水类型", dictType = "acct_entry_type")
    private String entryType;

    private String category;

    @Excel(name = "分类")
    private String categoryName;

    @Excel(name = "总金额", scale = 2, isStatistics = true)
    private BigDecimal totalAmount;

    @Excel(name = "已收金额", scale = 2, isStatistics = true)
    private BigDecimal paidAmount;

    @Excel(name = "未收金额", scale = 2, isStatistics = true)
    private BigDecimal unpaidAmount;

    @Excel(name = "状态", dictType = "acct_entry_status")
    private String status;

    private Long fundAccountId;

    @Excel(name = "资金账户")
    private String fundAccountName;

    @Excel(name = "收付款方式")
    private String paymentMethod;

    @Excel(name = "经办人")
    private String handlerName;

    @Excel(name = "客户姓名")
    private String customerName;

    @Excel(name = "手机号")
    private String customerPhone;

    @Excel(name = "车牌号")
    private String licensePlate;

    @Excel(name = "车型")
    private String carModel;

    @Excel(name = "业务描述")
    private String businessDesc;

    private String delFlag;

    private List<BookkeepingPayment> paymentList;

    public Long getBookkeepingId()
    {
        return bookkeepingId;
    }

    public void setBookkeepingId(Long bookkeepingId)
    {
        this.bookkeepingId = bookkeepingId;
    }

    @Size(max = 32, message = "单号长度不能超过32个字符")
    public String getEntryNo()
    {
        return entryNo;
    }

    public void setEntryNo(String entryNo)
    {
        this.entryNo = entryNo;
    }

    @NotNull(message = "业务日期不能为空")
    public Date getBusinessDate()
    {
        return businessDate;
    }

    public void setBusinessDate(Date businessDate)
    {
        this.businessDate = businessDate;
    }

    @NotBlank(message = "流水类型不能为空")
    public String getEntryType()
    {
        return entryType;
    }

    public void setEntryType(String entryType)
    {
        this.entryType = entryType;
    }

    @NotBlank(message = "分类不能为空")
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

    @NotNull(message = "总金额不能为空")
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

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @NotNull(message = "资金账户不能为空")
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

    @Size(max = 32, message = "收付款方式长度不能超过32个字符")
    public String getPaymentMethod()
    {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod)
    {
        this.paymentMethod = paymentMethod;
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

    @Size(max = 255, message = "业务描述长度不能超过255个字符")
    public String getBusinessDesc()
    {
        return businessDesc;
    }

    public void setBusinessDesc(String businessDesc)
    {
        this.businessDesc = businessDesc;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public List<BookkeepingPayment> getPaymentList()
    {
        return paymentList;
    }

    public void setPaymentList(List<BookkeepingPayment> paymentList)
    {
        this.paymentList = paymentList;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("bookkeepingId", getBookkeepingId())
            .append("entryNo", getEntryNo())
            .append("businessDate", getBusinessDate())
            .append("entryType", getEntryType())
            .append("category", getCategory())
            .append("categoryName", getCategoryName())
            .append("totalAmount", getTotalAmount())
            .append("paidAmount", getPaidAmount())
            .append("unpaidAmount", getUnpaidAmount())
            .append("status", getStatus())
            .append("fundAccountId", getFundAccountId())
            .append("fundAccountName", getFundAccountName())
            .append("paymentMethod", getPaymentMethod())
            .append("handlerName", getHandlerName())
            .append("customerName", getCustomerName())
            .append("customerPhone", getCustomerPhone())
            .append("licensePlate", getLicensePlate())
            .append("carModel", getCarModel())
            .append("businessDesc", getBusinessDesc())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
