package com.glovoapp.backender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@Controller
@ComponentScan("com.glovoapp.backender")
@EnableAutoConfiguration
class API {

	private final String welcomeMessage;
	private final OrderRepository orderRepository;

	@Autowired
	private OrdersFilter configuration;

	private final CourierRepository courierRepository;

	@Autowired
	API(@Value("${backender.welcome_message}") String welcomeMessage, OrderRepository orderRepository,
			CourierRepository courierRepository) {
		this.welcomeMessage = welcomeMessage;
		this.orderRepository = orderRepository;
		this.courierRepository = courierRepository;
	}

	@RequestMapping("/")
	@ResponseBody
	String root() {
		return welcomeMessage;
	}

	@RequestMapping("/orders")
	@ResponseBody
	List<OrderVM> orders() {
		return orderRepository.findAll().stream().map(order -> new OrderVM(order.getId(), order.getDescription()))
				.collect(Collectors.toList());
	}

	@RequestMapping("/orders/{courierId}")
	@ResponseBody
	Map<Boolean, List<OrderedOrderVM>> orders(@PathVariable String courierId) {
		Courier courier = courierRepository.findById(courierId);
		List<Order> filter = new ArrayList<>();
		List<OrderedOrderVM> result = new ArrayList<>();
		List<Order> orders = orderRepository.findAll();
		System.out.println("orders-->" + orders.size());
		orders.forEach((Order order) -> {
			String desc = order.getDescription().replace("\n", "").replace("\r", "");
			if (configuration.getBoxOnlyDescription().stream().parallel()
					.anyMatch(s -> desc.toLowerCase().contains(s.toLowerCase()))) {
				if (!courier.getBox()) {
					return;
				}
			}
			filter.add(order);
		});
		System.out.println("Boxonly-->" + filter.size());

		filter.forEach((Order order) -> {
			double dist = DistanceCalculator.calculateDistance(courier.getLocation(), order.getPickup());
			if (dist > configuration.getPickupDistance()) {
				if (!courier.getVehicle().equals(Vehicle.ELECTRIC_SCOOTER)
						&& !courier.getVehicle().equals(Vehicle.MOTORCYCLE)) {
					return;
				}
			}
			result.add(new OrderedOrderVM(order.getId(), order.getDescription(), dist));
		});

		System.out.println("result-->" + result.size());
		result.forEach(System.out::println);
		result.sort((o1, o2) -> o1.getDistance().compareTo(o2.getDistance()));
		Map<Boolean, List<OrderedOrderVM>> partitioned = result.stream()
				.collect(Collectors.partitioningBy(p -> p.getDistance() >= configuration.getCloserPriority()));

		System.out.println("Depois");
		result.forEach(System.out::println);

		System.out.println("Depois 2");
		partitioned.forEach((a, b) -> {
			b.forEach(System.out::println);
		});

		return partitioned;
	}

	public static void main(String[] args) {
		SpringApplication.run(API.class, args);
	}
}
