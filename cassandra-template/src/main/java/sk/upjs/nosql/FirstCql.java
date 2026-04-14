package sk.upjs.nosql;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.DriverException;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.DataTypes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.cassandra.core.cql.CqlTemplate;
import org.springframework.data.cassandra.core.cql.RowMapper;
import org.springframework.data.cassandra.core.cql.generator.CreateTableCqlGenerator;
import org.springframework.data.cassandra.core.cql.keyspace.CreateTableSpecification;

import java.util.UUID;
/* CREATE KEYSPACE template WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': 1 }; */

public class FirstCql {
    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(CassandraConfig.class);
        CqlTemplate template = context.getBean(CqlTemplate.class);
        CqlSession session = context.getBean(CqlSession.class);
        template.execute("DROP TABLE IF EXISTS first");
        template.execute("CREATE TABLE IF NOt EXISTS first " +
                "(id uuid PRIMARY KEY, " +
                " value text)");
        for (int i = 0; i < 10; i++) {
            template.execute("INSERT INTO first (id, value) VALUES (?, ?)",
                    UUID.randomUUID(), "value" + i);
        }
        template.query("SELECT * FROM first", new RowMapper<Void>() {

            @Override
            public Void mapRow(Row row, int rowNum) throws DriverException {
                System.out.println("ID: " + row.getUuid("id") +
                                   " Value: " + row.getString("value"));
                return null;
            }
        });
        CreateTableSpecification specification = CreateTableSpecification.createTable("second")
                .ifNotExists()
                .partitionKeyColumn("id_oddelenia", DataTypes.BIGINT)
                .clusteredKeyColumn("name", DataTypes.TEXT)
                .column("salary", DataTypes.DECIMAL);
        String cql = CreateTableCqlGenerator.toCql(specification);
        System.out.println(cql);
        template.execute(cql);

        session.close();
    }
}
