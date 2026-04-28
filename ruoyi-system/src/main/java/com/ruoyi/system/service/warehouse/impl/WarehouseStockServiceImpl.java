package com.ruoyi.system.service.warehouse.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.Seq;
import com.ruoyi.system.domain.warehouse.WarehousePart;
import com.ruoyi.system.domain.warehouse.WarehouseStockFlow;
import com.ruoyi.system.domain.warehouse.WarehouseStockOperation;
import com.ruoyi.system.mapper.warehouse.WarehousePartMapper;
import com.ruoyi.system.mapper.warehouse.WarehouseStockFlowMapper;
import com.ruoyi.system.service.warehouse.IWarehouseStockService;

/**
 * 库存流水服务实现
 *
 * @author ruoyi
 */
@Service
public class WarehouseStockServiceImpl implements IWarehouseStockService
{
    private static final String BUSINESS_TYPE_IN = "1";

    private static final String BUSINESS_TYPE_OUT = "2";

    private static final String BUSINESS_TYPE_ADJUST = "3";

    private static final String BUSINESS_TYPE_ORDER_RESERVE = "4";

    private static final String BUSINESS_TYPE_ORDER_RELEASE = "5";

    private static final String ADJUST_DIRECTION_INCREASE = "1";

    private static final String ADJUST_DIRECTION_DECREASE = "2";

    private static final String DEL_FLAG_NORMAL = "0";

    @Autowired
    private WarehousePartMapper warehousePartMapper;

    @Autowired
    private WarehouseStockFlowMapper warehouseStockFlowMapper;

    @Override
    public List<WarehouseStockFlow> selectWarehouseStockFlowList(WarehouseStockFlow stockFlow)
    {
        return warehouseStockFlowMapper.selectWarehouseStockFlowList(stockFlow);
    }

    @Override
    public WarehouseStockFlow selectWarehouseStockFlowById(Long stockFlowId)
    {
        return warehouseStockFlowMapper.selectWarehouseStockFlowById(stockFlowId);
    }

    @Override
    @Transactional
    public int registerStockIn(WarehouseStockOperation operation)
    {
        return executeStockOperation(BUSINESS_TYPE_IN, operation);
    }

    @Override
    @Transactional
    public int registerStockOut(WarehouseStockOperation operation)
    {
        return executeStockOperation(BUSINESS_TYPE_OUT, operation);
    }

    @Override
    @Transactional
    public int registerStockAdjust(WarehouseStockOperation operation)
    {
        return executeStockOperation(BUSINESS_TYPE_ADJUST, operation);
    }

    @Override
    @Transactional
    public int registerOrderReserve(WarehouseStockOperation operation)
    {
        return executeStockOperation(BUSINESS_TYPE_ORDER_RESERVE, operation);
    }

    @Override
    @Transactional
    public int registerOrderRelease(WarehouseStockOperation operation)
    {
        return executeStockOperation(BUSINESS_TYPE_ORDER_RELEASE, operation);
    }

    private int executeStockOperation(String businessType, WarehouseStockOperation operation)
    {
        WarehousePart part = requirePart(operation.getPartId());
        BigDecimal beforeStock = defaultAmount(part.getCurrentStock());
        BigDecimal quantity = normalizePositiveQuantity(operation.getQuantity());
        BigDecimal changeQuantity = resolveChangeQuantity(businessType, operation, beforeStock, quantity);
        BigDecimal afterStock = beforeStock.add(changeQuantity).setScale(2, RoundingMode.HALF_UP);
        String operator = defaultOperator(operation);

        int updatedRows = warehousePartMapper.updatePartStock(part.getPartId(), beforeStock, afterStock, operator);
        if (updatedRows == 0)
        {
            throw new ServiceException("库存已变化，请刷新后重试");
        }

        WarehouseStockFlow stockFlow = buildStockFlow(part, operation, businessType, changeQuantity, beforeStock, afterStock,
            operator);
        return warehouseStockFlowMapper.insertWarehouseStockFlow(stockFlow);
    }

    private WarehousePart requirePart(Long partId)
    {
        WarehousePart part = warehousePartMapper.selectWarehousePartById(partId);
        if (StringUtils.isNull(part))
        {
            throw new ServiceException("配件档案不存在");
        }
        return part;
    }

