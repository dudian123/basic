/**
 * 系统管理页面性能测试脚本
 * 测试页面加载速度、响应时间和资源使用情况
 */

describe('系统管理页面性能测试', () => {
  let authToken = ''
  
  before(() => {
    // 获取认证token
    cy.request({
      method: 'POST',
      url: '/login',
      body: {
        username: 'admin',
        password: 'admin123',
        code: '1234',
        uuid: 'test-uuid'
      }
    }).then(response => {
      authToken = response.body.token
    })
  })
  
  beforeEach(() => {
    // 设置性能监控
    cy.window().then(win => {
      win.performance.mark('test-start')
    })
  })
  
  afterEach(() => {
    // 记录性能指标
    cy.window().then(win => {
      win.performance.mark('test-end')
      win.performance.measure('test-duration', 'test-start', 'test-end')
      
      const measures = win.performance.getEntriesByType('measure')
      const testMeasure = measures.find(m => m.name === 'test-duration')
      if (testMeasure) {
        cy.log(`测试执行时间: ${testMeasure.duration.toFixed(2)}ms`)
      }
    })
  })

  describe('页面加载性能测试', () => {
    it('用户管理页面加载性能', () => {
      const startTime = Date.now()
      
      cy.visit('/system/user')
      cy.get('[data-testid="user-table"]', { timeout: 10000 }).should('be.visible')
      
      const loadTime = Date.now() - startTime
      cy.log(`用户管理页面加载时间: ${loadTime}ms`)
      expect(loadTime).to.be.lessThan(5000) // 页面应在5秒内加载完成
      
      // 检查页面性能指标
      cy.window().then(win => {
        const navigation = win.performance.getEntriesByType('navigation')[0]
        if (navigation) {
          cy.log(`DOM加载时间: ${navigation.domContentLoadedEventEnd - navigation.domContentLoadedEventStart}ms`)
          cy.log(`页面完全加载时间: ${navigation.loadEventEnd - navigation.loadEventStart}ms`)
          
          // DOM加载应在2秒内完成
          expect(navigation.domContentLoadedEventEnd - navigation.navigationStart).to.be.lessThan(2000)
        }
      })
    })

    it('角色管理页面加载性能', () => {
      const startTime = Date.now()
      
      cy.visit('/system/role')
      cy.get('[data-testid="role-table"]', { timeout: 10000 }).should('be.visible')
      
      const loadTime = Date.now() - startTime
      cy.log(`角色管理页面加载时间: ${loadTime}ms`)
      expect(loadTime).to.be.lessThan(5000)
    })

    it('菜单管理页面加载性能', () => {
      const startTime = Date.now()
      
      cy.visit('/system/menu')
      cy.get('[data-testid="menu-tree"]', { timeout: 10000 }).should('be.visible')
      
      const loadTime = Date.now() - startTime
      cy.log(`菜单管理页面加载时间: ${loadTime}ms`)
      expect(loadTime).to.be.lessThan(5000)
    })

    it('部门管理页面加载性能', () => {
      const startTime = Date.now()
      
      cy.visit('/system/dept')
      cy.get('[data-testid="dept-tree"]', { timeout: 10000 }).should('be.visible')
      
      const loadTime = Date.now() - startTime
      cy.log(`部门管理页面加载时间: ${loadTime}ms`)
      expect(loadTime).to.be.lessThan(5000)
    })

    it('字典管理页面加载性能', () => {
      const startTime = Date.now()
      
      cy.visit('/system/dict')
      cy.get('[data-testid="dict-table"]', { timeout: 10000 }).should('be.visible')
      
      const loadTime = Date.now() - startTime
      cy.log(`字典管理页面加载时间: ${loadTime}ms`)
      expect(loadTime).to.be.lessThan(5000)
    })
  })

  describe('API响应性能测试', () => {
    it('用户列表API响应时间', () => {
      const startTime = Date.now()
      
      cy.request({
        method: 'GET',
        url: '/system/user/list',
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      }).then(response => {
        const responseTime = Date.now() - startTime
        cy.log(`用户列表API响应时间: ${responseTime}ms`)
        
        expect(response.status).to.equal(200)
        expect(responseTime).to.be.lessThan(2000) // API应在2秒内响应
      })
    })

    it('角色列表API响应时间', () => {
      const startTime = Date.now()
      
      cy.request({
        method: 'GET',
        url: '/system/role/list',
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      }).then(response => {
        const responseTime = Date.now() - startTime
        cy.log(`角色列表API响应时间: ${responseTime}ms`)
        
        expect(response.status).to.equal(200)
        expect(responseTime).to.be.lessThan(2000)
      })
    })

    it('菜单列表API响应时间', () => {
      const startTime = Date.now()
      
      cy.request({
        method: 'GET',
        url: '/system/menu/list',
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      }).then(response => {
        const responseTime = Date.now() - startTime
        cy.log(`菜单列表API响应时间: ${responseTime}ms`)
        
        expect(response.status).to.equal(200)
        expect(responseTime).to.be.lessThan(2000)
      })
    })

    it('部门列表API响应时间', () => {
      const startTime = Date.now()
      
      cy.request({
        method: 'GET',
        url: '/system/dept/list',
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      }).then(response => {
        const responseTime = Date.now() - startTime
        cy.log(`部门列表API响应时间: ${responseTime}ms`)
        
        expect(response.status).to.equal(200)
        expect(responseTime).to.be.lessThan(2000)
      })
    })

    it('字典类型列表API响应时间', () => {
      const startTime = Date.now()
      
      cy.request({
        method: 'GET',
        url: '/system/dict/type/list',
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      }).then(response => {
        const responseTime = Date.now() - startTime
        cy.log(`字典类型列表API响应时间: ${responseTime}ms`)
        
        expect(response.status).to.equal(200)
        expect(responseTime).to.be.lessThan(2000)
      })
    })
  })

  describe('大数据量性能测试', () => {
    it('大量用户数据分页加载性能', () => {
      const pageSize = 100
      const startTime = Date.now()
      
      cy.request({
        method: 'GET',
        url: '/system/user/list',
        qs: {
          pageNum: 1,
          pageSize: pageSize
        },
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      }).then(response => {
        const responseTime = Date.now() - startTime
        cy.log(`加载${pageSize}条用户数据响应时间: ${responseTime}ms`)
        
        expect(response.status).to.equal(200)
        expect(responseTime).to.be.lessThan(3000) // 大数据量应在3秒内响应
        
        // 验证数据量
        expect(response.body.rows.length).to.be.at.most(pageSize)
      })
    })

    it('搜索功能性能测试', () => {
      const startTime = Date.now()
      
      cy.request({
        method: 'GET',
        url: '/system/user/list',
        qs: {
          userName: 'admin',
          nickName: '管理员',
          status: '0'
        },
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      }).then(response => {
        const responseTime = Date.now() - startTime
        cy.log(`用户搜索响应时间: ${responseTime}ms`)
        
        expect(response.status).to.equal(200)
        expect(responseTime).to.be.lessThan(2000) // 搜索应在2秒内完成
      })
    })
  })

  describe('并发性能测试', () => {
    it('并发API请求性能', () => {
      const concurrentRequests = 10
      const requests = []
      const startTime = Date.now()
      
      // 创建并发请求
      for (let i = 0; i < concurrentRequests; i++) {
        const request = cy.request({
          method: 'GET',
          url: '/system/user/list',
          qs: { pageNum: i + 1, pageSize: 10 },
          headers: {
            'Authorization': `Bearer ${authToken}`
          }
        })
        requests.push(request)
      }
      
      // 等待所有请求完成
      Promise.all(requests).then(responses => {
        const totalTime = Date.now() - startTime
        cy.log(`${concurrentRequests}个并发请求总时间: ${totalTime}ms`)
        cy.log(`平均响应时间: ${(totalTime / concurrentRequests).toFixed(2)}ms`)
        
        // 验证所有请求都成功
        responses.forEach(response => {
          expect(response.status).to.equal(200)
        })
        
        // 并发请求应在合理时间内完成
        expect(totalTime).to.be.lessThan(10000)
      })
    })

    it('并发页面访问性能', () => {
      const pages = [
        '/system/user',
        '/system/role', 
        '/system/menu',
        '/system/dept',
        '/system/dict'
      ]
      
      const startTime = Date.now()
      
      // 依次快速访问各个页面
      pages.forEach((page, index) => {
        cy.visit(page)
        cy.get('body').should('be.visible')
        
        if (index === pages.length - 1) {
          const totalTime = Date.now() - startTime
          cy.log(`访问${pages.length}个页面总时间: ${totalTime}ms`)
          expect(totalTime).to.be.lessThan(15000) // 应在15秒内完成
        }
      })
    })
  })

  describe('内存使用性能测试', () => {
    it('页面内存使用监控', () => {
      cy.visit('/system/user')
      
      cy.window().then(win => {
        // 检查内存使用情况（如果浏览器支持）
        if (win.performance && win.performance.memory) {
          const memory = win.performance.memory
          cy.log(`已使用内存: ${(memory.usedJSHeapSize / 1024 / 1024).toFixed(2)}MB`)
          cy.log(`总内存限制: ${(memory.totalJSHeapSize / 1024 / 1024).toFixed(2)}MB`)
          cy.log(`内存限制: ${(memory.jsHeapSizeLimit / 1024 / 1024).toFixed(2)}MB`)
          
          // 内存使用不应超过100MB
          expect(memory.usedJSHeapSize).to.be.lessThan(100 * 1024 * 1024)
        }
      })
    })

    it('长时间操作内存泄漏检测', () => {
      cy.visit('/system/user')
      
      let initialMemory = 0
      
      cy.window().then(win => {
        if (win.performance && win.performance.memory) {
          initialMemory = win.performance.memory.usedJSHeapSize
          cy.log(`初始内存使用: ${(initialMemory / 1024 / 1024).toFixed(2)}MB`)
        }
      })
      
      // 执行多次操作
      for (let i = 0; i < 10; i++) {
        cy.get('[data-testid="search-btn"]').click()
        cy.wait(500)
        cy.get('[data-testid="reset-btn"]').click()
        cy.wait(500)
      }
      
      cy.window().then(win => {
        if (win.performance && win.performance.memory && initialMemory > 0) {
          const finalMemory = win.performance.memory.usedJSHeapSize
          const memoryIncrease = finalMemory - initialMemory
          
          cy.log(`最终内存使用: ${(finalMemory / 1024 / 1024).toFixed(2)}MB`)
          cy.log(`内存增长: ${(memoryIncrease / 1024 / 1024).toFixed(2)}MB`)
          
          // 内存增长不应超过10MB
          expect(memoryIncrease).to.be.lessThan(10 * 1024 * 1024)
        }
      })
    })
  })

  describe('网络性能测试', () => {
    it('资源加载性能', () => {
      cy.visit('/system/user')
      
      cy.window().then(win => {
        const resources = win.performance.getEntriesByType('resource')
        
        let totalSize = 0
        let slowResources = []
        
        resources.forEach(resource => {
          const loadTime = resource.responseEnd - resource.requestStart
          
          if (resource.transferSize) {
            totalSize += resource.transferSize
          }
          
          if (loadTime > 1000) { // 超过1秒的资源
            slowResources.push({
              name: resource.name,
              loadTime: loadTime.toFixed(2)
            })
          }
        })
        
        cy.log(`总资源大小: ${(totalSize / 1024).toFixed(2)}KB`)
        cy.log(`慢加载资源数量: ${slowResources.length}`)
        
        if (slowResources.length > 0) {
          cy.log('慢加载资源:', slowResources)
        }
        
        // 总资源大小不应超过5MB
        expect(totalSize).to.be.lessThan(5 * 1024 * 1024)
        // 慢加载资源不应超过3个
        expect(slowResources.length).to.be.lessThan(3)
      })
    })

    it('API响应大小检测', () => {
      cy.intercept('GET', '/system/user/list').as('getUserList')
      
      cy.visit('/system/user')
      cy.wait('@getUserList')
      
      cy.get('@getUserList').then(interception => {
        const responseSize = JSON.stringify(interception.response.body).length
        cy.log(`用户列表API响应大小: ${(responseSize / 1024).toFixed(2)}KB`)
        
        // API响应大小不应超过1MB
        expect(responseSize).to.be.lessThan(1024 * 1024)
      })
    })
  })

  describe('用户体验性能测试', () => {
    it('页面交互响应时间', () => {
      cy.visit('/system/user')
      
      // 测试搜索按钮响应时间
      const searchStartTime = Date.now()
      cy.get('[data-testid="search-btn"]').click()
      cy.get('[data-testid="user-table"]').should('be.visible')
      const searchTime = Date.now() - searchStartTime
      
      cy.log(`搜索操作响应时间: ${searchTime}ms`)
      expect(searchTime).to.be.lessThan(1000) // 交互应在1秒内响应
      
      // 测试重置按钮响应时间
      const resetStartTime = Date.now()
      cy.get('[data-testid="reset-btn"]').click()
      cy.get('[data-testid="user-table"]').should('be.visible')
      const resetTime = Date.now() - resetStartTime
      
      cy.log(`重置操作响应时间: ${resetTime}ms`)
      expect(resetTime).to.be.lessThan(1000)
    })

    it('表格滚动性能', () => {
      cy.visit('/system/user')
      cy.get('[data-testid="user-table"]').should('be.visible')
      
      const startTime = Date.now()
      
      // 模拟表格滚动
      cy.get('[data-testid="user-table"] .el-table__body-wrapper')
        .scrollTo('bottom', { duration: 1000 })
        .scrollTo('top', { duration: 1000 })
      
      const scrollTime = Date.now() - startTime
      cy.log(`表格滚动操作时间: ${scrollTime}ms`)
      
      // 滚动操作应流畅，不超过3秒
      expect(scrollTime).to.be.lessThan(3000)
    })

    it('弹窗打开关闭性能', () => {
      cy.visit('/system/user')
      
      // 测试新增弹窗
      const openStartTime = Date.now()
      cy.get('[data-testid="add-btn"]').click()
      cy.get('[data-testid="user-dialog"]').should('be.visible')
      const openTime = Date.now() - openStartTime
      
      cy.log(`弹窗打开时间: ${openTime}ms`)
      expect(openTime).to.be.lessThan(500) // 弹窗应在0.5秒内打开
      
      // 测试弹窗关闭
      const closeStartTime = Date.now()
      cy.get('[data-testid="cancel-btn"]').click()
      cy.get('[data-testid="user-dialog"]').should('not.exist')
      const closeTime = Date.now() - closeStartTime
      
      cy.log(`弹窗关闭时间: ${closeTime}ms`)
      expect(closeTime).to.be.lessThan(500)
    })
  })

  describe('移动端性能测试', () => {
    it('移动端页面加载性能', () => {
      cy.viewport('iphone-6')
      
      const startTime = Date.now()
      cy.visit('/system/user')
      cy.get('[data-testid="user-table"]').should('be.visible')
      
      const loadTime = Date.now() - startTime
      cy.log(`移动端页面加载时间: ${loadTime}ms`)
      
      // 移动端加载时间可能稍长，但不应超过8秒
      expect(loadTime).to.be.lessThan(8000)
    })

    it('移动端触摸操作性能', () => {
      cy.viewport('iphone-6')
      cy.visit('/system/user')
      
      const startTime = Date.now()
      
      // 模拟触摸操作
      cy.get('[data-testid="search-btn"]').trigger('touchstart').trigger('touchend')
      cy.get('[data-testid="user-table"]').should('be.visible')
      
      const touchTime = Date.now() - startTime
      cy.log(`移动端触摸操作响应时间: ${touchTime}ms`)
      
      expect(touchTime).to.be.lessThan(1500) // 触摸操作应在1.5秒内响应
    })
  })

  describe('性能基准测试', () => {
    it('建立性能基准', () => {
      const performanceMetrics = {
        pageLoadTime: [],
        apiResponseTime: [],
        interactionTime: []
      }
      
      // 执行多次测试建立基准
      for (let i = 0; i < 5; i++) {
        // 页面加载测试
        const pageStartTime = Date.now()
        cy.visit('/system/user')
        cy.get('[data-testid="user-table"]').should('be.visible')
        performanceMetrics.pageLoadTime.push(Date.now() - pageStartTime)
        
        // API响应测试
        const apiStartTime = Date.now()
        cy.request({
          method: 'GET',
          url: '/system/user/list',
          headers: {
            'Authorization': `Bearer ${authToken}`
          }
        }).then(() => {
          performanceMetrics.apiResponseTime.push(Date.now() - apiStartTime)
        })
        
        // 交互测试
        const interactionStartTime = Date.now()
        cy.get('[data-testid="search-btn"]').click()
        cy.get('[data-testid="user-table"]').should('be.visible')
        performanceMetrics.interactionTime.push(Date.now() - interactionStartTime)
      }
      
      // 计算平均值
      const avgPageLoad = performanceMetrics.pageLoadTime.reduce((a, b) => a + b, 0) / performanceMetrics.pageLoadTime.length
      const avgApiResponse = performanceMetrics.apiResponseTime.reduce((a, b) => a + b, 0) / performanceMetrics.apiResponseTime.length
      const avgInteraction = performanceMetrics.interactionTime.reduce((a, b) => a + b, 0) / performanceMetrics.interactionTime.length
      
      cy.log(`平均页面加载时间: ${avgPageLoad.toFixed(2)}ms`)
      cy.log(`平均API响应时间: ${avgApiResponse.toFixed(2)}ms`)
      cy.log(`平均交互响应时间: ${avgInteraction.toFixed(2)}ms`)
      
      // 保存基准数据
      cy.writeFile('cypress/fixtures/performance-baseline.json', {
        pageLoadTime: avgPageLoad,
        apiResponseTime: avgApiResponse,
        interactionTime: avgInteraction,
        timestamp: new Date().toISOString()
      })
    })
  })
})