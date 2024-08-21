<template>
  <div>
    <h1>Volumetrias para {{ clientName }}</h1>

    <div>
      <p><b>OK:</b> {{ okCount }}</p>
      <p><b>ERROR:</b> {{ errorCount }}</p>
    </div>

    <v-data-table
      :headers="headers"
      :items="volumetries"
      :disable-pagination="true"
      class="elevation-1"
      fixed-header
    >
      <template v-slot:[`item.dataBusca`]="{ item }">
        {{ item.dataBusca | formatDate }}
      </template>
      
      <template v-slot:[`item.actions`]="{ item }">
        <v-btn @click="viewDetails(clientName, item.tabela)">Detalhes</v-btn>
        <v-btn @click="openPopup(item)">Ver queries</v-btn>
      </template>
    </v-data-table>

    <PopupQueries v-if="showPopup" :volumetry="selectedVolumetry" @close="closePopup" />
  </div>
</template>

<script>
import PopupQueries from '@/components/PopUpQueries.vue'; 

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
      headers: [
        { text: 'Nome da Tabela', value: 'tabela' },
        { text: 'Data da Busca', value: 'dataBusca' },
        { text: 'Postgres', value: 'postgres' },
        { text: 'Bigquery', value: 'bigquery' },
        { text: 'Diferença', value: 'difference' },
        { text: 'Status', value: 'status' },
        { text: 'Ações', value: 'actions', sortable: false }
      ],
    };
  },
  computed: {
    okCount() {
      return this.volumetries.filter(volumetry => volumetry.status !== 'ERRO').length;
    },
    errorCount() {
      return this.volumetries.filter(volumetry => volumetry.status === 'ERRO').length;
    }
  },
  mounted() {
    this.fetchVolumetries();
  },
  methods: {
    async fetchVolumetries() {
      try {
        const response = await this.$api.get(`/volumetries/${this.clientName}`);
        this.volumetries = response.data;
      } catch (error) {
        console.error('Error fetching volumetries:', error);
      }
    },
    openPopup(volumetry) {
      this.selectedVolumetry = volumetry;
      this.showPopup = true;
    },
    closePopup() {
      this.showPopup = false;
      this.selectedVolumetry = null;
    },
    viewDetails(clientName, tableName) {
      this.$router.push({ name: 'VolumetryTableDetails', params: { clientName, tableName } });
    },
  }
};
</script>


