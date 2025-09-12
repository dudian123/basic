<template>
  <div id="app">
    <div v-if="debugMode" class="debug-info">
      <h3>调试信息</h3>
      <p>当前路由: {{ currentRoute }}</p>
      <p>登录状态: {{ isLoggedIn }}</p>
      <p>用户信息: {{ userInfo }}</p>
    </div>
    <router-view />
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// 调试模式
const debugMode = ref(false)

// 路由和认证信息
const route = useRoute()
const authStore = useAuthStore()

const currentRoute = computed(() => route.path)
const isLoggedIn = computed(() => authStore.isLoggedIn)
const userInfo = computed(() => authStore.userInfo)
</script>

<style>
#app {
  width: 100%;
  height: 100vh;
  margin: 0;
  padding: 0;
}

body {
  margin: 0;
  padding: 0;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
}

.debug-info {
  position: fixed;
  top: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 10px;
  border-radius: 5px;
  z-index: 9999;
  font-size: 12px;
  max-width: 300px;
}

.debug-info h3 {
  margin: 0 0 10px 0;
  font-size: 14px;
}

.debug-info p {
  margin: 5px 0;
  word-break: break-all;
}
</style>