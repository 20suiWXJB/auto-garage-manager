-- ----------------------------
-- 财务管理 V1 - 经营流水型记账模块
-- ----------------------------

-- ----------------------------
-- 1、资金账户表
-- ----------------------------
drop table if exists acct_fund_account;
create table acct_fund_account (
  fund_account_id    bigint(20)      not null auto_increment    comment '账户ID',
  account_name       varchar(50)     not null                   comment '账户名称',
  account_type       char(1)         default '1'                comment '账户类型',
  opening_balance    decimal(12,2)   default 0.00              comment '期初余额',
  status             char(1)         default '0'                comment '状态（0正常 1停用）',
  del_flag           char(1)         default '0'                comment '删除标志（0存在 2删除）',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (fund_account_id)
) engine=innodb auto_increment=100 comment = '资金账户表';

insert into acct_fund_account values(100, '现金',   '1', 0.00, '0', '0', 'admin', sysdate(), '', null, '默认现金账户');
insert into acct_fund_account values(101, '微信',   '2', 0.00, '0', '0', 'admin', sysdate(), '', null, '默认微信账户');
insert into acct_fund_account values(102, '支付宝', '3', 0.00, '0', '0', 'admin', sysdate(), '', null, '默认支付宝账户');
insert into acct_fund_account values(103, '银行卡', '4', 0.00, '0', '0', 'admin', sysdate(), '', null, '默认银行卡账户');

-- ----------------------------
-- 2、记账流水主表
-- ----------------------------
drop table if exists acct_bookkeeping;
create table acct_bookkeeping (
  bookkeeping_id     bigint(20)      not null auto_increment    comment '记账ID',
  entry_no           varchar(32)     not null                   comment '单号',
  business_date      datetime        not null                   comment '业务日期',
  entry_type         char(1)         not null                   comment '流水类型',
  category           varchar(32)     not null                   comment '业务分类',
  total_amount       decimal(12,2)   not null default 0.00     comment '总金额',
  paid_amount        decimal(12,2)   not null default 0.00     comment '已收金额',
  unpaid_amount      decimal(12,2)   not null default 0.00     comment '未收金额',
  status             char(1)         not null                   comment '状态',
  fund_account_id    bigint(20)      default null               comment '资金账户ID',
  fund_account_name  varchar(50)     default ''                 comment '资金账户名称',
  payment_method     varchar(32)     default null               comment '收付款方式',
  handler_name       varchar(50)     default null               comment '经办人',
  customer_name      varchar(50)     default null               comment '客户姓名',
  customer_phone     varchar(20)     default null               comment '客户手机号',
  license_plate      varchar(20)     default null               comment '车牌号',
  car_model          varchar(50)     default null               comment '车型',
  business_desc      varchar(255)    default null               comment '业务描述',
  del_flag           char(1)         default '0'                comment '删除标志（0存在 2删除）',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (bookkeeping_id),
  unique key uk_acct_bookkeeping_no (entry_no),
  key idx_acct_bookkeeping_date (business_date),
  key idx_acct_bookkeeping_type_status (entry_type, status),
  key idx_acct_bookkeeping_customer (customer_name),
  key idx_acct_bookkeeping_plate (license_plate)
) engine=innodb auto_increment=1000 comment = '记账流水主表';

-- ----------------------------
-- 3、收付款明细表
-- ----------------------------
drop table if exists acct_bookkeeping_payment;
create table acct_bookkeeping_payment (
  payment_id         bigint(20)      not null auto_increment    comment '明细ID',
  bookkeeping_id     bigint(20)      not null                   comment '记账ID',
  entry_type         char(1)         not null                   comment '流水类型',
  payment_amount     decimal(12,2)   not null default 0.00     comment '收付款金额',
  payment_time       datetime        not null                   comment '收付款时间',
  fund_account_id    bigint(20)      default null               comment '资金账户ID',
  fund_account_name  varchar(50)     default ''                 comment '资金账户名称',
  payment_method     varchar(32)     default null               comment '收付款方式',
  del_flag           char(1)         default '0'                comment '删除标志（0存在 2删除）',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (payment_id),
  key idx_acct_payment_bookkeeping (bookkeeping_id),
  key idx_acct_payment_time (payment_time)
) engine=innodb auto_increment=1000 comment = '记账收付款明细表';

-- ----------------------------
-- 4、字典类型与字典数据
-- ----------------------------
delete from sys_dict_data where dict_type in ('acct_entry_type', 'acct_income_category', 'acct_expense_category', 'acct_entry_status', 'acct_fund_type');
delete from sys_dict_type where dict_type in ('acct_entry_type', 'acct_income_category', 'acct_expense_category', 'acct_entry_status', 'acct_fund_type');

insert into sys_dict_type values(100, '记账流水类型', 'acct_entry_type', '0', 'admin', sysdate(), '', null, '记账流水类型');
insert into sys_dict_type values(101, '收入分类',     'acct_income_category', '0', 'admin', sysdate(), '', null, '收入分类');
insert into sys_dict_type values(102, '支出分类',     'acct_expense_category', '0', 'admin', sysdate(), '', null, '支出分类');
insert into sys_dict_type values(103, '记账状态',     'acct_entry_status', '0', 'admin', sysdate(), '', null, '记账状态');
insert into sys_dict_type values(104, '资金账户类型', 'acct_fund_type', '0', 'admin', sysdate(), '', null, '资金账户类型');

