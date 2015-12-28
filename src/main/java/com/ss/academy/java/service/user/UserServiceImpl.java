package com.ss.academy.java.service.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ss.academy.java.dao.user.UserDao;
import com.ss.academy.java.model.user.User;
import com.ss.academy.java.model.user.UserRole;
import com.ss.academy.java.model.user.UserStatus;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao dao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<User> findAllUsers() {
		return dao.findAllUsers();
	}

	public void saveUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setUserRole(UserRole.USER);
		user.setUserStatus(UserStatus.ACTIVE);
		
		dao.saveUser(user);
	}

	public User findById(String id) {
		return dao.findById(id);
	}

	public User findByUsername(String username) {
		return dao.findByUsername(username);
	}

	public void updateUserStatus(User user) {
		User entity = dao.findById(user.getId());

		if (entity != null) {
			if (entity.getUserStatus().equals(UserStatus.ACTIVE) && entity.getUserRole().equals(UserRole.USER)) {
				entity.setUserStatus(UserStatus.BLOCKED);
			} else {
				entity.setUserStatus(UserStatus.ACTIVE);
			}
		}
	}

	public List<User> findUsersByUserName(String userName) {
		return dao.findUsersByUserName(userName);
	}

	public boolean isUsernameUnique(String username) {
		User user = findByUsername(username);
	   
		if (user == null) {
			return false;
		}
		
		return true;
	}
	
	public List<User> list(Integer offset, Integer maxResults) {
		return dao.list(offset, maxResults);
	}

	public Long count() {
		return dao.count();
	}
}
