<template>
  <v-container>
    <v-row>
      <v-col cols="12" md="6">
        <v-card>
          <v-card-title>
            Editar Serviço
          </v-card-title>
          <v-card-text>
            <v-form ref="form" v-model="valid">
              <v-text-field label="Nome do Serviço" v-model="service.serviceName" :rules="[rules.required]"
                required></v-text-field>

              <v-text-field label="Expressão Cron" v-model="service.cronExpression" :rules="[rules.required]"
                required></v-text-field>
              <v-checkbox v-model="service.active" label="Ativo"></v-checkbox>
              <v-textarea label="Descrição" v-model="service.description" :rules="[rules.required]"
                required></v-textarea>
            </v-form>
          </v-card-text>
          <v-card-actions>
            <v-btn color="primary" @click="saveService">Salvar</v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
    <!-- Snackbar para exibir mensagens de erro -->
    <v-snackbar v-model="snackbar.show" :timeout="snackbar.timeout" color="error">
      {{ snackbar.text }}
      <v-btn color="white" text @click="snackbar.show = false">Fechar</v-btn>
    </v-snackbar>
  </v-container>
</template>


<script>

export default {
  name: 'ScheduledTaskEdit',
  props: ['serviceName'],
  data() {
    return {
      valid: false,
      service: {
        serviceName: '',
        cronExpression: '',
        description: ''
      },
      snackbar: {
        show: false,
        text: '',
        timeout: 3000
      },
      rules: {
        required: value => !!value || 'Campo obrigatório.',
      }
    };
  },
  methods: {
    async fetchServices() {
      try {
        const response = await this.$api.get(`/scheduled-tasks/${this.serviceName}`);
        this.service = response.data;
      } catch (error) {
        this.showError('Erro ao buscar serviços', error);
      }
    },
    async saveService() {
      if (this.$refs.form.validate()) {
        try {
          if (this.service.id) {
            // Editar serviço existente
            await this.$api.put(`/scheduled-tasks/${this.service.id}`, this.service);
          } else {
            // Criar novo serviço
            await this.$api.post('/scheduled-tasks', this.service);
          }

          this.$router.push({ name: 'ScheduledTaskIndex' });

        } catch (error) {
          this.showError('Erro ao salvar serviço', error);
        }
      }
    },
    showError(title, error) {
      let message = title;

      if (error.response && error.response.data) {
        message += ': ' + error.response.data;
      } else if (error.message) {
        message += ': ' + error.message;
      } else {
        message += ': Ocorreu um erro desconhecido.';
      }

      // Exibir mensagem no snackbar
      this.snackbar.text = message;
      this.snackbar.show = true;
    },
    clearForm() {
      this.service = {
        serviceName: '',
        cronExpression: '',
        description: ''
      };
      this.$refs.form.reset();
    }
  },
  mounted() {
    this.fetchServices();
  }
};

</script>