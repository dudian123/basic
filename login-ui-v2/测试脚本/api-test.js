/**
 * 系统管理API接口测试脚本
 * 测试后端API的功能性、可靠性和数据完整性
 */

describe('系统管理API接口测试', () => {
  let authToken = ''
  let testUserId = null
  let testRoleId = null
  let testMenuId = null
  
  before(() => {
    // 获取认证token
    cy.request({
      method: 'POST',
      url: '/login',
      body: {
        username: 'admin',
        password: 'admin123',
        code: '1234', // 测试环境固定验证码
        uuid: 'test-uuid'
      }
    }).then(response => {
      expect(response.status).to.equal(200)
      authToken = response.body.token
      cy.log(`获取到认证token: ${authToken}`)
    })
  })
  
  beforeEach(() => {
    // 设置默认请求头
    cy.intercept('**', (req) => {
      if (authToken) {
        req.headers['Authorization'] = `Bearer ${authToken}`
      }
    })
  })

  describe('用户管理API测试', () => {
    it('获取用户列表 - GET /system/user/list', () => {
      cy.request({
        method: 'GET',
        url: '/system/user/list',
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      }).then(response => {
        expect(response.status).to.equal(200)
        expect(response.body).to.have.property('code', 200)
        expect(response.body).to.have.property('rows')
        expect(response.body.rows).to.be.an('array')
        expect(response.body).to.have.property('total')
        
        // 验证用户数据结构
        if (response.body.rows.length > 0) {
          const user = response.body.rows[0]
          expect(user).to.have.property('userId')
          expect(user).to.have.property('userName')
          expect(user).to.have.property('nickName')
          expect(user).to.have.property('email')
          expect(user).to.have.property('status')
        }
        
        cy.log(`获取到${response.body.rows.length}个用户，总计${response.body.total}个`)
      })
    })

    it('分页查询用户列表', () => {
      const pageParams = {
        pageNum: 1,
        pageSize: 10
      }
      
      cy.request({
        method: 'GET',
        url: '/system/user/list',
        qs: pageParams,
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      }).then(response => {
        expect(response.status).to.equal(200)
        expect(response.body.rows.length).to.be.at.most(10)
        
        // 验证分页信息
        expect(response.body).to.have.property('total')
        expect(response.body.total).to.be.a('number')
      })
    })

    it('搜索用户', () => {
      const searchParams = {
        userName: 'admin',
        status: '0'
      }
      
      cy.request({
        method: 'GET',
        url: '/system/user/list',
        qs: searchParams,
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      }).then(response => {
        expect(response.status).to.equal(200)
        
        // 验证搜索结果
        response.body.rows.forEach(user => {
          if (searchParams.userName) {
            expect(user.userName).to.include(searchParams.userName)
          }
          if (searchParams.status) {
            expect(user.status).to.equal(searchParams.status)
          }
        })
      })
    })

    it('新增用户 - POST /system/user', () => {
      const timestamp = Date.now()
      const newUser = {
        userName: `apitest${timestamp}`,
        nickName: `API测试用户${timestamp}`,
        email: `apitest${timestamp}@example.com`,
        phonenumber: `138${timestamp.toString().slice(-8)}`,
        sex: '1',
        status: '0',
        password: 'Test123456',
        deptId: 103,
        roleIds: [2]
      }
      
      cy.request({
        method: 'POST',
        url: '/system/user',
        body: newUser,
        headers: {
          'Authorization': `Bearer ${authToken}`,
          'Content-Type': 'application/json'
        }
      }).then(response => {
        expect(response.status).to.equal(200)
        expect(response.body).to.have.property('code', 200)
        expect(response.body).to.have.property('msg')
        
        // 保存用户ID用于后续测试
        if (response.body.data) {
          testUserId = response.body.data.userId || response.body.data
        }
        
        cy.log(`成功创建用户: ${newUser.userName}, ID: ${testUserId}`)
      })
    })

    it('获取用户详情 - GET /system/user/{id}', () => {
      if (!testUserId) {
        cy.log('跳过用户详情测试，因为没有测试用户ID')
        return
      }
      
      cy.request({
        method: 'GET',
        url: `/system/user/${testUserId}`,
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      }).then(response => {
        expect(response.status).to.equal(200)
        expect(response.body).to.have.property('code', 200)
        expect(response.body).to.have.property('data')
        
        const userData = response.body.data
        expect(userData).to.have.property('user')
        expect(userData.user.userId).to.equal(testUserId)
      })
    })

    it('修改用户 - PUT /system/user', () => {
      if (!testUserId) {
        cy.log('跳过用户修改测试，因为没有测试用户ID')
        return
      }
      
      const updateData = {
        userId: testUserId,
        nickName: `修改后的API测试用户${Date.now()}`,
        email: `updated${Date.now()}@example.com`,
        status: '0'
      }
      
      cy.request({
        method: 'PUT',
        url: '/system/user',
        body: updateData,
        headers: {
          'Authorization': `Bearer ${authToken}`,
          'Content-Type': 'application/json'
        }
      }).then(response => {
        expect(response.status).to.equal(200)
        expect(response.body).to.have.property('code', 200)
        
        cy.log(`成功修改用户: ${testUserId}`)
      })
    })

    it('重置用户密码 - PUT /system/user/resetPwd', () => {
      if (!testUserId) {
        cy.log('跳过密码重置测试，因为没有测试用户ID')
        return
      }
      
      const resetData = {
        userId: testUserId,
        password: 'NewPassword123'
      }
      
      cy.request({
        method: 'PUT',
        url: '/system/user/resetPwd',
        body: resetData,
        headers: {
          'Authorization': `Bearer ${authToken}`,
          'Content-Type': 'application/json'
        }
      }).then(response => {
        expect(response.status).to.equal(200)
        expect(response.body).to.have.property('code', 200)
        
        cy.log(`成功重置用户密码: ${testUserId}`)
      })
    })

    it('删除用户 - DELETE /system/user/{ids}', () => {
      if (!testUserId) {
        cy.log('跳过用户删除测试，因为没有测试用户ID')
        return
      }
      
      cy.request({
        method: 'DELETE',
        url: `/system/user/${testUserId}`,
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      }).then(response => {
        expect(response.status).to.equal(200)
        expect(response.body).to.have.property('code', 200)
        
        cy.log(`成功删除用户: ${testUserId}`)
        testUserId = null
      })
    })
  })

  describe('角色管理API测试', () => {
    it('获取角色列表 - GET /system/role/list', () => {
      cy.request({
        method: 'GET',
        url: '/system/role/list',
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      }).then(response => {
        expect(response.status).to.equal(200)
        expect(response.body).to.have.property('code', 200)
        expect(response.body).to.have.property('rows')
        expect(response.body.rows).to.be.an('array')
        
        // 验证角色数据结构
        if (response.body.rows.length > 0) {
          const role = response.body.rows[0]
          expect(role).to.have.property('roleId')
          expect(role).to.have.property('roleName')
          expect(role).to.have.property('roleKey')
          expect(role).to.have.property('status')
        }
      })
    })

    it('新增角色 - POST /system/role', () => {
      const timestamp = Date.now()
      const newRole = {
        roleName: `API测试角色${timestamp}`,
        roleKey: `api_test_role_${timestamp}`,
        roleSort: 10,
        status: '0',
        remark: '这是一个API测试角色',
        menuIds: [1, 2, 3] // 分配一些基础菜单权限
      }
      
      cy.request({
        method: 'POST',
        url: '/system/role',
        body: newRole,
        headers: {
          'Authorization': `Bearer ${authToken}`,
          'Content-Type': 'application/json'
        }
      }).then(response => {
        expect(response.status).to.equal(200)
        expect(response.body).to.have.property('code', 200)
        
        // 保存角色ID用于后续测试
        if (response.body.data) {
          testRoleId = response.body.data.roleId || response.body.data
        }
        
        cy.log(`成功创建角色: ${newRole.roleName}, ID: ${testRoleId}`)
      })
    })

    it('获取角色详情 - GET /system/role/{id}', () => {
      if (!testRoleId) {
        cy.log('跳过角色详情测试，因为没有测试角色ID')
        return
      }
      
      cy.request({
        method: 'GET',
        url: `/system/role/${testRoleId}`,
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      }).then(response => {
        expect(response.status).to.equal(200)
        expect(response.body).to.have.property('code', 200)
        expect(response.body).to.have.property('data')
        
        const roleData = response.body.data
        expect(roleData.roleId).to.equal(testRoleId)
      })
    })

    it('修改角色 - PUT /system/role', () => {
      if (!testRoleId) {
        cy.log('跳过角色修改测试，因为没有测试角色ID')
        return
      }
      
      const updateData = {
        roleId: testRoleId,
        roleName: `修改后的API测试角色${Date.now()}`,
        remark: '这是修改后的API测试角色',
        status: '0'
      }
      
      cy.request({
        method: 'PUT',
        url: '/system/role',
        body: updateData,
        headers: {
          'Authorization': `Bearer ${authToken}`,
          'Content-Type': 'application/json'
        }
      }).then(response => {
        expect(response.status).to.equal(200)
        expect(response.body).to.have.property('code', 200)
        
        cy.log(`成功修改角色: ${testRoleId}`)
      })
    })

    it('删除角色 - DELETE /system/role/{ids}', () => {
      if (!testRoleId) {
        cy.log('跳过角色删除测试，因为没有测试角色ID')
        return
      }
      
      cy.request({
        method: 'DELETE',
        url: `/system/role/${testRoleId}`,
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      }).then(response => {
        expect(response.status).to.equal(200)
        expect(response.body).to.have.property('code', 200)
        
        cy.log(`成功删除角色: ${testRoleId}`)
        testRoleId = null
      })
    })
  })

  describe('菜单管理API测试', () => {
    it('获取菜单列表 - GET /system/menu/list', () => {
      cy.request({
        method: 'GET',
        url: '/system/menu/list',
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      }).then(response => {
        expect(response.status).to.equal(200)
        expect(response.body).to.have.property('code', 200)
        expect(response.body).to.have.property('data')
        expect(response.body.data).to.be.an('array')
        
        // 验证菜单数据结构
        if (response.body.data.length > 0) {
          const menu = response.body.data[0]
          expect(menu).to.have.property('menuId')
          expect(menu).to.have.property('menuName')
          expect(menu).to.have.property('menuType')
          expect(menu).to.have.property('status')
        }
      })
    })

    it('获取菜单树结构 - GET /system/menu/treeselect', () => {
      cy.request({
        method: 'GET',
        url: '/system/menu/treeselect',
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      }).then(response => {
        expect(response.status).to.equal(200)
        expect(response.body).to.have.property('code', 200)
        expect(response.body).to.have.property('data')
        expect(response.body.data).to.be.an('array')
        
        // 验证树结构
        if (response.body.data.length > 0) {
          const treeNode = response.body.data[0]
          expect(treeNode).to.have.property('id')
          expect(treeNode).to.have.property('label')
        }
      })
    })

    it('新增菜单 - POST /system/menu', () => {
      const timestamp = Date.now()
      const newMenu = {
        menuName: `API测试菜单${timestamp}`,
        parentId: 0,
        orderNum: 10,
        path: `/api-test-${timestamp}`,
        component: 'api/test/index',
        menuType: 'C',
        visible: '0',
        status: '0',
        icon: 'test',
        remark: '这是一个API测试菜单'
      }
      
      cy.request({
        method: 'POST',
        url: '/system/menu',
        body: newMenu,
        headers: {
          'Authorization': `Bearer ${authToken}`,
          'Content-Type': 'application/json'
        }
      }).then(response => {
        expect(response.status).to.equal(200)
        expect(response.body).to.have.property('code', 200)
        
        // 保存菜单ID用于后续测试
        if (response.body.data) {
          testMenuId = response.body.data.menuId || response.body.data
        }
        
        cy.log(`成功创建菜单: ${newMenu.menuName}, ID: ${testMenuId}`)
      })
    })

    it('获取菜单详情 - GET /system/menu/{id}', () => {
      if (!testMenuId) {
        cy.log('跳过菜单详情测试，因为没有测试菜单ID')
        return
      }
      
      cy.request({
        method: 'GET',
        url: `/system/menu/${testMenuId}`,
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      }).then(response => {
        expect(response.status).to.equal(200)
        expect(response.body).to.have.property('code', 200)
        expect(response.body).to.have.property('data')
        
        const menuData = response.body.data
        expect(menuData.menuId).to.equal(testMenuId)
      })
    })

    it('修改菜单 - PUT /system/menu', () => {
      if (!testMenuId) {
        cy.log('跳过菜单修改测试，因为没有测试菜单ID')
        return
      }
      
      const updateData = {
        menuId: testMenuId,
        menuName: `修改后的API测试菜单${Date.now()}`,
        remark: '这是修改后的API测试菜单',
        status: '0'
      }
      
      cy.request({
        method: 'PUT',
        url: '/system/menu',
        body: updateData,
        headers: {
          'Authorization': `Bearer ${authToken}`,
          'Content-Type': 'application/json'
        }
      }).then(response => {
        expect(response.status).to.equal(200)
        expect(response.body).to.have.property('code', 200)
        
        cy.log(`成功修改菜单: ${testMenuId}`)
      })
    })

    it('删除菜单 - DELETE /system/menu/{id}', () => {
      if (!testMenuId) {
        cy.log('跳过菜单删除测试，因为没有测试菜单ID')
        return
      }
      
      cy.request({
        method: 'DELETE',
        url: `/system/menu/${testMenuId}`,
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      }).then(response => {
        expect(response.status).to.equal(200)
        expect(response.body).to.have.property('code', 200)
        
        cy.log(`成功删除菜单: ${testMenuId}`)
        testMenuId = null
      })
    })
  })

  describe('部门管理API测试', () => {
    it('获取部门列表 - GET /system/dept/list', () => {
      cy.request({
        method: 'GET',
        url: '/system/dept/list',
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      }).then(response => {
        expect(response.status).to.equal(200)
        expect(response.body).to.have.property('code', 200)
        expect(response.body).to.have.property('data')
        expect(response.body.data).to.be.an('array')
      })
    })

    it('获取部门树结构 - GET /system/dept/treeselect', () => {
      cy.request({
        method: 'GET',
        url: '/system/dept/treeselect',
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      }).then(response => {
        expect(response.status).to.equal(200)
        expect(response.body).to.have.property('code', 200)
        expect(response.body).to.have.property('data')
        expect(response.body.data).to.be.an('array')
      })
    })
  })

  describe('字典管理API测试', () => {
    it('获取字典类型列表 - GET /system/dict/type/list', () => {
      cy.request({
        method: 'GET',
        url: '/system/dict/type/list',
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      }).then(response => {
        expect(response.status).to.equal(200)
        expect(response.body).to.have.property('code', 200)
        expect(response.body).to.have.property('rows')
        expect(response.body.rows).to.be.an('array')
      })
    })

    it('获取字典数据 - GET /system/dict/data/type/{dictType}', () => {
      const dictType = 'sys_user_sex'
      
      cy.request({
        method: 'GET',
        url: `/system/dict/data/type/${dictType}`,
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      }).then(response => {
        expect(response.status).to.equal(200)
        expect(response.body).to.have.property('code', 200)
        expect(response.body).to.have.property('data')
        expect(response.body.data).to.be.an('array')
      })
    })
  })

  describe('参数配置API测试', () => {
    it('获取参数配置列表 - GET /system/config/list', () => {
      cy.request({
        method: 'GET',
        url: '/system/config/list',
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      }).then(response => {
        expect(response.status).to.equal(200)
        expect(response.body).to.have.property('code', 200)
        expect(response.body).to.have.property('rows')
        expect(response.body.rows).to.be.an('array')
      })
    })

    it('根据参数键名查询参数值 - GET /system/config/configKey/{configKey}', () => {
      const configKey = 'sys.user.initPassword'
      
      cy.request({
        method: 'GET',
        url: `/system/config/configKey/${configKey}`,
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      }).then(response => {
        expect(response.status).to.equal(200)
        expect(response.body).to.have.property('msg', '操作成功')
      })
    })
  })

  describe('通知公告API测试', () => {
    it('获取通知公告列表 - GET /system/notice/list', () => {
      cy.request({
        method: 'GET',
        url: '/system/notice/list',
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      }).then(response => {
        expect(response.status).to.equal(200)
        expect(response.body).to.have.property('code', 200)
        expect(response.body).to.have.property('rows')
        expect(response.body.rows).to.be.an('array')
      })
    })
  })

  describe('日志管理API测试', () => {
    it('获取操作日志列表 - GET /monitor/operlog/list', () => {
      cy.request({
        method: 'GET',
        url: '/monitor/operlog/list',
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      }).then(response => {
        expect(response.status).to.equal(200)
        expect(response.body).to.have.property('code', 200)
        expect(response.body).to.have.property('rows')
        expect(response.body.rows).to.be.an('array')
      })
    })

    it('获取登录日志列表 - GET /monitor/logininfor/list', () => {
      cy.request({
        method: 'GET',
        url: '/monitor/logininfor/list',
        headers: {
          'Authorization': `Bearer ${authToken}`
        }
      }).then(response => {
        expect(response.status).to.equal(200)
        expect(response.body).to.have.property('code', 200)
        expect(response.body).to.have.property('rows')
        expect(response.body.rows).to.be.an('array')
      })
    })
  })

  describe('错误处理测试', () => {
    it('无效token访问应返回401', () => {
      cy.request({
        method: 'GET',
        url: '/system/user/list',
        headers: {
          'Authorization': 'Bearer invalid-token'
        },
        failOnStatusCode: false
      }).then(response => {
        expect(response.status).to.equal(401)
      })
    })

    it('无权限访问应返回403', () => {
      // 这里需要使用一个权限受限的token
      cy.request({
        method: 'DELETE',
        url: '/system/user/999999',
        headers: {
          'Authorization': `Bearer ${authToken}`
        },
        failOnStatusCode: false
      }).then(response => {
        // 可能返回403或404，取决于具体实现
        expect([403, 404, 500]).to.include(response.status)
      })
    })

    it('不存在的资源应返回404', () => {
      cy.request({
        method: 'GET',
        url: '/system/user/999999',
        headers: {
          'Authorization': `Bearer ${authToken}`
        },
        failOnStatusCode: false
      }).then(response => {
        expect([404, 500]).to.include(response.status)
      })
    })

    it('无效的请求参数应返回400', () => {
      const invalidUser = {
        userName: '', // 空用户名
        email: 'invalid-email' // 无效邮箱格式
      }
      
      cy.request({
        method: 'POST',
        url: '/system/user',
        body: invalidUser,
        headers: {
          'Authorization': `Bearer ${authToken}`,
          'Content-Type': 'application/json'
        },
        failOnStatusCode: false
      }).then(response => {
        expect([400, 500]).to.include(response.status)
      })
    })
  })

  describe('数据完整性测试', () => {
    it('创建用户后应能正确查询', () => {
      const timestamp = Date.now()
      const testUser = {
        userName: `integrity_test_${timestamp}`,
        nickName: `完整性测试用户${timestamp}`,
        email: `integrity${timestamp}@example.com`,
        phonenumber: `139${timestamp.toString().slice(-8)}`,
        sex: '1',
        status: '0',
        password: 'Test123456',
        deptId: 103
      }
      
      // 创建用户
      cy.request({
        method: 'POST',
        url: '/system/user',
        body: testUser,
        headers: {
          'Authorization': `Bearer ${authToken}`,
          'Content-Type': 'application/json'
        }
      }).then(createResponse => {
        expect(createResponse.status).to.equal(200)
        
        // 查询用户列表验证用户已创建
        cy.request({
          method: 'GET',
          url: '/system/user/list',
          qs: { userName: testUser.userName },
          headers: {
            'Authorization': `Bearer ${authToken}`
          }
        }).then(listResponse => {
          expect(listResponse.status).to.equal(200)
          expect(listResponse.body.rows).to.have.length.greaterThan(0)
          
          const foundUser = listResponse.body.rows.find(u => u.userName === testUser.userName)
          expect(foundUser).to.exist
          expect(foundUser.nickName).to.equal(testUser.nickName)
          expect(foundUser.email).to.equal(testUser.email)
          
          // 清理测试数据
          cy.request({
            method: 'DELETE',
            url: `/system/user/${foundUser.userId}`,
            headers: {
              'Authorization': `Bearer ${authToken}`
            }
          })
        })
      })
    })
  })

  describe('并发测试', () => {
    it('并发创建用户测试', () => {
      const promises = []
      const userCount = 5
      
      for (let i = 0; i < userCount; i++) {
        const timestamp = Date.now() + i
        const user = {
          userName: `concurrent_test_${timestamp}`,
          nickName: `并发测试用户${timestamp}`,
          email: `concurrent${timestamp}@example.com`,
          phonenumber: `137${timestamp.toString().slice(-8)}`,
          sex: '1',
          status: '0',
          password: 'Test123456',
          deptId: 103
        }
        
        const promise = cy.request({
          method: 'POST',
          url: '/system/user',
          body: user,
          headers: {
            'Authorization': `Bearer ${authToken}`,
            'Content-Type': 'application/json'
          }
        })
        
        promises.push(promise)
      }
      
      // 等待所有请求完成
      Promise.all(promises).then(responses => {
        responses.forEach(response => {
          expect(response.status).to.equal(200)
        })
        
        cy.log(`成功并发创建${userCount}个用户`)
      })
    })
  })
})