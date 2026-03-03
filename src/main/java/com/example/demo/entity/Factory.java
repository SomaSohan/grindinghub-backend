package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "factories")
public class Factory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int factoryId;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "factory_name")
    private String factoryName;

    private String address;
    private String city;
    private String state;

    private double latitude;
    private double longitude;

    private String description;

    @Column(name = "search_appearances", columnDefinition = "integer default 0", nullable = false)
    private int searchAppearances = 0;

    @Column(name = "contact_clicks", columnDefinition = "integer default 0", nullable = false)
    private int contactClicks = 0;

    // Default Constructor
    public Factory() {
    }

    // Getters and Setters

    public int getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(int factoryId) {
        this.factoryId = factoryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSearchAppearances() {
        return searchAppearances;
    }

    public void setSearchAppearances(int searchAppearances) {
        this.searchAppearances = searchAppearances;
    }

    public int getContactClicks() {
        return contactClicks;
    }

    public void setContactClicks(int contactClicks) {
        this.contactClicks = contactClicks;
    }
}