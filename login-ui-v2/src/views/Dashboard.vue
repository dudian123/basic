<template>
  <div class="dashboard-container">
    <!-- 头部导航 -->
    <div class="dashboard-header">
      <div class="header-left">
        <el-button 
          type="text" 
          @click="toggleSidebar" 
          class="sidebar-toggle"
        >
          <el-icon><Menu /></el-icon>
        </el-button>
        <h2>RuoYi管理系统</h2>
      </div>
      <div class="header-right">
        <el-dropdown @command="handleCommand">
          <span class="el-dropdown-link">
            <el-avatar :size="32" :src="userAvatar">
              <el-icon><User /></el-icon>
            </el-avatar>
            <span class="username">{{ userInfo?.username || '用户' }}</span>
            <el-icon class="el-icon--right"><arrow-down /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon><User /></el-icon>
                个人中心
              </el-dropdown-item>
              <el-dropdown-item command="settings">
                <el-icon><Setting /></el-icon>
                系统设置
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <!-- 主要布局 -->
    <div class="dashboard-main">
      <!-- 侧边栏 -->
      <div class="sidebar" :class="{ collapsed: sidebarCollapsed }">
        <el-menu
          :default-active="activeMenu"
          :collapse="sidebarCollapsed"
          :unique-opened="true"
          router
          class="sidebar-menu"
        >
          <el-menu-item index="/dashboard" @click="showWelcome">
            <el-icon><House /></el-icon>
            <template #title>首页</template>
          </el-menu-item>
          
          <!-- 动态菜单 -->
          <template v-for="menu in menuTree" :key="menu.name">
            <!-- 有子菜单的目录 -->
            <el-sub-menu v-if="menu.children && menu.children.length > 0" :index="menu.path">
              <template #title>
                <el-icon><component :is="getIconComponent(menu.meta?.icon)" /></el-icon>
                <span>{{ menu.meta?.title || menu.name }}</span>
              </template>
              
              <!-- 子菜单 -->
              <template v-for="subMenu in menu.children" :key="subMenu.name">
                <!-- 子目录 -->
                <el-sub-menu v-if="subMenu.children && subMenu.children.length > 0" :index="subMenu.path">
                  <template #title>
                    <el-icon><component :is="getIconComponent(subMenu.meta?.icon)" /></el-icon>
                    <span>{{ subMenu.meta?.title || subMenu.name }}</span>
                  </template>
                  
                  <!-- 三级菜单 -->
                  <el-menu-item v-for="thirdMenu in subMenu.children" :key="thirdMenu.name" :index="thirdMenu.path" @click="handleMenuClick(thirdMenu, menu.path + '/' + subMenu.path)">
                    <el-icon><component :is="getIconComponent(thirdMenu.meta?.icon)" /></el-icon>
                    <template #title>{{ thirdMenu.meta?.title || thirdMenu.name }}</template>
                  </el-menu-item>
                </el-sub-menu>
                
                <!-- 直接菜单项 -->
                <el-menu-item v-else :index="subMenu.path.startsWith('/') ? subMenu.path : menu.path + '/' + subMenu.path" @click="handleMenuClick(subMenu, menu.path)">
                  <el-icon><component :is="getIconComponent(subMenu.meta?.icon)" /></el-icon>
                  <template #title>{{ subMenu.meta?.title || subMenu.name }}</template>
                </el-menu-item>
              </template>
            </el-sub-menu>
            
            <!-- 直接菜单项（无子菜单） -->
            <el-menu-item v-else-if="!menu.hidden" :index="menu.path" @click="handleMenuClick(menu)">
              <el-icon><component :is="getIconComponent(menu.meta?.icon)" /></el-icon>
              <template #title>{{ menu.meta?.title || menu.name }}</template>
            </el-menu-item>
          </template>
        </el-menu>
      </div>

      <!-- 内容区域 -->
      <div class="content-area">
        <!-- 欢迎页面 -->
        <div v-if="showWelcomePage" class="welcome-content">
          <div class="welcome-card">
            <h3>欢迎使用RuoYi管理系统</h3>
            <p>当前登录用户：<strong>{{ userInfo?.username }}</strong></p>
            <p>登录时间：<strong>{{ loginTime }}</strong></p>
            
            <el-divider />
            
            <div class="system-info">
              <h4>系统信息</h4>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="系统版本">RuoYi v1.0.0</el-descriptions-item>
                <el-descriptions-item label="前端框架">Vue 3 + Element Plus</el-descriptions-item>
                <el-descriptions-item label="后端框架">Spring Boot</el-descriptions-item>
                <el-descriptions-item label="数据库">MySQL</el-descriptions-item>
                <el-descriptions-item label="缓存">Redis</el-descriptions-item>
                <el-descriptions-item label="权限认证">Sa-Token</el-descriptions-item>
              </el-descriptions>
            </div>
            
            <el-divider />
            
            <div class="quick-actions">
              <h4>快速操作</h4>
              <el-space wrap>
                <el-button type="primary" @click="$router.push('/system/dict/type')">
                  <el-icon><Collection /></el-icon>
                  字典类型管理
                </el-button>
                <el-button type="success" @click="$router.push('/system/dict/data')">
                  <el-icon><DocumentCopy /></el-icon>
                  字典数据管理
                </el-button>
                <el-button type="warning" @click="handleSettings">
                  <el-icon><Setting /></el-icon>
                  系统设置
                </el-button>
                <el-button type="danger" @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-button>
              </el-space>
            </div>
          </div>
        </div>
        
        <!-- 路由视图 -->
        <div v-else class="router-view-container">
          <router-view :key="$route.fullPath" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  User,
  Setting,
  SwitchButton,
  ArrowDown,
  Refresh,
  Menu,
  House,
  Document,
  Collection,
  DocumentCopy,
  UserFilled,
  Grid,
  Monitor,
  Tools,
  Guide,
  Star,
  TrendCharts,
  List,
  Share,
  Postcard,
  Edit,
  Message,
  Upload,
  Link,
  Connection,
  Coin,
  Odometer,
  Timer,
  Key
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { getIconComponent } from '@/utils/menu'

