/**
 * 系统管理页面自动化测试脚本
 * 使用 Cypress 进行端到端测试
 */

describe('系统管理页面测试套件', () => {
  // 测试前的准备工作
  beforeEach(() => {
    // 登录系统
    cy.visit('/login')
    cy.get('[data-cy="username"]').type('admin')
    cy.get('[data-cy="password"]').type('admin123')
    cy.get('[data-cy="login-btn"]').click()
    
    // 等待登录完成
    cy.url().should('include', '/index')
  })

  describe('用户管理页面测试', () => {
    beforeEach(() => {
      // 导航到用户管理页面
      cy.get('[data-cy="system-menu"]').click()
      cy.get('[data-cy="user-management"]').click()
      cy.url().should('include', '/system/user')
    })

    it('应该正确加载用户管理页面', () => {
      // 验证页面标题
      cy.contains('用户管理').should('be.visible')
      
      // 验证搜索表单
      cy.get('[data-cy="search-form"]').should('be.visible')
      cy.get('[placeholder="请输入用户名称"]').should('be.visible')
      cy.get('[placeholder="请输入手机号码"]').should('be.visible')
      
      // 验证操作按钮
      cy.get('[data-cy="add-user-btn"]').should('be.visible')
      cy.get('[data-cy="edit-user-btn"]').should('be.disabled')
      cy.get('[data-cy="delete-user-btn"]').should('be.disabled')
      
      // 验证用户列表
      cy.get('[data-cy="user-table"]').should('be.visible')
      cy.get('.el-table__row').should('have.length.greaterThan', 0)
    })

    it('应该能够搜索用户', () => {
      // 输入搜索条件
      cy.get('[placeholder="请输入用户名称"]').type('admin')
      cy.get('[data-cy="search-btn"]').click()
      
      // 验证搜索结果
      cy.get('.el-table__row').should('contain', 'admin')
      
      // 重置搜索
      cy.get('[data-cy="reset-btn"]').click()
      cy.get('[placeholder="请输入用户名称"]').should('have.value', '')
    })

    it('应该能够新增用户', () => {
      // 点击新增按钮
      cy.get('[data-cy="add-user-btn"]').click()
      
      // 验证对话框打开
      cy.get('.el-dialog').should('be.visible')
      cy.contains('新增用户').should('be.visible')
      
      // 填写用户信息
      const timestamp = Date.now()
      const testUser = {
        username: `testuser${timestamp}`,
        nickname: `测试用户${timestamp}`,
        email: `test${timestamp}@example.com`,
        phone: `138${timestamp.toString().slice(-8)}`,
        password: 'Test123456'
      }
      
      cy.get('[data-cy="user-form"] [placeholder="请输入用户名称"]').type(testUser.username)
      cy.get('[data-cy="user-form"] [placeholder="请输入用户昵称"]').type(testUser.nickname)
      cy.get('[data-cy="user-form"] [placeholder="请输入邮箱"]').type(testUser.email)
      cy.get('[data-cy="user-form"] [placeholder="请输入手机号码"]').type(testUser.phone)
      cy.get('[data-cy="user-form"] [placeholder="请输入密码"]').type(testUser.password)
      cy.get('[data-cy="user-form"] [placeholder="请确认密码"]').type(testUser.password)
      
      // 提交表单
      cy.get('[data-cy="submit-btn"]').click()
      
      // 验证成功提示
      cy.get('.el-message--success').should('be.visible')
      
      // 验证对话框关闭
      cy.get('.el-dialog').should('not.exist')
      
      // 验证用户列表包含新用户
      cy.get('.el-table__row').should('contain', testUser.username)
    })

    it('应该能够编辑用户', () => {
      // 选择第一个非admin用户
      cy.get('.el-table__row').not(':contains("admin")').first().within(() => {
        cy.get('[data-cy="edit-btn"]').click()
      })
      
      // 验证编辑对话框打开
      cy.get('.el-dialog').should('be.visible')
      cy.contains('修改用户').should('be.visible')
      
      // 修改用户昵称
      const newNickname = `修改后昵称${Date.now()}`
      cy.get('[data-cy="user-form"] [placeholder="请输入用户昵称"]')
        .clear()
        .type(newNickname)
      
      // 提交修改
      cy.get('[data-cy="submit-btn"]').click()
      
      // 验证成功提示
      cy.get('.el-message--success').should('be.visible')
      
      // 验证修改生效
      cy.get('.el-table__row').should('contain', newNickname)
    })

    it('应该能够删除用户', () => {
      // 创建测试用户用于删除
      const testUsername = `deleteuser${Date.now()}`
      
      // 先创建用户
      cy.get('[data-cy="add-user-btn"]').click()
      cy.get('[data-cy="user-form"] [placeholder="请输入用户名称"]').type(testUsername)
      cy.get('[data-cy="user-form"] [placeholder="请输入用户昵称"]').type('待删除用户')
      cy.get('[data-cy="user-form"] [placeholder="请输入邮箱"]').type(`${testUsername}@example.com`)
      cy.get('[data-cy="user-form"] [placeholder="请输入手机号码"]').type('13800138000')
      cy.get('[data-cy="user-form"] [placeholder="请输入密码"]').type('Test123456')
      cy.get('[data-cy="user-form"] [placeholder="请确认密码"]').type('Test123456')
      cy.get('[data-cy="submit-btn"]').click()
      cy.get('.el-message--success').should('be.visible')
      
      // 删除刚创建的用户
      cy.get('.el-table__row').contains(testUsername).parent().within(() => {
        cy.get('[data-cy="delete-btn"]').click()
      })
      
      // 确认删除
      cy.get('.el-message-box').should('be.visible')
      cy.get('.el-message-box__btns .el-button--primary').click()
      
      // 验证删除成功
      cy.get('.el-message--success').should('be.visible')
      cy.get('.el-table__row').should('not.contain', testUsername)
    })
  })

  describe('角色管理页面测试', () => {
    beforeEach(() => {
      cy.get('[data-cy="system-menu"]').click()
      cy.get('[data-cy="role-management"]').click()
      cy.url().should('include', '/system/role')
    })

    it('应该正确加载角色管理页面', () => {
      cy.contains('角色管理').should('be.visible')
      cy.get('[data-cy="role-table"]').should('be.visible')
      cy.get('[data-cy="add-role-btn"]').should('be.visible')
    })

    it('应该能够搜索角色', () => {
      cy.get('[placeholder="请输入角色名称"]').type('管理员')
      cy.get('[data-cy="search-btn"]').click()
      cy.get('.el-table__row').should('contain', '管理员')
    })

    it('应该能够新增角色', () => {
      const roleName = `测试角色${Date.now()}`
      const roleKey = `test_role_${Date.now()}`
      
      cy.get('[data-cy="add-role-btn"]').click()
      cy.get('.el-dialog').should('be.visible')
      
      cy.get('[data-cy="role-form"] [placeholder="请输入角色名称"]').type(roleName)
      cy.get('[data-cy="role-form"] [placeholder="请输入权限字符"]').type(roleKey)
      cy.get('[data-cy="role-form"] [placeholder="请输入显示顺序"]').type('10')
      
      // 选择一些权限
      cy.get('[data-cy="permission-tree"] .el-tree-node').first().click()
      
      cy.get('[data-cy="submit-btn"]').click()
      cy.get('.el-message--success').should('be.visible')
      cy.get('.el-table__row').should('contain', roleName)
    })
  })

  describe('菜单管理页面测试', () => {
    beforeEach(() => {
      cy.get('[data-cy="system-menu"]').click()
      cy.get('[data-cy="menu-management"]').click()
      cy.url().should('include', '/system/menu')
    })

    it('应该正确加载菜单管理页面', () => {
      cy.contains('菜单管理').should('be.visible')
      cy.get('[data-cy="menu-table"]').should('be.visible')
      cy.get('[data-cy="add-menu-btn"]').should('be.visible')
      cy.get('[data-cy="expand-btn"]').should('be.visible')
    })

    it('应该能够展开折叠菜单树', () => {
      cy.get('[data-cy="expand-btn"]').click()
      cy.get('.el-table__expand-icon').should('be.visible')
    })

    it('应该能够搜索菜单', () => {
      cy.get('[placeholder="请输入菜单名称"]').type('系统管理')
      cy.get('[data-cy="search-btn"]').click()
      cy.get('.el-table__row').should('contain', '系统管理')
    })
  })

  describe('部门管理页面测试', () => {
    beforeEach(() => {
      cy.get('[data-cy="system-menu"]').click()
      cy.get('[data-cy="dept-management"]').click()
      cy.url().should('include', '/system/dept')
    })

    it('应该正确加载部门管理页面', () => {
      cy.contains('部门管理').should('be.visible')
      cy.get('[data-cy="dept-table"]').should('be.visible')
      cy.get('[data-cy="add-dept-btn"]').should('be.visible')
    })

    it('应该能够新增部门', () => {
      const deptName = `测试部门${Date.now()}`
      
      cy.get('[data-cy="add-dept-btn"]').click()
      cy.get('.el-dialog').should('be.visible')
      
      cy.get('[data-cy="dept-form"] [placeholder="请输入部门名称"]').type(deptName)
      cy.get('[data-cy="dept-form"] [placeholder="请输入显示顺序"]').type('10')
      
      cy.get('[data-cy="submit-btn"]').click()
      cy.get('.el-message--success').should('be.visible')
      cy.get('.el-table__row').should('contain', deptName)
    })
  })

  describe('字典管理页面测试', () => {
    beforeEach(() => {
      cy.get('[data-cy="system-menu"]').click()
      cy.get('[data-cy="dict-management"]').click()
      cy.url().should('include', '/system/dict')
    })

    it('应该正确加载字典类型页面', () => {
      cy.contains('字典类型').should('be.visible')
      cy.get('[data-cy="dict-type-table"]').should('be.visible')
      cy.get('[data-cy="add-dict-type-btn"]').should('be.visible')
    })

    it('应该能够新增字典类型', () => {
      const dictName = `测试字典${Date.now()}`
      const dictType = `test_dict_${Date.now()}`
      
      cy.get('[data-cy="add-dict-type-btn"]').click()
      cy.get('.el-dialog').should('be.visible')
      
      cy.get('[data-cy="dict-form"] [placeholder="请输入字典名称"]').type(dictName)
      cy.get('[data-cy="dict-form"] [placeholder="请输入字典类型"]').type(dictType)
      
      cy.get('[data-cy="submit-btn"]').click()
      cy.get('.el-message--success').should('be.visible')
      cy.get('.el-table__row').should('contain', dictName)
    })
  })

  describe('参数设置页面测试', () => {
    beforeEach(() => {
      cy.get('[data-cy="system-menu"]').click()
      cy.get('[data-cy="config-management"]').click()
      cy.url().should('include', '/system/config')
    })

    it('应该正确加载参数设置页面', () => {
      cy.contains('参数设置').should('be.visible')
      cy.get('[data-cy="config-table"]').should('be.visible')
      cy.get('[data-cy="add-config-btn"]').should('be.visible')
    })

    it('应该能够搜索参数', () => {
      cy.get('[placeholder="请输入参数名称"]').type('用户管理')
      cy.get('[data-cy="search-btn"]').click()
      cy.wait(1000) // 等待搜索完成
    })
  })

  describe('通知公告页面测试', () => {
    beforeEach(() => {
      cy.get('[data-cy="system-menu"]').click()
      cy.get('[data-cy="notice-management"]').click()
      cy.url().should('include', '/system/notice')
    })

    it('应该正确加载通知公告页面', () => {
      cy.contains('通知公告').should('be.visible')
      cy.get('[data-cy="notice-table"]').should('be.visible')
      cy.get('[data-cy="add-notice-btn"]').should('be.visible')
    })

    it('应该能够新增公告', () => {
      const noticeTitle = `测试公告${Date.now()}`
      
      cy.get('[data-cy="add-notice-btn"]').click()
      cy.get('.el-dialog').should('be.visible')
      
      cy.get('[data-cy="notice-form"] [placeholder="请输入公告标题"]').type(noticeTitle)
      cy.get('[data-cy="notice-form"] .el-select').first().click()
      cy.get('.el-select-dropdown__item').first().click()
      
      // 富文本编辑器内容
      cy.get('.ql-editor').type('这是一个测试公告内容')
      
      cy.get('[data-cy="submit-btn"]').click()
      cy.get('.el-message--success').should('be.visible')
      cy.get('.el-table__row').should('contain', noticeTitle)
    })
  })

  describe('日志管理页面测试', () => {
    it('应该正确加载登录日志页面', () => {
      cy.get('[data-cy="monitor-menu"]').click()
      cy.get('[data-cy="login-log"]').click()
      cy.url().should('include', '/monitor/logininfor')
      
      cy.contains('登录日志').should('be.visible')
      cy.get('[data-cy="login-log-table"]').should('be.visible')
    })

    it('应该正确加载操作日志页面', () => {
      cy.get('[data-cy="monitor-menu"]').click()
      cy.get('[data-cy="operation-log"]').click()
      cy.url().should('include', '/monitor/operlog')
      
      cy.contains('操作日志').should('be.visible')
      cy.get('[data-cy="operation-log-table"]').should('be.visible')
    })

    it('应该能够搜索登录日志', () => {
      cy.get('[data-cy="monitor-menu"]').click()
      cy.get('[data-cy="login-log"]').click()
      
      cy.get('[placeholder="请输入用户名称"]').type('admin')
      cy.get('[data-cy="search-btn"]').click()
      cy.get('.el-table__row').should('contain', 'admin')
    })
  })

  describe('响应式测试', () => {
    const viewports = [
      { width: 1920, height: 1080, name: '桌面端' },
      { width: 1366, height: 768, name: '笔记本' },
      { width: 768, height: 1024, name: '平板' },
      { width: 375, height: 667, name: '手机' }
    ]

    viewports.forEach(viewport => {
      it(`应该在${viewport.name}(${viewport.width}x${viewport.height})正常显示`, () => {
        cy.viewport(viewport.width, viewport.height)
        
        // 访问用户管理页面
        cy.get('[data-cy="system-menu"]').click()
        cy.get('[data-cy="user-management"]').click()
        
        // 验证页面元素可见
        cy.get('[data-cy="user-table"]').should('be.visible')
        
        if (viewport.width < 768) {
          // 移动端可能需要特殊处理
          cy.get('.el-table__body-wrapper').should('have.css', 'overflow-x', 'auto')
        }
      })
    })
  })

  describe('性能测试', () => {
    it('页面加载时间应该在合理范围内', () => {
      const startTime = Date.now()
      
      cy.get('[data-cy="system-menu"]').click()
      cy.get('[data-cy="user-management"]').click()
      
      cy.get('[data-cy="user-table"]').should('be.visible').then(() => {
        const loadTime = Date.now() - startTime
        expect(loadTime).to.be.lessThan(5000) // 5秒内加载完成
      })
    })

    it('大数据量列表应该正常显示', () => {
      // 模拟大数据量情况
      cy.get('[data-cy="system-menu"]').click()
      cy.get('[data-cy="user-management"]').click()
      
      // 设置每页显示100条
      cy.get('.el-pagination__sizes .el-select').click()
      cy.get('.el-select-dropdown__item').contains('100').click()
      
      // 验证列表正常显示
      cy.get('[data-cy="user-table"]').should('be.visible')
      cy.get('.el-table__row').should('have.length.lessThan', 101)
    })
  })

  describe('错误处理测试', () => {
    it('应该正确处理网络错误', () => {
      // 模拟网络错误
      cy.intercept('GET', '/system/user/list', { forceNetworkError: true })
      
      cy.get('[data-cy="system-menu"]').click()
      cy.get('[data-cy="user-management"]').click()
      
      // 验证错误提示
      cy.get('.el-message--error').should('be.visible')
    })

    it('应该正确处理服务器错误', () => {
      // 模拟服务器错误
      cy.intercept('GET', '/system/user/list', { statusCode: 500 })
      
      cy.get('[data-cy="system-menu"]').click()
      cy.get('[data-cy="user-management"]').click()
      
      // 验证错误提示
      cy.get('.el-message--error').should('be.visible')
    })
  })

  // 测试清理
  afterEach(() => {
    // 清理可能创建的测试数据
    cy.window().then((win) => {
      // 可以在这里添加清理逻辑
    })
  })
})

