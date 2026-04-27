<template>
  <div class="app-container">
    <div class="stats-panel">
      <div class="stats-card" v-for="card in statCards" :key="card.label">
        <div class="stats-label">{{ card.label }}</div>
        <div class="stats-value">{{ formatAmount(card.value) }}</div>
      </div>
    </div>

    <el-row :gutter="16" class="summary-row">
      <el-col :xs="24" :sm="24" :md="12">
        <el-card shadow="never" class="summary-card">
          <div slot="header" class="summary-title">收入分类汇总</div>
          <el-table :data="stats.incomeCategoryStats || []" size="mini">
            <el-table-column label="分类" prop="categoryName" />
            <el-table-column label="金额" align="right">
              <template slot-scope="scope">
                <span>{{ formatAmount(scope.row.totalAmount) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12">
        <el-card shadow="never" class="summary-card">
          <div slot="header" class="summary-title">支出分类汇总</div>
          <el-table :data="stats.expenseCategoryStats || []" size="mini">
            <el-table-column label="分类" prop="categoryName" />
            <el-table-column label="金额" align="right">
              <template slot-scope="scope">
                <span>{{ formatAmount(scope.row.totalAmount) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

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
      <el-form-item label="流水类型" prop="entryType">
        <el-select v-model="queryParams.entryType" placeholder="请选择类型" clearable @change="handleQueryTypeChange">
          <el-option
            v-for="dict in dict.type.acct_entry_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="分类" prop="category">
        <el-select v-model="queryParams.category" placeholder="请先选择类型" clearable :disabled="!queryParams.entryType">
          <el-option
            v-for="dict in queryCategoryOptions"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in dict.type.acct_entry_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="账户" prop="fundAccountId">
        <el-select v-model="queryParams.fundAccountId" placeholder="请选择账户" clearable filterable>
          <el-option
            v-for="account in accountOptions"
            :key="account.fundAccountId"
            :label="account.accountName"
            :value="account.fundAccountId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="客户" prop="customerName">
        <el-input
          v-model="queryParams.customerName"
          placeholder="请输入客户姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="车牌" prop="licensePlate">
        <el-input
          v-model="queryParams.licensePlate"
          placeholder="请输入车牌号"
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
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['finance:bookkeeping:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['finance:bookkeeping:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['finance:bookkeeping:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['finance:bookkeeping:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="bookkeepingList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="单号" align="center" prop="entryNo" width="170" />
      <el-table-column label="业务日期" align="center" prop="businessDate" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.businessDate) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="类型" align="center" prop="entryType" width="90">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.acct_entry_type" :value="scope.row.entryType" />
        </template>
      </el-table-column>
      <el-table-column label="分类" align="center" prop="categoryName" min-width="110" />
      <el-table-column label="总金额" align="center" prop="totalAmount" width="100">
        <template slot-scope="scope">
          <span>{{ formatAmount(scope.row.totalAmount) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="已收" align="center" prop="paidAmount" width="100">
        <template slot-scope="scope">
          <span>{{ formatAmount(scope.row.paidAmount) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="未收" align="center" prop="unpaidAmount" width="100">
        <template slot-scope="scope">
          <span>{{ formatAmount(scope.row.unpaidAmount) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="资金账户" align="center" prop="fundAccountName" min-width="110" />
      <el-table-column label="客户" align="center" prop="customerName" width="100" />
      <el-table-column label="车牌号" align="center" prop="licensePlate" width="110" />
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.acct_entry_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="280">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            v-hasPermi="['finance:bookkeeping:list']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['finance:bookkeeping:edit']"
          >修改</el-button>
          <el-button
            v-if="scope.row.entryType === '1' && Number(scope.row.unpaidAmount || 0) > 0"
            size="mini"
            type="text"
            icon="el-icon-money"
            @click="handleReceipt(scope.row)"
            v-hasPermi="['finance:bookkeeping:receipt']"
          >登记收款</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['finance:bookkeeping:remove']"
          >删除</el-button>
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

    <el-dialog :title="title" :visible.sync="open" width="760px" append-to-body>
      <el-alert
        v-if="form.bookkeepingId && paymentListCount > 1"
        title="该收入单已有多次收款记录，编辑时不能再调整已收金额。"
        type="warning"
        :closable="false"
        class="mb12"
      />
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="流水类型" prop="entryType">
              <el-radio-group v-model="form.entryType" :disabled="!!form.bookkeepingId">
                <el-radio
                  v-for="dict in dict.type.acct_entry_type"
                  :key="dict.value"
                  :label="dict.value"
                >{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="业务日期" prop="businessDate">
              <el-date-picker
                v-model="form.businessDate"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="请选择业务日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="分类" prop="category">
              <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%">
                <el-option
                  v-for="dict in formCategoryOptions"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资金账户" prop="fundAccountId">
              <el-select v-model="form.fundAccountId" placeholder="请选择账户" style="width: 100%" filterable>
                <el-option
                  v-for="account in accountOptions"
                  :key="account.fundAccountId"
                  :label="accountLabel(account)"
                  :value="account.fundAccountId"
                  :disabled="accountDisabled(account)"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="总金额" prop="totalAmount">
              <el-input-number v-model="form.totalAmount" :min="0" :step="0.01" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="form.entryType === '1' ? '首笔实收' : '已付金额'" prop="paidAmount">
              <el-input-number
                v-model="form.paidAmount"
                :min="0"
                :step="0.01"
                :disabled="form.entryType === '2' || !canEditInitialPaid"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="未收金额">
              <el-input :value="formatAmount(form.unpaidAmount)" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-input :value="statusLabel(form.status)" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="收付款方式" prop="paymentMethod">
              <el-input v-model="form.paymentMethod" placeholder="如：现金、转账、扫码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="经办人" prop="handlerName">
              <el-input v-model="form.handlerName" placeholder="请输入经办人" />
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
            <el-form-item label="手机号" prop="customerPhone">
              <el-input v-model="form.customerPhone" placeholder="请输入手机号" />
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
        <el-form-item label="业务描述" prop="businessDesc">
          <el-input v-model="form.businessDesc" type="textarea" :rows="3" placeholder="请输入业务描述" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="登记收款" :visible.sync="receiptOpen" width="520px" append-to-body>
      <el-form ref="receiptForm" :model="receiptForm" :rules="receiptRules" label-width="90px">
        <el-form-item label="记账单号">
          <el-input :value="receiptRow.entryNo" disabled />
        </el-form-item>
        <el-form-item label="未收金额">
          <el-input :value="formatAmount(receiptRow.unpaidAmount)" disabled />
        </el-form-item>
        <el-form-item label="资金账户" prop="fundAccountId">
          <el-select v-model="receiptForm.fundAccountId" placeholder="请选择账户" style="width: 100%" filterable>
            <el-option
              v-for="account in accountOptions"
              :key="account.fundAccountId"
              :label="accountLabel(account)"
              :value="account.fundAccountId"
              :disabled="account.status !== '0'"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="收款金额" prop="paymentAmount">
          <el-input-number v-model="receiptForm.paymentAmount" :min="0" :step="0.01" style="width: 100%" />
        </el-form-item>
        <el-form-item label="收款时间" prop="paymentTime">
          <el-date-picker
            v-model="receiptForm.paymentTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择收款时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="收款方式" prop="paymentMethod">
          <el-input v-model="receiptForm.paymentMethod" placeholder="如：扫码、转账、现金" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="receiptForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitReceipt">确 定</el-button>
        <el-button @click="cancelReceipt">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="记账详情" :visible.sync="detailOpen" width="900px" append-to-body>
      <el-row :gutter="16" class="detail-row">
        <el-col :span="8"><span class="detail-label">单号：</span>{{ detailForm.entryNo || '-' }}</el-col>
        <el-col :span="8"><span class="detail-label">业务日期：</span>{{ parseTime(detailForm.businessDate) || '-' }}</el-col>
        <el-col :span="8"><span class="detail-label">流水类型：</span>{{ typeLabel(detailForm.entryType) }}</el-col>
      </el-row>
      <el-row :gutter="16" class="detail-row">
        <el-col :span="8"><span class="detail-label">分类：</span>{{ detailForm.categoryName || '-' }}</el-col>
        <el-col :span="8"><span class="detail-label">总金额：</span>{{ formatAmount(detailForm.totalAmount) }}</el-col>
        <el-col :span="8"><span class="detail-label">状态：</span>{{ statusLabel(detailForm.status) }}</el-col>
      </el-row>
      <el-row :gutter="16" class="detail-row">
        <el-col :span="8"><span class="detail-label">已收金额：</span>{{ formatAmount(detailForm.paidAmount) }}</el-col>
        <el-col :span="8"><span class="detail-label">未收金额：</span>{{ formatAmount(detailForm.unpaidAmount) }}</el-col>
        <el-col :span="8"><span class="detail-label">资金账户：</span>{{ detailForm.fundAccountName || '-' }}</el-col>
      </el-row>
      <el-row :gutter="16" class="detail-row">
        <el-col :span="8"><span class="detail-label">客户姓名：</span>{{ detailForm.customerName || '-' }}</el-col>
        <el-col :span="8"><span class="detail-label">手机号：</span>{{ detailForm.customerPhone || '-' }}</el-col>
        <el-col :span="8"><span class="detail-label">车牌号：</span>{{ detailForm.licensePlate || '-' }}</el-col>
      </el-row>
      <el-row :gutter="16" class="detail-row">
        <el-col :span="8"><span class="detail-label">车型：</span>{{ detailForm.carModel || '-' }}</el-col>
        <el-col :span="8"><span class="detail-label">经办人：</span>{{ detailForm.handlerName || '-' }}</el-col>
        <el-col :span="8"><span class="detail-label">收付款方式：</span>{{ detailForm.paymentMethod || '-' }}</el-col>
      </el-row>
      <el-row class="detail-row">
        <el-col :span="24"><span class="detail-label">业务描述：</span>{{ detailForm.businessDesc || '-' }}</el-col>
      </el-row>
      <el-row class="detail-row">
        <el-col :span="24"><span class="detail-label">备注：</span>{{ detailForm.remark || '-' }}</el-col>
      </el-row>

      <div class="payment-title">收付款记录</div>
      <el-table :data="detailForm.paymentList || []" size="mini">
        <el-table-column label="时间" width="180">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.paymentTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="金额" width="120" align="right">
          <template slot-scope="scope">
            <span>{{ formatAmount(scope.row.paymentAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="账户" prop="fundAccountName" min-width="140" />
        <el-table-column label="方式" prop="paymentMethod" min-width="120" />
        <el-table-column label="备注" prop="remark" min-width="180" :show-overflow-tooltip="true" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { listBookkeeping, getBookkeeping, addBookkeeping, updateBookkeeping, delBookkeeping, receiptBookkeeping, statsBookkeeping } from '@/api/finance/bookkeeping'
import { listAccountOptions } from '@/api/finance/account'

export default {
  name: 'Bookkeeping',
  dicts: ['acct_entry_type', 'acct_income_category', 'acct_expense_category', 'acct_entry_status'],
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      bookkeepingList: [],
      dateRange: [],
      accountOptions: [],
      stats: {
        todayIncome: 0,
        monthIncome: 0,
        monthExpense: 0,
        currentDebtAmount: 0,
        incomeCategoryStats: [],
        expenseCategoryStats: []
      },
      title: '',
      open: false,
      receiptOpen: false,
      detailOpen: false,
      paymentListCount: 0,
      receiptRow: {},
      detailForm: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        entryType: undefined,
        category: undefined,
        status: undefined,
        fundAccountId: undefined,
        customerName: undefined,
        licensePlate: undefined
      },
      form: {},
      receiptForm: {},
      rules: {
        entryType: [
          { required: true, message: '流水类型不能为空', trigger: 'change' }
        ],
        businessDate: [
          { required: true, message: '业务日期不能为空', trigger: 'change' }
        ],
        category: [
          { required: true, message: '分类不能为空', trigger: 'change' }
        ],
        fundAccountId: [
          { required: true, message: '资金账户不能为空', trigger: 'change' }
        ],
        totalAmount: [
          { required: true, message: '总金额不能为空', trigger: 'blur' }
        ]
      },
      receiptRules: {
        fundAccountId: [
          { required: true, message: '资金账户不能为空', trigger: 'change' }
        ],
        paymentAmount: [
          { required: true, message: '收款金额不能为空', trigger: 'blur' }
        ],
        paymentTime: [
          { required: true, message: '收款时间不能为空', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    statCards() {
      return [
        { label: '今日收入', value: this.stats.todayIncome },
        { label: '本月收入', value: this.stats.monthIncome },
        { label: '本月支出', value: this.stats.monthExpense },
        { label: '当前挂账金额', value: this.stats.currentDebtAmount }
      ]
    },
    queryCategoryOptions() {
      if (this.queryParams.entryType === '2') {
        return this.dict.type.acct_expense_category || []
      }
      return this.dict.type.acct_income_category || []
    },
    formCategoryOptions() {
      if (this.form.entryType === '2') {
        return this.dict.type.acct_expense_category || []
      }
      return this.dict.type.acct_income_category || []
    },
    canEditInitialPaid() {
      return !this.form.bookkeepingId || this.paymentListCount <= 1
    }
  },
  watch: {
    'form.entryType'() {
      if (!this.form.bookkeepingId) {
        this.form.category = undefined
      }
      this.syncAmountFields()
    },
    'form.totalAmount'() {
      this.syncAmountFields()
    },
    'form.paidAmount'() {
      this.syncAmountFields()
    }
  },
  created() {
    this.getList()
    this.getStats()
    this.loadAccountOptions()
  },
  methods: {
    getList() {
      this.loading = true
      const query = this.addDateRange({ ...this.queryParams }, this.dateRange, 'BusinessDate')
      listBookkeeping(query).then(response => {
        this.bookkeepingList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    getStats() {
      statsBookkeeping().then(response => {
        this.stats = response.data || this.stats
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
      return this.selectDictLabel(this.dict.type.acct_entry_type, value) || '-'
    },
    statusLabel(value) {
      return this.selectDictLabel(this.dict.type.acct_entry_status, value) || '-'
    },
    accountLabel(account) {
      return account.status === '0' ? account.accountName : account.accountName + '（停用）'
    },
    accountDisabled(account) {
      return account.status !== '0' && account.fundAccountId !== this.form.fundAccountId
    },
    handleQueryTypeChange() {
      this.queryParams.category = undefined
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.paymentListCount = 0
      this.form = {
        bookkeepingId: undefined,
        entryType: '1',
        businessDate: this.parseTime(new Date()),
        category: undefined,
        totalAmount: 0,
        paidAmount: 0,
        unpaidAmount: 0,
        status: '3',
        fundAccountId: undefined,
        paymentMethod: undefined,
        handlerName: undefined,
        customerName: undefined,
        customerPhone: undefined,
        licensePlate: undefined,
        carModel: undefined,
        businessDesc: undefined,
        remark: undefined
      }
      this.resetForm('form')
      this.syncAmountFields()
    },
    resetReceipt() {
      this.receiptRow = {}
      this.receiptForm = {
        bookkeepingId: undefined,
        fundAccountId: undefined,
        paymentAmount: 0,
        paymentTime: this.parseTime(new Date()),
        paymentMethod: undefined,
        remark: undefined
      }
      this.resetForm('receiptForm')
    },
    syncAmountFields() {
      if (!this.form) {
        return
      }
      const total = Number(this.form.totalAmount || 0)
      // 支出单第一版默认一次性结清，所以已付金额始终跟总金额保持一致。
      if (this.form.entryType === '2') {
        this.form.paidAmount = total
      } else if (this.canEditInitialPaid) {
        const paid = Number(this.form.paidAmount || 0)
        if (paid > total) {
          this.form.paidAmount = total
        }
      }
      const paidAmount = Number(this.form.paidAmount || 0)
      this.form.unpaidAmount = Math.max(total - paidAmount, 0)
      this.form.status = this.previewStatus(total, paidAmount)
    },
    previewStatus(total, paidAmount) {
      if (this.form.entryType === '2') {
        return '1'
      }
      if (paidAmount <= 0) {
        return '3'
      }
      return paidAmount >= total ? '1' : '2'
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.dateRange = []
      this.resetForm('queryForm')
      this.queryParams.entryType = undefined
      this.queryParams.category = undefined
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.bookkeepingId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增记账单'
    },
    handleUpdate(row) {
      this.reset()
      const bookkeepingId = row.bookkeepingId || this.ids
      getBookkeeping(bookkeepingId).then(response => {
        this.form = response.data
        this.paymentListCount = (response.data.paymentList || []).length
        this.open = true
        this.title = '修改记账单'
        this.syncAmountFields()
      })
    },
    handleView(row) {
      getBookkeeping(row.bookkeepingId).then(response => {
        this.detailForm = response.data || {}
        this.detailOpen = true
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        // 统一走后端重算金额和状态，前端这里只做交互预览。
        const request = this.form.bookkeepingId ? updateBookkeeping : addBookkeeping
        request(this.form).then(() => {
          this.$modal.msgSuccess(this.form.bookkeepingId ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
          this.getStats()
        })
      })
    },
    handleDelete(row) {
      const bookkeepingIds = row.bookkeepingId || this.ids
      this.$modal.confirm('是否确认删除记账单编号为"' + bookkeepingIds + '"的数据项？').then(() => {
        return delBookkeeping(bookkeepingIds)
      }).then(() => {
        this.getList()
        this.getStats()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    handleReceipt(row) {
      this.resetReceipt()
      this.receiptRow = row
      this.receiptForm.bookkeepingId = row.bookkeepingId
      this.receiptForm.fundAccountId = row.fundAccountId
      this.receiptForm.paymentMethod = row.paymentMethod
      // 收款弹窗默认回填主单当前账户，方便连续核销。
      this.receiptOpen = true
    },
    cancelReceipt() {
      this.receiptOpen = false
      this.resetReceipt()
    },
    submitReceipt() {
      this.$refs.receiptForm.validate(valid => {
        if (!valid) {
          return
        }
        receiptBookkeeping(this.receiptForm).then(() => {
          this.$modal.msgSuccess('登记收款成功')
          this.receiptOpen = false
          this.getList()
          this.getStats()
        })
      })
    },
    handleExport() {
      const query = this.addDateRange({ ...this.queryParams }, this.dateRange, 'BusinessDate')
      this.download('finance/bookkeeping/export', query, `bookkeeping_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>

<style scoped>
.stats-panel {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.stats-card {
  padding: 18px 20px;
  border: 1px solid #e8edf3;
  border-radius: 10px;
  background: linear-gradient(135deg, #ffffff 0%, #f7fafc 100%);
}

.stats-label {
  font-size: 13px;
  color: #667085;
}

.stats-value {
  margin-top: 8px;
  font-size: 28px;
  font-weight: 600;
  color: #1f2937;
}

.summary-row {
  margin-bottom: 16px;
}

.summary-card {
  margin-bottom: 16px;
}

.summary-title {
  font-weight: 600;
}

.detail-row {
  margin-bottom: 12px;
  line-height: 24px;
}

.detail-label {
  color: #606266;
}

.payment-title {
  margin: 20px 0 12px;
  font-weight: 600;
}

@media (max-width: 992px) {
  .stats-panel {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 576px) {
  .stats-panel {
    grid-template-columns: 1fr;
  }
}
</style>
