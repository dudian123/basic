<template>
  <div class="p-4">
    <!-- 搜索区域 -->
    <transition name="el-fade-in">
      <div v-show="showSearch" class="mb-4">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="80px">
            <el-form-item label="系统模块" prop="title">
              <el-input 
                v-model="queryParams.title" 
                placeholder="请输入系统模块" 
                clearable 
                @keyup.enter="handleQuery" 
              />
            </el-form-item>
            <el-form-item label="操作人员" prop="operName">
              <el-input 
                v-model="queryParams.operName" 
                placeholder="请输入操作人员" 
                clearable 
                @keyup.enter="handleQuery" 
              />
            </el-form-item>
            <el-form-item label="类型" prop="businessType">
              <el-select v-model="queryParams.businessType" placeholder="操作类型" clearable>
                <el-option label="其它" value="0" />
                <el-option label="新增" value="1" />
                <el-option label="修改" value="2" />
                <el-option label="删除" value="3" />
                <el-option label="授权" value="4" />
                <el-option label="导出" value="5" />
                <el-option label="导入" value="6" />
                <el-option label="强退" value="7" />
                <el-option label="生成代码" value="8" />
                <el-option label="清空数据" value="9" />
              </el-select>
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="操作状态" clearable>
                <el-option label="成功" value="0" />
                <el-option label="失败" value="1" />
              </el-select>
            </el-form-item>
            <el-form-item label="操作时间">
              <el-date-picker
                v-model="dateRange"
                type="datetimerange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD HH:mm:ss"
                @change="handleDateChange"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
              <el-button icon="Refresh" @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </transition>

    <!-- 操作日志列表 -->
    <el-card shadow="hover">
      <template #header>
        <el-row :gutter="10" class="mb-2">
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" @click="handleClean">清空</el-button>
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
        :data="operList" 
        @selection-change="handleSelectionChange"
        border
        stripe
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="日志编号" prop="operId" width="120" />
        <el-table-column label="系统模块" prop="title" show-overflow-tooltip />
        <el-table-column label="操作类型" align="center" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.businessType === '1'" type="primary">新增</el-tag>
            <el-tag v-else-if="scope.row.businessType === '2'" type="success">修改</el-tag>
            <el-tag v-else-if="scope.row.businessType === '3'" type="danger">删除</el-tag>
            <el-tag v-else-if="scope.row.businessType === '4'" type="warning">授权</el-tag>
            <el-tag v-else-if="scope.row.businessType === '5'" type="info">导出</el-tag>
            <el-tag v-else-if="scope.row.businessType === '6'" type="info">导入</el-tag>
            <el-tag v-else-if="scope.row.businessType === '7'" type="danger">强退</el-tag>
            <el-tag v-else-if="scope.row.businessType === '8'" type="primary">生成代码</el-tag>
            <el-tag v-else-if="scope.row.businessType === '9'" type="danger">清空数据</el-tag>
            <el-tag v-else>其它</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作人员" prop="operName" width="120" show-overflow-tooltip />
        <el-table-column label="主机" prop="operIp" width="130" show-overflow-tooltip />
        <el-table-column label="操作地点" prop="operLocation" show-overflow-tooltip />
        <el-table-column label="操作状态" align="center" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === '0'" type="success">成功</el-tag>
            <el-tag v-else type="danger">失败</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作日期" prop="operTime" width="180" />
        <el-table-column label="消耗时间" prop="costTime" width="120" align="center">
          <template #default="scope">
            <span>{{ scope.row.costTime }}ms</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="150">
          <template #default="scope">
            <el-button type="primary" link icon="View" @click="handleView(scope.row)">详细</el-button>
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

    <!-- 操作日志详细 -->
    <el-dialog title="操作日志详细" v-model="open" width="700px" append-to-body>
      <el-form ref="formRef" :model="form" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="操作模块：">{{ form.title }} / {{ getBusinessTypeName(form.businessType) }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="登录信息：">{{ form.operName }} / {{ form.operIp }} / {{ form.operLocation }}</el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="请求地址：">{{ form.operUrl }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="请求方式：">{{ form.requestMethod }}</el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="操作方法：">{{ form.method }}</el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="操作状态：">
              <el-tag v-if="form.status === '0'" type="success">成功</el-tag>
              <el-tag v-else type="danger">失败</el-tag>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="请求参数：">
              <el-input v-model="form.operParam" type="textarea" :rows="5" readonly />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20" v-if="form.status === '1'">
          <el-col :span="24">
            <el-form-item label="异常信息：">
              <el-input v-model="form.errorMsg" type="textarea" :rows="5" readonly />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="open = false">关 闭</el-button>
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
const operList = ref([])
const open = ref(false)
const multiple = ref(true)
const total = ref(0)
const ids = ref([])
const dateRange = ref([])

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  title: '',
  operName: '',
  businessType: '',
  status: '',
  beginTime: '',
  endTime: ''
})

