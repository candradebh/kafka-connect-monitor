import Vue from 'vue';
import Router from 'vue-router';
import ConnectorListForCliente from '../components/ConnectorListForCliente.vue';
import ConnectorList from '../components/ConnectorList.vue';
import ConnectorDetails from '../components/ConnectorDetails.vue';
import VolumetryDetails from '../components/VolumetryDetails.vue';
import VolumetryTableDetails from '../components/VolumetryTableDetails.vue';
import VolumetryTableDetailsMesDia from '@/components/VolumetryTableDetailsMesDia.vue';
import VolumetryTableDetailsMesDiaHora from '@/components/VolumetryTableDetailsMesDiaHora.vue';
import VolumetryTableDetailsMesDiaHoraMinutes from '@/components/VolumetryTableDetailsMesDiaHoraMinutes.vue';
import VolumetryTableDetailsMesDiaHoraMinutesRows from '@/components/VolumetryTableDetailsMesDiaHoraMinutesRows.vue';
import ServicesDataUpdate from '@/components/ServicesDataUpdate.vue';
import VolumetryListForCliente from '@/components/VolumetryListForCliente.vue';
import HomePage from '@/components/HomePage.vue';

Vue.use(Router);

const routes = [
  {
    path: '/',
    name: 'HomePage',
    component: HomePage
  },
  {
    path: '/volumetry',
    name: 'VolumetryListForCliente',
    component: VolumetryListForCliente
  },
  {
    path: '/connectors',
    name: 'ConnectorListForCliente',
    component: ConnectorListForCliente
  },
  {
    path: '/services',
    name: 'ServicesDataUpdate',
    component: ServicesDataUpdate
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
  },
  {
    path: '/volumetry/:clientName/:tableName',
    name: 'VolumetryTableDetails',
    component: VolumetryTableDetails,
    props: true
  },
  {
    path: '/volumetry/:clientName/:tableName/:ano/:mes',
    name: 'VolumetryTableDetailsMesDia',
    component: VolumetryTableDetailsMesDia,
    props: true
  },
  {
    path: '/volumetry/:clientName/:tableName/:ano/:mes/:dia',
    name: 'VolumetryTableDetailsMesDiaHora',
    component: VolumetryTableDetailsMesDiaHora,
    props: true
  },
  {
    path: '/volumetry/:clientName/:tableName/:ano/:mes/:dia/:hora',
    name: 'VolumetryTableDetailsMesDiaHoraMinutos',
    component: VolumetryTableDetailsMesDiaHoraMinutes,
    props: true
  },
  {
    path: '/volumetry/:clientName/:tableName/:ano/:mes/:dia/:hora/:minuto',
    name: 'VolumetryTableDetailsMesDiaHoraMinutosRows',
    component: VolumetryTableDetailsMesDiaHoraMinutesRows,
    props: true
  }
];

const router = new Router({
  mode: 'history',
  routes
});

export default router;
