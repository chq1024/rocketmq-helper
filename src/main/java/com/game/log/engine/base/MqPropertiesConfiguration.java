package com.game.log.engine.base;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "mq")
public class MqPropertiesConfiguration {
    private String nameserver;
}
