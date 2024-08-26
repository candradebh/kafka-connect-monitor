<template>
  <div>
    <h2>Configuração de Tabelas</h2>
    <div class="d-flex justify-end align-center mb-4">
      <v-btn color="primary" @click="createTable">
        <v-icon left>mdi-plus</v-icon>
        Criar
      </v-btn>
    </div>
    
    <v-data-table
      :headers="headers"
      :items="tables"
      :disable-pagination="true"
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
  name: 'TableMetadataIndex',
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
      // Navega para a página de edição
      this.$router.push({ name: 'TableMetadataEdit', params: { id: table.id } });
    },
    createTable() {
      // Navega para a página de criação, sem ID
      this.$router.push({ name: 'TableMetadataEdit' });
    }
  }
};
</script>
