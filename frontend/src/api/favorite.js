import request from './request'

/**
 * 添加收藏
 * @param {Number} bookId 图书ID
 * @returns {Promise}
 */
export function addFavorite(bookId) {
  return request({
    url: '/favorites',
    method: 'post',
    data: { bookId }
  })
}

/**
 * 移除收藏
 * @param {Number} bookId 图书ID
 * @returns {Promise}
 */
export function removeFavorite(bookId) {
  return request({
    url: `/favorites/${bookId}`,
    method: 'delete'
  })
}

/**
 * 检查是否已收藏
 * @param {Number} bookId 图书ID
 * @returns {Promise}
 */
export function isFavorite(bookId) {
  return request({
    url: `/favorites/check/${bookId}`,
    method: 'get'
  })
}

/**
 * 获取用户收藏列表
 * @returns {Promise}
 */
export function getFavorites() {
  return request({
    url: '/favorites',
    method: 'get'
  })
} 