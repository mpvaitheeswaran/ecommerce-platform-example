package com.mpvaitheeswaran.ecommerce.user_service.repository;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.UUID;

@RegisterBeanMapper(Long.class)
public interface OrderCheckDao {

    @SqlQuery("""
        SELECT COUNT(*) 
        FROM orders 
        WHERE user_id = :userId 
        AND status IN ('CREATED','PROCESSING','SHIPPED')
    """)
    Long countActiveOrders(@Bind("userId") UUID userId);
}
