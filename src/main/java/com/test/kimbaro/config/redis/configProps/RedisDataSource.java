package com.test.kimbaro.config.redis.configProps;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@Data
@Builder
public class RedisDataSource {
    @JsonProperty(value = "host")
    private String HOST;

    @JsonProperty(value = "port")
    private Integer PORT;

    @JsonProperty(value = "password")
    private String PASSWORD;
}