const router = useRouter()
const authStore = useAuthStore()

// 响应式数据
const loginTime = ref('')
const userAvatar = ref('')
const sidebarCollapsed = ref(false)
const showWelcomePage = ref(true)

// 计算属性
const userInfo = computed(() => authStore.userInfo)
const menuTree = computed(() => {
  console.log('Dashboard中的菜单树:', authStore.menuTree)
  return authStore.menuTree
})
const activeMenu = computed(() => {
  return router.currentRoute.value.path
})

// 处理下拉菜单命令
const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      handleProfile()
      break
    case 'settings':
      handleSettings()
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 切换侧边栏
const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

// 显示欢迎页面
const showWelcome = () => {
  showWelcomePage.value = true
  router.push('/dashboard')
}

// 刷新页面
const handleRefresh = () => {
  window.location.reload()
}

// 个人中心
const handleProfile = () => {
  ElMessage.info('个人中心功能开发中...')
}

// 系统设置
const handleSettings = () => {
  ElMessage.info('系统设置功能开发中...')
}

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm(
      '确定注销并退出系统吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 执行退出登录
    await authStore.logoutAction()
    
    // 跳转到登录页
    router.push('/login')
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('退出登录失败:', error)
    }
  }
}

// 格式化时间
const formatTime = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 监听路由变化
router.afterEach((to) => {
  // 如果不是dashboard首页，隐藏欢迎页面
  if (to.path !== '/dashboard') {
    showWelcomePage.value = false
  } else {
    showWelcomePage.value = true
  }
})

// 处理菜单点击
const handleMenuClick = (menu, parentPath = '') => {
  showWelcomePage.value = false
  
  // 如果是外链（以http开头）
  if (menu.path && (menu.path.startsWith('http://') || menu.path.startsWith('https://'))) {
    window.open(menu.path, '_blank')
    return
  }
  
  // 如果有link属性且是外链
  if (menu.meta?.link) {
    window.open(menu.meta.link, '_blank')
    return
  }
  
  // 内部路由跳转
  if (menu.path && menu.path !== '#') {
    let fullPath = menu.path
    
    // 处理包含动态参数的路径
    if (fullPath.includes(':userId')) {
      // 分配角色菜单，重定向到用户管理页面
      ElMessage.warning('请从用户管理页面选择具体用户后进行角色分配')
      router.push('/system/user')
      return
    }
    
    if (fullPath.includes(':roleId')) {
      // 分配用户菜单，重定向到角色管理页面
      ElMessage.warning('请从角色管理页面选择具体角色后进行用户分配')
      router.push('/system/role')
      return
    }
    
    // 如果是相对路径，需要拼接父路径
    if (!menu.path.startsWith('/') && parentPath) {
      fullPath = parentPath + '/' + menu.path
    }
    router.push(fullPath)
  }
}

