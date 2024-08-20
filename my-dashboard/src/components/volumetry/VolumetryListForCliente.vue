<template>
  <div>
    <h1>Volumetria dos Clientes</h1>
    <v-data-table
      :headers="headers"
      :items="connectorSummaries"
      class="elevation-1"
      @click:row="viewVolumetry"
    >
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
    viewVolumetry(table) {
      this.$router.push({ name: 'VolumetryDetails', params: { clientName: table.nomeCliente }  });
    }
  }
};
</script>
