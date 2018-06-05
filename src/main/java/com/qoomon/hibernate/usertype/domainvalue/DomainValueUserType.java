package com.qoomon.hibernate.usertype.domainvalue;

import org.hibernate.type.SingleColumnType;

import com.qoomon.domainvalue.type.DV;
import com.qoomon.hibernate.usertype.AbstractUserType;
import org.hibernate.type.TypeResolver;
import org.hibernate.usertype.UserType;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class DomainValueUserType<T extends DV<V>, V> extends AbstractUserType<T, V> {

    private final SingleColumnType<V> hibernateType;

    public DomainValueUserType(TypeResolver typeResolver, final Class<T> domainValueType) {
        super(domainValueType);

        Class<V> valueType = DV.getValueType(domainValueType);
        //noinspection unchecked
        this.hibernateType = (SingleColumnType<V>) typeResolver.basic(valueType.getName());

        if (this.hibernateType == null) {
            throw new RuntimeException("No mapping for value type " + valueType.getName());
        }
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
        return DV.of(getUserType(), value);
    }

    @Override
    protected V nullSafeUnwrap(final T domainValue) {
        if (domainValue == null) {
            return null;
        }
        return domainValue.value();
    }

}
