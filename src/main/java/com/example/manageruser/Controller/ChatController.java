package com.example.manageruser.Controller;

import com.example.manageruser.Model.Message;
import com.example.manageruser.Dto.UserWithLastMessageDTO;
import com.example.manageruser.Service.ChatService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ChatController {
	@Autowired
	ChatService chatService;

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

//@RequestMapping("/messages")
//public class MessageController {
//
//	private ChatService chatService;
//
//	@GetMapping
//	public String showMessagesPage(HttpSession session, Model model) {
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		String username = authentication != null ? authentication.getName() : null;
//
//		if (username == null) {
//			return "redirect:/login"; // Nếu chưa đăng nhập, chuyển hướng về trang login
//		}
//
//		List<User> usersWithMessages = chatService.getUsersWithMessages(username);
//		model.addAttribute("usersWithMessages", usersWithMessages);
//		model.addAttribute("usn", username);
//		for (User user : usersWithMessages) {
//			System.out.println("User: " + user.getUsername());
//		}
//
//		return "messages";
//		//return "redirect:/ChatBox.html"; // chatbox html
//	}
//
//}

	@GetMapping("/messages")
	public String showMessagesPage(HttpSession session, Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication != null ? authentication.getName() : null;

		if (username == null) {
			return "redirect:/login"; // If not logged in, redirect to login
		}

		// Get users with their last message
		List<UserWithLastMessageDTO> usersWithMessages = chatService.getUsersWithMessages(username);
		model.addAttribute("usersWithMessages", usersWithMessages);
		model.addAttribute("usn", username);

		return "messages";
	}

	@GetMapping("/chatbox")
	public String showMessagesBox(HttpSession session, Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication != null ? authentication.getName() : null;

		if (username == null) {
			return "redirect:/login"; // If not logged in, redirect to login
		}

		// Get users with their last message
		List<UserWithLastMessageDTO> usersWithMessages = chatService.getUsersWithMessages(username);
		model.addAttribute("usersWithMessages", usersWithMessages);
		model.addAttribute("usn", username);

		return "redirect:/ChatBox.html"; // chatbox html
	}




}
