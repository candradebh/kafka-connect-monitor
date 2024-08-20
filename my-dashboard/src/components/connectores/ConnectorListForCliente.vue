<template>
  <div>
    <h1>Connectores Clientes</h1>
    <v-data-table
      :headers="headers"
      :items="connectorSummaries"
      class="elevation-1"
    >
      <template v-slot:[`item.actions`]="{ item }">
        <v-btn icon @click="viewDetails(item.nomeCliente)">
          <v-icon>mdi-magnify</v-icon>
        </v-btn>
      </template>
    </v-data-table>
  </div>
</template>

<script>

export default {
  data() {
    return {
      headers: [
        { text: 'Nome do Cliente', value: 'nomeCliente' },
        { text: 'Conectores', value: 'connectorCount' },
        { text: 'Ações', value: 'actions', sortable: false },
      ],
      connectorSummaries: []
    };
  },
  created() {
    this.fetchConnectorSummaries();
  },
  methods: {
    fetchConnectorSummaries() {
      this.$api.get('/connectors/grouped-by-client')
        .then(response => {
          this.connectorSummaries = response.data;
        })
        .catch(error => {
          console.error('Error fetching connector summaries:', error);
        });
    },
    viewDetails(clientName) {
      this.$router.push({ name: 'ConnectorList', params: { clientName } });
    },
    viewVolumetry(clientName) {
      this.$router.push({ name: 'VolumetryDetails', params: { clientName } });
    }
  }
};
</script>