    private BigDecimal resolveChangeQuantity(String businessType, WarehouseStockOperation operation, BigDecimal beforeStock,
        BigDecimal quantity)
    {
        if (StringUtils.equals(BUSINESS_TYPE_IN, businessType))
        {
            return quantity;
        }
        if (StringUtils.equals(BUSINESS_TYPE_ORDER_RELEASE, businessType))
        {
            return quantity;
        }
        if (StringUtils.equals(BUSINESS_TYPE_OUT, businessType))
        {
            if (beforeStock.compareTo(quantity) < 0)
            {
                throw new ServiceException("出库数量不能超过当前库存");
            }
            return quantity.negate();
        }
        if (StringUtils.equals(BUSINESS_TYPE_ORDER_RESERVE, businessType))
        {
            if (beforeStock.compareTo(quantity) < 0)
            {
                throw new ServiceException("库存不足，无法完成订单预扣");
            }
            return quantity.negate();
        }
        if (!StringUtils.equalsAny(operation.getAdjustDirection(), ADJUST_DIRECTION_INCREASE, ADJUST_DIRECTION_DECREASE))
        {
            throw new ServiceException("库存调整方向不能为空");
        }
        if (StringUtils.equals(ADJUST_DIRECTION_DECREASE, operation.getAdjustDirection()) && beforeStock.compareTo(quantity) < 0)
        {
            throw new ServiceException("调整减少数量不能超过当前库存");
        }
        return StringUtils.equals(ADJUST_DIRECTION_INCREASE, operation.getAdjustDirection()) ? quantity : quantity.negate();
    }

    private WarehouseStockFlow buildStockFlow(WarehousePart part, WarehouseStockOperation operation, String businessType,
        BigDecimal changeQuantity, BigDecimal beforeStock, BigDecimal afterStock, String operator)
    {
        WarehouseStockFlow stockFlow = new WarehouseStockFlow();
        stockFlow.setFlowNo(generateFlowNo(businessType));
        stockFlow.setBusinessType(businessType);
        stockFlow.setBusinessNo(operation.getBusinessNo());
        stockFlow.setPartId(part.getPartId());
        stockFlow.setPartCode(part.getPartCode());
        stockFlow.setPartName(part.getPartName());
        stockFlow.setSpecModel(part.getSpecModel());
        stockFlow.setUnit(part.getUnit());
        stockFlow.setChangeQuantity(changeQuantity);
        stockFlow.setBeforeStock(beforeStock);
        stockFlow.setAfterStock(afterStock);
        stockFlow.setBusinessTime(defaultDate(operation.getBusinessTime()));
        stockFlow.setHandlerName(StringUtils.isNotEmpty(operation.getHandlerName()) ? operation.getHandlerName() : operator);
        stockFlow.setAdjustDirection(operation.getAdjustDirection());
        stockFlow.setDelFlag(DEL_FLAG_NORMAL);
        stockFlow.setCreateBy(operator);
        stockFlow.setRemark(operation.getRemark());
        return stockFlow;
    }

    private String generateFlowNo(String businessType)
    {
        String prefix = "RK";
        if (StringUtils.equals(BUSINESS_TYPE_OUT, businessType))
        {
            prefix = "CK";
        }
        else if (StringUtils.equals(BUSINESS_TYPE_ADJUST, businessType))
        {
            prefix = "TJ";
        }
        else if (StringUtils.equals(BUSINESS_TYPE_ORDER_RESERVE, businessType))
        {
            prefix = "DK";
        }
        else if (StringUtils.equals(BUSINESS_TYPE_ORDER_RELEASE, businessType))
        {
            prefix = "SF";
        }
        return prefix + DateUtils.dateTime() + Seq.getId().substring(6);
    }

    private BigDecimal normalizePositiveQuantity(BigDecimal quantity)
    {
        BigDecimal normalized = defaultAmount(quantity);
        if (normalized.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("操作数量必须大于0");
        }
        return normalized;
    }

    private BigDecimal defaultAmount(BigDecimal amount)
    {
        return StringUtils.isNull(amount) ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
            : amount.setScale(2, RoundingMode.HALF_UP);
    }

    private Date defaultDate(Date date)
    {
        return StringUtils.isNull(date) ? DateUtils.getNowDate() : date;
    }

    private String defaultOperator(WarehouseStockOperation operation)
    {
        if (StringUtils.isNotEmpty(operation.getCreateBy()))
        {
            return operation.getCreateBy();
        }
        if (StringUtils.isNotEmpty(operation.getUpdateBy()))
        {
            return operation.getUpdateBy();
        }
        return "system";
    }
}
