package com.test.kimbaro.config.redis.redis00;

import com.test.kimbaro.config.redis.configProps.RedisDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.Duration;

import static java.util.Collections.singletonMap;

@Configuration
@Slf4j
public class Redis00Config {

    @Bean(name = "redis00Template")
    public RedisTemplate<String, Object> redis00Template(@Qualifier("redis00ConnectionFactory") LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean(name = "redis00ConnectionFactory")
    public LettuceConnectionFactory redis00ConnectionFactory(@Qualifier("redis00RedisDataSource") RedisDataSource redisDataSource) {
        LettuceConnectionFactory lettuceConnectionFactory =
                new LettuceConnectionFactory(redisDataSource.getHOST(), redisDataSource.getPORT());
        lettuceConnectionFactory.setDatabase(14);
        return lettuceConnectionFactory;
    }

    @Bean(name = "redis00RedisDataSource")
    @ConfigurationProperties(prefix = "spring.datasources.nosql.redis.redis00")
    public RedisDataSource redis01DataSource() {
        return RedisDataSource.builder().build();
    }
}
