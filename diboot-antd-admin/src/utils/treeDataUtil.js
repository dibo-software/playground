import _ from 'lodash'
/***
 * 将不标准的树结构数据转化为组件能够使用的标准结构
 * @param treeList
 * @param valueField
 * @param titleField
 * @param toString
 * @returns {[]|undefined}
 */
const treeListFormatter = function (treeList, valueField, titleField, toString) {
  if (treeList == null || treeList.length === 0) {
    return undefined
  }

  const formatterList = []
  treeList.forEach(item => {
    let formatterItem = {}
    if (toString) {
      formatterItem = {
        key: `${item[valueField]}`,
        value: `${item[valueField]}`,
        title: item[titleField]
      }
    } else {
      formatterItem = {
        key: item[valueField],
        value: item[valueField],
        title: item[titleField]
      }
    }
    if (item.children && item.children.length > 0) {
      formatterItem.children = treeListFormatter(item.children, valueField, titleField, toString)
    }
    formatterList.push(formatterItem)
  })

  return formatterList
}

/***
 * 清除空的children，赋值为undefined
 * @param treeList
 */
const clearNullChildren = function (treeList) {
  if (treeList == null || treeList.length === 0) {
    return undefined
  }
  treeList.forEach(item => {
    item.children = clearNullChildren(item.children)
  })
  return treeList
}

/***
 * 树型列表转普通列表
 * @param treeList
 * @returns {[]|*[]}
 */
const treeList2list = function (treeList) {
  if (treeList == null || treeList.length === 0) {
    return []
  }
  const allList = []
  treeList.forEach(item => {
    if (item.children != null && item.children.length > 0) {
      allList.push(...treeList2list(item.children))
      delete item.children
    }
  })
  allList.push(...treeList)
  return allList
}

const treeList2IndentList = function (treeList, level) {
  if (treeList == null || treeList.length === 0) {
    return []
  }
  level = level == null ? 0 : level
  const allList = []
  let prefix = ''
  for (let i = 0; i < level; i++) {
    prefix += '　'
  }
  treeList.forEach(item => {
    item.title = prefix + item.title
    allList.push(item)
    if (item.children != null && item.children.length > 0) {
      allList.push(...treeList2IndentList(item.children, (level + 1)))
      delete item.childre
    }
  })
  return allList
}

/***
 * 树形列表转子列表map
 * @param list
 * @returns {{}}
 */
const list2childrenMap = function (list) {
  const childrenMap = {}
  if (list == null || list.length === 0) {
    return childrenMap
  }
  list.forEach(item => {
    let children = childrenMap[item.parentId]
    if (children == null) {
      children = []
      childrenMap[item.parentId] = children
    }
    children.push(item)
  })
  return childrenMap
}

/***
 * 路由配置格式化
 * @param routers
 * @returns {[]|undefined}
 */
const routersFormatter = function (routers) {
  if (routers == null || routers.length === 0) {
    return undefined
  }

  const formatterList = []
  routers.forEach(item => {
    if (item.meta) {
      const code = item.meta.permission && item.meta.permission.length > 0 ? item.meta.permission[0] : item.name
      const formatterItem = {
        key: code,
        value: code,
        title: `${item.meta.title}`
      }
      if (item.children != null && item.children.length > 0) {
        formatterItem.children = routersFormatter(item.children)
      }
      formatterList.push(formatterItem)
    }
  })
  return formatterList
}

const apiListFormatter = function (apiList) {
  if (apiList == null || apiList.length === 0) {
    return undefined
  }

  const formatterList = []
  apiList.forEach((item, index) => {
    let children
    if (item.children) {
      children = item.children.map(child => {
        return {
          key: child.value,
          value: child.value,
          title: `[${item.classTitle}-${child.apiName}][${child.apiMethod}](${child.apiUri})`
        }
      })
    }
    formatterList.push({
      key: item.classTitle,
      value: `${index}`,
      title: item.classTitle,
      children,
      disabled: true
    })
  })
  return formatterList
}

