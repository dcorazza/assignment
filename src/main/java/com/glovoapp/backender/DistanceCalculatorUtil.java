package com.glovoapp.backender;

public class DistanceCalculatorUtil {

	public static double calculateCloserDistance(Order order, Courier courier, double closerPriority) {
		// Slots of 500 meters
		double divider = 2d * 500 / closerPriority;
		return (Math.ceil(getTotalDistance(courier, order) * 2) / divider) * 1000;
	}

	public static double getTotalDistance(Courier courier, Order order) {
		double distancePickUp = DistanceCalculator.calculateDistance(courier.getLocation(), order.getPickup());
		double distanceDelivery = DistanceCalculator.calculateDistance(order.getPickup(), order.getDelivery());
		double totalDistance = distancePickUp + distanceDelivery;
		return totalDistance;
	}
}
