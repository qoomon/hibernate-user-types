package com.qoomon.hibernate.usertype.joda.money;

import com.qoomon.hibernate.usertype.AbstractCompositeUserType;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.StringType;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

import javax.mail.internet.InternetAddress;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BigMoneyUserType extends AbstractCompositeUserType {

    protected static final int PROPERTY_COUNT = 2;

    protected static final int CURRENCY_INDEX = 0;
    protected static final int AMOUNT_INDEX = 1;

    private Constructor<BigMoney> bigMoneyConstructor;

    public BigMoneyUserType() {
        super(PROPERTY_COUNT);
        setProperty(CURRENCY_INDEX, "currency", StringType.INSTANCE);
        setProperty(AMOUNT_INDEX, "amount", BigDecimalType.INSTANCE);

        try {
            this.bigMoneyConstructor = BigMoney.class.getDeclaredConstructor(CurrencyUnit.class, BigDecimal.class);
            this.bigMoneyConstructor.setAccessible(true);
        } catch (NoSuchMethodException | SecurityException exception) {
            throw new RuntimeException("missing constructor " + BigMoney.class.getSimpleName() + "("
                    + CurrencyUnit.class.getSimpleName() + ", " + BigDecimal.class.getSimpleName() + ")", exception);
        }
    }


    @Override
    public Class<BigMoney> returnedClass() {
        return BigMoney.class;
    }


    @Override
    public Object getPropertyValue(final Object component, final int propertyIndex) throws HibernateException {
        BigMoney money = (BigMoney) component;

        if (money == null) {
            return null;
        }
        if (propertyIndex == CURRENCY_INDEX) {
            return money.getCurrencyUnit().getCode();
        }
        if (propertyIndex == AMOUNT_INDEX) {
            return money.getAmount();
        }
        throw new IndexOutOfBoundsException("unexpected property index " + propertyIndex);
    }

    @Override
    public BigMoney nullSafeGet(final ResultSet resultSet, final String[] names, final SessionImplementor session, final Object owner) throws HibernateException, SQLException {

        String currencyCode = resultSet.getString(names[CURRENCY_INDEX]);
        BigDecimal amount = resultSet.getBigDecimal(names[AMOUNT_INDEX]);

        if (currencyCode == null) {
            return null;
        }

        CurrencyUnit currencyUnit = CurrencyUnit.of(currencyCode);

        try {
            // safe operation. was check on construction.
            return bigMoneyConstructor.newInstance(currencyUnit, amount);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void nullSafeSet(final PreparedStatement st, final Object component, final int index, final SessionImplementor session) throws HibernateException, SQLException {
        BigMoney money = (BigMoney) component;
        
        String currencyCode = null;
        BigDecimal amount = null;

        if (money != null) {
            currencyCode = money.getCurrencyUnit().getCode();
            amount = money.getAmount();
        }

        st.setObject(index + CURRENCY_INDEX, currencyCode, getPropertyType(CURRENCY_INDEX).sqlType());
        st.setObject(index + AMOUNT_INDEX, amount, getPropertyType(AMOUNT_INDEX).sqlType());
    }
}
