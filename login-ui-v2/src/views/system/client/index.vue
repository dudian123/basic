<template>
  <div class="p-4">
    <!-- 搜索区域 -->
    <transition name="el-fade-in">
      <div v-show="showSearch" class="mb-4">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="80px">
            <el-form-item label="客户端ID" prop="clientId">
              <el-input 
                v-model="queryParams.clientId" 
                placeholder="请输入客户端ID" 
                clearable 
                @keyup.enter="handleQuery" 
              />
            </el-form-item>
            <el-form-item label="客户端名称" prop="clientName">
              <el-input 
                v-model="queryParams.clientName" 
                placeholder="请输入客户端名称" 
                clearable 
                @keyup.enter="handleQuery" 
              />
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="客户端状态" clearable>
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

    <!-- 客户端列表 -->
    <el-card shadow="hover">
      <template #header>
        <el-row :gutter="10" class="mb-2">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd">新增客户端</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
          </el-col>
          <div class="ml-auto">
            <el-button type="primary" plain icon="Search" @click="showSearch = !showSearch">搜索</el-button>
            <el-button type="info" plain icon="Refresh" @click="getList">刷新</el-button>
          </div>
        </el-row>
      </template>

      <el-table 
        v-loading="loading" 
        :data="clientList" 
        @selection-change="handleSelectionChange"
        border
        stripe
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="客户端ID" prop="clientId" width="200" />
        <el-table-column label="客户端名称" prop="clientName" />
        <el-table-column label="客户端密钥" prop="clientSecret" show-overflow-tooltip />
        <el-table-column label="授权类型" prop="grantTypes" />
        <el-table-column label="状态" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
              {{ scope.row.status === '0' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="180" />
        <el-table-column label="操作" align="center" width="200">
          <template #default="scope">
            <el-button type="primary" link icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
            <el-button type="primary" link icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
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

    <!-- 添加或修改客户端对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="clientFormRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="客户端ID" prop="clientId">
          <el-input v-model="form.clientId" placeholder="请输入客户端ID" />
        </el-form-item>
        <el-form-item label="客户端名称" prop="clientName">
          <el-input v-model="form.clientName" placeholder="请输入客户端名称" />
        </el-form-item>
        <el-form-item label="客户端密钥" prop="clientSecret">
          <el-input v-model="form.clientSecret" placeholder="请输入客户端密钥" type="password" />
        </el-form-item>
        <el-form-item label="授权类型" prop="grantTypes">
          <el-input v-model="form.grantTypes" placeholder="请输入授权类型" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
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
const clientList = ref([])
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
  clientId: '',
  clientName: '',
  status: ''
})

// 表单数据
const form = reactive({
  clientId: '',
  clientName: '',
  clientSecret: '',
  grantTypes: '',
  status: '0'
})

// 表单验证规则
const rules = reactive({
  clientId: [{ required: true, message: '客户端ID不能为空', trigger: 'blur' }],
  clientName: [{ required: true, message: '客户端名称不能为空', trigger: 'blur' }],
  clientSecret: [{ required: true, message: '客户端密钥不能为空', trigger: 'blur' }]
})

// 获取客户端列表
const getList = () => {
  loading.value = true
  // 模拟数据
  setTimeout(() => {
    clientList.value = [
      {
        clientId: 'web-client',
        clientName: 'Web客户端',
        clientSecret: '***',
        grantTypes: 'authorization_code,refresh_token',
        status: '0',
        createTime: '2024-01-01 10:00:00'
      },
      {
        clientId: 'mobile-client',
        clientName: '移动端客户端',
        clientSecret: '***',
        grantTypes: 'password,refresh_token',
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
    clientId: '',
    clientName: '',
    status: ''
  })
  getList()
}

// 新增
const handleAdd = () => {
  reset()
  open.value = true
  title.value = '添加客户端'
}

// 修改
const handleUpdate = (row) => {
  reset()
  const clientId = row?.clientId || ids.value[0]
  // 这里应该调用API获取详细信息
  Object.assign(form, {
    clientId: 'web-client',
    clientName: 'Web客户端',
    clientSecret: 'secret123',
    grantTypes: 'authorization_code,refresh_token',
    status: '0'
  })
  open.value = true
  title.value = '修改客户端'
}

// 删除
const handleDelete = (row) => {
  const clientIds = row?.clientId ? [row.clientId] : ids.value
  ElMessageBox.confirm(`是否确认删除客户端编号为"${clientIds}"的数据项？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 这里应该调用删除API
    ElMessage.success('删除成功')
    getList()
  })
}

// 多选框选中数据
const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.clientId)
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
    clientId: '',
    clientName: '',
    clientSecret: '',
    grantTypes: '',
    status: '0'
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