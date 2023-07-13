package com.adobe.acrobatsign.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.adobe.acrobatsign.service.ChatBotService;

@Controller
public class ChatBotController {

	@Autowired
	ChatBotService chatBotService;

	@GetMapping("/chat")
	public String chat() {
		return "chat.html";
	}

	@PostMapping("/chat")
	@ResponseBody
	public ResponseEntity<String> getChatbot(Model model, @RequestBody String content, HttpServletResponse response) {
		String obj = null;
		try {
			model.addAttribute("request", content);
			model.addAttribute("response", "Acrobat Sing");
			response.setContentType("application/zip");

			obj = this.chatBotService.chatWithGpt3(content);
		} catch (Exception e) {
			model.addAttribute("response", "Error in calling ChatGPT API");
			e.printStackTrace();
		}
		return new ResponseEntity<String>(obj, HttpStatus.OK);
	}
}
