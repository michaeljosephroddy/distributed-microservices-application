package com.example.customer_service.model;

public class CustomerDTO {
    private Long id;
    private String name;
    private String email;
    private Integer totalOrders;

    public CustomerDTO() {
    }

    public CustomerDTO(Long id, String name, String email, Integer totalOrders) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.totalOrders = totalOrders;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    @Override
    public String toString() {
        return "CustomerDTO [toString()=" + super.toString() + "]";
    }
}
