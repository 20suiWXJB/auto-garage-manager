package com.ruoyi.system.domain.finance;

import java.math.BigDecimal;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 资金账户对象 acct_fund_account
 *
 * @author ruoyi
 */
public class FundAccount extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long fundAccountId;

    @Excel(name = "账户名称")
    private String accountName;

    @Excel(name = "账户类型", dictType = "acct_fund_type")
    private String accountType;

    @Excel(name = "期初余额", scale = 2)
    private BigDecimal openingBalance;

    @Excel(name = "状态", dictType = "sys_normal_disable")
    private String status;

    private String delFlag;

    public Long getFundAccountId()
    {
        return fundAccountId;
    }

    public void setFundAccountId(Long fundAccountId)
    {
        this.fundAccountId = fundAccountId;
    }

    @NotBlank(message = "账户名称不能为空")
    @Size(max = 50, message = "账户名称长度不能超过50个字符")
    public String getAccountName()
    {
        return accountName;
    }

    public void setAccountName(String accountName)
    {
        this.accountName = accountName;
    }

    @NotBlank(message = "账户类型不能为空")
    public String getAccountType()
    {
        return accountType;
    }

    public void setAccountType(String accountType)
    {
        this.accountType = accountType;
    }

    @NotNull(message = "期初余额不能为空")
    public BigDecimal getOpeningBalance()
    {
        return openingBalance;
    }

    public void setOpeningBalance(BigDecimal openingBalance)
    {
        this.openingBalance = openingBalance;
    }

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
            .append("fundAccountId", getFundAccountId())
            .append("accountName", getAccountName())
            .append("accountType", getAccountType())
            .append("openingBalance", getOpeningBalance())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
