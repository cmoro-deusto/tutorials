package org.baeldung.spring.persistence.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan({ "org.baeldung.spring.persistence.dao", "org.baeldung.spring.persistence.service" })
public class PersistenceConfig {

    @Autowired
    private Environment env;

    public PersistenceConfig() {
        super();
    }

    @Bean
    public AnnotationSessionFactoryBean alertsSessionFactory() {
        final AnnotationSessionFactoryBean sessionFactory = new AnnotationSessionFactoryBean();
        sessionFactory.setDataSource(restDataSource());
        sessionFactory.setPackagesToScan(new String[] { "org.rest" });
        sessionFactory.setHibernateProperties(hibernateProperties());

        return sessionFactory;
    }

    @Bean
    public DataSource restDataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
        dataSource.setUrl(env.getProperty("jdbc.url"));
        dataSource.setUsername(env.getProperty("jdbc.user"));
        dataSource.setPassword(env.getProperty("jdbc.pass"));

        return dataSource;
    }

    @Bean
    public HibernateTransactionManager transactionManager() {
        final HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(alertsSessionFactory().getObject());

        return txManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    final Properties hibernateProperties() {
        return new Properties() {
            {
                setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
                // setProperty("hibernate.globally_quoted_identifiers", "true");
                // note: necessary in launchpad-storage, but causing problems here
            }
        };
    }
}