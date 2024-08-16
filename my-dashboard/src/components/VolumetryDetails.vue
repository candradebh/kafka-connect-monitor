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
          <th>Status</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="volumetry in volumetries" :key="volumetry.tabela">
          <td>{{ volumetry.tabela }}</td>
          <td>{{ volumetry.dataBusca | formatDate }}</td>
          <td>{{ volumetry.postgres }}</td>
          <td>{{ volumetry.bigquery }}</td>
          <td>{{ volumetry.postgres == volumetry.bigquery ? "OK" : "ERROR" }}</td>
          <td>
            <button @click="viewDetails(clientName,volumetry.tabela)">Detalhes</button>
            <button @click="openPopup(volumetry)">Ver queries</button>
          </td>
        </tr>
      </tbody>
    </table>

    <PopupQueries v-if="showPopup" :volumetry="selectedVolumetry" @close="showPopup = false" />
  </div>
</template>

<script>
import axios from 'axios';
import PopupQueries from './PopUpQueries.vue'; // Importe o componente PopupQueries

export default {
  name: 'VolumetryDetails',
  props: ['clientName'],
  components: {
    PopupQueries, // Declare o componente PopupQueries
  },
  data() {
    return {
      volumetries: [],
      showPopup: false,
      selectedVolumetry: null,
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
    },
    openPopup(volumetry) {
      console.log('Popup is being opened for:', volumetry);
      this.selectedVolumetry = volumetry;
      this.showPopup = true;
    },
    closePopup() {
      console.log('Popup is being closed');
      this.showPopup = false;
      this.selectedVolumetry = null;
    },
    viewDetails(clientName, tableName) {
      this.$router.push({ name: 'VolumetryTableDetails', params: { clientName, tableName } });
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
