// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************

// -- 登录相关命令 --

/**
 * 登录系统
 * @param {string} username - 用户名
 * @param {string} password - 密码
 */
Cypress.Commands.add('login', (username = 'admin', password = 'admin123') => {
  cy.session([username, password], () => {
    cy.visit('/login')
    
    // 等待页面加载完成
    cy.get('[data-cy="username"]', { timeout: 10000 }).should('be.visible')
    
    // 输入用户名和密码
    cy.get('[data-cy="username"]').clear().type(username)
    cy.get('[data-cy="password"]').clear().type(password)
    
    // 如果有验证码，跳过或使用测试验证码
    cy.get('body').then($body => {
      if ($body.find('[data-cy="captcha"]').length > 0) {
        cy.get('[data-cy="captcha"]').type('1234') // 测试环境固定验证码
      }
    })
    
    // 点击登录按钮
    cy.get('[data-cy="login-btn"]').click()
    
    // 等待登录成功，跳转到首页
    cy.url({ timeout: 15000 }).should('include', '/index')
    
    // 验证登录成功的标志
    cy.get('.navbar', { timeout: 10000 }).should('be.visible')
  })
})

/**
 * 退出登录
 */
Cypress.Commands.add('logout', () => {
  cy.get('.avatar-container').click()
  cy.get('[data-cy="logout"]').click()
  cy.get('.el-message-box__btns .el-button--primary').click()
  cy.url().should('include', '/login')
})

// -- 导航相关命令 --

/**
 * 导航到指定菜单
 * @param {string} menuPath - 菜单路径，如 'system.user'
 */
Cypress.Commands.add('navigateToMenu', (menuPath) => {
  const paths = menuPath.split('.')
  
  // 点击主菜单
  cy.get(`[data-cy="${paths[0]}-menu"]`).click()
  
  // 如果有子菜单，继续点击
  if (paths.length > 1) {
    cy.get(`[data-cy="${paths.join('-')}"]`).click()
  }
  
  // 等待页面加载
  cy.waitForPageLoad()
})

/**
 * 导航到用户管理页面
 */
Cypress.Commands.add('navigateToUserManagement', () => {
  cy.navigateToMenu('system.user')
  cy.url().should('include', '/system/user')
})

/**
 * 导航到角色管理页面
 */
Cypress.Commands.add('navigateToRoleManagement', () => {
  cy.navigateToMenu('system.role')
  cy.url().should('include', '/system/role')
})

/**
 * 导航到菜单管理页面
 */
Cypress.Commands.add('navigateToMenuManagement', () => {
  cy.navigateToMenu('system.menu')
  cy.url().should('include', '/system/menu')
})

// -- 表单操作命令 --

/**
 * 填写表单
 * @param {string} formSelector - 表单选择器
 * @param {object} data - 表单数据
 */
Cypress.Commands.add('fillForm', (formSelector, data) => {
  Object.keys(data).forEach(key => {
    const value = data[key]
    const fieldSelector = `${formSelector} [name="${key}"], ${formSelector} [data-field="${key}"], ${formSelector} [placeholder*="${key}"]`
    
    cy.get('body').then($body => {
      if ($body.find(fieldSelector).length > 0) {
        cy.get(fieldSelector).first().then($el => {
          const tagName = $el.prop('tagName').toLowerCase()
          const type = $el.attr('type')
          
          if (tagName === 'input') {
            if (type === 'checkbox' || type === 'radio') {
              if (value) {
                cy.wrap($el).check()
              } else {
                cy.wrap($el).uncheck()
              }
            } else {
              cy.wrap($el).clear().type(value.toString())
            }
          } else if (tagName === 'select') {
            cy.wrap($el).select(value.toString())
          } else if (tagName === 'textarea') {
            cy.wrap($el).clear().type(value.toString())
          } else if ($el.hasClass('el-select')) {
            cy.wrap($el).click()
            cy.get('.el-select-dropdown__item').contains(value.toString()).click()
          } else if ($el.hasClass('el-input__inner')) {
            cy.wrap($el).clear().type(value.toString())
          }
        })
      }
    })
  })
})

/**
 * 提交表单
 * @param {string} formSelector - 表单选择器
 */
Cypress.Commands.add('submitForm', (formSelector) => {
  cy.get(`${formSelector} [data-cy="submit-btn"], ${formSelector} [type="submit"], ${formSelector} .el-button--primary`)
    .contains(/确定|提交|保存|添加|修改/)
    .click()
})

/**
 * 取消表单
 * @param {string} formSelector - 表单选择器
 */
Cypress.Commands.add('cancelForm', (formSelector) => {
  cy.get(`${formSelector} [data-cy="cancel-btn"], ${formSelector} .el-button--default`)
    .contains(/取消|关闭/)
    .click()
})

