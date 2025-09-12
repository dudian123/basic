const { defineConfig } = require('cypress')

module.exports = defineConfig({
  e2e: {
    // 基础URL
    baseUrl: 'http://localhost:80',
    
    // 视口设置
    viewportWidth: 1280,
    viewportHeight: 720,
    
    // 测试文件模式
    specPattern: '测试脚本/**/*.{js,jsx,ts,tsx}',
    
    // 支持文件
    supportFile: 'cypress/support/e2e.js',
    
    // 截图和视频设置
    screenshotOnRunFailure: true,
    video: true,
    videosFolder: 'cypress/videos',
    screenshotsFolder: 'cypress/screenshots',
    
    // 测试超时设置
    defaultCommandTimeout: 10000,
    requestTimeout: 10000,
    responseTimeout: 10000,
    pageLoadTimeout: 30000,
    
    // 测试重试
    retries: {
      runMode: 2,
      openMode: 0
    },
    
    // 环境变量
    env: {
      // 测试用户凭据
      adminUsername: 'admin',
      adminPassword: 'admin123',
      
      // API基础URL
      apiUrl: 'http://localhost:8080',
      
      // 测试数据库配置
      dbHost: 'localhost',
      dbPort: 3306,
      dbName: 'study',
      
      // 测试标志
      isTestEnvironment: true
    },
    
    setupNodeEvents(on, config) {
      // 任务定义
      on('task', {
        // 数据库清理任务
        cleanupTestData() {
          // 这里可以添加清理测试数据的逻辑
          return null
        },
        
        // 创建测试数据任务
        createTestData(data) {
          // 这里可以添加创建测试数据的逻辑
          console.log('Creating test data:', data)
          return null
        },
        
        // 日志输出任务
        log(message) {
          console.log(message)
          return null
        }
      })
      
      // 浏览器启动配置
      on('before:browser:launch', (browser = {}, launchOptions) => {
        if (browser.name === 'chrome') {
          // Chrome浏览器配置
          launchOptions.args.push('--disable-dev-shm-usage')
          launchOptions.args.push('--no-sandbox')
          launchOptions.args.push('--disable-gpu')
          
          // 如果是无头模式
          if (launchOptions.args.includes('--headless')) {
            launchOptions.args.push('--disable-web-security')
            launchOptions.args.push('--allow-running-insecure-content')
          }
        }
        
        return launchOptions
      })
      
      // 文件预处理器
      on('file:preprocessor', (file) => {
        // 可以在这里添加文件预处理逻辑
        return file
      })
      
      return config
    },
  },
  
  component: {
    devServer: {
      framework: 'vue',
      bundler: 'vite',
    },
    specPattern: 'src/**/*.cy.{js,jsx,ts,tsx}',
  },
})