package com.ruoyi.system.service.warehouse;

import java.util.List;
import com.ruoyi.system.domain.warehouse.WarehousePart;

/**
 * 配件档案服务接口
 *
 * @author ruoyi
 */
public interface IWarehousePartService
{
    List<WarehousePart> selectWarehousePartList(WarehousePart part);

    List<WarehousePart> selectWarehousePartOptions();

    WarehousePart selectWarehousePartById(Long partId);

    boolean checkPartCodeUnique(WarehousePart part);

    int insertWarehousePart(WarehousePart part);

    int updateWarehousePart(WarehousePart part);

    int deleteWarehousePartByIds(Long[] partIds);
}
