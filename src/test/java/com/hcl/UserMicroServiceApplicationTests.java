package com.hcl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.hcl.usermicroservice.controller.UserController;
import com.hcl.usermicroservice.dto.LoginRequest;
import com.hcl.usermicroservice.entities.Users;
import com.hcl.usermicroservice.service.UsersService;



@SpringBootTest(classes=com.hcl.usermicroservice.service.UsersService.class)
class UserMicroServiceApplicationTests {

	@InjectMocks
	private UserController userController;
	@Mock
	private UsersService userService;
	
	@Test
	public void testUserByName()
	{
		  String  userName="user1";
		  boolean expected=false;
	      when(userService.getUserByName(userName)).thenReturn(expected);
	      boolean actual = userService.getUserByName(userName);
	      assertEquals(expected,actual);  
	}
	
	@Test
	public void testUserRegister() throws Exception
	{
		int userId=10;
		String name="user10";
		String passWord="user1";
		Users users=new Users(userId,name,passWord);
		ResponseEntity<?> reponse = userController.userRegister(users);
        assertEquals(HttpStatus.CREATED, reponse.getStatusCode());

	}
	@Test
	public void testAlreadyUserRegister() throws Exception
	{
		int userId=10;
		String name="user1";
		String passWord="user123";
		Users users=new Users(userId,name,passWord);
		ResponseEntity<?> reponse = userController.userRegister(users);
        assertEquals(HttpStatus.BAD_REQUEST, reponse.getStatusCode());

	}
	@Test
	public void testUserLogin() throws Exception
	{
		String name="user10";
		String passWord="user1";
		LoginRequest loginrequest=new LoginRequest(name,passWord);
		List<Users> list = userService.getAllUsers();
		String res2 = userController.userLogin(loginrequest);
		assertEquals("OOPS!!There are no users", res2);
		
	}
	
	@Test
	public void testUserLoginInvalid() throws Exception
	{
		String name="user106";
		String passWord="user1";
		LoginRequest loginrequest=new LoginRequest(name,passWord);
		List<Users> list = userService.getAllUsers();
		if(!list.isEmpty())
		{
			String res = userController.userLogin(loginrequest);
			if(res==null)
				assertEquals("Bad/Invalid Credentials Check And Try To Login Again", res);
		}
		
	}
}
