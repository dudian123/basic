<template>
  <div class="login-container">
    <el-form
      ref="loginFormRef"
      :model="loginForm"
      :rules="loginRules"
      class="login-form"
      auto-complete="on"
      label-position="left"
    >
      <div class="title-container">
        <h3 class="title text-center">RuoYi管理系统</h3>
      </div>

      <el-form-item prop="username">
        <span class="svg-container">
          <el-icon><User /></el-icon>
        </span>
        <el-input
          ref="username"
          v-model="loginForm.username"
          placeholder="账号"
          name="username"
          type="text"
          tabindex="1"
          auto-complete="on"
        />
      </el-form-item>

      <el-form-item prop="password">
        <span class="svg-container">
          <el-icon><Lock /></el-icon>
        </span>
        <el-input
          :key="passwordType"
          ref="password"
          v-model="loginForm.password"
          :type="passwordType"
          placeholder="密码"
          name="password"
          tabindex="2"
          auto-complete="on"
          @keyup.enter="handleLogin"
        />
        <span class="show-pwd" @click="showPwd">
          <el-icon><component :is="passwordType === 'password' ? 'View' : 'Hide'" /></el-icon>
        </span>
      </el-form-item>

      <!-- 验证码 -->
      <el-form-item v-if="captchaEnabled" prop="code">
        <span class="svg-container">
          <el-icon><Key /></el-icon>
        </span>
        <el-input
          v-model="loginForm.code"
          auto-complete="off"
          placeholder="验证码"
          style="width: 63%"
          @keyup.enter="handleLogin"
        />
        <div class="login-code">
          <img :src="codeUrl" class="login-code-img" @click="getCode" />
        </div>
      </el-form-item>

      <el-button
        :loading="loading"
        type="primary"
        style="width: 100%; margin-bottom: 30px"
        @click.prevent="handleLogin"
      >
        登录
      </el-button>
    </el-form>

    <div class="el-login-footer">
      <span>Copyright © 2018-2024 ruoyi.vip All Rights Reserved.</span>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Key, View, Hide } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

// 响应式数据
const loginFormRef = ref()
const username = ref()
const password = ref()
const loading = ref(false)
const passwordType = ref('password')
const captchaEnabled = ref(false)
const codeUrl = ref('')

// 登录表单
const loginForm = reactive({
  username: '',
  password: '',
  code: '',
  uuid: ''
})

// 表单验证规则
const loginRules = reactive({
  username: [{ required: true, trigger: 'blur', message: '请输入您的账号' }],
  password: [{ required: true, trigger: 'blur', message: '请输入您的密码' }],
  code: [{ required: true, trigger: 'change', message: '请输入验证码' }]
})

// 显示/隐藏密码
const showPwd = () => {
  if (passwordType.value === 'password') {
    passwordType.value = ''
  } else {
    passwordType.value = 'password'
  }
  nextTick(() => {
    password.value.focus()
  })
}

// 获取验证码 - 强制关闭验证码功能
const getCode = async () => {
  try {
    // 根据用户要求，强制关闭验证码功能
    captchaEnabled.value = false
    codeUrl.value = ''
    loginForm.uuid = ''
    loginForm.code = ''
    
    // 可选：仍然调用后端接口获取配置，但忽略验证码设置
    // const captchaData = await authStore.fetchCaptcha()
  } catch (error) {
    console.error('获取验证码失败:', error)
    // 即使获取失败，也确保验证码关闭
    captchaEnabled.value = false
    codeUrl.value = ''
    loginForm.uuid = ''
    loginForm.code = ''
  }
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    // 表单验证
    await loginFormRef.value.validate()
    
    loading.value = true
    
    // 构建登录数据 - 不包含验证码信息
    const loginData = {
      username: loginForm.username,
      password: loginForm.password,
      clientId: 'e5cd7e4891bf95d1d19206ce24a7b32e',
      grantType: 'password'
    }
    
    // 验证码功能已关闭，不发送验证码相关数据
    
    // 执行登录
    await authStore.loginAction(loginData)
    
    // 登录成功，跳转到首页
    router.push('/dashboard')
    
  } catch (error) {
    console.error('登录失败:', error)
    
    // 如果启用验证码，重新获取验证码
    if (captchaEnabled.value) {
      await getCode()
      loginForm.code = ''
    }
  } finally {
    loading.value = false
  }
}

// 组件挂载时获取验证码
onMounted(async () => {
  await getCode()
  
  // 聚焦到用户名输入框
  nextTick(() => {
    if (username.value) {
      username.value.focus()
    }
  })
})
</script>

<style lang="css" scoped>
.login-container {
  min-height: 100vh;
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}

.login-form {
  border-radius: 6px;
  background: #ffffff;
  width: 400px;
  padding: 25px 25px 5px 25px;
  box-shadow: 0 0 25px #cac6c6;
}

.title-container {
  position: relative;
}

.title {
  font-size: 26px;
  color: #2d3a4b;
  margin: 0px auto 40px auto;
  text-align: center;
  font-weight: bold;
}

.svg-container {
  padding: 6px 5px 6px 15px;
  color: #889aa4;
  vertical-align: middle;
  width: 30px;
  display: inline-block;
}

.show-pwd {
  position: absolute;
  right: 10px;
  top: 7px;
  font-size: 16px;
  color: #889aa4;
  cursor: pointer;
  user-select: none;
}

.login-form .el-form-item {
  border: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(0, 0, 0, 0.1);
  border-radius: 5px;
  color: #454545;
  position: relative;
}

.login-form .el-input {
  display: inline-block;
  height: 47px;
  width: 85%;
}

.login-form .el-input :deep(.el-input__wrapper) {
  background: transparent;
  border: 0px;
  border-radius: 0px;
  padding: 12px 5px 12px 15px;
  color: #fff;
  height: 47px;
  caret-color: #fff;
  box-shadow: none;
}

.login-form .el-input :deep(.el-input__inner) {
  background: transparent;
  border: 0px;
  border-radius: 0px;
  padding: 12px 5px 12px 15px;
  color: #fff;
  height: 47px;
  caret-color: #fff;
}

.login-form .el-input :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.7);
}

.login-code {
  width: 33%;
  height: 47px;
  float: right;
}

.login-code img {
  cursor: pointer;
  vertical-align: middle;
  height: 47px;
  border-radius: 4px;
  width: 100%;
}

.el-login-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}
</style>