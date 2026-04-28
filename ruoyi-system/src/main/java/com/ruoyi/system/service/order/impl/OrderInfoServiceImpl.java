package com.ruoyi.system.service.order.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.Seq;
import com.ruoyi.system.domain.finance.Bookkeeping;
import com.ruoyi.system.domain.order.OrderCompleteRequest;
import com.ruoyi.system.domain.order.OrderDetail;
import com.ruoyi.system.domain.order.OrderInfo;
import com.ruoyi.system.domain.order.vo.OrderDetailExportVO;
import com.ruoyi.system.domain.warehouse.WarehousePart;
import com.ruoyi.system.domain.warehouse.WarehouseStockOperation;
import com.ruoyi.system.mapper.order.OrderInfoMapper;
import com.ruoyi.system.mapper.warehouse.WarehousePartMapper;
import com.ruoyi.system.service.finance.IBookkeepingService;
import com.ruoyi.system.service.order.IOrderInfoService;
import com.ruoyi.system.service.warehouse.IWarehouseStockService;

/**
 * 订单服务实现
 *
 * @author ruoyi
 */
@Service
public class OrderInfoServiceImpl implements IOrderInfoService
{
    private static final String ORDER_TYPE_REPAIR = "repair";

    private static final String ORDER_TYPE_SALE = "sale";

    private static final String ORDER_STATUS_CONFIRMED = "confirmed";

    private static final String ORDER_STATUS_COMPLETED = "completed";

    private static final String ORDER_STATUS_CANCELLED = "cancelled";

    private static final String DETAIL_TYPE_SERVICE = "1";

    private static final String DETAIL_TYPE_PART = "2";

    private static final String BOOKKEEPING_ENTRY_TYPE_INCOME = "1";

    private static final String BOOKKEEPING_CATEGORY_PART_SALE = "3";

    private static final String DEL_FLAG_NORMAL = "0";

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private WarehousePartMapper warehousePartMapper;

    @Autowired
    private IWarehouseStockService warehouseStockService;

    @Autowired
    private IBookkeepingService bookkeepingService;

    @Override
    public List<OrderInfo> selectOrderInfoList(OrderInfo orderInfo)
    {
        return orderInfoMapper.selectOrderInfoList(orderInfo);
    }

    @Override
    public OrderInfo selectOrderInfoById(Long orderId)
    {
        OrderInfo orderInfo = orderInfoMapper.selectOrderInfoById(orderId);
        if (StringUtils.isNull(orderInfo))
        {
            throw new ServiceException("订单不存在");
        }
        return orderInfo;
    }

    @Override
    @Transactional
    public int insertOrderInfo(OrderInfo orderInfo)
    {
        validateOrderType(orderInfo.getOrderType());
        normalizeNewOrder(orderInfo);
        List<OrderDetail> normalizedDetails = normalizeDetails(orderInfo.getDetailList());
        fillAmounts(orderInfo, normalizedDetails);
        orderInfo.setOrderNo(generateOrderNo(orderInfo.getOrderType()));
        orderInfo.setOrderStatus(ORDER_STATUS_CONFIRMED);
        orderInfo.setCompletedTime(null);
        orderInfo.setPaidAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        orderInfo.setUnpaidAmount(orderInfo.getTotalAmount());
        orderInfo.setBookkeepingId(null);
        orderInfo.setDelFlag(DEL_FLAG_NORMAL);
        applyStockDiff(orderInfo.getOrderNo(), orderInfo.getOrderTime(), orderInfo.getHandlerName(), Map.of(),
            buildReservedQuantityMap(normalizedDetails), orderInfo.getCreateBy(), "订单创建预扣库存");
        int rows = orderInfoMapper.insertOrderInfo(orderInfo);
        bindOrderDetails(orderInfo.getOrderId(), normalizedDetails, orderInfo.getCreateBy());
        orderInfoMapper.insertOrderDetailBatch(normalizedDetails);
        return rows;
    }

