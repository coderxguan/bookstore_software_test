<template>
  <div class="login-container">
    <!-- 背景图案 -->
    <div class="login-background">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
      <div class="wave"></div>
    </div>
    
    <!-- 登录卡片 -->
    <div class="login-card-wrapper">
      <el-card class="login-card" :body-style="{ padding: '0' }">
        <div class="login-header">
          <div class="logo">
            <el-icon><Reading /></el-icon>
          </div>
          <h1 class="title">书海阁</h1>
          <p class="subtitle">探索知识的海洋，畅游书籍的世界</p>
        </div>
        
        <div class="login-body">
          <el-tabs v-model="activeTab" class="login-tabs">
            <!-- 登录表单 -->
            <el-tab-pane label="登录" name="login">
              <el-form 
                :model="loginForm" 
                :rules="loginRules" 
                ref="loginFormRef" 
                class="login-form"
                :hide-required-asterisk="true"
                @keyup.enter="handleLogin"
              >
                <el-form-item prop="username">
                  <el-input 
                    v-model="loginForm.username" 
                    placeholder="请输入用户名" 
                    :prefix-icon="User"
                    size="large"
                  />
                </el-form-item>
                <el-form-item prop="password">
                  <el-input 
                    v-model="loginForm.password" 
                    type="password" 
                    placeholder="请输入密码" 
                    show-password 
                    :prefix-icon="Lock"
                    size="large"
                  />
                </el-form-item>
                <div class="form-actions">
                  <el-checkbox v-model="rememberMe">记住我</el-checkbox>
                </div>
                <el-form-item>
                  <el-button 
                    type="primary" 
                    :loading="loginLoading" 
                    class="form-button" 
                    @click="handleLogin"
                    size="large"
                    round
                  >
                    登录
                  </el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            
            <!-- 注册表单 -->
            <el-tab-pane label="注册" name="register">
              <el-form 
                :model="registerForm" 
                :rules="registerRules" 
                ref="registerFormRef" 
                class="login-form"
                :hide-required-asterisk="true"
                @keyup.enter="handleRegister"
              >
                <el-form-item prop="username">
                  <el-input 
                    v-model="registerForm.username" 
                    placeholder="请设置用户名" 
                    :prefix-icon="User"
                    size="large"
                  />
                </el-form-item>
                <el-form-item prop="password">
                  <el-input 
                    v-model="registerForm.password" 
                    type="password" 
                    placeholder="请设置密码" 
                    show-password 
                    :prefix-icon="Lock"
                    size="large"
                  />
                </el-form-item>
                <el-form-item prop="confirmPassword">
                  <el-input 
                    v-model="registerForm.confirmPassword" 
                    type="password" 
                    placeholder="请确认密码" 
                    show-password 
                    :prefix-icon="Lock"
                    size="large"
                  />
                </el-form-item>
                <el-form-item>
                  <el-button 
                    type="primary" 
                    :loading="registerLoading" 
                    class="form-button" 
                    @click="handleRegister"
                    size="large"
                    round
                  >
                    注册
                  </el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </div>
        
        <div class="login-footer">
          <p>© {{ currentYear }} 书海阁 - 版权所有</p>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { User, Lock, Reading } from '@element-plus/icons-vue'
import { register as registerApi } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref(null)
const registerFormRef = ref(null)
const loginLoading = ref(false)
const registerLoading = ref(false)
const activeTab = ref('login')
const rememberMe = ref(false)
const currentYear = computed(() => new Date().getFullYear())

const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ]
}

const handleLogin = () => {
  loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loginLoading.value = true
      try {
        await userStore.login(loginForm)
        ElMessage({
          message: '登录成功，正在跳转...',
          type: 'success',
          duration: 1500
        })
        setTimeout(() => {
          router.push('/book')
        }, 800)
      } catch (error) {
        console.error('Login failed:', error)
        ElMessage.error('登录失败：' + (error.message || '未知错误'))
      } finally {
        loginLoading.value = false
      }
    } else {
      return false
    }
  })
}

const handleRegister = () => {
  registerFormRef.value.validate(async (valid) => {
    if (valid) {
      registerLoading.value = true
      try {
        // 调用注册API
        const response = await registerApi({
          username: registerForm.username,
          password: registerForm.password
        })
        
        if (response.code === 200) {
          ElMessage({
            message: '注册成功，请登录',
            type: 'success',
            duration: 1500
          })
          // 清空表单
          registerFormRef.value.resetFields()
          // 切换到登录页
          activeTab.value = 'login'
          // 预填用户名
          loginForm.username = registerForm.username
        } else {
          ElMessage.error(response.message || '注册失败')
        }
      } catch (error) {
        console.error('Registration failed:', error)
        ElMessage.error('注册失败：' + (error.message || '未知错误'))
      } finally {
        registerLoading.value = false
      }
    } else {
      return false
    }
  })
}
</script>

