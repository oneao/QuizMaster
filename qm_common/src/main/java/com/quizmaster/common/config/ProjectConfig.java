package com.quizmaster.common.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * 项目配置信息
 */
@Component
@ConfigurationProperties(prefix = "project")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectConfig {
    private String name;
    private String version;
    private String author;
}
