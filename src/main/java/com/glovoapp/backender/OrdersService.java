package com.glovoapp.backender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrdersService {

	@Autowired
	private OrdersFilter configuration;
	@Autowired
	private CourierRepository courierRepository;

	List<OrderVM> getFilteredOrders(OrderRepository orderRepository, String courierId) throws Exception {
		Courier courier = courierRepository.findById(courierId);

		List<Order> orders = orderRepository.findAll();

		System.out.println("orders-->" + orders.size());

		orders = filterBoxOnly(courier, orders);
		System.out.println("Boxonly-->" + orders.size());

		orders = filterByDistance(courier, orders);
		System.out.println("result-->" + orders.size());
		orders.forEach(System.out::println);

		orders = sortOrder(courier, orders);

		return orders.stream().map(order -> new OrderVM(order.getId(), order.getDescription()))
				.collect(Collectors.toList());
	}

	public List<Order> sortOrder(Courier courier, List<Order> orders) {

		for (String priority : configuration.getOrderPriority()) {
			switch (priority) {
			case "closer":
				orders = sortCloser(orders, courier);
				break;
			case "vip":
				orders = sortVip(orders);
				break;
			case "food":
				orders = sortFood(orders);
				break;
			default:
				continue;
			}
		}
		return orders;
	}

	private List<Order> sortFood(List<Order> orders) {
		Collections.sort(orders, new Comparator<Order>() {
			@Override
			public int compare(Order order1, Order order2) {
				return Boolean.compare(order2.getFood(), order1.getFood());
			}
		});
		return orders;
	}

	private List<Order> sortVip(List<Order> orders) {
		Collections.sort(orders, new Comparator<Order>() {
			@Override
			public int compare(Order order1, Order order2) {
				return Boolean.compare(order2.getVip(), order1.getVip());
			}
		});

		return orders;
	}

	private double calculateCloserDistance(Order order, Courier courier) {
		// Slots of 500 meters
		double divider = 2d * 500 / configuration.getCloserPriority();
		return (Math.ceil(getTotalDistance(courier, order) * 2) / divider) * 1000;
	}

	private List<Order> sortCloser(List<Order> orders, Courier courier) {
		orders.sort((o1, o2) -> Double.compare(calculateCloserDistance(o1, courier),
				(calculateCloserDistance(o2, courier))));
		System.out.println("Depois");
		orders.forEach(System.out::println);
		return orders;
	}

	private List<Order> filterBoxOnly(Courier courier, List<Order> orders) {
		List<Order> filtered = new ArrayList<Order>();
		orders.forEach((Order order) -> {
			String desc = order.getDescription().replace("\n", "").replace("\r", "");
			if (configuration.getBoxOnlyDescription().stream().parallel()
					.anyMatch(s -> desc.toLowerCase().contains(s.toLowerCase()))) {
				if (!courier.getBox()) {
					return;
				}
			}
			filtered.add(order);
		});
		return filtered;
	}

	private List<Order> filterByDistance(Courier courier, List<Order> orders) {
		List<Order> filtered = new ArrayList<Order>();
		orders.forEach((Order order) -> {

			double totalDistance = getTotalDistance(courier, order);
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

	private double getTotalDistance(Courier courier, Order order) {
		double distancePickUp = DistanceCalculator.calculateDistance(courier.getLocation(), order.getPickup());
		double distanceDelivery = DistanceCalculator.calculateDistance(order.getPickup(), order.getDelivery());
		double totalDistance = distancePickUp + distanceDelivery;
		return totalDistance;
	}
}
