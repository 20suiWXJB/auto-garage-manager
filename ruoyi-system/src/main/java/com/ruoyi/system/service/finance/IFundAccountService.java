package com.ruoyi.system.service.finance;

import java.util.List;
import com.ruoyi.system.domain.finance.FundAccount;

/**
 * 资金账户服务接口
 *
 * @author ruoyi
 */
public interface IFundAccountService
{
    List<FundAccount> selectFundAccountList(FundAccount fundAccount);

    List<FundAccount> selectFundAccountOptions();

    FundAccount selectFundAccountById(Long fundAccountId);

    boolean checkAccountNameUnique(FundAccount fundAccount);

    int insertFundAccount(FundAccount fundAccount);

    int updateFundAccount(FundAccount fundAccount);

    int deleteFundAccountByIds(Long[] fundAccountIds);
}
