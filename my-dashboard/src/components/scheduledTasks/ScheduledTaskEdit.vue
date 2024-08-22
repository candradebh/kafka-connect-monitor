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
                <v-text-field
                  label="Nome do Serviço"
                  v-model="service.serviceName"
                  :rules="[rules.required]"
                  required
                ></v-text-field>
                
                <v-text-field
                  label="Expressão Cron"
                  v-model="service.cronExpression"
                  :rules="[rules.required]"
                  required
                ></v-text-field>
                <v-checkbox v-model="service.active" label="Ativo"></v-checkbox>
                <v-textarea
                  label="Descrição"
                  v-model="service.description"
                  :rules="[rules.required]"
                  required
                ></v-textarea>
              </v-form>
            </v-card-text>
            <v-card-actions>
              <v-btn color="primary" @click="saveService">Salvar</v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
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
        services: [],
        headers: [
          { text: 'Nome do Serviço', value: 'serviceName' },
          { text: 'Expressão Cron', value: 'cronExpression' },
          { text: 'Descrição', value: 'description' },
          { text: 'Ações', value: 'actions', sortable: false }
        ],
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
          //console.log(this.service)
        } catch (error) {
          console.error('Erro ao buscar serviços:', error);
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
            
            this.$router.push({ name: 'ScheduledTaskIndex'});

          } catch (error) {
            console.error('Erro ao salvar serviço:', error);
          }
        }
      },
      editService(service) {
        this.service = { ...service };
      },
      async deleteService(service) {
        try {
          await this.$api.delete(`/api/services/${service.id}`);
          this.fetchServices();
        } catch (error) {
          console.error('Erro ao excluir serviço:', error);
        }
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
  