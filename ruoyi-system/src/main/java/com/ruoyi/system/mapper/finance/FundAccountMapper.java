package com.ruoyi.system.mapper.finance;

import java.util.List;
import com.ruoyi.system.domain.finance.FundAccount;

/**
 * 资金账户数据层
 *
 * @author ruoyi
 */
public interface FundAccountMapper
{
    List<FundAccount> selectFundAccountList(FundAccount fundAccount);

    List<FundAccount> selectFundAccountOptions();

    FundAccount selectFundAccountById(Long fundAccountId);

    FundAccount checkAccountNameUnique(String accountName);

    int insertFundAccount(FundAccount fundAccount);

    int updateFundAccount(FundAccount fundAccount);

    int deleteFundAccountByIds(Long[] fundAccountIds);

    int countFundAccountUsed(Long[] fundAccountIds);
}
