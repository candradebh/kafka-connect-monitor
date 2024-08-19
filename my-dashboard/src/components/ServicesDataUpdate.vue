<template>
  <div>
    <h2>Serviços da Aplicação</h2>
    <v-data-table
      :headers="headers"
      :items="services"
      class="elevation-1"
    >
    </v-data-table>
  </div>
</template>

<script>

export default {
  data() {
    return {
      headers: [
        { text: 'Nome', value: 'serviceName' },
        { text: 'Cron', value: 'cronExpression' },
        { text: 'Ultima Execução', value: 'lastExecutionTime' },
        { text: 'Descrição', value: 'description' },
        { text: 'Ações', value: 'actions', sortable: false },
      ],
      services: []
    };
  },
  created() {
    this.fetchServices();
  },
  methods: {
    fetchServices() {
      this.$api.get('/scheduled-tasks')
        .then(response => {
          this.services = response.data;
        })
        .catch(error => {
          console.error('Error fetching connector summaries:', error);
        });
    }
  }
};
</script>
