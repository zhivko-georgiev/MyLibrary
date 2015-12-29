package com.ss.academy.java.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.ss.academy.java.model.book.BookStatus;
import com.ss.academy.java.model.message.Message;
import com.ss.academy.java.model.user.User;
import com.ss.academy.java.service.book.BookHistoryService;
import com.ss.academy.java.service.book.BookService;
import com.ss.academy.java.service.user.UserService;
import com.ss.academy.java.util.UnreadMessagesCounter;

@Controller
@RequestMapping(value = "/books")
public class BookHistoryController {

	@Autowired
	UserService userService;
	
	@Autowired
	BookHistoryService bookHistoryService;
	
	@Autowired
	BookService bookService;
	
	@RequestMapping(value = {"/{user_id}"}, method = RequestMethod.GET)
	public String listBooksHistory(ModelMap model, @AuthenticationPrincipal UserDetails userDetails, 
			Integer offset, Integer maxResults){
		User currentUser = userService.findByUsername(userDetails.getUsername());
//		List<BookHistory> booksHistory = currentUser.getBooksHistory();
		
		List<Message> messages = currentUser.getReceivedMessage();
		int unread = UnreadMessagesCounter.counter(messages);
		
		List<BookHistory> booksHistory = bookHistoryService.findAllBooksHistory(offset, maxResults);
		Long countAllBookHistory = bookHistoryService.countAllBooksHistory();
		
		if (booksHistory.isEmpty()) {
			model.addAttribute("isEmpty", true);
		} else {
			model.addAttribute("isEmpty", false);
			model.addAttribute("booksHistory", booksHistory);
			model.addAttribute("count", countAllBookHistory);
			model.addAttribute("offset", offset);
		}
		
		model.addAttribute("unread", unread);
		model.addAttribute("currUser", currentUser.getId());
		
		return "users/booksHistory";
	}
	
	@RequestMapping(value = { "/{book_id}/{user_id}/addToHistory" }, method = RequestMethod.GET)
	public String addNewBookHistory(@PathVariable Long book_id,
			@AuthenticationPrincipal UserDetails userDetails) {
		BookHistory bookHistory = new BookHistory();
		Book book = bookService.findById(book_id);
		User user = userService.findByUsername(userDetails.getUsername());
		
		if (book.getStatus().equals(BookStatus.Available)) {
		user.getBooksHistory().add(bookHistory);
		book.getBooksHistory().add(bookHistory);
		bookService.changeBookStatus(book);
		bookHistory.setBook(book);
		bookHistory.setUser(user);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(bookHistory.getGetDate());
		cal.add(Calendar.DAY_OF_MONTH, 90);
		bookHistory.setReturnDate(cal.getTime());
		
		bookHistoryService.saveBookHistory(bookHistory);
		}
		
		return "redirect:/books/{user_id}";
	}
	
	@RequestMapping(value = "/{user_id}/{history_id}/return", method = RequestMethod.GET)
	public String returnBook(@PathVariable Long history_id, @AuthenticationPrincipal UserDetails userDetails) {	
		BookHistory bookHistory = bookHistoryService.findById(history_id);
		
		if (bookHistory.getIsReturned() == 0 && bookHistory.getBook().getStatus().equals(BookStatus.Loaned)) {
		Date currDate = new Date(System.currentTimeMillis());
		bookHistory.setReturnDate(currDate);
		bookHistory.setIsReturned(1);
		bookHistoryService.updateBookHistory(bookHistory);
		bookService.changeBookStatus(bookHistory.getBook());
		}
		
		return "redirect:/books/{user_id}";
	}
	
	@RequestMapping(value = "/loaned", method = RequestMethod.GET)
	public String showAllLoanedBooks(@AuthenticationPrincipal UserDetails userDetails, ModelMap model,
			Integer offset, Integer maxResults, BookHistory bookHistories) {	
		User currentUser = userService.findByUsername(userDetails.getUsername());
		List<Message> messages = currentUser.getReceivedMessage();
		int unread = UnreadMessagesCounter.counter(messages);
		Date currDate = new Date(System.currentTimeMillis());		
		
		
		List<BookHistory> bookHistory = bookHistoryService.findAllBooksHistory(offset, maxResults, 0);
		Long countAllBookHistory = bookHistoryService.countAllBooksHistory(0);
//		List<BookHistory> loanedBooks = new ArrayList<BookHistory>();
		
//		for (BookHistory book : bookHistory) {
//		 
//			if (book.getIsReturned() == 0) {
//				loanedBooks.add(book);
//			}
//		}
		
		if (bookHistory.isEmpty()) {
			model.addAttribute("isEmpty", true);
		} else {
			model.addAttribute("isEmpty", false);
			model.addAttribute("loanedBooks", bookHistory);
			model.addAttribute("count", countAllBookHistory);
			model.addAttribute("offset", offset);
			model.addAttribute("currDate", currDate);
		}
			
		model.addAttribute("unread", unread);
		model.addAttribute("currUser", currentUser.getId());
		
		return "users/loanedBooks";
	}
}
