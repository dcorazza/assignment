package com.glovoapp.backender;

import java.util.List;

public interface FilterOrder {

	List<Order> filterBoxOnly(Courier courier, List<Order> orders);

	List<Order> filterByDistance(Courier courier, List<Order> orders);
}
