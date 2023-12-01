# playground V3 新手体验项目
> Diboot v3.x版本 基础功能+Devtools 体验环境

## 项目启动与devtools等配置说明
### 0. playground 项目说明
> 依赖环境：
  * JDK 8
  * MySQL 8 （或Postgres等其他数据库）
  * Node v16
  * pnpm

> 项目目录说明:
  * demo：项目的后端接口示例（Spring boot + Diboot v3）
  * demo/libs：devtools 开发工具包
  * diboot-admin-ui：PC端前端项目（Vue3+TypeScript+Vite+Pinia+ElementPlus）
  * diboot-mobile-ui：移动端前端项目（Vant4）

### 1. 项目配置说明

* 配置demo应用的相关参数：
  * demo为后端接口项目，配置启动步骤：
    * 修改 `application-dev.yml` 中的数据库连接信息，改为本地连接
    * 配置 devtools 代码生成相关参数：
      * 将 `diboot.init-sql` 设置为 true ，以开启自动初始化
      * 配置 `diboot.file.storage-directory` 本地文件存储路径
      * 配置 `diboot.iam.anon-urls` 中加入 `/diboot/**` 配置项，以放行devtools接口免登录
      * 配置 `diboot.lcdp.devtools` 相关参数，说明（application-dev.yml中已有配置好的示例）：
        * license: 订阅用户配置该项，非订阅用户不配置
        * codes-author: 开发者姓名
        * codes-copyright: 代码版权归属
        * codes-version: 代码版本
        * output-path: 后端代码生成路径
        * output-path-admin-ui: 前端代码生成路径
 
### 2. 项目启动与初始化

* 2.1 启动后端项目：
  * 配置IDEA服务（`DemoApplication`为启动类），运行demo后台程序，此时diboot各starter会自动执行初始化SQL。
* 前端项目：
  *
* 2.2 启动 diboot-admin-ui PC端前端项目，步骤：
  * 命令行切换到 diboot-admin-ui 目录下， 执行install命令安装依赖组件
  ```cmd
  pnpm install
  ```
  执行 run dev 命令运行
  ```cmd
  pnpm run dev
  ```
  * 前端启动后登录系统(默认管理员登录账号： `admin` 密码：`123456`)

  > 如需启动移动端前端，切换至 diboot-mobile-ui 移动端前端项目下，参考以上步骤操作即可。

### 3. devtools 开发工具使用
* 项目启动完毕，可以在控制台看到打印的 devtools 的入口链接，点击链接即可进入devtools。
~~~
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
-- Diboot devtools v3.x.x 初始化完成: 
-> URL: http://localhost:8080/api/diboot/index.html
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
~~~
> 注：
> 1. 如您为订阅用户，请配置 `diboot.lcdp.devtools.license` 以便使用devtools全部功能。
> 2. 如启动过程报错：javax.net.ssl.SSLHandshakeException，无法连接devtools服务器：需升级到JDK8的最新小版本 [下载最新版 JDK8](https://www.oracle.com/java/technologies/downloads/)。


## 其他参考文档
* [V3 技术文档](http://www.diboot.com)

## 技术交流：
  * **VIP技术支持QQ群**（捐助/付费用户尊享）: [931266830]()
  
  * 技术交流QQ群: [731690096]() 
  
  * 技术交流微信群 加: [wx20201024]() (备注diboot)
  
## 关注公众号，diboot动态早知道
<img src="https://www.diboot.com/qrcode_gzh.jpg" width = "200" height = "200" alt="关注公众号" align=center />