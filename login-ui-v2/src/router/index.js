import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { hasPermi } from '@/utils/permission'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  // 直接路径重定向到系统管理路径
  {
    path: '/user',
    redirect: '/system/user'
  },
  {
    path: '/role',
    redirect: '/system/role'
  },
  {
    path: '/menu',
    redirect: '/system/menu'
  },
  {
    path: '/dept',
    redirect: '/system/dept'
  },
  {
    path: '/post',
    redirect: '/system/post'
  },
  {
    path: '/dict',
    redirect: '/system/dict/type'
  },
  {
    path: '/dict/type',
    redirect: '/system/dict/type'
  },
  {
    path: '/dict/data',
    redirect: '/system/dict/data'
  },
  {
    path: '/config',
    redirect: '/system/config'
  },
  {
    path: '/notice',
    redirect: '/system/notice'
  },
  {
    path: '/client',
    redirect: '/system/client'
  },
  {
    path: '/oss',
    redirect: '/system/oss'
  },
  {
    path: '/tenant',
    redirect: '/system/tenant'
  },
  {
    path: '/logininfor',
    redirect: '/monitor/logininfor'
  },
  {
    path: '/operlog',
    redirect: '/monitor/operlog'
  },
  // 用户角色授权页面


  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: {
      title: '登录',
      requiresAuth: false
    }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: {
      title: '首页',
      requiresAuth: true
    },
    children: [
      // 系统管理
      {
        path: '/system/user',
        name: 'User',
        component: () => import('@/views/system/user/index.vue'),
        meta: {
          title: '用户管理',
          requiresAuth: true,
          permissions: ['system:user:list']
        }
      },
      {
        path: '/system/role',
        name: 'Role',
        component: () => import('@/views/system/role/index.vue'),
        meta: {
          title: '角色管理',
          requiresAuth: true,
          permissions: ['system:role:list']
        }
      },
      {
        path: '/system/menu',
        name: 'Menu',
        component: () => import('@/views/system/menu/index.vue'),
        meta: {
          title: '菜单管理',
          requiresAuth: true,
          permissions: ['system:menu:list']
        }
      },
      {
        path: '/system/dept',
        name: 'Dept',
        component: () => import('@/views/system/dept/index.vue'),
        meta: {
          title: '部门管理',
          requiresAuth: true,
          permissions: ['system:dept:list']
        }
      },
      {
        path: '/system/post',
        name: 'Post',
        component: () => import('@/views/system/post/index.vue'),
        meta: {
          title: '岗位管理',
          requiresAuth: true,
          permissions: ['system:post:list']
        }
      },
      {
        path: '/system/dict',
        redirect: '/system/dict/type'
      },
      {
        path: '/system/dict/type',
        name: 'DictType',
        component: () => import('@/views/system/dict/type/index.vue'),
        meta: {
          title: '字典类型',
          requiresAuth: true,
          permissions: ['system:dict:list']
        }
      },
      {
        path: '/system/dict/data/:dictType?',
        name: 'DictData',
        component: () => import('@/views/system/dict/data/index.vue'),
        meta: {
          title: '字典数据',
          requiresAuth: true,
          permissions: ['system:dict:list']
        }
      },
      {
        path: '/system/dict/data/index/:dictId',
        name: 'DictDataWithId',
        component: () => import('@/views/system/dict/data/index.vue'),
        meta: {
          title: '字典数据',
          requiresAuth: true,
          permissions: ['system:dict:list']
        }
      },
      {
        path: '/system/config',
        name: 'Config',
        component: () => import('@/views/system/config/index.vue'),
        meta: {
          title: '参数设置',
          requiresAuth: true,
          permissions: ['system:config:list']
        }
      },
      {
        path: '/system/notice',
        name: 'Notice',
        component: () => import('@/views/system/notice/index.vue'),
        meta: {
          title: '通知公告',
          requiresAuth: true,
          permissions: ['system:notice:list']
        }
      },
      {
        path: '/system/client',
        name: 'Client',
        component: () => import('@/views/system/client/index.vue'),
        meta: {
          title: '客户端管理',
          requiresAuth: true,
          permissions: ['system:client:list']
        }
      },
      {
        path: '/system/oss',
        name: 'Oss',
        component: () => import('@/views/system/oss/index.vue'),
        meta: {
          title: '对象存储',
          requiresAuth: true,
          permissions: ['system:oss:list']
        }
      },
      {
        path: '/system/tenant',
        name: 'Tenant',
        component: () => import('@/views/system/tenant/index.vue'),
        meta: {
          title: '租户管理',
          requiresAuth: true,
          permissions: ['system:tenant:list']
        }
      },
      {
        path: '/monitor/logininfor',
        name: 'Logininfor',
        component: () => import('@/views/monitor/logininfor/index.vue'),
        meta: {
          title: '登录日志',
          requiresAuth: true,
          permissions: ['monitor:logininfor:list']
        }
      },
      {
        path: '/monitor/operlog',
        name: 'Operlog',
        component: () => import('@/views/monitor/operlog/index.vue'),
        meta: {
          title: '操作日志',
          requiresAuth: true,
          permissions: ['monitor:operlog:list']
        }
      },
      {
        path: '/test/token',
        name: 'TokenTest',
        component: () => import('@/views/test/TokenTest.vue'),
        meta: {
          title: 'Token测试',
          requiresAuth: false
        }
      },
      // 用户角色授权页面
      {
        path: '/system/user-auth/role/:userId',
        name: 'UserAuthRole',
        component: () => import('@/views/system/user/authRole.vue'),
        meta: {
          title: '用户角色授权',
          requiresAuth: true,
          permissions: ['system:user:edit']
        }
      },
      // 角色授权用户页面
      {
        path: '/system/role-auth/user/:roleId',
        name: 'RoleAuthUser',
        component: () => import('@/views/system/role/authUser.vue'),
        meta: {
          title: '角色授权用户',
          requiresAuth: true,
          permissions: ['system:role:edit']
        }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()
  
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - RuoYi管理系统`
  }
  
  // 如果访问登录页面且已登录，重定向到首页
  if (to.path === '/login' && authStore.isLoggedIn) {
    next('/dashboard')
    return
  }
  
  // 如果访问需要登录的页面但未登录，重定向到登录页
  if (to.path !== '/login' && !authStore.isLoggedIn) {
    // 清除可能存在的无效token
    authStore.clearUserData()
    next('/login')
    return
  }
  
  // 检查权限
  if (to.meta.permissions) {
    const permissions = Array.isArray(to.meta.permissions) ? to.meta.permissions : [to.meta.permissions]
    let hasPermission = false
    
    // 检查是否有任一权限
    for (const permission of permissions) {
      if (hasPermi(permission)) {
        hasPermission = true
        break
      }
    }
    
    if (!hasPermission) {
      ElMessage.error('您没有访问该页面的权限')
      next(false)
      return
    }
  }
  
  next()
})

export default router