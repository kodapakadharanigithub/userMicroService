package com.hcl.usermicroservice.controller;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcl.usermicroservice.dto.BookingRequest;
import com.hcl.usermicroservice.dto.LoginRequest;
import com.hcl.usermicroservice.dto.QueryResponse;
import com.hcl.usermicroservice.dto.Ride;
import com.hcl.usermicroservice.dto.UserBookings;
import com.hcl.usermicroservice.entities.QueryRequest;
import com.hcl.usermicroservice.entities.Users;
import com.hcl.usermicroservice.exceptions.NoAvailableVehiclesException;
import com.hcl.usermicroservice.exceptions.NoRidesAvailableException;
import com.hcl.usermicroservice.exceptions.UserAlreadyExistOrNotException;
import com.hcl.usermicroservice.exceptions.VehicleAlreadyExistOrNotException;
import com.hcl.usermicroservice.service.UsersService;
import com.hcl.usermicroservice.util.JwtUtil;

import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UsersService usersService;
	@Autowired
	private JwtUtil jwtUtil;
	
	Logger logger=LoggerFactory.getLogger(UserController.class);

	int id;
	@Value("${user.uName}")
	private String uName;

	private int uId;

	@Value("${user.uPass}")
	private String uPass;
	 
	@GetMapping("/addUserThroughCloud")
	public ResponseEntity<?> getData() throws UserAlreadyExistOrNotException{
		Users users =new Users(uId,uName,uPass);
		boolean flag=usersService.getUserByName(users.getUserName());
		if(flag==false)
		{
			usersService.userRegister(users);
			return new ResponseEntity<>("User added through cloud",HttpStatus.OK);
		}
		else
		{
			throw new UserAlreadyExistOrNotException("User can't added through cloud because Already exist");
		}
	}
	
	@PostMapping("/login")
	public String userLogin(@RequestBody LoginRequest loginRequest)
	{
		//to get all the user details
		List<Users> list = usersService.getAllUsers();
		boolean flag=false;
		if(list.isEmpty())
		{
			//if the list is null then there are no users registered
			logger.info("OOPS!!There are no users");
			return "OOPS!!There are no users";
		}
		else
		{
			//if users list is not null
			for(Users u:list)
			{
				//to check whether user crendentials are valid or not
				if(loginRequest.getUserName().equals(u.getUserName()) && loginRequest.getPassWord().equals(u.getPassWord()))
				{	
					flag=true;
					id=u.getUserId();
					break;
				}
				else
				{
					flag=false;	
				}
			}
		}
		if(flag==true)
		{
			//if user is valid token will be generated
			String token=jwtUtil.generateToken(loginRequest.getUserName());
			logger.info(loginRequest.getUserName()+" Logged in successfully");
			return token;
		}
		else
		{
			//if user is Invalid
			logger.error("Bad/Invalid Credentials Check And Try To Login Again");
			return "Bad/Invalid Credentials Check And Try To Login Again";
		}	
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> userRegister(@Valid @RequestBody Users user) throws UserAlreadyExistOrNotException
	{
		//to check whether user already exist or not
		boolean flag=usersService.getUserByName(user.getUserName());
		if(flag==false)
		{
			//if user not exist user will be register
			usersService.userRegister(user);
			logger.info(user.getUserName()+"Registered Successfully");
			return new ResponseEntity<>("Registered Successfully",HttpStatus.CREATED);
		}
		else
		{
			//if user already exist
			logger.error(user.getUserName()+" Already Exist So u Can't register again");
			throw new UserAlreadyExistOrNotException(user.getUserName()+" Already Exist So u Can't register again");
		}	
	} 
	@GetMapping("/getAvailableVehicles/{type}")
	public ResponseEntity<?> getAvailableVehicles(@PathVariable String type) throws NoAvailableVehiclesException
	{
		//to check whether user enters correct type or not
		if(type.equalsIgnoreCase("2-wheeler") || type.equalsIgnoreCase("4-wheeler"))
		{
			//to get all the available vehicles
			List<String> resultList=usersService.getAvailableVehicles(type);
			 
			if(!resultList.isEmpty())
			{
				Set<String> resultSet = new HashSet<String>(); 
		        for (String x : resultList) 
		        	resultSet.add(x); 
				//if there are available vehicles
				return new ResponseEntity<>(resultSet,HttpStatus.OK);		
			}
			else
			{
				//if there are no available vehicles
				logger.error("No Available Vehicles!!");
				throw new NoAvailableVehiclesException("No Available Vehicles!!");		
			}
		}
		else
		{
			//if user enters invalide vehicle type
			logger.error("You should enter either 2-wheeler or 4-wheeler");
			return new ResponseEntity<>("You should enter either 2-wheeler or 4-wheeler",HttpStatus.BAD_REQUEST);		
		}
	}	
	@GetMapping("/getRidesSortBasedOnPrice")
	public ResponseEntity<?> getRidesSortBasedOnPrice() throws NoRidesAvailableException
	{
		//to get sorted ride details based on price
		List<Ride> ridesList=usersService.getRidesSortByPrice();
		if(ridesList.isEmpty())
		{
			//if the list is empty
			logger.error("No Rides Available");
			throw new NoRidesAvailableException("No Rides Available");		
		}
		else
		{
			return new ResponseEntity<>(ridesList,HttpStatus.OK);		
		}
	}
	
	
	@PostMapping("/bookVehicle")
	public ResponseEntity<?> bookVehicle(@RequestBody BookingRequest bookingRequest) throws VehicleAlreadyExistOrNotException
	{
		if(bookingRequest.getDropLocation().equalsIgnoreCase(bookingRequest.getPickupLocation()))
		{
			logger.error("Pickup and Drop locations Should be different");
			return new ResponseEntity<>("Pickup and Drop locations Should be different",HttpStatus.BAD_REQUEST);
		}
		else
		{
			UserBookings userBookings=new UserBookings();
			userBookings.setUserId(id);
			String res=usersService.checkDateTime(bookingRequest);
			if(res==null)
			{
				boolean flag=usersService.bookVehicle(bookingRequest,id);
				if(flag==true)
				{
					logger.info("Vehicle Booking is Pending!!");
					return new ResponseEntity<>("Vehicle Booking is Pending!!",HttpStatus.OK);
				}
				logger.error("Vehicle Not Found Try again with other route or Time!!");
				throw new VehicleAlreadyExistOrNotException("Vehicle Not Found Try again with other route or Time!!");
			}
			else
			{
				return new ResponseEntity<>(res,HttpStatus.BAD_REQUEST);
	
			}
		}
	}
	
	@DeleteMapping("/cancelVehicle/{bookingId}")
	public ResponseEntity<?> cancelVehicle(@PathVariable int bookingId) 
	{
		boolean flag=usersService.cancelVehicle(bookingId,id);
		if(flag==false)
		{
			logger.error("Booking Not Found for:"+bookingId);
			return new ResponseEntity<>("Booking Not Found for:"+bookingId,HttpStatus.NOT_FOUND);
		}
		else
		{
			logger.info("Booking Cancelled Successfully");
			return new ResponseEntity<>("Booking Cancelled Successfully",HttpStatus.OK);
		}
	}
	
	@GetMapping("/getAllMyBookings")
	public ResponseEntity<?> getAllMyBookings() 
	{
		List<UserBookings> list=usersService.getAllMyBookings(id);
		if(!list.isEmpty())
		{
			return new ResponseEntity<>(list,HttpStatus.OK);
		}
		else
		{
			logger.error("No Bookings Found");
			return new ResponseEntity<>("No Bookings Found",HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/getConfirmedBookings/{bookingId}")
	public ResponseEntity<?> getConfirmedBookings(@PathVariable int bookingId)
	{
		UserBookings userBookings = usersService.getConfirmedBookings(id,bookingId);
		if(userBookings!=null)
		{
			return new ResponseEntity<>(userBookings,HttpStatus.OK);
		}
		else
		{
			logger.error("Not Yet Confirmed or You have not Booked any Vehicle");
			return new ResponseEntity<>("Not Yet Confirmed or You have not Booked any Vehicle",HttpStatus.NOT_FOUND);
		}
	}
	
	
	@PostMapping("/askQuery/{vehicleId}/{query}")
	public ResponseEntity<?> askQuery(@PathVariable String vehicleId,@PathVariable String query) throws VehicleAlreadyExistOrNotException
	{
		QueryRequest queryRequest = usersService.postQuery(id, vehicleId, query);
		if(queryRequest!=null)
		{
			return new ResponseEntity<>(queryRequest,HttpStatus.OK);
		}
		else
		{
			throw new VehicleAlreadyExistOrNotException(vehicleId+" not exist so can't ask query for this vehicle Owner Enter Correct vehicleId");
		}
	}
	
	@GetMapping("/getRepliedAnswers/{queryId}")
	public ResponseEntity<?> getRepliedAnswers(@PathVariable int queryId) 
	{
		if(usersService.getQueryById(queryId)==null)
		{
			logger.error(queryId +" doesn't exist So Enter correct QueryId");
			return new ResponseEntity<>(queryId +" doesn't exist So Enter correct QueryId",HttpStatus.NOT_FOUND);
		}
		else
		{
			QueryResponse queryResponse = usersService.getRepliedAnswers(id,queryId);
			if(queryResponse==null)
			{
				logger.error("Not Yet Replied");
				return new ResponseEntity<>("Not Yet Replied",HttpStatus.NOT_FOUND);
			}
			else
			{
				return  new ResponseEntity<>(queryResponse,HttpStatus.OK);
			}
		}
	}
	

	//these all are methods from ownerMicroservice for interaction
	@ApiIgnore
	@GetMapping("/getPendingQueries/{ownerId}")
	public List<QueryRequest> getPendingQueries(@PathVariable int ownerId)
	{
		//to get pending queries based on userid
		return usersService.getPendingQueries(ownerId);
		
	}
	
	@ApiIgnore
	@PostMapping("/replyToQuery")
	public boolean replyToQuery(@RequestBody QueryResponse queryResponse)
	{
		return usersService.replyToQuery(queryResponse);	
	}
	
	
}
