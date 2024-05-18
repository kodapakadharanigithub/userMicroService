package com.hcl.usermicroservice.serviceimpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.hcl.usermicroservice.dto.BookingRequest;
import com.hcl.usermicroservice.dto.QueryResponse;
import com.hcl.usermicroservice.dto.Ride;
import com.hcl.usermicroservice.dto.UserBookings;
import com.hcl.usermicroservice.dto.Vehicle;
import com.hcl.usermicroservice.entities.QueryRequest;
import com.hcl.usermicroservice.entities.Users;
import com.hcl.usermicroservice.repository.QueryRequestRepository;
import com.hcl.usermicroservice.repository.UsersRepository;
import com.hcl.usermicroservice.service.UsersService;

@Service
public class UsersServiceImpl implements UsersService{
	
	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private  QueryRequestRepository queryRequestRepository;
	
	private  QueryRequest queryRequest;
	private  Vehicle vehicle;
	
	private static final String OWNER_URL="http://owner-ms/owner";
	
	@Override
	public List<Users> getAllUsers() {
		// TO get all the users
		return usersRepository.findAll();
	}
	
	
	@Override
	public Users userRegister(Users user) {
		// TO insert user 
		return usersRepository.save(user);
	}
	@Override
	public boolean getUserByName(String userName) {
		// TO check and get the user exist or not by id
		boolean flag=true;
		
		Users user = usersRepository.findByName(userName);
		if (user == null) {
			// if user not exist
			flag = false;
		}	
		return flag;
	}
	
	@Override
	public List<String> getAvailableVehicles(String type) {
		// TO get available vehicles based on type from the ownermicroservice url	
		String url=OWNER_URL+"/getAvailableVehicles/{type}";
		List<String> list = restTemplate.getForObject(url,List.class,type);	
		return list;
		
	}
	
	@Override
	public List<Ride> getRidesSortByPrice() {
		// TO get the rides by sorting the price
		String url=OWNER_URL+"/getRidesSortBasedOnPrice";
		List<Ride> list  = restTemplate.getForObject(url,List.class);	
		return list;
	}
	
	@Override
	public boolean bookVehicle(BookingRequest bookingRequest,int userId) {
		// TO book the vehicle
		bookingRequest.setUserId(userId);
		//to check the vehicle is available or not
		boolean flag=checkVehicle(bookingRequest);
		return flag;
	}
	
	public boolean checkVehicle(BookingRequest bookingRequest)
	{
		//to check vehicle is available for booking or not it will be checked by owner
		//for posting the object as Request Body
		HttpHeaders headers=new HttpHeaders();
		//set the header content type as Json
		headers.setContentType(MediaType.APPLICATION_JSON);
		//set the httpEntity as object type what u want to pass as request body
		HttpEntity<BookingRequest> requestEntity=new HttpEntity<>(bookingRequest,headers);
		String url=OWNER_URL+"/checkVehicle";
		return restTemplate.postForObject(url,requestEntity,Boolean.class);
	}
	
	@Override
	public boolean cancelVehicle(int bookingId,int userId) {
		// TO cancel the booked vehicle
		//to interact with owner's userBooking repository for cancelling the vehicle
		String url=OWNER_URL+"/cancelVehicle/{bookingId}/{userId}";
		Boolean flag = restTemplate.getForObject(url, Boolean.class,bookingId,userId);
		return flag;
		
	}
	@Override
	public List<UserBookings> getAllMyBookings(int userId) {
		// TO get all my bookings
		//to interact with owner's userBooking repository for getting user's bookings 
		String url=OWNER_URL+"/getAllMyBookings/{userId}";
		List<UserBookings> list = restTemplate.getForObject(url, List.class,userId);	
		return list;
	}
	@Override
	public UserBookings getConfirmedBookings(int userId,int bookingId) {
		// TO get confirmed bookings from the database
		//to interact with owner's userBooking repository for confirmed user's bookings 
		String url=OWNER_URL+"/getConfirmedBookings/{userId}/{bookingId}";
		UserBookings userBookings= restTemplate.getForObject(url, UserBookings.class,userId,bookingId);	
		return userBookings;		
	}
	
	@Override
	public String  DateConversion(String date) 
	{
		String  msg=null;
		//defining the format of string representation
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		try
		{
			
			//parsing the string to date
			dateFormat.parse(date);
		}
		catch(ParseException e)
		{
			msg="Date format is not correct check format should be=yyyy-MM-dd";
			return msg;
		}
		return msg;
	}
	
