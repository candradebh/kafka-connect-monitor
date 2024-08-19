import Vue from 'vue';
import App from './App.vue';
import router from './router';
import moment from 'moment';
import vuetify from './plugins/vuetify';
import { loadFonts } from './plugins/webfontloader';



loadFonts();

Vue.config.productionTip = false;

Vue.filter('formatDate', function(value) {
    if (value) {
        return moment(String(value)).format('DD/MM/YYYY HH:mm');
    }
});


import api from './api';  // Importe a instância de API
Vue.prototype.$api = api;// Adicione a instância da API ao protótipo do Vue

new Vue({
    router,
    vuetify,
    render: h => h(App)
}).$mount('#app');
