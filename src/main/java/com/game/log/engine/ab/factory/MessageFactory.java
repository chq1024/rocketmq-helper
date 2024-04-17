package com.game.log.engine.ab.factory;

/**
 * @author bk
 */
public class MessageFactory {

    public static MessageListenerFactory listener() {
        return MessageListenerFactory.instance();
    }

    public static MessageHandlerFactory handler() {
        return MessageHandlerFactory.instance();
    }
}
