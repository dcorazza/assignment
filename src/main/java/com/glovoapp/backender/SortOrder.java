package com.glovoapp.backender;

import java.util.List;

public interface SortOrder {

	List<Order> sortFood(List<Order> orders);
	
	List<Order> sortVip(List<Order> orders);
	
	List<Order> sortCloser(List<Order> orders, Courier courier);
	
}
