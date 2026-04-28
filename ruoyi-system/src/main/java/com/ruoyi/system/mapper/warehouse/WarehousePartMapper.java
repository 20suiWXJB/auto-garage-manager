package com.ruoyi.system.mapper.warehouse;

import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.warehouse.WarehousePart;

/**
 * 配件档案数据层
 *
 * @author ruoyi
 */
public interface WarehousePartMapper
{
    List<WarehousePart> selectWarehousePartList(WarehousePart part);

    List<WarehousePart> selectWarehousePartOptions();

    WarehousePart selectWarehousePartById(Long partId);

    WarehousePart checkPartCodeUnique(String partCode);

    int insertWarehousePart(WarehousePart part);

    int updateWarehousePart(WarehousePart part);

    int deleteWarehousePartByIds(Long[] partIds);

    int countWarehouseStockFlowUsed(Long[] partIds);

    int updatePartStock(@Param("partId") Long partId, @Param("beforeStock") BigDecimal beforeStock,
        @Param("afterStock") BigDecimal afterStock, @Param("updateBy") String updateBy);

    List<WarehousePart> selectWarehousePartListByIds(@Param("partIds") List<Long> partIds);
}
