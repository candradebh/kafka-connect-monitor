package com.kafka.connect.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.kafka.connect.entity.CustomerEntity;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long>
{

    CustomerEntity findByName(String name);

    List<CustomerEntity> findByNameContaining(String name);

}
