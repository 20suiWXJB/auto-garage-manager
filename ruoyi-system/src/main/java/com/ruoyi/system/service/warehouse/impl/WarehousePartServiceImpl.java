package com.ruoyi.system.service.warehouse.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.warehouse.WarehousePart;
import com.ruoyi.system.mapper.warehouse.WarehousePartMapper;
import com.ruoyi.system.service.warehouse.IWarehousePartService;

/**
 * 配件档案服务实现
 *
 * @author ruoyi
 */
@Service
public class WarehousePartServiceImpl implements IWarehousePartService
{
    private static final String DEL_FLAG_NORMAL = "0";

    @Autowired
    private WarehousePartMapper warehousePartMapper;

    @Override
    public List<WarehousePart> selectWarehousePartList(WarehousePart part)
    {
        return warehousePartMapper.selectWarehousePartList(part);
    }

    @Override
    public List<WarehousePart> selectWarehousePartOptions()
    {
        return warehousePartMapper.selectWarehousePartOptions();
    }

    @Override
    public WarehousePart selectWarehousePartById(Long partId)
    {
        return warehousePartMapper.selectWarehousePartById(partId);
    }

    @Override
    public boolean checkPartCodeUnique(WarehousePart part)
    {
        Long partId = StringUtils.isNull(part.getPartId()) ? -1L : part.getPartId();
        WarehousePart info = warehousePartMapper.checkPartCodeUnique(part.getPartCode());
        if (StringUtils.isNotNull(info) && info.getPartId().longValue() != partId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public int insertWarehousePart(WarehousePart part)
    {
        part.setPurchasePrice(normalizeAmount(part.getPurchasePrice()));
        part.setSalePrice(normalizeAmount(part.getSalePrice()));
        part.setCurrentStock(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        part.setMinStock(normalizeAmount(part.getMinStock()));
        part.setDelFlag(DEL_FLAG_NORMAL);
        return warehousePartMapper.insertWarehousePart(part);
    }

    @Override
    public int updateWarehousePart(WarehousePart part)
    {
        WarehousePart existing = requirePart(part.getPartId());
        part.setPurchasePrice(normalizeAmount(part.getPurchasePrice()));
        part.setSalePrice(normalizeAmount(part.getSalePrice()));
        part.setCurrentStock(existing.getCurrentStock());
        part.setMinStock(normalizeAmount(part.getMinStock()));
        part.setDelFlag(existing.getDelFlag());
        return warehousePartMapper.updateWarehousePart(part);
    }

    @Override
    public int deleteWarehousePartByIds(Long[] partIds)
    {
        if (warehousePartMapper.countWarehouseStockFlowUsed(partIds) > 0)
        {
            throw new ServiceException("配件已存在库存流水，不能删除，请通过停用或库存调整维护");
        }
        return warehousePartMapper.deleteWarehousePartByIds(partIds);
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

    private BigDecimal normalizeAmount(BigDecimal amount)
    {
        return StringUtils.isNull(amount) ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
            : amount.setScale(2, RoundingMode.HALF_UP);
    }
}
