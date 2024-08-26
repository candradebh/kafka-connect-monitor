<template>
  <v-responsive class="border rounded">
    <v-app>
      <v-layout>
        <v-app-bar app>
          <v-app-bar-nav-icon variant="text" @click.stop="drawer = !drawer"></v-app-bar-nav-icon>
          <v-toolbar-title>Monitor Kafka</v-toolbar-title>

          <v-spacer></v-spacer>

          <template v-if="true">
            <v-btn icon>
              <v-icon>mdi-magnify</v-icon>
            </v-btn>

            <v-btn icon>
              <v-icon>mdi-filter</v-icon>
            </v-btn>
          </template>
        </v-app-bar>

        <v-navigation-drawer v-model="drawer" app>
          <v-list>
            <v-list-item v-for="item in items" :key="item.title" :to="item.path" router link>
              <v-list-item-title>{{ item.title }}</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-navigation-drawer>

        <v-main class="mt-5 ml-4">
          <router-view />
        </v-main>
      </v-layout>
    </v-app>
  </v-responsive>


</template>

<script>

//console.log(process.env.NODE_ENV);

export default {
  name: 'App',
  data: () => ({
    drawer: false,
    items: [
      { title: 'Home', path: '/' },
      { title: 'Conectores', path: '/connectors' },
      { title: 'Volumetria', path: '/volumetry' },
      { title: 'Serviços', path: '/services' },
      { title: 'Tabelas', path: '/tables' },
      { title: 'Destinatários', path: '/recipients' },
      { title: 'Log Notificação', path: '/notificationlogs' },
    ],
  }),
};
</script>
