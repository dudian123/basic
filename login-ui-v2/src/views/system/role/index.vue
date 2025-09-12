<template>
  <div class="p-4">
    <!-- 搜索区域 -->
    <transition name="el-fade-in">
      <div v-show="showSearch" class="mb-4">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="80px">
            <el-form-item label="角色名称" prop="roleName">
              <el-input 
                v-model="queryParams.roleName" 
                placeholder="请输入角色名称" 
                clearable 
                @keyup.enter="handleQuery" 
              />
            </el-form-item>
            <el-form-item label="权限字符" prop="roleKey">
              <el-input 
                v-model="queryParams.roleKey" 
                placeholder="请输入权限字符" 
                clearable 
                @keyup.enter="handleQuery" 
              />
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="角色状态" clearable>
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

    <!-- 角色列表 -->
    <el-card shadow="hover">
      <template #header>
        <el-row :gutter="10" class="mb-2">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd">新增角色</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button 
              type="success" 
              plain 
              :disabled="single" 
              icon="Edit" 
              @click="handleUpdate"
            >
              修改
            </el-button>
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
            <el-button type="warning" plain icon="Download" @click="handleExport">导出</el-button>
          </el-col>
          <div class="ml-auto">
            <el-button type="primary" plain icon="Search" @click="showSearch = !showSearch">搜索</el-button>
            <el-button type="info" plain icon="Refresh" @click="getList">刷新</el-button>
          </div>
        </el-row>
      </template>

      <el-table 
        ref="roleTableRef" 
        v-loading="loading" 
        border 
        :data="roleList" 
        @selection-change="handleSelectionChange"
        class="role-table"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="角色编号" prop="roleId" width="120" align="center" />
        <el-table-column 
          label="角色名称" 
          prop="roleName" 
          :show-overflow-tooltip="true" 
          width="150" 
        />
        <el-table-column 
          label="权限字符" 
          prop="roleKey" 
          :show-overflow-tooltip="true" 
          width="200" 
        />
        <el-table-column label="显示顺序" prop="roleSort" width="100" align="center" />
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
        <el-table-column label="创建时间" align="center" prop="createTime" width="160">
          <template #default="scope">
            <span>{{ formatTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" :show-overflow-tooltip="true" />
        <el-table-column fixed="right" label="操作" width="320" align="center">
          <template #default="scope">
            <div class="operation-buttons">
              <el-button 
                size="small"
                type="primary" 
                icon="Edit" 
                @click="handleUpdate(scope.row)"
              >
                修改
              </el-button>
              <el-button 
                v-if="scope.row.roleId !== 1"
                size="small"
                type="danger" 
                icon="Delete" 
                @click="handleDelete(scope.row)"
              >
                删除
              </el-button>
              <el-button 
                size="small"
                type="warning" 
                icon="CircleCheck" 
                @click="handleDataScope(scope.row)"
              >
                数据权限
              </el-button>
              <el-button 
                size="small"
                type="success" 
                icon="User" 
                @click="handleAuthUser(scope.row)"
              >
                分配用户
              </el-button>
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

    <!-- 添加或修改角色对话框 -->
    <el-dialog 
      v-model="dialog.visible" 
      :title="dialog.title" 
      width="500px" 
      append-to-body 
      @close="closeDialog"
    >
      <el-form ref="roleFormRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="权限字符" prop="roleKey">
          <el-input v-model="form.roleKey" placeholder="请输入权限字符" />
        </el-form-item>
        <el-form-item label="角色顺序" prop="roleSort">
          <el-input-number v-model="form.roleSort" controls-position="right" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单权限">
          <el-checkbox v-model="menuExpand" @change="handleCheckedTreeExpand">展开/折叠</el-checkbox>
          <el-checkbox v-model="menuNodeAll" @change="handleCheckedTreeNodeAll">全选/全不选</el-checkbox>
          <el-checkbox v-model="form.menuCheckStrictly" @change="handleCheckedTreeConnect">父子联动</el-checkbox>
          <el-tree
            ref="menuRef"
            class="tree-border"
            :data="menuOptions"
            show-checkbox
            node-key="id"
            :check-strictly="!form.menuCheckStrictly"
            empty-text="加载中，请稍候"
            :props="{ label: 'label', children: 'children' }"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 分配角色数据权限对话框 -->
    <el-dialog v-model="openDataScope" title="分配数据权限" width="500px" append-to-body>
      <el-form :model="form" label-width="80px">
        <el-form-item label="角色名称">
          <el-input v-model="form.roleName" :disabled="true" />
        </el-form-item>
        <el-form-item label="权限字符">
          <el-input v-model="form.roleKey" :disabled="true" />
        </el-form-item>
        <el-form-item label="权限范围">
          <el-select v-model="form.dataScope" @change="dataScopeSelectChange">
            <el-option
              v-for="item in dataScopeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-show="form.dataScope == 2" label="数据权限">
          <el-checkbox v-model="deptExpand" @change="handleCheckedDeptTreeExpand">展开/折叠</el-checkbox>
          <el-checkbox v-model="deptNodeAll" @change="handleCheckedDeptTreeNodeAll">全选/全不选</el-checkbox>
          <el-checkbox v-model="form.deptCheckStrictly" @change="handleCheckedTreeConnect">父子联动</el-checkbox>
          <el-tree
            ref="deptRef"
            class="tree-border"
            :data="deptOptions"
            show-checkbox
            node-key="id"
            :check-strictly="!form.deptCheckStrictly"
            empty-text="加载中，请稍候"
            :props="{ label: 'label', children: 'children' }"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitDataScope">确 定</el-button>
          <el-button @click="cancelDataScope">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, ElTree } from 'element-plus'
import { 
  listRole, 
  getRole, 
  addRole, 
  updateRole, 
  delRole, 
  changeRoleStatus,
  dataScope,
  deptTreeSelect,
  type RoleQuery as ApiRoleQuery,
  type RoleVO as ApiRoleVO,
  type RoleForm as ApiRoleForm
} from '@/api/system/role'
import { listMenu, roleMenuTreeselect } from '@/api/system/menu'

// 接口类型定义
interface RoleQuery {
  pageNum: number
  pageSize: number
  roleName?: string
  roleKey?: string
  status?: string
}

interface RoleForm {
  roleId?: number
  roleName: string
  roleKey: string
  roleSort: number
  status: string
  menuIds: number[]
  deptIds: number[]
  menuCheckStrictly: boolean
  deptCheckStrictly: boolean
  dataScope?: string
  remark?: string
}

interface RoleVO {
  roleId: number
  roleName: string
  roleKey: string
  roleSort: number
  status: string
  createTime: string
  remark?: string
}

interface MenuOption {
  id: number
  label: string
  children?: MenuOption[]
}

// 路由
const router = useRouter()

// 响应式数据
const loading = ref(false)
const showSearch = ref(true)
const roleList = ref<RoleVO[]>([])
const total = ref(0)
const single = ref(true)
const multiple = ref(true)
const ids = ref<number[]>([])
const dateRange = ref<string[]>([])
const menuExpand = ref(false)
const menuNodeAll = ref(false)
const deptExpand = ref(true)
const deptNodeAll = ref(false)
const openDataScope = ref(false)
const menuOptions = ref<MenuOption[]>([])
const deptOptions = ref<MenuOption[]>([])

// 表单引用
const queryFormRef = ref()
const roleFormRef = ref()
const roleTableRef = ref()
const menuRef = ref<InstanceType<typeof ElTree>>()
const deptRef = ref<InstanceType<typeof ElTree>>()

// 查询参数
const queryParams = reactive<RoleQuery>({
  pageNum: 1,
  pageSize: 10,
  roleName: undefined,
  roleKey: undefined,
  status: undefined
})

// 表单数据
const form = reactive<RoleForm>({
  roleId: undefined,
  roleName: '',
  roleKey: '',
  roleSort: 0,
  status: '0',
  menuIds: [],
  deptIds: [],
  menuCheckStrictly: true,
  deptCheckStrictly: true,
  dataScope: '1',
  remark: ''
})

// 对话框
const dialog = reactive({
  visible: false,
  title: ''
})

// 数据范围选项
const dataScopeOptions = ref([
  { value: '1', label: '全部数据权限' },
  { value: '2', label: '自定数据权限' },
  { value: '3', label: '本部门数据权限' },
  { value: '4', label: '本部门及以下数据权限' },
  { value: '5', label: '仅本人数据权限' }
])

// 表单验证规则
const rules = {
  roleName: [
    { required: true, message: '角色名称不能为空', trigger: 'blur' }
  ],
  roleKey: [
    { required: true, message: '权限字符不能为空', trigger: 'blur' }
  ],
  roleSort: [
    { required: true, message: '角色顺序不能为空', trigger: 'blur' }
  ]
}

// 方法
const formatTime = (time: string) => {
  if (!time) return ''
  return new Date(time).toLocaleString()
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
    
    const response = await listRole(params)
    // 后端返回的数据结构是 { total: 3, rows: [...], code: 200, msg: '查询成功' }
    roleList.value = response.rows || []
    total.value = response.total || 0
  } catch (error) {
    console.error('获取角色列表失败:', error)
    ElMessage.error('获取角色列表失败')
  } finally {
    loading.value = false
  }
}

