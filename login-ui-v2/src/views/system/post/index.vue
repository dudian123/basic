<template>
  <div class="p-4">
    <!-- 搜索区域 -->
    <transition name="el-fade-in">
      <div v-show="showSearch" class="mb-4">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="80px">
            <el-form-item label="岗位编码" prop="postCode">
              <el-input 
                v-model="queryParams.postCode" 
                placeholder="请输入岗位编码" 
                clearable 
                @keyup.enter="handleQuery" 
              />
            </el-form-item>
            <el-form-item label="岗位名称" prop="postName">
              <el-input 
                v-model="queryParams.postName" 
                placeholder="请输入岗位名称" 
                clearable 
                @keyup.enter="handleQuery" 
              />
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="岗位状态" clearable>
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

    <!-- 岗位列表 -->
    <el-card shadow="hover">
      <template #header>
        <el-row :gutter="10" class="mb-2">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd">新增岗位</el-button>
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
        ref="postTableRef" 
        v-loading="loading" 
        border 
        :data="postList" 
        @selection-change="handleSelectionChange"
        class="post-table"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="岗位编号" prop="postId" width="120" align="center" />
        <el-table-column 
          label="岗位编码" 
          prop="postCode" 
          :show-overflow-tooltip="true" 
          width="150" 
        />
        <el-table-column 
          label="岗位名称" 
          prop="postName" 
          :show-overflow-tooltip="true" 
          width="150" 
        />
        <el-table-column label="岗位排序" prop="postSort" width="100" align="center" />
        <el-table-column label="状态" align="center" width="100">
          <template #default="scope">
            <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="160">
          <template #default="scope">
            <span>{{ formatTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" :show-overflow-tooltip="true" />
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

    <!-- 添加或修改岗位对话框 -->
    <el-dialog 
      v-model="dialog.visible" 
      :title="dialog.title" 
      width="500px" 
      append-to-body 
      @close="closeDialog"
    >
      <el-form ref="postFormRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="岗位名称" prop="postName">
          <el-input v-model="form.postName" placeholder="请输入岗位名称" />
        </el-form-item>
        <el-form-item label="岗位编码" prop="postCode">
          <el-input v-model="form.postCode" placeholder="请输入岗位编码" />
        </el-form-item>
        <el-form-item label="岗位顺序" prop="postSort">
          <el-input-number v-model="form.postSort" controls-position="right" :min="0" />
        </el-form-item>
        <el-form-item label="岗位状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
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
import { listPost, addPost, updatePost, delPost, getPost, exportPost, type PostQuery, type PostForm, type PostVO } from '@/api/system/post'

// 接口类型定义已从API文件导入

// 响应式数据
const loading = ref(false)
const showSearch = ref(true)
const postList = ref<PostVO[]>([])
const total = ref(0)
const single = ref(true)
const multiple = ref(true)
const ids = ref<number[]>([])

// 字典数据
const sys_normal_disable = ref([
  { label: '正常', value: '0', elTagType: 'primary' },
  { label: '停用', value: '1', elTagType: 'danger' }
])

// 表单引用
const queryFormRef = ref()
const postFormRef = ref()
const postTableRef = ref()

// 查询参数
const queryParams = reactive<PostQuery>({
  pageNum: 1,
  pageSize: 10,
  postCode: undefined,
  postName: undefined,
  status: undefined
})

// 表单数据
const form = reactive<PostForm>({
  postId: undefined,
  postCode: '',
  postName: '',
  postSort: 0,
  status: '0',
  remark: ''
})

// 对话框
const dialog = reactive({
  visible: false,
  title: ''
})

// 表单验证规则
const rules = {
  postName: [
    { required: true, message: '岗位名称不能为空', trigger: 'blur' }
  ],
  postCode: [
    { required: true, message: '岗位编码不能为空', trigger: 'blur' }
  ],
  postSort: [
    { required: true, message: '岗位顺序不能为空', trigger: 'blur' }
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
    const response = await listPost(queryParams)
    // 后端返回的数据结构是 { code: 200, msg: '操作成功', data: [...] }
    postList.value = response.data || []
    total.value = response.data ? response.data.length : 0
  } catch (error) {
    console.error('获取岗位列表失败:', error)
    ElMessage.error('获取岗位列表失败')
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

const handleSelectionChange = (selection: PostVO[]) => {
  ids.value = selection.map(item => item.postId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

const handleAdd = () => {
  reset()
  dialog.visible = true
  dialog.title = '添加岗位'
}

const handleUpdate = async (row?: PostVO) => {
  reset()
  const postId = row?.postId || ids.value[0]
  
  try {
    const response = await getPost(postId)
    Object.assign(form, response.data)
    dialog.visible = true
    dialog.title = '修改岗位'
  } catch (error) {
    console.error('获取岗位详情失败:', error)
    ElMessage.error('获取岗位详情失败')
  }
}

const handleDelete = async (row?: PostVO) => {
  const postIds = row?.postId ? [row.postId] : ids.value
  try {
    await ElMessageBox.confirm(
      `是否确认删除岗位编号为"${postIds}"的数据项？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await delPost(postIds)
    ElMessage.success('删除成功')
    await getList()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除岗位失败:', error)
      ElMessage.error('删除失败')
    } else {
      ElMessage.info('已取消删除')
    }
  }
}

const handleExport = async () => {
  try {
    await exportPost(queryParams)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

const submitForm = async () => {
  if (!postFormRef.value) return
  
  try {
    await postFormRef.value.validate()
    
    if (form.postId) {
      await updatePost(form)
      ElMessage.success('修改成功')
    } else {
      await addPost(form)
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
    postId: undefined,
    postCode: '',
    postName: '',
    postSort: 0,
    status: '0',
    remark: ''
  })
  
  nextTick(() => {
    postFormRef.value?.clearValidate()
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
.post-table {
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
}
</style>