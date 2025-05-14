import request from './request'

/**
 * 获取图书列表
 * 支持搜索查询功能
 * @param {Object} params 请求参数
 * @returns {Promise} 图书列表响应
 */
export function getBookList(params) {
  const requestParams = {
    query: params?.query || ''
  };
  
  return request({
    url: '/books',
    method: 'get',
    params: requestParams
  })
}

/**
 * 高级搜索图书
 * 支持按标题、作者、类别进行多条件搜索
 * @param {Object} params 搜索参数 (title, author, category)
 * @returns {Promise} 搜索结果响应
 */
export function searchBooks(params) {
  return request({
    url: '/books/search',
    method: 'get',
    params
  })
}

/**
 * 获取图书详情
 * @param {Number} id 图书ID
 * @returns {Promise} 图书详情响应
 */
export function getBookDetail(id) {
  return request({
    url: `/books/${id}`,
    method: 'get'
  })
}

/**
 * 添加图书
 * @param {Object} data 图书数据 (name, author, category, price, description等)
 * @returns {Promise} 添加结果响应
 */
export function addBook(data) {
  return request({
    url: '/books',
    method: 'post',
    data
  })
}

/**
 * 更新图书
 * @param {Number} id 图书ID
 * @param {Object} data 图书更新数据
 * @returns {Promise} 更新结果响应
 */
export function updateBook(id, data) {
  return request({
    url: `/books/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除图书
 * @param {Number} id 图书ID
 * @returns {Promise} 删除结果响应
 */
export function deleteBook(id) {
  return request({
    url: `/books/${id}`,
    method: 'delete'
  })
}

/**
 * 按收藏量排序获取图书列表
 * @param {Object} params 查询参数
 * @returns {Promise} 排序后的图书列表响应
 */
export function getBooksByFavoriteCount(params) {
  const requestParams = {
    query: params?.query || '',
    sortType: params?.sortType || 'desc'  // 默认降序(高到低)
  };
  
  return request({
    url: '/books/favorite-sort',
    method: 'get',
    params: requestParams
  })
} 