package com.game.log.engine.product.templates;

import com.game.log.engine.base.ProducerTemplate;
import com.game.log.engine.base.TemplateFactory;
import com.game.log.engine.base.TemplateMappingEnum;
import org.apache.rocketmq.spring.annotation.ExtRocketMQTemplateConfiguration;

import javax.annotation.PostConstruct;

/**
 * 消息生产者模板，@ExtRocketMQTemplateConfiguration每个Template对应一个product
 * 如果想建立生产者集群，需要使用同样的配置注入多个product
 * @author bk
 */

@ExtRocketMQTemplateConfiguration(instanceName = "commentOneProduct",nameServer = "${rocketmq.name-server}",group = "${rocketmq.producer.group}")
public class OneProduct extends ProducerTemplate {

    @PostConstruct
    public void inited() {
        register();
    }

    @Override
    public void register() {
        TemplateFactory.regOrReleaseMqTemplate(TemplateMappingEnum.PRODUCT_TEMPLATE,this);
    }

    @Override
    public void release() {
        TemplateFactory.regOrReleaseMqTemplate(TemplateMappingEnum.PRODUCT_TEMPLATE,this);
    }
}
