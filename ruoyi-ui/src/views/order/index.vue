<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="订单号" prop="orderNo">
        <el-input
          v-model="queryParams.orderNo"
          placeholder="请输入订单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="订单类型" prop="orderType">
        <el-select v-model="queryParams.orderType" placeholder="请选择订单类型" clearable>
          <el-option
            v-for="dict in dict.type.order_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="订单状态" prop="orderStatus">
        <el-select v-model="queryParams.orderStatus" placeholder="请选择订单状态" clearable>
          <el-option
            v-for="dict in dict.type.order_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="客户姓名" prop="customerName">
        <el-input
          v-model="queryParams.customerName"
          placeholder="请输入客户姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="车牌号" prop="licensePlate">
        <el-input
          v-model="queryParams.licensePlate"
          placeholder="请输入车牌号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="下单时间">
        <el-date-picker
          v-model="dateRange"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['order:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate()"
          v-hasPermi="['order:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-circle-check"
          size="mini"
          :disabled="single"
          @click="handleComplete()"
          v-hasPermi="['order:complete']"
        >完工</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-close"
          size="mini"
          :disabled="single"
          @click="handleCancel()"
          v-hasPermi="['order:cancel']"
        >取消</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['order:detail:export']"
        >明细导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="订单号" align="center" prop="orderNo" width="170" />
      <el-table-column label="订单类型" align="center" prop="orderType" width="110">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.order_type" :value="scope.row.orderType" />
        </template>
      </el-table-column>
      <el-table-column label="订单状态" align="center" prop="orderStatus" width="110">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.order_status" :value="scope.row.orderStatus" />
        </template>
      </el-table-column>
      <el-table-column label="客户姓名" align="center" prop="customerName" width="110" />
      <el-table-column label="车牌号" align="center" prop="licensePlate" width="110" />
      <el-table-column label="下单时间" align="center" prop="orderTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.orderTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="完工时间" align="center" prop="completedTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.completedTime) || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="服务金额" align="center" prop="serviceAmount" width="100">
        <template slot-scope="scope">
          <span>{{ formatAmount(scope.row.serviceAmount) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="配件金额" align="center" prop="partAmount" width="100">
        <template slot-scope="scope">
          <span>{{ formatAmount(scope.row.partAmount) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="总金额" align="center" prop="totalAmount" width="100">
        <template slot-scope="scope">
          <span>{{ formatAmount(scope.row.totalAmount) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="首笔实收" align="center" prop="paidAmount" width="100">
        <template slot-scope="scope">
          <span>{{ formatAmount(scope.row.paidAmount) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="未收金额" align="center" prop="unpaidAmount" width="100">
        <template slot-scope="scope">
          <span>{{ formatAmount(scope.row.unpaidAmount) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="经办人" align="center" prop="handlerName" width="100" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="300">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            v-hasPermi="['order:list']"
          >详情</el-button>
          <el-button
            v-if="scope.row.orderStatus === 'confirmed'"
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['order:edit']"
          >修改</el-button>
          <el-button
            v-if="scope.row.orderStatus === 'confirmed'"
            size="mini"
            type="text"
            icon="el-icon-circle-check"
            @click="handleComplete(scope.row)"
            v-hasPermi="['order:complete']"
          >完工</el-button>
          <el-button
            v-if="scope.row.orderStatus === 'confirmed'"
            size="mini"
            type="text"
            icon="el-icon-close"
            @click="handleCancel(scope.row)"
            v-hasPermi="['order:cancel']"
          >取消</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="title" :visible.sync="open" width="1100px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="92px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="订单类型" prop="orderType">
              <el-radio-group v-model="form.orderType" :disabled="!!form.orderId">
                <el-radio
                  v-for="dict in dict.type.order_type"
                  :key="dict.value"
                  :label="dict.value"
                >{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="下单时间" prop="orderTime">
              <el-date-picker
                v-model="form.orderTime"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="请选择下单时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="客户姓名" prop="customerName">
              <el-input v-model="form.customerName" placeholder="请输入客户姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户手机号" prop="customerPhone">
              <el-input v-model="form.customerPhone" placeholder="请输入客户手机号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="车牌号" prop="licensePlate">
              <el-input v-model="form.licensePlate" placeholder="请输入车牌号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="车型" prop="carModel">
              <el-input v-model="form.carModel" placeholder="请输入车型" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="经办人" prop="handlerName">
              <el-input v-model="form.handlerName" placeholder="请输入经办人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>

        <div class="detail-toolbar">
          <div class="detail-title">订单明细</div>
          <div>
            <el-button size="mini" type="primary" plain icon="el-icon-plus" @click="addServiceDetail">新增服务项</el-button>
            <el-button size="mini" type="warning" plain icon="el-icon-plus" @click="addPartDetail">新增配件项</el-button>
          </div>
        </div>

        <el-table :data="form.detailList" border size="mini" class="detail-table">
          <el-table-column label="#" type="index" width="50" align="center" />
          <el-table-column label="明细类型" width="100" align="center">
            <template slot-scope="scope">
              <el-select v-model="scope.row.detailType" size="mini" @change="handleDetailTypeChange(scope.row)">
                <el-option
                  v-for="dict in dict.type.order_detail_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="项目/配件" min-width="240">
            <template slot-scope="scope">
              <el-select
                v-if="scope.row.detailType === '2'"
                v-model="scope.row.partId"
                filterable
                clearable
                size="mini"
                placeholder="请选择配件"
                style="width: 100%"
                @change="handlePartChange(scope.row)"
              >
                <el-option
                  v-for="part in partOptions"
                  :key="part.partId"
                  :label="partOptionLabel(part)"
                  :value="part.partId"
                />
              </el-select>
              <el-input
                v-else
                v-model="scope.row.itemName"
                size="mini"
                placeholder="请输入服务项目名称"
              />
            </template>
          </el-table-column>
          <el-table-column label="规格型号" prop="specModel" min-width="120" />
          <el-table-column label="单位" min-width="90">
            <template slot-scope="scope">
              <span>{{ unitLabel(scope.row.unit) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="数量" width="120" align="center">
            <template slot-scope="scope">
              <el-input-number v-model="scope.row.quantity" :min="0.01" :step="0.01" size="mini" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="单价" width="120" align="center">
            <template slot-scope="scope">
              <el-input-number v-model="scope.row.unitPrice" :min="0" :step="0.01" size="mini" style="width: 100%" />
            </template>
          </el-table-column>
          <el-table-column label="金额" width="110" align="center">
            <template slot-scope="scope">
              <span>{{ formatAmount(calcLineAmount(scope.row)) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="备注" min-width="160">
            <template slot-scope="scope">
              <el-input v-model="scope.row.remark" size="mini" placeholder="请输入备注" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center">
            <template slot-scope="scope">
              <el-button size="mini" type="text" icon="el-icon-delete" @click="removeDetail(scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="amount-summary">
          <span>服务金额：{{ formatAmount(serviceAmount) }}</span>
          <span>配件金额：{{ formatAmount(partAmount) }}</span>
          <span>订单总金额：{{ formatAmount(totalAmount) }}</span>
        </div>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancelForm">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="订单完工" :visible.sync="completeOpen" width="560px" append-to-body>
      <el-form ref="completeForm" :model="completeForm" :rules="completeRules" label-width="96px">
        <el-form-item label="订单号">
          <el-input :value="currentRow.orderNo" disabled />
        </el-form-item>
        <el-form-item label="订单总金额">
          <el-input :value="formatAmount(currentRow.totalAmount)" disabled />
        </el-form-item>
        <el-form-item label="完工时间" prop="completedTime">
          <el-date-picker
            v-model="completeForm.completedTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择完工时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="首笔实收" prop="paidAmount">
          <el-input-number v-model="completeForm.paidAmount" :min="0" :step="0.01" style="width: 100%" />
        </el-form-item>
        <el-form-item label="资金账户" prop="fundAccountId">
          <el-select v-model="completeForm.fundAccountId" clearable filterable placeholder="请选择资金账户" style="width: 100%">
            <el-option
              v-for="account in accountOptions"
              :key="account.fundAccountId"
              :label="account.accountName"
              :value="account.fundAccountId"
              :disabled="account.status !== '0'"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="支付方式" prop="paymentMethod">
          <el-input v-model="completeForm.paymentMethod" placeholder="如：现金、转账、扫码" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitComplete">确 定</el-button>
        <el-button @click="cancelCompleteDialog">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="订单详情" :visible.sync="detailOpen" width="1000px" append-to-body>
      <el-row :gutter="16" class="detail-row">
        <el-col :span="8"><span class="detail-label">订单号：</span>{{ detailForm.orderNo || '-' }}</el-col>
        <el-col :span="8"><span class="detail-label">订单类型：</span>{{ typeLabel(detailForm.orderType) }}</el-col>
        <el-col :span="8"><span class="detail-label">订单状态：</span>{{ statusLabel(detailForm.orderStatus) }}</el-col>
      </el-row>
      <el-row :gutter="16" class="detail-row">
        <el-col :span="8"><span class="detail-label">客户姓名：</span>{{ detailForm.customerName || '-' }}</el-col>
        <el-col :span="8"><span class="detail-label">客户手机号：</span>{{ detailForm.customerPhone || '-' }}</el-col>
        <el-col :span="8"><span class="detail-label">经办人：</span>{{ detailForm.handlerName || '-' }}</el-col>
      </el-row>
      <el-row :gutter="16" class="detail-row">
        <el-col :span="8"><span class="detail-label">车牌号：</span>{{ detailForm.licensePlate || '-' }}</el-col>
        <el-col :span="8"><span class="detail-label">车型：</span>{{ detailForm.carModel || '-' }}</el-col>
        <el-col :span="8"><span class="detail-label">关联财务单：</span>{{ detailForm.bookkeepingEntryNo || '-' }}</el-col>
      </el-row>
      <el-row :gutter="16" class="detail-row">
        <el-col :span="8"><span class="detail-label">下单时间：</span>{{ parseTime(detailForm.orderTime) || '-' }}</el-col>
        <el-col :span="8"><span class="detail-label">完工时间：</span>{{ parseTime(detailForm.completedTime) || '-' }}</el-col>
        <el-col :span="8"><span class="detail-label">订单总金额：</span>{{ formatAmount(detailForm.totalAmount) }}</el-col>
      </el-row>
      <el-row class="detail-row">
        <el-col :span="24"><span class="detail-label">备注：</span>{{ detailForm.remark || '-' }}</el-col>
      </el-row>

      <el-table :data="detailForm.detailList || []" size="mini" border>
        <el-table-column label="明细类型" width="100" align="center">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.order_detail_type" :value="scope.row.detailType" />
          </template>
        </el-table-column>
        <el-table-column label="项目/配件编码" prop="partCode" min-width="130" />
        <el-table-column label="项目/配件名称" min-width="180">
          <template slot-scope="scope">
            <span>{{ scope.row.detailType === '1' ? scope.row.itemName : scope.row.partName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="规格型号" prop="specModel" min-width="120" />
        <el-table-column label="单位" min-width="90">
          <template slot-scope="scope">
            <span>{{ unitLabel(scope.row.unit) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="数量" align="center" width="100">
          <template slot-scope="scope">
            <span>{{ formatAmount(scope.row.quantity) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="单价" align="center" width="100">
          <template slot-scope="scope">
            <span>{{ formatAmount(scope.row.unitPrice) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="金额" align="center" width="100">
          <template slot-scope="scope">
            <span>{{ formatAmount(scope.row.amount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" min-width="160" :show-overflow-tooltip="true" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { listOrder, getOrder, addOrder, updateOrder, completeOrder, cancelOrder } from '@/api/order/order'
import { listPartOptions } from '@/api/warehouse/part'
import { listAccountOptions } from '@/api/finance/account'

export default {
  name: 'OrderIndex',
  dicts: ['order_type', 'order_status', 'order_detail_type', 'warehouse_part_unit'],
  data() {
    return {
      loading: true,
      ids: [],
      selectedRows: [],
      single: true,
      showSearch: true,
      total: 0,
      orderList: [],
      dateRange: [],
      partOptions: [],
      accountOptions: [],
      title: '',
      open: false,
      detailOpen: false,
      completeOpen: false,
      currentRow: {},
      detailForm: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderNo: undefined,
        orderType: undefined,
        orderStatus: undefined,
        customerName: undefined,
        licensePlate: undefined
      },
      form: {
        detailList: []
      },
      completeForm: {
        completedTime: undefined,
        paidAmount: 0,
        fundAccountId: undefined,
        paymentMethod: undefined
      },
      rules: {
        orderType: [
          { required: true, message: '订单类型不能为空', trigger: 'change' }
        ],
        orderTime: [
          { required: true, message: '下单时间不能为空', trigger: 'change' }
        ]
      },
      completeRules: {
        completedTime: [
          { required: true, message: '完工时间不能为空', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    serviceAmount() {
      return this.sumAmount('1')
    },
    partAmount() {
      return this.sumAmount('2')
    },
    totalAmount() {
      return Number(this.serviceAmount) + Number(this.partAmount)
    }
  },
  created() {
    this.getList()
    this.loadPartOptions()
    this.loadAccountOptions()
  },
  methods: {
    getList() {
      this.loading = true
      const query = this.addDateRange({ ...this.queryParams }, this.dateRange, 'OrderTime')
      listOrder(query).then(response => {
        this.orderList = response.rows
        this.total = response.total
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    loadPartOptions() {
      listPartOptions().then(response => {
        this.partOptions = response.data || []
      })
    },
    loadAccountOptions() {
      listAccountOptions().then(response => {
        this.accountOptions = response.data || []
      })
    },
    formatAmount(value) {
      return Number(value || 0).toFixed(2)
    },
    typeLabel(value) {
      return this.selectDictLabel(this.dict.type.order_type, value) || '-'
    },
    statusLabel(value) {
      return this.selectDictLabel(this.dict.type.order_status, value) || '-'
    },
    unitLabel(value) {
      return this.selectDictLabel(this.dict.type.warehouse_part_unit, value) || value || '-'
    },
    partOptionLabel(part) {
      return `${part.partCode} / ${part.partName}`
    },
    sumAmount(detailType) {
      return (this.form.detailList || []).reduce((sum, item) => {
        if (item.detailType !== detailType) {
          return sum
        }
        return sum + Number(this.calcLineAmount(item))
      }, 0).toFixed(2)
    },
    calcLineAmount(row) {
      return Number(row.quantity || 0) * Number(row.unitPrice || 0)
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.dateRange = []
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.selectedRows = selection
      this.ids = selection.map(item => item.orderId)
      this.single = selection.length !== 1
    },
    reset() {
      this.form = {
        orderId: undefined,
        orderType: 'repair',
        orderTime: this.parseTime(new Date()),
        customerName: undefined,
        customerPhone: undefined,
        licensePlate: undefined,
        carModel: undefined,
        handlerName: undefined,
        remark: undefined,
        detailList: []
      }
      this.resetForm('form')
      this.addServiceDetail()
    },
    resetComplete() {
      this.completeForm = {
        completedTime: this.parseTime(new Date()),
        paidAmount: 0,
        fundAccountId: undefined,
        paymentMethod: undefined
      }
      this.resetForm('completeForm')
    },
    cancelForm() {
      this.open = false
      this.reset()
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增订单'
    },
    handleView(row) {
      getOrder(row.orderId).then(response => {
        this.detailForm = response.data || {}
        this.detailOpen = true
      })
    },
    handleUpdate(row) {
      const target = row || this.selectedRows[0]
      if (!target) {
        return
      }
      if (target.orderStatus !== 'confirmed') {
        this.$modal.msgError('只有已确认订单允许修改')
        return
      }
      getOrder(target.orderId).then(response => {
        this.form = this.normalizeForm(response.data)
        this.open = true
        this.title = '修改订单'
      })
    },
    handleComplete(row) {
      const target = row || this.selectedRows[0]
      if (!target) {
        return
      }
      if (target.orderStatus !== 'confirmed') {
        this.$modal.msgError('只有已确认订单允许完工')
        return
      }
      this.currentRow = target
      this.resetComplete()
      this.completeOpen = true
    },
    handleCancel(row) {
      const target = row || this.selectedRows[0]
      if (!target) {
        return
      }
      if (target.orderStatus !== 'confirmed') {
        this.$modal.msgError('只有已确认订单允许取消')
        return
      }
      this.$modal.confirm(`是否确认取消订单"${target.orderNo}"？`).then(() => {
        return cancelOrder(target.orderId)
      }).then(() => {
        this.$modal.msgSuccess('取消成功')
        this.getList()
      }).catch(() => {})
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid || !this.validateDetails()) {
          return
        }
        const data = this.buildSubmitData()
        const request = data.orderId ? updateOrder : addOrder
        request(data).then(() => {
          this.$modal.msgSuccess(data.orderId ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    submitComplete() {
      this.$refs.completeForm.validate(valid => {
        if (!valid) {
          return
        }
        if (Number(this.completeForm.paidAmount || 0) > Number(this.currentRow.totalAmount || 0)) {
          this.$modal.msgError('首笔实收不能超过订单总金额')
          return
        }
        if (Number(this.completeForm.paidAmount || 0) > 0 && !this.completeForm.fundAccountId) {
          this.$modal.msgError('录入首笔实收时必须选择资金账户')
          return
        }
        completeOrder(this.currentRow.orderId, this.completeForm).then(() => {
          this.$modal.msgSuccess('订单完工成功')
          this.completeOpen = false
          this.getList()
        })
      })
    },
    cancelCompleteDialog() {
      this.completeOpen = false
      this.resetComplete()
    },
    handleExport() {
      const query = this.addDateRange({ ...this.queryParams }, this.dateRange, 'OrderTime')
      this.download('order/detail/export', query, `order_detail_${new Date().getTime()}.xlsx`)
    },
    addServiceDetail() {
      this.form.detailList.push({
        detailType: '1',
        itemName: undefined,
        partId: undefined,
        partCode: undefined,
        partName: undefined,
        specModel: undefined,
        unit: undefined,
        quantity: 1,
        unitPrice: 0,
        remark: undefined
      })
    },
    addPartDetail() {
      this.form.detailList.push({
        detailType: '2',
        itemName: undefined,
        partId: undefined,
        partCode: undefined,
        partName: undefined,
        specModel: undefined,
        unit: undefined,
        quantity: 1,
        unitPrice: 0,
        remark: undefined
      })
    },
    removeDetail(index) {
      this.form.detailList.splice(index, 1)
      if (!this.form.detailList.length) {
        this.addServiceDetail()
      }
    },
    handleDetailTypeChange(row) {
      row.itemName = undefined
      row.partId = undefined
      row.partCode = undefined
      row.partName = undefined
      row.specModel = undefined
      row.unit = undefined
      row.quantity = 1
      row.unitPrice = 0
    },
    handlePartChange(row) {
      const part = this.partOptions.find(item => item.partId === row.partId)
      if (!part) {
        row.partCode = undefined
        row.partName = undefined
        row.specModel = undefined
        row.unit = undefined
        row.unitPrice = 0
        return
      }
      row.partCode = part.partCode
      row.partName = part.partName
      row.specModel = part.specModel
      row.unit = part.unit
      row.unitPrice = Number(part.salePrice || 0)
    },
    validateDetails() {
      if (!this.form.detailList.length) {
        this.$modal.msgError('订单明细不能为空')
        return false
      }
      for (const item of this.form.detailList) {
        if (item.detailType === '1') {
          if (!item.itemName) {
            this.$modal.msgError('服务项目名称不能为空')
            return false
          }
        } else if (item.detailType === '2') {
          if (!item.partId) {
            this.$modal.msgError('配件明细必须选择配件')
            return false
          }
        } else {
          this.$modal.msgError('订单明细类型不正确')
          return false
        }
        if (Number(item.quantity || 0) <= 0) {
          this.$modal.msgError('订单明细数量必须大于0')
          return false
        }
        if (Number(item.unitPrice || 0) < 0) {
          this.$modal.msgError('订单明细单价不能小于0')
          return false
        }
      }
      return true
    },
    buildSubmitData() {
      return {
        ...this.form,
        detailList: this.form.detailList.map((item, index) => ({
          detailType: item.detailType,
          lineNo: index + 1,
          itemName: item.detailType === '1' ? item.itemName : item.partName,
          partId: item.detailType === '2' ? item.partId : undefined,
          partCode: item.detailType === '2' ? item.partCode : undefined,
          partName: item.detailType === '2' ? item.partName : undefined,
          specModel: item.detailType === '2' ? item.specModel : undefined,
          unit: item.detailType === '2' ? item.unit : undefined,
          quantity: Number(item.quantity || 0),
          unitPrice: Number(item.unitPrice || 0),
          amount: Number(this.calcLineAmount(item)),
          reservedQuantity: item.detailType === '2' ? Number(item.quantity || 0) : 0,
          remark: item.remark
        }))
      }
    },
    normalizeForm(data) {
      const form = {
        ...data,
        detailList: (data.detailList || []).map(item => ({
          detailType: item.detailType,
          itemName: item.detailType === '1' ? item.itemName : item.partName,
          partId: item.partId,
          partCode: item.partCode,
          partName: item.partName,
          specModel: item.specModel,
          unit: item.unit,
          quantity: Number(item.quantity || 0),
          unitPrice: Number(item.unitPrice || 0),
          remark: item.remark
        }))
      }
      if (!form.detailList.length) {
        form.detailList = []
        this.form = form
        this.addServiceDetail()
        return this.form
      }
      return form
    }
  }
}
</script>

<style scoped>
.detail-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 8px 0 12px;
}

.detail-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.detail-table {
  margin-bottom: 12px;
}

.amount-summary {
  display: flex;
  justify-content: flex-end;
  gap: 24px;
  font-size: 14px;
  color: #303133;
}

.detail-row {
  margin-bottom: 12px;
  line-height: 24px;
}

.detail-label {
  color: #606266;
}

@media (max-width: 768px) {
  .amount-summary {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>
