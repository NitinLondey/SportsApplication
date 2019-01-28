package com.TestWeb.TestApp.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.*;

@Entity
@Table(name = "Teamdata")

public class Teamdata {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "team_id")
	private int teamID;


	@Column(name = "team_name")
	private String teamName;

	@Column(name = "abbreviation")
	private String abbreviation;
	
	@ManyToOne
    @JoinColumn(name="user_id")
    private User user;
	
	@Column(name = "win")
	private int win; 

	@Column(name = "loss")
	private int loss; 

	public Teamdata() {

	}

	public Teamdata(Integer id,int teamID, String teamName, String abbreviation) {
		this.id = id;
		this.teamID=teamID;
		this.teamName = teamName;
		this.abbreviation = abbreviation;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public int getTeamID() {
		return teamID;
	}

	public void setTeamID(int teamID) {
		this.teamID = teamID;
	}

	public int getWin() {
		return win;
	}

	public void setWin(int win) {
		this.win = win;
	}

	public int getLoss() {
		return loss;
	}

	public void setLoss(int loss) {
		this.loss = loss;
	}

	public String toString() {
		return "id is" + id + "name is" + teamName;
	}
}
