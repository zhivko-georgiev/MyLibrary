package com.ss.academy.java.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ss.academy.java.model.message.Message;
import com.ss.academy.java.model.user.User;
import com.ss.academy.java.service.message.MessageService;
import com.ss.academy.java.service.user.UserService;


@Controller
@RequestMapping(value = { "/messages" })
public class MessageController {
	
	@Autowired
	MessageService messageService;
	
	@Autowired
	UserService userService;
	
	
	@RequestMapping(value = { "/inbox" }, method = RequestMethod.GET)
	public String listAllReceivedMessages(ModelMap model, @AuthenticationPrincipal UserDetails userDetails) {
		User user = userService.findByUsername(userDetails.getUsername());
		List<Message> messages = user.getReceivedMessage();
			model.addAttribute("messages", messages);
		return "messages/inbox";
	}
	
	@RequestMapping(value = { "/outbox" }, method = RequestMethod.GET)
	public String listAllSentMessages(ModelMap model, @AuthenticationPrincipal UserDetails userDetails) {
		User user = userService.findByUsername(userDetails.getUsername());
		List<Message> messages = user.getSentMessage();
			model.addAttribute("messages", messages);
		return "messages/outbox";
	}
	
	@RequestMapping(value = { "/{user_id}/new" }, method = RequestMethod.GET)
	public String sendNewMessage(ModelMap model) {
		Message message = new Message();
		model.addAttribute("message", message);
		return "messages/new";
	}


	@RequestMapping(value = { "/{user_id}/new" }, method = RequestMethod.POST)
	public String saveMessage(@Valid Message message, BindingResult result, ModelMap model,
			@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long user_id) {

		if (result.hasErrors()) {
			return "messages/new";
		}
		User receiver = userService.findById(user_id);
		User sender = userService.findByUsername(userDetails.getUsername());
		sender.getSentMessage().add(message);
		receiver.getReceivedMessage().add(message);
		message.setReceiver(receiver);
		message.setSender(sender);
		messageService.saveMessage(message);
		return "redirect:/messages/outbox";
	}
	
	@RequestMapping(value = { "/{message_id}/reply" }, method = RequestMethod.GET)
	public String replyToMessage(ModelMap model, @PathVariable Integer message_id) {
		Message parent = messageService.findById(message_id);
		List<Message> previousMessages = new ArrayList<Message>();
		previousMessages.add(parent);
		
		while (parent.getIn_reply_to() != 0) {
			parent = messageService.findById(parent.getIn_reply_to());
			previousMessages.add(parent);	
		}
		
		if (parent.getIsNew() == 1) {
			messageService.updateMessageStatus(parent);
		}
		
		Message message = new Message();
		model.addAttribute("message", message);
		model.addAttribute("parents", previousMessages);
		return "messages/reply";
	}
	
	
	@RequestMapping(value = { "/{message_id}/reply" }, method = RequestMethod.POST)
	public String replyToMessage(@Valid Message message, BindingResult result, ModelMap model,
			@AuthenticationPrincipal UserDetails userDetails, @PathVariable Integer message_id) {
	
		if (result.hasErrors()) {
			return "messages/reply";
		}
		Message parent = messageService.findById(message_id);
		User receiver = userService.findByUsername(parent.getSender().getUsername());
		User sender = userService.findByUsername(userDetails.getUsername());
		sender.getSentMessage().add(message);
		receiver.getReceivedMessage().add(message);
		message.setReceiver(receiver);
		message.setSender(sender);
		message.setIn_reply_to(parent.getMessage_id());
		message.setHeader("Re: " + parent.getHeader());
		messageService.saveMessage(message);
		
		
		return "redirect:/messages/outbox";
	}
}