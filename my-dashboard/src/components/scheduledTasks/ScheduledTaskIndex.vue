<template>
  <div>
    <h2>Serviços da Aplicação</h2>
    <v-data-table
      :headers="headers"
      :items="services"
      class="elevation-1"
      @click:row="editService"
    >
    <template v-slot:[`item.lastExecutionTime`]="{ item }">
        {{ item.lastExecutionTime | formatDate }}
      </template>
    </v-data-table>
  </div>
</template>

<script>

export default {
  name: 'ScheduledTaskIndex',
  data() {
    return {
      headers: [
        { text: 'Nome', value: 'serviceName' },
        { text: 'Cron', value: 'cronExpression' },
        { text: 'Ativo', value: 'active' },
        { text: 'Ultima Execução', value: 'lastExecutionTime' },
        { text: 'Descrição', value: 'description' },
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
    },
    editService(service) {
      // Navega para a página de edição ou abre um modal de edição
      this.$router.push({ name: 'ScheduledTaskEdit', params: { serviceName: service.serviceName } });
    }
  }
};
</script>
