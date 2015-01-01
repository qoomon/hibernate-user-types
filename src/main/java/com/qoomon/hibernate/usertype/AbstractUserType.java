package com.qoomon.hibernate.usertype;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.SerializationException;
import org.hibernate.type.SingleColumnType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbstractUserType<U, V> implements UserType {

    private final Class<U> userType;

    public AbstractUserType(final Class<U> userType) {
        this.userType = userType;
    }

    public abstract SingleColumnType<V> getHibernateType();

    @Override
    public int[] sqlTypes() {
        return new int[]{getHibernateType().sqlType()};
    }

    @Override
    public U nullSafeGet(final ResultSet resultSet, final String[] strings, final SessionImplementor session, final Object owner) throws SQLException {
        @SuppressWarnings("unchecked")
        final V value = (V) getHibernateType().nullSafeGet(resultSet, strings[0], session, owner);
        return nullSafeWrap(value);
    }

    @Override
    public void nullSafeSet(final PreparedStatement preparedStatement, final Object object, final int index, final SessionImplementor session) throws SQLException {
        @SuppressWarnings("unchecked")
        final U userObject = (U) object;
        final V value = nullSafeUnwrap(userObject);
        getHibernateType().nullSafeSet(preparedStatement, value, index, session);
    }


    /**
     * @param sqlValue
     * @return UserObject or null
     */
    protected abstract U nullSafeWrap(final V sqlValue);

    /**
     * @param userObject
     * @return SqlValue or null
     */
    protected abstract V nullSafeUnwrap(final U userObject);


    public Class<U> getUserType() {
        return this.userType;
    }

    @Override
    public Class<U> returnedClass() {
        return this.userType;
    }

    @Override
    public final boolean isMutable() {
        return false;
    }

    @Override
    public boolean equals(final Object x, final Object y) throws HibernateException {
        if ((x == null) && (y == null)) {
            return true;
        }
        if ((x == null) || (y == null)) {
            return false;
        }
        return x.equals(y);
    }

    @Override
    public int hashCode(final Object x) throws HibernateException {
        assert (x != null);
        return x.hashCode();
    }

    @Override
    public Object assemble(final Serializable cachedValue, final Object owner) throws HibernateException {
        return deepCopy(cachedValue);
    }

    @Override
    public Serializable disassemble(final Object value) throws HibernateException {

        if (value == null) {
            return null;
        }

        final Object deepCopy = deepCopy(value);
        if (!(deepCopy instanceof Serializable)) {
            throw new SerializationException(String.format("deepCopy of %s is not serializable", value), null);
        }
        return (Serializable) deepCopy;

    }

    @Override
    public Object replace(final Object originalValue, final Object target, final Object owner)
            throws HibernateException {
        return deepCopy(originalValue);
    }

    @Override
    public Object deepCopy(final Object value) throws HibernateException {
        return value;
    }

}
