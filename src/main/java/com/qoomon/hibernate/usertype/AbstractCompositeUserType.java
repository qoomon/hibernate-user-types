package com.qoomon.hibernate.usertype;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.SerializationException;
import org.hibernate.type.SingleColumnType;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by qoomon on 30/11/14.
 */
public abstract class AbstractCompositeUserType implements CompositeUserType {

    protected final String[] propertyNames;
    protected final SingleColumnType<?>[] propertyTypes;
    private final int propertyCount;

    protected AbstractCompositeUserType(int propertyCount) {
        this.propertyCount = propertyCount;
        propertyNames = new String[this.propertyCount];
        propertyTypes = new SingleColumnType<?>[this.propertyCount];
    }

    /**
     * @param type      class of field
     * @param fieldName field name
     * @return the field with setAccessible(true)
     */
    protected static Field getAccessibleField(Class<?> type, String fieldName) {
        try {
            Field field = type.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param field      field of instance
     * @param instance   instance of field
     * @param fieldValue field value
     */
    protected static void setFieldValue(Field field, Object instance, Object fieldValue) {
        try {
            field.set(instance, fieldValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param field    field of instance
     * @param instance instance of field
     * @param <T>      field type
     * @return field value
     */
    protected static <T> T getFieldValue(Field field, Object instance) {
        try {
            return (T) field.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected void setProperty(int index, String name, SingleColumnType<?> type) {
        propertyNames[index] = name;
        propertyTypes[index] = type;
    }

    protected String getPropertyName(int index) {
        return propertyNames[index];
    }

    protected SingleColumnType<?> getPropertyType(int index) {
        return propertyTypes[index];
    }

    @Override
    public String[] getPropertyNames() {
        return propertyNames.clone();
    }

    @Override
    public Type[] getPropertyTypes() {
        return propertyTypes.clone();
    }

    @Override
    abstract public Class returnedClass();

    /**
     * This will return the individual value of the property
     */
    @Override
    abstract public Object getPropertyValue(final Object component, final int property) throws HibernateException;

    /**
     * This method is called to convert the sql column data into Java model object
     */
    @Override
    public void setPropertyValue(final Object component, final int property, final Object setValue) throws HibernateException {
        throw new HibernateException("setPropertyValue() for immutable components is not supported! CompositeUserTypes for mutable objects needs to overwrite setPropertyValue()");
    }

    /**
     * This is called so as to retrieve the values from the sql resultset
     */
    @Override
    abstract public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException;

    /**
     * Before executing the save call this method is called. It will set the
     * values in the prepared statement
     */
    @Override
    abstract public void nullSafeSet(PreparedStatement statement, Object component, int index, SessionImplementor session) throws HibernateException, SQLException;

    /**
     * method called when Hibernate puts the data in a second level cache. The data is stored
     * in a serializable form
     */
    @Override
    public Serializable disassemble(final Object component, final SessionImplementor paramSession) {

        if (component == null) {
            return null;
        }

        if (!(component instanceof Serializable)) {
            throw new SerializationException(component.getClass().getName() + " is not serializable! You must overwrite disassemble()", null);
        }

        return (Serializable) deepCopy(component);
    }

    /**
     * Returns the object from the 2 level cache
     */
    @Override
    public Object assemble(final Serializable cached, final SessionImplementor session, final Object owner) {

        if (!returnedClass().isInstance(cached)) {
            throw new SerializationException(cached.getClass().getName() + " is not deserializable! You must overwrite assemble()", null);
        }

        return deepCopy(cached);
    }

    /**
     * Method is called when merging two objects.
     */
    @Override
    public Object replace(final Object original, final Object target, final SessionImplementor session, final Object owner) throws HibernateException {
        //For mutable types at bare minimum return a deep copy of first argument
        return deepCopy(original);
    }

    /**
     * Used while dirty checking
     */
    @Override
    public boolean equals(final Object o1, final Object o2) throws HibernateException {

        if (null == o1 || null == o2) {
            return false;
        }
        if (o1 == o2) {
            return true;
        }
        return o1.equals(o2);
    }

    @Override
    public int hashCode(final Object value) throws HibernateException {
        return value.hashCode();
    }

    /**
     * Helps hibernate apply certain optimizations for immutable objects
     */
    @Override
    public boolean isMutable() {
        return false;
    }

    /**
     * Used to create Snapshots of the object
     */
    @Override
    public Object deepCopy(final Object component) throws HibernateException {

        if (!(returnedClass().isInstance(component))) {
            throw new IllegalArgumentException(component.getClass().getName() + " is no instance of " + returnedClass().getName(), null);
        }

        if (isMutable()) {
            throw new IllegalStateException("deepCopy() for mutable components is not supported. CompositeUserTypes for mutable objects needs to overwrite deepCopy()");
        }
        return component;
    }

}
