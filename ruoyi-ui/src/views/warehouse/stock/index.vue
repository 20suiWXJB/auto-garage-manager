<template>
  <div class="app-container">
    <el-alert
      v-if="routeBusinessType"
      :title="routeBusinessType === '3' ? '当前入口默认查看库存调整流水' : '当前入口已带入库存业务类型筛选'"
      type="info"
      :closable="false"
      class="mb12"
    />

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="业务日期">
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
      <el-form-item label="业务类型" prop="businessType">
        <el-select v-model="queryParams.businessType" placeholder="请选择业务类型" clearable>
          <el-option
            v-for="dict in dict.type.warehouse_stock_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="流水单号" prop="flowNo">
        <el-input
          v-model="queryParams.flowNo"
          placeholder="请输入流水单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="配件编码" prop="partCode">
        <el-input
          v-model="queryParams.partCode"
          placeholder="请输入配件编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="配件名称" prop="partName">
        <el-input
          v-model="queryParams.partName"
          placeholder="请输入配件名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="经办人" prop="handlerName">
        <el-input
          v-model="queryParams.handlerName"
          placeholder="请输入经办人"
          clearable
          @keyup.enter.native="handleQuery"
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
          icon="el-icon-bottom"
          size="mini"
          @click="handleOpenDialog('in')"
          v-hasPermi="['warehouse:stock:in']"
        >入库</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-top"
          size="mini"
          @click="handleOpenDialog('out')"
          v-hasPermi="['warehouse:stock:out']"
        >出库</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-refresh"
          size="mini"
          @click="handleOpenDialog('adjust')"
          v-hasPermi="['warehouse:stock:adjust']"
        >库存调整</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['warehouse:stock:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="stockList">
      <el-table-column label="单号" align="center" prop="flowNo" width="200" />
      <el-table-column label="业务类型" align="center" prop="businessType" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.warehouse_stock_type" :value="scope.row.businessType" />
        </template>
      </el-table-column>
      <el-table-column label="配件编码" align="center" prop="partCode" width="130" />
      <el-table-column label="配件名称" align="center" prop="partName" min-width="140" />
      <el-table-column label="变动数量" align="center" prop="changeQuantity" width="110">
        <template slot-scope="scope">
          <span :class="quantityClass(scope.row.changeQuantity)">{{ formatSignedAmount(scope.row.changeQuantity) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="变动前库存" align="center" prop="beforeStock" width="110">
        <template slot-scope="scope">
          <span>{{ formatAmount(scope.row.beforeStock) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="变动后库存" align="center" prop="afterStock" width="110">
        <template slot-scope="scope">
          <span>{{ formatAmount(scope.row.afterStock) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="业务时间" align="center" prop="businessTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.businessTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="经办人" align="center" prop="handlerName" width="110" />
      <el-table-column label="备注" align="center" prop="remark" min-width="180" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="100">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            v-hasPermi="['warehouse:stock:list']"
          >详情</el-button>
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

    <el-dialog :title="dialogTitle" :visible.sync="open" width="620px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="92px">
        <el-form-item label="仓库">
          <el-input value="总仓" disabled />
        </el-form-item>
        <el-form-item label="选择配件" prop="partId">
          <el-select
            v-model="form.partId"
            placeholder="请选择配件"
            style="width: 100%"
            filterable
            @change="handlePartChange"
          >
            <el-option
              v-for="part in partOptions"
              :key="part.partId"
              :label="partOptionLabel(part)"
              :value="part.partId"
            />
          </el-select>
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="当前库存">
              <el-input :value="formatAmount(currentStock)" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="调整后库存">
              <el-input :value="formatAmount(afterStockPreview)" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item v-if="formMode === 'adjust'" label="调整方向" prop="adjustDirection">
          <el-radio-group v-model="form.adjustDirection">
            <el-radio label="1">增加</el-radio>
            <el-radio label="2">减少</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item :label="quantityLabel" prop="quantity">
              <el-input-number v-model="form.quantity" :min="0.01" :step="0.01" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="业务时间" prop="businessTime">
              <el-date-picker
                v-model="form.businessTime"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="请选择业务时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="经办人" prop="handlerName">
          <el-input v-model="form.handlerName" placeholder="请输入经办人" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="库存流水详情" :visible.sync="detailOpen" width="760px" append-to-body>
      <el-row :gutter="16" class="detail-row">
        <el-col :span="12"><span class="detail-label">单号：</span>{{ detailForm.flowNo || '-' }}</el-col>
        <el-col :span="12"><span class="detail-label">业务类型：</span>{{ typeLabel(detailForm.businessType) }}</el-col>
      </el-row>
      <el-row :gutter="16" class="detail-row">
        <el-col :span="12"><span class="detail-label">配件编码：</span>{{ detailForm.partCode || '-' }}</el-col>
        <el-col :span="12"><span class="detail-label">配件名称：</span>{{ detailForm.partName || '-' }}</el-col>
      </el-row>
      <el-row :gutter="16" class="detail-row">
        <el-col :span="12"><span class="detail-label">规格型号：</span>{{ detailForm.specModel || '-' }}</el-col>
        <el-col :span="12"><span class="detail-label">单位：</span>{{ unitLabel(detailForm.unit) }}</el-col>
      </el-row>
      <el-row :gutter="16" class="detail-row">
        <el-col :span="8"><span class="detail-label">变动数量：</span>{{ formatSignedAmount(detailForm.changeQuantity) }}</el-col>
        <el-col :span="8"><span class="detail-label">变动前库存：</span>{{ formatAmount(detailForm.beforeStock) }}</el-col>
        <el-col :span="8"><span class="detail-label">变动后库存：</span>{{ formatAmount(detailForm.afterStock) }}</el-col>
      </el-row>
      <el-row :gutter="16" class="detail-row">
        <el-col :span="12"><span class="detail-label">业务时间：</span>{{ parseTime(detailForm.businessTime) || '-' }}</el-col>
        <el-col :span="12"><span class="detail-label">经办人：</span>{{ detailForm.handlerName || '-' }}</el-col>
      </el-row>
      <el-row v-if="detailForm.businessType === '3'" class="detail-row">
        <el-col :span="24"><span class="detail-label">调整方向：</span>{{ adjustDirectionLabel(detailForm.adjustDirection) }}</el-col>
      </el-row>
      <el-row class="detail-row">
        <el-col :span="24"><span class="detail-label">备注：</span>{{ detailForm.remark || '-' }}</el-col>
      </el-row>
    </el-dialog>
  </div>
</template>

<script>
import { listPartOptions } from '@/api/warehouse/part'
import { listStock, getStock, stockIn, stockOut, stockAdjust } from '@/api/warehouse/stock'

export default {
  name: 'WarehouseStock',
  dicts: ['warehouse_stock_type', 'warehouse_part_unit'],
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      stockList: [],
      partOptions: [],
      dateRange: [],
      routeBusinessType: undefined,
      open: false,
      detailOpen: false,
      formMode: 'in',
      form: {},
      detailForm: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        businessType: undefined,
        flowNo: undefined,
        partCode: undefined,
        partName: undefined,
        handlerName: undefined
      },
      rules: {
        partId: [
          { required: true, message: '请选择配件', trigger: 'change' }
        ],
        quantity: [
          { required: true, message: '数量不能为空', trigger: 'blur' }
        ],
        businessTime: [
          { required: true, message: '业务时间不能为空', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    currentPart() {
      return this.partOptions.find(item => item.partId === this.form.partId) || {}
    },
    currentStock() {
      return Number(this.currentPart.currentStock || 0)
    },
    afterStockPreview() {
      const quantity = Number(this.form.quantity || 0)
      if (this.formMode === 'in') {
        return this.currentStock + quantity
      }
      if (this.formMode === 'out') {
        return Math.max(this.currentStock - quantity, 0)
      }
      return this.form.adjustDirection === '2'
        ? Math.max(this.currentStock - quantity, 0)
        : this.currentStock + quantity
    },
    dialogTitle() {
      if (this.formMode === 'out') {
        return '出库登记'
      }
      if (this.formMode === 'adjust') {
        return '库存调整'
      }
      return '入库登记'
    },
    quantityLabel() {
      if (this.formMode === 'out') {
        return '出库数量'
      }
      if (this.formMode === 'adjust') {
        return '调整数量'
      }
      return '入库数量'
    }
  },
  watch: {
    '$route.query.businessType': {
      immediate: true,
      handler(value) {
        this.routeBusinessType = value
        this.queryParams.businessType = value || undefined
      }
    }
  },
  created() {
    this.getList()
    this.loadPartOptions()
  },
  methods: {
    getList() {
      this.loading = true
      const query = this.addDateRange({ ...this.queryParams }, this.dateRange, 'BusinessTime')
      listStock(query).then(response => {
        this.stockList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    loadPartOptions() {
      listPartOptions().then(response => {
        this.partOptions = response.data || []
      })
    },
    formatAmount(value) {
      return Number(value || 0).toFixed(2)
    },
    formatSignedAmount(value) {
      const amount = Number(value || 0)
      const prefix = amount > 0 ? '+' : ''
      return prefix + amount.toFixed(2)
    },
    quantityClass(value) {
      return Number(value || 0) >= 0 ? 'quantity-increase' : 'quantity-decrease'
    },
    typeLabel(value) {
      return this.selectDictLabel(this.dict.type.warehouse_stock_type, value) || '-'
    },
    unitLabel(value) {
      return this.selectDictLabel(this.dict.type.warehouse_part_unit, value) || '-'
    },
    adjustDirectionLabel(value) {
      return value === '2' ? '减少' : '增加'
    },
    partOptionLabel(part) {
      return `${part.partCode} / ${part.partName} / 库存${this.formatAmount(part.currentStock)}`
    },
    reset() {
      this.form = {
        partId: undefined,
        quantity: 1,
        businessTime: this.parseTime(new Date()),
        handlerName: undefined,
        adjustDirection: '1',
        remark: undefined
      }
      this.resetForm('form')
    },
    cancel() {
      this.open = false
      this.reset()
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      const routeType = this.routeBusinessType
      this.dateRange = []
      this.resetForm('queryForm')
      this.queryParams.businessType = routeType
      this.handleQuery()
    },
    handleOpenDialog(mode) {
      this.reset()
      this.formMode = mode
      this.open = true
      this.loadPartOptions()
    },
    handlePartChange() {
      if (this.formMode === 'adjust' && !this.form.adjustDirection) {
        this.form.adjustDirection = '1'
      }
    },
    handleView(row) {
      getStock(row.stockFlowId).then(response => {
        this.detailForm = response.data || {}
        this.detailOpen = true
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        if (this.formMode === 'adjust' && !this.form.adjustDirection) {
          this.$modal.msgError('请选择调整方向')
          return
        }
        const payload = {
          partId: this.form.partId,
          quantity: this.form.quantity,
          businessTime: this.form.businessTime,
          handlerName: this.form.handlerName,
          adjustDirection: this.formMode === 'adjust' ? this.form.adjustDirection : undefined,
          remark: this.form.remark
        }
        const request = this.formMode === 'out'
          ? stockOut
          : this.formMode === 'adjust'
            ? stockAdjust
            : stockIn
        request(payload).then(() => {
          this.$modal.msgSuccess('提交成功')
          this.open = false
          this.getList()
          this.loadPartOptions()
        })
      })
    },
    handleExport() {
      const query = this.addDateRange({ ...this.queryParams }, this.dateRange, 'BusinessTime')
      this.download('warehouse/stock/export', query, `warehouse_stock_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>

<style scoped>
.mb12 {
  margin-bottom: 12px;
}

.detail-row {
  margin-bottom: 12px;
  line-height: 24px;
}

.detail-label {
  color: #606266;
}

.quantity-increase {
  color: #67c23a;
  font-weight: 600;
}

.quantity-decrease {
  color: #f56c6c;
  font-weight: 600;
}
</style>
