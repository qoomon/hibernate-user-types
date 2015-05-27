package com.qoomon.hibernate.usertype.domainvalue;

public class DomainValueUserTypeFactory {

    public <T extends DV<V>, V> DomainValueUserType<T, V> generate( Class<T> domainValueType) {
        return new DomainValueUserType<T, V>(domainValueType);
    }

    public <T extends DV<V>, V> List<DomainValueUserType<T, V>> generate(final Collection<Class<T>> domainValueTypes) {
        final List<DomainValueUserType> result = new LinkedList<DomainValueUserType>();
        for (final Class<T> domainValueType : domainValueTypes) {
            result.add(generate(domainValueType));
        }
        return result;
    }
}