// 表单数据
const form = reactive({
  operId: '',
  title: '',
  businessType: '',
  operName: '',
  operIp: '',
  operLocation: '',
  operUrl: '',
  requestMethod: '',
  method: '',
  operParam: '',
  status: '',
  errorMsg: '',
  operTime: '',
  costTime: ''
})

// 获取操作类型名称
const getBusinessTypeName = (type) => {
  const typeMap = {
    '0': '其它',
    '1': '新增',
    '2': '修改',
    '3': '删除',
    '4': '授权',
    '5': '导出',
    '6': '导入',
    '7': '强退',
    '8': '生成代码',
    '9': '清空数据'
  }
  return typeMap[type] || '其它'
}

// 获取操作日志列表
const getList = () => {
  loading.value = true
  // 模拟数据
  setTimeout(() => {
    operList.value = [
      {
        operId: 1,
        title: '用户管理',
        businessType: '1',
        operName: 'admin',
        operIp: '127.0.0.1',
        operLocation: '内网IP',
        operUrl: '/system/user',
        requestMethod: 'POST',
        method: 'com.ruoyi.web.controller.system.SysUserController.add()',
        operParam: '{"userName":"test","nickName":"测试用户"}',
        status: '0',
        errorMsg: '',
        operTime: '2024-01-15 10:30:00',
        costTime: 156
      },
      {
        operId: 2,
        title: '角色管理',
        businessType: '2',
        operName: 'admin',
        operIp: '127.0.0.1',
        operLocation: '内网IP',
        operUrl: '/system/role',
        requestMethod: 'PUT',
        method: 'com.ruoyi.web.controller.system.SysRoleController.edit()',
        operParam: '{"roleId":2,"roleName":"普通角色"}',
        status: '0',
        errorMsg: '',
        operTime: '2024-01-15 10:25:00',
        costTime: 89
      },
      {
        operId: 3,
        title: '菜单管理',
        businessType: '3',
        operName: 'admin',
        operIp: '127.0.0.1',
        operLocation: '内网IP',
        operUrl: '/system/menu/100',
        requestMethod: 'DELETE',
        method: 'com.ruoyi.web.controller.system.SysMenuController.remove()',
        operParam: '{"menuId":100}',
        status: '1',
        errorMsg: '删除失败，菜单下存在子菜单',
        operTime: '2024-01-15 10:20:00',
        costTime: 45
      },
      {
        operId: 4,
        title: '用户管理',
        businessType: '5',
        operName: 'admin',
        operIp: '127.0.0.1',
        operLocation: '内网IP',
        operUrl: '/system/user/export',
        requestMethod: 'POST',
        method: 'com.ruoyi.web.controller.system.SysUserController.export()',
        operParam: '{}',
        status: '0',
        errorMsg: '',
        operTime: '2024-01-15 10:15:00',
        costTime: 1234
      },
      {
        operId: 5,
        title: '代码生成',
        businessType: '8',
        operName: 'admin',
        operIp: '127.0.0.1',
        operLocation: '内网IP',
        operUrl: '/tool/gen/batchGenCode',
        requestMethod: 'GET',
        method: 'com.ruoyi.generator.controller.GenController.batchGenCode()',
        operParam: '{"tables":"sys_test"}',
        status: '0',
        errorMsg: '',
        operTime: '2024-01-15 10:10:00',
        costTime: 2156
      }
    ]
    total.value = 5
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
  dateRange.value = []
  Object.assign(queryParams, {
    pageNum: 1,
    pageSize: 10,
    title: '',
    operName: '',
    businessType: '',
    status: '',
    beginTime: '',
    endTime: ''
  })
  getList()
}

// 日期范围改变
const handleDateChange = (dates) => {
  if (dates && dates.length === 2) {
    queryParams.beginTime = dates[0]
    queryParams.endTime = dates[1]
  } else {
    queryParams.beginTime = ''
    queryParams.endTime = ''
  }
}

// 查看详细
const handleView = (row) => {
  Object.assign(form, row)
  open.value = true
}

// 删除
const handleDelete = (row) => {
  const operIds = row?.operId ? [row.operId] : ids.value
  ElMessageBox.confirm(`是否确认删除日志编号为"${operIds}"的数据项？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 这里应该调用删除API
    ElMessage.success('删除成功')
    getList()
  })
}

// 清空
const handleClean = () => {
  ElMessageBox.confirm('是否确认清空所有操作日志数据项？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 这里应该调用清空API
    ElMessage.success('清空成功')
    getList()
  })
}

// 导出
const handleExport = () => {
  // 这里应该调用导出API
  ElMessage.success('导出成功')
}

// 多选框选中数据
const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.operId)
  multiple.value = !selection.length
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