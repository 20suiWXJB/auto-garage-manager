-- ----------------------------
-- 仓库管理 V1 - 汽车修理厂配件库存模块
-- ----------------------------

-- ----------------------------
-- 1、配件档案表
-- ----------------------------
drop table if exists wh_part;
create table wh_part (
  part_id           bigint(20)      not null auto_increment    comment '配件ID',
  part_code         varchar(32)     not null                   comment '配件编码',
  part_name         varchar(100)    not null                   comment '配件名称',
  category          varchar(32)     not null                   comment '配件分类',
  spec_model        varchar(100)    default null               comment '规格型号',
  part_unit         varchar(32)     not null                   comment '单位',
  purchase_price    decimal(12,2)   not null default 0.00     comment '参考进价',
  sale_price        decimal(12,2)   not null default 0.00     comment '销售单价',
  current_stock     decimal(12,2)   not null default 0.00     comment '当前库存',
  min_stock         decimal(12,2)   not null default 0.00     comment '最低库存',
  status            char(1)         default '0'                comment '状态（0正常 1停用）',
  del_flag          char(1)         default '0'                comment '删除标志（0存在 2删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (part_id),
  unique key uk_wh_part_code (part_code),
  key idx_wh_part_name (part_name),
  key idx_wh_part_category_status (category, status)
) engine=innodb auto_increment=1000 comment = '配件档案表';

-- ----------------------------
-- 2、库存流水表
-- ----------------------------
drop table if exists wh_stock_flow;
create table wh_stock_flow (
  stock_flow_id     bigint(20)      not null auto_increment    comment '流水ID',
  flow_no           varchar(32)     not null                   comment '流水单号',
  business_type     char(1)         not null                   comment '业务类型',
  part_id           bigint(20)      not null                   comment '配件ID',
  part_code         varchar(32)     not null                   comment '配件编码',
  part_name         varchar(100)    not null                   comment '配件名称',
  spec_model        varchar(100)    default null               comment '规格型号',
  part_unit         varchar(32)     default null               comment '单位',
  change_quantity   decimal(12,2)   not null default 0.00     comment '变动数量',
  before_stock      decimal(12,2)   not null default 0.00     comment '变动前库存',
  after_stock       decimal(12,2)   not null default 0.00     comment '变动后库存',
  business_time     datetime        not null                   comment '业务时间',
  handler_name      varchar(50)     default null               comment '经办人',
  adjust_direction  char(1)         default null               comment '调整方向（1增加 2减少）',
  del_flag          char(1)         default '0'                comment '删除标志（0存在 2删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '业务备注',
  primary key (stock_flow_id),
  unique key uk_wh_stock_flow_no (flow_no),
  key idx_wh_stock_flow_time (business_time),
  key idx_wh_stock_flow_type (business_type),
  key idx_wh_stock_flow_part (part_id, part_code)
) engine=innodb auto_increment=1000 comment = '库存流水表';

-- ----------------------------
-- 3、字典类型与字典数据
-- ----------------------------
delete from sys_dict_data where dict_type in ('warehouse_part_category', 'warehouse_part_unit', 'warehouse_stock_type');
delete from sys_dict_type where dict_type in ('warehouse_part_category', 'warehouse_part_unit', 'warehouse_stock_type');

insert into sys_dict_type values(200, '配件分类', 'warehouse_part_category', '0', 'admin', sysdate(), '', null, '仓库管理配件分类');
insert into sys_dict_type values(201, '配件单位', 'warehouse_part_unit', '0', 'admin', sysdate(), '', null, '仓库管理配件单位');
insert into sys_dict_type values(202, '库存业务类型', 'warehouse_stock_type', '0', 'admin', sysdate(), '', null, '仓库管理库存业务类型');

