<template>
  <div>
    <h2>Configuração de Tabelas</h2>
    <v-data-table
      :headers="headers"
      :items="tables"
      class="elevation-1"
      @click:row="editTable"
    >
    <template v-slot:[`item.volumetryData`]="{ item }">
        <span v-if="item.volumetryData">Sim</span>
        <span v-else>Não</span>
      </template>
    </v-data-table>
  </div>
</template>

<script>

export default {
  name: 'ScheduledTaskIndex',
  data() {
    return {
      headers: [
        { text: 'Tabela', value: 'tableName' },
        { text: 'Coluna Data', value: 'dateColumnName' },
        { text: 'Volumetria', value: 'volumetryData' },
      ],
      tables: []
    };
  },
  created() {
    this.fetchItems();
  },
  methods: {
    fetchItems() {
      this.$api.get('/tablemetadata')
        .then(response => {
          this.tables = response.data;
        })
        .catch(error => {
          console.error('Erro ao obter os dados na api:', error);
        });
    },
    editTable(table) {
      // Navega para a página de edição ou abre um modal de edição
      this.$router.push({ name: 'TableMetadataEdit', params: { id: table.id } });
    }
  }
};
</script>
