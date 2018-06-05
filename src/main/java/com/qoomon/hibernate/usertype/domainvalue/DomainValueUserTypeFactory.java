package com.qoomon.hibernate.usertype.domainvalue;

import com.qoomon.domainvalue.type.DV;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class DomainValueUserTypeFactory {

    public <T extends DV<V>, V> DomainValueUserType<T, V> generate(Class<T> domainValueType) {
        return new DomainValueUserType<T, V>(domainValueType);
    }

    public <T extends DV<V>, V> List<DomainValueUserType<T, V>> generate(final Collection<Class<T>> domainValueTypes) {
        final List<DomainValueUserType<T, V>> result = new LinkedList<DomainValueUserType<T, V>>();
        for (final Class<T> domainValueType : domainValueTypes) {
            result.add(generate(domainValueType));
        }
        return result;
    }
}
