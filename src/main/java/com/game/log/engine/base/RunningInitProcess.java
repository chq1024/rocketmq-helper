package com.game.log.engine.base;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author bk
 */
@Configuration
@EnableConfigurationProperties(MqProperties.class)
public class RunningInitProcess implements ApplicationRunner{

    private MqProperties mqProperties;

    public RunningInitProcess(MqProperties mqProperties) {
        this.mqProperties = mqProperties;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

    }

}
