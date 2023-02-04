package com.xuecheng.manage_cms;


import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EntityScan(basePackages = {"com.xuecheng.framework.domain.cms"})
@ComponentScan(basePackages = {"com.xuecheng.api"})
@ComponentScan(basePackages = {"com.xuecheng.manage_cms"})
@ComponentScan(basePackages = {"com.xuecheng.framework"})
public class ManageCmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ManageCmsApplication.class,args);
    }


    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
    }

    @Value("${spring.data.mongodb.database}")
    private String db;

    @Bean
    public GridFSBucket gridFSBucket(MongoClient mongoClient) {

        // 把application.yml中的配置信息赋值给db

        // 获取数据库
        MongoDatabase mongoDatabase = mongoClient.getDatabase(db);

        // 获取打开下载流
        GridFSBucket gridFSBucket = GridFSBuckets.create(mongoDatabase);
        // 指定数据库下的集合
        // GridFSBucket gridFSBucket = GridFSBuckets.create(mongoDatabase, "集合名称");
        return gridFSBucket;
    }

}

