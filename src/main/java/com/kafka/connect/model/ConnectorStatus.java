package com.kafka.connect.model;

import java.util.List;
import lombok.Data;

@Data
public class ConnectorStatus
{
    private String name;

    private String type;

    private Connector connector;

    private List<Task> tasks;

    @Data
    public static class Connector
    {
        private String state;

        private String worker_id;
    }

    @Data
    public static class Task
    {
        private int id;

        private String state;

        private String worker_id;
    }
}
