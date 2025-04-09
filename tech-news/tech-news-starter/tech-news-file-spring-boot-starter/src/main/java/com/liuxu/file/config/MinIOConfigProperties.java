package com.liuxu.file.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "minio")
public class MinIOConfigProperties implements Serializable {
    private String endpoint;
    private String bucket;
    private String accessKey;
    private String secretKey;
}
