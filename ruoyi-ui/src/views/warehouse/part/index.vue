<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
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
      <el-form-item label="分类" prop="category">
        <el-select v-model="queryParams.category" placeholder="请选择分类" clearable>
          <el-option
            v-for="dict in dict.type.warehouse_part_category"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in dict.type.sys_normal_disable"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
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
          v-hasPermi="['warehouse:part:add']"
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
          v-hasPermi="['warehouse:part:edit']"
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
          v-hasPermi="['warehouse:part:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['warehouse:part:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="partList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="配件编码" align="center" prop="partCode" width="140" />
      <el-table-column label="配件名称" align="center" prop="partName" min-width="140" />
      <el-table-column label="分类" align="center" prop="category" width="110">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.warehouse_part_category" :value="scope.row.category" />
        </template>
      </el-table-column>
      <el-table-column label="规格型号" align="center" prop="specModel" min-width="120" />
      <el-table-column label="单位" align="center" prop="unit" width="90">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.warehouse_part_unit" :value="scope.row.unit" />
        </template>
      </el-table-column>
      <el-table-column label="参考进价" align="center" prop="purchasePrice" width="100">
        <template slot-scope="scope">
          <span>{{ formatAmount(scope.row.purchasePrice) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="销售单价" align="center" prop="salePrice" width="100">
        <template slot-scope="scope">
          <span>{{ formatAmount(scope.row.salePrice) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="当前库存" align="center" prop="currentStock" width="110">
        <template slot-scope="scope">
          <el-tag :type="isLowStock(scope.row) ? 'danger' : 'success'" effect="plain">
            {{ formatAmount(scope.row.currentStock) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="最低库存" align="center" prop="minStock" width="110">
        <template slot-scope="scope">
          <span>{{ formatAmount(scope.row.minStock) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" min-width="180" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['warehouse:part:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['warehouse:part:remove']"
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

    <el-dialog :title="title" :visible.sync="open" width="680px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="96px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="配件编码" prop="partCode">
              <el-input v-model="form.partCode" placeholder="请输入配件编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="配件名称" prop="partName">
              <el-input v-model="form.partName" placeholder="请输入配件名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="配件分类" prop="category">
              <el-select v-model="form.category" placeholder="请选择配件分类" style="width: 100%">
                <el-option
                  v-for="dict in dict.type.warehouse_part_category"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位" prop="unit">
              <el-select v-model="form.unit" placeholder="请选择单位" style="width: 100%">
                <el-option
                  v-for="dict in dict.type.warehouse_part_unit"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="规格型号" prop="specModel">
              <el-input v-model="form.specModel" placeholder="请输入规格型号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="当前库存">
              <el-input :value="formatAmount(form.currentStock)" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="参考进价" prop="purchasePrice">
              <el-input-number v-model="form.purchasePrice" :min="0" :step="0.01" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="销售单价" prop="salePrice">
              <el-input-number v-model="form.salePrice" :min="0" :step="0.01" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="最低库存" prop="minStock">
              <el-input-number v-model="form.minStock" :min="0" :step="0.01" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in dict.type.sys_normal_disable"
                  :key="dict.value"
                  :label="dict.value"
                >{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listPart, getPart, addPart, updatePart, delPart } from '@/api/warehouse/part'

export default {
  name: 'WarehousePart',
  dicts: ['warehouse_part_category', 'warehouse_part_unit', 'sys_normal_disable'],
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      partList: [],
      title: '',
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        partCode: undefined,
        partName: undefined,
        category: undefined,
        status: undefined
      },
      form: {},
      rules: {
        partCode: [
          { required: true, message: '配件编码不能为空', trigger: 'blur' }
        ],
        partName: [
          { required: true, message: '配件名称不能为空', trigger: 'blur' }
        ],
        category: [
          { required: true, message: '配件分类不能为空', trigger: 'change' }
        ],
        unit: [
          { required: true, message: '单位不能为空', trigger: 'change' }
        ],
        purchasePrice: [
          { required: true, message: '参考进价不能为空', trigger: 'blur' }
        ],
        salePrice: [
          { required: true, message: '销售单价不能为空', trigger: 'blur' }
        ],
        minStock: [
          { required: true, message: '最低库存不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listPart(this.queryParams).then(response => {
        this.partList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    formatAmount(value) {
      return Number(value || 0).toFixed(2)
    },
    isLowStock(row) {
      return Number(row.currentStock || 0) <= Number(row.minStock || 0)
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        partId: undefined,
        partCode: undefined,
        partName: undefined,
        category: undefined,
        specModel: undefined,
        unit: undefined,
        purchasePrice: 0,
        salePrice: 0,
        currentStock: 0,
        minStock: 0,
        status: '0',
        remark: undefined
      }
      this.resetForm('form')
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.partId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增配件档案'
    },
    handleUpdate(row) {
      this.reset()
      const partId = row.partId || this.ids
      getPart(partId).then(response => {
        this.form = response.data
        this.open = true
        this.title = '修改配件档案'
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        const request = this.form.partId ? updatePart : addPart
        request(this.form).then(() => {
          this.$modal.msgSuccess(this.form.partId ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const partIds = row.partId || this.ids
      this.$modal.confirm('是否确认删除配件编号为"' + partIds + '"的数据项？').then(() => {
        return delPart(partIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    handleExport() {
      this.download('warehouse/part/export', {
        ...this.queryParams
      }, `warehouse_part_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
