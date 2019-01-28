package com.TestWeb.TestApp.model;

import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "User")

public class User {


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@Column(name = "user_id")
	private Long user_id;
	
	@Column(name = "user_name")
	private String user_name;
	

	@Column(name="role", length=50)
	private String role;
	
	@Column(name="status")
	private boolean status;
	
	
	@OneToMany(mappedBy="user")
    private List<Teamdata> teamdata;
	
	
	
	public User() { 

	}

	public User(Integer id,Long userid, String user_name) {
		super();
		this.id = id;
		this.user_id = userid;
		this.user_name = user_name;
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public List<Teamdata> getTeamdata() {
		return teamdata;
	}

	public void setTeamdata(List<Teamdata> teamdata) {
		this.teamdata = teamdata;
	}

	

	
	
	}
