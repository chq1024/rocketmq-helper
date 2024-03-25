package com.game.log.engine.product.templates;

import com.game.log.engine.base.AbstractExtTemplate;
import com.game.log.engine.product.ProductEnum;
import com.game.log.engine.product.ProductFactory;
import org.apache.rocketmq.spring.annotation.ExtRocketMQTemplateConfiguration;

import javax.annotation.PostConstruct;

/**
 * @author bk
 */
@ExtRocketMQTemplateConfiguration(value = "specialMqTemplate", instanceName = "specialProduct", nameServer = "${rocketmq.name-server}", group = "${mq.product.special.group}")
public class SpecialMsgProduct extends AbstractExtTemplate {

    @PostConstruct
    public void inited() {
        register();
    }

    @Override
    public void register() {
        ProductFactory.regOrReleaseMqTemplate(ProductEnum.SPECIAL_MQ,this);
    }

    @Override
    public void release() {
        ProductFactory.regOrReleaseMqTemplate(ProductEnum.SPECIAL_MQ,this);
    }
}
