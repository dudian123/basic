<template>
  <div class="p-4">
    <!-- 搜索区域 -->
    <transition name="el-fade-in">
      <div v-show="showSearch" class="mb-4">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true" label-width="80px">
            <el-form-item label="文件名" prop="fileName">
              <el-input 
                v-model="queryParams.fileName" 
                placeholder="请输入文件名" 
                clearable 
                @keyup.enter="handleQuery" 
              />
            </el-form-item>
            <el-form-item label="文件类型" prop="fileType">
              <el-select v-model="queryParams.fileType" placeholder="文件类型" clearable>
                <el-option label="图片" value="image" />
                <el-option label="文档" value="document" />
                <el-option label="视频" value="video" />
                <el-option label="音频" value="audio" />
              </el-select>
            </el-form-item>
            <el-form-item label="上传时间">
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="-"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="YYYY-MM-DD"
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

    <!-- 文件列表 -->
    <el-card shadow="hover">
      <template #header>
        <el-row :gutter="10" class="mb-2">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Upload" @click="handleUpload">上传文件</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
          </el-col>
          <div class="ml-auto">
            <el-button type="primary" plain icon="Search" @click="showSearch = !showSearch">搜索</el-button>
            <el-button type="info" plain icon="Refresh" @click="getList">刷新</el-button>
          </div>
        </el-row>
      </template>

      <el-table 
        v-loading="loading" 
        :data="fileList" 
        @selection-change="handleSelectionChange"
        border
        stripe
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="文件预览" width="100" align="center">
          <template #default="scope">
            <el-image
              v-if="scope.row.fileType === 'image'"
              :src="scope.row.url"
              :preview-src-list="[scope.row.url]"
              fit="cover"
              style="width: 60px; height: 60px"
            />
            <el-icon v-else size="40">
              <Document v-if="scope.row.fileType === 'document'" />
              <VideoPlay v-else-if="scope.row.fileType === 'video'" />
              <Headset v-else-if="scope.row.fileType === 'audio'" />
              <Files v-else />
            </el-icon>
          </template>
        </el-table-column>
        <el-table-column label="文件名" prop="fileName" show-overflow-tooltip />
        <el-table-column label="文件大小" prop="fileSize" width="120">
          <template #default="scope">
            {{ formatFileSize(scope.row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column label="文件类型" prop="fileType" width="100" />
        <el-table-column label="存储位置" prop="service" width="120" />
        <el-table-column label="上传时间" prop="createTime" width="180" />
        <el-table-column label="操作" align="center" width="200">
          <template #default="scope">
            <el-button type="primary" link icon="View" @click="handlePreview(scope.row)">预览</el-button>
            <el-button type="primary" link icon="Download" @click="handleDownload(scope.row)">下载</el-button>
            <el-button type="primary" link icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-card>

    <!-- 上传文件对话框 -->
    <el-dialog title="上传文件" v-model="uploadOpen" width="600px" append-to-body>
      <el-upload
        ref="uploadRef"
        :action="uploadUrl"
        :headers="uploadHeaders"
        :file-list="uploadFileList"
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
        :before-upload="beforeUpload"
        multiple
        drag
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            支持jpg/png/gif/pdf/doc/docx/xls/xlsx等格式，单个文件大小不超过10MB
          </div>
        </template>
      </el-upload>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="uploadOpen = false">取 消</el-button>
          <el-button type="primary" @click="submitUpload">确 定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 文件预览对话框 -->
    <el-dialog title="文件预览" v-model="previewOpen" width="800px" append-to-body>
      <div class="preview-container">
        <el-image
          v-if="previewFile.fileType === 'image'"
          :src="previewFile.url"
          fit="contain"
          style="width: 100%; max-height: 500px"
        />
        <iframe
          v-else-if="previewFile.fileType === 'document'"
          :src="previewFile.url"
          style="width: 100%; height: 500px; border: none"
        />
        <video
          v-else-if="previewFile.fileType === 'video'"
          :src="previewFile.url"
          controls
          style="width: 100%; max-height: 500px"
        />
        <audio
          v-else-if="previewFile.fileType === 'audio'"
          :src="previewFile.url"
          controls
          style="width: 100%"
        />
        <div v-else class="text-center p-4">
          <el-icon size="60"><Files /></el-icon>
          <p class="mt-2">该文件类型不支持预览</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, VideoPlay, Headset, Files, UploadFilled } from '@element-plus/icons-vue'

// 响应式数据
const loading = ref(true)
const showSearch = ref(true)
const fileList = ref([])
const uploadOpen = ref(false)
const previewOpen = ref(false)
const multiple = ref(true)
const total = ref(0)
const ids = ref([])
const dateRange = ref([])
const uploadFileList = ref([])
const uploadUrl = ref('/api/oss/upload')
const uploadHeaders = ref({})

// 预览文件信息
const previewFile = ref({
  fileName: '',
  fileType: '',
  url: ''
})

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  fileName: '',
  fileType: ''
})

