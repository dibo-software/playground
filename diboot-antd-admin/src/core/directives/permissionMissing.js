import Vue from 'vue'
import store from '@/store'

/**
 * Permission 权限指令
 */
const permissionMissing = Vue.directive('permissionMissing', {
  inserted: function (el, binding, vnode) {
    const { value } = binding
    const roles = store.getters && store.getters.roles
    if (roles.superAdmin === true) {
      el.parentNode && el.parentNode.removeChild(el) || (el.style.display = 'none')
    }

    const elVal = vnode.context.$route.meta.permission
    const permissionId = elVal instanceof String && [elVal] || elVal
    // 如果没有配置菜单权限，则都视作有权限处理
    if (!permissionId) {
      el.parentNode && el.parentNode.removeChild(el) || (el.style.display = 'none')
      return
    }

    // 此处的权限列表判断为或关系，也就是v-permission的参数中的权限有一个满足，就不显示该元素
    if (value && value instanceof Array && value.length > 0) {
      // 当前允许的权限列表
      const actions = value
      const hasPermission = roles.permissions.some(p => {
        if (!permissionId.includes(p.permissionId)) {
          return false
        }
        if (!p.actionList || p.actionList.length === 0) {
          return false
        }
        let hasAction = false
        for (let i = 0; i < actions.length; i++) {
          const actionName = actions[i]
          if (p.actionList.includes(actionName)) {
            hasAction = true
            break
          }
        }
        return hasAction
      })

      if (hasPermission) {
        el.parentNode && el.parentNode.removeChild(el) || (el.style.display = 'none')
      }
    } else {
      throw new Error(`需要配置权限! Like v-permission-missing="['create','update']"`)
    }
  }
})

export default permissionMissing
