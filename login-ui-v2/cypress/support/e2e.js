// ***********************************************************
// This example support/e2e.js is processed and
// loaded automatically before your test files.
//
// This is a great place to put global configuration and
// behavior that modifies Cypress.
//
// You can change the location of this file or turn off
// automatically serving support files with the
// 'supportFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/configuration
// ***********************************************************

// Import commands.js using ES2015 syntax:
import './commands'

// Alternatively you can use CommonJS syntax:
// require('./commands')

// 全局配置
Cypress.on('uncaught:exception', (err, runnable) => {
  // 忽略某些不影响测试的错误
  if (err.message.includes('ResizeObserver loop limit exceeded')) {
    return false
  }
  if (err.message.includes('Non-Error promise rejection captured')) {
    return false
  }
  // 返回false阻止Cypress因为未捕获的异常而失败
  return false
})

// 全局钩子
beforeEach(() => {
  // 在每个测试前执行
  // 设置视口
  cy.viewport(1280, 720)
  
  // 清除本地存储
  cy.clearLocalStorage()
  
  // 清除会话存储
  cy.clearCookies()
  
  // 设置请求拦截
  cy.intercept('GET', '/system/user/getInfo', { fixture: 'user-info.json' }).as('getUserInfo')
  cy.intercept('GET', '/getRouters', { fixture: 'routers.json' }).as('getRouters')
})

afterEach(() => {
  // 在每个测试后执行
  // 可以在这里添加清理逻辑
})

// 全局命令超时设置
Cypress.config('defaultCommandTimeout', 10000)
Cypress.config('requestTimeout', 10000)
Cypress.config('responseTimeout', 10000)

// 自定义断言
Chai.use((chai, utils) => {
  chai.Assertion.addMethod('beVisibleInViewport', function () {
    const obj = this._obj
    
    new chai.Assertion(obj).to.be.visible
    
    cy.wrap(obj).then(($el) => {
      const rect = $el[0].getBoundingClientRect()
      const windowHeight = Cypress.config('viewportHeight')
      const windowWidth = Cypress.config('viewportWidth')
      
      expect(rect.top).to.be.at.least(0)
      expect(rect.left).to.be.at.least(0)
      expect(rect.bottom).to.be.at.most(windowHeight)
      expect(rect.right).to.be.at.most(windowWidth)
    })
  })
})

// 添加自定义命令用于等待元素
Cypress.Commands.add('waitForElement', (selector, timeout = 10000) => {
  cy.get(selector, { timeout }).should('exist')
})

// 添加自定义命令用于等待API响应
Cypress.Commands.add('waitForApi', (alias, timeout = 10000) => {
  cy.wait(alias, { timeout })
})

// 添加自定义命令用于检查元素是否在视口内
Cypress.Commands.add('shouldBeInViewport', { prevSubject: true }, (subject) => {
  cy.wrap(subject).should('beVisibleInViewport')
})

// 添加自定义命令用于滚动到元素
Cypress.Commands.add('scrollToElement', (selector) => {
  cy.get(selector).scrollIntoView()
})

// 添加自定义命令用于等待加载完成
Cypress.Commands.add('waitForPageLoad', () => {
  cy.window().its('document.readyState').should('equal', 'complete')
})

// 添加自定义命令用于检查响应式布局
Cypress.Commands.add('checkResponsive', (breakpoints = [1920, 1366, 768, 375]) => {
  breakpoints.forEach(width => {
    cy.viewport(width, 720)
    cy.wait(500) // 等待布局调整
    
    // 检查主要元素是否可见
    cy.get('body').should('be.visible')
    cy.get('.app-main').should('be.visible')
  })
})

// 添加自定义命令用于模拟网络延迟
Cypress.Commands.add('simulateSlowNetwork', (delay = 1000) => {
  cy.intercept('**', (req) => {
    req.reply((res) => {
      return new Promise(resolve => {
        setTimeout(() => resolve(res), delay)
      })
    })
  })
})

// 添加自定义命令用于检查无障碍性
Cypress.Commands.add('checkA11y', () => {
  // 检查基本的无障碍性要求
  cy.get('img').each(($img) => {
    cy.wrap($img).should('have.attr', 'alt')
  })
  
  cy.get('input').each(($input) => {
    const type = $input.attr('type')
    if (type !== 'hidden' && type !== 'submit' && type !== 'button') {
      cy.wrap($input).should('satisfy', ($el) => {
        return $el.attr('placeholder') || $el.attr('aria-label') || $el.prev('label').length > 0
      })
    }
  })
})

// 添加自定义命令用于性能监控
Cypress.Commands.add('measurePerformance', (name) => {
  cy.window().then((win) => {
    win.performance.mark(`${name}-start`)
  })
  
  return {
    end: () => {
      cy.window().then((win) => {
        win.performance.mark(`${name}-end`)
        win.performance.measure(name, `${name}-start`, `${name}-end`)
        
        const measure = win.performance.getEntriesByName(name)[0]
        cy.log(`Performance: ${name} took ${measure.duration}ms`)
        
        // 断言性能在合理范围内
        expect(measure.duration).to.be.lessThan(5000)
      })
    }
  }
})

// 添加自定义命令用于数据驱动测试
Cypress.Commands.add('testWithData', (testData, testFunction) => {
  testData.forEach((data, index) => {
    cy.log(`Testing with data set ${index + 1}:`, data)
    testFunction(data)
  })
})

// 添加自定义命令用于表单验证测试
Cypress.Commands.add('testFormValidation', (formSelector, validationRules) => {
  Object.keys(validationRules).forEach(fieldName => {
    const rule = validationRules[fieldName]
    const fieldSelector = `${formSelector} [name="${fieldName}"], ${formSelector} [data-field="${fieldName}"]`
    
    if (rule.required) {
      // 测试必填验证
      cy.get(fieldSelector).clear()
      cy.get(`${formSelector} [type="submit"]`).click()
      cy.get('.el-form-item__error').should('be.visible')
    }
    
    if (rule.minLength) {
      // 测试最小长度验证
      cy.get(fieldSelector).clear().type('a'.repeat(rule.minLength - 1))
      cy.get(`${formSelector} [type="submit"]`).click()
      cy.get('.el-form-item__error').should('be.visible')
    }
    
    if (rule.pattern) {
      // 测试格式验证
      cy.get(fieldSelector).clear().type('invalid-format')
      cy.get(`${formSelector} [type="submit"]`).click()
      cy.get('.el-form-item__error').should('be.visible')
    }
  })
})

// 添加自定义命令用于批量操作测试
Cypress.Commands.add('testBatchOperations', (tableSelector, operations) => {
  operations.forEach(operation => {
    // 选择多行
    cy.get(`${tableSelector} .el-checkbox`).first().check()
    cy.get(`${tableSelector} .el-checkbox`).eq(1).check()
    
    // 执行批量操作
    cy.get(`[data-cy="${operation.buttonId}"]`).click()
    
    if (operation.needConfirm) {
      cy.get('.el-message-box__btns .el-button--primary').click()
    }
    
    // 验证操作结果
    if (operation.successMessage) {
      cy.get('.el-message--success').should('contain', operation.successMessage)
    }
  })
})