<template>
  <div class="p-4">
    <!-- 搜索区域 -->
    <transition name="el-fade-in">
      <div v-show="showSearch" class="mb-4">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="80px">
            <el-form-item label="菜单名称" prop="menuName">
              <el-input 
                v-model="queryParams.menuName" 
                placeholder="请输入菜单名称" 
                clearable 
                @keyup.enter="handleQuery" 
              />
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="菜单状态" clearable>
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

    <!-- 菜单列表 -->
    <el-card shadow="hover">
      <template #header>
        <el-row :gutter="10" class="mb-2">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd">新增菜单</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="info" plain icon="Sort" @click="toggleExpandAll">展开/折叠</el-button>
          </el-col>
          <div class="ml-auto">
            <el-button type="primary" plain icon="Search" @click="showSearch = !showSearch">搜索</el-button>
            <el-button type="info" plain icon="Refresh" @click="getList">刷新</el-button>
          </div>
        </el-row>
      </template>

      <el-table
        ref="menuTableRef"
        v-loading="loading"
        :data="menuList"
        row-key="menuId"
        :default-expand-all="isExpandAll"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        border
        class="menu-table"
      >
        <el-table-column prop="menuName" label="菜单名称" :show-overflow-tooltip="true" width="160" />
        <el-table-column prop="icon" label="图标" align="center" width="100">
          <template #default="scope">
            <svg-icon v-if="scope.row.icon" :icon-class="scope.row.icon" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderNum" label="排序" width="80" align="center" />
        <el-table-column prop="perms" label="权限标识" :show-overflow-tooltip="true" width="160" />
        <el-table-column prop="component" label="组件路径" :show-overflow-tooltip="true" width="200" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="scope">
            <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="160">
          <template #default="scope">
            <span>{{ formatTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="240" align="center">
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
                size="small" 
                type="success" 
                icon="Plus" 
                @click="handleAdd(scope.row)"
              >
                新增
              </el-button>
              <el-button 
                size="small" 
                type="danger" 
                icon="Delete" 
                @click="handleDelete(scope.row)"
              >
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <el-pagination
        v-show="total > 0"
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        class="pagination-container"
      />
    </el-card>

    <!-- 添加或修改菜单对话框 -->
    <el-dialog 
      v-model="dialog.visible" 
      :title="dialog.title" 
      width="680px" 
      append-to-body 
      @close="closeDialog"
    >
      <el-form ref="menuFormRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="上级菜单">
              <el-tree-select
                v-model="form.parentId"
                :data="menuOptions"
                :props="{ value: 'id', label: 'name', children: 'children' }"
                value-key="id"
                placeholder="选择上级菜单"
                check-strictly
                :render-after-expand="false"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="菜单类型" prop="menuType">
              <el-radio-group v-model="form.menuType">
                <el-radio label="M">目录</el-radio>
                <el-radio label="C">菜单</el-radio>
                <el-radio label="F">按钮</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="form.menuType != 'F'">
            <el-form-item label="菜单图标" prop="icon">
              <el-popover
                placement="bottom-start"
                :width="540"
                v-model:visible="showChooseIcon"
                trigger="click"
                @show="showSelectIcon"
              >
                <template #reference>
                  <el-input v-model="form.icon" placeholder="点击选择图标" readonly>
                    <template #prefix>
                      <svg-icon
                        v-if="form.icon"
                        :icon-class="form.icon"
                        class="el-input__icon"
                        style="height: 32px;width: 16px;"
                      />
                      <el-icon v-else style="height: 32px;width: 16px;"><search /></el-icon>
                    </template>
                  </el-input>
                </template>
                <icon-select ref="iconSelectRef" @selected="selected" />
              </el-popover>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单名称" prop="menuName">
              <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示排序" prop="orderNum">
              <el-input-number v-model="form.orderNum" controls-position="right" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType != 'F'">
            <el-form-item>
              <template #label>
                <span>
                  <el-tooltip content="选择是外链则路由地址需要以`http(s)://`开头" placement="top">
                    <el-icon><question-filled /></el-icon>
                  </el-tooltip>
                  是否外链
                </span>
              </template>
              <el-radio-group v-model="form.isFrame">
                <el-radio label="0">是</el-radio>
                <el-radio label="1">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType != 'F'">
            <el-form-item prop="path">
              <template #label>
                <span>
                  <el-tooltip content="访问的路由地址，如：`user`，如外网地址需内链访问则以`http(s)://`开头" placement="top">
                    <el-icon><question-filled /></el-icon>
                  </el-tooltip>
                  路由地址
                </span>
              </template>
              <el-input v-model="form.path" placeholder="请输入路由地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType == 'C'">
            <el-form-item prop="component">
              <template #label>
                <span>
                  <el-tooltip content="访问的组件路径，如：`system/user/index`，默认在`views`目录下" placement="top">
                    <el-icon><question-filled /></el-icon>
                  </el-tooltip>
                  组件路径
                </span>
              </template>
              <el-input v-model="form.component" placeholder="请输入组件路径" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType != 'M'">
            <el-form-item>
              <el-input v-model="form.perms" placeholder="请输入权限标识" maxlength="100" />
              <template #label>
                <span>
                  <el-tooltip content="控制器中定义的权限字符，如：@PreAuthorize(`@ss.hasPermi('system:user:list')`)" placement="top">
                    <el-icon><question-filled /></el-icon>
                  </el-tooltip>
                  权限字符
                </span>
              </template>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType == 'C'">
            <el-form-item>
              <el-input v-model="form.query" placeholder="请输入路由参数" maxlength="255" />
              <template #label>
                <span>
                  <el-tooltip content='访问路由的默认传递参数，如：`{"id": 1, "name": "ry"}`' placement="top">
                    <el-icon><question-filled /></el-icon>
                  </el-tooltip>
                  路由参数
                </span>
              </template>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType == 'C'">
            <el-form-item>
              <template #label>
                <span>
                  <el-tooltip content="选择是则会被`keep-alive`缓存，需要匹配组件的`name`和地址保持一致" placement="top">
                    <el-icon><question-filled /></el-icon>
                  </el-tooltip>
                  是否缓存
                </span>
              </template>
              <el-radio-group v-model="form.isCache">
                <el-radio label="0">缓存</el-radio>
                <el-radio label="1">不缓存</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType != 'F'">
            <el-form-item>
              <template #label>
                <span>
                  <el-tooltip content="选择隐藏则路由将不会出现在侧边栏，但仍然可以访问" placement="top">
                    <el-icon><question-filled /></el-icon>
                  </el-tooltip>
                  显示状态
                </span>
              </template>
              <el-radio-group v-model="form.visible">
                <el-radio label="0">显示</el-radio>
                <el-radio label="1">隐藏</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item>
              <template #label>
                <span>
                  <el-tooltip content="选择停用则路由将不会出现在侧边栏，也不能被访问" placement="top">
                    <el-icon><question-filled /></el-icon>
                  </el-tooltip>
                  菜单状态
                </span>
              </template>
              <el-radio-group v-model="form.status">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">停用</el-radio>
              </el-radio-group>
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, QuestionFilled } from '@element-plus/icons-vue'
import { listMenu, getMenu, addMenu, updateMenu, delMenu, treeselect, type MenuVO, type MenuQuery, type MenuForm } from '@/api/system/menu'

// 接口类型定义

interface MenuOption {
  menuId: number
  menuName: string
  children?: MenuOption[]
}

// 响应式数据
const loading = ref(false)
const showSearch = ref(true)
const menuList = ref<MenuVO[]>([])
const menuOptions = ref<MenuOption[]>([])
const isExpandAll = ref(false)
const refreshTable = ref(true)
const showChooseIcon = ref(false)

// 字典数据
const sys_normal_disable = ref([
  { label: '正常', value: '0', elTagType: 'primary' },
  { label: '停用', value: '1', elTagType: 'danger' }
])

// 表单引用
const queryFormRef = ref()
const menuFormRef = ref()
const menuTableRef = ref()
const iconSelectRef = ref()

// 查询参数
const queryParams = reactive({
  menuName: undefined as string | undefined,
  status: undefined as string | undefined,
  pageNum: 1,
  pageSize: 10
})

// 分页数据
const total = ref(0)

// 表单数据
const form = reactive({
  menuId: undefined as number | undefined,
  parentId: 0,
  menuName: '',
  orderNum: 0,
  path: '',
  component: '',
  query: '',
  isFrame: '1',
  isCache: '0',
  menuType: 'M',
  visible: '0',
  status: '0',
  perms: '',
  icon: '',
  remark: ''
})

// 对话框
const dialog = reactive({
  visible: false,
  title: ''
})

// 表单验证规则
const rules = {
  menuName: [
    { required: true, message: '菜单名称不能为空', trigger: 'blur' }
  ],
  orderNum: [
    { required: true, message: '菜单顺序不能为空', trigger: 'blur' }
  ],
  path: [
    { required: true, message: '路由地址不能为空', trigger: 'blur' }
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
    const response = await listMenu(queryParams)
    if (response.data && response.data.rows) {
      // 分页数据
      menuList.value = response.data.rows || []
      total.value = response.data.total || 0
    } else if (response.data) {
      // 树形数据（无分页）
      menuList.value = response.data || []
      total.value = 0
    } else {
      menuList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取菜单列表失败:', error)
    ElMessage.error('获取菜单列表失败')
  } finally {
    loading.value = false
  }
}


const getTreeselect = async () => {
  try {
    const response = await treeselect()
    menuOptions.value = response.data || []
  } catch (error) {
    console.error('获取菜单树失败:', error)
    ElMessage.error('获取菜单树失败')
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryFormRef.value?.resetFields()
  handleQuery()
}

// 分页处理
const handleSizeChange = (val: number) => {
  queryParams.pageSize = val
  getList()
}

const handleCurrentChange = (val: number) => {
  queryParams.pageNum = val
  getList()
}

const handleAdd = async (row?: MenuVO) => {
  reset()
  await getTreeselect()
  if (row && row.menuId) {
    form.parentId = row.menuId
  } else {
    form.parentId = 0
  }
  dialog.visible = true
  dialog.title = '添加菜单'
}

const handleUpdate = async (row: MenuVO) => {
  reset()
  await getTreeselect()
  
  try {
    const response = await getMenu(row.menuId!)
    Object.assign(form, response.data)
    dialog.visible = true
    dialog.title = '修改菜单'
  } catch (error) {
    console.error('获取菜单详情失败:', error)
    ElMessage.error('获取菜单详情失败')
  }
}

const handleDelete = async (row: MenuVO) => {
  try {
    await ElMessageBox.confirm(
      `是否确认删除名称为"${row.menuName}"的数据项？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await delMenu(row.menuId!)
    ElMessage.success('删除成功')
    await getList()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除菜单失败:', error)
      ElMessage.error('删除失败')
    } else {
      ElMessage.info('已取消删除')
    }
  }
}

const submitForm = async () => {
  if (!menuFormRef.value) return
  
  try {
    await menuFormRef.value.validate()
    
    if (form.menuId) {
      await updateMenu(form)
      ElMessage.success('修改成功')
    } else {
      await addMenu(form)
      ElMessage.success('新增成功')
    }
    
    dialog.visible = false
    await getList()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

const cancel = () => {
  dialog.visible = false
  reset()
}

const reset = () => {
  Object.assign(form, {
    menuId: undefined,
    parentId: 0,
    menuName: '',
    orderNum: 0,
    path: '',
    component: '',
    query: '',
    isFrame: '1',
    isCache: '0',
    menuType: 'M',
    visible: '0',
    status: '0',
    perms: '',
    icon: '',
    remark: ''
  })
  
  nextTick(() => {
    menuFormRef.value?.clearValidate()
  })
}

const closeDialog = () => {
  dialog.visible = false
  reset()
}

const toggleExpandAll = () => {
  refreshTable.value = false
  isExpandAll.value = !isExpandAll.value
  nextTick(() => {
    refreshTable.value = true
  })
}

// 图标选择相关
const showSelectIcon = () => {
  iconSelectRef.value?.reset()
}

const selected = (name: string) => {
  form.icon = name
  showChooseIcon.value = false
}

// 生命周期
onMounted(() => {
  getList()
})
</script>

<style scoped>
.menu-table {
  margin-top: 16px;
}

.dialog-footer {
  text-align: right;
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
  min-width: 60px;
  text-align: center;
}

.pagination-container {
  margin-top: 20px;
  text-align: center;
}
</style>