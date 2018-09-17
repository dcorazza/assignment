package com.glovoapp.backender;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrdersService {

	private final FilterOrder filter;

	private final SortOrder sort;

	private final ConfigurationFile configuration;

	private final CourierRepository courierRepository;

	@Autowired
	public OrdersService(FilterOrder filter, SortOrder sort, ConfigurationFile configuration,
			CourierRepository courierRepository) {
		super();
		this.filter = filter;
		this.sort = sort;
		this.configuration = configuration;
		this.courierRepository = courierRepository;
	}

	List<OrderVM> getFilteredOrders(OrderRepository orderRepository, String courierId) {

		Courier courier = courierRepository.findById(courierId);
		if (courier == null) {
			throw new UserNotFoundException(String.format("Courier %s not found", courierId));
		}
		List<Order> orders = orderRepository.findAll();

		System.out.println("orders-->" + orders.size());

		orders = filter.filterBoxOnly(courier, orders);
		System.out.println("Boxonly-->" + orders.size());

		orders = filter.filterByDistance(courier, orders);
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
				orders = sort.sortCloser(orders, courier);
				break;
			case "vip":
				orders = sort.sortVip(orders);
				break;
			case "food":
				orders = sort.sortFood(orders);
				break;
			default:
				continue;
			}
		}
		return orders;
	}
}
