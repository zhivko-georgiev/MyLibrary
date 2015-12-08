package com.ss.academy.java.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ss.academy.java.model.book.Book;
import com.ss.academy.java.model.book.BookHistory;
import com.ss.academy.java.model.user.User;
import com.ss.academy.java.service.book.BookService;
import com.ss.academy.java.service.user.UserService;

import io.swagger.models.Model;

@Controller
//@RequestMapping(value = "/authors/{id}/books/")
@RequestMapping(value = "/{user_id}")
public class HistoryController {

	@Autowired
	BookService bookService;
	@Autowired
	UserService userService;
	
//	@RequestMapping(value = {"/"}) 
//	public String changeStatus(ModelMap model, Long user_id){
//		List<Book> books = bookService.listMyBooks(user_id);
//		
//		model.addAttribute("books", books);
//		return "success";
//	}
	
//	@RequestMapping(value = {"/{book_id}"}) 
//	public String changeStatus(@PathVariable Long book_id){
//		bookService.changeStatus(book_id);
//		return "redirect:/authors/{id}/books/";
//	}
	
//	@RequestMapping(value = {"/{book_id}"})
//	public String getThisBook(@PathVariable Long book_id, @AuthenticationPrincipal UserDetails userDetails){
//		User currentUser = userService.findByUsername(userDetails.getUsername());
//		Long user_id = currentUser.getId();
//		bookService.getThisBook(user_id, book_id);
//		return "redirect:/authors/{id}/books/";
//		
//	}
	
	@RequestMapping(value = {"books/myhistory/{book_id}"}, method = RequestMethod.GET)
	public String returnThisBook(@PathVariable Long book_id, @AuthenticationPrincipal UserDetails userDetails){
		User currentUser = userService.findByUsername(userDetails.getUsername());
		Long user_id = currentUser.getId();
		bookService.returnThisBook(user_id, book_id);
		return "books/myhistory";
		
	}
	
	@RequestMapping(value = {"/myhistory"}, method = RequestMethod.GET)
	public String listMyBooks(ModelMap model, @AuthenticationPrincipal UserDetails userDetails, Long user_id){
		User currentUser = userService.findByUsername(userDetails.getUsername());
		user_id = currentUser.getId();
		List<Book> books = bookService.listMyBooks(user_id);
		
		model.addAttribute("books", books);
		model.addAttribute("currUser", currentUser.getId());
		return "/books/myhistory";
		
	}
	
	
}
