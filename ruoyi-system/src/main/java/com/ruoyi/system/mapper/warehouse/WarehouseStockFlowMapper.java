package com.ruoyi.system.mapper.warehouse;

import java.util.List;
import com.ruoyi.system.domain.warehouse.WarehouseStockFlow;

/**
 * 库存流水数据层
 *
 * @author ruoyi
 */
public interface WarehouseStockFlowMapper
{
    List<WarehouseStockFlow> selectWarehouseStockFlowList(WarehouseStockFlow stockFlow);

    WarehouseStockFlow selectWarehouseStockFlowById(Long stockFlowId);

    int insertWarehouseStockFlow(WarehouseStockFlow stockFlow);
}
