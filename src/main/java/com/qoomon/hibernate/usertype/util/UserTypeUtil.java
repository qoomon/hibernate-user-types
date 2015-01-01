package com.qoomon.hibernate.usertype.util;

import org.hibernate.cfg.Configuration;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.UserType;

import java.util.Collection;

public final class UserTypeUtil {

    private UserTypeUtil() {
    }

    public static void registerUserTypes(final Configuration configuration, final Collection<UserType> userTypes) {
        for (final UserType userType : userTypes) {
            registerUserType(configuration, userType);
        }
    }

    public static void registerUserType(final Configuration configuration, final UserType userType) {
        configuration.registerTypeOverride(userType, new String[] { userType.returnedClass().getName() });
    }

    public static void registerCompositeUserTypes(final Configuration configuration, final Collection<CompositeUserType> userTypes) {
        for (final CompositeUserType userType : userTypes) {
            registerCompositeUserType(configuration, userType);
        }
    }

    public static void registerCompositeUserType(final Configuration configuration, final CompositeUserType userType) {
        configuration.registerTypeOverride(userType, new String[] { userType.returnedClass().getName() });
    }
    
}
