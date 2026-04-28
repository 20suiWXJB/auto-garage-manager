package com.ruoyi.system.service.order;

import java.util.List;
import com.ruoyi.system.domain.order.OrderCompleteRequest;
import com.ruoyi.system.domain.order.OrderInfo;
import com.ruoyi.system.domain.order.vo.OrderDetailExportVO;

/**
 * 订单服务接口
 *
 * @author ruoyi
 */
public interface IOrderInfoService
{
    List<OrderInfo> selectOrderInfoList(OrderInfo orderInfo);

    OrderInfo selectOrderInfoById(Long orderId);

    int insertOrderInfo(OrderInfo orderInfo);

    int updateOrderInfo(OrderInfo orderInfo);

    int completeOrder(Long orderId, OrderCompleteRequest request);

    int cancelOrder(Long orderId, String operator);

    List<OrderDetailExportVO> selectOrderDetailExportList(OrderInfo orderInfo);
}
