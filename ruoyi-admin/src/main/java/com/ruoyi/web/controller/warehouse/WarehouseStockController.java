package com.ruoyi.web.controller.warehouse;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.warehouse.WarehouseStockFlow;
import com.ruoyi.system.domain.warehouse.WarehouseStockOperation;
import com.ruoyi.system.service.warehouse.IWarehouseStockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 库存流水控制器
 *
 * @author ruoyi
 */
@Tag(name = "仓库管理-库存流水")
@RestController
@RequestMapping("/warehouse/stock")
public class WarehouseStockController extends BaseController
{
    @Autowired
    private IWarehouseStockService warehouseStockService;

    @Operation(summary = "查询库存流水列表")
    @PreAuthorize("@ss.hasPermi('warehouse:stock:list')")
    @GetMapping("/list")
    public TableDataInfo list(WarehouseStockFlow stockFlow)
    {
        startPage();
        List<WarehouseStockFlow> list = warehouseStockService.selectWarehouseStockFlowList(stockFlow);
        return getDataTable(list);
    }

    @Operation(summary = "导出库存流水")
    @Log(title = "库存流水", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('warehouse:stock:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, WarehouseStockFlow stockFlow)
    {
        List<WarehouseStockFlow> list = warehouseStockService.selectWarehouseStockFlowList(stockFlow);
        ExcelUtil<WarehouseStockFlow> util = new ExcelUtil<WarehouseStockFlow>(WarehouseStockFlow.class);
        util.exportExcel(response, list, "库存流水数据");
    }

    @Operation(summary = "查询库存流水详情")
    @PreAuthorize("@ss.hasPermi('warehouse:stock:list')")
    @GetMapping("/{stockFlowId}")
    public AjaxResult getInfo(@PathVariable Long stockFlowId)
    {
        return success(warehouseStockService.selectWarehouseStockFlowById(stockFlowId));
    }

    @Operation(summary = "登记入库")
    @PreAuthorize("@ss.hasPermi('warehouse:stock:in')")
    @Log(title = "库存入库", businessType = BusinessType.INSERT)
    @PostMapping("/in")
    public AjaxResult stockIn(@Validated @RequestBody WarehouseStockOperation operation)
    {
        operation.setCreateBy(getUsername());
        return toAjax(warehouseStockService.registerStockIn(operation));
    }

    @Operation(summary = "登记出库")
    @PreAuthorize("@ss.hasPermi('warehouse:stock:out')")
    @Log(title = "库存出库", businessType = BusinessType.INSERT)
    @PostMapping("/out")
    public AjaxResult stockOut(@Validated @RequestBody WarehouseStockOperation operation)
    {
        operation.setCreateBy(getUsername());
        return toAjax(warehouseStockService.registerStockOut(operation));
    }

    @Operation(summary = "登记库存调整")
    @PreAuthorize("@ss.hasPermi('warehouse:stock:adjust')")
    @Log(title = "库存调整", businessType = BusinessType.UPDATE)
    @PostMapping("/adjust")
    public AjaxResult adjust(@Validated @RequestBody WarehouseStockOperation operation)
    {
        operation.setCreateBy(getUsername());
        return toAjax(warehouseStockService.registerStockAdjust(operation));
    }
}
