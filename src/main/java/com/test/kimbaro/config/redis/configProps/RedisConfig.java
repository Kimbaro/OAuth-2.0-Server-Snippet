package com.test.kimbaro.config.redis.configProps;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@Data
public class RedisConfig {
    @JsonProperty(value = "host")
    private String HOST;

    @JsonProperty(value = "port")
    private Integer PORT;

    @JsonProperty(value = "password")
    private String PASSWORD;
}
