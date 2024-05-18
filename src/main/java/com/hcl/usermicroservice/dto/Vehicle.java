package com.hcl.usermicroservice.dto;



public class Vehicle {

	private String vehicleId;
	private String type;
	private String model;
	private Owner owner;
	public Vehicle()
	{
		
	}
	public Vehicle(String vehicleId, String type, String model, Owner owner) {
		super();
		this.vehicleId = vehicleId;
		this.type = type;
		this.model = model;
		this.owner = owner;
	}

	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		return "Vehicle [vehicleId=" + vehicleId + ", type=" + type + ", model=" + model  + "]";
	}
	
}
