package com.adobe.acrobatsign.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.adobe.acrobatsign.model.User;
import com.adobe.acrobatsign.repository.UserRepository;
@Controller
public class UserController {

	@Autowired
	private UserRepository userRepo;

	/*
	 * @GetMapping("/users") public String loginPage(Model model) {
	 * List<EnrolledCourses> mycourseDetails = this.almService.usersEnrollment();
	 * model.addAttribute("myCourseList", mycourseDetails); return "mycourses"; }
	 */

	@PostMapping("/process_register")
	public String processRegister(User user) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);

		userRepo.save(user);

		return "mycourses";
	}

	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("user", new User());

		return "registrationForm";
	}

	@GetMapping("")
	public String viewHomePage() {
		return "index";
	}
}
