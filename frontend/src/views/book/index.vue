<template>
  <div class="book-container">
    <div class="page-header">
      <h2>图书管理</h2>
      <el-button type="success" @click="handleAddBook">添加图书</el-button>
    </div>
    
    <div class="search-container">
      <div class="search-bar">
        <el-input
          v-model="searchQuery"
          placeholder="搜索图书（标题、作者、分类）"
          clearable
          class="search-input"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
        
        <el-select v-model="sortType" placeholder="排序方式" class="sort-select" @change="handleSortChange">
          <el-option label="默认排序" value="default" />
          <el-option label="收藏量高到低" value="desc" />
          <el-option label="收藏量低到高" value="asc" />
        </el-select>
      </div>
    </div>
    
    <!-- 卡片式图书列表 -->
    <div v-loading="loading" class="book-grid">
      <el-empty v-if="bookList.length === 0" description="暂无图书" />
      <el-card 
        v-for="book in bookList" 
        :key="book.id" 
        class="book-card"
        :body-style="{ padding: '0' }"
      >
        <div class="book-card-header">
          <h3 class="book-title" :title="book.name">{{ book.name }}</h3>
        </div>
        <div class="book-card-content">
          <div class="book-info">
            <div class="book-author">
              <span class="label">作者：</span>
              <span class="value">{{ book.author }}</span>
            </div>
            <div class="book-category">
              <span class="label">分类：</span>
              <span class="value">{{ book.category }}</span>
            </div>
            <div class="book-price">
              <span class="label">价格：</span>
              <span class="value">￥{{ book.price }}</span>
            </div>
            <div class="book-favorites">
              <span class="label">收藏量：</span>
              <span class="value">{{ book.favoriteCount || 0 }}</span>
            </div>
            <div class="book-desc">
              <span class="label">描述：</span>
              <span class="value">{{ book.description }}</span>
            </div>
          </div>
        </div>
        <div class="book-card-footer">
          <el-tooltip content="查看详情" placement="top">
            <el-button type="info" circle @click="handleViewDetail(book)">
              <el-icon :size="16"><View /></el-icon>
            </el-button>
          </el-tooltip>
          
          <el-tooltip :content="favoriteStatus[book.id] ? '取消收藏' : '收藏'" placement="top">
            <el-button 
              :type="favoriteStatus[book.id] ? 'warning' : 'primary'" 
              circle
              @click="handleFavorite(book)"
            >
              <el-icon :size="16"><Star /></el-icon>
            </el-button>
          </el-tooltip>
          
          <el-tooltip content="编辑" placement="top">
            <el-button type="success" circle @click="handleEdit(book)">
              <el-icon :size="16"><Edit /></el-icon>
            </el-button>
          </el-tooltip>
          
          <el-tooltip content="删除" placement="top">
            <el-button type="danger" circle @click="handleDelete(book)">
              <el-icon :size="16"><Delete /></el-icon>
            </el-button>
          </el-tooltip>
        </div>
      </el-card>
    </div>
    
    <!-- 添加/编辑图书对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑图书' : '添加图书'"
      width="500px"
    >
      <el-form :model="bookForm" :rules="bookRules" ref="bookFormRef" label-width="80px">
        <el-form-item label="书名" prop="name">
          <el-input v-model="bookForm.name" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="bookForm.author" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-input v-model="bookForm.category" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="bookForm.price" :precision="2" :step="0.1" :min="0" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="bookForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 图书详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="图书详情"
      width="500px"
    >
      <div v-if="detailBook" class="book-detail">
        <h3>{{ detailBook.name }}</h3>
        <div class="book-info">
          <p><strong>作者：</strong>{{ detailBook.author }}</p>
          <p><strong>分类：</strong>{{ detailBook.category }}</p>
          <p><strong>价格：</strong>￥{{ detailBook.price }}</p>
          <p><strong>收藏量：</strong>{{ detailBook.favoriteCount || 0 }}</p>
          <p><strong>描述：</strong>{{ detailBook.description }}</p>
          <p><strong>收藏状态：</strong>
            <el-tag :type="detailBook.isFavorite ? 'success' : 'info'">
              {{ detailBook.isFavorite ? '已收藏' : '未收藏' }}
            </el-tag>
          </p>
        </div>
        <div class="detail-actions">
          <el-button 
            :type="detailBook.isFavorite ? 'warning' : 'primary'" 
            @click="handleFavoriteInDetail(detailBook)"
            size="large"
          >
            <el-icon :size="18"><Star /></el-icon>
            {{ detailBook.isFavorite ? '取消收藏' : '加入收藏' }}
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBookList, getBookDetail, addBook, updateBook, deleteBook, searchBooks, getBooksByFavoriteCount } from '@/api/book'
import { addFavorite, removeFavorite, isFavorite } from '@/api/favorite'
import { useUserStore } from '@/store/user'
import { Star, View, Edit, Delete } from '@element-plus/icons-vue'