const getMenuTreeselect = async () => {
  try {
    const response = await listMenu()
    menuOptions.value = response.data || []
  } catch (error) {
    console.error('获取菜单树失败:', error)
    ElMessage.error('获取菜单树失败')
  }
}

const getDeptTreeselect = async () => {
  try {
    const response = await deptTreeSelect(0)
    deptOptions.value = response.data || []
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
  handleQuery()
}

const handleSelectionChange = (selection: RoleVO[]) => {
  ids.value = selection.map(item => item.roleId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

const handleAdd = () => {
  reset()
  getMenuTreeselect()
  dialog.visible = true
  dialog.title = '添加角色'
}

const handleUpdate = async (row?: RoleVO) => {
  reset()
  await getMenuTreeselect()
  const roleId = row?.roleId || ids.value[0]
  
  try {
    const response = await getRole(roleId)
    Object.assign(form, response.data)
    
    dialog.visible = true
    dialog.title = '修改角色'
    
    nextTick(() => {
      // 设置选中的菜单
      const checkedKeys = response.data.menuIds || []
      menuRef.value?.setCheckedKeys(checkedKeys, false)
    })
  } catch (error) {
    console.error('获取角色详情失败:', error)
    ElMessage.error('获取角色详情失败')
  }
}

const handleDelete = async (row?: RoleVO) => {
  const roleIds = row?.roleId ? [row.roleId] : ids.value
  try {
    await ElMessageBox.confirm(
      `是否确认删除角色编号为"${roleIds}"的数据项？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await delRole(roleIds)
    ElMessage.success('删除成功')
    await getList()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除角色失败:', error)
      ElMessage.error('删除失败')
    } else {
      ElMessage.info('已取消删除')
    }
  }
}

const handleExport = () => {
  // 导出功能
  ElMessage.info('导出功能开发中')
}

const handleStatusChange = async (row: RoleVO) => {
  const text = row.status === '0' ? '启用' : '停用'
  try {
    await ElMessageBox.confirm(
      `确认要"${text}""${row.roleName}"角色吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await changeRoleStatus(row.roleId, row.status)
    ElMessage.success(`${text}成功`)
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('更改角色状态失败:', error)
      ElMessage.error(`${text}失败`)
    }
    // 恢复原状态
    row.status = row.status === '0' ? '1' : '0'
  }
}

const handleDataScope = async (row: RoleVO) => {
  reset()
  await getDeptTreeselect()
  
  try {
    const response = await getRole(row.roleId)
    Object.assign(form, response.data)
    
    openDataScope.value = true
    
    nextTick(() => {
      // 设置选中的部门
      const checkedKeys = response.data.deptIds || []
      deptRef.value?.setCheckedKeys(checkedKeys, false)
    })
  } catch (error) {
    console.error('获取角色数据权限失败:', error)
    ElMessage.error('获取角色数据权限失败')
  }
}

const handleAuthUser = (row: RoleVO) => {
  // 跳转到用户分配页面
  router.push(`/system/role-auth/user/${row.roleId}`)
}

const submitForm = async () => {
  if (!roleFormRef.value) return
  
  try {
    await roleFormRef.value.validate()
    
    // 获取选中的菜单ID
    form.menuIds = getMenuAllCheckedKeys()
    
    if (form.roleId) {
      await updateRole(form)
      ElMessage.success('修改成功')
    } else {
      await addRole(form)
      ElMessage.success('新增成功')
    }
    
    dialog.visible = false
    await getList()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

const submitDataScope = async () => {
  if (form.roleId) {
    try {
      // 获取选中的部门ID
      form.deptIds = getDeptAllCheckedKeys()
      
      await dataScope(form)
      ElMessage.success('修改成功')
      openDataScope.value = false
      await getList()
    } catch (error) {
      console.error('修改数据权限失败:', error)
      ElMessage.error('修改数据权限失败')
    }
  }
}

const cancel = () => {
  dialog.visible = false
  reset()
}

const cancelDataScope = () => {
  openDataScope.value = false
  reset()
}

const reset = () => {
  Object.assign(form, {
    roleId: undefined,
    roleName: '',
    roleKey: '',
    roleSort: 0,
    status: '0',
    menuIds: [],
    deptIds: [],
    menuCheckStrictly: true,
    deptCheckStrictly: true,
    dataScope: '1',
    remark: ''
  })
  
  nextTick(() => {
    roleFormRef.value?.clearValidate()
  })
}

const closeDialog = () => {
  dialog.visible = false
  reset()
}

// 树形控件相关方法
const getMenuAllCheckedKeys = () => {
  // 目前无法直接使用 getCheckedKeys 方法，所以返回空数组
  const checkedKeys = menuRef.value?.getCheckedKeys() || []
  const halfCheckedKeys = menuRef.value?.getHalfCheckedKeys() || []
  return checkedKeys.concat(halfCheckedKeys)
}

const getDeptAllCheckedKeys = () => {
  const checkedKeys = deptRef.value?.getCheckedKeys() || []
  const halfCheckedKeys = deptRef.value?.getHalfCheckedKeys() || []
  return checkedKeys.concat(halfCheckedKeys)
}

const handleCheckedTreeExpand = (value: boolean) => {
  if (menuOptions.value.length > 0) {
    for (let i = 0; i < menuOptions.value.length; i++) {
      if (menuRef.value?.store.nodesMap[menuOptions.value[i].id]) {
        menuRef.value.store.nodesMap[menuOptions.value[i].id].expanded = value
      }
    }
  }
}

const handleCheckedTreeNodeAll = (value: boolean) => {
  menuRef.value?.setCheckedNodes(value ? menuOptions.value : [])
}

const handleCheckedDeptTreeExpand = (value: boolean) => {
  if (deptOptions.value.length > 0) {
    for (let i = 0; i < deptOptions.value.length; i++) {
      if (deptRef.value?.store.nodesMap[deptOptions.value[i].id]) {
        deptRef.value.store.nodesMap[deptOptions.value[i].id].expanded = value
      }
    }
  }
}

const handleCheckedDeptTreeNodeAll = (value: boolean) => {
  deptRef.value?.setCheckedNodes(value ? deptOptions.value : [])
}

const handleCheckedTreeConnect = (value: boolean) => {
  form.menuCheckStrictly = value
}

const dataScopeSelectChange = (value: string) => {
  if (value !== '2') {
    deptRef.value?.setCheckedKeys([], false)
  }
}

// 生命周期
onMounted(() => {
  getList()
})
</script>

<style scoped>
.role-table {
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

.tree-border {
  margin-top: 5px;
  border: 1px solid #e5e6e7;
  background: #FFFFFF none;
  border-radius: 4px;
  width: 100%;
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
</style>