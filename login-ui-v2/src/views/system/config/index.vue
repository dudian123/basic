<template>
  <div class="p-4">
    <!-- 搜索区域 -->
    <transition name="el-fade-in">
      <div v-show="showSearch" class="mb-4">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="80px">
            <el-form-item label="参数名称" prop="configName">
              <el-input 
                v-model="queryParams.configName" 
                placeholder="请输入参数名称" 
                clearable 
                @keyup.enter="handleQuery" 
              />
            </el-form-item>
            <el-form-item label="参数键名" prop="configKey">
              <el-input 
                v-model="queryParams.configKey" 
                placeholder="请输入参数键名" 
                clearable 
                @keyup.enter="handleQuery" 
              />
            </el-form-item>
            <el-form-item label="系统内置" prop="configType">
              <el-select v-model="queryParams.configType" placeholder="系统内置" clearable>
                <el-option label="是" value="Y" />
                <el-option label="否" value="N" />
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

    <!-- 参数设置列表 -->
    <el-card shadow="hover">
      <template #header>
        <el-row :gutter="10" class="mb-2">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd">新增参数</el-button>
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
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Refresh" @click="handleRefreshCache">刷新缓存</el-button>
          </el-col>
          <div class="ml-auto">
            <el-button type="primary" plain icon="Search" @click="showSearch = !showSearch">搜索</el-button>
            <el-button type="info" plain icon="Refresh" @click="getList">刷新</el-button>
          </div>
        </el-row>
      </template>

      <el-table 
        ref="configTableRef" 
        v-loading="loading" 
        border 
        :data="configList" 
        @selection-change="handleSelectionChange"
        class="config-table"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="参数主键" prop="configId" width="120" align="center" />
        <el-table-column 
          label="参数名称" 
          prop="configName" 
          :show-overflow-tooltip="true" 
          width="150" 
        />
        <el-table-column 
          label="参数键名" 
          prop="configKey" 
          :show-overflow-tooltip="true" 
          width="150" 
        />
        <el-table-column 
          label="参数键值" 
          prop="configValue" 
          :show-overflow-tooltip="true" 
          width="150" 
        />
        <el-table-column label="系统内置" align="center" width="100">
          <template #default="scope">
            <dict-tag :options="sys_yes_no" :value="scope.row.configType" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="160">
          <template #default="scope">
            <span>{{ formatTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" :show-overflow-tooltip="true" />
        <el-table-column fixed="right" label="操作" width="200" align="center">
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
                v-if="scope.row.configType === 'N'"
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

    <!-- 添加或修改参数配置对话框 -->
    <el-dialog 
      v-model="dialog.visible" 
      :title="dialog.title" 
      width="500px" 
      append-to-body 
      @close="closeDialog"
    >
      <el-form ref="configFormRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="参数名称" prop="configName">
          <el-input v-model="form.configName" placeholder="请输入参数名称" />
        </el-form-item>
        <el-form-item label="参数键名" prop="configKey">
          <el-input v-model="form.configKey" placeholder="请输入参数键名" />
        </el-form-item>
        <el-form-item label="参数键值" prop="configValue">
          <el-input v-model="form.configValue" type="textarea" placeholder="请输入参数键值" />
        </el-form-item>
        <el-form-item label="系统内置" prop="configType">
          <el-radio-group v-model="form.configType">
            <el-radio label="Y">是</el-radio>
            <el-radio label="N">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listConfig, addConfig, updateConfig, delConfig, getConfig, refreshCache, exportConfig } from '@/api/system/config'
import type { ConfigQuery, ConfigForm, ConfigVO } from '@/api/system/config'

// 接口类型定义已从API文件导入

// 响应式数据
const loading = ref(false)
const showSearch = ref(true)
const configList = ref<ConfigVO[]>([])
const total = ref(0)
const single = ref(true)
const multiple = ref(true)
const ids = ref<number[]>([])

// 字典数据
const sys_yes_no = ref([
  { label: '是', value: 'Y', elTagType: 'primary' },
  { label: '否', value: 'N', elTagType: 'danger' }
])

// 表单引用
const queryFormRef = ref()
const configFormRef = ref()
const configTableRef = ref()

// 查询参数
const queryParams = reactive<ConfigQuery>({
  pageNum: 1,
  pageSize: 10,
  configName: undefined,
  configKey: undefined,
  configType: undefined
})

// 表单数据
const form = reactive<ConfigForm>({
  configId: undefined,
  configName: '',
  configKey: '',
  configValue: '',
  configType: 'N',
  remark: ''
})

// 对话框
const dialog = reactive({
  visible: false,
  title: ''
})

// 表单验证规则
const rules = {
  configName: [
    { required: true, message: '参数名称不能为空', trigger: 'blur' }
  ],
  configKey: [
    { required: true, message: '参数键名不能为空', trigger: 'blur' }
  ],
  configValue: [
    { required: true, message: '参数键值不能为空', trigger: 'blur' }
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
    const response = await listConfig(queryParams)
    // 处理API响应格式：{code, msg, data} 而不是 {rows, total}
    if (response.code === 200) {
      configList.value = response.data || []
      total.value = response.data?.length || 0
    } else {
      ElMessage.error(response.msg || '获取参数设置列表失败')
      configList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取参数设置列表失败:', error)
    ElMessage.error('获取参数设置列表失败')
    configList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryFormRef.value?.resetFields()
  queryParams.pageNum = 1
  handleQuery()
}

const handleSelectionChange = (selection: ConfigVO[]) => {
  ids.value = selection.map(item => item.configId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

const handleAdd = () => {
  reset()
  dialog.visible = true
  dialog.title = '添加参数'
}

const handleUpdate = async (row?: ConfigVO) => {
  reset()
  const configId = row?.configId || ids.value[0]
  
  try {
    const response = await getConfig(configId)
    Object.assign(form, response.data)
    dialog.visible = true
    dialog.title = '修改参数'
  } catch (error) {
    console.error('获取参数详情失败:', error)
    ElMessage.error('获取参数详情失败')
  }
}

const handleDelete = async (row?: ConfigVO) => {
  const configIds = row?.configId ? [row.configId] : ids.value
  try {
    await ElMessageBox.confirm(
      `是否确认删除参数编号为"${configIds}"的数据项？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await delConfig(configIds.join(','))
    ElMessage.success('删除成功')
    await getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除参数失败:', error)
      ElMessage.error('删除参数失败')
    } else {
      ElMessage.info('已取消删除')
    }
  }
}

const handleExport = async () => {
  try {
    await exportConfig(queryParams)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

const handleRefreshCache = async () => {
  try {
    await refreshCache()
    ElMessage.success('刷新缓存成功')
  } catch (error) {
    console.error('刷新缓存失败:', error)
    ElMessage.error('刷新缓存失败')
  }
}

const submitForm = async () => {
  if (!configFormRef.value) return
  
  try {
    await configFormRef.value.validate()
    
    if (form.configId) {
      await updateConfig(form)
      ElMessage.success('修改成功')
    } else {
      await addConfig(form)
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
    configId: undefined,
    configName: '',
    configKey: '',
    configValue: '',
    configType: 'N',
    remark: ''
  })
  
  nextTick(() => {
    configFormRef.value?.clearValidate()
  })
}

const closeDialog = () => {
  dialog.visible = false
  reset()
}

// 生命周期
onMounted(() => {
  getList()
})
</script>

<style scoped>
.config-table {
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
</style>