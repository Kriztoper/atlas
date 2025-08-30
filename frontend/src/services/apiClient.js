// Enhanced API client with interceptors, caching, and better error handling
class APIClient {
  constructor(config = {}) {
    this.baseURL = config.baseURL || import.meta.env.VITE_API_URL || 'http://localhost:3001/api'
    this.timeout = config.timeout || 30000
    this.defaultHeaders = {
      'Content-Type': 'application/json',
      ...config.headers
    }
    
    // Request/Response interceptors
    this.requestInterceptors = []
    this.responseInterceptors = []
    this.errorInterceptors = []
    
    // Simple cache implementation
    this.cache = new Map()
    this.cacheTimeout = config.cacheTimeout || 5 * 60 * 1000 // 5 minutes
    
    // Network status tracking
    this.isOnline = navigator.onLine
    this.setupNetworkListeners()
    
    // Pending requests queue for offline support
    this.pendingRequests = []
  }
  
  setupNetworkListeners() {
    window.addEventListener('online', () => {
      this.isOnline = true
      this.flushPendingRequests()
    })
    
    window.addEventListener('offline', () => {
      this.isOnline = false
    })
  }
  
  // Interceptor management
  addRequestInterceptor(interceptor) {
    this.requestInterceptors.push(interceptor)
    return () => {
      const index = this.requestInterceptors.indexOf(interceptor)
      if (index > -1) this.requestInterceptors.splice(index, 1)
    }
  }
  
  addResponseInterceptor(interceptor) {
    this.responseInterceptors.push(interceptor)
    return () => {
      const index = this.responseInterceptors.indexOf(interceptor)
      if (index > -1) this.responseInterceptors.splice(index, 1)
    }
  }
  
  addErrorInterceptor(interceptor) {
    this.errorInterceptors.push(interceptor)
    return () => {
      const index = this.errorInterceptors.indexOf(interceptor)
      if (index > -1) this.errorInterceptors.splice(index, 1)
    }
  }
  
  // Cache management
  getCacheKey(url, options) {
    return `${options.method || 'GET'}:${url}:${JSON.stringify(options.body || {})}`
  }
  
  setCache(key, data) {
    this.cache.set(key, {
      data,
      timestamp: Date.now()
    })
  }
  
  getCache(key) {
    const cached = this.cache.get(key)
    if (!cached) return null
    
    if (Date.now() - cached.timestamp > this.cacheTimeout) {
      this.cache.delete(key)
      return null
    }
    
    return cached.data
  }
  
  clearCache() {
    this.cache.clear()
  }
  
  // Main request method
  async request(endpoint, options = {}) {
    const url = endpoint.startsWith('http') ? endpoint : `${this.baseURL}${endpoint}`
    
    // Check if we're offline
    if (!this.isOnline) {
      throw new APIError('You appear to be offline. Please check your internet connection.', 'OFFLINE', 0)
    }
    
    const config = {
      method: 'GET',
      headers: {
        ...this.defaultHeaders,
        ...options.headers
      },
      ...options
    }
    
    // Apply request interceptors
    let modifiedConfig = config
    for (const interceptor of this.requestInterceptors) {
      modifiedConfig = await interceptor(url, modifiedConfig)
    }
    
    const cacheKey = this.getCacheKey(url, modifiedConfig)
    
    // Check cache for GET requests
    if (modifiedConfig.method === 'GET') {
      const cached = this.getCache(cacheKey)
      if (cached) {
        console.log(`🎯 Cache hit for ${endpoint}`)
        return cached
      }
    }
    
    try {
      console.log(`🌐 ${modifiedConfig.method} ${url}`)
      
      const controller = new AbortController()
      const timeoutId = setTimeout(() => controller.abort(), this.timeout)
      
      const response = await fetch(url, {
        ...modifiedConfig,
        signal: controller.signal
      })
      
      clearTimeout(timeoutId)
      
      // Apply response interceptors
      let modifiedResponse = response
      for (const interceptor of this.responseInterceptors) {
        modifiedResponse = await interceptor(modifiedResponse, url, modifiedConfig)
      }
      
      if (!modifiedResponse.ok) {
        const errorData = await this.parseErrorResponse(modifiedResponse)
        const error = new APIError(
          errorData.message || `HTTP ${modifiedResponse.status}`,
          errorData.code || 'HTTP_ERROR',
          modifiedResponse.status,
          errorData
        )
        
        // Apply error interceptors
        for (const interceptor of this.errorInterceptors) {
          await interceptor(error, url, modifiedConfig)
        }
        
        throw error
      }
      
      const data = await this.parseResponse(modifiedResponse)
      
      // Invalidate cache on successful mutations
      if (modifiedConfig.method !== 'GET') {
        console.log(`✅ Cache invalidated due to ${modifiedConfig.method} request to ${endpoint}`)
        this.clearCache()
      }

      // Cache successful GET requests
      if (modifiedConfig.method === 'GET' && data) {
        this.setCache(cacheKey, data)
      }
      
      return data
      
    } catch (error) {
      if (error.name === 'AbortError') {
        throw new APIError('Request timeout', 'TIMEOUT', 0)
      }
      
      if (error instanceof APIError) {
        throw error
      }
      
      // Network or other errors
      const apiError = new APIError(
        error.message || 'Network error occurred',
        'NETWORK_ERROR',
        0
      )
      
      // Apply error interceptors
      for (const interceptor of this.errorInterceptors) {
        await interceptor(apiError, url, modifiedConfig)
      }
      
      throw apiError
    }
  }
  
