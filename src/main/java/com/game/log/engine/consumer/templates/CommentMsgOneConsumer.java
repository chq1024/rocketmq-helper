package com.game.log.engine.consumer.templates;

import com.game.log.engine.base.ConsumerTemplate;
import com.game.log.engine.base.TemplateFactory;
import com.game.log.engine.base.TemplateMappingEnum;
import org.apache.rocketmq.spring.annotation.ExtRocketMQConsumerConfiguration;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author bk
 */
@Service
public class CommentMsgOneConsumer extends ConsumerTemplate {

    @PostConstruct
    public void inited() {
        register();
    }


    @Override
    public void register() {
        TemplateFactory.regOrReleaseMqTemplate(TemplateMappingEnum.MSG_COMMENT_MQ,this);
    }

    @Override
    public void release() {
        TemplateFactory.regOrReleaseMqTemplate(TemplateMappingEnum.MSG_COMMENT_MQ,this);
    }
}
