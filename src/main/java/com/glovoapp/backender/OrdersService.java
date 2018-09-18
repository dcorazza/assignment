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

	/**
	 *
	 * @param filter
	 * @param sort
	 * @param configuration
	 * @param courierRepository
	 */
	@Autowired
	OrdersService(FilterOrder filter, SortOrder sort, ConfigurationFile configuration,
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

		orders = filter.filterBoxOnly(courier, orders);

		orders = filter.filterByDistance(courier, orders);

		orders = sortOrder(courier, orders);
		orders.forEach(System.out::println);

		return orders.stream().map(order -> new OrderVM(order.getId(), order.getDescription()))
				.collect(Collectors.toList());
	}

	/**
	 * Make sure we can configure the order in which the priorities are applied
	 * (e.g. we should be able to put VIP before food, or food before close orders
	 * just by changing the configuration)
	 * 
	 * @param courier
	 * @param orders
	 * @return
	 */
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
			}
		}
		return orders;
	}
}