insert into sys_dict_data values(200, 1, '发动机件', '1', 'warehouse_part_category', '', 'danger',  'Y', '0', 'admin', sysdate(), '', null, '发动机件');
insert into sys_dict_data values(201, 2, '制动件',   '2', 'warehouse_part_category', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '制动件');
insert into sys_dict_data values(202, 3, '底盘件',   '3', 'warehouse_part_category', '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '底盘件');
insert into sys_dict_data values(203, 4, '易损耗材', '4', 'warehouse_part_category', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '易损耗材');

insert into sys_dict_data values(204, 1, '件', '1', 'warehouse_part_unit', '', 'primary', 'Y', '0', 'admin', sysdate(), '', null, '件');
insert into sys_dict_data values(205, 2, '个', '2', 'warehouse_part_unit', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '个');
insert into sys_dict_data values(206, 3, '套', '3', 'warehouse_part_unit', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '套');
insert into sys_dict_data values(207, 4, '桶', '4', 'warehouse_part_unit', '', 'info',    'N', '0', 'admin', sysdate(), '', null, '桶');
insert into sys_dict_data values(208, 5, '支', '5', 'warehouse_part_unit', '', '',        'N', '0', 'admin', sysdate(), '', null, '支');

insert into sys_dict_data values(209, 1, '入库', '1', 'warehouse_stock_type', '', 'success', 'Y', '0', 'admin', sysdate(), '', null, '入库');
insert into sys_dict_data values(210, 2, '出库', '2', 'warehouse_stock_type', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '出库');
insert into sys_dict_data values(211, 3, '调整', '3', 'warehouse_stock_type', '', 'danger',  'N', '0', 'admin', sysdate(), '', null, '调整');

-- ----------------------------
-- 4、菜单与权限
-- ----------------------------
delete from sys_role_menu where menu_id in (2200, 2201, 2202, 2203, 2210, 2211, 2212, 2213, 2214, 2215, 2216, 2217, 2218, 2219);
delete from sys_menu where menu_id in (2200, 2201, 2202, 2203, 2210, 2211, 2212, 2213, 2214, 2215, 2216, 2217, 2218, 2219);

insert into sys_menu values('2200', '仓库管理', '0', '6', 'warehouse', '', '', '', 1, 0, 'M', '0', '0', '', 'shopping', 'admin', sysdate(), '', null, '仓库管理目录');
insert into sys_menu values('2201', '配件档案', '2200', '1', 'part', 'warehouse/part/index', '', 'warehousePart', 1, 0, 'C', '0', '0', 'warehouse:part:list', 'clipboard', 'admin', sysdate(), '', null, '配件档案菜单');
insert into sys_menu values('2202', '库存流水', '2200', '2', 'stock', 'warehouse/stock/index', '', 'warehouseStock', 1, 0, 'C', '0', '0', 'warehouse:stock:list', 'list', 'admin', sysdate(), '', null, '库存流水菜单');
insert into sys_menu values('2203', '库存调整', '2200', '3', 'adjust', 'warehouse/stock/index', '{"businessType":"3"}', 'warehouseAdjust', 1, 0, 'C', '0', '0', 'warehouse:stock:list', 'edit', 'admin', sysdate(), '', null, '库存调整菜单');

insert into sys_menu values('2210', '配件查询', '2201', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'warehouse:part:list', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2211', '配件新增', '2201', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'warehouse:part:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2212', '配件修改', '2201', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'warehouse:part:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2213', '配件删除', '2201', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'warehouse:part:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2214', '配件导出', '2201', '5', '#', '', '', '', 1, 0, 'F', '0', '0', 'warehouse:part:export', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu values('2215', '库存查询', '2202', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'warehouse:stock:list', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2216', '库存入库', '2202', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'warehouse:stock:in', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2217', '库存出库', '2202', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'warehouse:stock:out', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2218', '库存调整', '2202', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'warehouse:stock:adjust', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2219', '库存导出', '2202', '5', '#', '', '', '', 1, 0, 'F', '0', '0', 'warehouse:stock:export', '#', 'admin', sysdate(), '', null, '');

insert into sys_role_menu values ('1', '2200');
insert into sys_role_menu values ('1', '2201');
insert into sys_role_menu values ('1', '2202');
insert into sys_role_menu values ('1', '2203');
insert into sys_role_menu values ('1', '2210');
insert into sys_role_menu values ('1', '2211');
insert into sys_role_menu values ('1', '2212');
insert into sys_role_menu values ('1', '2213');
insert into sys_role_menu values ('1', '2214');
insert into sys_role_menu values ('1', '2215');
insert into sys_role_menu values ('1', '2216');
insert into sys_role_menu values ('1', '2217');
insert into sys_role_menu values ('1', '2218');
insert into sys_role_menu values ('1', '2219');

insert into sys_role_menu values ('2', '2200');
insert into sys_role_menu values ('2', '2201');
insert into sys_role_menu values ('2', '2202');
insert into sys_role_menu values ('2', '2203');
insert into sys_role_menu values ('2', '2210');
insert into sys_role_menu values ('2', '2211');
insert into sys_role_menu values ('2', '2212');
insert into sys_role_menu values ('2', '2213');
insert into sys_role_menu values ('2', '2214');
insert into sys_role_menu values ('2', '2215');
insert into sys_role_menu values ('2', '2216');
insert into sys_role_menu values ('2', '2217');
insert into sys_role_menu values ('2', '2218');
insert into sys_role_menu values ('2', '2219');
