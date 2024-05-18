package com.hcl.usermicroservice.dto;

import java.util.ArrayList;
import java.util.List;

public class Owner {
	

	private Integer ownerId;

	private String  ownerName;

	private String passWord;

	private List<Vehicle> vehicleList=new ArrayList<>();
	
	public Owner()
	{
		
	}
	public Owner( String ownerName, String passWord) {
		super();

		this.ownerName = ownerName;
		this.passWord = passWord;
	}
	
	public Owner(Integer ownerId, String ownerName, String passWord, List<Vehicle> vehicleList) {
		super();
		this.ownerId = ownerId;
		this.ownerName = ownerName;
		this.passWord = passWord;
		this.vehicleList = vehicleList;
	}
	public Integer getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	@Override
	public String toString() {
		return "Owner [ownerId="  + ", ownerName=" + ownerName + ", passWord=" + passWord + "]";
	}
	
	
	

}
