package com.hcl.usermicroservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hcl.usermicroservice.entities.QueryRequest;

@Repository
public interface QueryRequestRepository extends JpaRepository<QueryRequest,Integer>{
	
	@Query(value="select * from Query_request where status=?1",nativeQuery=true)
	public List<QueryRequest> findQueries(String status); 
	
	@Query(value="select * from Query_request where status=?1 and user_id=?2 and query_id=?3",nativeQuery=true)
	public QueryRequest findRepliedQueries(String status,int user_id,int query_id); 

}
