<template>
  <div class="dict-data-container">
    <!-- 搜索区域 -->
    <div class="search-form">
      <el-form :model="queryParams" ref="queryForm" :inline="true">
        <el-form-item label="字典名称" prop="dictType">
          <el-select v-model="queryParams.dictType" placeholder="请选择字典类型">
            <el-option
              v-for="item in typeOptions"
              :key="item.dictId"
              :label="item.dictName"
              :value="item.dictType"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="字典标签" prop="dictLabel">
          <el-input
            v-model="queryParams.dictLabel"
            placeholder="请输入字典标签"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="数据状态" clearable>
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
      <el-button type="primary" icon="Plus" @click="handleAdd" :disabled="!queryParams.dictType">新增</el-button>
      <el-button type="success" icon="Edit" :disabled="single" @click="handleUpdate">修改</el-button>
      <el-button type="danger" icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
      <el-button type="info" icon="Download" @click="handleExport" :disabled="!queryParams.dictType">导出</el-button>
      <el-button type="warning" icon="Close" @click="handleClose">关闭</el-button>
    </div>

    <!-- 表格数据 -->
    <el-table
      v-loading="loading"
      :data="dictDataList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="字典编码" align="center" prop="dictCode" />
      <el-table-column label="字典标签" align="center" prop="dictLabel">
        <template #default="scope">
          <span v-if="scope.row.listClass == '' || scope.row.listClass == 'default'">{{ scope.row.dictLabel }}</span>
          <el-tag v-else :type="scope.row.listClass == 'primary' ? '' : scope.row.listClass">{{ scope.row.dictLabel }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="字典键值" align="center" prop="dictValue" />
      <el-table-column label="字典排序" align="center" prop="dictSort" />
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

    <!-- 添加或修改字典数据对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="dictDataForm" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="字典类型">
          <el-input v-model="form.dictType" :disabled="true" />
        </el-form-item>
        <el-form-item label="数据标签" prop="dictLabel">
          <el-input v-model="form.dictLabel" placeholder="请输入数据标签" />
        </el-form-item>
        <el-form-item label="数据键值" prop="dictValue">
          <el-input v-model="form.dictValue" placeholder="请输入数据键值" />
        </el-form-item>
        <el-form-item label="样式属性" prop="cssClass">
          <el-input v-model="form.cssClass" placeholder="请输入样式属性" />
        </el-form-item>
        <el-form-item label="显示排序" prop="dictSort">
          <el-input-number v-model="form.dictSort" controls-position="right" :min="0" />
        </el-form-item>
        <el-form-item label="回显样式" prop="listClass">
          <el-select v-model="form.listClass">
            <el-option label="默认" value="default" />
            <el-option label="主要" value="primary" />
            <el-option label="成功" value="success" />
            <el-option label="信息" value="info" />
            <el-option label="警告" value="warning" />
            <el-option label="危险" value="danger" />
          </el-select>
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
import { ref, reactive, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  listDictData,
  getDictData,
  delDictData,
  addDictData,
  updateDictData,
  optionselectDictType,
  exportDictData,
  getDictType
} from '@/api/dict'

const route = useRoute()
const router = useRouter()

// 响应式数据
const loading = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const dictDataList = ref([])
const title = ref('')
const open = ref(false)
const typeOptions = ref([])
const defaultDictType = ref('')
const dictDataForm = ref()

// 查询参数
const queryParams = reactive({
  dictName: undefined,
  dictLabel: undefined,
  status: undefined,
  dictType: undefined,
  pageNum: 1,
  pageSize: 10
})

// 表单数据
const form = reactive({
  dictCode: undefined,
  dictLabel: undefined,
  dictValue: undefined,
  dictType: undefined,
  cssClass: undefined,
  listClass: 'default',
  dictSort: 0,
  status: '0',
  remark: undefined
})

// 表单校验规则
const rules = reactive({
  dictLabel: [{ required: true, message: '数据标签不能为空', trigger: 'blur' }],
  dictValue: [{ required: true, message: '数据键值不能为空', trigger: 'blur' }],
  dictSort: [{ required: true, message: '数据顺序不能为空', trigger: 'blur' }]
})

// 获取字典数据列表
const getList = async () => {
  loading.value = true
  try {
    const response = await listDictData(queryParams)
    if (response.code === 200) {
      if (response.rows) {
        dictDataList.value = response.rows || []
        total.value = response.total || 0
      } else {
        dictDataList.value = response.data || []
        total.value = 0
      }
    } else {
      ElMessage.error(response.msg || '获取字典数据列表失败')
      dictDataList.value = []
      total.value = 0
    }
  } catch (error) {
    ElMessage.error('获取字典数据列表失败')
    dictDataList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 获取字典类型选择框列表
const getTypeList = async () => {
  try {
    const res = await optionselectDictType()
    typeOptions.value = res.data
  } catch (error) {
    ElMessage.error('获取字典类型列表失败')
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
    } else if (key !== 'dictType') {
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
  ids.value = selection.map(item => item.dictCode)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

// 新增按钮操作
const handleAdd = () => {
  reset()
  open.value = true
  title.value = '添加字典数据'
  form.dictType = queryParams.dictType
}

// 修改按钮操作
const handleUpdate = async (row) => {
  reset()
  const dictCode = row.dictCode || ids.value[0]
  try {
    const res = await getDictData(dictCode)
    Object.assign(form, res.data)
    open.value = true
    title.value = '修改字典数据'
  } catch (error) {
    ElMessage.error('获取字典数据详情失败')
  }
}

// 提交按钮
const submitForm = () => {
  if (!dictDataForm.value) {
    ElMessage.error('表单引用未找到')
    return
  }
  dictDataForm.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.dictCode !== undefined) {
          await updateDictData(form)
          ElMessage.success('修改成功')
        } else {
          await addDictData(form)
          ElMessage.success('新增成功')
        }
        open.value = false
        getList()
      } catch (error) {
        ElMessage.error(form.dictCode !== undefined ? '修改失败' : '新增失败')
      }
    }
  })
}

// 删除按钮操作
const handleDelete = async (row) => {
  const dictCodes = row.dictCode ? [row.dictCode] : ids.value
  try {
    await ElMessageBox.confirm('是否确认删除字典编码为"' + dictCodes + '"的数据项？', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await delDictData(dictCodes)
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
    await exportDictData(queryParams)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

// 关闭按钮操作
const handleClose = () => {
  const obj = { path: '/system/dict/type' }
  router.push(obj)
}

// 取消按钮
const cancel = () => {
  open.value = false
  reset()
}

// 表单重置
const reset = () => {
  Object.keys(form).forEach(key => {
    if (key === 'status') {
      form[key] = '0'
    } else if (key === 'dictSort') {
      form[key] = 0
    } else if (key === 'listClass') {
      form[key] = 'default'
    } else {
      form[key] = undefined
    }
  })
}

onMounted(() => {
  getTypeList();
});

watch(
  () => [route.params.dictType, route.params.dictId],
  ([newDictType, newDictId]) => {
    const dictParam = newDictType || newDictId;
    if (dictParam && dictParam !== ':dictType' && dictParam !== ':dictId') {
      queryParams.dictType = dictParam;
      defaultDictType.value = dictParam;
    }
    getList();
  },
  { immediate: true }
)
</script>

<style scoped>
.dict-data-container {
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