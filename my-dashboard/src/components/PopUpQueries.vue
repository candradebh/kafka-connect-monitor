<template>
    <div class="popup-overlay" @click.self="close">
      <div class="popup-content">
        <h2>Comparação de Volumetria</h2>
        <div class="comparison">
          <div class="left">
            <p><b>Postgres:</b> {{ volumetry.querySource }}</p>
          </div>
          <div class="separator">|</div>
          <div class="right">
            <p><b>BigQuery:</b> {{ volumetry.querySink }}</p>
          </div>
        </div>
        
        <button @click="close">Fechar</button>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'PopupQueries',
    props: {
      volumetry: {
        type: Object,
        required: true
      }
    },
    methods: {
      close() {
        this.$emit('close');
      }
    }
  };
  </script>
  
  <style scoped>
  .popup-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  .popup-content {
    background: white;
    padding: 20px;
    border-radius: 8px;
    width: 400px;
    max-width: 90%;
  }
  
  .comparison {
    display: flex;
    justify-content: space-between;
    margin-bottom: 20px;
  }
  
  .left, .right {
    flex: 1;
    text-align: center;
  }
  
  .separator {
    margin: 0 10px;
    font-weight: bold;
  }
  </style>
  