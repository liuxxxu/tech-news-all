// 按需导入Vant组件
import {
    Tabbar,
    TabbarItem,
    Icon,
    NavBar,
    Search,
    Cell,
    CellGroup,
    Button,
    Image as VanImage,
    Lazyload,
    Badge,
    Grid,
    GridItem,
    Tab,
    Tabs,
    Swipe,
    SwipeItem,
    showToast,
    showLoadingToast,
    showSuccessToast,
    showFailToast,
    closeToast,
    showDialog,
    Field,
    Form,
    Checkbox,
    CheckboxGroup,
    Divider,
    Popup,
    Empty
} from 'vant';

// 定义一个安装所有组件的函数
export function setupVant(app) {
    // 注册组件
    app.use(Tabbar)
        .use(TabbarItem)
        .use(Icon)
        .use(NavBar)
        .use(Search)
        .use(Cell)
        .use(CellGroup)
        .use(Button)
        .use(VanImage)
        .use(Badge)
        .use(Grid)
        .use(GridItem)
        .use(Tab)
        .use(Tabs)
        .use(Swipe)
        .use(SwipeItem)
        .use(Field)
        .use(Form)
        .use(Checkbox)
        .use(CheckboxGroup)
        .use(Divider)
        .use(Popup)
        .use(Empty)
        .use(Lazyload);
}

// 导出需要单独使用的组件
export { showToast, showSuccessToast, showFailToast, showDialog, showLoadingToast, closeToast };
