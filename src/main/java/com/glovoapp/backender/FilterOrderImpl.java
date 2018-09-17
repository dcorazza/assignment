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

	/**
	 * If the description of the order contains the words pizza, cake or flamingo,
	 * we can only show the order to the courier if they are equipped with a Glovo
	 * box.
	 *
	 * @param courier
	 * @param orders
	 * @return
	 */
	@Override
	public List<Order> filterBoxOnly(Courier courier, List<Order> orders) {
		List<Order> filtered = new ArrayList<Order>();
		orders.forEach((order) -> {
			if (configuration.getBoxOnlyDescription().stream().parallel()
					.anyMatch(s -> order.getDescription().toLowerCase().contains(s.toLowerCase()))) {
				if (courier.getBox()) {
					filtered.add(order);
				}
			} else {
				filtered.add(order);
			}
		});
		return filtered;
	}

	/**
	 * If the order is further than 5km to the courier, we will only show it to
	 * couriers that move in motorcycle or electric scooter.
	 * 
	 * @param courier
	 * @param orders
	 * @return
	 */
	@Override
	public List<Order> filterByDistance(Courier courier, List<Order> orders) {
		List<Order> filtered = new ArrayList<Order>();
		orders.forEach((order) -> {
			double totalDistance = DistanceCalculatorUtil.getTotalDistance(courier, order);
			if (totalDistance > configuration.getOrderDistanceFromCourier()) {
				if (courier.getVehicle().equals(Vehicle.ELECTRIC_SCOOTER)
						&& !courier.getVehicle().equals(Vehicle.MOTORCYCLE)) {
					filtered.add(order);
				}
			} else {
				filtered.add(order);
			}
		});
		return filtered;
	}

}
