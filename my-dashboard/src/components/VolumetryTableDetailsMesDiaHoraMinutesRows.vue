<template>
  <div>
    <h2>Cliente: {{ clientName }} </h2>
    <h2>Tabela: {{ tableName }} </h2>
    <h2>MINUTO: {{ minuto }} | HORA: {{ hora }} | DIA: {{ dia }} | MÊS: {{ mes }} | ANO: {{ ano }}</h2>
    
    <div>
      <p><b>OK:</b> {{ okCount }}</p>
      <p><b>ERROR:</b> {{ errorCount }}</p>
    </div>

    <v-data-table
      :headers="headers"
      :items="volumetries"
      class="elevation-1"
      :items-per-page="volumetries.length" 
      :disable-pagination="true"  
    >
      <template v-slot:[`item.dataBusca`]="{ item }">
        {{ item.dataBusca | formatDate }}
      </template>
      <template v-slot:[`item.minuto`]="{ item }">
        {{ item.minuto }}
      </template>
      <template v-slot:[`item.oid`]="{ item }">
        {{ item.oid }}
      </template>
      <template v-slot:[`item.postgres`]="{ item }">
        {{ item.postgres }}
      </template>
      <template v-slot:[`item.bigquery`]="{ item }">
        {{ item.bigquery }}
      </template>
      <template v-slot:[`item.status`]="{ item }">
        {{ item.postgres === item.bigquery ? 'OK' : 'ERROR' }}
      </template>
      <template v-slot:[`item.deletado`]="{ item }">
        {{ item.deletado }}
      </template>
      <template v-slot:[`item.dataDeletado`]="{ item }">
        {{ item.dataDeletado | formatDate }}
      </template>
      <template v-slot:[`item.actions`]="{ item }">
        <v-btn v-if="item.postgres !== item.bigquery" @click="sendVolumetry(item)">Deletar</v-btn>
      </template>
    </v-data-table>
    
  </div>
</template>

<script>

export default {
  name: 'VolumetryTableDetailsMesDiaHoraMinutosRows',
  props: ['clientName', 'tableName', 'ano', 'mes', 'dia', 'hora', 'minuto'],
  data() {
    return {
      volumetries: [],
      headers: [
        { text: 'Data Busca', value: 'dataBusca' },
        { text: 'Minutos', value: 'minuto' },
        { text: 'OID', value: 'oid' },
        { text: 'OID Postgres', value: 'postgres' },
        { text: 'OID Bigquery', value: 'bigquery' },
        { text: 'Status', value: 'status' },
        { text: 'Deletado', value: 'deletado' },
        { text: 'Data Deletado', value: 'dataDeletado' },
        { text: 'Ações', value: 'actions', sortable: false }
      ]
    };
  },
  computed: {
    okCount() {
      return this.volumetries.filter(volumetry => volumetry.postgres === volumetry.bigquery).length;
    },
    errorCount() {
      return this.volumetries.filter(volumetry => volumetry.postgres !== volumetry.bigquery).length;
    }
  },
  mounted() {
    this.fetchVolumetries();
  },
  methods: {
    async fetchVolumetries() {
      try {
        const response = await this.$api.get(`/volumetries/${this.clientName}/${this.tableName}/${this.ano}/${this.mes}/${this.dia}/${this.hora}/${this.minuto}`);
        this.volumetries = response.data;
      } catch (error) {
        console.error('Erro ao obter os dados:', error);
      }
    },
    async sendVolumetry(volumetry) {
      try {
        const response = await this.$api.post('http://localhost:9999/volumetries/deletar', volumetry);
        this.volumetries = response.data;
      } catch (error) {
        console.error('Erro ao enviar os dados:', error);
      }
    }
  }
};
</script>


