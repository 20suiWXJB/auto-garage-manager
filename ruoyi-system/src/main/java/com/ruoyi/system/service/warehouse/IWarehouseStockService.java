package com.ruoyi.system.service.warehouse;

import java.util.List;
import com.ruoyi.system.domain.warehouse.WarehouseStockFlow;
import com.ruoyi.system.domain.warehouse.WarehouseStockOperation;

/**
 * 库存流水服务接口
 *
 * @author ruoyi
 */
public interface IWarehouseStockService
{
    List<WarehouseStockFlow> selectWarehouseStockFlowList(WarehouseStockFlow stockFlow);

    WarehouseStockFlow selectWarehouseStockFlowById(Long stockFlowId);

    int registerStockIn(WarehouseStockOperation operation);

    int registerStockOut(WarehouseStockOperation operation);

    int registerStockAdjust(WarehouseStockOperation operation);

    int registerOrderReserve(WarehouseStockOperation operation);

    int registerOrderRelease(WarehouseStockOperation operation);
}
