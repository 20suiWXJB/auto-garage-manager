package com.ruoyi.web.controller.finance;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
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
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.finance.Bookkeeping;
import com.ruoyi.system.domain.finance.BookkeepingPayment;
import com.ruoyi.system.service.finance.IBookkeepingService;

/**
 * 记账管理控制器
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/finance/bookkeeping")
public class BookkeepingController extends BaseController
{
    @Autowired
    private IBookkeepingService bookkeepingService;

    @PreAuthorize("@ss.hasPermi('finance:bookkeeping:list')")
    @GetMapping("/list")
    public TableDataInfo list(Bookkeeping bookkeeping)
    {
        startPage();
        List<Bookkeeping> list = bookkeepingService.selectBookkeepingList(bookkeeping);
        return getDataTable(list);
    }

    @Log(title = "记账管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('finance:bookkeeping:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, Bookkeeping bookkeeping)
    {
        List<Bookkeeping> list = bookkeepingService.selectBookkeepingList(bookkeeping);
        ExcelUtil<Bookkeeping> util = new ExcelUtil<Bookkeeping>(Bookkeeping.class);
        util.exportExcel(response, list, "记账流水数据");
    }

    @PreAuthorize("@ss.hasPermi('finance:bookkeeping:list')")
    @GetMapping("/{bookkeepingId}")
    public AjaxResult getInfo(@PathVariable Long bookkeepingId)
    {
        return success(bookkeepingService.selectBookkeepingById(bookkeepingId));
    }

    @PreAuthorize("@ss.hasPermi('finance:bookkeeping:list')")
    @GetMapping("/stats")
    public AjaxResult stats()
    {
        return success(bookkeepingService.selectStats());
    }

    @PreAuthorize("@ss.hasPermi('finance:bookkeeping:add')")
    @Log(title = "记账管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody Bookkeeping bookkeeping)
    {
        bookkeeping.setCreateBy(getUsername());
        return toAjax(bookkeepingService.insertBookkeeping(bookkeeping));
    }

    @PreAuthorize("@ss.hasPermi('finance:bookkeeping:edit')")
    @Log(title = "记账管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody Bookkeeping bookkeeping)
    {
        bookkeeping.setUpdateBy(getUsername());
        return toAjax(bookkeepingService.updateBookkeeping(bookkeeping));
    }

    @PreAuthorize("@ss.hasPermi('finance:bookkeeping:remove')")
    @Log(title = "记账管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{bookkeepingIds}")
    public AjaxResult remove(@PathVariable Long[] bookkeepingIds)
    {
        return toAjax(bookkeepingService.deleteBookkeepingByIds(bookkeepingIds));
    }

    @PreAuthorize("@ss.hasPermi('finance:bookkeeping:receipt')")
    @Log(title = "记账收款", businessType = BusinessType.UPDATE)
    @PostMapping("/receipt")
    public AjaxResult receipt(@Validated @RequestBody BookkeepingPayment payment)
    {
        payment.setCreateBy(getUsername());
        return toAjax(bookkeepingService.registerReceipt(payment));
    }
}
