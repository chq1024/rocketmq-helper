package com.game.log.engine;

import com.game.log.engine.base.MqPropertiesConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author beikei
 */
@SpringBootApplication
@EnableConfigurationProperties(value = {MqPropertiesConfiguration.class})
public class EngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(EngineApplication.class, args);
    }

}
