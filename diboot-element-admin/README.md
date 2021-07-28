# diboot-element-admin

介绍
-----
diboot-element-admin前端基础项目，是一个与diboot其他后端组件构成的后端系统相吻合的前端项目。您可以将此用于您的日常开发中，和diboot其他组件一起用于快速构建您的实际项目，享受自动化带来的快速开发体验。

相关技术栈
------
 * Vue
 * Vue全家桶套餐：[Vue](https://cn.vuejs.org/index.html), [vue-router](https://router.vuejs.org/zh/), [axios](https://github.com/axios/axios), [vuex](https://vuex.vuejs.org/zh/) 
 * 组件库：[Element](https://element.eleme.cn/#/zh-CN)
 * 基于Element的中后台管理基础项目：[vue-element-admin](https://panjiachen.github.io/vue-element-admin-site/zh/)
 * ES6 语法

项目特性
------
* 基于开源项目[vue-element-admin](https://panjiachen.github.io/vue-element-admin-site/zh/)的基础模板[vue-admin-template](https://github.com/PanJiaChen/vue-admin-template)；
* 在**vue-admin-template**项目基础上，增加了vue-element-admin具有的多页签、菜单搜索、全屏显示、调整布局大小等功能；
* 登录、权限、接口对接上，与diboot-v2相关组件构建的后端应用无缝集成且开箱可用；
* 提取CRUD页面相关通用属性与方法到mixins文件中，少写代码，多做事情；
* 菜单到按钮级别的细粒度权限控制；
* 智能化的权限配置方案；
* 自动化的token交换方案；
* 预置多种常用请求方式，轻松完成异步文件下载等；
* 数据字典管理功能；
* 登录人员管理界面；
* 角色与权限管理功能；
* 权限管理功能；
* 登录日志管理功能。


项目下载与运行
-----

* 项目下载：
```bash
git clone https://github.com/dibo-software/diboot-element-admin.git
cd diboot-element-admin
```

* 安装依赖
```bash
yarn
# OR
npm install
```

* 运行项目
```bash
yarn serve
# OR
npm run serve
```

