<template>
  <v-container>
    <v-row>
      <v-col cols="12" md="6">
        <v-card>
          <v-card-title>
            {{ id ? 'Editar' : 'Criar Nova' }} Tabela
          </v-card-title>
          <v-card-text>
            <v-form ref="form" v-model="valid">
              <v-text-field
                label="Nome da Tabela"
                v-model="customer.name"
                :rules="[rules.required]"
                required
              ></v-text-field>

              <v-text-field
                label="Nome Da Coluna"
                v-model="customer.description"
                :rules="[rules.required]"
                required
              ></v-text-field>

              <!-- Campo de seleção múltipla para conectores -->
              <v-select
                v-model="customer.connectors"
                :items="connectors"
                item-text="name"
                item-value="id"
                label="Selecione os conectores"
                multiple
                chips
                required
              ></v-select>
              
            </v-form>
          </v-card-text>
          <v-card-actions>
            <v-btn color="primary" @click="saveCustomer">Salvar</v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>


<script>
export default {
  name: 'CustomerEdit',
  props: ['id'],
  data() {
    return {
      valid: false,
      customer: {
        name: '',
        description: '',
        connectors: [] // Armazena os conectores selecionados
      },
      connectors: [], // Lista de conectores que será preenchida com a resposta da API
      rules: {
        required: value => !!value || 'Campo obrigatório.',
      }
    };
  },
  methods: {
    async fetchCustomer() {
      if (this.id) {
        try {
          const response = await this.$api.get(`/customer/${this.id}`);
          this.customer = response.data;
        } catch (error) {
          console.error('Erro ao buscar a tabela:', error);
        }
      }
    },
    async fetchConnectors() {
      try {
        const response = await this.$api.get('/connectors'); // Chamada ao endpoint para buscar conectores
        this.connectors = response.data; // Preenche a lista de conectores
      } catch (error) {
        console.error('Erro ao buscar conectores:', error);
      }
    },
    async saveCustomer() {
      if (this.$refs.form.validate()) {
        try {
          if (this.id) {
            await this.$api.put(`/customer/${this.id}`, this.customer);
          } else {
            await this.$api.post('/customer', this.customer);
          }
          this.$router.push({ name: 'CustomerIndex' });
        } catch (error) {
          console.error('Erro ao salvar a tabela:', error);
        }
      }
    },
    clearForm() {
      this.customer = {
        name: '',
        description: '',
        connectors: []
      };
      this.$refs.form.reset();
    }
  },
  mounted() {
    this.fetchCustomer();
    this.fetchConnectors(); // Chama a função para carregar os conectores quando o componente é montado
  }
};
</script>
