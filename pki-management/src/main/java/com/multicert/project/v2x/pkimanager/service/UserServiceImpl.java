package com.multicert.project.v2x.pkimanager.service;

import java.util.Arrays;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.multicert.project.v2x.pkimanager.model.Role;
import com.multicert.project.v2x.pkimanager.model.User;
import com.multicert.project.v2x.pkimanager.repository.RoleRepository;
import com.multicert.project.v2x.pkimanager.repository.UserRepository;


@Service("userService")
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public void saveUser(User user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setActive(1);
		Role userRole = roleRepository.findByRole("USER");
		user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
		userRepository.save(user);
	}

	@Override
	public void updateUser(User user) {
		if(user.getPassword() != null && !user.getPassword().isEmpty()){
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		}
		userRepository.save(user);
	}

}
