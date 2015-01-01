package com.qoomon.hibernate.usertype.javax.mail;

import com.qoomon.hibernate.usertype.AbstractCompositeUserType;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StringType;

import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InternetAddressUserType extends AbstractCompositeUserType {

    protected static final int PROPERTY_COUNT = 2;

    protected static final int ADDRESS_INDEX = 0;
    protected static final int PERSONAL_INDEX = 1;

    public InternetAddressUserType() {
        super(PROPERTY_COUNT);
        setProperty(ADDRESS_INDEX, "address", StringType.INSTANCE);
        setProperty(PERSONAL_INDEX, "personal", StringType.INSTANCE);
    }


    @Override
    public Class<InternetAddress> returnedClass() {
        return InternetAddress.class;
    }


    @Override
    public Object getPropertyValue(final Object component, final int propertyIndex) throws HibernateException {
        InternetAddress internetAddress = (InternetAddress) component;

        if (internetAddress == null) {
            return null;
        }
        if (propertyIndex == ADDRESS_INDEX) {
            return internetAddress.getAddress();
        }
        if (propertyIndex == PERSONAL_INDEX) {
            return internetAddress.getPersonal();
        }
        throw new IndexOutOfBoundsException("unexpected property index " + propertyIndex);
    }

    @Override
    public Object nullSafeGet(final ResultSet resultSet, final String[] names, final SessionImplementor session, final Object owner) throws SQLException {

        String address = resultSet.getString(names[ADDRESS_INDEX]);
        String personal = resultSet.getString(names[PERSONAL_INDEX]);

        if (address == null) {
            return null;
        }

        try {
            return new InternetAddress(address, personal);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void nullSafeSet(final PreparedStatement st, final Object component, final int index, final SessionImplementor session) throws SQLException {

        String address = null;
        String personal = null;

        InternetAddress internetAddress = (InternetAddress) component;
        if (internetAddress != null) {
            address = internetAddress.getAddress();
            personal = internetAddress.getPersonal();
        }

        st.setObject(index + ADDRESS_INDEX, address, getPropertyType(ADDRESS_INDEX).sqlType());
        st.setObject(index + PERSONAL_INDEX, personal, getPropertyType(PERSONAL_INDEX).sqlType());
    }
}
