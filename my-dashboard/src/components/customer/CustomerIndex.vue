<template>
  <div>
    <h2>Cadastro de Clientes </h2>
    <div class="d-flex justify-end align-center mb-4">
      <v-btn color="primary" @click="createTable">
        <v-icon left>mdi-plus</v-icon>
        Criar
      </v-btn>
    </div>
    
    <v-data-table
      :headers="headers"
      :items="customers"
      :disable-pagination="true"
      class="elevation-1"
      @click:row="editTable"
    >
      
    </v-data-table>
  </div>
</template>

<script>

export default {
  name: 'CustomerIndex',
  data() {
    return {
      headers: [
        { text: 'Nome', value: 'name' },
        { text: 'Descrição', value: 'description' },
      ],
      customers: []
    };
  },
  created() {
    this.fetchItems();
  },
  methods: {
    fetchItems() {
      this.$api.get('/customer')
        .then(response => {
          this.customers = response.data;
        })
        .catch(error => {
          console.error('Erro ao obter os dados na api:', error);
        });
    },
    editTable(table) {
      // Navega para a página de edição
      this.$router.push({ name: 'CustomerEdit', params: { id: table.id } });
    },
    createTable() {
      // Navega para a página de criação, sem ID
      this.$router.push({ name: 'CustomerEdit' });
    }
  }
};
</script>
