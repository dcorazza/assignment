package com.glovoapp.backender;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FilterOrderImpl implements FilterOrder {

	private final ConfigurationFile configuration;

	@Autowired
	FilterOrderImpl(ConfigurationFile configuration) {
		this.configuration = configuration;
	}

	public List<Order> filterBoxOnly(Courier courier, List<Order> orders) {
		List<Order> filtered = new ArrayList<Order>();
		orders.forEach((Order order) -> {
			if (configuration.getBoxOnlyDescription().stream().parallel()
					.anyMatch(s -> order.getDescription().toLowerCase().contains(s.toLowerCase()))) {
				if (!courier.getBox()) {
					return;
				}
			}
			filtered.add(order);
		});
		return filtered;
	}

	public List<Order> filterByDistance(Courier courier, List<Order> orders) {
		List<Order> filtered = new ArrayList<Order>();
		orders.forEach((Order order) -> {

			double totalDistance = DistanceCalculatorUtil.getTotalDistance(courier, order);
			if (totalDistance > configuration.getPickupDistance()) {
				if (!courier.getVehicle().equals(Vehicle.ELECTRIC_SCOOTER)
						&& !courier.getVehicle().equals(Vehicle.MOTORCYCLE)) {
					return;
				}
			}
			filtered.add(order);
		});
		return filtered;
	}

}
