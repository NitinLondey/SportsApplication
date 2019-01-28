package com.TestWeb.TestApp.Controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.TestWeb.TestApp.Repository.UserRepository;
import com.TestWeb.TestApp.model.User;

@Controller
public class UserController {

	@Autowired
	UserRepository userepo;

	@PostMapping("/login")
	public ModelAndView getlogin(HttpServletRequest request, Model model, HttpSession session,
			@RequestParam("userID") String id, @RequestParam("username") String username) {

		System.out.println(id);

		ModelAndView login = new ModelAndView();
		String id1 = id;

		String name = username;

		System.out.println(id1);
		boolean flag = false;
		System.out.println("inside post");
		// HttpSession session = request.getSession();
		session.setAttribute("userID", id1);
		session.setAttribute("userName", name);

		System.out.println(id1);
		System.out.println(name);
		long teamid = 0;

		try {
			teamid = Long.parseLong(id1);
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}

		Long userid = Long.valueOf(teamid);

		System.out.println(userid);
		User user = null;
		List<User> userlist = (List<User>) userepo.findAll();
		for (User u : userlist) {

			if (u.getUser_name().equals(name)) {
				user = u;
				flag = true;
				break;
			}

		}

		if (name.equals("Sharon Alcajejaeekh Bushakberg")) {
			session.setAttribute("role", "Admin");
		} else {
			session.setAttribute("role", "User");
		}
		if (!flag) {
			user = new User();
			user.setUser_id(userid);
			user.setUser_name(name);
			user.setStatus(false);
			user.setRole(session.getAttribute("role").toString());
			userepo.save(user);

		}
		// user.setStatus(false);
		// userepo.save(user);
		// model.addAttribute("userName", name);
		login.addObject("userName", name);

		String currentuserrole = session.getAttribute("role").toString();

		if (user.isStatus()) {
			login.addObject("blockstatus", "You are blocked by the admin");
			// model.addAttribute("blockstatus", "You are blocked by the admin");
			login.setViewName("redirect:/login");
			return login;
		}

		if (currentuserrole.equals("Admin")) {

			login.setViewName("redirect:/viewusers");
			return login;
		} else {
			login.setViewName("redirect:/");
			return login;
		}
	}

	@GetMapping("/login")
	public ModelAndView login() {
		ModelAndView mv = new ModelAndView("login");
		System.out.println("inside login get");

		return mv;
	}

	@GetMapping("/viewusers")
	public ModelAndView getAllUsers() {
		ModelAndView modelview = new ModelAndView("userslist");
		List<User> list = (List<User>) userepo.findAll();
		// ServletRequestAttributes attr = (ServletRequestAttributes)
		// RequestContextHolder.currentRequestAttributes();
		// HttpSession session=attr.getRequest().getSession(true);
		// String currentuserrole=session.getAttribute("role").toString();
		List<User> newlist = new ArrayList<User>();
		Iterator<User> itr = list.iterator();
		while (itr.hasNext()) {
			User user = itr.next();
			if (user.getRole().equals("User")) {
				// itr.remove();
				if (!user.isStatus()) {
					newlist.add(user);
				}
			}
		}
		modelview.addObject("listofusers", newlist);
		return modelview;
	}

	@PostMapping("/block")
	public ModelAndView blockUsers(HttpSession session, @RequestParam("user_data") String checkid) {
		ModelAndView modelview = new ModelAndView("userslist");

		// String usernamesession = session.getAttribute("userName").toString();
		Long userid = Long.parseLong(checkid);

		List<User> list = (List<User>) userepo.findAll();
		// ServletRequestAttributes attr = (ServletRequestAttributes)
		// RequestContextHolder.currentRequestAttributes();
		// HttpSession session=attr.getRequest().getSession(true);
		// String currentuserrole=session.getAttribute("role").toString();
		List<User> newlist = new ArrayList<User>();
		Iterator<User> itr = list.iterator();
		while (itr.hasNext()) {
			User user = itr.next();
			if (user.getUser_id().compareTo(userid) == 0) {
				user.setStatus(true);
				userepo.save(user);
				break;
			}

		}

		for (User u : list) {
			if (u.getRole().equals("User")) {
				if (!u.isStatus()) {
					newlist.add(u);
				}
			}
		}

		modelview.addObject("listofusers", newlist);
		return modelview;
	}

}