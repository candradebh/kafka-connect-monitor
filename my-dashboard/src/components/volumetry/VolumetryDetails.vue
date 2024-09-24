<template>
  <div>

    <!-- Tabela de Volumetria -->
    <v-data-table
      :headers="headers"
      :items="volumetries"
      :disable-pagination="true"
      class="elevation-1"
      fixed-header
    >
      <template v-slot:top>
        <v-toolbar flat>
          <v-toolbar-title>
            Cliente: {{ clientName }} <br>
            OK:{{ okCount }} | ERROR:{{ errorCount }}
          </v-toolbar-title>
          <v-spacer></v-spacer>
        </v-toolbar>
      </template>
      <!-- Formatação da Data da Busca -->
      <template v-slot:[`item.dataBusca`]="{ item }">
        {{ item.dataBusca | formatDate }}
      </template>
      
      <!-- Ações na linha -->
      <template v-slot:[`item.actions`]="{ item }">
        <v-btn @click="viewDetails(clientName, item.tabela)">Detalhes</v-btn>
        <v-btn @click="openPopup(item)">Ver queries</v-btn>
        <!-- Novo botão para abrir o modal com as 10 últimas volumetrias -->
        <v-btn @click="openVolumetryModal(item)">Ver últimas 10 Volumetrias</v-btn>
      </template>
    </v-data-table>

    <!-- Componente de Popup de Queries -->
    <PopupQueries v-if="showPopup" :volumetry="selectedVolumetry" @close="closePopup" />

    <!-- Modal de últimas 10 volumetrias -->
    <v-dialog v-model="volumetryModal" max-width="600">
      <v-card>
        <v-card-title>
          Últimas 10 Volumetrias para {{ selectedVolumetry?.tabela }}
        </v-card-title>
        <v-card-text>
          <v-data-table :headers="modalHeaders" :items="lastVolumetries" :disable-pagination="true" class="elevation-1">
            <template v-slot:[`item.dataBusca`]="{ item }">
              {{ item.dataBusca | formatDate }}
            </template>
            <template v-slot:[`item.postgres`]="{ item }">
              {{ item.postgres }}
            </template>
            <template v-slot:[`item.bigquery`]="{ item }">
              {{ item.bigquery }}
            </template>
          </v-data-table>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" @click="volumetryModal = false">Fechar</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
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
      lastVolumetries: [], // Para armazenar as últimas 10 volumetrias
      showPopup: false,
      selectedVolumetry: null,
      volumetryModal: false, // Controle do modal das últimas 10 volumetrias
      headers: [
        { text: 'Nome da Tabela', value: 'tabela' },
        { text: 'Data da Busca', value: 'dataBusca' },
        { text: 'Postgres', value: 'postgres' },
        { text: 'Bigquery', value: 'bigquery' },
        { text: 'Diferença', value: 'difference' },
        { text: 'Status', value: 'status' },
        { text: 'Ações', value: 'actions', sortable: false }
      ],
      modalHeaders: [
        { text: 'Data', value: 'dataBusca' },
        { text: 'Postgres', value: 'postgres' },
        { text: 'Bigquery', value: 'bigquery' }
      ]
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
    async openVolumetryModal(volumetry) {
      this.selectedVolumetry = volumetry; // Defina a volumetria selecionada
      try {
        // Buscar as últimas 10 volumetrias
        //const response = await this.$api.get(`/volumetries/${this.clientName}/${volumetry.tabela}/history`);
        this.lastVolumetries = volumetry.volumetryHistory; // Pegue as 10 primeiras
        this.volumetryModal = true; // Abrir o modal
      } catch (error) {
        console.error('Erro ao buscar as últimas volumetrias:', error);
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