// 获取文件列表
const getList = () => {
  loading.value = true
  // 模拟数据
  setTimeout(() => {
    fileList.value = [
      {
        id: 1,
        fileName: 'avatar.jpg',
        fileSize: 102400,
        fileType: 'image',
        service: '阿里云OSS',
        url: 'https://via.placeholder.com/300x200',
        createTime: '2024-01-01 10:00:00'
      },
      {
        id: 2,
        fileName: 'document.pdf',
        fileSize: 2048000,
        fileType: 'document',
        service: '本地存储',
        url: '#',
        createTime: '2024-01-01 11:00:00'
      },
      {
        id: 3,
        fileName: 'video.mp4',
        fileSize: 10485760,
        fileType: 'video',
        service: '腾讯云COS',
        url: '#',
        createTime: '2024-01-01 12:00:00'
      }
    ]
    total.value = 3
    loading.value = false
  }, 500)
}

// 格式化文件大小
const formatFileSize = (size) => {
  if (size < 1024) {
    return size + ' B'
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(2) + ' KB'
  } else if (size < 1024 * 1024 * 1024) {
    return (size / (1024 * 1024)).toFixed(2) + ' MB'
  } else {
    return (size / (1024 * 1024 * 1024)).toFixed(2) + ' GB'
  }
}

// 搜索
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置搜索
const resetQuery = () => {
  Object.assign(queryParams, {
    pageNum: 1,
    pageSize: 10,
    fileName: '',
    fileType: ''
  })
  dateRange.value = []
  getList()
}

// 上传文件
const handleUpload = () => {
  uploadFileList.value = []
  uploadOpen.value = true
}

// 预览文件
const handlePreview = (row) => {
  previewFile.value = {
    fileName: row.fileName,
    fileType: row.fileType,
    url: row.url
  }
  previewOpen.value = true
}

// 下载文件
const handleDownload = (row) => {
  // 这里应该调用下载API
  ElMessage.success('开始下载文件')
}

// 删除文件
const handleDelete = (row) => {
  const fileIds = row?.id ? [row.id] : ids.value
  ElMessageBox.confirm(`是否确认删除文件"${row?.fileName || '选中的文件'}"？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 这里应该调用删除API
    ElMessage.success('删除成功')
    getList()
  })
}

// 多选框选中数据
const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.id)
  multiple.value = !selection.length
}

// 上传前检查
const beforeUpload = (file) => {
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    ElMessage.error('上传文件大小不能超过 10MB!')
  }
  return isLt10M
}

// 上传成功
const handleUploadSuccess = (response, file) => {
  ElMessage.success('上传成功')
  getList()
}

// 上传失败
const handleUploadError = (error, file) => {
  ElMessage.error('上传失败')
}

// 提交上传
const submitUpload = () => {
  uploadOpen.value = false
  getList()
}

// 组件挂载时获取数据
onMounted(() => {
  getList()
})
</script>

<style scoped>
.ml-auto {
  margin-left: auto;
}

.preview-container {
  text-align: center;
}

.text-center {
  text-align: center;
}

.p-4 {
  padding: 1rem;
}

.mt-2 {
  margin-top: 0.5rem;
}
</style>