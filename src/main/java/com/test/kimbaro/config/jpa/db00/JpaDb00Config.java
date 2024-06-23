package com.test.kimbaro.config.jpa.db00;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = {"com.test.kimbaro.db.db00.repo"},
        entityManagerFactoryRef = "db00EntityManager",
        transactionManagerRef = "db00TransactionManager"
)
public class JpaDb00Config {
    @Primary
    @Bean(name = "db00TransactionManager")
    public PlatformTransactionManager db00TransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(db00EntityManager().getObject());
        return transactionManager;
    }

    @Primary
    @Bean(name = "db00EntityManager")
    public LocalContainerEntityManagerFactoryBean db00EntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(db00DataSource());
        em.setPackagesToScan("com.test.kimbaro.db.db00.entity");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        return em;
    }

    @Primary
    @Bean(name = "db00DataSource")
    @ConfigurationProperties(prefix = "spring.datasources.sql.db00")
    public DataSource db00DataSource() {
        return DataSourceBuilder.create().build();
    }
}