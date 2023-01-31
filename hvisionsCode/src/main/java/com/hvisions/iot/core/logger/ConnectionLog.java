package com.hvisions.iot.core.logger;

public class ConnectionLog {
    private String level;
    private String connFailCategory;
    private String message;
    private String connectionName;
    private String equipmentName;
    private String origin;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getConnFailCategory() {
        return connFailCategory;
    }

    public void setConnFailCategory(String connFailCategory) {
        this.connFailCategory = connFailCategory;
    }
}
