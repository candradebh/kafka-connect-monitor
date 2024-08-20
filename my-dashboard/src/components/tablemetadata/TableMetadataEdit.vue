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
                v-model="table.tableName"
                :rules="[rules.required]"
                required
              ></v-text-field>

              <v-text-field
                label="Nome Da Coluna"
                v-model="table.dateColumnName"
                :rules="[rules.required]"
                required
              ></v-text-field>
              <v-switch
                label="Verificar volumetria"
                v-model="table.volumetryData"
              ></v-switch>
              
            </v-form>
          </v-card-text>
          <v-card-actions>
            <v-btn color="primary" @click="saveTable">Salvar</v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  name: 'TableMetadataEdit',
  props: ['id'],
  data() {
    return {
      valid: false,
      table: {
        tableName: '',
        dateColumnName: '',
        volumetryData: false
      },
      rules: {
        required: value => !!value || 'Campo obrigatório.',
      }
    };
  },
  methods: {
    async fetchTable() {
      if (this.id) {
        try {
          const response = await this.$api.get(`/tablemetadata/${this.id}`);
          this.table = response.data;
        } catch (error) {
          console.error('Erro ao buscar a tabela:', error);
        }
      }
    },
    async saveTable() {
      if (this.$refs.form.validate()) {
        try {
          if (this.id) {
            await this.$api.put(`/tablemetadata/${this.id}`, this.table);
          } else {
            await this.$api.post('/tablemetadata', this.table);
          }
          this.$router.push({ name: 'TableMetadataIndex' });
        } catch (error) {
          console.error('Erro ao salvar a tabela:', error);
        }
      }
    },
    clearForm() {
      this.table = {
        tableName: '',
        dateColumnName: '',
        volumetryData: false
      };
      this.$refs.form.reset();
    }
  },
  mounted() {
    this.fetchTable();
  }
};
</script>
