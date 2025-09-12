import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, logout, getCaptcha } from '@/api/auth'
import { getRouters } from '@/api/system/menu'
import { ElMessage } from 'element-plus'
import { buildMenuTree, filterMenus } from '@/utils/menu'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))
  const permissions = ref(JSON.parse(localStorage.getItem('permissions') || '[]'))
  const roles = ref(JSON.parse(localStorage.getItem('roles') || '[]'))
  const menus = ref(JSON.parse(localStorage.getItem('menus') || '[]'))
  const menuTree = ref([])
  const captchaInfo = ref({
    captchaEnabled: false,
    uuid: '',
    img: ''
  })
  
  // 计算属性
  const isLoggedIn = computed(() => {
    return !!token.value && !!userInfo.value
  })
  
  // 获取验证码
  const fetchCaptcha = async () => {
    try {
      const response = await getCaptcha()
      captchaInfo.value = {
        captchaEnabled: response.captchaEnabled,
        uuid: response.uuid || '',
        img: response.img || ''
      }
      return captchaInfo.value
    } catch (error) {
      console.error('获取验证码失败:', error)
      ElMessage.error('获取验证码失败')
      throw error
    }
  }
  
  // 登录
  const loginAction = async (loginForm) => {
    try {
      const response = await login(loginForm)
      
      if (response.code === 200) {
        // 保存token和用户信息
        token.value = response.data.access_token
        userInfo.value = response.data
        permissions.value = response.data.permissions || ['*:*:*'] // 默认给予所有权限
        roles.value = response.data.roles || ['admin'] // 默认给予admin角色
        
        // 持久化存储
        localStorage.setItem('token', token.value)
        localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
        localStorage.setItem('permissions', JSON.stringify(permissions.value))
        localStorage.setItem('roles', JSON.stringify(roles.value))
        
        // 获取菜单权限
        await fetchMenus()
        
        ElMessage.success('登录成功')
        return response
      } else {
        ElMessage.error(response.msg || '登录失败')
        throw new Error(response.msg || '登录失败')
      }
    } catch (error) {
      console.error('登录失败:', error)
      ElMessage.error(error.message || '登录失败')
      throw error
    }
  }
  
  // 登出
  const logoutAction = async () => {
    try {
      // 如果有token，尝试调用后端退出接口
      if (token.value) {
        await logout()
      }
      
      // 清除本地存储
      clearUserData()
      
      ElMessage.success('退出成功')
    } catch (error) {
      console.error('退出失败:', error)
      // 即使后端退出失败，也要清除本地存储
      clearUserData()
      
      // 根据错误类型显示不同消息
      if (error.response && error.response.status === 401) {
        ElMessage.info('会话已过期，已自动退出')
      } else {
        ElMessage.warning('退出登录')
      }
    }
  }
  
  // 获取菜单权限
  const fetchMenus = async () => {
    try {
      const response = await getRouters()
      console.log('菜单API响应:', response)
      if (response.code === 200) {
        // 后端返回的是路由数据，直接使用response.data
        menus.value = response.data || []
        console.log('获取到的菜单数据:', menus.value)
        
        // 直接使用后端返回的路由数据作为菜单树
        menuTree.value = menus.value
        console.log('设置的菜单树:', menuTree.value)
        
        // 持久化存储
        localStorage.setItem('menus', JSON.stringify(menus.value))
        
        return menuTree.value
      } else {
        console.error('菜单API返回错误:', response)
        // 如果API失败，使用临时菜单数据
        const tempMenus = [
          {
            name: 'System',
            path: '/system',
            meta: {
              title: '系统管理',
              icon: 'system'
            },
            children: [
              {
                name: 'User',
                path: '/system/user',
                meta: {
                  title: '用户管理',
                  icon: 'user'
                }
              },
              {
                name: 'Role',
                path: '/system/role',
                meta: {
                  title: '角色管理',
                  icon: 'peoples'
                }
              },
              {
                name: 'Menu',
                path: '/system/menu',
                meta: {
                  title: '菜单管理',
                  icon: 'tree-table'
                }
              },
              {
                name: 'Dept',
                path: '/system/dept',
                meta: {
                  title: '部门管理',
                  icon: 'tree'
                }
              },
              {
                name: 'Post',
                path: '/system/post',
                meta: {
                  title: '岗位管理',
                  icon: 'post'
                }
              },
              {
                name: 'Dict',
                path: '/system/dict',
                meta: {
                  title: '字典管理',
                  icon: 'dict'
                },
                children: [
                  {
                    name: 'DictType',
                    path: '/system/dict/type',
                    meta: {
                      title: '字典类型',
                      icon: 'dict'
                    }
                  },
                  {
                    name: 'DictData',
                    path: '/system/dict/data',
                    meta: {
                      title: '字典数据',
                      icon: 'dict'
                    }
                  }
                ]
              },
              {
                name: 'Config',
                path: '/system/config',
                meta: {
                  title: '参数设置',
                  icon: 'edit'
                }
              },
              {
                name: 'Notice',
                path: '/system/notice',
                meta: {
                  title: '通知公告',
                  icon: 'message'
                }
              },
              {
                name: 'Client',
                path: '/system/client',
                meta: {
                  title: '客户端管理',
                  icon: 'client'
                }
              },
              {
                name: 'Oss',
                path: '/system/oss',
                meta: {
                  title: '对象存储',
                  icon: 'upload'
                }
              },
              {
                name: 'Tenant',
                path: '/system/tenant',
                meta: {
                  title: '租户管理',
                  icon: 'tenant'
                }
              }
            ]
          },
          {
            name: 'Monitor',
            path: '/monitor',
            meta: {
              title: '系统监控',
              icon: 'monitor'
            },
            children: [
              {
                name: 'Logininfor',
                path: '/monitor/logininfor',
                meta: {
                  title: '登录日志',
                  icon: 'logininfor'
                }
              },
              {
                name: 'Operlog',
                path: '/monitor/operlog',
                meta: {
                  title: '操作日志',
                  icon: 'form'
                }
              }
            ]
          }
        ]
        menus.value = tempMenus
        menuTree.value = tempMenus
        console.log('使用临时菜单数据:', tempMenus)
        return menuTree.value
      }
    } catch (error) {
      console.error('获取菜单失败:', error)
      ElMessage.warning('菜单加载失败，使用默认菜单')
      
      // 使用临时菜单数据
      const tempMenus = [
        {
          name: 'System',
          path: '/system',
          meta: {
            title: '系统管理',
            icon: 'system'
          },
          children: [
            {
              name: 'User',
              path: '/system/user',
              meta: {
                title: '用户管理',
                icon: 'user'
              }
            },
            {
              name: 'Role',
              path: '/system/role',
              meta: {
                title: '角色管理',
                icon: 'peoples'
              }
            },
            {
              name: 'Menu',
              path: '/system/menu',
              meta: {
                title: '菜单管理',
                icon: 'tree-table'
              }
            },
            {
              name: 'Dept',
              path: '/system/dept',
              meta: {
                title: '部门管理',
                icon: 'tree'
              }
            },
            {
              name: 'Post',
              path: '/system/post',
              meta: {
                title: '岗位管理',
                icon: 'post'
              }
            },
            {
              name: 'Dict',
              path: '/system/dict',
              meta: {
                title: '字典管理',
                icon: 'dict'
              },
              children: [
                {
                  name: 'DictType',
                  path: '/system/dict/type',
                  meta: {
                    title: '字典类型',
                    icon: 'dict'
                  }
                },
                {
                  name: 'DictData',
                  path: '/system/dict/data',
                  meta: {
                    title: '字典数据',
                    icon: 'dict'
                  }
                }
              ]
            },
            {
              name: 'Config',
              path: '/system/config',
              meta: {
                title: '参数设置',
                icon: 'edit'
              }
            },
            {
              name: 'Notice',
              path: '/system/notice',
              meta: {
                title: '通知公告',
                icon: 'message'
              }
            },
            {
              name: 'Client',
              path: '/system/client',
              meta: {
                title: '客户端管理',
                icon: 'client'
              }
            },
            {
              name: 'Oss',
              path: '/system/oss',
              meta: {
                title: '对象存储',
                icon: 'upload'
              }
            },
            {
              name: 'Tenant',
              path: '/system/tenant',
              meta: {
                title: '租户管理',
                icon: 'tenant'
              }
            }
          ]
        },
        {
          name: 'Monitor',
          path: '/monitor',
          meta: {
            title: '系统监控',
            icon: 'monitor'
          },
          children: [
            {
              name: 'Logininfor',
              path: '/monitor/logininfor',
              meta: {
                title: '登录日志',
                icon: 'logininfor'
              }
            },
            {
              name: 'Operlog',
              path: '/monitor/operlog',
              meta: {
                title: '操作日志',
                icon: 'form'
              }
            }
          ]
        }
      ]
      menus.value = tempMenus
      menuTree.value = tempMenus
      console.log('使用临时菜单数据(catch):', tempMenus)
      return menuTree.value
    }
  }
  
  // 清除用户数据的统一方法
  const clearUserData = () => {
    token.value = ''
    userInfo.value = null
    permissions.value = []
    roles.value = []
    menus.value = []
    menuTree.value = []
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('permissions')
    localStorage.removeItem('roles')
    localStorage.removeItem('menus')
  }
  
  return {
    // 状态
    token,
    userInfo,
    permissions,
    roles,
    menus,
    menuTree,
    captchaInfo,
    
    // 计算属性
    isLoggedIn,
    
    // 方法
    fetchCaptcha,
    loginAction,
    logoutAction,
    fetchMenus,
    clearUserData
  }
})