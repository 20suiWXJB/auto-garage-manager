package com.ruoyi.web.controller.warehouse;

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
import com.ruoyi.system.domain.warehouse.WarehousePart;
import com.ruoyi.system.service.warehouse.IWarehousePartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 配件档案控制器
 *
 * @author ruoyi
 */
@Tag(name = "仓库管理-配件档案")
@RestController
@RequestMapping("/warehouse/part")
public class WarehousePartController extends BaseController
{
    @Autowired
    private IWarehousePartService warehousePartService;

    @Operation(summary = "查询配件档案列表")
    @PreAuthorize("@ss.hasPermi('warehouse:part:list')")
    @GetMapping("/list")
    public TableDataInfo list(WarehousePart part)
    {
        startPage();
        List<WarehousePart> list = warehousePartService.selectWarehousePartList(part);
        return getDataTable(list);
    }

    @Operation(summary = "导出配件档案")
    @Log(title = "配件档案", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('warehouse:part:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, WarehousePart part)
    {
        List<WarehousePart> list = warehousePartService.selectWarehousePartList(part);
        ExcelUtil<WarehousePart> util = new ExcelUtil<WarehousePart>(WarehousePart.class);
        util.exportExcel(response, list, "配件档案数据");
    }

    @Operation(summary = "查询配件下拉选项")
    @PreAuthorize("@ss.hasAnyPermi('warehouse:part:list,warehouse:stock:list,warehouse:stock:in,warehouse:stock:out,warehouse:stock:adjust,order:list,order:add,order:edit,order:complete')")
    @GetMapping("/options")
    public AjaxResult options()
    {
        return success(warehousePartService.selectWarehousePartOptions());
    }

    @Operation(summary = "查询配件档案详情")
    @PreAuthorize("@ss.hasPermi('warehouse:part:list')")
    @GetMapping("/{partId}")
    public AjaxResult getInfo(@PathVariable Long partId)
    {
        return success(warehousePartService.selectWarehousePartById(partId));
    }

    @Operation(summary = "新增配件档案")
    @PreAuthorize("@ss.hasPermi('warehouse:part:add')")
    @Log(title = "配件档案", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody WarehousePart part)
    {
        if (!warehousePartService.checkPartCodeUnique(part))
        {
            return error("新增配件档案'" + part.getPartCode() + "'失败，配件编码已存在");
        }
        part.setCreateBy(getUsername());
        return toAjax(warehousePartService.insertWarehousePart(part));
    }

    @Operation(summary = "修改配件档案")
    @PreAuthorize("@ss.hasPermi('warehouse:part:edit')")
    @Log(title = "配件档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody WarehousePart part)
    {
        if (!warehousePartService.checkPartCodeUnique(part))
        {
            return error("修改配件档案'" + part.getPartCode() + "'失败，配件编码已存在");
        }
        part.setUpdateBy(getUsername());
        return toAjax(warehousePartService.updateWarehousePart(part));
    }

    @Operation(summary = "删除配件档案")
    @PreAuthorize("@ss.hasPermi('warehouse:part:remove')")
    @Log(title = "配件档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{partIds}")
    public AjaxResult remove(@PathVariable Long[] partIds)
    {
        return toAjax(warehousePartService.deleteWarehousePartByIds(partIds));
    }
}