// -- 表格操作命令 --

/**
 * 在表格中搜索
 * @param {object} searchData - 搜索条件
 */
Cypress.Commands.add('searchInTable', (searchData) => {
  // 填写搜索条件
  Object.keys(searchData).forEach(key => {
    const value = searchData[key]
    cy.get(`[placeholder*="${key}"], [data-search="${key}"]`).clear().type(value)
  })
  
  // 点击搜索按钮
  cy.get('[data-cy="search-btn"]').click()
  
  // 等待搜索结果
  cy.wait(1000)
})

/**
 * 重置搜索条件
 */
Cypress.Commands.add('resetSearch', () => {
  cy.get('[data-cy="reset-btn"]').click()
  cy.wait(500)
})

/**
 * 选择表格行
 * @param {number} rowIndex - 行索引
 */
Cypress.Commands.add('selectTableRow', (rowIndex) => {
  cy.get('.el-table__row').eq(rowIndex).find('.el-checkbox').check()
})

/**
 * 选择所有表格行
 */
Cypress.Commands.add('selectAllTableRows', () => {
  cy.get('.el-table__header .el-checkbox').check()
})

/**
 * 获取表格行数据
 * @param {number} rowIndex - 行索引
 */
Cypress.Commands.add('getTableRowData', (rowIndex) => {
  return cy.get('.el-table__row').eq(rowIndex).find('td').then($cells => {
    const data = {}
    $cells.each((index, cell) => {
      const text = Cypress.$(cell).text().trim()
      data[`column${index}`] = text
    })
    return data
  })
})

// -- 用户管理相关命令 --

/**
 * 创建测试用户
 * @param {object} userData - 用户数据
 */
Cypress.Commands.add('createTestUser', (userData = {}) => {
  const timestamp = Date.now()
  const defaultData = {
    username: `testuser${timestamp}`,
    nickname: `测试用户${timestamp}`,
    email: `test${timestamp}@example.com`,
    phone: `138${timestamp.toString().slice(-8)}`,
    password: 'Test123456',
    confirmPassword: 'Test123456'
  }
  
  const finalData = { ...defaultData, ...userData }
  
  cy.navigateToUserManagement()
  cy.get('[data-cy="add-user-btn"]').click()
  cy.get('.el-dialog').should('be.visible')
  
  cy.fillForm('[data-cy="user-form"]', finalData)
  cy.submitForm('[data-cy="user-form"]')
  
  cy.get('.el-message--success').should('be.visible')
  cy.get('.el-dialog').should('not.exist')
  
  return cy.wrap(finalData)
})

/**
 * 删除测试用户
 * @param {string} username - 用户名
 */
Cypress.Commands.add('deleteTestUser', (username) => {
  cy.navigateToUserManagement()
  
  // 搜索用户
  cy.searchInTable({ username })
  
  // 删除用户
  cy.get('.el-table__row').contains(username).parent().within(() => {
    cy.get('[data-cy="delete-btn"], .el-button--danger').click()
  })
  
  // 确认删除
  cy.get('.el-message-box__btns .el-button--primary').click()
  cy.get('.el-message--success').should('be.visible')
})

/**
 * 编辑用户
 * @param {string} username - 用户名
 * @param {object} newData - 新的用户数据
 */
Cypress.Commands.add('editUser', (username, newData) => {
  cy.navigateToUserManagement()
  
  // 搜索用户
  cy.searchInTable({ username })
  
  // 编辑用户
  cy.get('.el-table__row').contains(username).parent().within(() => {
    cy.get('[data-cy="edit-btn"], .el-button--primary').first().click()
  })
  
  cy.get('.el-dialog').should('be.visible')
  cy.fillForm('[data-cy="user-form"]', newData)
  cy.submitForm('[data-cy="user-form"]')
  
  cy.get('.el-message--success').should('be.visible')
  cy.get('.el-dialog').should('not.exist')
})

// -- 角色管理相关命令 --

/**
 * 创建测试角色
 * @param {object} roleData - 角色数据
 */
Cypress.Commands.add('createTestRole', (roleData = {}) => {
  const timestamp = Date.now()
  const defaultData = {
    roleName: `测试角色${timestamp}`,
    roleKey: `test_role_${timestamp}`,
    roleSort: '10',
    remark: '这是一个测试角色'
  }
  
  const finalData = { ...defaultData, ...roleData }
  
  cy.navigateToRoleManagement()
  cy.get('[data-cy="add-role-btn"]').click()
  cy.get('.el-dialog').should('be.visible')
  
  cy.fillForm('[data-cy="role-form"]', finalData)
  
  // 选择权限
  cy.get('[data-cy="permission-tree"] .el-tree-node').first().click()
  
  cy.submitForm('[data-cy="role-form"]')
  
  cy.get('.el-message--success').should('be.visible')
  cy.get('.el-dialog').should('not.exist')
  
  return cy.wrap(finalData)
})

