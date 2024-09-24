<template>
    <v-container>
        <v-row>
            <v-col>
                <v-text-field v-model="filters.entityName" label="Entity Name" @input="fetchData"></v-text-field>
            </v-col>

            <v-col>
                <v-text-field v-model="filters.newValue" label="New Value (LIKE)" @input="fetchData"></v-text-field>
            </v-col>

            <v-col>
                <v-menu ref="menu" v-model="menu" :close-on-content-click="false" transition="scale-transition" offset-y
                    min-width="auto">
                    <template v-slot:activator="{ on, attrs }">
                        <v-text-field v-model="filters.startDate" label="Start Date" readonly v-bind="attrs"
                            v-on="on"></v-text-field>
                    </template>
                    <v-date-picker v-model="filters.startDate" @input="fetchData"></v-date-picker>
                </v-menu>
            </v-col>

            <v-col>
                <v-menu ref="menu" v-model="menuEnd" :close-on-content-click="false" transition="scale-transition"
                    offset-y min-width="auto">
                    <template v-slot:activator="{ on, attrs }">
                        <v-text-field v-model="filters.endDate" label="End Date" readonly v-bind="attrs"
                            v-on="on"></v-text-field>
                    </template>
                    <v-date-picker v-model="filters.endDate" @input="fetchData"></v-date-picker>
                </v-menu>
            </v-col>

        </v-row>

        <v-data-table :headers="headers" :items="items" :server-items-length="totalItems" :loading="loading"
            :items-per-page.sync="itemsPerPage" :page.sync="page" @update:page="fetchData"
            @update:items-per-page="fetchData">

            <template v-slot:top>
                <v-toolbar flat>
                    <v-toolbar-title>Audit Logs</v-toolbar-title>
                    <v-spacer></v-spacer>
                </v-toolbar>
            </template>

            <template v-slot="{ item }">
                <v-tooltip bottom>
                    <template v-slot:activator="{ on, attrs }">
                        <span class="truncate-text" v-bind="attrs" v-on="on">
                            {{ item.newValue }}
                        </span>
                    </template>
                    <span>{{ item.newValue }}</span>
                </v-tooltip>
            </template>

        </v-data-table>
    </v-container>
</template>

<script>
export default {
    data() {
        return {
            filters: {
                entityName: 'ConnectorVolumetryEntity',
                newValue: '',
                startDate: null,
                endDate: null
            },
            headers: [
                { text: 'Entity Name', value: 'entityName' },
                { text: 'Operation Type', value: 'operationType' },
                { text: 'Changed At', value: 'changedAt' },
                { text: 'New Value', value: 'newValue' }
                
            ],
            items: [],
            totalItems: 0,
            loading: false,
            itemsPerPage: 10,
            page: 1
        };
    },
    methods: {
        async fetchData() {
            this.loading = true;
            const params = {
                entityName: this.filters.entityName,
                newValue: this.filters.newValue,
                startDate: this.filters.startDate,
                endDate: this.filters.endDate,
                page: this.page - 1,
                size: this.itemsPerPage
            };

            try {
                const response = await this.$api.get('/audit-logs', { params });
                this.items = response.data.content;
                this.totalItems = response.data.totalElements;
            } catch (error) {
                console.error('Error fetching data:', error);
            } finally {
                this.loading = false;
            }
        }
    },
    watch: {
        page() {
            this.fetchData();
        },
        itemsPerPage() {
            this.fetchData();
        }
    },
    mounted() {
        this.fetchData();
    }
};
</script>

<style>
.truncate-text {
  display: inline-block;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 200px; /* Ajuste a largura conforme necessário */
}
</style>