package org.migrationtool.models;

import java.util.ArrayList;
import java.util.List;

public class Column {
    private String name;
    private String tableName;
    private String type;
    private String newDataType;
    private String newName;
    private final List<Constraint> constraintsList = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Column:").append(name).append("->")
                .append(newDataType.isEmpty() ? "" : newDataType).append(newName.isEmpty() ? "" : "|")
                .append(newName.isEmpty() ? "" : newName).append(newName.isEmpty() ? "" : "|");
        constraintsList.forEach(constraint -> stringBuilder.append(constraint).append(" "));
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public List<Constraint> getConstraintsList() {
        return constraintsList;
    }

    public String getNewDataType() {
        return newDataType;
    }
    public void setNewDataType(String newDataType) {
        this.newDataType = newDataType;
    }

    public String getNewName() {
        return newName;
    }
    public void setNewName(String newName) {
        this.newName = newName;
    }
}
