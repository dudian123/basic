<template>
  <div class="p-4">
    <!-- 搜索区域 -->
    <transition name="el-fade-in">
      <div v-show="showSearch" class="mb-4">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="80px">
            <el-form-item label="部门名称" prop="deptName">
              <el-input 
                v-model="queryParams.deptName" 
                placeholder="请输入部门名称" 
                clearable 
                @keyup.enter="handleQuery" 
              />
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="部门状态" clearable>
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

    <!-- 部门列表 -->
    <el-card shadow="hover">
      <template #header>
        <el-row :gutter="10" class="mb-2">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd">新增部门</el-button>
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
        ref="deptTableRef"
        v-loading="loading"
        :data="deptList"
        row-key="deptId"
        :default-expand-all="isExpandAll"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        border
        class="dept-table"
      >
        <el-table-column prop="deptName" label="部门名称" width="260" />
        <el-table-column prop="orderNum" label="排序" width="200" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="200">
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
                v-if="scope.row.parentId != 0"
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

    <!-- 添加或修改部门对话框 -->
    <el-dialog 
      v-model="dialog.visible" 
      :title="dialog.title" 
      width="600px" 
      append-to-body 
      @close="closeDialog"
    >
      <el-form ref="deptFormRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24" v-if="form.parentId !== 0">
            <el-form-item label="上级部门" prop="parentId">
              <el-tree-select
                v-model="form.parentId"
                :data="deptOptions"
                :props="{ value: 'deptId', label: 'deptName', children: 'children' }"
                value-key="deptId"
                placeholder="选择上级部门"
                check-strictly
                :render-after-expand="false"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门名称" prop="deptName">
              <el-input v-model="form.deptName" placeholder="请输入部门名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示排序" prop="orderNum">
              <el-input-number v-model="form.orderNum" controls-position="right" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人" prop="leader">
              <el-input v-model="form.leader" placeholder="请输入负责人" maxlength="20" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门状态">
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
import { listDept, getDept, addDept, updateDept, delDept, listDeptExcludeChild } from '@/api/system/dept'
import type { DeptQuery, DeptVO, DeptForm } from '@/api/system/dept'

// 接口类型定义已从API文件导入

interface DeptOption {
  deptId: number
  deptName: string
  children?: DeptOption[]
}

// 响应式数据
const loading = ref(false)
const showSearch = ref(true)
const deptList = ref<DeptVO[]>([])
const deptOptions = ref<DeptOption[]>([])
const isExpandAll = ref(true)
const refreshTable = ref(true)

// 字典数据
const sys_normal_disable = ref([
  { label: '正常', value: '0', elTagType: 'primary' },
  { label: '停用', value: '1', elTagType: 'danger' }
])

// 表单引用
const queryFormRef = ref()
const deptFormRef = ref()
const deptTableRef = ref()

// 查询参数
const queryParams = reactive<DeptQuery>({
  deptName: undefined,
  status: undefined,
  pageNum: 1,
  pageSize: 10
})

// 分页数据
const total = ref(0)

// 表单数据
const form = reactive<DeptForm>({
  deptId: undefined,
  parentId: 0,
  deptName: '',
  orderNum: 0,
  leader: '',
  phone: '',
  email: '',
  status: '0'
})

// 对话框
const dialog = reactive({
  visible: false,
  title: ''
})

// 表单验证规则
const rules = {
  parentId: [
    { required: true, message: '上级部门不能为空', trigger: 'blur' }
  ],
  deptName: [
    { required: true, message: '部门名称不能为空', trigger: 'blur' }
  ],
  orderNum: [
    { required: true, message: '显示排序不能为空', trigger: 'blur' }
  ],
  email: [
    {
      type: 'email',
      message: '请输入正确的邮箱地址',
      trigger: ['blur', 'change']
    }
  ],
  phone: [
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

const getList = async () => {
  loading.value = true
  try {
    const response = await listDept(queryParams)
    if (response.rows) {
      // 分页数据
      deptList.value = response.rows || []
      total.value = response.total || 0
    } else {
      // 树形数据（无分页）
      deptList.value = response.data || []
      total.value = 0
    }
  } catch (error) {
    console.error('获取部门列表失败:', error)
    ElMessage.error('获取部门列表失败')
  } finally {
    loading.value = false
  }
}

const getTreeselect = async (excludeId?: number) => {
  try {
    let response
    if (excludeId) {
      response = await listDeptExcludeChild(excludeId)
    } else {
      response = await listDept({})
    }
    
    // 添加顶级选项
    const treeData = [
      {
        deptId: 0,
        deptName: '主类目',
        children: response.data || []
      }
    ]
    
    deptOptions.value = treeData
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

const handleAdd = async (row?: DeptVO) => {
  reset()
  await getTreeselect()
  if (row && row.deptId) {
    form.parentId = row.deptId
  } else {
    form.parentId = 0
  }
  dialog.visible = true
  dialog.title = '添加部门'
}

const handleUpdate = async (row: DeptVO) => {
  reset()
  await getTreeselect(row.deptId)
  
  try {
    const response = await getDept(row.deptId)
    Object.assign(form, response.data)
    dialog.visible = true
    dialog.title = '修改部门'
  } catch (error) {
    console.error('获取部门详情失败:', error)
    ElMessage.error('获取部门详情失败')
  }
}

const handleDelete = async (row: DeptVO) => {
  try {
    await ElMessageBox.confirm(
      `是否确认删除名称为"${row.deptName}"的数据项？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await delDept(row.deptId)
    ElMessage.success('删除成功')
    await getList()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除部门失败:', error)
      ElMessage.error('删除失败')
    } else {
      ElMessage.info('已取消删除')
    }
  }
}

const submitForm = async () => {
  if (!deptFormRef.value) return
  
  try {
    await deptFormRef.value.validate()
    
    if (form.deptId) {
      await updateDept(form)
      ElMessage.success('修改成功')
    } else {
      await addDept(form)
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
    deptId: undefined,
    parentId: 0,
    deptName: '',
    orderNum: 0,
    leader: '',
    phone: '',
    email: '',
    status: '0'
  })
  
  nextTick(() => {
    deptFormRef.value?.clearValidate()
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

// 生命周期
onMounted(() => {
  getList()
})
</script>

<style scoped>
.dept-table {
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