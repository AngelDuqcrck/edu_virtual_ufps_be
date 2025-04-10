package com.sistemas_mangager_be.edu_virtual_ufps.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.*;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;

// @Configuration
// @EnableJpaRepositories(
//     basePackages = "com.sistemas_mangager_be.edu_virtual_ufps.repositoriesOracle",
//     entityManagerFactoryRef = "oracleEntityManagerFactory",
//     transactionManagerRef = "oracleTransactionManager"
// )
// @EntityScan(basePackages = "com.sistemas_mangager_be.edu_virtual_ufps.entitiesOracle")
public class OracleDataSourceConfig {

    // @Bean(name = "oracleDataSource")
    // @ConfigurationProperties(prefix = "oracle.datasource")
    // public DataSource oracleDataSource() {
    //     return DataSourceBuilder.create().build();
    // }

    // @Bean(name = "oracleEntityManagerFactory")
    // public LocalContainerEntityManagerFactoryBean oracleEntityManagerFactory(
    //         EntityManagerFactoryBuilder builder,
    //         @Qualifier("oracleDataSource") DataSource dataSource) {

    //     return builder
    //             .dataSource(dataSource)
    //             .packages("com.sistemas_mangager_be.edu_virtual_ufps.entitiesOracle")
    //             .persistenceUnit("oracle")
    //             .build();
    // }

    // @Bean(name = "oracleTransactionManager")
    // public PlatformTransactionManager oracleTransactionManager(
    //         @Qualifier("oracleEntityManagerFactory") EntityManagerFactory entityManagerFactory) {

    //     return new JpaTransactionManager(entityManagerFactory);
    // }
}
