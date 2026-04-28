package com.ruoyi.system.mapper.order;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.order.OrderDetail;
import com.ruoyi.system.domain.order.OrderInfo;
import com.ruoyi.system.domain.order.vo.OrderDetailExportVO;

/**
 * 订单数据层
 *
 * @author ruoyi
 */
public interface OrderInfoMapper
{
    List<OrderInfo> selectOrderInfoList(OrderInfo orderInfo);

    OrderInfo selectOrderInfoById(Long orderId);

    int insertOrderInfo(OrderInfo orderInfo);

    int updateOrderInfo(OrderInfo orderInfo);

    List<OrderDetail> selectOrderDetailListByOrderId(Long orderId);

    int insertOrderDetailBatch(@Param("list") List<OrderDetail> detailList);

    int deleteOrderDetailByOrderId(Long orderId);

    List<OrderDetailExportVO> selectOrderDetailExportList(OrderInfo orderInfo);
}
