<template>
  <div class="pagination-container" v-show="total > 0">
    <el-pagination
      background
      :total="total"
      :current-page="currentPage"
      :page-size="pageSize"
      :page-sizes="pageSizes"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'

interface Props {
  total: number
  page: number
  limit: number
  pageSizes?: number[]
}

const props = withDefaults(defineProps<Props>(), {
  pageSizes: () => [10, 20, 50, 100]
})

const emit = defineEmits(['update:page', 'update:limit', 'pagination'])

const currentPage = ref<number>(props.page)
const pageSize = ref<number>(props.limit)

watch(() => props.page, (val) => {
  if (val !== currentPage.value) currentPage.value = val
})

watch(() => props.limit, (val) => {
  if (val !== pageSize.value) pageSize.value = val
})

const pageSizes = computed(() => props.pageSizes)

const handleSizeChange = (val: number) => {
  if (val !== pageSize.value) {
    pageSize.value = val
  }
  emit('update:limit', val)
  emit('update:page', 1)
  emit('pagination')
}

const handleCurrentChange = (val: number) => {
  if (val !== currentPage.value) {
    currentPage.value = val
  }
  emit('update:page', val)
  emit('pagination')
}
</script>

<style scoped>
.pagination-container {
  padding: 10px 0;
  display: flex;
  justify-content: flex-end;
}
</style>