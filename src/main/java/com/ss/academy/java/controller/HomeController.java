package com.ss.academy.java.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.ss.academy.java.model.user.User;
import com.ss.academy.java.service.author.AuthorService;
import com.ss.academy.java.service.book.BookService;
import com.ss.academy.java.service.user.UserService;
import com.ss.academy.java.util.CommonAttributesPopulator;
import com.ss.academy.java.util.ResourceNotFoundException;

/**
 * Handles requests for the application home page.
 */
@Controller
@RequestMapping(value = { "/" })
public class HomeController {

	@Autowired
	AuthorService authorService;

	@Autowired
	BookService bookService;

	@Autowired
	UserService userService;

	// Simply selects the home view to render by returning its name.
	@RequestMapping(value = { "/" })
	public String home(@AuthenticationPrincipal UserDetails user, ModelMap model) {
		model.addAttribute("logged", false);

		if (user != null) {
			User currentUser = userService.findByUsername(user.getUsername());
			
			model.addAttribute("logged", true);
			CommonAttributesPopulator.populate(currentUser, model);
		}

		int authorsCount = authorService.findAllAuthors().size();
		int booksCount = bookService.findAllBooks().size();
		int usersCount = userService.findAllUsers().size();

		model.addAttribute("authorsCount", authorsCount);
		model.addAttribute("booksCount", booksCount);
		model.addAttribute("usersCount", usersCount);

		return "home";
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public String handleResourceNotFoundException() {
		return "layout/404";
	}
}
