<template>
  <div>
    <h1>Connectores monitorados de {{clientName}}</h1>
    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>Name</th>
          <th>Client Name</th>
          <th>Type</th>
          <th>Status Conector</th>
          <th>Status Tasks</th>
          <th>Data Ultimo Status</th>
          <th>Ações</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="connector in connectors" :key="connector.id">
          <td>{{ connector.id }}</td>
          <td>{{ connector.name }}</td>
          <td>{{ connector.nomeCliente }}</td>
          <td>{{ connector.type }}</td>
          <td>{{ connector.ultimoStatusConector }}</td>
          <td>{{ connector.ultimoStatusTask1 }}</td>
          <td>{{ connector.dataUltimoStatus | formatDate }}</td>
          <td>
            <button @click="viewDetails(connector.id)">Detalhes</button>
            <button @click="viewVolumetry(connector.nomeCliente)">Volumetria</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'ConnectorList',
  props: ['clientName'],
  data() {
    return {
      connectors: []
    };
  },
  mounted() {
    this.fetchConnectors();
  },
  methods: {
    async fetchConnectors() {
      try {
        const response = await axios.get(`http://localhost:9999/connectors/cliente/${this.clientName}`);
        this.connectors = response.data;
      } catch (error) {
        console.error('Error fetching connectors:', error);
      }
    },
    viewDetails(id) {
      this.$router.push({ name: 'ConnectorDetails', params: { id } });
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
