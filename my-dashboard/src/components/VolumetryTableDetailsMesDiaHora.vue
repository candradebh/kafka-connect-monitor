<template>
  <div>
    <h2>Cliente: {{ clientName }} </h2>
    <h2>Tabela: {{ tableName }} </h2>
    <h2>DIA: {{ dia }} | MÊS: {{ mes }} | ANO: {{ ano }}</h2>
    
    <div>
      <p><b>OK:</b> {{ okCount }}</p>
      <p><b>ERROR:</b> {{ errorCount }}</p>
    </div>

    <v-data-table
      fixed-header
      :headers="headers"
      :items="volumetries"
      class="elevation-1"
      :items-per-page="volumetries.length"  
      :disable-pagination="true"  
    >
      <template v-slot:[`item.dataBusca`]="{ item }">
        {{ item.dataBusca | formatDate }}
      </template>
      <template v-slot:[`item.hora`]="{ item }">
        {{ item.hora }}
      </template>
      <template v-slot:[`item.totalRecordsPostgres`]="{ item }">
        {{ item.totalRecordsPostgres }}
      </template>
      <template v-slot:[`item.totalRecordsBigquery`]="{ item }">
        {{ item.totalRecordsBigquery }}
      </template>
      <template v-slot:[`item.status`]="{ item }">
        {{ item.totalRecordsPostgres === item.totalRecordsBigquery ? 'OK' : 'ERROR' }}
      </template>
      <template v-slot:[`item.actions`]="{ item }">
        <v-btn @click="viewDetails(clientName, tableName, item.ano, item.mes, item.dia, item.hora)">Detalhes</v-btn>
      </template>
    </v-data-table>
    
  </div>
</template>

<script>

export default {
  name: 'VolumetryTableDetailsMesDiaHora',
  props: ['clientName', 'tableName', 'ano', 'mes', 'dia'],
  data() {
    return {
      volumetries: [],
      headers: [
        { text: 'Data Busca', value: 'dataBusca' },
        { text: 'Hora', value: 'hora' },
        { text: 'Postgres', value: 'totalRecordsPostgres' },
        { text: 'Bigquery', value: 'totalRecordsBigquery' },
        { text: 'Status', value: 'status' },
        { text: 'Ações', value: 'actions', sortable: false }
      ]
    };
  },
  computed: {
    okCount() {
      return this.volumetries.filter(volumetry => volumetry.totalRecordsPostgres === volumetry.totalRecordsBigquery).length;
    },
    errorCount() {
      return this.volumetries.filter(volumetry => volumetry.totalRecordsPostgres !== volumetry.totalRecordsBigquery).length;
    }
  },
  mounted() {
    this.fetchVolumetries();
  },
  methods: {
    async fetchVolumetries() {
      try {
        const response = await this.$api.get(`/volumetries/${this.clientName}/${this.tableName}/${this.ano}/${this.mes}/${this.dia}`);
        this.volumetries = response.data;
      } catch (error) {
        console.error('Erro ao obter os dados:', error);
      }
    },
    viewDetails(clientName, tableName, ano, mes, dia, hora) {
      this.$router.push({ name: 'VolumetryTableDetailsMesDiaHoraMinutos', params: { clientName, tableName, ano, mes, dia, hora } });
    },
  }
};
</script>


