package com.ruoyi.system.service.finance;

import java.util.List;
import com.ruoyi.system.domain.finance.Bookkeeping;
import com.ruoyi.system.domain.finance.BookkeepingPayment;
import com.ruoyi.system.domain.finance.vo.BookkeepingStatsVO;

/**
 * 记账管理服务接口
 *
 * @author ruoyi
 */
public interface IBookkeepingService
{
    List<Bookkeeping> selectBookkeepingList(Bookkeeping bookkeeping);

    Bookkeeping selectBookkeepingById(Long bookkeepingId);

    int insertBookkeeping(Bookkeeping bookkeeping);

    int updateBookkeeping(Bookkeeping bookkeeping);

    int deleteBookkeepingByIds(Long[] bookkeepingIds);

    int registerReceipt(BookkeepingPayment payment);

    BookkeepingStatsVO selectStats();
}
