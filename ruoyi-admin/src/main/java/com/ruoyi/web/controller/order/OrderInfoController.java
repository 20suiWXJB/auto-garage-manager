package com.ruoyi.web.controller.order;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
import com.ruoyi.system.domain.order.OrderCompleteRequest;
import com.ruoyi.system.domain.order.OrderInfo;
import com.ruoyi.system.domain.order.vo.OrderDetailExportVO;
import com.ruoyi.system.service.order.IOrderInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 订单控制器
 *
 * @author ruoyi
 */
@Tag(name = "订单管理")
@RestController
@RequestMapping("/order")
public class OrderInfoController extends BaseController
{
    @Autowired
    private IOrderInfoService orderInfoService;

    @Operation(summary = "查询订单列表")
    @PreAuthorize("@ss.hasPermi('order:list')")
    @GetMapping("/list")
    public TableDataInfo list(OrderInfo orderInfo)
    {
        startPage();
        List<OrderInfo> list = orderInfoService.selectOrderInfoList(orderInfo);
        return getDataTable(list);
    }

    @Operation(summary = "查询订单详情")
    @PreAuthorize("@ss.hasPermi('order:list')")
    @GetMapping("/{orderId}")
    public AjaxResult getInfo(@PathVariable Long orderId)
    {
        return success(orderInfoService.selectOrderInfoById(orderId));
    }

    @Operation(summary = "新增订单")
    @PreAuthorize("@ss.hasPermi('order:add')")
    @Log(title = "订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody OrderInfo orderInfo)
    {
        orderInfo.setCreateBy(getUsername());
        return toAjax(orderInfoService.insertOrderInfo(orderInfo));
    }

    @Operation(summary = "修改订单")
    @PreAuthorize("@ss.hasPermi('order:edit')")
    @Log(title = "订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody OrderInfo orderInfo)
    {
        orderInfo.setUpdateBy(getUsername());
        return toAjax(orderInfoService.updateOrderInfo(orderInfo));
    }

    @Operation(summary = "订单完工")
    @PreAuthorize("@ss.hasPermi('order:complete')")
    @Log(title = "订单", businessType = BusinessType.UPDATE)
    @PostMapping("/{orderId}/complete")
    public AjaxResult complete(@PathVariable Long orderId, @Validated @RequestBody OrderCompleteRequest request)
    {
        request.setOrderId(orderId);
        request.setCreateBy(getUsername());
        return toAjax(orderInfoService.completeOrder(orderId, request));
    }

    @Operation(summary = "取消订单")
    @PreAuthorize("@ss.hasPermi('order:cancel')")
    @Log(title = "订单", businessType = BusinessType.UPDATE)
    @PostMapping("/{orderId}/cancel")
    public AjaxResult cancel(@PathVariable Long orderId)
    {
        return toAjax(orderInfoService.cancelOrder(orderId, getUsername()));
    }

    @Operation(summary = "导出订单明细")
    @Log(title = "订单明细", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('order:detail:export')")
    @PostMapping("/detail/export")
    public void exportDetail(HttpServletResponse response, OrderInfo orderInfo)
    {
        List<OrderDetailExportVO> list = orderInfoService.selectOrderDetailExportList(orderInfo);
        ExcelUtil<OrderDetailExportVO> util = new ExcelUtil<OrderDetailExportVO>(OrderDetailExportVO.class);
        util.exportExcel(response, list, "订单明细数据");
    }
}
