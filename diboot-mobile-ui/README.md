# diboot-mobile-ui

[diboot-mobile-ui 官方文档](https://www.diboot.com/guide/diboot-mobile-ui/introduce.html)

介绍
-----
diboot-mobile-ui是一款基于uni-app、风格极简、快速上手、与diboot-mobile后端组件结合、开箱即用的移动端项目。


相关技术栈
-----

* uni-app基础
* uni-app基础
* 技术栈：[uni-app](https://uniapp.dcloud.io/), [Vue](https://cn.vuejs.org/index.html), [luch-request](https://www.quanzhan.co/luch-request/handbook/)
* 组件库：[uView](https://v1.uviewui.com/)
* 前端开发工具：[HBuilderX](https://hx.dcloud.net.cn/Tutorial/HistoryVersion)
* ES6 语法


项目特性
-----
* 开箱即用的项目脚手架；
* 简洁、易上手的form组件库；
* 风格统一的CRUD模版代码；
* 支持devtools自动化生成；
* 内置多种登录方式：
  * 账号密码登录 （支持IamMember/IamUser切换）
  * 微信小程序登录（自动注册）
  * 微信公众号登录（自动注册）
* 与diboot PC端前端项目整体结构保持一致，熟悉diboot的小伙伴更容易上手开发。

> - uView版本为[v1.8.5](https://v1.uviewui.com/)，使用时请注意文档版本；
> - HBuilderX版本为[V3.3.5.20211229](https://hx.dcloud.net.cn/Tutorial/HistoryVersion)，由于软件版本不同可能会导致实际结果存在差异或异常，我们建议您使用此版本进行开发，其他版本不能保证完全适配；
> - 当前项目脚手架已验证`H5`、`APP`、`微信小程序`平台，其他平台环境不能保证完全适配。


项目下载与运行
-----

* 项目下载：
```bash
git clone https://gitee.com/dibo_software/diboot-mobile-ui.git
cd diboot-mobile-ui
```

* 安装依赖
```bash
yarn
# OR
npm install
```

* 接口配置
  * 接口配置文件位于项目根目录下：utils/constant.js


* 运行项目
  * 项目导入 HBuilderX 后选择对应的方式运行即可