/***
 * 权限树状结构转化（用于角色权限配置中）
 * @param treeList
 * @param valueField
 * @param titleField
 * @returns {[]|undefined}
 */
const permissionTreeListFormatter = function (treeList, valueField, titleField) {
  if (treeList == null || treeList.length === 0) {
    return undefined
  }

  const formatterList = []
  treeList.forEach(item => {
    const slots = {
      icon: item.displayType === 'MENU' ? 'menu' : 'permission'
    }
    const formatterItem = {
      slots,
      type: item.displayType,
      parentId: item.parentId,
      id: item.id,
      sortId: item.sortId,
      key: item[valueField],
      value: item[valueField],
      title: item[titleField]
    }
    if (item.children != null && item.children.length > 0) {
      formatterItem.children = permissionTreeListFormatter(item.children, valueField, titleField)
    }
    formatterList.push(formatterItem)
  })

  return formatterList
}

/***
 * 排序树状结构格式化
 * @param treeList
 * @param valueField
 * @param titleField
 * @returns {undefined}
 */
const sortTreeListFormatter = function (treeList, valueField, titleField) {
  if (treeList == null || treeList.length === 0) {
    return undefined
  }

  const formatterList = []
  treeList.forEach(item => {
    const formatterItem = {
      type: item.displayType,
      parentId: item.parentId,
      id: item.id,
      sortId: item.sortId,
      key: item[valueField],
      value: item[valueField],
      title: item[titleField]
    }
    if (item.children != null && item.children.length > 0) {
      formatterItem.children = sortTreeListFormatter(item.children, valueField, titleField)
    }
    formatterList.push(formatterItem)
  })

  return formatterList
}

const getDeepExpandKeys = function (treeList, keyField, childrenField) {
  if (!treeList || treeList.length === 0) {
    return []
  }
  const keys = []
  keyField = keyField || 'id'
  childrenField = childrenField || 'children'
  const newKeys = treeList.map(tree => {
    const children = tree[childrenField]
    keys.push(...getDeepExpandKeys(children, keyField, childrenField))
    return tree[keyField]
  })
  keys.push(...newKeys)
  return keys
}

/**
 * 构造树型结构数据
 * @param {Array} data 数据源
 * @param {string | number} rootId 根Id 默认 0
 * @param {string} value 值字段 默认 'value'
 * @param {string} label 标签（要显示字段）默认 'label'
 * @param {string} parentId 父节点字段 默认 'ext'
 * @param {string} children 孩子节点字段 默认 'children'
 */
const list2tree = (data = [], rootId = '0', value = 'value', label = 'label', parentId = 'ext', children = 'children') => {
  // 对源数据深度克隆
  const cloneData = JSON.parse(JSON.stringify(data))
  // 循环所有项
  const treeData = cloneData.filter(father => {
    const branchArr = cloneData.filter(child => {
      // 返回每一项的子级数组
      return father[value] === child[parentId]
    })
    father[children] = branchArr.length > 0 ? branchArr : undefined
    father.label = father[label]
    // 返回第一层
    return father[parentId] === rootId
  })
  return treeData.length === 0 ? data : treeData
}
/**
 * tree转化为list
 * @param tree
 */
const tree2List = (tree, children = 'children') => {
  const list = []
  const cloneData = _.cloneDeep(tree)
  for (const node of cloneData) {
    const nodeChildren = node[children] || []
    if (nodeChildren && nodeChildren.length > 0) {
      for (const nodeChild of tree2List(nodeChildren)) {
        list.push(nodeChild)
      }
    }
    list.push(node)
  }
  return list
}

export {
  treeListFormatter,
  clearNullChildren,
  treeList2list,
  treeList2IndentList,
  list2childrenMap,
  routersFormatter,
  apiListFormatter,
  permissionTreeListFormatter,
  sortTreeListFormatter,
  getDeepExpandKeys,
  list2tree,
  tree2List
}
