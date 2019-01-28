package com.TestWeb.TestApp.Controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.TestWeb.TestApp.Services.TeamService;
import com.TestWeb.TestApp.model.Teamdata;
import com.TestWeb.TestApp.Services.TeamService;
import com.TestWeb.TestApp.*;
import com.TestWeb.TestApp.Repository.TeamRepository;

@Controller
public class TeamController {

	
	@Autowired
	TeamRepository teamrepo;

	
/*	@GetMapping("/login")
	public String getlogin(Model model) {
		
		return "login";
	}*/

	
	


/*	
	@GetMapping("/home")
	public String getdata(Model model) {
		List<Teamdata> list = (List<Teamdata>) teamrepo.findAll();
		model.addAttribute("data", list);
		
		return "home";
	}
	
	@GetMapping("/selfavoriteteam")
	public String allTeams(Model model) {
		Teamdata t = new Teamdata();
		model.addAttribute("data", t);
		return "favoriteteam";
	}

	@PostMapping("/home")
	public String updateTeam(HttpServletRequest request, @RequestParam String teamselection, Model model) {

		String s[] = teamselection.split(",");
		String changes[] = new String[5];
		String s1, s2;
		teamrepo.deleteAll();
		for (int i = 0; i < s.length; i++) {
			changes = s[i].split("-");
			s1 = changes[0];
			s2 = changes[1];
			Teamdata t = new Teamdata();
			
			teamrepo.save(t);

		}
		List<Teamdata> list = (List<Teamdata>) teamrepo.findAll();
		model.addAttribute("data", list);
		
		return "home";
	}
*/
}
