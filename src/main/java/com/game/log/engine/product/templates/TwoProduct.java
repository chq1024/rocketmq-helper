package com.game.log.engine.product.templates;

import com.game.log.engine.base.ProducerTemplate;
import com.game.log.engine.base.TemplateFactory;
import com.game.log.engine.base.TemplateMappingEnum;
import org.apache.rocketmq.spring.annotation.ExtRocketMQTemplateConfiguration;

import javax.annotation.PostConstruct;

/**
 * 等同于CommentMsgOneProduct
 * @author bk
 */
@ExtRocketMQTemplateConfiguration(instanceName = "commentTwoProduct",nameServer = "${rocketmq.name-server}", group = "${rocketmq.producer.group}")
public class TwoProduct extends ProducerTemplate {

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
