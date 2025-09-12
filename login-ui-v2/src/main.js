import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import './styles/index.css'
import { hasPermi, hasRole, hasPermiOr, hasRoleOr } from './directives/permission'
import Pagination from './components/Pagination/index.vue'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(ElementPlus)
app.use(router)

// 注册权限指令
app.directive('hasPermi', hasPermi)
app.directive('hasRole', hasRole)
app.directive('hasPermiOr', hasPermiOr)
app.directive('hasRoleOr', hasRoleOr)

// 全局注册分页组件，兼容 <pagination /> 用法
app.component('Pagination', Pagination)

app.mount('#app')