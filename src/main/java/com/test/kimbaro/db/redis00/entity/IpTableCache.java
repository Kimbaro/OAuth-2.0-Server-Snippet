package com.test.kimbaro.db.redis00.entity;

import lombok.Builder;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@Data
@RedisHash(value = "ipTableCache")
@Builder
public class IpTableCache implements Serializable {
    @Id
    private String companyId;

    private String ipTables;
}
