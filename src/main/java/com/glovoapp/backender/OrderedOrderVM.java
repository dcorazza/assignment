package com.glovoapp.backender;

public class OrderedOrderVM extends OrderVM {

	private Double distance;

	public OrderedOrderVM(String id, String description, Double distance) {
		super(id, description);
		this.distance = distance;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	@Override
	public String toString() {
		return "OrderedOrderVM{" + "id='" + getId() + '\'' + ", description='" + getDescription() + '\'' + ", distance="
				+ distance + '}';
	}
}
