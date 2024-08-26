<template>
  <div>
    <h1>Connectores monitorados de {{ clientName }}</h1>

    <v-data-table
      :headers="headers"
      :items="connectors"
      :items-per-page="connectors.length"  
      :disable-pagination="false"
      class="elevation-1"
       
    >
      
      <template v-slot:[`item.dataUltimoStatus`]="{ item }">
        {{ item.dataUltimoStatus | formatDate }}
      </template>
      <template v-slot:[`item.actions`]="{ item }">
        <v-btn icon @click="viewDetails(item.id)">
          <v-icon>mdi-magnify</v-icon>
        </v-btn>
        <v-btn icon @click="viewStatus(item.id)">
          <v-icon>mdi-eye</v-icon>
        </v-btn>
      </template>
    </v-data-table>
  </div>
</template>

<script>

export default {
  name: 'ConnectorList',
  props: ['clientName'],
  data() {
    return {
      connectors: [],
      headers: [
        { text: 'ID', value: 'id' },
        { text: 'Name', value: 'name' },
        { text: 'Client Name', value: 'nomeCliente' },
        { text: 'Type', value: 'type' },
        { text: 'Status Conector', value: 'ultimoStatusConector' },
        { text: 'Status Tasks', value: 'ultimoStatusTask1' },
        { text: 'Data Ultimo Status', value: 'dataUltimoStatus' },
        { text: 'Ações', value: 'actions', sortable: false }
      ]
    };
  },
  mounted() {
    this.fetchConnectors();
  },
  methods: {
    async fetchConnectors() {
      try {
        const response = await this.$api.get(`/connectors/cliente/${this.clientName}`);
        this.connectors = response.data;
      } catch (error) {
        console.error('Error fetching connectors:', error);
      }
    },
    viewDetails(id) {
      this.$router.push({ name: 'ConnectorDetails', params: { id } });
    },
    viewStatus(id) {
      this.$router.push({ name: 'ConnectorStatusList', params: { id } });
    }
  }
};
</script>

<style scoped>
/* Personalize a aparência conforme necessário */
button {
  margin-right: 5px;
}
</style>
