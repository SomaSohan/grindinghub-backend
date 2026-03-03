package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entity.WorkOrder;
import com.example.demo.repository.WorkOrderRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.FactoryRepository;
import com.example.demo.repository.GrindingServiceRepository;
import com.example.demo.entity.User;
import com.example.demo.entity.Factory;
import com.example.demo.entity.GrindingService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/work-orders")
public class WorkOrderController {

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FactoryRepository factoryRepository;

    @Autowired
    private GrindingServiceRepository grindingServiceRepository;

    // 🔨 CREATE: Client requests a service
    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping
    public ResponseEntity<?> createWorkOrder(@RequestBody Map<String, Object> payload) {

        int clientId = (int) payload.get("clientId");
        int factoryId = (int) payload.get("factoryId");
        int serviceId = (int) payload.get("serviceId");
        double weightInTons = Double.parseDouble(payload.get("weightInTons").toString());
        String preferredDate = (String) payload.get("preferredDate");

        Optional<User> clientOpt = userRepository.findById(clientId);
        Optional<Factory> factoryOpt = factoryRepository.findById(factoryId);
        Optional<GrindingService> serviceOpt = grindingServiceRepository.findById(serviceId);

        if (clientOpt.isEmpty() || factoryOpt.isEmpty() || serviceOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid IDs provided.");
        }

        WorkOrder order = new WorkOrder();
        order.setClient(clientOpt.get());
        order.setFactory(factoryOpt.get());
        order.setService(serviceOpt.get());
        order.setWeightInTons(weightInTons);
        order.setPreferredDate(preferredDate);
        order.setStatus("PENDING");

        WorkOrder savedOrder = workOrderRepository.save(order);
        return ResponseEntity.ok(savedOrder);
    }

    // 👀 VIEW: Client gets their orders
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<WorkOrder>> getClientOrders(@PathVariable int clientId) {
        return ResponseEntity.ok(workOrderRepository.findByClient_UserId(clientId));
    }

    // 👀 VIEW: Factory gets their queue
    @PreAuthorize("hasRole('FACTORY')")
    @GetMapping("/factory/{userId}")
    public ResponseEntity<List<WorkOrder>> getFactoryOrders(@PathVariable int userId) {
        List<Factory> factories = factoryRepository.findByUserId(userId);
        if (factories.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        int factoryId = factories.get(0).getFactoryId();
        return ResponseEntity.ok(workOrderRepository.findByFactory_FactoryId(factoryId));
    }

    // ✏️ UPDATE: Factory updates the status
    @PreAuthorize("hasRole('FACTORY')")
    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable int orderId, @RequestBody Map<String, String> payload) {
        Optional<WorkOrder> optionalOrder = workOrderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        WorkOrder order = optionalOrder.get();
        order.setStatus(payload.get("status"));

        WorkOrder updatedOrder = workOrderRepository.save(order);
        return ResponseEntity.ok(updatedOrder);
    }

    // ❌ DELETE: General cleanup (Admin or self)
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT', 'FACTORY')")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable int orderId) {
        if (!workOrderRepository.existsById(orderId)) {
            return ResponseEntity.notFound().build();
        }
        workOrderRepository.deleteById(orderId);
        return ResponseEntity.ok(Map.of("message", "Deleted Successfully"));
    }
}
