package com.mpvaitheeswaran.ecommerce.user_service.config;

import com.mpvaitheeswaran.ecommerce.user_service.repository.OrderCheckDao;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class JdbiConfig {

    @Bean
    public Jdbi jdbi(DataSource dataSource) {
        Jdbi jdbi = Jdbi.create(dataSource);
        jdbi.installPlugin(new SqlObjectPlugin());
        return jdbi;
    }

    @Bean
    public OrderCheckDao orderCheckDao(Jdbi jdbi) {
        return jdbi.onDemand(OrderCheckDao.class);
    }

}