    @Override
    @Transactional
    public int updateOrderInfo(OrderInfo orderInfo)
    {
        OrderInfo existing = selectOrderInfoById(orderInfo.getOrderId());
        ensureEditable(existing);
        validateOrderType(orderInfo.getOrderType());
        if (!StringUtils.equals(existing.getOrderType(), orderInfo.getOrderType()))
        {
            throw new ServiceException("订单类型创建后不允许修改");
        }
        List<OrderDetail> normalizedDetails = normalizeDetails(orderInfo.getDetailList());
        fillAmounts(orderInfo, normalizedDetails);
        orderInfo.setOrderNo(existing.getOrderNo());
        orderInfo.setOrderStatus(existing.getOrderStatus());
        orderInfo.setOrderTime(defaultDate(orderInfo.getOrderTime(), existing.getOrderTime()));
        orderInfo.setCompletedTime(existing.getCompletedTime());
        orderInfo.setBookkeepingId(existing.getBookkeepingId());
        orderInfo.setPaidAmount(existing.getPaidAmount());
        orderInfo.setUnpaidAmount(existing.getUnpaidAmount());
        orderInfo.setDelFlag(existing.getDelFlag());
        if (StringUtils.isEmpty(orderInfo.getHandlerName()))
        {
            orderInfo.setHandlerName(existing.getHandlerName());
        }
        applyStockDiff(existing.getOrderNo(), DateUtils.getNowDate(),
            StringUtils.isNotEmpty(orderInfo.getHandlerName()) ? orderInfo.getHandlerName() : existing.getHandlerName(),
            buildReservedQuantityMap(existing.getDetailList()), buildReservedQuantityMap(normalizedDetails), orderInfo.getUpdateBy(),
            "订单修改重算预扣库存");
        int rows = orderInfoMapper.updateOrderInfo(orderInfo);
        orderInfoMapper.deleteOrderDetailByOrderId(existing.getOrderId());
        bindOrderDetails(existing.getOrderId(), normalizedDetails, orderInfo.getUpdateBy());
        orderInfoMapper.insertOrderDetailBatch(normalizedDetails);
        return rows;
    }

    @Override
    @Transactional
    public int completeOrder(Long orderId, OrderCompleteRequest request)
    {
        OrderInfo existing = selectOrderInfoById(orderId);
        if (StringUtils.equals(ORDER_STATUS_CANCELLED, existing.getOrderStatus()))
        {
            throw new ServiceException("已取消订单不能完工");
        }
        if (StringUtils.equals(ORDER_STATUS_COMPLETED, existing.getOrderStatus()))
        {
            throw new ServiceException("订单已完工，请勿重复操作");
        }
        if (existing.getBookkeepingId() != null)
        {
            throw new ServiceException("订单已生成财务单，请勿重复完工");
        }
        if (safeAmount(existing.getTotalAmount()).compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("订单总金额必须大于0");
        }
        BigDecimal paidAmount = normalizeAmount(request.getPaidAmount());
        if (paidAmount.compareTo(existing.getTotalAmount()) > 0)
        {
            throw new ServiceException("首笔实收不能超过订单总金额");
        }
        if (paidAmount.compareTo(BigDecimal.ZERO) > 0 && request.getFundAccountId() == null)
        {
            throw new ServiceException("录入首笔实收时必须选择资金账户");
        }

        Date completedTime = defaultDate(request.getCompletedTime(), DateUtils.getNowDate());
        Bookkeeping bookkeeping = buildBookkeeping(existing, request, completedTime, paidAmount);
        bookkeepingService.insertBookkeeping(bookkeeping);

        OrderInfo update = new OrderInfo();
        update.setOrderId(existing.getOrderId());
        update.setOrderStatus(ORDER_STATUS_COMPLETED);
        update.setCompletedTime(completedTime);
        update.setBookkeepingId(bookkeeping.getBookkeepingId());
        update.setPaidAmount(paidAmount);
        update.setUnpaidAmount(existing.getTotalAmount().subtract(paidAmount).setScale(2, RoundingMode.HALF_UP));
        update.setUpdateBy(request.getCreateBy());
        return orderInfoMapper.updateOrderInfo(update);
    }

