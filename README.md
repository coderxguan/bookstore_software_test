# 书海阁

这是一个前后端分离的在线图书馆系统，用于学习和测试Java Spring Boot及Vue 3技术栈。

## 项目结构
```
bookstore/
├── backend/             # 后端Spring Boot项目
│   └── bookstore-backend/
│       ├── src/
│       │   ├── main/    # 主要代码
│       │   └── test/    # 测试代码
│       └── pom.xml
│   
└── frontend/            # 前端Vue 3项目
    ├── public/
    ├── src/
    ├── index.html
    ├── package.json
    └── vite.config.js
```

## 技术栈

### 后端
- Spring Boot 3.x
- MyBatis-Plus
- MySQL
- Maven
- JUnit 5 (测试框架)
- JaCoCo (代码覆盖率)
- Allure (测试报告)

### 前端
- Vue 3 (Composition API)
- Vite
- Element Plus
- Vue Router
- Pinia (状态管理)
- Axios

## 功能特性

- 用户登录/注册/注销
- 图书查询和管理
- 图书收藏功能
- 响应式界面设计

## 开发指南

### 数据库配置

已经配置云服务器数据库, 无需本地配置：
- 主机地址：47.121.181.198:3307
- 数据库名：bookstore
- 用户名：root
- 密码：123456

### 后端启动

```bash
# 进入后端项目目录
cd bookstore/backend/bookstore-backend

# 编译并启动项目
mvn clean install
mvn spring-boot:run

# 或直接使用IDE打开项目并运行
```

后端API将在 http://localhost:8080/api 上提供服务。

### 前端启动

```bash
# 进入前端项目目录
cd bookstore/frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端界面将在 http://localhost:3000 上可访问。

## 测试与报告

本项目采用全面的测试策略，结合黑盒和白盒测试方法：

### 单元测试和集成测试

项目包含针对四个主要模块的测试用例：
- 图书管理服务 `BookServiceTest`
- 图书搜索功能 `BookSearchTest`
- 用户收藏功能 `FavoriteServiceTest`
- 用户账户管理 `UserServiceTest`

### 测试执行与报告生成

```bash
# 运行测试并生成报告
cd bookstore/backend/bookstore-backend
mvn clean test

# 生成Surefire测试报告
mvn surefire-report:report

# 生成JaCoCo代码覆盖率报告
mvn jacoco:report

# 生成Allure可视化报告
mvn allure:report
mvn allure:serve  # 启动本地服务查看报告
```

### 测试报告位置

- Surefire测试报告：`target/site/surefire-report.html`
- JaCoCo代码覆盖率报告：`target/site/jacoco/index.html`
- Allure测试报告：`target/site/allure-maven-plugin/index.html`

### 缺陷报告

在测试过程中发现的缺陷已记录在专门的缺陷报告文档中：

- **缺陷报告文档**：[test_reports/缺陷报告.md](test_reports/03-缺陷报告)
- **缺陷总数**：10个
- **主要问题类别**：
  - 输入验证不足
  - 边界值处理不完善
  - 参照完整性问题
  - 搜索功能逻辑错误

缺陷报告包含了每个问题的详细描述、严重级别、优先级以及建议的修复方案。这些缺陷已被提交给开发团队进行跟踪和修复。

## 安全及数据保护

- 用户密码使用加密存储
- 数据传输采用HTTPS协议
- 提供用户会话管理和权限控制

## 许可证

本项目仅用于教育和学习目的。

## 贡献指南

欢迎提交问题报告和改进建议。

## 访问应用

启动后端和前端后，在浏览器中访问 http://localhost:3000 即可使用应用

## 测试账户

- 用户名: admin
- 密码: 123456

## 接口文档

所有接口都以 `/api` 为前缀

### 认证接口

- POST `/api/auth/login` - 用户登录
- POST `/api/auth/register` - 用户注册
- POST `/api/auth/logout` - 用户登出

### 图书接口

- GET `/api/books` - 获取图书列表
- GET `/api/books/{id}` - 获取图书详情
- POST `/api/books` - 创建新图书
- PUT `/api/books/{id}` - 更新图书
- DELETE `/api/books/{id}` - 删除图书

### 收藏接口

- GET `/api/favorites` - 获取用户收藏列表
- POST `/api/favorites` - 添加收藏
- DELETE `/api/favorites/{id}` - 取消收藏 