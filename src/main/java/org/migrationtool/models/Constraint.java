package org.migrationtool.models;

public class Constraint {
    private String name;
    private String tableName;
    private String columnName;
    private String expression;
    private ConstraintType type;

    @Override
    public String toString() {
        return switch (type) {
            case AUTO_INCREMENT -> "AUTO_INCREMENT";
            case NOT_NULL -> "NOT NULL";
            case CHECK -> String.format("%s CHECK (%s)", name, expression);
            case FOREIGN_KEY -> String.format("%s FOREIGN KEY (%s) REFERENCES %s", name, columnName, expression);
            case UNIQUE -> String.format("%s UNIQUE (%s)", name, columnName);
            case PRIMARY_KEY -> String.format("%s PRIMARY KEY (%s)", name, columnName);
        };
    }

    public boolean isNamed() {
        return (type != ConstraintType.AUTO_INCREMENT) && (type != ConstraintType.NOT_NULL);
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

    public String getColumnName() {
        return columnName;
    }
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public ConstraintType getType() {
        return type;
    }
    public void setType(ConstraintType type) {
        this.type = type;
    }
}
