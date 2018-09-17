package com.glovoapp.backender;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("orders.filter")
public class ConfigurationFile {

    private double orderDistanceFromCourier;
    private List<String> boxOnlyDescription;
    private List<String> orderPriority;
    private double closerPriority;

    public List<String> getBoxOnlyDescription() {
        return boxOnlyDescription;
    }

    public void setBoxOnlyDescription(List<String> boxOnlyDescription) {
        this.boxOnlyDescription = boxOnlyDescription;
    }

    public List<String> getOrderPriority() {
        return orderPriority;
    }

    public void setOrderPriority(List<String> orderPriority) {
        this.orderPriority = orderPriority;
    }

    public double getCloserPriority() {
        return closerPriority;
    }

    public void setCloserPriority(double closerPriority) {
        this.closerPriority = closerPriority;
    }

    public double getOrderDistanceFromCourier() {
        return orderDistanceFromCourier;
    }

    public void setOrderDistanceFromCourier(double orderDistanceFromCourier) {
        this.orderDistanceFromCourier = orderDistanceFromCourier;
    }

}
