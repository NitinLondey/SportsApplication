package com.TestWeb.TestApp.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.TestWeb.TestApp.model.Teamdata;

@Repository
public interface TeamRepository extends CrudRepository<Teamdata, Long> {

}
