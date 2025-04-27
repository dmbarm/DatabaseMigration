package org.migrationtool.utils;

public class TagNames {
    private TagNames() {}

    // Base tags
    public static final String MIGRATION = "migration";

    // Action tags
    public static final String CREATE_TABLE = "createTable";
    public static final String ADD_COLUMN = "addColumn";
    public static final String ADD_CONSTRAINT = "addConstraint";
    public static final String ADD_INDEX = "addIndex";
    public static final String RENAME_TABLE = "renameTable";
    public static final String RENAME_COLUMN = "renameColumn";
    public static final String MODIFY_COLUMN_TYPE = "modifyColumnType";
    public static final String DROP_COLUMN = "dropColumn";
    public static final String DROP_TABLE = "dropTable";
    public static final String DROP_CONSTRAINT = "dropConstraint";
    public static final String DROP_INDEX = "dropIndex";
    public static final String ROLLBACK = "rollback";

    // Model tags
    public static final String COLUMN = "column";
    public static final String CONSTRAINT = "constraint";
    public static final String INDEX = "index";
}
