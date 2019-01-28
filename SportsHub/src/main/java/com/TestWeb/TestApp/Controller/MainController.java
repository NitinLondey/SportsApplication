package com.TestWeb.TestApp.Controller;

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hibernate.loader.custom.Return;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.ModelAndView;

import com.TestWeb.TestApp.Repository.TeamRepository;
import com.TestWeb.TestApp.Repository.UserRepository;
import com.TestWeb.TestApp.model.Teamdata;
import com.TestWeb.TestApp.model.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class MainController {

	@Autowired
	TeamRepository teamrepo;

	@Autowired
	UserRepository userepo;

	private static Map<String, List<String>> cache = new HashMap<String, List<String>>();

	@GetMapping("/teams")
	public ModelAndView getTeams(HttpSession session) {
		ModelAndView showTeams = new ModelAndView("showTeams");

		try {
			session.getAttribute("userID").toString();

		} catch (Exception e) {
			ModelAndView mv = new ModelAndView("redirect:/login");
			return mv;

		}
		String url = "https://api.mysportsfeeds.com/v1.2/pull/nba/2018-2019-regular/overall_team_standings.json";

		String encoding = Base64.getEncoder().encodeToString("c7c12b0b-0cf5-4624-88b6-23b77c:Term@123".getBytes());
		// TOKEN:PASS
		// Add headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Basic " + encoding);
		HttpEntity<String> request = new HttpEntity<String>(headers);

		// Make the call
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<NBATeamStanding> response = restTemplate.exchange(url, HttpMethod.GET, request,
				NBATeamStanding.class);
		NBATeamStanding ts = response.getBody();
		System.out.println(ts.toString());

		// Send the object to view

		showTeams.addObject("teamStandingEntries", ts.getOverallteamstandings().getTeamstandingsentries());

//		String s4 = session.getAttribute("userID").toString();
		/*
		 * Long userid = Long.parseLong(user_Id); List<User> userlist = (List<User>)
		 * userepo.findAll(); User userdata = null; for (User u : userlist) {
		 * 
		 * if (u.getUser_id().compareTo(userid) == 0) { userdata = u; break;
		 * 
		 * } } List<Teamdata> team1data = null; if (userdata != null) { team1data =
		 * userdata.getTeamdata();
		 * 
		 * } Map<String, List<String>> notification = getNotification(team1data);
		 * 
		 * Iterator<Entry<String, List<String>>> it =
		 * notification.entrySet().iterator();
		 * 
		 * while (it.hasNext()) { Entry<String, List<String>> entry = it.next();
		 * 
		 * if (entry.getKey().equals("wins")) {
		 * 
		 * if (entry.getValue().size() > 0) { showTeams.addObject("winNotifications",
		 * entry.getValue());
		 * 
		 * } } else {
		 * 
		 * if (entry.getValue().size() > 0) { showTeams.addObject("lossNotifications",
		 * entry.getValue());
		 * 
		 * }
		 * 
		 * }
		 */

		return showTeams;

	}

	@GetMapping("/logout")
	public ModelAndView getlogout(HttpSession session) {

		session.invalidate();
		cache.clear();

		return new ModelAndView("redirect:/login");
	}

	@GetMapping("/")
	public ModelAndView gethome(HttpSession session) {
		ModelAndView index = new ModelAndView("index");
		String user_Id = null;
		String name = null;
		String role = null;
		try {
			user_Id = session.getAttribute("userID").toString();
			name = session.getAttribute("userName").toString();
			role = session.getAttribute("role").toString();

		} catch (Exception e) {
			ModelAndView mv = new ModelAndView("redirect:/login");
			return mv;

		}
		if (role.equals("User")) {
			Long userid = Long.parseLong(user_Id);
			List<User> userlist = (List<User>) userepo.findAll();
			User userdata = null;
			for (User u : userlist) {

				if (u.getUser_id().compareTo(userid) == 0) {
					userdata = u;
					break;

				}
			}
			List<Teamdata> team1data = null;
			if (userdata != null) {
				team1data = userdata.getTeamdata();

			}
			Map<String, List<String>> notification = getNotification(team1data);

			Iterator<Entry<String, List<String>>> it = notification.entrySet().iterator();

			while (it.hasNext()) {
				Entry<String, List<String>> entry = it.next();

				if (entry.getKey().equals("wins")) {

					if (entry.getValue().size() > 0) {
						index.addObject("winNotifications", entry.getValue());

					}
				} else if (entry.getKey().equals("loss")) {

					if (entry.getValue().size() > 0) {
						index.addObject("lossNotifications", entry.getValue());

					}

				} else {
					if (entry.getValue().size() > 0) {
						index.addObject("sameNotifications", entry.getValue());

					}
				}

			}

		}

		index.addObject("userName", name);

		return index;
	}

	private static Map<String, List<String>> getNotification(List<Teamdata> team1data) {

		List<String> winNotifications = new ArrayList<>();
		List<String> lossNotifications = new ArrayList<>();
		List<String> sameNotifications = new ArrayList<>();

		Map<String, List<String>> details = new HashMap<>();
		boolean flag = false;
		if (team1data.size() > 0) {
			for (Teamdata t : team1data) {

				ArrayList<HashMap<String, String>> list = getWinsdetails(t.getAbbreviation());

				if (!list.isEmpty()) {
					flag = true;
					int win = calculate(list, "wins");
					int loss = calculate(list, "loss");
					if (t.getWin() < win) {
						winNotifications.add(t.getTeamName());
					} else if (t.getLoss() < loss) {
						lossNotifications.add(t.getTeamName());
					} else {
						System.out.println("same");
						String s = "Score Not changed since last time checked";
						sameNotifications.add(t.getTeamName());
						details.put("No Change", sameNotifications);
						cache.put("No Change", sameNotifications);
					}
				} else {
					sameNotifications.add(t.getTeamName());
					details.put("No Change", sameNotifications);
					cache.put("No Change", sameNotifications);
				}

			}

		}

		if (winNotifications.size() > 0) {
			details.put("wins", winNotifications);
			cache.put("wins", winNotifications);
		}
		if (lossNotifications.size() > 0) {

			details.put("loss", lossNotifications);
			cache.put("loss", lossNotifications);

		}

		if (!flag) {
			return cache;
		}

		return details;

	}
	@GetMapping("/scoreboarddeatils")
	public ModelAndView getcurrentdayscores(HttpSession session) {
		ModelAndView teamscores = new ModelAndView("scorecard");

		ArrayList<HashMap<String, String>> scoreDetails = new ArrayList<HashMap<String, String>>();
		String yesterdayDate = null;
		LocalDate today = LocalDate.now();
		 LocalDate yesterday = today.minusDays(1);
		yesterdayDate = DateTimeFormatter.ofPattern("yyyyMMdd").format(yesterday);
		// yesterdayDate=sdf.format(yesterday);
		System.out.println("date is" + yesterdayDate);
		String url = "https://api.mysportsfeeds.com/v1.2/pull/nba/2018-2019-regular/scoreboard.json?fordate="+ yesterdayDate;
		String encoding = Base64.getEncoder().encodeToString("c7c12b0b-0cf5-4624-88b6-23b77c:Term@123".getBytes());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Basic " + encoding);
		HttpEntity<String> request = new HttpEntity<String>(headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
		if (response.getStatusCode().toString().equals("204")) {

		} else {
			String str = response.getBody();
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode root = mapper.readTree(str);
				System.out.println(str);
				// JsonNode jsonNode1 = actualObj.get("lastUpdatedOn");
				System.out.println(root.get("scoreboard").get("lastUpdatedOn").asText());
				System.out.println(root.get("scoreboard").get("gameScore").getNodeType());
				JsonNode gameScore = root.get("scoreboard").get("gameScore");

				if (gameScore.isArray()) {

					gameScore.forEach(gamesco -> {
						JsonNode game = gamesco.get("game");
						if(!game.isNull()) {
						HashMap<String, String> gameDetail = new HashMap<String, String>();
						gameDetail.put("id", game.get("ID").asText());
						gameDetail.put("date", game.get("date").asText());
						gameDetail.put("time", game.get("time").asText());
						gameDetail.put("awayTeam", game.get("awayTeam").get("Abbreviation").asText());
						gameDetail.put("homeTeam", game.get("homeTeam").get("Abbreviation").asText());
						gameDetail.put("awayScore", gamesco.get("awayScore").asText());
						gameDetail.put("homeScore", gamesco.get("homeScore").asText());

						scoreDetails.add(gameDetail);
						}

					});

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

			}

		if(!scoreDetails.isEmpty()) {
			teamscores.addObject("scoreDetails", scoreDetails);
		}else {
			teamscores.addObject("scoredata", "NO matches for today");
		}
		
		return teamscores;

	

	}

	@GetMapping("/score")
	public ModelAndView getscore(HttpSession session) {
		ModelAndView teamscores = new ModelAndView("score");

		String s4 = null;

		try {
			s4 = session.getAttribute("userID").toString();

		} catch (Exception e) {
			ModelAndView mv = new ModelAndView("redirect:/login");
			return mv;

		}

		Long userid = Long.parseLong(s4);
		List<User> userlist = (List<User>) userepo.findAll();
		User userdata = null;
		for (User u : userlist) {

			if (u.getUser_id().compareTo(userid) == 0) {
				userdata = u;
				break;

			}
		}
		//teamscores.addObject("scoreDetails", getcurrentdayscores());
		List<Teamdata> teamdata = userdata.getTeamdata();

		teamscores.addObject("data", teamdata);
		return teamscores;
	}

	@PostMapping("/score")
	public ModelAndView updateTeam(HttpServletRequest request, @RequestParam String teamselection, Model model,
			HttpSession session) {

		ModelAndView updateteam = new ModelAndView("score");
		String s[] = teamselection.split(",");
		List<String> listofteams = new ArrayList<>();
		List<Teamdata> userspecificteam = new ArrayList<>();
		// teamrepo.deleteAll();
		for (int i = 0; i < s.length; i++) {
			listofteams.add(s[i]);

		}

		String s4 = session.getAttribute("userID").toString();
		// String usernamesession = session.getAttribute("userName").toString();

		Long userid = Long.parseLong(s4);
		List<User> userlist = (List<User>) userepo.findAll();

		User userdata = null;
		for (User u : userlist) {

			if (u.getUser_id().compareTo(userid) == 0) {
				userdata = u;
				// flag = true;
				break;

			}

		}

		List<Teamdata> teamdatalist = (List<Teamdata>) teamrepo.findAll();
		List<String> teamName = new ArrayList<>();
		for (Teamdata teamdata : teamdatalist) {

			if (teamdata.getUser().getId().compareTo(userdata.getId()) == 0) {
				System.out.println("already present");
				teamName.add(teamdata.getTeamName());

			}
		}
		for (String s2 : listofteams) {
			String s3[] = s2.split("!");
			if (teamName.contains(s3[1]))
				continue;
			int teamid = Integer.parseInt(s3[0]);
			ArrayList<HashMap<String, String>> list = getWinsdetails(s3[2]);
			int win = calculate(list, "wins");
			int loss = calculate(list, "loss");
			Teamdata td = new Teamdata();

			td.setTeamID(teamid);

			td.setTeamName(s3[1]);
			td.setAbbreviation(s3[2]);
			td.setUser(userdata);
			td.setWin(win);
			td.setLoss(loss);
			teamrepo.save(td);
			userspecificteam.add(td);

		}
		List<Teamdata> teamdata = userdata.getTeamdata();

		if (userspecificteam.size() > 0) {

			for (Teamdata t : userspecificteam) {
				teamdata.add(t);
			}
			userdata.setTeamdata(teamdata);
			userepo.save(userdata);
		}
		// List<Teamdata> teamdata = userdata.getTeamdata();

		updateteam.addObject("data", teamdata);
		// model.addAttribute("data", teamdata);
	//	updateteam.addObject("scoreDetails", getcurrentdayscores());

		return updateteam;
	}

	private static int calculate(ArrayList<HashMap<String, String>> winsdetails, String data) {
		// TODO Auto-generated method stub

		List<Integer> listofwinpct = new ArrayList<Integer>();
		int sum = 0;

		System.out.println("count");
		for (HashMap<String, String> has1 : winsdetails) {

			Iterator<Map.Entry<String, String>> it = has1.entrySet().iterator();

			while (it.hasNext()) {
				Map.Entry<String, String> entry = it.next();

				if (data.equals("wins")) {
					if (entry.getKey().equals("selectedTeam_wins")) {
						int j = Integer.parseInt(entry.getValue());
						listofwinpct.add(j);
					}
				}

				else {
					if (entry.getKey().equals("selectedTeam_loss")) {
						int j = Integer.parseInt(entry.getValue());
						listofwinpct.add(j);
						// listofwinpct.add(entry.getValue());
					}
				}
			}
		}

		for (int i : listofwinpct) {
			sum += i;
		}
		return sum;
	}

	@GetMapping("/selectfavteam")
	public ModelAndView getAlltheTeams(HttpSession session) {
		ModelAndView favteams = new ModelAndView("selection");

		String user_Id = session.getAttribute("userID").toString();
		if (user_Id == null) {
			return new ModelAndView("login");
		}

		ArrayList<HashMap<String, String>> favteamslist = new ArrayList<HashMap<String, String>>();
		String url = "https://api.mysportsfeeds.com/v1.2/pull/nba/2018-2019-regular/overall_team_standings.json";
		String encoding = Base64.getEncoder().encodeToString("c7c12b0b-0cf5-4624-88b6-23b77c:Term@123".getBytes());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Basic " + encoding);
		HttpEntity<String> request = new HttpEntity<String>(headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
		String str = response.getBody();
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode root = mapper.readTree(str);
			System.out.println(str);
			// JsonNode jsonNode1 = actualObj.get("lastUpdatedOn");
			System.out.println(root.get("overallteamstandings").get("lastUpdatedOn").asText());
			System.out.println(root.get("overallteamstandings").get("teamstandingsentry").getNodeType());
			JsonNode teamdetails = root.get("overallteamstandings").get("teamstandingsentry");

			if (teamdetails.isArray()) {

				teamdetails.forEach(teamdata -> {
					JsonNode teamentry = teamdata.get("team");
					HashMap<String, String> teamDetail = new HashMap<String, String>();
					teamDetail.put("id", teamentry.get("ID").asText());
					teamDetail.put("Name", teamentry.get("Name").asText());
					teamDetail.put("Abbreviation", teamentry.get("Abbreviation").asText());
					favteamslist.add(teamDetail);

				});
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		favteams.addObject("favteamslist", favteamslist);

		return favteams;

	}

	public static List<HashMap<String, String>> getgameSchdule() {

		ArrayList<HashMap<String, String>> teamslist = new ArrayList<HashMap<String, String>>();
		String url = "https://api.mysportsfeeds.com/v1.2/pull/nba/2018-2019-regular/full_game_schedule.json";
		String encoding = Base64.getEncoder().encodeToString("c7c12b0b-0cf5-4624-88b6-23b77c:Term@123".getBytes());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Basic " + encoding);
		HttpEntity<String> request = new HttpEntity<String>(headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
		String str = response.getBody();
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode root = mapper.readTree(str);
			System.out.println(str);
			// JsonNode jsonNode1 = actualObj.get("lastUpdatedOn");
			System.out.println(root.get("fullgameschedule").get("lastUpdatedOn").asText());
			System.out.println(root.get("fullgameschedule").get("gameentry").getNodeType());
			JsonNode schedule = root.get("fullgameschedule").get("gameentry");

			if (schedule.isArray()) {

				schedule.forEach(schduledata -> {
					// JsonNode teamentry = schduledata.get("team");
					HashMap<String, String> fullDetail = new HashMap<String, String>();
					fullDetail.put("id", schduledata.get("id").asText());
					fullDetail.put("date", schduledata.get("date").asText());
					fullDetail.put("time", schduledata.get("time").asText());
					fullDetail.put("awayTeam_id", schduledata.get("awayTeam").get("ID").asText());
					fullDetail.put("awayTeam_city", schduledata.get("awayTeam").get("City").asText());
					fullDetail.put("awayTeam_Name", schduledata.get("awayTeam").get("Name").asText());
					fullDetail.put("awayTeam_Abbreviation", schduledata.get("awayTeam").get("Abbreviation").asText());
					fullDetail.put("homeTeam_id", schduledata.get("homeTeam").get("ID").asText());
					fullDetail.put("homeTeam_city", schduledata.get("homeTeam").get("City").asText());
					fullDetail.put("homeTeam_name", schduledata.get("homeTeam").get("Name").asText());
					fullDetail.put("homeTeam_abbreviation", schduledata.get("homeTeam").get("Abbreviation").asText());

					teamslist.add(fullDetail);

				});
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return teamslist;

	}

	@GetMapping("/team")
	public ModelAndView getprofile(@RequestParam("id") String teamID, HttpSession session) {
		ModelAndView gamelogsmodel = new ModelAndView("profiletab");

		String user_Id = session.getAttribute("userID").toString();
		if (user_Id == null) {
			return new ModelAndView("login");
		}

		ArrayList<HashMap<String, String>> gameDetails = new ArrayList<HashMap<String, String>>();
		String url = "https://api.mysportsfeeds.com/v1.2/pull/nba/2018-2019-regular/team_gamelogs.json?team=" + teamID;
		String encoding = Base64.getEncoder().encodeToString("c7c12b0b-0cf5-4624-88b6-23b77c:Term@123".getBytes());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Basic " + encoding);
		HttpEntity<String> request = new HttpEntity<String>(headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
		String str = response.getBody();
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode root = mapper.readTree(str);
			System.out.println(str);
			// JsonNode jsonNode1 = actualObj.get("lastUpdatedOn");
			System.out.println(root.get("teamgamelogs").get("lastUpdatedOn").asText());
			System.out.println(root.get("teamgamelogs").get("gamelogs").getNodeType());
			JsonNode gamelogs = root.get("teamgamelogs").get("gamelogs");

			if (gamelogs.isArray()) {

				gamelogs.forEach(gamelog -> {
					JsonNode game = gamelog.get("game");
					JsonNode team = gamelog.get("team");
					JsonNode statsdata = gamelog.get("stats");

					HashMap<String, String> gameDetail = new HashMap<String, String>();
					/*
					 * gameDetail.put("id", game.get("id").asText()); gameDetail.put("date",
					 * game.get("date").asText()); gameDetail.put("time",
					 * game.get("time").asText()); gameDetail.put("awayTeam",
					 * game.get("awayTeam").get("Abbreviation").asText());
					 */
					gameDetail.put("id", game.get("id").asText());
					gameDetail.put("date", game.get("date").asText());
					gameDetail.put("time", game.get("time").asText());
					gameDetail.put("awayTeam_abbre", game.get("awayTeam").get("Abbreviation").asText());
					gameDetail.put("homeTeam_abbre", game.get("homeTeam").get("Abbreviation").asText());
					gameDetail.put("selectedTeam_id", team.get("ID").asText());
					gameDetail.put("selectedTeam_City", team.get("City").asText());
					gameDetail.put("selectedTeam_Name", team.get("Name").asText());
					gameDetail.put("selectedTeam_abbre", team.get("Abbreviation").asText());
					gameDetail.put("selectedTeam_wins", statsdata.get("Wins").get("#text").asText());
					gameDetail.put("selectedTeam_loss", statsdata.get("Losses").get("#text").asText());

					gameDetails.add(gameDetail);

				});
			}
			String user_idseesion = session.getAttribute("userID").toString();
			session.getAttribute("userName").toString();
			Long userid = Long.parseLong(user_idseesion);
			List<User> userlist = (List<User>) userepo.findAll();
			User userdata = null;
			for (User u : userlist) {

				if (u.getUser_id().compareTo(userid) == 0) {
					userdata = u;

					break;

				}

			}
			List<Teamdata> teamdatalist = (List<Teamdata>) teamrepo.findAll();
			List<String> teamNameAlreadypresent = new ArrayList<>();
			for (Teamdata teamdata : teamdatalist) {
				if (teamdata.getUser().getId().compareTo(userdata.getId()) == 0) {
					System.out.println("already present in the database");
					teamNameAlreadypresent.add(teamdata.getTeamName());

				}

			}
			for (String name : teamNameAlreadypresent) {
				if (gameDetails.get(1).get("selectedTeam_Name").equals(name)) {
					System.out.println("inside selected team");
					gamelogsmodel.addObject("message", "Team Already present in Favorites");
				} else {

				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		gamelogsmodel.addObject("gameDetails", gameDetails);
		gamelogsmodel.addObject("schdule", getgameSchdule());
		gamelogsmodel.addObject("Teamname", gameDetails.get(1).get("selectedTeam_Name"));
		gamelogsmodel.addObject("Team_id", gameDetails.get(1).get("selectedTeam_id"));
		gamelogsmodel.addObject("Team_City", gameDetails.get(1).get("selectedTeam_City"));
		gamelogsmodel.addObject("Team_abbree", gameDetails.get(1).get("selectedTeam_abbre"));
		gamelogsmodel.addObject("image", "/images/" + gameDetails.get(1).get("selectedTeam_abbre") + ".png");
		return gamelogsmodel;
	}

	@PostMapping("/team")
	public ModelAndView addFavorite(HttpSession session, @RequestParam("Teamname") String Teamname,
			@RequestParam("Team_id") String Team_id, @RequestParam("Team_abbree") String Team_abbree,
			@RequestParam("Team_City") String Team_City) {
		ModelAndView profile = new ModelAndView("profiletab");
		// teamrepo.deleteAll();
		String user_idseesion = session.getAttribute("userID").toString();
		session.getAttribute("userName").toString();

		Long userid = Long.parseLong(user_idseesion);
		List<User> userlist = (List<User>) userepo.findAll();
		User userdata = null;
		for (User u : userlist) {

			if (u.getUser_id().compareTo(userid) == 0) {
				userdata = u;

				break;

			}

		}
		int teamid = 0;

		try {
			teamid = Integer.parseInt(Team_id);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		// Integer userid = Long.valueOf(teamid);

		boolean teamflag = false;

		List<Teamdata> teamdatalist = (List<Teamdata>) teamrepo.findAll();
		List<String> teamNameAlreadypresent = new ArrayList<>();
		for (Teamdata teamdata : teamdatalist) {

			if (userdata.getId().compareTo(teamdata.getUser().getId()) == 0) {// check again
				System.out.println("already present");
				// teamflag = true;
				teamNameAlreadypresent.add(teamdata.getTeamName());

			}

		} // add wins and loss

		for (String s : teamNameAlreadypresent) {
			if (s.equals(Teamname)) {
				teamflag = true;
			}
		}

		if (!teamflag) {

			ArrayList<HashMap<String, String>> list = getWinsdetails(Team_abbree);
			int win = calculate(list, "wins");
			int loss = calculate(list, "loss");

			Teamdata td = new Teamdata();
			td.setTeamID(teamid);
			td.setTeamName(Teamname);
			td.setAbbreviation(Team_abbree);
			td.setWin(win);
			td.setLoss(loss);
			td.setUser(userdata);
			teamrepo.save(td);
			List<Teamdata> teamdatalist12 = null;
			if (!userdata.getTeamdata().isEmpty()) {
				teamdatalist12 = userdata.getTeamdata();
				teamdatalist12.add(td);
			} else {
				teamdatalist12 = new ArrayList<>();
				teamdatalist12.add(td);

			}
			if (teamdatalist12 != null)
				userdata.setTeamdata(teamdatalist12);
			userepo.save(userdata);

			profile.addObject("message", "Team Added to Favorites");
		} else {
			profile.addObject("message", "Team Already present in Favorites");
		}
		profile.addObject("schdule", getgameSchdule());
		profile.addObject("Teamname", Teamname);
		profile.addObject("Team_id", Team_id);
		profile.addObject("Team_City", Team_City);
		profile.addObject("Team_abbree", Team_abbree);
		profile.addObject("image", "/images/" + Team_abbree + ".png");

		return profile;
	}

	@GetMapping("/rank")
	public ModelAndView getCurrentRanking() {
		ModelAndView rank = new ModelAndView("ranking");
		ArrayList<HashMap<String, String>> rankDetails = new ArrayList<HashMap<String, String>>();
		String url = "https://api.mysportsfeeds.com/v1.2/pull/nba/2018-2019-regular/overall_team_standings.json";
		String encoding = Base64.getEncoder().encodeToString("c7c12b0b-0cf5-4624-88b6-23b77c:Term@123".getBytes());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Basic " + encoding);
		HttpEntity<String> request = new HttpEntity<String>(headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
		String str = response.getBody();
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode root = mapper.readTree(str);
			System.out.println(str);
			System.out.println(root.get("overallteamstandings").get("lastUpdatedOn").asText());
			System.out.println(root.get("overallteamstandings").get("teamstandingsentry").getNodeType());
			JsonNode teamdetails = root.get("overallteamstandings").get("teamstandingsentry");

			if (teamdetails.isArray()) {

				teamdetails.forEach(teamdata -> {
					JsonNode teamentry = teamdata.get("team");

					HashMap<String, String> teamDetail = new HashMap<String, String>();
					teamDetail.put("rank", teamdata.get("rank").asText());
					teamDetail.put("id", teamentry.get("ID").asText());
					teamDetail.put("Name", teamentry.get("Name").asText());
					teamDetail.put("Abbreviation", teamentry.get("Abbreviation").asText());
					rankDetails.add(teamDetail);

				});
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		rank.addObject("rankDetails", rankDetails);

		return rank;
	}

	public static ArrayList<HashMap<String, String>> getWinsdetails(String teamID) {// 303
		ArrayList<HashMap<String, String>> gameDetails = new ArrayList<HashMap<String, String>>();
		String url = "https://api.mysportsfeeds.com/v1.2/pull/nba/2018-2019-regular/team_gamelogs.json?force=false&team="
				+ teamID;
		String encoding = Base64.getEncoder().encodeToString("c7c12b0b-0cf5-4624-88b6-23b77c:Term@123".getBytes());

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Basic " + encoding);

		/*
		 * HttpComponentsClientHttpRequestFactory factory = new
		 * HttpComponentsClientHttpRequestFactory(); factory.setReadTimeout(10000);
		 * factory.setConnectTimeout(10000);
		 */

		HttpEntity<String> request = new HttpEntity<String>(headers);
		RestTemplate restTemplate = new RestTemplate();

		// RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());

		/*
		 * HttpComponentsClientHttpRequestFactory rf =
		 * (HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory();
		 * rf.setReadTimeout(10 * 1000); rf.setConnectTimeout(10 * 1000);
		 */

		// RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class); // response.
		System.out.println(response.getStatusCode());

		if (response.getStatusCode().toString().equalsIgnoreCase("304")) {

		} else {
			String str = response.getBody();

			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode root = mapper.readTree(str);
				System.out.println(str);
				// JsonNode jsonNode1 =actualObj.get("lastUpdatedOn");
				System.out.println(root.get("teamgamelogs").get("lastUpdatedOn").asText());
				System.out.println(root.get("teamgamelogs").get("gamelogs").getNodeType());
				JsonNode gamelogs = root.get("teamgamelogs").get("gamelogs");

				if (gamelogs.isArray()) {

					gamelogs.forEach(gamelog -> {
						JsonNode game = gamelog.get("game");
						JsonNode team = gamelog.get("team");
						JsonNode statsdata = gamelog.get("stats");

						HashMap<String, String> gameDetail = new HashMap<String, String>();

						gameDetail.put("id", game.get("id").asText());
						gameDetail.put("date", game.get("date").asText());
						gameDetail.put("time", game.get("time").asText());
						gameDetail.put("awayTeam", game.get("awayTeam").get("Abbreviation").asText());

						gameDetail.put("id", game.get("id").asText());
						gameDetail.put("date", game.get("date").asText());
						gameDetail.put("time", game.get("time").asText());
						gameDetail.put("selectedTeam_id", team.get("ID").asText());
						gameDetail.put("selectedTeam_City", team.get("City").asText());
						gameDetail.put("selectedTeam_Name", team.get("Name").asText());
						gameDetail.put("selectedTeam_abbre", team.get("Abbreviation").asText());
						gameDetail.put("selectedTeam_wins", statsdata.get("Wins").get("#text").asText());
						gameDetail.put("selectedTeam_loss", statsdata.get("Losses").get("#text").asText());
						gameDetail.put("winpercent", statsdata.get("WinPct").get("#text").asText());

						gameDetails.add(gameDetail);

					});
				}
			} catch (IOException e) { // TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return gameDetails;
	}

}

/*
 * public static ArrayList<HashMap<String, String>> getWinsdetails(String
 * teamID) { ArrayList<HashMap<String, String>> gameDetails = new
 * ArrayList<HashMap<String, String>>(); String url =
 * "https://api.mysportsfeeds.com/v1.2/pull/nba/2018-2019-regular/team_gamelogs.json?team="
 * + teamID; String encoding =
 * Base64.getEncoder().encodeToString("c7c12b0b-0cf5-4624-88b6-23b77c:Term@123".
 * getBytes());
 * 
 * HttpHeaders headers = new HttpHeaders();
 * headers.setContentType(MediaType.APPLICATION_JSON);
 * headers.set("Authorization", "Basic " + encoding);
 * 
 * 
 * HttpComponentsClientHttpRequestFactory factory = new
 * HttpComponentsClientHttpRequestFactory(); factory.setReadTimeout(10000);
 * factory.setConnectTimeout(10000);
 * 
 * 
 * HttpEntity<String> request = new HttpEntity<String>(headers); RestTemplate
 * restTemplate = new RestTemplate();
 * 
 * // RestTemplate restTemplate = new
 * RestTemplate(getClientHttpRequestFactory());
 * 
 * 
 * HttpComponentsClientHttpRequestFactory rf =
 * (HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory();
 * rf.setReadTimeout(10 * 1000); rf.setConnectTimeout(10 * 1000);
 * 
 * // RestTemplate restTemplate = new
 * RestTemplate(getClientHttpRequestFactory());
 * 
 * ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,
 * request, String.class); // response.
 * System.out.println(response.getStatusCode());
 * 
 * String str = response.getBody();
 * 
 * ObjectMapper mapper = new ObjectMapper(); try { JsonNode root =
 * mapper.readTree(str); System.out.println(str); // JsonNode jsonNode1 =
 * actualObj.get("lastUpdatedOn");
 * System.out.println(root.get("teamgamelogs").get("lastUpdatedOn").asText());
 * System.out.println(root.get("teamgamelogs").get("gamelogs").getNodeType());
 * JsonNode gamelogs = root.get("teamgamelogs").get("gamelogs");
 * 
 * if (gamelogs.isArray()) {
 * 
 * gamelogs.forEach(gamelog -> { JsonNode game = gamelog.get("game"); JsonNode
 * team = gamelog.get("team"); JsonNode statsdata = gamelog.get("stats");
 * 
 * HashMap<String, String> gameDetail = new HashMap<String, String>();
 * 
 * gameDetail.put("id", game.get("id").asText()); gameDetail.put("date",
 * game.get("date").asText()); gameDetail.put("time",
 * game.get("time").asText()); gameDetail.put("awayTeam",
 * game.get("awayTeam").get("Abbreviation").asText());
 * 
 * gameDetail.put("id", game.get("id").asText()); gameDetail.put("date",
 * game.get("date").asText()); gameDetail.put("time",
 * game.get("time").asText()); gameDetail.put("selectedTeam_id",
 * team.get("ID").asText()); gameDetail.put("selectedTeam_City",
 * team.get("City").asText()); gameDetail.put("selectedTeam_Name",
 * team.get("Name").asText()); gameDetail.put("selectedTeam_abbre",
 * team.get("Abbreviation").asText()); gameDetail.put("selectedTeam_wins",
 * statsdata.get("Wins").get("#text").asText());
 * gameDetail.put("selectedTeam_loss",
 * statsdata.get("Losses").get("#text").asText()); gameDetail.put("winpercent",
 * statsdata.get("WinPct").get("#text").asText());
 * 
 * gameDetails.add(gameDetail);
 * 
 * }); } } catch (IOException e) { // TODO Auto-generated catch block
 * e.printStackTrace(); }
 * 
 * return gameDetails; }
 */
