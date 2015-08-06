Hibernate User Types [![Build Status](https://travis-ci.org/qoomon/hibernate-user-type.svg?branch=master)](https://travis-ci.org/qoomon/hibernate-user-type)
===================
**Maven Dependency**
```xml
<dependency>
    <groupId>com.qoomon</groupId>
    <artifactId>hibernate-usertype</artifactId>
    <version>0.1.0</version>
</dependency>
```

* **Domain Value** - https://github.com/qoomon/domain-value
  * **DV<T>** - com.qoomon.domainvalue.type.DV;
    * com.qoomon.hibernate.usertype.domainvalue.DomainValueUserType<T,V> 

* **Joda Money** - http://www.joda.org/joda-money
  * **BigMoney** - org.joda.money.BigMoney;
    * com.qoomon.hibernate.usertype.joda.money.BigMoneyUserType
  * **Money** - org.joda.money.Money;
    * com.qoomon.hibernate.usertype.joda.money.MoneyUserType

* **Joda Time** - http://www.joda.org/joda-time
  * (comming soon...)

* **Java Mail API** - https://java.net/projects/javamail
  * **InternetAddress** - javax.mail.internet.InternetAddress
    * com.qoomon.hibernate.usertype.javax.mail.InternetAddressUserType


**Spring Example**
```java
@Bean
public SessionFactory sessionFactory(){
    Configuration configuration = new Configuration();
    
    UserTypeUtil.registerUserTypes(configuration, createDomainValueUserTypes("com.qoomon.fancyapp.domainvalues"));
    UserTypeUtil.registerUserTypes(configuration, new BigMoneyUserType());
    UserTypeUtil.registerUserTypes(configuration, new MoneyUserType());
    UserTypeUtil.registerUserTypes(configuration, new InternetAddressUserType());
    
    StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder();
    serviceRegistryBuilder.applySetting(Environment.DATASOURCE, dataSource());
    ServiceRegistry serviceRegistry = serviceRegistryBuilder.build();
    SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
    return sessionFactory; 
}
//...

public List<DomainValueUserType> createDomainValueUserTypes(String domainValuePackage){
  List<DomainValueUserType> resultList = new LinkedList<>()
  ClassPathScanningCandidateComponentProvider componentProvider = new ClassPathScanningCandidateComponentProvider(false);
  // Filter to include only classes assignable to DV.class
  componentProvider.addIncludeFilter(new AssignableTypeFilter(DV.class));
  // Find classes in the given package (or subpackages)
  Set<BeanDefinition> beans = componentProvider.findCandidateComponents(domainValuePackage);
  for (BeanDefinition bean : beans) {
      // Create DomainValueUserType for domainValueType
      Class<? extends DV> domainValueType = Class.of(bean.getBeanClassName();
      resultList.add(new DomainValueUserType(domainValueType));
  }
  return resultList;
}

```
