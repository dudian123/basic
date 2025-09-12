<template>
  <el-dialog
    v-model="visible"
    title="选择用户"
    width="900px"
    append-to-body
  >
    <!-- 搜索区域 -->
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

    <!-- 用户列表 -->
    <el-table
      v-loading="loading"
      :data="userList"
      @selection-change="handleSelectionChange"
      height="350px"
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
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="handleSelectUser">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { unallocatedUserList, authUserSelectAll } from '@/api/system/role'
import { parseTime } from '@/utils/ruoyi'

interface Props {
  roleId?: number
}

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

const props = defineProps<Props>()
const emit = defineEmits(['ok'])

// 数据
const visible = ref(false)
const loading = ref(false)
const total = ref(0)
const userList = ref<UserVO[]>([])

// 查询参数
const queryParams = reactive<QueryParams>({
  pageNum: 1,
  pageSize: 10,
  roleId: props.roleId,
  userName: '',
  phonenumber: ''
})

// 选中的用户
const userIds = ref<number[]>([])

// refs
const queryFormRef = ref()

// 获取未分配用户列表
const getList = async () => {
  loading.value = true
  try {
    queryParams.roleId = props.roleId
    const res = await unallocatedUserList(queryParams)
    // 后端返回的数据结构是 { total: number, rows: [...], code: 200, msg: '查询成功' }
    userList.value = res.rows || []
    total.value = res.total || 0
  } catch (error) {
    console.error('获取用户列表失败:', error)
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
}

// 选择用户
const handleSelectUser = async () => {
  if (userIds.value.length === 0) {
    ElMessage.warning('请选择要分配的用户')
    return
  }
  
  try {
    await authUserSelectAll({
      roleId: props.roleId,
      userIds: userIds.value.join(',')
    })
    
    ElMessage.success('分配用户成功')
    visible.value = false
    emit('ok')
  } catch (error) {
    console.error('分配用户失败:', error)
    ElMessage.error('分配用户失败')
  }
}

// 取消
const cancel = () => {
  visible.value = false
  userIds.value = []
}

// 显示对话框
const show = () => {
  visible.value = true
  queryParams.roleId = props.roleId
  handleQuery()
}

// 暴露方法
defineExpose({
  show
})
</script>

<style scoped>
.dialog-footer {
  text-align: right;
}
</style>