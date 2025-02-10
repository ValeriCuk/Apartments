package org.example;

import lombok.Data;

import java.util.Arrays;

@Data
public class Address {
    private String city;
    private String street;
    private Integer bNum;
    private Integer apNum;

    public Address(){}

    public Address(String city, String street, Integer bNum, Integer apNum) {
        this.city = city;
        this.street = street;
        this.bNum = bNum;
        this.apNum = apNum;
    }

    @Override
    public String toString() {
        return city +
                ", " + bNum + " " + street +
                ", Apt " + apNum;
    }

    public void fromString(String addressString) {
        String[] parts = addressString.split(", ");
        String city = parts[0].trim();
        this.city = city;

        String streetAndNumber = parts[1];
        String[] streetParts = streetAndNumber.split(" ");
        String street = streetParts[1];
        int houseNumber = Integer.parseInt(streetParts[0]);
        this.street = street;
        this.bNum = houseNumber;

        int apartmentNumber = 0;
        if (parts.length > 2) {
            String apartmentPart = parts[2].trim();
            apartmentNumber = Integer.parseInt(apartmentPart.replaceAll("\\D", ""));
            this.apNum = apartmentNumber;
        }
    }
}
