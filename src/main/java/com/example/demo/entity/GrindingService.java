package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "grinding_services")
public class GrindingService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private int serviceId;

    @ManyToOne
    @JoinColumn(name = "factory_id", nullable = false)
    private Factory factory;

    @Column(name = "machine_name")
    private String machineName;

    @Column(name = "material_name")
    private String materialName;

    @Column(name = "time_per_quintal")
    private double timePerQuintal;

    @Column(name = "price_per_kg")
    private double pricePerKg;

    // Constructors
    public GrindingService() {}

    // Getters & Setters
    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public Factory getFactory() {
        return factory;
    }

    public void setFactory(Factory factory) {
        this.factory = factory;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public double getTimePerQuintal() {
        return timePerQuintal;
    }

    public void setTimePerQuintal(double timePerQuintal) {
        this.timePerQuintal = timePerQuintal;
    }

    public double getPricePerKg() {
        return pricePerKg;
    }

    public void setPricePerKg(double pricePerKg) {
        this.pricePerKg = pricePerKg;
    }
}