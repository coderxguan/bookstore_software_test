# 书海阁

这是一个前后端分离的在线图书馆系统，用于学习和测试Java Spring Boot及Vue 3技术栈。

## 项目结构
test
```
bookstore/
├── backend/             # 后端Spring Boot项目
│   └── bookstore-backend/
│       ├── src/
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
- MySQL / H2 (测试)
- Maven

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

已经配置云服务器数据库, 无需配置

### 后端启动

```bash
# 进入后端项目目录
cd bookstore/backend/bookstore-backend

# 编译并启动项目
mvn clean install
mvn spring-boot:run

# 或直接使用IDE打开项目并运行
```

后端服务将在 http://localhost:8080 上运行

### 前端启动

```bash
# 进入前端项目目录
cd bookstore/frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build
```

前端开发服务器将在 http://localhost:5173 上运行

## 访问应用

启动后端和前端后，在浏览器中访问 http://localhost:5173 即可使用应用

## 测试账户

- 用户名: admin
- 密码: 123456

## 接口文档

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

## 测试策略

本项目适合进行多种测试实践：

1. 单元测试：针对服务层和工具类
2. 集成测试：验证REST API功能
3. 前端组件测试：Vue组件测试
4. E2E测试：模拟用户行为测试完整流程 