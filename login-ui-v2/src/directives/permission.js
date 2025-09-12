import { checkPermi, checkRole } from '@/utils/permission'

/**
 * v-hasPermi 权限指令
 * 用法：v-hasPermi="['system:user:add']"
 */
export const hasPermi = {
  mounted(el, binding) {
    const { value } = binding
    if (value && value instanceof Array && value.length > 0) {
      const hasPermission = checkPermi(value)
      if (!hasPermission) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    } else {
      throw new Error(`请设置操作权限标签值")`)
    }
  }
}

/**
 * v-hasRole 角色指令
 * 用法：v-hasRole="['admin']"
 */
export const hasRole = {
  mounted(el, binding) {
    const { value } = binding
    if (value && value instanceof Array && value.length > 0) {
      const hasRoleFlag = checkRole(value)
      if (!hasRoleFlag) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    } else {
      throw new Error(`请设置角色权限标签值")`)
    }
  }
}

/**
 * v-hasPermiOr 权限指令（满足其中一个即可）
 * 用法：v-hasPermiOr="['system:user:add', 'system:user:edit']"
 */
export const hasPermiOr = {
  mounted(el, binding) {
    const { value } = binding
    if (value && value instanceof Array && value.length > 0) {
      const hasPermission = checkPermi(value)
      if (!hasPermission) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    } else {
      throw new Error(`请设置操作权限标签值")`)
    }
  }
}

/**
 * v-hasRoleOr 角色指令（满足其中一个即可）
 * 用法：v-hasRoleOr="['admin', 'editor']"
 */
export const hasRoleOr = {
  mounted(el, binding) {
    const { value } = binding
    if (value && value instanceof Array && value.length > 0) {
      const hasRoleFlag = checkRole(value)
      if (!hasRoleFlag) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    } else {
      throw new Error(`请设置角色权限标签值")`)
    }
  }
}