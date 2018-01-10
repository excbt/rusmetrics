package ru.excbt.datafuse.nmk.service;

import java.io.Serializable;

public class ConsumptionTask implements Serializable {

    public static final String CONS_TASK_QUEUE = "CONS_TASK_QUEUE";

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ConsumptionTask name(String arg) {
        this.name = arg;
        return this;
    }

    @Override
    public String toString() {
        return "ConsumptionTask{" +
            "name='" + name + '\'' +
            '}';
    }
}
