<template>
  <div class="p-4">
    <!-- 搜索区域 -->
    <transition name="el-fade-in">
      <div v-show="showSearch" class="mb-4">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="80px">
            <el-form-item label="租户编号" prop="tenantId">
              <el-input 
                v-model="queryParams.tenantId" 
                placeholder="请输入租户编号" 
                clearable 
                @keyup.enter="handleQuery" 
              />
            </el-form-item>
            <el-form-item label="租户名称" prop="companyName">
              <el-input 
                v-model="queryParams.companyName" 
                placeholder="请输入租户名称" 
                clearable 
                @keyup.enter="handleQuery" 
              />
            </el-form-item>
            <el-form-item label="联系人" prop="contactUserName">
              <el-input 
                v-model="queryParams.contactUserName" 
                placeholder="请输入联系人" 
                clearable 
                @keyup.enter="handleQuery" 
              />
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="租户状态" clearable>
                <el-option label="正常" value="0" />
                <el-option label="停用" value="1" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
              <el-button icon="Refresh" @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </transition>

    <!-- 租户列表 -->
    <el-card shadow="hover">
      <template #header>
        <el-row :gutter="10" class="mb-2">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd">新增租户</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport">导出</el-button>
          </el-col>
          <div class="ml-auto">
            <el-button type="primary" plain icon="Search" @click="showSearch = !showSearch">搜索</el-button>
            <el-button type="info" plain icon="Refresh" @click="getList">刷新</el-button>
          </div>
        </el-row>
      </template>

      <el-table 
        v-loading="loading" 
        :data="tenantList" 
        @selection-change="handleSelectionChange"
        border
        stripe
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="租户编号" prop="tenantId" width="120" />
        <el-table-column label="租户名称" prop="companyName" show-overflow-tooltip />
        <el-table-column label="联系人" prop="contactUserName" width="120" />
        <el-table-column label="联系电话" prop="contactPhone" width="150" />
        <el-table-column label="企业邮箱" prop="companyEmail" show-overflow-tooltip />
        <el-table-column label="租户套餐" prop="packageName" width="120" />
        <el-table-column label="过期时间" prop="expireTime" width="180" />
        <el-table-column label="账号数量" prop="accountCount" width="100" align="center">
          <template #default="scope">
            <el-tag type="info">{{ scope.row.accountCount }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" width="100">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              active-value="0"
              inactive-value="1"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="180" />
        <el-table-column label="操作" align="center" width="200">
          <template #default="scope">
            <el-button type="primary" link icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
            <el-button type="primary" link icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
            <el-button type="primary" link icon="Key" @click="handleResetPwd(scope.row)">重置密码</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 添加或修改租户对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="tenantFormRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="租户编号" prop="tenantId">
              <el-input v-model="form.tenantId" placeholder="请输入租户编号" :disabled="form.id !== undefined" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="租户名称" prop="companyName">
              <el-input v-model="form.companyName" placeholder="请输入租户名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactUserName">
              <el-input v-model="form.contactUserName" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="企业邮箱" prop="companyEmail">
              <el-input v-model="form.companyEmail" placeholder="请输入企业邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="租户套餐" prop="packageId">
              <el-select v-model="form.packageId" placeholder="请选择租户套餐" style="width: 100%">
                <el-option label="基础版" value="1" />
                <el-option label="专业版" value="2" />
                <el-option label="企业版" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="过期时间" prop="expireTime">
              <el-date-picker
                v-model="form.expireTime"
                type="datetime"
                placeholder="选择过期时间"
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账号数量" prop="accountCount">
              <el-input-number v-model="form.accountCount" :min="1" :max="9999" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 响应式数据
const loading = ref(true)
const showSearch = ref(true)
const tenantList = ref([])
const open = ref(false)
const title = ref('')
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const ids = ref([])

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  tenantId: '',
  companyName: '',
  contactUserName: '',
  status: ''
})

// 表单数据
const form = reactive({
  id: undefined,
  tenantId: '',
  companyName: '',
  contactUserName: '',
  contactPhone: '',
  companyEmail: '',
  packageId: '',
  expireTime: '',
  accountCount: 10,
  status: '0',
  remark: ''
})