insert into sys_dict_data values(100, 1, '收入',       '1', 'acct_entry_type',        '', 'success', 'Y', '0', 'admin', sysdate(), '', null, '收入');
insert into sys_dict_data values(101, 2, '支出',       '2', 'acct_entry_type',        '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '支出');
insert into sys_dict_data values(102, 1, '维修收入',   '1', 'acct_income_category',   '', 'primary', 'Y', '0', 'admin', sysdate(), '', null, '维修收入');
insert into sys_dict_data values(103, 2, '保养收入',   '2', 'acct_income_category',   '', 'success', 'N', '0', 'admin', sysdate(), '', null, '保养收入');
insert into sys_dict_data values(104, 3, '配件销售',   '3', 'acct_income_category',   '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '配件销售');
insert into sys_dict_data values(105, 4, '其他收入',   '4', 'acct_income_category',   '', 'info',    'N', '0', 'admin', sysdate(), '', null, '其他收入');
insert into sys_dict_data values(106, 1, '配件采购',   '1', 'acct_expense_category',  '', 'warning', 'Y', '0', 'admin', sysdate(), '', null, '配件采购');
insert into sys_dict_data values(107, 2, '人工工资',   '2', 'acct_expense_category',  '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '人工工资');
insert into sys_dict_data values(108, 3, '房租水电',   '3', 'acct_expense_category',  '', 'info',    'N', '0', 'admin', sysdate(), '', null, '房租水电');
insert into sys_dict_data values(109, 4, '其他支出',   '4', 'acct_expense_category',  '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '其他支出');
insert into sys_dict_data values(110, 1, '已结清',     '1', 'acct_entry_status',      '', 'success', 'Y', '0', 'admin', sysdate(), '', null, '已结清');
insert into sys_dict_data values(111, 2, '部分收款',   '2', 'acct_entry_status',      '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '部分收款');
insert into sys_dict_data values(112, 3, '挂账中',     '3', 'acct_entry_status',      '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '挂账中');
insert into sys_dict_data values(113, 1, '现金',       '1', 'acct_fund_type',         '', 'primary', 'Y', '0', 'admin', sysdate(), '', null, '现金');
insert into sys_dict_data values(114, 2, '微信',       '2', 'acct_fund_type',         '', 'success', 'N', '0', 'admin', sysdate(), '', null, '微信');
insert into sys_dict_data values(115, 3, '支付宝',     '3', 'acct_fund_type',         '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '支付宝');
insert into sys_dict_data values(116, 4, '银行卡',     '4', 'acct_fund_type',         '', 'info',    'N', '0', 'admin', sysdate(), '', null, '银行卡');
insert into sys_dict_data values(117, 5, '其他',       '5', 'acct_fund_type',         '', '',        'N', '0', 'admin', sysdate(), '', null, '其他');

-- ----------------------------
-- 5、菜单与权限
-- ----------------------------
delete from sys_role_menu where menu_id in (2000, 2001, 2002, 2100, 2101, 2102, 2103, 2104, 2105, 2106, 2107, 2108);
delete from sys_menu where menu_id in (2000, 2001, 2002, 2100, 2101, 2102, 2103, 2104, 2105, 2106, 2107, 2108);

insert into sys_menu values('2000', '财务管理', '0', '5', 'finance', '', '', '', 1, 0, 'M', '0', '0', '', 'money', 'admin', sysdate(), '', null, '财务管理目录');
insert into sys_menu values('2001', '记账管理', '2000', '1', 'bookkeeping', 'finance/bookkeeping/index', '', '', 1, 0, 'C', '0', '0', 'finance:bookkeeping:list', 's-finance', 'admin', sysdate(), '', null, '记账管理菜单');
insert into sys_menu values('2002', '资金账户管理', '2000', '2', 'account', 'finance/account/index', '', '', 1, 0, 'C', '0', '0', 'finance:account:list', 'wallet', 'admin', sysdate(), '', null, '资金账户管理菜单');

insert into sys_menu values('2100', '记账查询',   '2001', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:bookkeeping:list',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2101', '记账新增',   '2001', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:bookkeeping:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2102', '记账修改',   '2001', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:bookkeeping:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2103', '记账删除',   '2001', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:bookkeeping:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2104', '记账导出',   '2001', '5', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:bookkeeping:export', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2105', '登记收款',   '2001', '6', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:bookkeeping:receipt','#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2106', '账户查询',   '2002', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:account:list',       '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2107', '账户新增',   '2002', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:account:add',        '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2108', '账户修改删除', '2002', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'finance:account:edit,finance:account:remove', '#', 'admin', sysdate(), '', null, '');

insert into sys_role_menu values ('2', '2000');
insert into sys_role_menu values ('2', '2001');
insert into sys_role_menu values ('2', '2002');
insert into sys_role_menu values ('2', '2100');
insert into sys_role_menu values ('2', '2101');
insert into sys_role_menu values ('2', '2102');
insert into sys_role_menu values ('2', '2103');
insert into sys_role_menu values ('2', '2104');
insert into sys_role_menu values ('2', '2105');
insert into sys_role_menu values ('2', '2106');
insert into sys_role_menu values ('2', '2107');
insert into sys_role_menu values ('2', '2108');
