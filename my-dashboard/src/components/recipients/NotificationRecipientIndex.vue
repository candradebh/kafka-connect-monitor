<template>
    <v-container>
        <v-data-table :headers="headers" :items="recipients" class="elevation-1">
            <template v-slot:top>
                <v-toolbar flat>
                    <v-toolbar-title>Destinatarios</v-toolbar-title>
                    <v-spacer></v-spacer>
                    <v-btn color="primary" @click="openForm(null)">Criar</v-btn>
                </v-toolbar>
            </template>

            <template v-slot:[`item.actions`]="{ item }">
                <v-icon small @click="openForm(item)">mdi-pencil</v-icon>
                <v-icon small @click="confirmDelete(item)">mdi-delete</v-icon>
            </template>
        </v-data-table>

        <!-- Form Dialog -->
        <v-dialog v-model="dialogForm" max-width="500px">
            <NotificationRecipientForm 
                :recipient="selectedRecipient" 
                @close="dialogForm = false"
                @save="fetchRecipients" 
            />
        </v-dialog>

        <!-- Delete Confirmation Dialog -->
        <v-dialog v-model="dialogDelete" max-width="400px">
            <v-card>
                <v-card-title class="text-h5">Atenção</v-card-title>
                <v-card-text>Deseja deletar esse registro?</v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="green darken-1" text @click="deleteRecipient">Sim</v-btn>
                    <v-btn color="red darken-1" text @click="dialogDelete = false">Não</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-container>
</template>

<script>
import NotificationRecipientForm from './NotificationRecipientForm.vue';

export default {
    components: { NotificationRecipientForm },
    data() {
        return {
            recipients: [],
            selectedRecipient: null,
            dialogForm: false,
            dialogDelete: false,
            headers: [
                { text: 'Name', value: 'name' },
                { text: 'Email', value: 'email' },
                { text: 'Active', value: 'isActive' },
                { text: 'Actions', value: 'actions', sortable: false }
            ]
        };
    },
    methods: {
        fetchRecipients() {
            this.$api.get('/recipients').then(response => {
                this.recipients = response.data;
            });
        },
        openForm(recipient) {
            this.selectedRecipient = recipient;
            this.dialogForm = true;
        },
        confirmDelete(recipient) {
            this.selectedRecipient = recipient;
            this.dialogDelete = true;
        },
        deleteRecipient() {
            this.$api.delete(`/recipients/${this.selectedRecipient.id}`).then(() => {
                this.fetchRecipients();
                this.dialogDelete = false;
            });
        }
    },
    created() {
        this.fetchRecipients();
    }
};
</script>