const userStore = useUserStore()
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const isEdit = ref(false)
const bookList = ref([])
const searchQuery = ref('')
const bookFormRef = ref(null)
const detailBook = ref(null)
const favoriteStatus = ref({})
const sortType = ref('default') // 默认排序，'desc'收藏量高到低，'asc'收藏量低到高

// 书籍表单
const bookForm = reactive({
  id: null,
  name: '',
  author: '',
  category: '',
  price: 0,
  description: ''
})

const bookRules = {
  name: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
  category: [{ required: true, message: '请输入分类', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

onMounted(() => {
  fetchBookList()
})

const fetchBookList = async () => {
  loading.value = true
  try {
    let res
    if (sortType.value === 'default') {
      res = await getBookList({
        query: searchQuery.value
      })
    } else {
      res = await getBooksByFavoriteCount({
        query: searchQuery.value,
        sortType: sortType.value
      })
    }
    
    if (res.code === 200) {
      bookList.value = res.data.list
      // 当获取到书籍列表后，立即加载它们的收藏状态
      await loadFavoriteStatus()
    } else {
      ElMessage.error(res.message || '获取图书列表失败')
    }
  } catch (error) {
    console.error('获取图书列表失败:', error)
    ElMessage.error('获取图书列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  fetchBookList()
}

const handleSortChange = () => {
  fetchBookList()
}

const handleViewDetail = async (row) => {
  try {
    // 获取图书详情
    const res = await getBookDetail(row.id)
    if (res.code === 200) {
      detailBook.value = res.data
      
      // 检查收藏状态
      const favoriteRes = await isFavorite(row.id)
      if (favoriteRes.code === 200) {
        detailBook.value.isFavorite = favoriteRes.data
      } else {
        detailBook.value.isFavorite = false
      }
      
      detailDialogVisible.value = true
    } else {
      ElMessage.error(res.message || '获取图书详情失败')
    }
  } catch (error) {
    console.error('获取图书详情失败:', error)
    ElMessage.error('获取图书详情失败')
  }
}

const handleFavorite = async (row) => {
  try {
    // 获取当前收藏状态
    const isFav = favoriteStatus.value[row.id] || false
    
    if (isFav) {
      // 如果已收藏，则取消收藏
      const res = await removeFavorite(row.id)
      if (res.code === 200) {
        ElMessage.success('已取消收藏')
        // 更新状态
        favoriteStatus.value[row.id] = false
        // 刷新列表数据
        fetchBookList()
      } else {
        ElMessage.error(res.message || '取消收藏失败')
      }
    } else {
      // 如果未收藏，则添加收藏
      const res = await addFavorite(row.id)
      if (res.code === 200) {
        ElMessage.success('收藏成功')
        // 更新状态
        favoriteStatus.value[row.id] = true
        // 刷新列表数据
        fetchBookList()
      } else {
        ElMessage.error(res.message || '收藏失败')
      }
    }
  } catch (error) {
    console.error('操作收藏失败:', error)
    ElMessage.error('操作收藏失败')
  }
}

const handleFavoriteInDetail = async (book) => {
  try {
    if (book.isFavorite) {
      // 取消收藏
      const res = await removeFavorite(book.id)
      if (res.code === 200) {
        ElMessage.success('已取消收藏')
        book.isFavorite = false
        // 刷新列表数据
        fetchBookList()
      } else {
        ElMessage.error(res.message || '取消收藏失败')
      }
    } else {
      // 添加收藏
      const res = await addFavorite(book.id)
      if (res.code === 200) {
        ElMessage.success('收藏成功')
        book.isFavorite = true
        // 刷新列表数据
        fetchBookList()
      } else {
        ElMessage.error(res.message || '收藏失败')
      }
    }
  } catch (error) {
    console.error('操作收藏失败:', error)
    ElMessage.error('操作收藏失败')
  }
}

const handleAddBook = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  resetForm()
  Object.assign(bookForm, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该图书吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await deleteBook(row.id)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchBookList()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const resetForm = () => {
  if (bookFormRef.value) {
    bookFormRef.value.resetFields()
  }
  bookForm.id = null
  bookForm.name = ''
  bookForm.author = ''
  bookForm.category = ''
  bookForm.price = 0
  bookForm.description = ''
}

const submitForm = async () => {
  bookFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (isEdit.value) {
          // 更新图书
          const res = await updateBook(bookForm.id, bookForm)
          if (res.code === 200) {
            ElMessage.success('更新成功')
            dialogVisible.value = false
            fetchBookList()
          } else {
            ElMessage.error(res.message || '更新失败')
          }
        } else {
          // 添加图书
          const res = await addBook(bookForm)
          if (res.code === 200) {
            ElMessage.success('添加成功')
            dialogVisible.value = false
            fetchBookList()
          } else {
            ElMessage.error(res.message || '添加失败')
          }
        }
      } catch (error) {
        console.error(isEdit.value ? '更新失败:' : '添加失败:', error)
        ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
      } finally {
        submitLoading.value = false
      }
    } else {
      return false
    }
  })
}

const loadFavoriteStatus = async () => {
  favoriteStatus.value = {} // 重置收藏状态
  try {
    const promises = bookList.value.map(async (book) => {
      const res = await isFavorite(book.id)
      if (res.code === 200) {
        favoriteStatus.value[book.id] = res.data
      }
    })
    await Promise.all(promises)
  } catch (error) {
    console.error('获取收藏状态失败:', error)
  }
}
</script>

<style scoped>
.book-container {
  padding: 10px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
}

.search-container {
  margin-bottom: 15px;
}

.search-bar {
  display: flex;
  gap: 10px;
}

.search-input {
  flex: 1;
}

.sort-select {
  width: 130px;
}

.book-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 15px;
  margin-bottom: 20px;
  min-height: 500px; /* Ensure consistent height for all pages */
}

/* 不同屏幕尺寸下的响应式调整 */
@media (max-width: 1400px) {
  .book-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 1100px) {
  .book-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .book-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 500px) {
  .book-grid {
    grid-template-columns: 1fr;
  }
}

.book-card {
  transition: all 0.3s;
  height: 100%;
  display: flex;
  flex-direction: column;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.book-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 10px rgba(0, 0, 0, 0.1);
}

.book-card-header {
  background-color: #f2f6fc;
  padding: 8px 12px;
  border-bottom: 1px solid #ebeef5;
}

.book-title {
  margin: 0;
  font-size: 15px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.book-card-content {
  padding: 10px;
  flex: 1;
}

.book-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.book-author, .book-category, .book-price, .book-favorites {
  display: flex;
  align-items: center;
  font-size: 13px;
}

.label {
  color: #606266;
  width: 55px;
  flex-shrink: 0;
}

.value {
  color: #303133;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.book-desc {
  margin-top: 5px;
}

.book-desc .label {
  display: block;
  width: auto;
  margin-bottom: 2px;
  font-size: 13px;
}

.book-desc .value {
  display: block;
  white-space: normal;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
  color: #606266;
  font-size: 12px;
  line-height: 1.4;
}

.book-card-footer {
  padding: 10px 12px;
  display: flex;
  justify-content: center;
  gap: 12px;
  border-top: 1px solid #ebeef5;
  background-color: #f9fafc;
  flex-wrap: wrap;
}

/* 新增图标按钮样式 */
:deep(.el-button.is-circle) {
  width: 40px;
  height: 40px;
  padding: 8px;
}

:deep(.el-icon) {
  margin: 0;
}

.book-detail {
  padding: 10px;
}

.book-info {
  margin: 15px 0;
}

.detail-actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 