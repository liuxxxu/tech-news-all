import { createApp } from 'vue';
import { createPinia } from 'pinia';
import Antd from 'ant-design-vue';
import App from './App.vue';
import 'ant-design-vue/dist/reset.css';

import router from './router';

// 引入全局样式
import './style/index.css';

const app = createApp(App);

// 注册 Pinia
app.use(createPinia());
// 注册 Vue Router
app.use(router);
// 注册 Ant Design Vue
app.use(Antd);

app.mount('#app');