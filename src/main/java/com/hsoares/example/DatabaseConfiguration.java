package com.hsoares.example;

import java.beans.PropertyVetoException;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * Created by hevilavio on 9/22/15.
 */
@Configuration
public class DatabaseConfiguration {

    @Autowired
    private Environment env;

    @Value("${connection.pool.maxPoolSize:10}")
    private int maxPoolSize;

    @Value("${connection.pool.minPoolSize:1}")
    private int minPoolSize;

    @Value("${connection.pool.acquireIncrement:1}")
    private int acquireIncrement;

    @Value("${connection.pool.test.connection.on.checkin:false}")
    private boolean testConnectionOnCheckin;

    @Value("${connection.pool.preferred.testquery:SELECT 1 FROM DUAL}")
    private String preferredTestQuery;

    @Value("${connection.pool.idle.test.period:120}")
    private int idleConnectionTestPeriodInSeconds;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Value("${spring.datasource.password}")
    private String password;

    /**
     * TODO hevilavio - remover config de hsql db
     * */
    @Bean
    public DataSource dataSource() throws PropertyVetoException {

        final ComboPooledDataSource dataSource = new ComboPooledDataSource();

        dataSource.setJdbcUrl(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        dataSource.setDriverClass(driverClassName);
        dataSource.setMaxPoolSize(maxPoolSize);
        dataSource.setMinPoolSize(minPoolSize);
        dataSource.setAcquireIncrement(acquireIncrement);
        dataSource.setTestConnectionOnCheckin(testConnectionOnCheckin);
        dataSource.setPreferredTestQuery(preferredTestQuery);
        dataSource.setIdleConnectionTestPeriod(idleConnectionTestPeriodInSeconds);

        return dataSource;
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() throws PropertyVetoException {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
            vendorAdapter.setDatabase(Database.ORACLE);

        final Boolean showSql = env.getProperty("db.show.sql", Boolean.class);
        if (showSql != null) {
            vendorAdapter.setShowSql(showSql);
        }

        final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        //factory.setPackagesToScan(DATA_PACKAGE);
        factory.setDataSource(dataSource());
        //factory.setPersistenceUnitName(PERSISTENCE_UNIT_PS_SWITCH);
        factory.afterPropertiesSet();

        return factory.getObject();
    }

//    @Bean
//    public PlatformTransactionManager transactionManager(final DataSource dataSource) {
//        return new DataSourceTransactionManager(dataSource);
//    }

    @Bean
    public PlatformTransactionManager transactionManager(final EntityManagerFactory emf) throws PropertyVetoException {

        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(emf);
        txManager.setDataSource(dataSource());
        txManager.setDefaultTimeout(30);

        return txManager;
    }

}
