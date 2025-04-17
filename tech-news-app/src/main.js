import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import { setupVant } from './utils/vant-ui'
import 'vant/lib/index.css'
import './styles/common.less'

// 创建app实例
const app = createApp(App)
// 创建pinia实例
const pinia = createPinia()

// 注册Vant组件
setupVant(app)

// 使用插件
app.use(router).use(pinia).mount('#app')
