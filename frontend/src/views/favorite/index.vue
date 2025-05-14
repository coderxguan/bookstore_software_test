<template>
  <div class="favorite-container">
    <div class="page-header">
      <h2>我的收藏</h2>
    </div>
    
    <el-table :data="favoriteList" stripe border style="width: 100%" v-loading="loading">
      <el-table-column type="index" width="50" />
      <el-table-column prop="name" label="书名" />
      <el-table-column prop="author" label="作者" />
      <el-table-column prop="category" label="分类" />
      <el-table-column prop="price" label="价格" width="100" />
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button type="primary" size="small" @click="handleViewDetail(scope.row)">查看</el-button>
          <el-button type="danger" size="small" @click="handleRemoveFavorite(scope.row)">取消收藏</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 图书详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="图书详情"
      width="600px"
    >
      <div v-if="detailBook" class="book-detail">
        <h3>{{ detailBook.name }}</h3>
        <div class="book-info">
          <p><strong>作者：</strong>{{ detailBook.author }}</p>
          <p><strong>分类：</strong>{{ detailBook.category }}</p>
          <p><strong>价格：</strong>￥{{ detailBook.price }}</p>
          <p><strong>描述：</strong>{{ detailBook.description }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBookDetail } from '@/api/book'
import { getFavorites, removeFavorite } from '@/api/favorite'

const loading = ref(false)
const favoriteList = ref([])
const detailDialogVisible = ref(false)
const detailBook = ref(null)

onMounted(() => {
  fetchFavorites()
})

const fetchFavorites = async () => {
  loading.value = true
  try {
    const res = await getFavorites()
    
    if (res.code === 200) {
      favoriteList.value = res.data.list
    } else {
      ElMessage.error(res.message || '获取收藏列表失败')
    }
  } catch (error) {
    console.error('获取收藏列表失败:', error)
    ElMessage.error('获取收藏列表失败')
  } finally {
    loading.value = false
  }
}

const handleViewDetail = async (row) => {
  try {
    const res = await getBookDetail(row.id)
    if (res.code === 200) {
      detailBook.value = res.data
      detailDialogVisible.value = true
    } else {
      ElMessage.error(res.message || '获取图书详情失败')
    }
  } catch (error) {
    console.error('获取图书详情失败:', error)
    ElMessage.error('获取图书详情失败')
  }
}

const handleRemoveFavorite = (row) => {
  ElMessageBox.confirm('确定要取消收藏该图书吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await removeFavorite(row.id)
      if (res.code === 200) {
        ElMessage.success('取消收藏成功')
        fetchFavorites()
      } else {
        ElMessage.error(res.message || '取消收藏失败')
      }
    } catch (error) {
      console.error('取消收藏失败:', error)
      ElMessage.error('取消收藏失败')
    }
  }).catch(() => {})
}
</script>

<style scoped>
.favorite-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.book-detail {
  padding: 10px;
}

.book-info {
  margin: 15px 0;
}
</style> 