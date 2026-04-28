-- ----------------------------
-- 订单管理 V1 - 统一订单模块（维修工单/商品销售单）
-- ----------------------------

-- ----------------------------
-- 1、订单主表
-- ----------------------------
drop table if exists ord_order;
create table ord_order (
  order_id           bigint(20)      not null auto_increment    comment '订单ID',
  order_no           varchar(32)     not null                   comment '订单号',
  order_type         varchar(16)     not null                   comment '订单类型',
  order_status       varchar(16)     not null                   comment '订单状态',
  order_time         datetime        not null                   comment '下单时间',
  completed_time     datetime        default null               comment '完工时间',
  customer_name      varchar(50)     default null               comment '客户姓名',
  customer_phone     varchar(20)     default null               comment '客户手机号',
  license_plate      varchar(20)     default null               comment '车牌号',
  car_model          varchar(50)     default null               comment '车型',
  service_amount     decimal(12,2)   not null default 0.00     comment '服务金额',
  part_amount        decimal(12,2)   not null default 0.00     comment '配件金额',
  total_amount       decimal(12,2)   not null default 0.00     comment '订单总金额',
  paid_amount        decimal(12,2)   not null default 0.00     comment '首笔实收',
  unpaid_amount      decimal(12,2)   not null default 0.00     comment '未收金额',
  bookkeeping_id     bigint(20)      default null               comment '关联记账ID',
  handler_name       varchar(50)     default null               comment '经办人',
  del_flag           char(1)         default '0'                comment '删除标志（0存在 2删除）',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (order_id),
  unique key uk_ord_order_no (order_no),
  key idx_ord_order_time (order_time),
  key idx_ord_order_type_status (order_type, order_status),
  key idx_ord_order_customer (customer_name),
  key idx_ord_order_plate (license_plate)
) engine=innodb auto_increment=1000 comment = '订单主表';

-- ----------------------------
-- 2、订单明细表
-- ----------------------------
drop table if exists ord_order_detail;
create table ord_order_detail (
  detail_id          bigint(20)      not null auto_increment    comment '明细ID',
  order_id           bigint(20)      not null                   comment '订单ID',
  detail_type        char(1)         not null                   comment '明细类型',
  line_no            int(11)         not null                   comment '行号',
  item_name          varchar(100)    default null               comment '项目名称',
  part_id            bigint(20)      default null               comment '配件ID',
  part_code          varchar(32)     default null               comment '配件编码',
  part_name          varchar(100)    default null               comment '配件名称',
  spec_model         varchar(100)    default null               comment '规格型号',
  part_unit          varchar(32)     default null               comment '单位',
  quantity           decimal(12,2)   not null default 0.00     comment '数量',
  unit_price         decimal(12,2)   not null default 0.00     comment '单价',
  amount             decimal(12,2)   not null default 0.00     comment '金额',
  reserved_quantity  decimal(12,2)   not null default 0.00     comment '预扣库存数量',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (detail_id),
  key idx_ord_detail_order (order_id),
  key idx_ord_detail_part (part_id)
) engine=innodb auto_increment=1000 comment = '订单明细表';

-- ----------------------------
-- 3、库存流水补充订单追溯字段
-- ----------------------------
alter table wh_stock_flow add column business_no varchar(32) default null comment '业务单号' after business_type;
alter table wh_stock_flow add key idx_wh_stock_flow_business_no (business_no);

-- ----------------------------
-- 4、字典类型与字典数据
-- ----------------------------
delete from sys_dict_data where dict_type in ('order_type', 'order_status', 'order_detail_type');
delete from sys_dict_type where dict_type in ('order_type', 'order_status', 'order_detail_type');
delete from sys_dict_data where dict_type = 'warehouse_stock_type' and dict_value in ('4', '5');

insert into sys_dict_type values(230, '订单类型', 'order_type', '0', 'admin', sysdate(), '', null, '统一订单类型');
insert into sys_dict_type values(231, '订单状态', 'order_status', '0', 'admin', sysdate(), '', null, '统一订单状态');
insert into sys_dict_type values(232, '订单明细类型', 'order_detail_type', '0', 'admin', sysdate(), '', null, '统一订单明细类型');

insert into sys_dict_data values(230, 1, '维修工单',   'repair',    'order_type',        '', 'primary', 'Y', '0', 'admin', sysdate(), '', null, '维修工单');
insert into sys_dict_data values(231, 2, '商品销售单', 'sale',      'order_type',        '', 'success', 'N', '0', 'admin', sysdate(), '', null, '商品销售单');
insert into sys_dict_data values(232, 1, '已确认',     'confirmed', 'order_status',      '', 'primary', 'Y', '0', 'admin', sysdate(), '', null, '已确认');
insert into sys_dict_data values(233, 2, '已完工',     'completed', 'order_status',      '', 'success', 'N', '0', 'admin', sysdate(), '', null, '已完工');
insert into sys_dict_data values(234, 3, '已取消',     'cancelled', 'order_status',      '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '已取消');
insert into sys_dict_data values(235, 1, '服务项',     '1',         'order_detail_type', '', 'primary', 'Y', '0', 'admin', sysdate(), '', null, '服务项');
insert into sys_dict_data values(236, 2, '配件项',     '2',         'order_detail_type', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '配件项');
insert into sys_dict_data values(237, 4, '订单预扣',   '4',         'warehouse_stock_type', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '订单预扣');
insert into sys_dict_data values(238, 5, '订单释放',   '5',         'warehouse_stock_type', '', 'info',    'N', '0', 'admin', sysdate(), '', null, '订单释放');

-- ----------------------------
-- 5、菜单与权限
-- ----------------------------
delete from sys_role_menu where menu_id in (2400, 2401, 2410, 2411, 2412, 2413, 2414, 2415);
delete from sys_menu where menu_id in (2400, 2401, 2410, 2411, 2412, 2413, 2414, 2415);

insert into sys_menu values('2400', '订单管理', '0', '7', 'order', '', '', '', 1, 0, 'M', '0', '0', '', 'shopping', 'admin', sysdate(), '', null, '统一订单目录');
insert into sys_menu values('2401', '订单列表', '2400', '1', 'index', 'order/index', '', 'orderIndex', 1, 0, 'C', '0', '0', 'order:list', 'list', 'admin', sysdate(), '', null, '订单列表菜单');

insert into sys_menu values('2410', '订单查询', '2401', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'order:list', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2411', '订单新增', '2401', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'order:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2412', '订单修改', '2401', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'order:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2413', '订单完工', '2401', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'order:complete', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2414', '订单取消', '2401', '5', '#', '', '', '', 1, 0, 'F', '0', '0', 'order:cancel', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2415', '订单明细导出', '2401', '6', '#', '', '', '', 1, 0, 'F', '0', '0', 'order:detail:export', '#', 'admin', sysdate(), '', null, '');

insert into sys_role_menu values ('1', '2400');
insert into sys_role_menu values ('1', '2401');
insert into sys_role_menu values ('1', '2410');
insert into sys_role_menu values ('1', '2411');
insert into sys_role_menu values ('1', '2412');
insert into sys_role_menu values ('1', '2413');
insert into sys_role_menu values ('1', '2414');
insert into sys_role_menu values ('1', '2415');

insert into sys_role_menu values ('2', '2400');
insert into sys_role_menu values ('2', '2401');
insert into sys_role_menu values ('2', '2410');
insert into sys_role_menu values ('2', '2411');
insert into sys_role_menu values ('2', '2412');
insert into sys_role_menu values ('2', '2413');
insert into sys_role_menu values ('2', '2414');
insert into sys_role_menu values ('2', '2415');
