package org.example;

import lombok.Data;

@Data
public class Apartment {
    private int id;
    private String region;
    private Address address;
    private double area;
    private int bedrooms;
    private int price;

    public Apartment(int id, String region, Address address, double area, int bedrooms, int price) {
        this.id = id;
        this.region = region;
        this.address = address;
        this.area = area;
        this.bedrooms = bedrooms;
        this.price = price;
    }

    @Override
    public String toString() {
        return "ID_" + id +
                ", " + region + " region, " +
                address.toString() +
                ", " + area + "m²" +
                ", " + bedrooms + "-bedroom" +
                ", " + price + "$";
    }
}
