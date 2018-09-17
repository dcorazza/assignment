package com.glovoapp.backender;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = API.class)
class OrderServiceTest {

	@Autowired
	private FilterOrder filter;

	@Autowired
	private SortOrder sort;

	@Autowired
	private ConfigurationFile configuration;

	@Autowired
	private CourierRepository courierRepository;

	@Test
	void filterByDistance() {
		List<Order> expected = new OrderRepository().findAll();
		Courier courier = new CourierRepository().findById("courier-1");
		List<Order> result = new FilterOrderImpl(configuration).filterByDistance(courier, expected);
		assertEquals(expected, result);
	}

	@Test
	void sortByPriority() {
		List<Order> orders = new OrderRepository().findAll();
		Courier courier = new CourierRepository().findById("courier-6dfd0ba53155");

		List<Order> expected = new ArrayList<Order>();
		expected.add(new Order().withId("order-204e551b1518").withDescription("Envelope").withFood(true).withVip(true)
				.withPickup(new Location(41.40164715388642, 2.166349824536057))
				.withDelivery(new Location(41.40071490938333, 2.168594577858919)));

		expected.add(new Order().withId("order-412bc658ecb8")
				.withDescription("1x Hot dog with Fries\n2x Kebab with Fries\nChocolate cake").withFood(true)
				.withVip(false).withPickup(new Location(41.38412925150105, 2.1870953755511464))
				.withDelivery(new Location(41.39265932307547, 2.1743998837459806)));

		expected.add(new Order().withId("order-1").withDescription("I want a pizza cut into very small slices")
				.withFood(true).withVip(false).withPickup(new Location(41.3965463, 2.1963997))
				.withDelivery(new Location(41.407834, 2.1675979)));

		expected.add(new Order().withId("order-e8117c343f38")
				.withDescription("2x Pork bao with Fries\n1x Kebab with Fries\n1x Hot dog with Salad").withFood(false)
				.withVip(false).withPickup(new Location(41.407023334350875, 2.166170976747128))
				.withDelivery(new Location(41.39521063717707, 2.169669100704417)));

		List<Order> result = new OrdersService(filter, sort, configuration, courierRepository).sortOrder(courier,
				orders);
		assertEquals(expected, result);
	}

	@Test
	void filterBoxOnly() {
		List<Order> expected = new ArrayList<Order>();
		expected.add(new Order().withId("order-1").withDescription("I want a pizza cut into very small slices")
				.withFood(true).withVip(false).withPickup(new Location(41.3965463, 2.1963997))
				.withDelivery(new Location(41.407834, 2.1675979)));
		Courier courier = new CourierRepository().findById("courier-1");
		List<Order> result = new FilterOrderImpl(configuration).filterBoxOnly(courier, expected);
		assertEquals(expected, result);
	}

	@Test
	void filterBoxOnlyWithoutBox() {
		List<Order> orders = new OrderRepository().findAll();
		Courier courier = new CourierRepository().findById("courier-6dfd0ba53155");
		List<Order> expected = new ArrayList<Order>();
		expected.add(new Order().withId("order-204e551b1518").withDescription("Envelope").withFood(true).withVip(true)
				.withPickup(new Location(41.40164715388642, 2.166349824536057))
				.withDelivery(new Location(41.40071490938333, 2.168594577858919)));
		expected.add(new Order().withId("order-e8117c343f38")
				.withDescription("2x Pork bao with Fries\n1x Kebab with Fries\n1x Hot dog with Salad").withFood(false)
				.withVip(false).withPickup(new Location(41.407023334350875, 2.166170976747128))
				.withDelivery(new Location(41.39521063717707, 2.169669100704417)));

		List<Order> result = new FilterOrderImpl(configuration).filterBoxOnly(courier, orders);
		assertEquals(expected, result);

	}

}
