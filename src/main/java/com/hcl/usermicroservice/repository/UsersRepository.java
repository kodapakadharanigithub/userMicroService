package com.hcl.usermicroservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hcl.usermicroservice.entities.Users;


@Repository
public interface UsersRepository extends JpaRepository<Users,Integer>{

	@Query(value="select * from users where user_name=?1",nativeQuery=true)
	public Users findByName(String userName);
}
