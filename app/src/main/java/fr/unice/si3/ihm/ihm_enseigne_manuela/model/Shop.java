package fr.unice.si3.ihm.ihm_enseigne_manuela.model;

import android.support.annotation.NonNull;

import java.util.Comparator;

/**
 * Created by user on 07/05/2017.
 */

public class Shop implements Comparable<Shop> {
    private String city;
    private String cityCode;
    private String address;
    private String phoneNumber;
    private int revenue;
    private int numberOfEmployees;

    public Shop(String city, String cityCode, String address, String phoneNumber, int revenue, int numberOfEmployees) {
        this.city = city;
        this.cityCode = cityCode;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.revenue = revenue;
        this.numberOfEmployees = numberOfEmployees;
    }

    public String getName() {
        return "To Be Or To Have " + getCity();
    }

    public String getCity() {
        return city;
    }

    public String getCityCode() {
        return cityCode;
    }

    public String getAddress() {
        return address;
    }

    public String getFullAddress() {
        return cityCode + " " + city + ", " + address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getRevenue() {
        return revenue;
    }

    public int getNumberOfEmployees() {
        return numberOfEmployees;
    }


    @Override
    public int compareTo(@NonNull Shop shop) {
        return 1;
    }

//    public static class Comparators {
//        public static final Comparator<Shop> REVENUE = (Shop o1, Shop o2) -> o1.get.compareTo(o2.name);
//        public static final Comparator<Shop> EMPLOYEES = (Shop o1, Shop o2) -> Integer.compare(o1.age, o2.age);
//    }
}
