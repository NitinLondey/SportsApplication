package com.TestWeb.TestApp.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.TestWeb.TestApp.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

}
