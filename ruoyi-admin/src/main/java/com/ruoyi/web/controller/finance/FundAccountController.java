package com.ruoyi.web.controller.finance;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.finance.FundAccount;
import com.ruoyi.system.service.finance.IFundAccountService;

/**
 * 资金账户控制器
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/finance/account")
public class FundAccountController extends BaseController
{
    @Autowired
    private IFundAccountService fundAccountService;

    @PreAuthorize("@ss.hasPermi('finance:account:list')")
    @GetMapping("/list")
    public TableDataInfo list(FundAccount fundAccount)
    {
        startPage();
        List<FundAccount> list = fundAccountService.selectFundAccountList(fundAccount);
        return getDataTable(list);
    }

    @GetMapping("/options")
    public AjaxResult options()
    {
        return success(fundAccountService.selectFundAccountOptions());
    }

    @PreAuthorize("@ss.hasPermi('finance:account:list')")
    @GetMapping("/{fundAccountId}")
    public AjaxResult getInfo(@PathVariable Long fundAccountId)
    {
        return success(fundAccountService.selectFundAccountById(fundAccountId));
    }

    @PreAuthorize("@ss.hasPermi('finance:account:add')")
    @Log(title = "资金账户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody FundAccount fundAccount)
    {
        if (!fundAccountService.checkAccountNameUnique(fundAccount))
        {
            return error("新增资金账户'" + fundAccount.getAccountName() + "'失败，账户名称已存在");
        }
        fundAccount.setCreateBy(getUsername());
        return toAjax(fundAccountService.insertFundAccount(fundAccount));
    }

    @PreAuthorize("@ss.hasPermi('finance:account:edit')")
    @Log(title = "资金账户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody FundAccount fundAccount)
    {
        if (!fundAccountService.checkAccountNameUnique(fundAccount))
        {
            return error("修改资金账户'" + fundAccount.getAccountName() + "'失败，账户名称已存在");
        }
        fundAccount.setUpdateBy(getUsername());
        return toAjax(fundAccountService.updateFundAccount(fundAccount));
    }

    @PreAuthorize("@ss.hasPermi('finance:account:remove')")
    @Log(title = "资金账户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{fundAccountIds}")
    public AjaxResult remove(@PathVariable Long[] fundAccountIds)
    {
        return toAjax(fundAccountService.deleteFundAccountByIds(fundAccountIds));
    }
}
