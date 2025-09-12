<template>
  <div class="p-4">
    <!-- 页面标题 -->
    <div class="mb-4">
      <el-page-header @back="handleBack">
        <template #content>
          <span class="text-large font-600 mr-3">分配用户</span>
          <span class="text-sm text-gray-500">角色名称：{{ roleInfo.roleName }}</span>
        </template>
      </el-page-header>
    </div>

    <!-- 搜索区域 -->
    <div class="mb-4">
      <el-card shadow="hover">
        <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="80px">
          <el-form-item label="用户名称" prop="userName">
            <el-input 
              v-model="queryParams.userName" 
              placeholder="请输入用户名称" 
              clearable 
              @keyup.enter="handleQuery" 
            />
          </el-form-item>
          <el-form-item label="手机号码" prop="phonenumber">
            <el-input 
              v-model="queryParams.phonenumber" 
              placeholder="请输入手机号码" 
              clearable 
              @keyup.enter="handleQuery" 
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 操作按钮 -->
    <div class="mb-4">
      <el-row :gutter="10">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="Plus"
            @click="openSelectUser"
            v-hasPermi="['system:role:edit']"
          >
            添加用户
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="danger"
            plain
            icon="CircleClose"
            :disabled="multiple"
            @click="cancelAuthUserAll"
            v-hasPermi="['system:role:edit']"
          >
            批量取消授权
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="warning"
            plain
            icon="Close"
            @click="handleClose"
          >
            关闭
          </el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 用户列表 -->
    <el-card shadow="hover">
      <el-table
        v-loading="loading"
        :data="userList"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column label="用户编号" align="center" prop="userId" />
        <el-table-column label="用户名称" align="center" prop="userName" :show-overflow-tooltip="true" />
        <el-table-column label="用户昵称" align="center" prop="nickName" :show-overflow-tooltip="true" />
        <el-table-column label="邮箱" align="center" prop="email" :show-overflow-tooltip="true" />
        <el-table-column label="手机" align="center" prop="phonenumber" :show-overflow-tooltip="true" />
        <el-table-column label="状态" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.status == '0'" type="success">正常</el-tag>
            <el-tag v-else type="danger">停用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template #default="scope">
            <el-button
              link
              type="primary"
              icon="CircleClose"
              @click="cancelAuthUser(scope.row)"
              v-hasPermi="['system:role:edit']"
            >
              取消授权
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 选择用户对话框 -->
    <select-user
      ref="selectUserRef"
      :role-id="roleId"
      @ok="handleQuery"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { allocatedUserList, authUserCancel, authUserCancelAll } from '@/api/system/role'
import { parseTime } from '@/utils/ruoyi'
import SelectUser from './selectUser.vue'

interface QueryParams {
  pageNum: number
  pageSize: number
  roleId?: number
  userName?: string
  phonenumber?: string
}

interface UserVO {
  userId: number
  userName: string
  nickName: string
  email: string
  phonenumber: string
  status: string
  createTime: string
}

interface RoleInfo {
  roleId: number
  roleName: string
}

const route = useRoute()
const router = useRouter()

// 获取角色ID
const roleIdParam = route.params.roleId as string
console.log('获取到的roleId参数:', roleIdParam)

// 检查roleId是否有效并初始化
let validRoleId = 0
if (!roleIdParam || roleIdParam === ':roleId' || isNaN(parseInt(roleIdParam))) {
  ElMessage.error('角色ID参数无效，请从角色管理页面正确进入')
  router.push('/system/role')
} else {
  validRoleId = parseInt(roleIdParam)
}

const roleId = ref<number>(validRoleId)

// 数据
const loading = ref(false)
const multiple = ref(true)
const total = ref(0)
const userList = ref<UserVO[]>([])
const roleInfo = ref<RoleInfo>({
  roleId: 0,
  roleName: ''
})

// 查询参数
const queryParams = reactive<QueryParams>({
  pageNum: 1,
  pageSize: 10,
  roleId: roleId.value,
  userName: '',
  phonenumber: ''
})

// 选中的用户
const userIds = ref<number[]>([])

// refs
const queryFormRef = ref()
const selectUserRef = ref()

// 获取已分配用户列表
const getList = async () => {
  loading.value = true
  try {
    const response = await allocatedUserList(queryParams)
    // 后端返回的数据结构是 { total: number, rows: [...], code: 200, msg: '查询成功' }
    userList.value = response.rows || []
    total.value = response.total || 0
    
    // 设置角色信息
    if (!roleInfo.value.roleName) {
      roleInfo.value.roleId = roleId.value
      roleInfo.value.roleName = `角色${roleId.value}`
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    userList.value = []
    total.value = 0
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置
const resetQuery = () => {
  queryParams.userName = ''
  queryParams.phonenumber = ''
  queryFormRef.value?.resetFields()
  handleQuery()
}

// 多选框选中数据
const handleSelectionChange = (selection: UserVO[]) => {
  userIds.value = selection.map(item => item.userId)
  multiple.value = !selection.length
}

// 取消授权用户
const cancelAuthUser = async (row: UserVO) => {
  try {
    await ElMessageBox.confirm(
      `确认要取消该用户"${row.userName}"角色吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await authUserCancel({
      userId: row.userId,
      roleId: roleId.value
    })
    
    ElMessage.success('取消授权成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消授权失败:', error)
      ElMessage.error('取消授权失败')
    }
  }
}

// 批量取消授权
const cancelAuthUserAll = async () => {
  if (userIds.value.length === 0) {
    ElMessage.warning('请选择要取消授权的用户')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确认要取消选中的${userIds.value.length}个用户的角色吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await authUserCancelAll({
      roleId: roleId.value,
      userIds: userIds.value.join(',')
    })
    
    ElMessage.success('取消授权成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量取消授权失败:', error)
      ElMessage.error('批量取消授权失败')
    }
  }
}

// 打开选择用户对话框
const openSelectUser = () => {
  selectUserRef.value?.show()
}

// 返回
const handleBack = () => {
  router.back()
}

// 关闭
const handleClose = () => {
  router.push('/system/role')
}

// 初始化
onMounted(() => {
  getList()
})
</script>

<style scoped>
.text-large {
  font-size: 18px;
}

.font-600 {
  font-weight: 600;
}

.text-gray-500 {
  color: #6b7280;
}
</style>