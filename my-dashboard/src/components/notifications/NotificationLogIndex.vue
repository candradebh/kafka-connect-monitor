<template>
    <v-container>
      <v-data-table
        :headers="headers"
        :items="logs"
        :server-items-length="totalLogs"
        :loading="loading"
        :items-per-page.sync="itemsPerPage"
        :page.sync="page"
        :options.sync="options"
        @update:page="fetchLogs"
        @update:items-per-page="fetchLogs"
      >
        <template v-slot:top>
          <v-toolbar flat>
            <v-toolbar-title>Notification Logs</v-toolbar-title>
            <v-spacer></v-spacer>
            <v-text-field v-model="filters.nomeCliente" label="Cliente"></v-text-field>
            <v-text-field v-model="filters.recipient" label="Recipient"></v-text-field>
            <v-text-field v-model="filters.subject" label="Assunto"></v-text-field>
            <v-btn color="primary" @click="fetchLogs">Filtrar</v-btn>
          </v-toolbar>
        </template>

        <template v-slot:[`item.dataCriacao`]="{ item }">
          {{ item.dataCriacao | formatDate }}
        </template>
      </v-data-table>
    </v-container>
  </template>
  
  <script>
  export default {
    data() {
      return {
        logs: [],
        totalLogs: 0,
        loading: false,
        itemsPerPage: 10,
        page: 1,
        options: {},
        filters: {
          nomeCliente: '',
          recipient: '',
          subject: ''
        },
        headers: [
          { text: 'Cliente', value: 'nomeCliente' },
          { text: 'Destinatários', value: 'recipient' },
          { text: 'Assunto', value: 'subject' },
          { text: 'Mensagem', value: 'message' },
          { text: 'Data Criação', value: 'dataCriacao' },
          { text: 'Enviado', value: 'foiEnviado' },
          { text: 'Tem Erro', value: 'temErro' },
          { text: 'Error', value: 'errorMessage' }
        ]
      };
    },
    methods: {
      fetchLogs() {
        this.loading = true;
        const { page, itemsPerPage, filters } = this;
        this.$api
          .get('/notificationlogs', {
            params: {
              page: page - 1,
              size: itemsPerPage,
              nomeCliente: filters.nomeCliente,
              recipient: filters.recipient,
              subject: filters.subject
            }
          })
          .then(response => {
            this.logs = response.data.content;
            this.totalLogs = response.data.totalElements;
            this.loading = false;
          })
          .catch(() => {
            this.loading = false;
          });
      }
    },
    created() {
      this.fetchLogs();
    }
  };
  </script>
  