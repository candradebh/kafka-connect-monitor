import Vue from 'vue';
import Router from 'vue-router';
import HomePage from '@/components/HomePage.vue';
import ConnectorListForCliente from '@/components/connectores/ConnectorListForCliente.vue';
import ConnectorList from '@/components/connectores/ConnectorList.vue';
import ConnectorDetails from '@/components/connectores/ConnectorDetails.vue';
import VolumetryDetails from '@/components/volumetry/VolumetryDetails.vue';
import VolumetryTableDetails from '@/components/volumetry/VolumetryTableDetails.vue';
import VolumetryTableDetailsMesDia from '@/components/volumetry/VolumetryTableDetailsMesDia.vue';
import VolumetryTableDetailsMesDiaHora from '@/components/volumetry/VolumetryTableDetailsMesDiaHora.vue';
import VolumetryTableDetailsMesDiaHoraMinutes from '@/components/volumetry/VolumetryTableDetailsMesDiaHoraMinutes.vue';
import VolumetryTableDetailsMesDiaHoraMinutesRows from '@/components/volumetry/VolumetryTableDetailsMesDiaHoraMinutesRows.vue';
import VolumetryListForCliente from '@/components/volumetry/VolumetryListForCliente.vue';
import ScheduledTaskIndex from '@/components/scheduledTasks/ScheduledTaskIndex.vue';
import ScheduledTaskEdit from '@/components/scheduledTasks/ScheduledTaskEdit.vue';
import TableMetadataIndex from '@/components/tablemetadata/TableMetadataIndex.vue';
import TableMetadataEdit from '@/components/tablemetadata/TableMetadataEdit.vue';
import NotificationRecipientIndex from '@/components/recipients/NotificationRecipientIndex.vue';
import NotificationLogIndex from '@/components/notifications/NotificationLogIndex.vue';
import ConnectorStatusList from '@/components/connectores/ConnectorStatusList.vue';

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
    path: '/connector/status/:id',
    name: 'ConnectorStatusList',
    component: ConnectorStatusList,
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
  },
  {
    path: '/services',
    name: 'ScheduledTaskIndex',
    component: ScheduledTaskIndex
  },
  {
    path: '/services/:serviceName',
    name: 'ScheduledTaskEdit',
    component: ScheduledTaskEdit,
    props: true
  },
  {
    path: '/tables',
    name: 'TableMetadataIndex',
    component: TableMetadataIndex
  },
  {
    path: '/tables/:id',
    name: 'TableMetadataEdit',
    component: TableMetadataEdit,
    props: true
  },
  {
    path: '/recipients',
    name: 'NotificationRecipientIndex',
    component: NotificationRecipientIndex
  },
  {
    path: '/notificationlogs',
    name: 'NotificationLogIndex',
    component: NotificationLogIndex
  }
  
];

const router = new Router({
  mode: 'history',
  routes
});

export default router;
