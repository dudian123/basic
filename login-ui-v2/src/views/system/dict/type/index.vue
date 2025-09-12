<template>
  <div class="dict-type-container">
    <!-- 搜索区域 -->
    <div class="search-form">
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="字典名称" prop="dictName">
          <el-input
            v-model="queryParams.dictName"
            placeholder="请输入字典名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="字典类型" prop="dictType">
          <el-input
            v-model="queryParams.dictType"
            placeholder="请输入字典类型"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="字典状态" clearable>
            <el-option label="正常" value="0" />
            <el-option label="停用" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作按钮 -->
    <div class="toolbar">
      <el-button type="primary" icon="Plus" @click="handleAdd">新增</el-button>
      <el-button type="success" icon="Edit" :disabled="single" @click="handleUpdate">修改</el-button>
      <el-button type="danger" icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
      <el-button type="info" icon="Download" @click="handleExport">导出</el-button>
      <el-button type="warning" icon="Refresh" @click="handleRefreshCache">刷新缓存</el-button>
    </div>

    <!-- 表格数据 -->
    <el-table
      v-loading="loading"
      :data="dictTypeList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="字典编号" align="center" prop="dictId" />
      <el-table-column label="字典名称" align="center" prop="dictName" :show-overflow-tooltip="true" />
      <el-table-column label="字典类型" align="center" :show-overflow-tooltip="true">
        <template #default="scope">
          <router-link :to="'/system/dict/data/' + scope.row.dictType" class="link-type">
            <span>{{ scope.row.dictType }}</span>
          </router-link>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
            {{ scope.row.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
      <el-table-column label="操作" align="center" width="200">
        <template #default="scope">
          <div class="operation-buttons">
            <el-button size="small" type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
            <el-button size="small" type="danger" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
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

    <!-- 添加或修改字典类型对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="dictTypeForm" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="字典名称" prop="dictName">
          <el-input v-model="form.dictName" placeholder="请输入字典名称" />
        </el-form-item>
        <el-form-item label="字典类型" prop="dictType">
          <el-input v-model="form.dictType" placeholder="请输入字典类型" :disabled="form.dictId !== undefined" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio value="0">正常</el-radio>
            <el-radio value="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"></el-input>
        </el-form-item>
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

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  listDictType,
  getDictType,
  delDictType,
  addDictType,
  updateDictType,
  refreshCache,
  checkDictTypeUnique,
  exportDictType
} from '@/api/dict'

// 响应式数据
const loading = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const dictTypeList = ref([])
const title = ref('')
const open = ref(false)
const dictTypeForm = ref()

// 查询参数
const queryParams = reactive({
  dictName: undefined,
  dictType: undefined,
  status: undefined,
  pageNum: 1,
  pageSize: 10
})

// 表单数据
const form = reactive({
  dictId: undefined,
  dictName: undefined,
  dictType: undefined,
  status: '0',
  remark: undefined
})

// 表单校验规则
const rules = reactive({
  dictName: [{ required: true, message: '字典名称不能为空', trigger: 'blur' }],
  dictType: [
    { required: true, message: '字典类型不能为空', trigger: 'blur' },
    {
      validator: async (rule, value, callback) => {
        if (form.dictId === undefined && value) {
          try {
            const res = await checkDictTypeUnique(value)
            if (!res.data) {
              callback(new Error('字典类型已存在'))
            } else {
              callback()
            }
          } catch (error) {
            callback(new Error('校验字典类型失败'))
          }
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

// 获取字典类型列表
const getList = async () => {
  loading.value = true
  try {
    const res = await listDictType(queryParams)
    if (res.rows) {
      dictTypeList.value = res.rows
      total.value = res.total
    } else {
      dictTypeList.value = res.data
      total.value = 0
    }
  } catch (error) {
    ElMessage.error('获取字典类型列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置搜索
const resetQuery = () => {
  Object.keys(queryParams).forEach(key => {
    if (key === 'pageNum') {
      queryParams[key] = 1
    } else if (key === 'pageSize') {
      queryParams[key] = 10
    } else {
      queryParams[key] = undefined
    }
  })
  handleQuery()
}

// 分页大小改变
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  getList()
}

// 当前页改变
const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getList()
}

// 多选框选中数据
const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.dictId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

// 新增按钮操作
const handleAdd = () => {
  reset()
  open.value = true
  title.value = '添加字典类型'
}

// 修改按钮操作
const handleUpdate = async (row) => {
  reset()
  const dictId = row.dictId || ids.value[0]
  try {
    const res = await getDictType(dictId)
    Object.assign(form, res.data)
    open.value = true
    title.value = '修改字典类型'
  } catch (error) {
    ElMessage.error('获取字典类型详情失败')
  }
}

// 提交按钮
const submitForm = () => {
  if (!dictTypeForm.value) {
    ElMessage.error('表单引用未找到')
    return
  }
  dictTypeForm.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.dictId !== undefined) {
          await updateDictType(form)
          ElMessage.success('修改成功')
        } else {
          await addDictType(form)
          ElMessage.success('新增成功')
        }
        open.value = false
        getList()
      } catch (error) {
        ElMessage.error(form.dictId !== undefined ? '修改失败' : '新增失败')
      }
    }
  })
}

// 删除按钮操作
const handleDelete = async (row) => {
  const dictIds = row.dictId ? [row.dictId] : ids.value
  try {
    await ElMessageBox.confirm('是否确认删除字典编号为"' + dictIds + '"的数据项？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await delDictType(dictIds)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 导出
const handleExport = async () => {
  try {
    ElMessage.info('正在导出数据，请稍候...')
    await exportDictType(queryParams)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

// 刷新缓存
const handleRefreshCache = async () => {
  try {
    await refreshCache()
    ElMessage.success('刷新缓存成功')
  } catch (error) {
    ElMessage.error('刷新缓存失败')
  }
}

// 取消按钮
const cancel = () => {
  open.value = false
  reset()
}

// 表单重置
const reset = () => {
  Object.keys(form).forEach(key => {
    form[key] = key === 'status' ? '0' : undefined
  })
}

// 页面加载时获取数据
onMounted(() => {
  getList()
})
</script>

<style scoped>
.dict-type-container {
  padding: 20px;
}

.search-form {
  background: #fff;
  padding: 20px;
  margin-bottom: 20px;
  border-radius: 4px;
}

.toolbar {
  margin-bottom: 20px;
}

.link-type {
  color: #409eff;
  text-decoration: none;
}

.link-type:hover {
  text-decoration: underline;
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