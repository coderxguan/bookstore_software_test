import axios from 'axios'
import { ElMessage } from 'element-plus'

/**
 * 创建axios实例
 * 配置基础URL和超时时间
 */
const service = axios.create({
  baseURL: '/api',  // API基础路径，与后端接口路径匹配
  timeout: 10000    // 请求超时时间：10秒
})

/**
 * 请求拦截器
 * 在请求发送前处理请求配置
 */
service.interceptors.request.use(
  config => {
    // 这里可以添加认证令牌等通用请求配置
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 * 在接收到响应后统一处理响应数据和错误
 */
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果状态码不是200，视为错误
    if (res.code !== 200) {
      ElMessage({
        message: res.message || '请求失败',
        type: 'error',
        duration: 5 * 1000
      })
      return Promise.reject(new Error(res.message || '请求失败'))
    } else {
      return res
    }
  },
  error => {
    console.error('响应错误:', error)
    const message = error.response?.data?.message || error.message || '请求失败'
    
    ElMessage({
      message,
      type: 'error',
      duration: 5 * 1000
    })
    
    return Promise.reject(error)
  }
)

export default service 