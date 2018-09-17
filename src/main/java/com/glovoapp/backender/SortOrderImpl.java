package com.glovoapp.backender;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SortOrderImpl implements SortOrder {

	private final ConfigurationFile configuration;

	@Autowired
	SortOrderImpl(ConfigurationFile configuration) {
		this.configuration = configuration;
	}

	@Override
	public List<Order> sortFood(List<Order> orders) {
		Collections.sort(orders, new Comparator<Order>() {
			@Override
			public int compare(Order order1, Order order2) {
				return Boolean.compare(order2.getFood(), order1.getFood());
			}
		});
		return orders;
	}

	@Override
	public List<Order> sortVip(List<Order> orders) {
		Collections.sort(orders, new Comparator<Order>() {
			@Override
			public int compare(Order order1, Order order2) {
				return Boolean.compare(order2.getVip(), order1.getVip());
			}
		});

		return orders;
	}

	@Override
	public List<Order> sortCloser(List<Order> orders, Courier courier) {
		orders.sort((o1, o2) -> Double.compare(
				DistanceCalculatorUtil.calculateCloserDistance(o1, courier, configuration.getCloserPriority()),
				(DistanceCalculatorUtil.calculateCloserDistance(o2, courier, configuration.getCloserPriority()))));
		System.out.println("Depois");
		orders.forEach(System.out::println);
		return orders;

	}

}
