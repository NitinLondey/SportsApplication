package com.TestWeb.TestApp.Services;

import java.util.List;

import com.TestWeb.TestApp.model.Teamdata;

public interface TeamService {

	public void createTeam(Teamdata team);
	public List<Teamdata> getUSer();
	
}
