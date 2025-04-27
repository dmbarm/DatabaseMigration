package org.migrationtool.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.migrationtool.utils.ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class DatabasePool {
    private static final Logger logger = LoggerFactory.getLogger(DatabasePool.class);
    private static final HikariDataSource dataSource;

    static {
        if (logger.isInfoEnabled()) {
            logger.info("Initializing database pool with URL: {}", ConfigLoader.getProperty("database.url"));
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(ConfigLoader.getProperty("database.url"));
        config.setUsername(ConfigLoader.getProperty("database.user"));
        config.setPassword(ConfigLoader.getProperty("database.password"));
        config.setMaximumPoolSize(10);
        config.setAutoCommit(false);
        config.setDriverClassName("org.h2.Driver");

        dataSource = new HikariDataSource(config);
    }

    private DatabasePool() {}

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static void close() {
        dataSource.close();
    }
}