    @Override
    @Transactional
    public int cancelOrder(Long orderId, String operator)
    {
        OrderInfo existing = selectOrderInfoById(orderId);
        if (StringUtils.equals(ORDER_STATUS_COMPLETED, existing.getOrderStatus()))
        {
            throw new ServiceException("已完工订单不能取消");
        }
        if (StringUtils.equals(ORDER_STATUS_CANCELLED, existing.getOrderStatus()))
        {
            throw new ServiceException("订单已取消，请勿重复操作");
        }
        applyStockDiff(existing.getOrderNo(), DateUtils.getNowDate(), existing.getHandlerName(),
            buildReservedQuantityMap(existing.getDetailList()), Map.of(), operator, "订单取消释放预扣库存");
        OrderInfo update = new OrderInfo();
        update.setOrderId(existing.getOrderId());
        update.setOrderStatus(ORDER_STATUS_CANCELLED);
        update.setUpdateBy(operator);
        return orderInfoMapper.updateOrderInfo(update);
    }

    @Override
    public List<OrderDetailExportVO> selectOrderDetailExportList(OrderInfo orderInfo)
    {
        return orderInfoMapper.selectOrderDetailExportList(orderInfo);
    }

    private void normalizeNewOrder(OrderInfo orderInfo)
    {
        orderInfo.setOrderTime(defaultDate(orderInfo.getOrderTime(), DateUtils.getNowDate()));
        if (StringUtils.isEmpty(orderInfo.getHandlerName()))
        {
            orderInfo.setHandlerName(orderInfo.getCreateBy());
        }
    }

    private void ensureEditable(OrderInfo existing)
    {
        if (StringUtils.equals(ORDER_STATUS_COMPLETED, existing.getOrderStatus()))
        {
            throw new ServiceException("已完工订单不允许修改");
        }
        if (StringUtils.equals(ORDER_STATUS_CANCELLED, existing.getOrderStatus()))
        {
            throw new ServiceException("已取消订单不允许修改");
        }
    }

