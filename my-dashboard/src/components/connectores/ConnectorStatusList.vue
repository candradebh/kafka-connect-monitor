<template>
    <v-container>
      <v-row>
        <v-col cols="12">
          <v-card>
            <v-card-title>
              Lista de Status dos Conectores
            </v-card-title>
            <v-card-text>
              <v-data-table
                :headers="headers"
                :items="statuses"
                class="elevation-1"
              >
                <template v-slot:[`item.dataBusca`]="{ item }">
                  {{  item.dataBusca | formatDate }}
                </template>
  
                <template v-slot:[`item.connector`]="{ item }">
                  <div>
                    <strong>{{ item.connector.name }}</strong><br />
                    Cliente: {{ item.connector.nomeCliente }}<br />
                    Tipo: {{ item.connector.type }}<br />
                    Projeto BigQuery: {{ item.connector.projectBigquery }}<br />
                  </div>
                </template>
  
                <template v-slot:[`item.tasks`]="{ item }">
                  <div>
                    <div v-for="task in item.tasks" :key="task.id">
                      Task ID: {{ task.taskId }} - State: {{ task.taskState }}<br />
                      Worker ID: {{ task.taskWorkerId }}
                    </div>
                  </div>
                </template>
  
                <template v-slot:[`item.errorReason`]="{ item }">
                  {{ item.errorReason || 'Nenhum erro' }}
                </template>
  
                <template v-slot:[`item.actions`]="{ item }">
                  <v-btn icon @click="viewDetails(item)">
                    <v-icon>mdi-eye</v-icon>
                  </v-btn>
                </template>
              </v-data-table>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </template>
  
  <script>


  export default {
    name: 'ConnectorStatusList',
    props: ['id'],
    data() {
      return {
        headers: [
          { text: 'Data', value: 'dataBusca' },
          { text: 'Status', value: 'connectorState' },
          { text: 'Connector Worker Id', value: 'connectorWorkerId' },
          { text: 'Conector', value: 'connector' },
          { text: 'Tasks', value: 'tasks' },
          { text: 'Erro', value: 'errorReason' },
          { text: 'Ações', value: 'actions', sortable: false },
        ],
        statuses: [],
      };
    },
    methods: {
      async fetchStatuses() {
        try {
          const response = await this.$api.get(`/status/${this.id}`);
          this.statuses = response.data;
        } catch (error) {
          console.error('Erro ao buscar status dos conectores:', error);
        }
      },
      viewDetails(item) {
        // Função para visualizar detalhes do conector
        console.log('Visualizando detalhes do item:', item);
      },
    },
    mounted() {
      this.fetchStatuses();
    },
  };
  </script>
  