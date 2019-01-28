package com.TestWeb.TestApp.Controller;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
class homeTeam{
		@JsonProperty("ID")
		String ID;
		@JsonProperty("City")
		String City;
		@JsonProperty("Name")
		String Name;
		@JsonProperty("Abbreviation")
		String Abbreviation;
}

@Data
class awayTeam{
		@JsonProperty("ID")
		String ID;
		@JsonProperty("City")
		String City;
		@JsonProperty("Name")
		String Name;
		@JsonProperty("Abbreviation")
		String Abbreviation;
		
}


@Data
class game{
	@JsonProperty("ID")
	String ID;
	@JsonProperty("date")
	String date;
	@JsonProperty("time")
	String time;
	awayTeam awayTeam; 
	homeTeam homeTeam;
	String location;
}

@Data
class gameScore{
	game game;
	String isUnplayed;
	String isInProgress;
	String isCompleted;
	String awayScore;
	String homeScore;

}

@Data
class scoreboard{
	String lastUpdatedOn;
	@JsonProperty("gameScore")
	ArrayList<gameScore> gameScores;
}

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class OverScoreboard {
	 scoreboard scoreboard;
	
}