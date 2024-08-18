package org.david.demo.config;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;

@Configuration
public class MongoConfiguration {

    @Bean
    public MongoClientFactoryBean mongo(@Value("${spring.data.mongodb.uri}") String uri) {
        MongoClientFactoryBean mongo = new MongoClientFactoryBean();
        ConnectionString conn = new ConnectionString(uri);
        mongo.setConnectionString(conn);
        mongo.setSingleton(true);
        return mongo;
    }

    @Bean
    public MongoClient mongoClient(MongoClientFactoryBean mongo) throws Exception {
        return mongo.getObject();
    }

    @Bean
    public GridFSBucket gridFSBucket(@Value("${spring.data.mongodb.gridfs.database}") String database,
                                     @Value("${spring.data.mongodb.gridfs.bucket}") String bucket,
                                     MongoClient mongoClient) {
        return GridFSBuckets.create(mongoClient.getDatabase(database), bucket);
    }

}
