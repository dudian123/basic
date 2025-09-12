<template>
  <div class="p-4">
    <!-- 搜索区域 -->
    <transition name="el-fade-in">
      <div v-show="showSearch" class="mb-4">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="80px">
            <el-form-item label="公告标题" prop="noticeTitle">
              <el-input 
                v-model="queryParams.noticeTitle" 
                placeholder="请输入公告标题" 
                clearable 
                @keyup.enter="handleQuery" 
              />
            </el-form-item>
            <el-form-item label="操作人员" prop="createBy">
              <el-input 
                v-model="queryParams.createBy" 
                placeholder="请输入操作人员" 
                clearable 
                @keyup.enter="handleQuery" 
              />
            </el-form-item>
            <el-form-item label="类型" prop="noticeType">
              <el-select v-model="queryParams.noticeType" placeholder="公告类型" clearable>
                <el-option label="通知" value="1" />
                <el-option label="公告" value="2" />
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

    <!-- 通知公告列表 -->
    <el-card shadow="hover">
      <template #header>
        <el-row :gutter="10" class="mb-2">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd">新增公告</el-button>
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
          <div class="ml-auto">
            <el-button type="primary" plain icon="Search" @click="showSearch = !showSearch">搜索</el-button>
            <el-button type="info" plain icon="Refresh" @click="getList">刷新</el-button>
          </div>
        </el-row>
      </template>

      <el-table 
        ref="noticeTableRef" 
        v-loading="loading" 
        border 
        :data="noticeList" 
        @selection-change="handleSelectionChange"
        class="notice-table"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="序号" type="index" width="60" align="center" />
        <el-table-column 
          label="公告标题" 
          prop="noticeTitle" 
          :show-overflow-tooltip="true" 
          width="200" 
        />
        <el-table-column label="公告类型" align="center" width="100">
          <template #default="scope">
            <dict-tag :options="sys_notice_type" :value="scope.row.noticeType" />
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" width="100">
          <template #default="scope">
            <dict-tag :options="sys_notice_status" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="创建者" prop="createBy" width="120" align="center" />
        <el-table-column label="创建时间" align="center" prop="createTime" width="160">
          <template #default="scope">
            <span>{{ formatTime(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
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

    <!-- 添加或修改通知公告对话框 -->
    <el-dialog 
      v-model="dialog.visible" 
      :title="dialog.title" 
      width="780px" 
      append-to-body 
      @close="closeDialog"
    >
      <el-form ref="noticeFormRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="公告标题" prop="noticeTitle">
              <el-input v-model="form.noticeTitle" placeholder="请输入公告标题" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公告类型" prop="noticeType">
              <el-select v-model="form.noticeType" placeholder="请选择公告类型">
                <el-option label="通知" value="1" />
                <el-option label="公告" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">关闭</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="内容" prop="noticeContent">
              <el-input 
                v-model="form.noticeContent" 
                type="textarea" 
                placeholder="请输入内容" 
                :rows="6"
              />
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
import { listNotice, addNotice, updateNotice, delNotice, getNotice } from '@/api/system/notice'
import type { NoticeQuery, NoticeForm, NoticeVO } from '@/api/system/notice'

// 接口类型定义已从API文件导入

// 响应式数据
const loading = ref(false)
const showSearch = ref(true)
const noticeList = ref<NoticeVO[]>([])
const total = ref(0)
const single = ref(true)
const multiple = ref(true)
const ids = ref<number[]>([])

// 字典数据
const sys_notice_type = ref([
  { label: '通知', value: '1', elTagType: 'primary' },
  { label: '公告', value: '2', elTagType: 'success' }
])

const sys_notice_status = ref([
  { label: '正常', value: '0', elTagType: 'primary' },
  { label: '关闭', value: '1', elTagType: 'danger' }
])

// 表单引用
const queryFormRef = ref()
const noticeFormRef = ref()
const noticeTableRef = ref()

// 查询参数
const queryParams = reactive<NoticeQuery>({
  pageNum: 1,
  pageSize: 10,
  noticeTitle: undefined,
  createBy: undefined,
  noticeType: undefined
})

// 表单数据
const form = reactive<NoticeForm>({
  noticeId: undefined,
  noticeTitle: '',
  noticeType: '1',
  noticeContent: '',
  status: '0'
})

// 对话框
const dialog = reactive({
  visible: false,
  title: ''
})

// 表单验证规则
const rules = {
  noticeTitle: [
    { required: true, message: '公告标题不能为空', trigger: 'blur' }
  ],
  noticeType: [
    { required: true, message: '公告类型不能为空', trigger: 'change' }
  ],
  noticeContent: [
    { required: true, message: '公告内容不能为空', trigger: 'blur' }
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
    const response = await listNotice(queryParams)
    // 处理API响应格式：{code, msg, data} 而不是 {rows, total}
    if (response.code === 200) {
      noticeList.value = response.data || []
      total.value = response.data?.length || 0
    } else {
      ElMessage.error(response.msg || '获取通知公告列表失败')
      noticeList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取通知公告列表失败:', error)
    ElMessage.error('获取通知公告列表失败')
    noticeList.value = []
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

const handleSelectionChange = (selection: NoticeVO[]) => {
  ids.value = selection.map(item => item.noticeId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

const handleAdd = () => {
  reset()
  dialog.visible = true
  dialog.title = '添加公告'
}

const handleUpdate = async (row?: NoticeVO) => {
  reset()
  const noticeId = row?.noticeId || ids.value[0]
  
  try {
    const response = await getNotice(noticeId)
    Object.assign(form, response.data)
    dialog.visible = true
    dialog.title = '修改公告'
  } catch (error) {
    console.error('获取公告详情失败:', error)
    ElMessage.error('获取公告详情失败')
  }
}

const handleDelete = async (row?: NoticeVO) => {
  const noticeIds = row?.noticeId ? [row.noticeId] : ids.value
  try {
    await ElMessageBox.confirm(
      `是否确认删除公告编号为"${noticeIds}"的数据项？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await delNotice(noticeIds.join(','))
    ElMessage.success('删除成功')
    await getList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除公告失败:', error)
      ElMessage.error('删除公告失败')
    } else {
      ElMessage.info('已取消删除')
    }
  }
}

const submitForm = async () => {
  if (!noticeFormRef.value) return
  
  try {
    await noticeFormRef.value.validate()
    
    if (form.noticeId) {
      await updateNotice(form)
      ElMessage.success('修改成功')
    } else {
      await addNotice(form)
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
    noticeId: undefined,
    noticeTitle: '',
    noticeType: '1',
    noticeContent: '',
    status: '0'
  })
  
  nextTick(() => {
    noticeFormRef.value?.clearValidate()
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
.notice-table {
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