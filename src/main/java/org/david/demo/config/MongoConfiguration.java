package org.david.demo.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfiguration extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}") private String uri;

    @Bean
    public MongoClient mongoClient() {
        final ConnectionString connectionString = new ConnectionString(uri);
        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public GridFSBucket gridFSBucket(@Value("${spring.data.mongodb.gridfs.database}") String database,
                                     @Value("${spring.data.mongodb.gridfs.bucket}") String bucket,
                                     MongoClient mongoClient) {
        return GridFSBuckets.create(mongoClient.getDatabase(database), bucket);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory dbFactory){
        return new MongoTemplate(dbFactory);
    }

    @Override
    protected String getDatabaseName() {
        ConnectionString connectionString = new ConnectionString(uri);
        return connectionString.getDatabase();
    }

    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }
}
