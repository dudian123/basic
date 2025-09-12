const { defineConfig } = require('cypress')

module.exports = defineConfig({
  e2e: {
    // 基础配置
    baseUrl: 'http://localhost:3000',
    
    // 视窗配置
    viewportWidth: 1280,
    viewportHeight: 720,
    
    // 超时配置
    defaultCommandTimeout: 10000,
    requestTimeout: 10000,
    responseTimeout: 10000,
    pageLoadTimeout: 30000,
    
    // 重试配置
    retries: {
      runMode: 2,
      openMode: 0
    },
    
    // 视频和截图配置
    video: false,
    videosFolder: 'test-reports/videos',
    screenshotOnRunFailure: true,
    screenshotsFolder: 'test-reports/screenshots',
    
    // 测试文件配置
    specPattern: '*.js',
    excludeSpecPattern: [
      'run-tests.js',
      'cypress.config.js',
      'README.md'
    ],
    
    // 支持文件
    supportFile: false,
    
    // 实验性功能
    experimentalStudio: true,
    experimentalWebKitSupport: true,
    
    // 环境变量
    env: {
      // API配置
      apiUrl: 'http://localhost:8080',
      
      // 测试用户配置
      adminUsername: 'admin',
      adminPassword: 'admin123',
      
      // 测试数据前缀
      testPrefix: 'test_',
      
      // 性能测试阈值
      performanceThresholds: {
        pageLoadTime: 5000,
        apiResponseTime: 2000,
        interactionTime: 1000,
        memoryUsage: 100 * 1024 * 1024 // 100MB
      },
      
      // 测试配置
      testConfig: {
        enablePerformanceTests: true,
        enableCompatibilityTests: true,
        enableVisualTests: false,
        maxRetries: 3,
        testDataCleanup: true
      }
    },
    
    setupNodeEvents(on, config) {
      // 任务配置
      on('task', {
        // 日志任务
        log(message) {
          console.log(message)
          return null
        },
        
        // 生成测试数据
        generateTestData(type) {
          const timestamp = Date.now()
          const testData = {
            user: {
              userName: `test_user_${timestamp}`,
              nickName: `测试用户${timestamp}`,
              email: `test${timestamp}@example.com`,
              phonenumber: `138${String(timestamp).slice(-8)}`,
              password: 'Test123456',
              status: '0'
            },
            role: {
              roleName: `test_role_${timestamp}`,
              roleKey: `test_role_${timestamp}`,
              roleSort: 99,
              status: '0',
              remark: `测试角色${timestamp}`
            },
            dept: {
              deptName: `测试部门${timestamp}`,
              orderNum: 99,
              status: '0'
            },
            menu: {
              menuName: `测试菜单${timestamp}`,
              orderNum: 99,
              menuType: 'M',
              visible: '0',
              status: '0'
            },
            dict: {
              dictName: `测试字典${timestamp}`,
              dictType: `test_dict_${timestamp}`,
              status: '0',
              remark: `测试字典类型${timestamp}`
            }
          }
          return testData[type] || null
        },
        
        // 清理测试数据
        cleanupTestData() {
          console.log('清理测试数据...')
          // 这里可以添加清理逻辑
          return null
        },
        
        // 数据库查询任务
        queryDatabase(query) {
          // 这里可以添加数据库查询逻辑
          console.log('执行数据库查询:', query)
          return null
        },
        
        // 文件操作任务
        readFile(filename) {
          const fs = require('fs')
          const path = require('path')
          try {
            const filePath = path.join(__dirname, filename)
            return fs.readFileSync(filePath, 'utf8')
          } catch (error) {
            return null
          }
        },
        
        writeFile({ filename, content }) {
          const fs = require('fs')
          const path = require('path')
          try {
            const filePath = path.join(__dirname, filename)
            fs.writeFileSync(filePath, content)
            return true
          } catch (error) {
            return false
          }
        },
        
        // 性能监控任务
        recordPerformance(data) {
          const fs = require('fs')
          const path = require('path')
          const perfFile = path.join(__dirname, 'test-reports/performance.json')
          
          let perfData = []
          if (fs.existsSync(perfFile)) {
            try {
              perfData = JSON.parse(fs.readFileSync(perfFile, 'utf8'))
            } catch (error) {
              perfData = []
            }
          }
          
          perfData.push({
            ...data,
            timestamp: new Date().toISOString()
          })
          
          fs.writeFileSync(perfFile, JSON.stringify(perfData, null, 2))
          return null
        },
        
        // 截图比较任务
        compareScreenshots({ baseline, current }) {
          // 这里可以添加截图比较逻辑
          console.log('比较截图:', baseline, 'vs', current)
          return { match: true, difference: 0 }
        }
      })
      
      // 浏览器启动配置
      on('before:browser:launch', (browser, launchOptions) => {
        if (browser.name === 'chrome') {
          // Chrome性能优化
          launchOptions.args.push('--disable-dev-shm-usage')
          launchOptions.args.push('--no-sandbox')
          launchOptions.args.push('--disable-gpu')
          launchOptions.args.push('--disable-web-security')
          launchOptions.args.push('--allow-running-insecure-content')
          
          // 内存限制
          launchOptions.args.push('--max_old_space_size=4096')
          
          // 禁用扩展
          launchOptions.args.push('--disable-extensions')
          
          // 性能监控
          if (config.env.testConfig.enablePerformanceTests) {
            launchOptions.args.push('--enable-precise-memory-info')
          }
        }
        
        if (browser.name === 'firefox') {
          // Firefox配置
          launchOptions.preferences['dom.ipc.processCount'] = 1
          launchOptions.preferences['browser.tabs.remote.autostart'] = false
        }
        
        return launchOptions
      })
      
      // 测试文件配置
      on('file:preprocessor', (file) => {
        // 这里可以添加文件预处理逻辑
        return file
      })
      
      // 环境变量配置
      config.env.testStartTime = Date.now()
      
      return config
    }
  },
  
  component: {
    devServer: {
      framework: 'vue',
      bundler: 'vite'
    },
    specPattern: 'src/**/*.cy.{js,jsx,ts,tsx}'
  }
})