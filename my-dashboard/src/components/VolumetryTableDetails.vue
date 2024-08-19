<template>
  <div>
    <v-card>
    <h2>Cliente: {{ clientName }} </h2>
    <h2>Tabela: {{ tableName }} </h2>
    <div>
      <p><b>OK:</b> {{ okCount }}</p>
      <p><b>ERROR:</b> {{ errorCount }}</p>
    </div>
    </v-card>

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
      <template v-slot:[`item.ano`]="{ item }">
        {{ item.ano }}
      </template>
      <template v-slot:[`item.mes`]="{ item }">
        {{ item.mes }}
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
        <v-btn @click="viewDetails(clientName, tableName, item.ano, item.mes)">Detalhes</v-btn>
      </template>
    </v-data-table>
  </div>
</template>

<script>

export default {
  name: 'VolumetryTableDetails',
  props: ['clientName', 'tableName'],
  data() {
    return {
      volumetries: [],
      headers: [
        { text: 'Data', value: 'dataBusca' },
        { text: 'Ano', value: 'ano' },
        { text: 'Mês', value: 'mes' },
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
        const response = await this.$api.get(`/volumetries/${this.clientName}/${this.tableName}`);
        this.volumetries = response.data;
      } catch (error) {
        console.error('Erro ao obter os dados:', error);
      }
    },
    viewDetails(clientName, tableName, ano, mes) {
      this.$router.push({ name: 'VolumetryTableDetailsMesDia', params: { clientName, tableName, ano, mes } });
    },
  }
};
</script>

