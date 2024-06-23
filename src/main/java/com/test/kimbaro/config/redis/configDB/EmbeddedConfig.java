package com.test.kimbaro.config.redis.configDB;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.util.Date;


@Slf4j
@Configuration
public class EmbeddedConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;
    @Value("${spring.data.redis.port}")
    private int redisPort;

    private RedisServer redisServer;

    @PostConstruct
    public void startRedis() throws IOException {
        redisServer = RedisServer.builder()
                .setting("maxmemory 128M")
                .port(redisPort)
                .bind(redisHost)
                .build();
        redisServer.start();
        log.info("embedded redis status {}:{}[{}]", redisHost, redisPort, redisServer.isActive() ? "running" : "stopped");
        if (redisServer.isActive()) {
            RedisURI redisURI = RedisURI.builder()
                    .withHost(redisHost)
                    .withPort(redisPort)
                    .withDatabase(0)
                    .build();

            RedisClient client = RedisClient.create(redisURI);
            StatefulRedisConnection<String, String> connection = client.connect();
            connection.sync().set("TimeToLive", new Date().getTime() + "");
        }
    }

    @PreDestroy
    public void stopRedis() {
        redisServer.stop();
    }
}