<template>
  <div class="app-container" :class="{ 'theme-light': true }">
    <!-- 只在非登录页面显示头部导航栏 -->
    <header class="app-header" v-if="!isLoginPage">
      <div class="left-section">
        <div class="logo">
          <el-icon class="logo-icon"><Reading /></el-icon>
          书海阁
        </div>
        
        <div class="nav-links">
          <router-link to="/book" class="nav-link" :class="{ 'active': isActive('/book') }">
            <el-icon><Document /></el-icon>
            图书列表
          </router-link>
          <router-link to="/favorite" class="nav-link" :class="{ 'active': isActive('/favorite') }">
            <el-icon><Star /></el-icon>
            我的收藏
          </router-link>
        </div>
      </div>
      
      <div class="right-section">
        <el-button type="danger" size="small" class="logout-btn" @click="handleLogout">
          <el-icon><SwitchButton /></el-icon>
          退出登录
        </el-button>
      </div>
    </header>
    
    <main :class="{ 'full-height': isLoginPage }">
      <el-config-provider>
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-config-provider>
    </main>
    
    <footer class="app-footer" v-if="!isLoginPage">
      <div class="footer-content">
        <p>© {{ currentYear }} 书海阁 - 版权所有</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElConfigProvider } from 'element-plus'
import { Document, Star, SwitchButton, Reading } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 当前年份
const currentYear = computed(() => new Date().getFullYear())

// 检查是否在登录页面
const isLoginPage = computed(() => {
  return route.path === '/login'
})

// 检查当前路由是否活跃
const isActive = (path) => {
  return route.path === path
}

// 处理退出登录
const handleLogout = () => {
  // 清除本地存储
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  
  ElMessage({
    message: '退出登录成功',
    type: 'success',
    duration: 2000,
    showClose: true
  })
  router.push('/login')
}
</script>

<style>
/* 全局样式 */
:root {
  --primary-color: #409EFF;
  --primary-light: #ecf5ff;
  --primary-hover: #66b1ff;
  --text-color: #303133;
  --text-color-light: #606266;
  --border-color: #DCDFE6;
  --bg-color: #f5f7fa;
  --header-height: 60px;
  --border-radius: 4px;
  --transition-time: 0.3s;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen,
    Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
  color: var(--text-color);
  background-color: var(--bg-color);
}

.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.theme-light {
  --primary-color: #409EFF;
  --bg-color: #f5f7fa;
}

/* 头部样式 */
.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background-color: #fff;
  height: var(--header-height);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.left-section {
  display: flex;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: 600;
  color: var(--primary-color);
  margin-right: 40px;
  letter-spacing: 0.5px;
}

.logo-icon {
  margin-right: 8px;
  font-size: 24px;
}

.nav-links {
  display: flex;
  gap: 20px;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 6px;
  text-decoration: none;
  color: var(--text-color-light);
  font-size: 15px;
  padding: 8px 12px;
  border-radius: var(--border-radius);
  transition: all var(--transition-time);
  position: relative;
}

.nav-link:hover {
  color: var(--primary-color);
  background-color: var(--primary-light);
}

.nav-link.active {
  color: var(--primary-color);
  font-weight: 500;
  background-color: var(--primary-light);
}

.nav-link.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: var(--primary-color);
  transform: scaleX(0.8);
  transition: transform 0.3s;
}

.nav-link:hover.active::after {
  transform: scaleX(1);
}

.right-section {
  display: flex;
  align-items: center;
}

.logout-btn {
  border-radius: 20px;
  border: 1px solid rgba(220, 53, 69, 0.2);
  transition: all 0.3s;
}

.logout-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 3px 8px rgba(220, 53, 69, 0.2);
}

/* 主内容区域 */
main {
  flex: 1;
  padding: 20px;
  background-color: var(--bg-color);
}

.full-height {
  padding: 0;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7eb 100%);
}

/* 页脚样式 */
.app-footer {
  background-color: #fff;
  padding: 15px 0;
  border-top: 1px solid var(--border-color);
}

.footer-content {
  text-align: center;
  color: var(--text-color-light);
  font-size: 13px;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
