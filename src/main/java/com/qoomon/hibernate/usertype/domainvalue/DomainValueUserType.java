package com.qoomon.hibernate.usertype.domainvalue;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.type.SingleColumnType;
import org.hibernate.usertype.UserType;

import com.qoomon.domainvalue.type.DV;
import com.qoomon.hibernate.usertype.AbstractUserType;
import com.qoomon.hibernate.usertype.util.HibernateTypeUtil;

public class DomainValueUserType<T extends DV<V>, V> extends AbstractUserType<T, V> {

    private final SingleColumnType<V> hibernateType;

    public DomainValueUserType(final Class<T> domainValueType) {
        super(domainValueType);

        Class<V> valueType = DV.getValueType(domainValueType);
        this.hibernateType = HibernateTypeUtil.getType(valueType);
        if (this.hibernateType == null) {
            throw new RuntimeException("No mapping for value type " + valueType.getName());
        }
    }

    public static <T extends DV<V>, V> List<UserType> generate(final Collection<Class<T>> domainValueTypes) {
        final List<UserType> result = new LinkedList<UserType>();

        for (final Class<T> domainValueClass : domainValueTypes) {
            final UserType userType = new DomainValueUserType<T, V>(domainValueClass);
            result.add(userType);
        }
        return result;
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