// 组件挂载时初始化
onMounted(async () => {
  // 设置登录时间
  loginTime.value = formatTime(new Date())
  
  // 检查用户是否已登录
  if (!authStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  // 如果没有菜单数据，尝试获取
  if (authStore.menuTree.length === 0) {
    try {
      await authStore.fetchMenus()
    } catch (error) {
      console.error('获取菜单失败:', error)
    }
  }
  
  // 初始化页面状态
  if (router.currentRoute.value.path !== '/dashboard') {
    showWelcomePage.value = false
  }
})
</script>

<style lang="css" scoped>
.dashboard-container {
  min-height: 100vh;
  background: #f0f2f5;
  display: flex;
  flex-direction: column;
}

.dashboard-header {
  background: #fff;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 64px;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-left {
  display: flex;
  align-items: center;
}

.sidebar-toggle {
  margin-right: 16px;
  font-size: 18px;
}

.header-left h2 {
  margin: 0;
  color: #2c3e50;
  font-size: 20px;
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #606266;
  font-size: 14px;
}

.username {
  margin: 0 8px;
  font-weight: 500;
}

.dashboard-main {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.sidebar {
  width: 200px;
  background: #fff;
  box-shadow: 2px 0 6px rgba(0,21,41,.08);
  transition: width 0.3s;
  overflow-y: auto;
  overflow-x: hidden;
  position: fixed;
  top: 64px;
  left: 0;
  bottom: 0;
  z-index: 999;
  /* 自定义滚动条样式 */
  scrollbar-width: thin;
  scrollbar-color: #c1c1c1 transparent;
}

.sidebar::-webkit-scrollbar {
  width: 6px;
}

.sidebar::-webkit-scrollbar-track {
  background: transparent;
}

.sidebar::-webkit-scrollbar-thumb {
  background-color: #c1c1c1;
  border-radius: 3px;
}

.sidebar::-webkit-scrollbar-thumb:hover {
  background-color: #a8a8a8;
}

.sidebar.collapsed {
  width: 64px;
}

.sidebar.collapsed + .content-area {
  margin-left: 64px;
}

.sidebar-menu {
  border-right: none;
  height: 100%;
  min-height: calc(100vh - 64px);
}

.content-area {
  flex: 1;
  overflow: hidden;
  background: #f0f2f5;
  margin-left: 200px;
  transition: margin-left 0.3s;
  height: calc(100vh - 64px);
}

.welcome-content {
  padding: 20px;
  height: 100%;
  overflow-y: auto;
}

.router-view-container {
  height: 100%;
  background: #f0f2f5;
  overflow-y: auto;
  padding: 0;
}

.welcome-card {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
}

.welcome-card h3 {
  margin: 0 0 16px 0;
  color: #2c3e50;
  font-size: 24px;
  font-weight: 600;
}

.welcome-card p {
  margin: 8px 0;
  color: #606266;
  font-size: 14px;
}

.system-info {
  margin: 20px 0;
}

.system-info h4 {
  margin: 0 0 16px 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
}

.quick-actions {
  margin: 20px 0;
}

.quick-actions h4 {
  margin: 0 0 16px 0;
  color: #2c3e50;
  font-size: 16px;
  font-weight: 600;
}

.el-divider {
  margin: 20px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .dashboard-header {
    padding: 0 16px;
  }
  
  .sidebar {
    position: fixed;
    left: -200px;
    top: 64px;
    height: calc(100vh - 64px);
    z-index: 999;
    transition: left 0.3s;
  }
  
  .sidebar.collapsed {
    left: 0;
    width: 200px;
  }
  
  .welcome-content {
    padding: 16px;
  }
  
  .welcome-card {
    padding: 16px;
  }
  
  .header-left h2 {
    font-size: 18px;
  }
  
  .username {
    display: none;
  }
}
</style>