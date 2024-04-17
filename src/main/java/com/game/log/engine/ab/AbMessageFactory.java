package com.game.log.engine.ab;

/**
 * 用于consumer listener和handler的添加和获取
 * @author bk
 */
public abstract class AbMessageFactory<T> {

    public abstract T get(String groupName);

    public abstract void set(String groupName,T t);
}
