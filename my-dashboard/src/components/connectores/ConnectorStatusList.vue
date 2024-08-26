<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <v-card>
          <v-card-title>
            Lista de Status dos Conectores
          </v-card-title>
          <v-card-text>
            <v-row>
              <!-- Filtro para o connectorState usando v-select -->
              <v-col cols="6">
                <v-select
                  v-model="connectorStateFilter"
                  :items="connectorStates"
                  label="Filtrar por Status"
                  @change="fetchStatuses"
                  clearable
                ></v-select>
              </v-col>

              <!-- Campo de busca -->
              <v-col cols="6">
                <v-text-field
                  v-model="search"
                  label="Buscar"
                  @input="fetchStatuses"
                  clearable
                ></v-text-field>
              </v-col>
            </v-row>

            <v-data-table
              :headers="headers"
              :items="statuses"
              :server-items-length="totalItems"
              :loading="loading"
              :items-per-page="itemsPerPage"
              :page.sync="page"
              :sort-by.sync="sortBy"
              :sort-desc.sync="sortDesc"
              :search="search"
              @update:page="fetchStatuses"
              @update:items-per-page="fetchStatuses"
              @update:sort-by="fetchStatuses"
              @update:sort-desc="fetchStatuses"
              class="elevation-1"
            >
              <template v-slot:[`item.dataBusca`]="{ item }">
                {{ item.dataBusca | formatDate }}
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
      totalItems: 0,
      loading: false,
      itemsPerPage: 10,
      page: 1,
      sortBy: ['dataBusca'],
      sortDesc: [true],
      search: '', // Variável para armazenar o termo de busca
      connectorStateFilter: null,
      connectorStates: ['UNASSIGNED', 'RUNNING', 'PAUSED', 'FAILED'],
    };
  },
  methods: {
    async fetchStatuses() {
      this.loading = true;
      try {
        const params = {
          page: this.page,
          itemsPerPage: this.itemsPerPage,
          sortBy: this.sortBy.length ? this.sortBy[0] : null,
          sortDesc: this.sortDesc.length ? this.sortDesc[0] : null,
          search: this.search, // Passa o termo de busca como parâmetro
          connectorState: this.connectorStateFilter,
        };
        const response = await this.$api.get(`/status/${this.id}`, { params });
        this.statuses = response.data.items;
        this.totalItems = response.data.total;
      } catch (error) {
        console.error('Erro ao buscar status dos conectores:', error);
      } finally {
        this.loading = false;
      }
    },
    viewDetails(item) {
      console.log('Visualizando detalhes do item:', item);
    },
  },
  mounted() {
    this.fetchStatuses();
  },
};
</script>
