/**
 * 菜单工具函数
 */

/**
 * 构建菜单树结构
 * @param {Array} menus 菜单列表
 * @param {Number} parentId 父级ID
 * @returns {Array} 菜单树
 */
export function buildMenuTree(menus, parentId = 0) {
  const tree = []
  
  menus.forEach(menu => {
    if (menu.parentId === parentId) {
      const children = buildMenuTree(menus, menu.menuId)
      if (children.length > 0) {
        menu.children = children
      }
      tree.push(menu)
    }
  })
  
  return tree.sort((a, b) => a.orderNum - b.orderNum)
}

/**
 * 过滤菜单（只保留目录和菜单类型）
 * @param {Array} menus 菜单列表
 * @returns {Array} 过滤后的菜单
 */
export function filterMenus(menus) {
  return menus.filter(menu => {
    // M: 目录, C: 菜单, F: 按钮
    return menu.menuType === 'M' || menu.menuType === 'C'
  }).filter(menu => {
    // 只显示可见的菜单
    return menu.visible === '0' || menu.visible === 0
  })
}

/**
 * 构建路由配置
 * @param {Array} menus 菜单树
 * @returns {Array} 路由配置
 */
export function buildRoutes(menus) {
  const routes = []
  
  menus.forEach(menu => {
    const route = {
      path: menu.path,
      name: menu.menuName,
      meta: {
        title: menu.menuName,
        icon: menu.icon,
        noCache: menu.isCache === '1',
        link: menu.isFrame === '0' ? menu.path : null
      }
    }
    
    // 如果有组件路径，设置组件
    if (menu.component && menu.menuType === 'C') {
      route.component = () => import(`@/views/${menu.component}.vue`)
    }
    
    // 如果有子菜单，递归构建
    if (menu.children && menu.children.length > 0) {
      route.children = buildRoutes(menu.children)
    }
    
    routes.push(route)
  })
  
  return routes
}

/**
 * 获取菜单图标组件名
 * @param {String} iconName 图标名称
 * @returns {String} 组件名
 */
export function getIconComponent(iconName) {
  const iconMap = {
    'system': 'Setting',
    'user': 'User',
    'peoples': 'UserFilled',
    'tree-table': 'Grid',
    'dict': 'Collection',
    'monitor': 'Monitor',
    'tool': 'Tools',
    'guide': 'Guide',
    'star': 'Star',
    'chart': 'TrendCharts',
    'form': 'Document',
    'list': 'List',
    'tree': 'Share',
    'post': 'Postcard',
    'edit': 'Edit',
    'message': 'Message',
    'log': 'Document',
    'upload': 'Upload',
    'international': 'Globe',
    'online': 'Connection',
    'redis': 'Coin',
    'dashboard': 'Odometer',
    'job': 'Timer',
    'code': 'DocumentCopy',
    'logininfor': 'Key'
  }
  
  return iconMap[iconName] || 'Document'
}