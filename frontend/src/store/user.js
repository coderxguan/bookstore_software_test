import { defineStore } from 'pinia'
import axios from 'axios'
import { login as loginApi, logout as logoutApi } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: JSON.parse(localStorage.getItem('user') || '{}')
  }),
  
  getters: {
    isLoggedIn: (state) => !!state.token,
    currentUser: (state) => state.user || {}
  },
  
  actions: {
    async login(credentials) {
      try {
        const response = await loginApi(credentials)
        if (response.code === 200) {
          const { token, user } = response.data
          this.setUserData(token, user)
          return Promise.resolve(user)
        } else {
          return Promise.reject(new Error(response.message))
        }
      } catch (error) {
        console.error('Login failed in store:', error)
        return Promise.reject(error)
      }
    },
    
    async logout() {
      try {
        await logoutApi()
        this.clearUserData()
        return Promise.resolve()
      } catch (error) {
        console.error('Logout failed in store:', error)
        // 即使API调用失败，也清除用户数据
        this.clearUserData()
        return Promise.reject(error)
      }
    },
    
    setUserData(token, user) {
      if (!token || !user) {
        console.error('Invalid user data, token or user is missing')
        return
      }
      
      this.token = token
      this.user = user
      
      // 存储到localStorage
      try {
        localStorage.setItem('token', token)
        localStorage.setItem('user', JSON.stringify(user))
        // 设置请求头
        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
      } catch (e) {
        console.error('Error storing user data:', e)
      }
    },
    
    clearUserData() {
      this.token = ''
      this.user = {}
      
      // 清除localStorage
      try {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        delete axios.defaults.headers.common['Authorization']
      } catch (e) {
        console.error('Error clearing user data:', e)
      }
    }
  }
}) 