import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/book'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue')
  },
  {
    path: '/book',
    name: 'Book',
    component: () => import('@/views/book/index.vue')
  },
  {
    path: '/favorite',
    name: 'Favorite',
    component: () => import('@/views/favorite/index.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router 