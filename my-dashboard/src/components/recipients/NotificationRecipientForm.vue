<template>
    <v-card>
      <v-card-title>
        <span class="text-h5">{{ recipient ? 'Editar' : 'Adicionar' }} destinatário</span>
      </v-card-title>
  
      <v-card-text>
        <v-form ref="form">
          <v-text-field v-model="form.name" label="Name" required></v-text-field>
          <v-text-field v-model="form.email" label="Email" required></v-text-field>
          <v-checkbox v-model="form.isActive" label="Active"></v-checkbox>
        </v-form>
      </v-card-text>
  
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="blue darken-1" text @click="$emit('close')">Cancelar</v-btn>
        <v-btn color="blue darken-1" text @click="saveRecipient">Salvar</v-btn>
      </v-card-actions>
    </v-card>
  </template>
  
  <script>
  
  export default {
    props: {
      recipient: Object
    },
    data() {
      return {
        form: {
          id: null,
          name: '',
          email: '',
          severityLevel: '',
          isActive: true
        },
        severityLevels: ['INFO', 'WARN', 'ERROR']
      };
    },
    watch: {
      recipient: {
        handler(newValue) {
          if (newValue) {
            this.form = { ...newValue };
          } else {
            this.resetForm();
          }
        },
        immediate: true
      }
    },
    methods: {
      resetForm() {
        this.form = {
          id: null,
          name: '',
          email: '',
          severityLevel: '',
          isActive: true
        };
      },
      saveRecipient() {
        if (this.$refs.form.validate()) {
          const request = this.form.id
            ? this.$api.put(`/recipients/${this.form.id}`, this.form)
            : this.$api.post('/recipients', this.form);
  
          request.then(() => {
            this.$emit('save');
            this.$emit('close');
          });
        }
      }
    }
  };
  </script>
  