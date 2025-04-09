import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/reset.css';
import { createPinia } from 'pinia';

// 引入图标
import * as Icons from '@ant-design/icons-vue';

const app = createApp(App);
const pinia = createPinia();

// 注册Pinia
app.use(pinia);

// 注册Ant Design Vue
app.use(Antd);

// 注册全局方法
app.config.globalProperties.$message = Antd.message;

// 注册所有图标组件
for (const [key, component] of Object.entries(Icons)) {
    app.component(key, component);
}

app.use(router);
app.mount('#app');