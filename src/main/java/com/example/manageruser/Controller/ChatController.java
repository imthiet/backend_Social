package com.example.manageruser.Controller;

import com.example.manageruser.Model.Message;
import com.example.manageruser.Model.User;
import com.example.manageruser.Service.ChatService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ChatController {

	@MessageMapping("chat.sendMessage")
	@SendTo("/topic/chat")
	public Message sendMsg(@Payload Message msg) {
		return msg;
	}

	@MessageMapping("chat.addUser")
	@SendTo("/topic/chat")
	public Message addUser(@Payload Message msg, SimpMessageHeaderAccessor headerAccessor) {
		headerAccessor.getSessionAttributes().put("username", msg.getSender());
		return msg;
	}

//	@GetMapping("/chatbox")
//	public String showChatBox(@RequestParam("username") String username) {
//		//model.addAttribute("username", username);
//		return "redirect:/ChatBox.html?username=" + username;
//	}
@Controller
@RequestMapping("/messages")
public class MessageController {
	@Autowired
	private ChatService chatService;

	@GetMapping
	public String showMessagesPage(HttpSession session, Model model) {
		String username = (String) session.getAttribute("username");

		if (username == null) {
			return "redirect:/users/login"; // Nếu chưa đăng nhập, chuyển hướng về trang login
		}

		List<User> usersWithMessages = chatService.getUsersWithMessages(username);
		model.addAttribute("usersWithMessages", usersWithMessages);
		model.addAttribute("usn", username);
		for (User user : usersWithMessages) {
			System.out.println("User: " + user.getUsername());
		}

		return "messages"; // Tên của template Thymeleaf
		//return "redirect:/ChatBox.html";
	}

}


}
