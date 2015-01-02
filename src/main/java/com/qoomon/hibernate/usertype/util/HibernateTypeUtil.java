package com.qoomon.hibernate.usertype.util;

import org.hibernate.type.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class HibernateTypeUtil {
    private static final Map<Class<?>, SingleColumnType<?>> hibernateTypeMap = new HashMap<Class<?>, SingleColumnType<?>>();

    static {
        setSqlMapping(Boolean.class, BooleanType.INSTANCE);

        setSqlMapping(Byte.class, ByteType.INSTANCE);
        setSqlMapping(byte[].class, BinaryType.INSTANCE);

        setSqlMapping(Short.class, ShortType.INSTANCE);
        setSqlMapping(Integer.class, IntegerType.INSTANCE);
        setSqlMapping(Long.class, LongType.INSTANCE);
        setSqlMapping(BigInteger.class, BigIntegerType.INSTANCE);

        setSqlMapping(Float.class, FloatType.INSTANCE);
        setSqlMapping(Double.class, DoubleType.INSTANCE);
        setSqlMapping(BigDecimal.class, BigDecimalType.INSTANCE);

        setSqlMapping(Character.class, CharacterType.INSTANCE);
        setSqlMapping(String.class, StringType.INSTANCE);
    }


    public static <V> SingleColumnType<V> setSqlMapping(final Class<V> valueType, final SingleColumnType<V> hibernateType) {
        @SuppressWarnings("unchecked")
        final SingleColumnType<V> oldSqlMapping = (SingleColumnType<V>) hibernateTypeMap.put(valueType, hibernateType);
        return oldSqlMapping;
    }

    public static <V> SingleColumnType<V> getType(final Class<V> valueType) {
        @SuppressWarnings("unchecked")
        final SingleColumnType<V> sqlMapping = (SingleColumnType<V>) hibernateTypeMap.get(valueType);
        return sqlMapping;
    }
}