// 自定义命令
Cypress.Commands.add('login', (username = 'admin', password = 'admin123') => {
  cy.visit('/login')
  cy.get('[data-cy="username"]').type(username)
  cy.get('[data-cy="password"]').type(password)
  cy.get('[data-cy="login-btn"]').click()
  cy.url().should('include', '/index')
})

Cypress.Commands.add('navigateToUserManagement', () => {
  cy.get('[data-cy="system-menu"]').click()
  cy.get('[data-cy="user-management"]').click()
  cy.url().should('include', '/system/user')
})

Cypress.Commands.add('createTestUser', (userData) => {
  cy.get('[data-cy="add-user-btn"]').click()
  cy.get('.el-dialog').should('be.visible')
  
  Object.keys(userData).forEach(key => {
    cy.get(`[data-cy="user-form"] [data-field="${key}"]`).type(userData[key])
  })
  
  cy.get('[data-cy="submit-btn"]').click()
  cy.get('.el-message--success').should('be.visible')
})

Cypress.Commands.add('deleteTestUser', (username) => {
  cy.get('.el-table__row').contains(username).parent().within(() => {
    cy.get('[data-cy="delete-btn"]').click()
  })
  
  cy.get('.el-message-box__btns .el-button--primary').click()
  cy.get('.el-message--success').should('be.visible')
})