/**
 * 删除测试角色
 * @param {string} roleName - 角色名称
 */
Cypress.Commands.add('deleteTestRole', (roleName) => {
  cy.navigateToRoleManagement()
  
  cy.searchInTable({ roleName })
  
  cy.get('.el-table__row').contains(roleName).parent().within(() => {
    cy.get('[data-cy="delete-btn"], .el-button--danger').click()
  })
  
  cy.get('.el-message-box__btns .el-button--primary').click()
  cy.get('.el-message--success').should('be.visible')
})

// -- 菜单管理相关命令 --

/**
 * 创建测试菜单
 * @param {object} menuData - 菜单数据
 */
Cypress.Commands.add('createTestMenu', (menuData = {}) => {
  const timestamp = Date.now()
  const defaultData = {
    menuName: `测试菜单${timestamp}`,
    orderNum: '10',
    path: `/test${timestamp}`,
    component: 'test/index',
    menuType: 'C'
  }
  
  const finalData = { ...defaultData, ...menuData }
  
  cy.navigateToMenuManagement()
  cy.get('[data-cy="add-menu-btn"]').click()
  cy.get('.el-dialog').should('be.visible')
  
  cy.fillForm('[data-cy="menu-form"]', finalData)
  cy.submitForm('[data-cy="menu-form"]')
  
  cy.get('.el-message--success').should('be.visible')
  cy.get('.el-dialog').should('not.exist')
  
  return cy.wrap(finalData)
})

// -- 文件上传命令 --

/**
 * 上传文件
 * @param {string} selector - 文件输入选择器
 * @param {string} fileName - 文件名
 * @param {string} fileType - 文件类型
 */
Cypress.Commands.add('uploadFile', (selector, fileName, fileType = 'image/png') => {
  cy.fixture(fileName, 'base64').then(fileContent => {
    const blob = Cypress.Blob.base64StringToBlob(fileContent, fileType)
    const file = new File([blob], fileName, { type: fileType })
    
    cy.get(selector).then(input => {
      const dataTransfer = new DataTransfer()
      dataTransfer.items.add(file)
      input[0].files = dataTransfer.files
      
      cy.wrap(input).trigger('change', { force: true })
    })
  })
})

// -- 等待和重试命令 --

/**
 * 等待元素出现并可见
 * @param {string} selector - 元素选择器
 * @param {number} timeout - 超时时间
 */
Cypress.Commands.add('waitForVisible', (selector, timeout = 10000) => {
  cy.get(selector, { timeout }).should('be.visible')
})

/**
 * 重试操作直到成功
 * @param {function} operation - 要执行的操作
 * @param {number} maxRetries - 最大重试次数
 */
Cypress.Commands.add('retryOperation', (operation, maxRetries = 3) => {
  let attempts = 0
  
  const attempt = () => {
    attempts++
    try {
      operation()
    } catch (error) {
      if (attempts < maxRetries) {
        cy.wait(1000).then(attempt)
      } else {
        throw error
      }
    }
  }
  
  attempt()
})

// -- 数据清理命令 --

/**
 * 清理测试数据
 */
Cypress.Commands.add('cleanupTestData', () => {
  // 清理测试用户
  cy.task('cleanupTestData')
  
  // 清理浏览器数据
  cy.clearLocalStorage()
  cy.clearCookies()
  cy.clearAllSessionStorage()
})

// -- 断言增强命令 --

/**
 * 验证表格包含数据
 * @param {string} tableSelector - 表格选择器
 * @param {object} expectedData - 期望的数据
 */
Cypress.Commands.add('shouldContainTableData', (tableSelector, expectedData) => {
  Object.keys(expectedData).forEach(key => {
    cy.get(tableSelector).should('contain', expectedData[key])
  })
})

/**
 * 验证表单验证错误
 * @param {string} formSelector - 表单选择器
 * @param {string} expectedError - 期望的错误信息
 */
Cypress.Commands.add('shouldShowFormError', (formSelector, expectedError) => {
  cy.get(`${formSelector} .el-form-item__error`).should('contain', expectedError)
})

/**
 * 验证成功消息
 * @param {string} message - 期望的成功消息
 */
Cypress.Commands.add('shouldShowSuccessMessage', (message) => {
  cy.get('.el-message--success').should('be.visible')
  if (message) {
    cy.get('.el-message--success').should('contain', message)
  }
})

/**
 * 验证错误消息
 * @param {string} message - 期望的错误消息
 */
Cypress.Commands.add('shouldShowErrorMessage', (message) => {
  cy.get('.el-message--error').should('be.visible')
  if (message) {
    cy.get('.el-message--error').should('contain', message)
  }
})