package com.hcl.usermicroservice.service;

import java.text.ParseException;
import java.util.List;

import com.hcl.usermicroservice.dto.BookingRequest;
import com.hcl.usermicroservice.dto.QueryResponse;
import com.hcl.usermicroservice.dto.Ride;
import com.hcl.usermicroservice.dto.UserBookings;
import com.hcl.usermicroservice.entities.QueryRequest;
import com.hcl.usermicroservice.entities.Users;



public interface UsersService {
	
	public List<Users> getAllUsers();
	public Users userRegister(Users user);
	public boolean getUserByName(String userName);
	public List<String> getAvailableVehicles(String type);
	public List<Ride> getRidesSortByPrice();
	public boolean bookVehicle(BookingRequest bookingRequest,int userId);
	public boolean cancelVehicle(int booking_id,int id);
	public List<UserBookings> getAllMyBookings(int user_id);
	public String  DateConversion(String date) throws ParseException;
	public String timeConversion(String time) throws ParseException;
	public String checkDateTime(BookingRequest bookingRequest);
	public UserBookings getConfirmedBookings(int userId,int bookingId);
	public QueryRequest postQuery(int userId,String vehicleId,String query);
	public QueryResponse getRepliedAnswers(int userId,int queryId);
	public QueryResponse getQueryById(int queryId);
	
	//these are methods used for owner Microservice interaction with userMicroservice
	public List<QueryRequest> getPendingQueries(int ownerId);
	public boolean replyToQuery(QueryResponse queryResponse);


}
