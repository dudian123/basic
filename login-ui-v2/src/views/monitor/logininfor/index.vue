<template>
  <div class="p-4">
    <!-- 搜索区域 -->
    <transition name="el-fade-in">
      <div v-show="showSearch" class="mb-4">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="80px">
            <el-form-item label="登录地址" prop="ipaddr">
              <el-input 
                v-model="queryParams.ipaddr" 
                placeholder="请输入登录地址" 
                clearable 
                @keyup.enter="handleQuery" 
              />
            </el-form-item>
            <el-form-item label="用户名称" prop="userName">
              <el-input 
                v-model="queryParams.userName" 
                placeholder="请输入用户名称" 
                clearable 
                @keyup.enter="handleQuery" 
              />
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="登录状态" clearable>
                <el-option label="成功" value="0" />
                <el-option label="失败" value="1" />
              </el-select>
            </el-form-item>
            <el-form-item label="登录时间">
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

    <!-- 登录日志列表 -->
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
            <el-button type="primary" plain icon="Unlock" :disabled="single" @click="handleUnlock">解锁</el-button>
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
        :data="loginList" 
        @selection-change="handleSelectionChange"
        border
        stripe
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="访问编号" prop="infoId" width="120" />
        <el-table-column label="用户名称" prop="userName" width="120" show-overflow-tooltip />
        <el-table-column label="登录地址" prop="ipaddr" width="130" show-overflow-tooltip />
        <el-table-column label="登录地点" prop="loginLocation" show-overflow-tooltip />
        <el-table-column label="浏览器" prop="browser" show-overflow-tooltip />
        <el-table-column label="操作系统" prop="os" show-overflow-tooltip />
        <el-table-column label="登录状态" align="center" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === '0'" type="success">成功</el-tag>
            <el-tag v-else type="danger">失败</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作信息" prop="msg" show-overflow-tooltip />
        <el-table-column label="登录日期" prop="loginTime" width="180" />
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 响应式数据
const loading = ref(true)
const showSearch = ref(true)
const loginList = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const ids = ref([])
const dateRange = ref([])

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  ipaddr: '',
  userName: '',
  status: '',
  beginTime: '',
  endTime: ''
})

// 获取登录日志列表
const getList = () => {
  loading.value = true
  // 模拟数据
  setTimeout(() => {
    loginList.value = [
      {
        infoId: 1,
        userName: 'admin',
        ipaddr: '127.0.0.1',
        loginLocation: '内网IP',
        browser: 'Chrome 120',
        os: 'Windows 11',
        status: '0',
        msg: '登录成功',
        loginTime: '2024-01-15 09:30:00'
      },
      {
        infoId: 2,
        userName: 'test',
        ipaddr: '192.168.1.100',
        loginLocation: '内网IP',
        browser: 'Firefox 121',
        os: 'Windows 10',
        status: '1',
        msg: '密码错误',
        loginTime: '2024-01-15 09:25:00'
      },
      {
        infoId: 3,
        userName: 'admin',
        ipaddr: '127.0.0.1',
        loginLocation: '内网IP',
        browser: 'Chrome 120',
        os: 'Windows 11',
        status: '0',
        msg: '登录成功',
        loginTime: '2024-01-15 08:45:00'
      },
      {
        infoId: 4,
        userName: 'user1',
        ipaddr: '192.168.1.101',
        loginLocation: '内网IP',
        browser: 'Edge 120',
        os: 'Windows 11',
        status: '0',
        msg: '登录成功',
        loginTime: '2024-01-15 08:30:00'
      },
      {
        infoId: 5,
        userName: 'test',
        ipaddr: '192.168.1.100',
        loginLocation: '内网IP',
        browser: 'Firefox 121',
        os: 'Windows 10',
        status: '1',
        msg: '用户不存在',
        loginTime: '2024-01-15 08:15:00'
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
    ipaddr: '',
    userName: '',
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

// 删除
const handleDelete = (row) => {
  const infoIds = row?.infoId ? [row.infoId] : ids.value
  ElMessageBox.confirm(`是否确认删除访问编号为"${infoIds}"的数据项？`, '警告', {
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
  ElMessageBox.confirm('是否确认清空所有登录日志数据项？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 这里应该调用清空API
    ElMessage.success('清空成功')
    getList()
  })
}

// 解锁
const handleUnlock = () => {
  const userName = loginList.value.find(item => ids.value.includes(item.infoId))?.userName
  ElMessageBox.confirm(`是否确认解锁用户"${userName}"？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 这里应该调用解锁API
    ElMessage.success('解锁成功')
  })
}

// 导出
const handleExport = () => {
  // 这里应该调用导出API
  ElMessage.success('导出成功')
}

// 多选框选中数据
const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.infoId)
  single.value = selection.length !== 1
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