  async parseResponse(response) {
    const contentType = response.headers.get('content-type')
    
    if (contentType?.includes('application/json')) {
      return await response.json()
    }
    
    if (response.status === 204 || response.status === 202) {
      return null
    }
    
    return await response.text()
  }
  
  async parseErrorResponse(response) {
    try {
      const contentType = response.headers.get('content-type')
      if (contentType?.includes('application/json')) {
        return await response.json()
      }
      return { message: await response.text() }
    } catch {
      return { message: `HTTP ${response.status} ${response.statusText}` }
    }
  }
  
  // Convenience methods
  get(endpoint, options = {}) {
    return this.request(endpoint, { ...options, method: 'GET' })
  }
  
  post(endpoint, data, options = {}) {
    return this.request(endpoint, {
      ...options,
      method: 'POST',
      body: JSON.stringify(data)
    })
  }
  
  put(endpoint, data, options = {}) {
    return this.request(endpoint, {
      ...options,
      method: 'PUT',
      body: JSON.stringify(data)
    })
  }
  
  patch(endpoint, data, options = {}) {
    return this.request(endpoint, {
      ...options,
      method: 'PATCH',
      body: JSON.stringify(data)
    })
  }
  
  delete(endpoint, options = {}) {
    return this.request(endpoint, { ...options, method: 'DELETE' })
  }
  
  // Retry mechanism
  async requestWithRetry(endpoint, options = {}, retries = 3, delay = 1000) {
    for (let i = 0; i < retries; i++) {
      try {
        return await this.request(endpoint, options)
      } catch (error) {
        if (i === retries - 1 || !this.shouldRetry(error)) {
          throw error
        }
        
        console.log(`🔄 Retrying request ${i + 1}/${retries} after ${delay}ms`)
        await new Promise(resolve => setTimeout(resolve, delay * Math.pow(2, i)))
      }
    }
  }
  
  shouldRetry(error) {
    // Retry on network errors, timeouts, and 5xx errors
    return (
      error.code === 'NETWORK_ERROR' ||
      error.code === 'TIMEOUT' ||
      (error.status >= 500 && error.status < 600)
    )
  }
  
  async flushPendingRequests() {
    if (this.pendingRequests.length === 0) return
    
    console.log(`🔄 Flushing ${this.pendingRequests.length} pending requests`)
    
    const requests = [...this.pendingRequests]
    this.pendingRequests = []
    
    for (const request of requests) {
      try {
        await this.request(request.endpoint, request.options)
        request.resolve()
      } catch (error) {
        request.reject(error)
      }
    }
  }
}

// Custom API Error class
class APIError extends Error {
  constructor(message, code = 'UNKNOWN', status = 0, data = null) {
    super(message)
    this.name = 'APIError'
    this.code = code
    this.status = status
    this.data = data
    this.timestamp = new Date().toISOString()
  }
  
  toJSON() {
    return {
      name: this.name,
      message: this.message,
      code: this.code,
      status: this.status,
      data: this.data,
      timestamp: this.timestamp
    }
  }
}

// Create and configure the default API client
const apiClient = new APIClient()

// Add common interceptors
apiClient.addRequestInterceptor((url, config) => {
  // Add timestamp for cache busting if needed
  if (config.method !== 'GET' && config.bustCache) {
    const separator = url.includes('?') ? '&' : '?'
    url = `${url}${separator}_t=${Date.now()}`
  }
  return { ...config, url }
})

apiClient.addResponseInterceptor((response, url, config) => {
  // Log response times in development
  if (import.meta.env.DEV) {
    console.log(`✅ ${config.method} ${url} - ${response.status} (${response.headers.get('X-Response-Time') || 'unknown'})`)
  }
  return response
})

apiClient.addErrorInterceptor((error, url, config) => {
  // Log errors in development
  if (import.meta.env.DEV) {
    console.error(`❌ ${config.method} ${url} -`, error)
  }
  
  // Global error handling could go here
  return error
})

export { APIClient, APIError }
export default apiClient
