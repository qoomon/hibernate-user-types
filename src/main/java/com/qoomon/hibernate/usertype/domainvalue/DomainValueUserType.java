package com.qoomon.hibernate.usertype.domainvalue;

import com.qoomon.domainvalue.type.DV;
import com.qoomon.hibernate.usertype.AbstractUserType;
import com.qoomon.hibernate.usertype.util.GenericTypeUtil;
import com.qoomon.hibernate.usertype.util.HibernateTypeUtil;
import org.hibernate.type.SingleColumnType;
import org.hibernate.usertype.UserType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class DomainValueUserType<T extends DV<V>, V> extends AbstractUserType<T, V> {

    private static final long serialVersionUID = -4693695512787385672L;

    private final SingleColumnType<V> hibernateType;
    private Constructor<T> domainValueConstructor;


    public DomainValueUserType(final Class<T> domainValueType) {
        super(domainValueType);

        Class<V> valueType = getTypeArgument(domainValueType);
        try {
            this.domainValueConstructor = domainValueType.getDeclaredConstructor(valueType);
            this.domainValueConstructor.setAccessible(true);
        } catch (Exception ex) {
            throw new RuntimeException("missing constructor " + domainValueType.getSimpleName() + "("
                    + valueType.getSimpleName() + ")", ex);
        }

        this.hibernateType = HibernateTypeUtil.getType(valueType);
        if (this.hibernateType == null) {
            throw new RuntimeException("No mapping for value type " + valueType.getName());
        }
    }

    @SuppressWarnings("unchecked")
    protected  Class<V> getTypeArgument(final Class<T> domainValueType) {
        return (Class<V>) GenericTypeUtil.getTypeArguments(DV.class, domainValueType).get(0);
    }

    @Override
    public SingleColumnType<V> getHibernateType() {
        return hibernateType;
    }

    @Override
    protected T nullSafeWrap(final V value) {
        if (value == null) {
            return null;
        }
        try {
            // safe operation. was check on construction.
            return this.domainValueConstructor.newInstance(value);
        } catch (Exception ex) {
            throw new RuntimeException("unexpected",ex);
        }
    }

    @Override
    protected V nullSafeUnwrap(final T domainValue) {
        if (domainValue == null) {
            return null;
        }
        return domainValue.value();
    }

}
