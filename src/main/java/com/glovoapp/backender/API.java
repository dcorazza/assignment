package com.glovoapp.backender;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ComponentScan("com.glovoapp.backender")
@EnableAutoConfiguration
class API {

    private final String welcomeMessage;
    private final OrderRepository orderRepository;
    private final OrdersService ordersService;

    @Autowired
    API(@Value("${backender.welcome_message}") String welcomeMessage, OrderRepository orderRepository,
            OrdersService ordersService) {
        this.welcomeMessage = welcomeMessage;
        this.orderRepository = orderRepository;
        this.ordersService = ordersService;

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
    List<OrderVM> orders(@PathVariable String courierId) {
        return ordersService.getFilteredOrders(orderRepository, courierId);
    }

    public static void main(String[] args) {
        SpringApplication.run(API.class, args);
    }
}
