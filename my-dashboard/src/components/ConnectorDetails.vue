<template>
<div v-if="connector.connectorConfig">
  <h1>Detalhes do Conector: {{ connector.connectorConfig.name }}</h1>
  <table>
    <thead>
      <tr>
        <th>Parâmetro</th>
        <th>Nome da Propriedade</th>
        <th>Valor Padrão</th>
        <th>Valor</th>
        <th>Tipo</th>
        <th>Descrição</th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td>ID</td>
        <td>connector.connectorConfig.id</td>
        <td></td>
        <td>{{ connector.connectorConfig.id }}</td>
        <td></td>
        <td></td>
      </tr>
      <tr>
        <td>{{ getParameter("name").title }}</td>
        <td>{{ getParameter("name").name }}</td>
        <td>{{ getParameter("name").defaultValue }}</td>
        <td>{{ connector.connectorConfig.name }}</td>
        <td>{{ getParameter('name').type }}</td>
        <td>{{ getParameter("name").description }}</td>
      </tr>
      <tr>
        <td>Nome do cliente</td>
        <td>nome.cliente</td>
        <td></td>
        <td>{{ connector.connectorConfig.nomeCliente }}</td>
        <td>all</td>
        <td>Parâmetro personalizado</td>
      </tr>
      <tr>
        <td>{{ getParameter("connector.class").title }}</td>
        <td>{{ getParameter("connector.class").name }}</td>
        <td>{{ getParameter("connector.class").defaultValue }}</td>
        <td>{{ connector.connectorConfig.classname }}</td>
        <td>{{ getParameter('connector.class').type }}</td>
        <td>{{ getParameter("connector.class").description }}</td>
      </tr>
      <tr>
        <td>Tipo do Conector</td>
        <td>type</td>
        <td></td>
        <td>{{ connector.connectorConfig.type }}</td>
        <td>all</td>
        <td>Parâmetro personalizado</td>
      </tr>
      <tr v-if="getParameter('database.user').type == 'all' || getParameter('database.user').type == connector.connectorConfig.type">
        <td>{{ getParameter("database.user").title }}</td>
        <td>{{ getParameter("database.user").name }}</td>
        <td>{{ getParameter("database.user").defaultValue }}</td>
        <td>{{ connector.connectorConfig.usuario }}</td>
        <td>{{ getParameter('database.user').type }}</td>
        <td>{{ getParameter("database.user").description }}</td>
      </tr>
      <tr v-if="getParameter('database.password').type == connector.connectorConfig.type">
        <td>{{ getParameter("database.password").title }}</td>
        <td>{{ getParameter("database.password").name }}</td>
        <td>{{ getParameter("database.password").defaultValue }}</td>
        <td>{{ connector.connectorConfig.senha }}</td>
        <td>{{ getParameter('database.password').type }}</td>
        <td>{{ getParameter("database.password").description }}</td>
      </tr>
      <tr v-if="getParameter('database.hostname').type == connector.connectorConfig.type">
        <td>{{ getParameter("database.hostname").title }}</td>
        <td>{{ getParameter("database.hostname").name }}</td>
        <td>{{ getParameter("database.hostname").defaultValue }}</td>
        <td>{{ connector.connectorConfig.host }}</td>
        <td>{{ getParameter('database.hostname').type }}</td>
        <td>{{ getParameter("database.hostname").description }}</td>
      </tr>
      <tr v-if="getParameter('database.port').type == connector.connectorConfig.type">
        <td>{{ getParameter("database.port").title }}</td>
        <td>{{ getParameter("database.port").name }}</td>
        <td>{{ getParameter("database.port").defaultValue }}</td>
        <td>{{ connector.connectorConfig.port }}</td>
        <td>{{ getParameter('database.port').type }}</td>
        <td>{{ getParameter("database.port").description }}</td>
      </tr>
      <tr v-if="getParameter('database.dbname').type == connector.connectorConfig.type">
        <td>{{ getParameter("database.dbname").title }}</td>
        <td>{{ getParameter("database.dbname").name }}</td>
        <td>{{ getParameter("database.dbname").defaultValue }}</td>
        <td>{{ connector.connectorConfig.database }}</td>
        <td>{{ getParameter('database.dbname').type }}</td>
        <td>{{ getParameter("database.dbname").description }}</td>
      </tr>
      <tr v-if="getParameter('defaultDataset').type == connector.connectorConfig.type">
        <td>{{ getParameter("defaultDataset").title }}</td>
        <td>{{ getParameter("defaultDataset").name }}</td>
        <td>{{ getParameter("defaultDataset").defaultValue }}</td>
        <td>{{ connector.connectorConfig.defaultDataset }}</td>
        <td>{{ getParameter('defaultDataset').type }}</td>
        <td>{{ getParameter("defaultDataset").description }}</td>
      </tr>
      <tr v-if="getParameter('projectBigquery').type == connector.connectorConfig.type">
        <td>{{ getParameter("projectBigquery").title }}</td>
        <td>{{ getParameter("projectBigquery").name }}</td>
        <td>{{ getParameter("projectBigquery").defaultValue }}</td>
        <td>{{ connector.connectorConfig.projectBigquery }}</td>
        <td>{{ getParameter('projectBigquery').type }}</td>
        <td>{{ getParameter("projectBigquery").description }}</td>
      </tr>
      <tr v-if="getParameter('mergeIntervalMs').type == connector.connectorConfig.type">
        <td>{{ getParameter("mergeIntervalMs").title }}</td>
        <td>{{ getParameter("mergeIntervalMs").name }}</td>
        <td>{{ getParameter("mergeIntervalMs").defaultValue }}</td>
        <td>{{ connector.connectorConfig.mergeIntervalMs }}</td>
        <td>{{ getParameter('mergeIntervalMs').type }}</td>
        <td>{{ getParameter("mergeIntervalMs").description }}</td>
      </tr>
      <tr v-if="getParameter('table.include.list').type == connector.connectorConfig.type">
        <td>{{ getParameter("table.include.list").title }}</td>
        <td>{{ getParameter("table.include.list").name }}</td>
        <td>{{ getParameter("table.include.list").defaultValue }}</td>
        <td>{{ connector.connectorConfig.tableIncludeList }}</td>
        <td>{{ getParameter('table.include.list').type }}</td>
        <td>{{ getParameter("table.include.list").description }}</td>
      </tr>
    </tbody>
  </table>
</div>

</template>

<script>

export default {
  name: 'ConnectorDetails',
  props: ['id'],
  data() {
    return {
      connector: null
    };
  },
  watch: {
    id: 'fetchConnectorDetails'
  },
  mounted() {
    this.fetchConnectorDetails();
  },
  methods: {
    async fetchConnectorDetails() {
      try {
        const response = await this.$api.get(`/connectors/${this.id}`);
        this.connector = response.data;
      } catch (error) {
        console.error('Error fetching connector details:', error);
      }
    },
    getParameter(propertyName) {
      const parameter = this.connector.parameters.find(param => param.name === propertyName);
      return parameter ? parameter : 'N/A';
    }
  }
};
</script>

<style scoped>
table {
  width: 100%;
  border-collapse: collapse;
}
th, td {
  padding: 10px;
  border: 1px solid #ccc;
  text-align: left;
}
th {
  background-color: #f4f4f4;
}
</style>
