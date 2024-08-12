<template>
  <div>
    <h1>Cliente {{ clientName }} | Tabela: {{ tableName }}</h1>

    <div>
      <p><b>OK:</b> {{ okCount }}</p>
      <p><b>ERROR:</b> {{ errorCount }}</p>
    </div>

    <table>
      <thead>
        <tr>
          <th>Ano</th>
          <th>Mes</th>
          <th>Postgres</th>
          <th>Bigquery</th>
          <th>Status</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="volumetry in volumetries" :key="volumetry.tabela">
          <td>{{ volumetry.ano }}</td>
          <td>{{ volumetry.mes }}</td>
          <td>{{ volumetry.totalRecordsPostgres }}</td>
          <td>{{ volumetry.totalRecordsBigquery }}</td>
          <td>{{ volumetry.totalRecordsPostgres == volumetry.totalRecordsBigquery ? "OK" : "ERROR" }}</td>
          <td>
            <button @click="viewDetails(clientName,volumetry.tabela,volumetry.ano,volumetry.mes)">Detalhes</button>
          </td>
        </tr>
      </tbody>
    </table>
    
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'VolumetryTableDetails',
  props: ['clientName','tableName'],
  data() {
    return {
      volumetries: []
    };
  },
  computed: {
    okCount() {
      return 0;//this.volumetries.filter(volumetry => volumetry.postgres == volumetry.bigquery).length;
    },
    errorCount() {
      return 0; //this.volumetries.filter(volumetry => volumetry.postgres != volumetry.bigquery).length;
    }
  },
  mounted() {
    this.fetchVolumetries();
  },
  methods: {
    async fetchVolumetries() {
      try {
        const response = await axios.get(`http://localhost:9999/volumetries/${this.clientName}/${this.tableName}`);
        this.volumetries = response.data;
      } catch (error) {
        console.error('Error fetching volumetries:', error);
      }
    },
    viewDetails(clientName,tableName,ano,mes) {
      this.$router.push({ name: 'VolumetryTableDetailsMesDia', params: { clientName,tableName,ano,mes } });
    },
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
