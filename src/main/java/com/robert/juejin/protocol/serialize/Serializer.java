package com.robert.juejin.protocol.serialize;

import com.robert.juejin.protocol.serialize.json.JSONSerializer;

public interface Serializer {

    /**
     * 序列化算法
     */
    byte getSerializerAlgorithm();

    /**
     * Java对象转换成二进制
     */
    byte[] serialize(Object object);

    /**
     * 二进制转换成Java对象
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);

    /**
     * 默认序列化
     */
    Serializer DEFAULT = new JSONSerializer();
}