	@Override
	public String timeConversion(String time) 
	{
		String  msg=null;
		//defining the format of string representation
		SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm:ss");
		try
		{
			//parse the string to java.util.Date
			timeFormat.parse(time);
		}
		catch(ParseException e)
		{
			msg="Time format is not correct check format should be=HH:mm:ss";
			return msg;
		}
		return msg;
	}
	@Override
	public String checkDateTime(BookingRequest bookingRequest)
	{
		//to check bookingRequest date and time are correct or not
		String strDate=DateConversion(bookingRequest.getBookingDate());
		String strtime=timeConversion(bookingRequest.getBookingTime());
		
		if(strDate!=null)
		{
			return strDate;
		}
		else if(strtime!=null)
		{
			return strtime;
		}
		else
		{
			//2024-12-31  24:60:60
			int year=Integer.parseInt(bookingRequest.getBookingDate().substring(0, 4));
			int month=Integer.parseInt(bookingRequest.getBookingDate().substring(5,7));
			int date=Integer.parseInt(bookingRequest.getBookingDate().substring(8,10));
			int hour=Integer.parseInt(bookingRequest.getBookingTime().substring(0, 2));
			int min=Integer.parseInt(bookingRequest.getBookingTime().substring(3,5));
			int sec=Integer.parseInt(bookingRequest.getBookingTime().substring(6,8));
			if((year>=2024 && year<=3000) && (month>=1 && month<=12) && (date>=1 && date<=31))
			{
				if((hour>=1 && hour<=24) && (min>=0 && min<=59) && (sec>=0 && sec<=59))
				{
					return null;
				}
					
				else
				{
					return "Enter correct Time!!!";	
				}
				
			}
			else
			{
				return "Enter Correct Date!!!";
			}
		}
	}
	
	@Override
	public QueryRequest postQuery(int userId, String vehicleId, String query) {
		// TO post the query asked by user
		queryRequest=new QueryRequest(query,vehicleId,userId,"pending");
		String url=OWNER_URL+"/getVehicle/{vehicleId}";
		vehicle=restTemplate.getForObject(url,Vehicle.class,queryRequest.getVehicleId());
		if(vehicle!=null)
		{
			queryRequestRepository.save(queryRequest);
			return queryRequest;
		}
		else
		{
			return null;
		}
	}
	@Override
	public QueryResponse getQueryById(int queryId) {
		// TO get query By id
		String url=OWNER_URL+"/getQueryResponse/{queryId}";
		return restTemplate.getForObject(url, QueryResponse.class,queryId);
	}
	
	@Override
	public QueryResponse getRepliedAnswers(int userId,int queryId) {
		// TO get the answers for queries

		QueryRequest queryRequest=queryRequestRepository.findRepliedQueries("replied", userId,queryId);
		QueryResponse queryResponse=new QueryResponse();
		boolean flag=false;
		if (queryRequest!=null && queryRequest.getQueryId() == queryId) {
			// to interact with owner's QueryResponse Repository
			String url = OWNER_URL+"/getQueryResponse/{queryId}";
			queryResponse = restTemplate.getForObject(url, QueryResponse.class, queryId);
			flag = true;
		} else {
			flag = false;
		}
		
		if(flag==true)
		{
			return queryResponse;
		}
		else
		{
			return null;
		}
	}
	
	//these all are methods of ownerMicroservice for interaction

	@Override
	public List<QueryRequest> getPendingQueries(int ownerId) {
		// TO get the queries what are pending
		List<QueryRequest> list = queryRequestRepository.findQueries("pending");
		List<QueryRequest> list2=new ArrayList<>();
		boolean flag=true;
		if(list!=null)
		{
			for(QueryRequest queryRequest:list)
			{
				 String url=OWNER_URL+"/getVehicle/{vehicleId}";
				 vehicle=restTemplate.getForObject(url,Vehicle.class,queryRequest.getVehicleId());
				 if(vehicle!=null && vehicle.getOwner().getOwnerId()==ownerId)
				 {
					list2.add(queryRequest);
				 }
				 else
				 {
					flag=false;
				 }
			}
			return list2;
		}
		else
		{
			flag=false;
		}
		if(flag==false)
		{
			return null;
		}
		else
		{
			return list2;
		}
			
	}
	
	
	@Override
	public boolean replyToQuery(QueryResponse queryResponse) {
		//to save the query answer
		queryRequest=queryRequestRepository.findById(queryResponse.getQueryId()).orElse(null);
		if(queryRequest!=null && queryResponse.getQueryId()==queryRequest.getQueryId())
		{
			//if owner enters correct query details
			queryRequest.setStatus("replied");
			queryRequestRepository.save(queryRequest);
			String url=OWNER_URL+"/saveQueryResponse";
			HttpHeaders headers=new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<QueryResponse> requestEntity=new HttpEntity<>(queryResponse,headers);
			restTemplate.postForObject(url,requestEntity,Boolean.class);
			return true;
		}
		else
		{
			//if owner enters Incorrect query details
			return false;
		}
		
	}


	
	

}
