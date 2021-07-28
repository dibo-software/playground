export function actionToObject (json) {
  try {
    return JSON.parse(json)
  } catch (e) {
    console.log('err', e.message)
  }
  return []
}

/***
 * 将后端的permissionList转为permissions
 * @param permissionList
 */
export function permissionListToPermissions (permissionList) {
  if (!permissionList || permissionList.length === 0) {
    return permissionList
  }

  const menuPermissionList = permissionList.filter(item => {
    return item.displayType === 'MENU'
  })
  const actionPermissionList = permissionList.filter(item => {
    return item.displayType !== 'MENU'
  })
  const actionPermissionListMap = {}
  actionPermissionList.forEach(item => {
    let actionList = actionPermissionListMap[item.parentId]
    if (actionList === undefined) {
      actionList = []
      actionPermissionListMap[item.parentId] = actionList
    }
    actionList.push(item)
  })

  const permissions = []
  menuPermissionList.forEach(item => {
    // 获取actionList
    let actionEntitySet = actionPermissionListMap[item.id]
    if (actionEntitySet !== undefined) {
      actionEntitySet = actionEntitySet.map(action => {
        return {
          action: action.resourceCode,
          describe: action.displayName,
          defaultCheck: false
        }
      })
    } else {
      actionEntitySet = []
    }
    const actionList = actionEntitySet.map(item => {
      return item.action
    })

    permissions.push({
      permissionId: item.resourceCode,
      permissionName: item.displayName,
      actionEntitySet,
      actionList
    })
  })
  return permissions
}
