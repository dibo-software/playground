import { dibootApi } from '@/utils/request'

export default {
  data () {
    return {
      // 是否从当前业务的attachMore接口中自动获取关联数据
      getMore: false,
      // 获取关联数据列表的配置列表
      attachMoreList: [],
      // 远程过滤关联数据列表的配置对象
      attachMoreLoader: {},
      // 远程过滤加载状态
      attachMoreLoading: false,
      // 关联相关的更多数据
      more: {},
      // 控制，用于级联控制选择的选择加载控制
      control: {}
    }
  },
  methods: {
    /**
     * 加载当前页面关联的对象或者字典
     */
    async attachMore () {
      const reqList = []
      // 个性化接口
      this.getMore === true && reqList.push(dibootApi.get(`${this.baseApi}/attachMore`))
      // 通用获取当前对象关联的数据的接口
      this.attachMoreList.length > 0 && reqList.push(dibootApi.post('/common/attachMore', this.attachMoreList))
      if (reqList.length > 0) {
        const resList = await Promise.all(reqList)
        resList.forEach(res => res.ok
          ? Object.keys(res.data).forEach(key => this.$set(this.more, key, res.data[key]))
          : this.$notification.error({
            title: '获取选项数据失败',
            message: res.msg
          }))
      }
    },
    /**
     * 远程过滤加载选项
     *
     * @param value 输入值
     * @param loader 加载器类型
     */
    async attachMoreFilter (value, loader) {
      if (value == null || (value = value.trim()).length === 0) {
        this.$set(this.more, `${loader}Options`, [])
        return
      }
      this.attachMoreLoading = true
      const moreLoader = this.attachMoreLoader[loader]
      moreLoader.keyword = value
      this.$set(this.more, `${loader}Options`, await this.loadAttachMore(moreLoader))
      this.attachMoreLoading = false
    },
    /**
     * 加载AttachMore
     *
     * @param moreLoader 加载器
     * @param node 节点（可空）
     */
    async loadAttachMore (moreLoader, node = {}) {
      if (moreLoader.disabled) {
        return
      }
      const build = item => item ? `/${item}` : ''
      const res = await dibootApi.post('/common/attachMoreFilter' + build(node.type) + build(node.value), moreLoader)
      if (res.ok) {
        return res.data
      } else {
        this.$notification.error({
          title: '获取选项数据失败',
          message: res.msg
        })
      }
    },
    /**
     * 异步加载更多
     *
     * @param node 当前节点
     * @param loader 加载器名称
     */
    async lazyLoadMore (node, loader) {
      const targetNode = node[node.length - 1]
      targetNode.loading = true
      const moreLoader = this.attachMoreLoader[loader]
      const dataLsit = await this.loadAttachMore(moreLoader, targetNode) || []
      dataLsit.length === 0 && moreLoader.next != null && this.$set(targetNode, 'disabled', true)
      targetNode.children = dataLsit.map(e => {
        e.isLeaf = e.isLeaf || false
        return e
      })
      targetNode.loading = false
      this.$set(this.more, `${loader}Options`, [...this.more[`${loader}Options`]])
    },
    /**
     * 控制相关选项的获取
     *
     * @param value 选项值
     * @param control 控制器名称
     * @param resetValue 是否需要重置被控属性值
     */
    controlRelationOptions (value, control, resetValue = true) {
      const controlItem = this.control[control]
      const isNull = value == null || value.length === 0
      const execute = async ({ name, loader, condition, lazy }) => {
        const moreLoader = this.attachMoreLoader[loader]
        moreLoader.disabled = isNull
        moreLoader.condition == null && (moreLoader.condition = {})
        moreLoader.condition[condition] = value
        if (resetValue) {
          const obj = {}
          obj[name] = undefined
          this.form.setFieldsValue(obj)
        }
        this.$set(this.more, `${loader}Options`, isNull || lazy ? [] : await this.loadAttachMore(moreLoader))
      }
      controlItem instanceof Array ? controlItem.forEach(item => execute(item)) : execute(controlItem || {})
    }
  }
}
