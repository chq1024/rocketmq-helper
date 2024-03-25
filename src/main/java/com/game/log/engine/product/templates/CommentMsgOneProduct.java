package com.game.log.engine.product.templates;

import com.game.log.engine.base.AbstractExtTemplate;
import com.game.log.engine.product.ProductEnum;
import com.game.log.engine.product.ProductFactory;
import org.apache.rocketmq.spring.annotation.ExtRocketMQTemplateConfiguration;

import javax.annotation.PostConstruct;

/**
 * @author bk
 */
@ExtRocketMQTemplateConfiguration(value = "commentMqOneProduct", instanceName = "commentOneProduct", nameServer = "${rocketmq.name-server}", group = "${mq.product.comment.group}")
public class CommentMsgOneProduct extends AbstractExtTemplate {

    @PostConstruct
    public void inited() {
        register();
    }

    @Override
    public void register() {
        ProductFactory.regOrReleaseMqTemplate(ProductEnum.COMMENT_MQ,this);
    }

    @Override
    public void release() {
        ProductFactory.regOrReleaseMqTemplate(ProductEnum.COMMENT_MQ,this);
    }
}
