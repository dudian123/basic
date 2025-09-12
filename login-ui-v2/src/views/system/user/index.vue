<template>
  <div class="p-4">
    <el-row :gutter="20">
      <!-- 部门树 -->
      <el-col :lg="4" :xs="24">
        <el-card shadow="hover" class="dept-tree-card">
          <template #header>
            <span class="card-title">部门列表</span>
          </template>
          <el-input 
            v-model="deptName" 
            placeholder="请输入部门名称" 
            prefix-icon="Search" 
            clearable 
            class="mb-3"
          />
          <el-tree
            ref="deptTreeRef"
            node-key="deptId"
            :data="deptOptions"
            :props="{ label: 'deptName', children: 'children' }"
            :expand-on-click-node="false"
            :filter-node-method="filterNode"
            highlight-current
            default-expand-all
            @node-click="handleNodeClick"
          />
        </el-card>
      </el-col>
      
      <!-- 用户管理主体 -->
      <el-col :lg="20" :xs="24">
        <!-- 搜索区域 -->
        <transition name="el-fade-in">
          <div v-show="showSearch" class="mb-4">
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
                <el-form-item label="用户昵称" prop="nickName">
                  <el-input 
                    v-model="queryParams.nickName" 
                    placeholder="请输入用户昵称" 
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
                <el-form-item label="状态" prop="status">
                  <el-select v-model="queryParams.status" placeholder="用户状态" clearable>
                    <el-option label="正常" value="0" />
                    <el-option label="停用" value="1" />
                  </el-select>
                </el-form-item>
                <el-form-item label="创建时间">
                  <el-date-picker
                    v-model="dateRange"
                    value-format="YYYY-MM-DD HH:mm:ss"
                    type="daterange"
                    range-separator="-"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    :default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 1, 1, 23, 59, 59)]"
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

        <!-- 用户列表 -->
        <el-card shadow="hover">
          <template #header>
            <el-row :gutter="10" class="mb-8">
              <el-col :span="1.5">
                <el-button
                  type="primary"
                  plain
                  icon="Plus"
                  @click="handleAdd"
                  v-hasPermi="['system:user:add']"
                  >新增</el-button
                >
              </el-col>
              <el-col :span="1.5">
                <el-button
                  type="success"
                  plain
                  icon="Edit"
                  :disabled="single"
                  @click="handleUpdate"
                  >修改</el-button
                >
              </el-col>
              <el-col :span="1.5">
                <el-button 
                  type="danger" 
                  plain 
                  :disabled="multiple" 
                  icon="Delete" 
                  @click="handleDelete"
                >
                  删除
                </el-button>
              </el-col>
              <el-col :span="1.5">
                <el-dropdown>
                  <el-button plain type="info">
                    更多操作
                    <el-icon class="el-icon--right"><ArrowDown /></el-icon>
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item icon="Download" @click="importTemplate">下载模板</el-dropdown-item>
                      <el-dropdown-item icon="Top" @click="handleImport">导入数据</el-dropdown-item>
                      <el-dropdown-item icon="Download" @click="handleExport">导出数据</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </el-col>
              <div class="ml-auto">
                <el-button type="primary" plain icon="Search" @click="showSearch = !showSearch">搜索</el-button>
                <el-button type="info" plain icon="Refresh" @click="getList">刷新</el-button>
              </div>
            </el-row>
          </template>

          <el-table 
            v-loading="loading" 
            border 
            :data="userList" 
            @selection-change="handleSelectionChange"
            class="user-table"
          >
            <el-table-column type="selection" width="50" align="center" />
            <el-table-column label="用户编号" align="center" prop="userId" width="80" />
            <el-table-column 
              label="用户名称" 
              align="center" 
              prop="userName" 
              :show-overflow-tooltip="true" 
              width="120"
            />
            <el-table-column 
              label="用户昵称" 
              align="center" 
              prop="nickName" 
              :show-overflow-tooltip="true" 
              width="120"
            />
            <el-table-column 
              label="部门" 
              align="center" 
              prop="deptName" 
              :show-overflow-tooltip="true" 
              width="120"
            />
            <el-table-column label="手机号码" align="center" prop="phonenumber" width="120" />
            <el-table-column label="状态" align="center" width="80">
              <template #default="scope">
                <el-switch 
                  v-model="scope.row.status" 
                  active-value="0" 
                  inactive-value="1" 
                  @change="handleStatusChange(scope.row)"
                />
              </template>
            </el-table-column>
            <el-table-column label="创建时间" align="center" prop="createTime" width="160">
              <template #default="scope">
                <span>{{ formatTime(scope.row.createTime) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" fixed="right" width="320" align="center">
              <template #default="scope">
                <div class="operation-buttons">
                  <el-button 
                    v-if="canOperateUser(scope.row.userId)" 
                    size="small" 
                    type="primary" 
                    icon="Edit" 
                    @click="handleUpdate(scope.row)"
                  >
                    修改
                  </el-button>
                  <el-button 
                    v-if="scope.row.userId !== 1 && canOperateUser(scope.row.userId)" 
                    size="small" 
                    type="danger" 
                    icon="Delete" 
                    @click="handleDelete(scope.row)"
                  >
                    删除
                  </el-button>
                  <el-button 
                    v-if="canOperateUser(scope.row.userId)" 
                    size="small" 
                    type="warning" 
                    icon="Key" 
                    @click="handleResetPwd(scope.row)"
                  >
                    重置密码
                  </el-button>
                  <el-button 
                    v-if="canOperateUser(scope.row.userId)" 
                    size="small" 
                    type="success" 
                    icon="CircleCheck" 
                    @click="handleAuthRole(scope.row)"
                  >
                    分配角色
                  </el-button>
                  <span v-if="!canOperateUser(scope.row.userId)" class="no-permission-text">
                    仅超级管理员可操作
                  </span>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-container">
            <el-pagination
              v-show="total > 0"
              v-model:current-page="queryParams.pageNum"
              v-model:page-size="queryParams.pageSize"
              :total="total"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="getList"
              @current-change="getList"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 添加或修改用户对话框 -->
    <el-dialog 
      v-model="dialog.visible" 
      :title="dialog.title" 
      width="600px" 
      append-to-body 
      @close="closeDialog"
    >
      <el-form ref="userFormRef" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户昵称" prop="nickName">
              <el-input v-model="form.nickName" placeholder="请输入用户昵称" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="归属部门" prop="deptId">
              <el-tree-select
                v-model="form.deptId"
                :data="enabledDeptOptions"
                :props="{ value: 'id', label: 'label', children: 'children' }"
                value-key="id"
                placeholder="请选择归属部门"
                check-strictly
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="手机号码" prop="phonenumber">
              <el-input v-model="form.phonenumber" placeholder="请输入手机号码" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item v-if="form.userId == undefined" label="用户名称" prop="userName">
              <el-input v-model="form.userName" placeholder="请输入用户名称" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item v-if="form.userId == undefined" label="用户密码" prop="password">
              <el-input 
                v-model="form.password" 
                placeholder="请输入用户密码" 
                type="password" 
                maxlength="20" 
                show-password 
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户性别">
              <el-select v-model="form.sex" placeholder="请选择">
                <el-option label="男" value="0" />
                <el-option label="女" value="1" />
                <el-option label="未知" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="岗位">
              <el-select v-model="form.postIds" multiple placeholder="请选择">
                <el-option
                  v-for="item in postOptions"
                  :key="item.postId"
                  :label="item.postName"
                  :value="item.postId"
                  :disabled="item.status == 1"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色">
              <el-select v-model="form.roleIds" multiple placeholder="请选择">
                <el-option
                  v-for="item in roleOptions"
                  :key="item.roleId"
                  :label="item.roleName"
                  :value="item.roleId"
                  :disabled="item.status == 1"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 用户导入对话框 -->
    <el-dialog v-model="upload.open" title="用户导入" width="400px" append-to-body>
      <el-upload
        ref="uploadRef"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip text-center">
            <div class="el-upload__tip">
              <el-checkbox v-model="upload.updateSupport" />是否更新已经存在的用户数据
            </div>
            <span>仅允许导入xls、xlsx格式文件。</span>
            <el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;" @click="importTemplate">
              下载模板
            </el-link>
          </div>
        </template>
      </el-upload>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitFileForm">确 定</el-button>
          <el-button @click="upload.open = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, ElTree } from 'element-plus'
import { ArrowDown, UploadFilled } from '@element-plus/icons-vue'
import { getToken } from '@/utils/auth'
import { useAuthStore } from '@/stores/auth'
import { hasRole } from '@/utils/permission'
import { 
  listUser, 
  getUser, 
  addUser, 
  updateUser, 
  delUser, 
  changeUserStatus,
  deptTreeSelect,
  type UserQuery as ApiUserQuery,
  type UserVO as ApiUserVO,
  type UserForm as ApiUserForm
} from '@/api/system/user'
import { optionselect as roleOptionselect } from '@/api/system/role'
import { optionselect as postOptionselect } from '@/api/system/post'

// 路由
const router = useRouter()

// 权限相关
const authStore = useAuthStore()

// 计算属性 - 判断是否为超级管理员
const isSuperAdmin = computed(() => {
  return hasRole('admin') && authStore.userInfo?.userId === 1
})

// 判断是否可以操作指定用户
const canOperateUser = (userId: number) => {
  // 如果是超级管理员账户(userId=1)，只有超级管理员本人可以操作
  if (userId === 1) {
    return isSuperAdmin.value
  }
  // 其他用户，有相应权限即可操作
  return true
}

// 接口类型定义
interface UserQuery {
  pageNum: number
  pageSize: number
  userName?: string
  nickName?: string
  phonenumber?: string
  status?: string
  deptId?: number
}

interface UserForm {
  userId?: number
  deptId?: number
  userName: string
  nickName?: string
  password?: string
  phonenumber?: string
  email?: string
  sex?: string
  status: string
  remark?: string
  postIds: number[]
  roleIds: number[]
}

interface UserVO {
  userId: number
  userName: string
  nickName: string
  deptName: string
  phonenumber: string
  status: string
  createTime: string
}

interface DeptOption {
  id: number
  label: string
  children?: DeptOption[]
}

// 响应式数据
const loading = ref(false)
const showSearch = ref(true)
const userList = ref<UserVO[]>([])
const total = ref(0)
const single = ref(true)
const multiple = ref(true)
const ids = ref<number[]>([])
const dateRange = ref<string[]>([])
const deptName = ref('')
const deptOptions = ref<DeptOption[]>([])
const enabledDeptOptions = ref<DeptOption[]>([])
const postOptions = ref<any[]>([])
const roleOptions = ref<any[]>([])

// 表单引用
const queryFormRef = ref()
const userFormRef = ref()
const deptTreeRef = ref<InstanceType<typeof ElTree>>()
const uploadRef = ref()

// 查询参数
const queryParams = reactive<UserQuery>({
  pageNum: 1,
  pageSize: 10,
  userName: undefined,
  nickName: undefined,
  phonenumber: undefined,
  status: undefined,
  deptId: undefined
})

// 表单数据
const form = reactive<UserForm>({
  userId: undefined,
  deptId: undefined,
  userName: '',
  nickName: '',
  password: '',
  phonenumber: '',
  email: '',
  sex: '0',
  status: '0',
  remark: '',
  postIds: [],
  roleIds: []
})

// 对话框
const dialog = reactive({
  visible: false,
  title: ''
})

// 上传
const upload = reactive({
  open: false,
  title: '',
  isUploading: false,
  updateSupport: 0,
  headers: { Authorization: 'Bearer ' + getToken() },
  url: import.meta.env.VITE_APP_BASE_API + '/system/user/importData'
})

// 表单验证规则
const rules = {
  userName: [
    { required: true, message: '用户名称不能为空', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名称长度必须介于 2 和 20 之间', trigger: 'blur' }
  ],
  nickName: [
    { required: true, message: '用户昵称不能为空', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '用户密码不能为空', trigger: 'blur' },
    { min: 5, max: 20, message: '用户密码长度必须介于 5 和 20 之间', trigger: 'blur' }
  ],
  email: [
    {
      type: 'email',
      message: '请输入正确的邮箱地址',
      trigger: ['blur', 'change']
    }
  ],
  phonenumber: [
    {
      pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/,
      message: '请输入正确的手机号码',
      trigger: 'blur'
    }
  ]
}

// 方法
const formatTime = (time: string) => {
  if (!time) return ''
  return new Date(time).toLocaleString()
}

const filterNode = (value: string, data: any) => {
  if (!value) return true
  return data.label.indexOf(value) !== -1
}

const handleNodeClick = (data: any) => {
  queryParams.deptId = data.deptId
  handleQuery()
}

const getList = async () => {
  loading.value = true
  try {
    const params = {
      ...queryParams,
      ...dateRange.value.length > 0 ? {
        beginTime: dateRange.value[0],
        endTime: dateRange.value[1]
      } : {}
    }
    
    const response = await listUser(params)
    // 后端返回的数据结构是 TableDataInfo { rows: [...], total: number }
    if (response.rows) {
      userList.value = response.rows
      total.value = response.total || 0
    } else {
      // 兼容旧格式
      userList.value = response.data || []
      total.value = response.data ? response.data.length : 0
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const getDeptTree = async () => {
  try {
    const response = await deptTreeSelect()
    deptOptions.value = response.data || []
    enabledDeptOptions.value = response.data || []
  } catch (error) {
    console.error('获取部门树失败:', error)
    ElMessage.error('获取部门树失败')
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  dateRange.value = []
  queryFormRef.value?.resetFields()
  queryParams.pageNum = 1
  queryParams.deptId = undefined
  deptTreeRef.value?.setCurrentKey(null)
  handleQuery()
}

const handleSelectionChange = (selection: UserVO[]) => {
  ids.value = selection.map(item => item.userId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

const getRoleOptions = async () => {
  try {
    const response = await roleOptionselect();
    roleOptions.value = response.data || [];
  } catch (error) {
    console.error("获取角色列表失败:", error);
    roleOptions.value = [];
  }
};

/** 获取岗位列表 */
const getPostOptions = async () => {
  try {
    const response = await postOptionselect();
    postOptions.value = response.data || [];
  } catch (error) {
    console.error("获取岗位列表失败:", error);
    postOptions.value = [];
  }
};

/** 新增按钮操作 */
async function handleAdd() {
  try {
    reset();
    await getRoleOptions();
    await getPostOptions();
    dialog.visible = true;
    dialog.title = "新增用户";
  } catch (error) {
    console.error("Error in handleAdd:", error);
  }
}

/** 修改按钮操作 */
async function handleUpdate(row) {
  try {
    reset();
    const userId = row.userId || ids.value[0];
    await getRoleOptions();
    await getPostOptions();
    const response = await getUser(userId);
    Object.assign(form, response.data);
    form.postIds = response.data.postIds;
    form.roleIds = response.data.roleIds;
    dialog.visible = true;
    dialog.title = "修改用户";
  } catch (error) {
    console.error("Error in handleUpdate:", error);
  }
}

const handleDelete = async (row?: UserVO) => {
  const userIds = row?.userId ? [row.userId] : ids.value
  try {
    await ElMessageBox.confirm(
      `是否确认删除用户编号为"${userIds}"的数据项？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await delUser(userIds.join(','))
    ElMessage.success('删除成功')
    await getList()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除用户失败:', error)
      ElMessage.error('删除失败')
    } else {
      ElMessage.info('已取消删除')
    }
  }
}

const handleStatusChange = async (row: UserVO) => {
  const text = row.status === '0' ? '启用' : '停用'
  try {
    await ElMessageBox.confirm(
      `确认要"${text}""${row.userName}"用户吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await changeUserStatus(row.userId, row.status)
    ElMessage.success(`${text}成功`)
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('更改用户状态失败:', error)
      ElMessage.error(`${text}失败`)
    }
    // 恢复原状态
    row.status = row.status === '0' ? '1' : '0'
  }
}

const handleResetPwd = async (row: UserVO) => {
  try {
    const { value: password } = await ElMessageBox.prompt(
      '请输入"' + row.userName + '"的新密码',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        closeOnClickModal: false,
        inputPattern: /^.{5,20}$/,
        inputErrorMessage: '用户密码长度必须介于 5 和 20 之间'
      }
    )
    
    // 这里应该调用重置密码API
    ElMessage.success('修改成功，新密码是：' + password)
  } catch {
    ElMessage.info('已取消操作')
  }
}

const handleAuthRole = (row: UserVO) => {
  console.log('handleAuthRole called with row:', row)
  // 跳转到角色分配页面
  router.push({ name: 'UserAuthRole', params: { userId: row.userId } })
}

const handleImport = () => {
  upload.title = '用户导入'
  upload.open = true
}

const handleExport = () => {
  // 导出功能
  ElMessage.info('导出功能开发中')
}

const importTemplate = () => {
  // 下载模板
  ElMessage.info('下载模板功能开发中')
}

const submitForm = async () => {
  if (!userFormRef.value) return
  
  try {
    await userFormRef.value.validate()
    
    console.log('Submitting form with data:', JSON.stringify(form))
    
    if (form.userId) {
      await updateUser(form)
      ElMessage.success('修改成功')
    } else {
      await addUser(form)
      ElMessage.success('新增成功')
    }
    
    dialog.visible = false
    await getList()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error(form.userId ? '修改失败' : '新增失败')
  }
}

const cancel = () => {
  dialog.visible = false
  reset()
}

const reset = () => {
  Object.assign(form, {
    userId: undefined,
    deptId: undefined,
    userName: '',
    nickName: '',
    password: '',
    phonenumber: '',
    email: '',
    sex: '0',
    status: '0',
    remark: '',
    postIds: [],
    roleIds: []
  })
  
  nextTick(() => {
    userFormRef.value?.clearValidate()
  })
}

const closeDialog = () => {
  dialog.visible = false
  reset()
}

const handleFileUploadProgress = () => {
  upload.isUploading = true
}

const handleFileSuccess = (response: any) => {
  upload.open = false
  upload.isUploading = false
  uploadRef.value?.clearFiles()
  
  if (response.code === 200) {
    ElMessage.success('导入成功')
    getList()
  } else {
    ElMessage.error(response.msg || '导入失败')
  }
}

const submitFileForm = () => {
  uploadRef.value?.submit()
}

// 生命周期
onMounted(() => {
  getList()
  getDeptTree()
})
</script>

<style scoped>
.dept-tree-card {
  height: calc(100vh - 300px);
  overflow-y: auto;
}

.card-title {
  font-weight: 600;
  color: #303133;
}

.user-table {
  margin-top: 16px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.dialog-footer {
  text-align: right;
}

.el-upload__tip {
  color: #606266;
  font-size: 12px;
  margin-top: 7px;
}

.operation-buttons {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  flex-wrap: nowrap;
}

.operation-buttons .el-button {
  margin: 0;
  white-space: nowrap;
}

.no-permission-text {
  color: #909399;
  font-size: 12px;
  font-style: italic;
}
</style>