package org.migrationtool.parsers;

import org.migrationtool.actions.*;
import org.migrationtool.main.Migration;
import org.migrationtool.models.Column;
import org.migrationtool.models.Constraint;
import org.migrationtool.models.ConstraintType;
import org.migrationtool.models.Index;
import org.migrationtool.utils.AttributeNames;
import org.migrationtool.utils.LoggerHelper;
import org.migrationtool.utils.NameGenerator;
import org.migrationtool.utils.TagNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MigrationParser {
    private static final Logger logger = LoggerFactory.getLogger(MigrationParser.class);

    public List<Migration> parseMigrations(String filePath) {
        List<Migration> migrations = new ArrayList<>();

        logger.info("Parsing migrations from file: {}", filePath);

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("https://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("https://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("https://xml.org/sax/features/external-parameter-entities", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(filePath);

            document.getDocumentElement().normalize();

            // Taking each single migration
            NodeList migrationNodes = document.getElementsByTagName(TagNames.MIGRATION);
            logger.debug("Found {} migrations in file", migrationNodes.getLength());

            for (int i = 0; i < migrationNodes.getLength(); i++) {
                logger.info("");
                logger.info(LoggerHelper.SEPARATOR);
                Element migrationElement = (Element) migrationNodes.item(i);
                Migration migration = parseMigration(migrationElement);
                migrations.add(migration);
                logger.info("Parsed migration: id={}, author={}", migration.getId(), migration.getAuthor());
            }

            logger.info("");
        }
        catch (Exception e) {
            logger.error("Failed to parse migrations: {}", e.getMessage(), e);
            return Collections.emptyList();
        }

        logger.info("Parsing migrations completed successfully.");
        return migrations;
    }

    private Migration parseMigration(Element migrationElement) {
        int id = Integer.parseInt(migrationElement.getAttribute(AttributeNames.ID));
        String author = migrationElement.getAttribute(AttributeNames.AUTHOR);
        logger.debug("Parsing migration: ID={}, Author={}", id, author);

        // List for all possible actions
        List<MigrationAction> migrationActions = new ArrayList<>();
        List<MigrationAction> rollbackActions = new ArrayList<>();

        // Going through all actions in single migration
        NodeList actionNodes = migrationElement.getChildNodes();

        for (int j = 0; j < actionNodes.getLength(); j++) {
            Node actionNode = actionNodes.item(j);
            if (actionNode.getNodeType() != Node.ELEMENT_NODE) continue;
            Element actionElement = (Element) actionNode;

            // Parsing rollback info(if exists)
            if (actionElement.getTagName().equals(TagNames.ROLLBACK)) {
                logger.debug(LoggerHelper.CONNECT_INDENT + "   Parsing rollback actions...");
                rollbackActions.addAll(parseActions(actionElement.getChildNodes()));
            }
            else {
                MigrationAction migrationAction = parseAction(actionElement);
                if (migrationAction != null) {
                    migrationActions.add(migrationAction);
                    logger.info(LoggerHelper.REVERSE_INDENT + "   Parsed action: {}", migrationAction.getClass().getSimpleName());
                }
                else {
                    logger.warn("Unknown action: {}", actionElement.getTagName());
                }
            }
        }

        return new Migration(id, author, migrationActions, rollbackActions);
    }

    private MigrationAction parseAction(Element actionElement) {
        String actionType = actionElement.getTagName();
        String tableName = actionElement.getAttribute(AttributeNames.TABLE_NAME);
        logger.debug(LoggerHelper.INDENT + "   Parsing action: Type={}, Table={}", actionType, tableName);

        return switch (actionType) {
            case TagNames.CREATE_TABLE        -> parseCreateTableAction(actionElement, tableName);
            case TagNames.ADD_COLUMN          -> parseAddColumnAction(actionElement, tableName);
            case TagNames.ADD_CONSTRAINT      -> parseAddConstraintAction(actionElement, tableName);
            case TagNames.ADD_INDEX           -> parseAddIndexAction(actionElement, tableName);
            case TagNames.RENAME_TABLE        -> parseRenameTableAction(actionElement, tableName);
            case TagNames.RENAME_COLUMN       -> parseRenameColumnAction(actionElement, tableName);
            case TagNames.MODIFY_COLUMN_TYPE  -> parseModifyColumnTypeAction(actionElement, tableName);
            case TagNames.DROP_COLUMN         -> parseDropColumnAction(actionElement, tableName);
            case TagNames.DROP_TABLE          -> new DropTableAction(tableName);
            case TagNames.DROP_CONSTRAINT     -> parseDropConstraintAction(actionElement, tableName);
            case TagNames.DROP_INDEX          -> parseDropIndexAction(actionElement, tableName);
            default -> {
                logger.error("Unsupported action type: {}", actionType);
                yield null;
            }
        };
    }

    private MigrationAction parseCreateTableAction(Element actionElement, String tableName) {
        List<Column> columns = new ArrayList<>();
        NodeList columnNodes = actionElement.getElementsByTagName(TagNames.COLUMN);
        for (int i = 0; i < columnNodes.getLength(); i++) {
            columns.add(parseColumn((Element) columnNodes.item(i), tableName));
        }
        return new CreateTableAction(columns);
    }

    private MigrationAction parseAddColumnAction(Element actionElement, String tableName) {
        Element columnElement = (Element) actionElement.getElementsByTagName(TagNames.COLUMN).item(0);
        Column column = parseColumn(columnElement, tableName);
        return new AddColumnAction(column);
    }

    private MigrationAction parseAddConstraintAction(Element actionElement, String tableName) {
        Element columnElement = (Element) actionElement.getElementsByTagName(TagNames.COLUMN).item(0);
        Column column = parseColumn(columnElement, tableName);
        return new AddConstraintAction(column.getConstraintsList().getFirst());
    }

    private MigrationAction parseAddIndexAction(Element actionElement, String tableName) {
        Element indexElement = (Element) actionElement.getElementsByTagName(TagNames.INDEX).item(0);
        Index index = parseIndex(indexElement, tableName);
        return new AddIndexAction(index);
    }

    private MigrationAction parseRenameTableAction(Element actionElement, String tableName) {
        String newTableName = actionElement.getAttribute(AttributeNames.NEW_TABLE_NAME);
        return new RenameTableAction(tableName, newTableName);
    }

    private MigrationAction parseRenameColumnAction(Element actionElement, String tableName) {
        Element columnElement = (Element) actionElement.getElementsByTagName(TagNames.COLUMN).item(0);
        Column column = parseColumn(columnElement, tableName);
        return new RenameColumnAction(column);
    }

    private MigrationAction parseModifyColumnTypeAction(Element actionElement, String tableName) {
        Element columnElement = (Element) actionElement.getElementsByTagName(TagNames.COLUMN).item(0);
        Column column = parseColumn(columnElement, tableName);
        return new ModifyColumnTypeAction(column);
    }

    private MigrationAction parseDropColumnAction(Element actionElement, String tableName) {
        Element columnElement = (Element) actionElement.getElementsByTagName(TagNames.COLUMN).item(0);
        Column column = parseColumn(columnElement, tableName);
        return new DropColumnAction(column);
    }

    private MigrationAction parseDropConstraintAction(Element actionElement, String tableName) {
        Element columnElement = (Element) actionElement.getElementsByTagName(TagNames.COLUMN).item(0);
        Column column = parseColumn(columnElement, tableName);
        return new DropConstraintAction(column.getConstraintsList().getFirst());
    }

    private MigrationAction parseDropIndexAction(Element actionElement, String tableName) {
        Element indexElement = (Element) actionElement.getElementsByTagName(TagNames.INDEX).item(0);
        Index index = parseIndex(indexElement, tableName);
        return new DropIndexAction(index);
    }

    private List<MigrationAction> parseActions(NodeList rollbackActionNodes) {
        List<MigrationAction> rollbackActions = new ArrayList<>();

        for (int k = 0; k < rollbackActionNodes.getLength(); k++) {
            Node rollbackActionNode = rollbackActionNodes.item(k);
            if (rollbackActionNode.getNodeType() != Node.ELEMENT_NODE) continue;
            Element rollbackActionElement = (Element) rollbackActionNode;

            MigrationAction rollbackAction = parseAction(rollbackActionElement);
            if (rollbackAction != null) {
                rollbackActions.add(rollbackAction);
                logger.debug(LoggerHelper.REVERSE_INDENT + "   Parsed rollback action: {}", rollbackAction.getClass().getSimpleName());
            }
            else {
                logger.warn("Unknown action faced: {}", rollbackActionElement.getTagName());
            }
        }

        return rollbackActions;
    }

    private Column parseColumn(Element columnElement, String tableName) {
        Column column = new Column();
        column.setName(parseStringOrDefault(columnElement, AttributeNames.COLUMN_NAME, ""));
        column.setTableName(tableName);
        column.setType(parseStringOrDefault(columnElement, AttributeNames.COLUMN_TYPE, ""));
        column.setNewDataType(parseStringOrDefault(columnElement, AttributeNames.NEW_DATA_TYPE, ""));
        column.setNewName(parseStringOrDefault(columnElement, AttributeNames.NEW_COLUMN_NAME, ""));

        logger.debug(LoggerHelper.INDENT + "      Parsing column: Name={}", column.getName());

        NodeList constraintsNodes = columnElement.getElementsByTagName(TagNames.CONSTRAINT);
        for (int i = 0; i < constraintsNodes.getLength(); i++) {
            Element constraintElement = (Element) constraintsNodes.item(i);
            Constraint constraint = parseConstraint(constraintElement, tableName, column.getName());
            column.getConstraintsList().add(constraint);
        }

        logger.debug(LoggerHelper.REVERSE_INDENT + "      Parsed column {} with {} constraints", column.getName(), column.getConstraintsList().size());

        return column;
    }

    private Index parseIndex(Element indexElement, String tableName) {
        Index index = new Index();
        index.setUnique(parseBooleanOrDefault(indexElement, AttributeNames.INDEX_UNIQUENESS, false));
        index.setTableName(tableName);

        List<String> columnNames = index.getColumns();
        NodeList columnNodes = indexElement.getElementsByTagName(TagNames.COLUMN);
        for (int i = 0; i < columnNodes.getLength(); i++) {
            String columnName = parseStringOrDefault((Element) columnNodes.item(i), AttributeNames.COLUMN_NAME, "");

            if (!columnName.isEmpty()) {
                columnNames.add(columnName);
            }
        }

        index.setName(NameGenerator.generateIndexName(tableName, columnNames));

        logger.debug(LoggerHelper.INDENT + "          Index: Unique={}, Name={}", index.isUnique(), index.getName());

        return index;
    }

    private Constraint parseConstraint(Element constraintElement, String tableName, String columnName) {
        Constraint constraint = new Constraint();
        constraint.setTableName(tableName);

        ConstraintType type = ConstraintType.valueOf(constraintElement.getAttribute(AttributeNames.CONSTRAINT_TYPE).toUpperCase());
        constraint.setType(type);

        constraint.setName(NameGenerator.generateConstraintName(tableName, columnName, type.toString()));
        constraint.setExpression(parseStringOrDefault(constraintElement, AttributeNames.EXPRESSION, ""));
        constraint.setColumnName(columnName);

        logger.debug(LoggerHelper.INDENT + "          Constraint: Type={}, Name={}", type, constraint.getName());

        return constraint;
    }

    // Helper
    private String parseStringOrDefault(Element element, String attributeName, String defaultValue) {
        if (element.hasAttribute(attributeName)) {
            return element.getAttribute(attributeName);
        }
        return defaultValue;
    }

    private boolean parseBooleanOrDefault(Element element, String attributeName, boolean defaultValue) {
        if (element.hasAttribute(attributeName)) {
            return Boolean.parseBoolean(element.getAttribute(attributeName));
        }
        return defaultValue;
    }
}
