<template>
  <div>
    <h1>Volumetrias para {{ clientName }}</h1>

    <div>
      <p><b>OK:</b> {{ okCount }}</p>
      <p><b>ERROR:</b> {{ errorCount }}</p>
    </div>

    <table>
      <thead>
        <tr>
          <th>Nome da Tabela</th>
          <th>Data da Busca</th>
          <th>Postgres</th>
          <th>Bigquery</th>
          <th>Query Source</th>
          <th>Query Sink</th>
          <th>Status</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="volumetry in volumetries" :key="volumetry.tabela">
          <td>{{ volumetry.tabela }}</td>
          <td>{{ volumetry.dataBusca | formatDate }}</td>
          <td>{{ volumetry.postgres }}</td>
          <td>{{ volumetry.bigquery }}</td>
          <td>{{ volumetry.querySource }}</td>
          <td>{{ volumetry.querySink }}</td>
          <td>{{ volumetry.postgres == volumetry.bigquery ? "OK" : "ERROR" }}</td>
        </tr>
      </tbody>
    </table>
    
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'VolumetryDetails',
  props: ['clientName'],
  data() {
    return {
      volumetries: []
    };
  },
  computed: {
    okCount() {
      return this.volumetries.filter(volumetry => volumetry.postgres == volumetry.bigquery).length;
    },
    errorCount() {
      return this.volumetries.filter(volumetry => volumetry.postgres != volumetry.bigquery).length;
    }
  },
  mounted() {
    this.fetchVolumetries();
  },
  methods: {
    async fetchVolumetries() {
      try {
        const response = await axios.get(`http://localhost:9999/volumetries/${this.clientName}`);
        this.volumetries = response.data;
      } catch (error) {
        console.error('Error fetching volumetries:', error);
      }
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
</style>
