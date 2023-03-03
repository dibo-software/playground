# playground V3 新手体验项目
* [v3 前端项目 视频介绍](教程地址)

> Diboot v3目前为 beta 版本，不建议使用于生产环境

## 项目启动配置
> 环境配置: 
  * JDK 8
  * MySQL 8
  * Node v14.8+
  * pnpm

> 项目代码说明：
* demo项目为PC端后端接口项目示例（Spring boot + Diboot v3），配置启动步骤：
  * 因后端项目尚未发布到 maven 库，体验后端需先[手动下载 diboot 基础组件代码](https://gitee.com/dibo_software/diboot/tree/dev-feature/) 并本地执行 maven install
  * 修改 `application-dev.yml` 中的数据库连接信息，改为本地连接
  * 配置IDEA服务（`DemoApplication`为启动类），运行demo后台程序，此时diboot各starter会自动执行初始化SQL

* diboot-admin-ui为前端项目（Vue3+TypeScript+Vite+Pinia+ElementPlus），配置启动步骤：
  * 切换到 diboot-admin-ui 目录下，
  执行install命令安装依赖组件
  ```cmd
  pnpm install
  ```
  执行run dev命令运行
  ```cmd
  pnpm run dev
  ```
  * 前端启动后登录系统(默认管理员登录账号： `admin` 密码：`123456`)

## 技术交流：
  * **VIP技术支持QQ群**（捐助/付费用户尊享）: [931266830]()
  
  * 技术交流QQ群: [731690096]() 
  
  * 技术交流微信群 加: [wx20201024]() (备注diboot)
  
## 关注公众号，diboot动态早知道
<img src="https://www.diboot.com/qrcode_gzh.jpg" width = "200" height = "200" alt="关注公众号" align=center />
    