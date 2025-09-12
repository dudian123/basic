import { useAuthStore } from '@/stores/auth'

/**
 * 字符权限校验
 * @param {Array} value 校验值
 * @returns {Boolean}
 */
export const checkPermi = (value) => {
  if (value && value instanceof Array && value.length > 0) {
    const authStore = useAuthStore()
    const permissions = authStore.permissions || []
    const permissionDatas = value
    const all_permission = '*:*:*'

    const hasPermission = permissions.some((permission) => {
      return all_permission === permission || permissionDatas.includes(permission)
    })

    if (!hasPermission) {
      return false
    }
    return true
  } else {
    console.error(`need permissions! Like checkPermi="['system:user:add','system:user:edit']"`)
    return false
  }
}

/**
 * 角色权限校验
 * @param {Array} value 校验值
 * @returns {Boolean}
 */
export const checkRole = (value) => {
  if (value && value instanceof Array && value.length > 0) {
    const authStore = useAuthStore()
    const roles = authStore.roles || []
    const permissionRoles = value
    const super_admin = 'admin'

    const hasRole = roles.some((role) => {
      return super_admin === role || permissionRoles.includes(role)
    })

    if (!hasRole) {
      return false
    }
    return true
  } else {
    console.error(`need roles! Like checkRole="['admin','editor']"`)
    return false
  }
}

/**
 * 验证用户是否具备某权限
 * @param {String} permission 权限字符串
 * @returns {Boolean}
 */
export const hasPermi = (permission) => {
  return checkPermi([permission])
}

/**
 * 验证用户是否含有指定权限，只需包含其中一个
 * @param {Array} permissions 权限列表
 * @returns {Boolean}
 */
export const hasPermiOr = (permissions) => {
  return checkPermi(permissions)
}

/**
 * 验证用户是否含有指定权限，必须全部拥有
 * @param {Array} permissions 权限列表
 * @returns {Boolean}
 */
export const hasPermiAnd = (permissions) => {
  if (permissions && permissions instanceof Array && permissions.length > 0) {
    const authStore = useAuthStore()
    const userPermissions = authStore.permissions || []
    const all_permission = '*:*:*'
    
    return permissions.every(permission => {
      return userPermissions.includes(all_permission) || userPermissions.includes(permission)
    })
  }
  return false
}

/**
 * 验证用户是否具备某角色
 * @param {String} role 角色字符串
 * @returns {Boolean}
 */
export const hasRole = (role) => {
  return checkRole([role])
}

/**
 * 验证用户是否含有指定角色，只需包含其中一个
 * @param {Array} roles 角色列表
 * @returns {Boolean}
 */
export const hasRoleOr = (roles) => {
  return checkRole(roles)
}

/**
 * 验证用户是否含有指定角色，必须全部拥有
 * @param {Array} roles 角色列表
 * @returns {Boolean}
 */
export const hasRoleAnd = (roles) => {
  if (roles && roles instanceof Array && roles.length > 0) {
    const authStore = useAuthStore()
    const userRoles = authStore.roles || []
    const super_admin = 'admin'
    
    return roles.every(role => {
      return userRoles.includes(super_admin) || userRoles.includes(role)
    })
  }
  return false
}