<style scoped>
/* 登录页整体容器 */
.login-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  width: 100vw;
  margin: 0;
  padding: 0;
  overflow: hidden;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

/* 背景动画 */
.login-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.6;
}

.circle-1 {
  top: -100px;
  right: -100px;
  width: 300px;
  height: 300px;
  background: linear-gradient(to right, #4facfe 0%, #00f2fe 100%);
  animation: float 15s ease-in-out infinite;
}

.circle-2 {
  bottom: -150px;
  left: -150px;
  width: 500px;
  height: 500px;
  background: linear-gradient(to right, #667eea 0%, #764ba2 100%);
  animation: float 20s ease-in-out infinite;
}

.circle-3 {
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 400px;
  height: 400px;
  background: linear-gradient(to right, #43e97b 0%, #38f9d7 100%);
  opacity: 0.2;
  animation: pulse 8s ease-in-out infinite;
}

.wave {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 100px;
  background: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320"><path fill="%23ffffff" fill-opacity="0.3" d="M0,96L48,112C96,128,192,160,288,160C384,160,480,128,576,112C672,96,768,96,864,112C960,128,1056,160,1152,160C1248,160,1344,128,1392,112L1440,96L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"></path></svg>');
  background-size: cover;
  animation: wave 15s linear infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-30px) rotate(5deg);
  }
}

@keyframes pulse {
  0%, 100% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 0.2;
  }
  50% {
    transform: translate(-50%, -50%) scale(1.2);
    opacity: 0.1;
  }
}

@keyframes wave {
  0% {
    background-position-x: 0;
  }
  100% {
    background-position-x: 1440px;
  }
}

/* 登录卡片样式 */
.login-card-wrapper {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 480px;
  padding: 0;
  animation: fadeIn 1s ease-out;
}

.login-card {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  background-color: rgba(255, 255, 255, 0.9);
  transition: all 0.3s ease;
}

.login-card:hover {
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.15);
  transform: translateY(-5px);
}

.login-header {
  padding: 30px 0;
  text-align: center;
  background: linear-gradient(135deg, #68BFFF 0%, #0085FF 100%);
  color: white;
}

.logo {
  display: inline-flex;
  justify-content: center;
  align-items: center;
  width: 70px;
  height: 70px;
  margin-bottom: 15px;
  border-radius: 50%;
  background-color: white;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.logo .el-icon {
  font-size: 36px;
  color: #0085FF;
}

.title {
  margin: 10px 0 5px;
  font-size: 28px;
  font-weight: 600;
  letter-spacing: 1px;
}

.subtitle {
  margin: 0;
  font-size: 14px;
  opacity: 0.8;
}

.login-body {
  padding: 20px 0;
  width: 100%;
}

.login-tabs {
  padding: 0 20px;
}

:deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background-color: rgba(0, 0, 0, 0.05);
}

:deep(.el-tabs__active-bar) {
  background-color: #0085FF;
  height: 3px;
  border-radius: 3px;
}

:deep(.el-tabs__item) {
  font-size: 16px;
  color: #606266;
  padding: 0 20px 10px !important;
}

:deep(.el-tabs__item.is-active) {
  color: #0085FF;
  font-weight: 600;
}

.login-form {
  padding: 20px 30px 10px;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.form-button {
  height: 48px;
  font-size: 16px;
  margin-top: 10px;
  width: 100%;
  border: none;
  background: linear-gradient(135deg, #68BFFF 0%, #0085FF 100%);
  transition: all 0.3s ease;
}

.form-button:hover {
  background: linear-gradient(135deg, #0085FF 0%, #0066CC 100%);
  box-shadow: 0 5px 15px rgba(0, 133, 255, 0.3);
  transform: translateY(-2px);
}

.login-footer {
  padding: 15px 0;
  text-align: center;
  color: #909399;
  font-size: 12px;
  border-top: 1px solid #f0f0f0;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

:deep(.el-input__inner) {
  height: 48px;
}

:deep(.el-input__prefix) {
  color: #909399;
}

:deep(.el-form-item) {
  margin-bottom: 24px;
}

/* 响应式调整 */
@media (max-width: 500px) {
  .login-card-wrapper {
    max-width: 95%;
  }
}

@media (max-width: 576px) {
  .login-card-wrapper {
    padding: 0;
    max-width: 95%;
  }
  
  .login-card {
    border-radius: 8px;
    width: 100%;
  }
  
  .login-header {
    padding: 20px 0;
  }
  
  .logo {
    width: 60px;
    height: 60px;
  }
  
  .title {
    font-size: 24px;
  }
  
  .login-form {
    padding: 15px 20px 5px;
  }
}
</style> 