package com.sistemas_mangager_be.edu_virtual_ufps.config;

// import org.springframework.beans.factory.annotation.Qualifier;
// import org.springframework.boot.autoconfigure.domain.EntityScan;
// import org.springframework.boot.context.properties.ConfigurationProperties;
// import org.springframework.boot.jdbc.DataSourceBuilder;
// import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
// import org.springframework.context.annotation.*;
// import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
// import org.springframework.orm.jpa.*;
// import org.springframework.transaction.PlatformTransactionManager;
// import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
// import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

// import java.util.HashMap;
// import jakarta.persistence.EntityManagerFactory;
// import javax.sql.DataSource;

// @Configuration
// @EnableJpaRepositories(
//     basePackages = "com.sistemas_mangager_be.edu_virtual_ufps.repositories",
//     entityManagerFactoryRef = "mysqlEntityManagerFactory",
//     transactionManagerRef = "mysqlTransactionManager"
// )
// @EntityScan(basePackages = "com.sistemas_mangager_be.edu_virtual_ufps.entities")
public class MySQLDataSourceConfig {

    

    // @Bean(name = "mysqlDataSource")
    // @ConfigurationProperties(prefix = "spring.datasource") 
    // public DataSource mysqlDataSource() {
    //     return DataSourceBuilder.create().build();
    // }

    // @Bean(name = "mysqlEntityManagerFactory")
    // public LocalContainerEntityManagerFactoryBean mysqlEntityManagerFactory(
    //         EntityManagerFactoryBuilder builder,
    //         @Qualifier("mysqlDataSource") DataSource dataSource) {
    //     return builder
    //             .dataSource(dataSource)
    //             .packages("com.sistemas_mangager_be.edu_virtual_ufps.entities") 
    //             .persistenceUnit("mysql")
    //             .build();
    // }

    // @Bean(name = "mysqlTransactionManager")
    // public PlatformTransactionManager mysqlTransactionManager(
    //         @Qualifier("mysqlEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
    //     return new JpaTransactionManager(entityManagerFactory);
    // }
}
