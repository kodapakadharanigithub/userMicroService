package com.hcl.usermicroservice.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
@Entity
public class Users {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int userId;
	@Column(nullable=false,unique=true)
	@NotNull
	@Size(min=2, message="UserName should Greater than 2 characters")
	private String userName;
	@Column(nullable=false)
	@NotNull
	@Size(min=8, message="Password must be Contain 8 characters")
	private String passWord;
	
	public Users()
	{
		
	}

	public Users(int userId, String userName, String passWord) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.passWord = passWord;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	@Override
	public String toString() {
		return "Users [userId=" + userId + ", userName=" + userName + ", passWord=" + passWord + "]";
	}
	



}