// 表单验证规则
const rules = reactive({
  tenantId: [{ required: true, message: '租户编号不能为空', trigger: 'blur' }],
  companyName: [{ required: true, message: '租户名称不能为空', trigger: 'blur' }],
  contactUserName: [{ required: true, message: '联系人不能为空', trigger: 'blur' }],
  contactPhone: [
    { required: true, message: '联系电话不能为空', trigger: 'blur' },
    { pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  companyEmail: [
    { required: true, message: '企业邮箱不能为空', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  packageId: [{ required: true, message: '租户套餐不能为空', trigger: 'change' }],
  expireTime: [{ required: true, message: '过期时间不能为空', trigger: 'change' }]
})

// 获取租户列表
const getList = () => {
  loading.value = true
  // 模拟数据
  setTimeout(() => {
    tenantList.value = [
      {
        id: 1,
        tenantId: '000000',
        companyName: '超级管理员租户',
        contactUserName: '管理员',
        contactPhone: '15888888888',
        companyEmail: 'admin@example.com',
        packageName: '企业版',
        expireTime: '2025-12-31 23:59:59',
        accountCount: 100,
        status: '0',
        createTime: '2024-01-01 10:00:00'
      },
      {
        id: 2,
        tenantId: '000001',
        companyName: '测试租户',
        contactUserName: '测试用户',
        contactPhone: '13666666666',
        companyEmail: 'test@example.com',
        packageName: '基础版',
        expireTime: '2024-12-31 23:59:59',
        accountCount: 10,
        status: '0',
        createTime: '2024-01-01 11:00:00'
      }
    ]
    total.value = 2
    loading.value = false
  }, 500)
}

// 搜索
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置搜索
const resetQuery = () => {
  Object.assign(queryParams, {
    pageNum: 1,
    pageSize: 10,
    tenantId: '',
    companyName: '',
    contactUserName: '',
    status: ''
  })
  getList()
}

// 新增
const handleAdd = () => {
  reset()
  open.value = true
  title.value = '添加租户'
}

// 修改
const handleUpdate = (row) => {
  reset()
  const id = row?.id || ids.value[0]
  // 这里应该调用API获取详细信息
  Object.assign(form, {
    id: 1,
    tenantId: '000000',
    companyName: '超级管理员租户',
    contactUserName: '管理员',
    contactPhone: '15888888888',
    companyEmail: 'admin@example.com',
    packageId: '3',
    expireTime: '2025-12-31 23:59:59',
    accountCount: 100,
    status: '0',
    remark: '系统默认租户'
  })
  open.value = true
  title.value = '修改租户'
}

// 删除
const handleDelete = (row) => {
  const tenantIds = row?.tenantId ? [row.tenantId] : ids.value
  ElMessageBox.confirm(`是否确认删除租户编号为"${tenantIds}"的数据项？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 这里应该调用删除API
    ElMessage.success('删除成功')
    getList()
  })
}

// 状态修改
const handleStatusChange = (row) => {
  const text = row.status === '0' ? '启用' : '停用'
  ElMessageBox.confirm(`确认要"${text}""${row.companyName}"租户吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 这里应该调用API修改状态
    ElMessage.success(`${text}成功`)
  }).catch(() => {
    row.status = row.status === '0' ? '1' : '0'
  })
}

// 重置密码
const handleResetPwd = (row) => {
  ElMessageBox.prompt(`请输入"${row.companyName}"的新密码`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    closeOnClickModal: false,
    inputType: 'password',
    inputValidator: (value) => {
      if (!value || value.length < 6) {
        return '密码长度不能少于6位'
      }
      return true
    }
  }).then(({ value }) => {
    // 这里应该调用API重置密码
    ElMessage.success('重置密码成功')
  })
}

// 导出
const handleExport = () => {
  // 这里应该调用导出API
  ElMessage.success('导出成功')
}

// 多选框选中数据
const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.id)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

// 提交表单
const submitForm = () => {
  // 这里应该调用API保存数据
  ElMessage.success('操作成功')
  open.value = false
  getList()
}

// 取消
const cancel = () => {
  open.value = false
  reset()
}

// 重置表单
const reset = () => {
  Object.assign(form, {
    id: undefined,
    tenantId: '',
    companyName: '',
    contactUserName: '',
    contactPhone: '',
    companyEmail: '',
    packageId: '',
    expireTime: '',
    accountCount: 10,
    status: '0',
    remark: ''
  })
}

// 组件挂载时获取数据
onMounted(() => {
  getList()
})
</script>

<style scoped>
.ml-auto {
  margin-left: auto;
}
</style>