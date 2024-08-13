<template>
  <div>
    <h2>Cliente: {{ clientName }} </h2>
    <h2>Tabela: {{ tableName }} </h2>
    <h2>DIA: {{ dia }} | MES: {{ mes }} | ANO: {{ ano }}</h2>
    <div>
      <p><b>OK:</b> {{ okCount }}</p>
      <p><b>ERROR:</b> {{ errorCount }}</p>
    </div>

    <table>
      <thead>
        <tr>
          <th>Hora</th>
          <th>Postgres</th>
          <th>Bigquery</th>
          <th>Status</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="volumetry in volumetries" :key="volumetry.tabela">
          <td>{{ volumetry.hora }}</td>
          <td>{{ volumetry.totalRecordsPostgres }}</td>
          <td>{{ volumetry.totalRecordsBigquery }}</td>
          <td>{{ volumetry.totalRecordsPostgres == volumetry.totalRecordsBigquery ? "OK" : "ERROR" }}</td>
          <td>
            <button @click="viewDetails(clientName,volumetry.tabela)">Detalhes</button>
          </td>
        </tr>
      </tbody>
    </table>
    
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'VolumetryTableDetailsMesDiaHora',
  props: ['clientName','tableName','ano','mes','dia'],
  data() {
    return {
      volumetries: []
    };
  },
  computed: {
    okCount() {
      return this.volumetries.filter(volumetry => volumetry.totalRecordsPostgres == volumetry.totalRecordsBigquery).length;
    },
    errorCount() {
      return this.volumetries.filter(volumetry => volumetry.totalRecordsPostgres != volumetry.totalRecordsBigquery).length;
    }
  },
  mounted() {
    this.fetchVolumetries();
  },
  methods: {
    async fetchVolumetries() {
      try {
        const response = await axios.get(`http://localhost:9999/volumetries/${this.clientName}/${this.tableName}/${this.ano}/${this.mes}/${this.dia}`);
        this.volumetries = response.data;
      } catch (error) {
        console.error('Erro ao obter os dados:', error);
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
