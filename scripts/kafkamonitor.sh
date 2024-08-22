#!/bin/bash

# Nome do arquivo jar
JAR_NAME="connect-0.0.1-SNAPSHOT.jar"

# Caminho completo do arquivo jar
JAR_PATH="/var/lib/kafka-monitor/$JAR_NAME"

# Nome do serviço
SERVICE_NAME="kafka-monitor"

# PID do processo
PID_PATH_NAME="/tmp/$SERVICE_NAME-pid"

# Comando Java para rodar o jar
JAVA_CMD="sudo java -jar $JAR_PATH"

start() {
    if [ -f $PID_PATH_NAME ]; then
        echo "$SERVICE_NAME já está rodando."
    else
        echo "Iniciando $SERVICE_NAME..."
        nohup $JAVA_CMD >> /dev/null 2>&1 &
        echo $! > $PID_PATH_NAME
        echo "$SERVICE_NAME iniciado com sucesso."
    fi
}

stop() {
    if [ -f $PID_PATH_NAME ]; then
        PID=$(cat $PID_PATH_NAME)
        echo "Parando $SERVICE_NAME..."
        kill $PID
        echo "$SERVICE_NAME parado com sucesso."
        rm $PID_PATH_NAME
    else
        echo "$SERVICE_NAME não está rodando."
    fi
}

case $1 in
    start)
        start
    ;;
    stop)
        stop
    ;;
    restart)
        stop
        start
    ;;
    *)
        echo "Uso: $0 {start|stop|restart}"
    ;;
esac
