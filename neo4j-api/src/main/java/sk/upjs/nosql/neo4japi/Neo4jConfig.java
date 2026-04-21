package sk.upjs.nosql.neo4japi;

import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.springframework.context.annotation.Bean;
import org.neo4j.dbms.api.DatabaseManagementService;

import java.nio.file.Path;

public class Neo4jConfig {

    public static final Path DATABASE_DIRECTORY = Path.of("target/database");

    @Bean
    public DatabaseManagementService databaseManagementService() {
        DatabaseManagementService service = new DatabaseManagementServiceBuilder(DATABASE_DIRECTORY).build();
        return service;
    }
}
