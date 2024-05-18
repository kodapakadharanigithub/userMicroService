package com.hcl.usermicroservice.exceptions;

public class UserAlreadyExistOrNotException extends Exception{
	
	public UserAlreadyExistOrNotException(String msg)
	{
		super(msg);
	}

}
