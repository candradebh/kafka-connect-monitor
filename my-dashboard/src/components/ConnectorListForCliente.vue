<template>
  <div>
    <h1>Clientes</h1>
    <table>
      <thead>
        <tr>
          <th>Nome do Cliente</th>
          <th>Conectores</th>
          <th>Ações</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="summary in connectorSummaries" :key="summary.nomeCliente">
          <td>{{ summary.nomeCliente }}</td>
          <td>{{ summary.connectorCount }}</td>
          <td>
            <button @click="viewDetails(summary.nomeCliente)">Detalhes</button>
            <button @click="viewVolumetry(summary.nomeCliente)">Volumetria</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      connectorSummaries: []
    };
  },
  created() {
    this.fetchConnectorSummaries();
  },
  methods: {
    fetchConnectorSummaries() {
      axios.get('http://localhost:9999/connectors/grouped-by-client')
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
<style scoped>
table {
  width: 100%;
  border-collapse: collapse;
}
th, td {
  padding: 10px;
  border: 1px solid #ccc;
  text-align: left;
}
th {
  background-color: #f4f4f4;
}
button {
  margin-right: 5px;
}
</style>