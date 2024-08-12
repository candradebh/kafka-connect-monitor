import Vue from 'vue';
import Router from 'vue-router';
import ConnectorListForCliente from '../components/ConnectorListForCliente.vue';
import ConnectorList from '../components/ConnectorList.vue';
import ConnectorDetails from '../components/ConnectorDetails.vue';
import VolumetryDetails from '../components/VolumetryDetails.vue';

Vue.use(Router);

const routes = [
  {
    path: '/',
    name: 'ConnectorListForCliente',
    component: ConnectorListForCliente
  },
  {
    path: '/connectors/:clientName',
    name: 'ConnectorList',
    component: ConnectorList,
    props: true
  },
  {
    path: '/connector/:id',
    name: 'ConnectorDetails',
    component: ConnectorDetails,
    props: true
  },
  {
    path: '/volumetry/:clientName',
    name: 'VolumetryDetails',
    component: VolumetryDetails,
    props: true
  }
];

const router = new Router({
  mode: 'history',
  routes
});

export default router;
