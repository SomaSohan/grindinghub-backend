package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.WorkOrder;
import java.util.List;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Integer> {
    List<WorkOrder> findByClient_UserId(int clientId);

    List<WorkOrder> findByFactory_FactoryId(int factoryId);
}