    private List<OrderDetail> normalizeDetails(List<OrderDetail> detailList)
    {
        if (CollectionUtils.isEmpty(detailList))
        {
            throw new ServiceException("订单明细不能为空");
        }
        Set<Long> partIds = detailList.stream()
            .filter(detail -> StringUtils.equals(DETAIL_TYPE_PART, detail.getDetailType()))
            .map(OrderDetail::getPartId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        Map<Long, WarehousePart> partMap = loadPartMap(partIds);

        List<OrderDetail> normalized = new ArrayList<>();
        for (int i = 0; i < detailList.size(); i++)
        {
            OrderDetail source = detailList.get(i);
            OrderDetail target = new OrderDetail();
            target.setDetailId(source.getDetailId());
            target.setDetailType(source.getDetailType());
            target.setLineNo(i + 1);
            target.setRemark(source.getRemark());
            if (StringUtils.equals(DETAIL_TYPE_SERVICE, source.getDetailType()))
            {
                fillServiceDetail(target, source);
            }
            else if (StringUtils.equals(DETAIL_TYPE_PART, source.getDetailType()))
            {
                fillPartDetail(target, source, partMap);
            }
            else
            {
                throw new ServiceException("订单明细类型不支持");
            }
            normalized.add(target);
        }
        return normalized;
    }

    private void fillServiceDetail(OrderDetail target, OrderDetail source)
    {
        if (StringUtils.isEmpty(source.getItemName()))
        {
            throw new ServiceException("服务项目名称不能为空");
        }
        BigDecimal quantity = normalizePositiveQuantity(source.getQuantity(), "服务项目数量必须大于0");
        BigDecimal unitPrice = normalizeAmount(source.getUnitPrice());
        target.setItemName(source.getItemName().trim());
        target.setPartId(null);
        target.setPartCode(null);
        target.setPartName(null);
        target.setSpecModel(null);
        target.setUnit(null);
        target.setQuantity(quantity);
        target.setUnitPrice(unitPrice);
        target.setAmount(calculateAmount(quantity, unitPrice));
        target.setReservedQuantity(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
    }

    private void fillPartDetail(OrderDetail target, OrderDetail source, Map<Long, WarehousePart> partMap)
    {
        if (source.getPartId() == null)
        {
            throw new ServiceException("配件明细必须选择配件");
        }
        WarehousePart part = partMap.get(source.getPartId());
        if (StringUtils.isNull(part))
        {
            throw new ServiceException("配件档案不存在或已删除");
        }
        BigDecimal quantity = normalizePositiveQuantity(source.getQuantity(), "配件数量必须大于0");
        BigDecimal unitPrice = source.getUnitPrice() == null ? normalizeAmount(part.getSalePrice()) : normalizeAmount(source.getUnitPrice());
        target.setItemName(part.getPartName());
        target.setPartId(part.getPartId());
        target.setPartCode(part.getPartCode());
        target.setPartName(part.getPartName());
        target.setSpecModel(part.getSpecModel());
        target.setUnit(part.getUnit());
        target.setQuantity(quantity);
        target.setUnitPrice(unitPrice);
        target.setAmount(calculateAmount(quantity, unitPrice));
        target.setReservedQuantity(quantity);
    }

    private Map<Long, WarehousePart> loadPartMap(Set<Long> partIds)
    {
        if (CollectionUtils.isEmpty(partIds))
        {
            return Map.of();
        }
        List<WarehousePart> parts = warehousePartMapper.selectWarehousePartListByIds(new ArrayList<>(partIds));
        return parts.stream().collect(Collectors.toMap(WarehousePart::getPartId, part -> part, (left, right) -> left,
            LinkedHashMap::new));
    }

    private void fillAmounts(OrderInfo orderInfo, List<OrderDetail> detailList)
    {
        BigDecimal serviceAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal partAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        for (OrderDetail detail : detailList)
        {
            if (StringUtils.equals(DETAIL_TYPE_SERVICE, detail.getDetailType()))
            {
                serviceAmount = serviceAmount.add(safeAmount(detail.getAmount()));
            }
            else
            {
                partAmount = partAmount.add(safeAmount(detail.getAmount()));
            }
        }
        orderInfo.setServiceAmount(serviceAmount);
        orderInfo.setPartAmount(partAmount);
        orderInfo.setTotalAmount(serviceAmount.add(partAmount).setScale(2, RoundingMode.HALF_UP));
    }

    private void bindOrderDetails(Long orderId, List<OrderDetail> detailList, String operator)
    {
        for (OrderDetail detail : detailList)
        {
            detail.setOrderId(orderId);
            detail.setCreateBy(operator);
        }
    }

    private Map<Long, BigDecimal> buildReservedQuantityMap(List<OrderDetail> detailList)
    {
        if (CollectionUtils.isEmpty(detailList))
        {
            return Map.of();
        }
        Map<Long, BigDecimal> quantityMap = new LinkedHashMap<>();
        for (OrderDetail detail : detailList)
        {
            if (!StringUtils.equals(DETAIL_TYPE_PART, detail.getDetailType()) || detail.getPartId() == null)
            {
                continue;
            }
            quantityMap.merge(detail.getPartId(), normalizeAmount(detail.getReservedQuantity()), BigDecimal::add);
        }
        return quantityMap;
    }

    private void applyStockDiff(String orderNo, Date businessTime, String handlerName, Map<Long, BigDecimal> oldMap,
        Map<Long, BigDecimal> newMap, String operator, String remark)
    {
        Set<Long> partIds = new java.util.LinkedHashSet<>();
        partIds.addAll(oldMap.keySet());
        partIds.addAll(newMap.keySet());
        for (Long partId : partIds)
        {
            BigDecimal oldQuantity = normalizeAmount(oldMap.get(partId));
            BigDecimal newQuantity = normalizeAmount(newMap.get(partId));
            BigDecimal diff = newQuantity.subtract(oldQuantity).setScale(2, RoundingMode.HALF_UP);
            if (diff.compareTo(BigDecimal.ZERO) > 0)
            {
                warehouseStockService.registerOrderReserve(buildStockOperation(partId, diff, orderNo, businessTime,
                    handlerName, operator, remark));
            }
            else if (diff.compareTo(BigDecimal.ZERO) < 0)
            {
                warehouseStockService.registerOrderRelease(buildStockOperation(partId, diff.abs(), orderNo, businessTime,
                    handlerName, operator, remark));
            }
        }
    }

    private WarehouseStockOperation buildStockOperation(Long partId, BigDecimal quantity, String orderNo, Date businessTime,
        String handlerName, String operator, String remark)
    {
        WarehouseStockOperation operation = new WarehouseStockOperation();
        operation.setPartId(partId);
        operation.setQuantity(quantity);
        operation.setBusinessNo(orderNo);
        operation.setBusinessTime(defaultDate(businessTime, DateUtils.getNowDate()));
        operation.setHandlerName(handlerName);
        operation.setCreateBy(operator);
        operation.setRemark(remark);
        return operation;
    }

    private Bookkeeping buildBookkeeping(OrderInfo orderInfo, OrderCompleteRequest request, Date completedTime, BigDecimal paidAmount)
    {
        Bookkeeping bookkeeping = new Bookkeeping();
        bookkeeping.setBusinessDate(completedTime);
        bookkeeping.setEntryType(BOOKKEEPING_ENTRY_TYPE_INCOME);
        bookkeeping.setCategory(BOOKKEEPING_CATEGORY_PART_SALE);
        bookkeeping.setTotalAmount(orderInfo.getTotalAmount());
        bookkeeping.setPaidAmount(paidAmount);
        bookkeeping.setFundAccountId(request.getFundAccountId());
        bookkeeping.setPaymentMethod(request.getPaymentMethod());
        bookkeeping.setHandlerName(orderInfo.getHandlerName());
        bookkeeping.setCustomerName(orderInfo.getCustomerName());
        bookkeeping.setCustomerPhone(orderInfo.getCustomerPhone());
        bookkeeping.setLicensePlate(orderInfo.getLicensePlate());
        bookkeeping.setCarModel(orderInfo.getCarModel());
        bookkeeping.setBusinessDesc("订单完工自动生成：" + orderInfo.getOrderNo());
        bookkeeping.setRemark(orderInfo.getRemark());
        bookkeeping.setCreateBy(request.getCreateBy());
        return bookkeeping;
    }

    private String generateOrderNo(String orderType)
    {
        String prefix = StringUtils.equals(ORDER_TYPE_REPAIR, orderType) ? "WX" : "XS";
        return prefix + Seq.getId();
    }

    private void validateOrderType(String orderType)
    {
        if (!StringUtils.equalsAny(orderType, ORDER_TYPE_REPAIR, ORDER_TYPE_SALE))
        {
            throw new ServiceException("不支持的订单类型");
        }
    }

    private BigDecimal normalizePositiveQuantity(BigDecimal quantity, String message)
    {
        BigDecimal normalized = normalizeAmount(quantity);
        if (normalized.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException(message);
        }
        return normalized;
    }

    private BigDecimal calculateAmount(BigDecimal quantity, BigDecimal unitPrice)
    {
        return quantity.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal normalizeAmount(BigDecimal amount)
    {
        return safeAmount(amount).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal safeAmount(BigDecimal amount)
    {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private Date defaultDate(Date target, Date fallback)
    {
        return target == null ? fallback : target;
    }
}
