<template>
  <div>
    <h2>Cliente: {{ clientName }} </h2>
    <h2>Tabela: {{ tableName }} </h2>
    <h2>MINUTO: {{minuto}} | HORA: {{hora}} | DIA: {{ dia }} | MES: {{ mes }} | ANO: {{ ano }}</h2>
    <div>
      <p><b>OK:</b> {{ okCount }}</p>
      <p><b>ERROR:</b> {{ errorCount }}</p>
    </div>

    <table>
      <thead>
        <tr>  
          <th>Data Busca</th>
          <th>minuto</th>
          <th>oid</th>
          <th>OidPostgres</th>
          <th>OidBigquery</th>
          <th>Status</th>
          <th>Deletado</th>
          <th>Data deletado</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="volumetry in volumetries" :key="volumetry.tabela">
          <td>{{ volumetry.dataBusca | formatDate }}</td>
          <td>{{ volumetry.minuto }}</td>
          <td>{{ volumetry.oid }}</td>
          <td>{{ volumetry.postgres }}</td>
          <td>{{ volumetry.bigquery }}</td>
          <td>{{ volumetry.postgres == volumetry.bigquery ? "OK" : "ERROR" }}</td>
          <td>{{ volumetry.deletado }}</td>
          <td>{{ volumetry.dataDeletado | formatDate }}</td>
          <td>
            <button v-if="volumetry.postgres != volumetry.bigquery" @click="sendVolumetry(volumetry)">Deletar</button>
          </td>
        </tr>
      </tbody>
    </table>
    
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'VolumetryTableDetailsMesDiaHoraMinutosRows',
  props: ['clientName','tableName','ano','mes','dia','hora','minuto'],
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
        const response = await axios.get(`http://localhost:9999/volumetries/${this.clientName}/${this.tableName}/${this.ano}/${this.mes}/${this.dia}/${this.hora}/${this.minuto}`);
        this.volumetries = response.data;
      } catch (error) {
        console.error('Erro ao obter os dados:', error);
      }
    },
    async sendVolumetry(volumetry) {
    try {
      const response = await axios.post('http://localhost:9999/volumetries/deletar', volumetry);
      this.volumetries = response.data;
    } catch (error) {
      console.error('Erro ao enviar os dados:', error);
    }
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
