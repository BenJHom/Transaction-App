package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface UserDao {

    List<User> findAll();

    User findByUsername(String username);

    int findIdByUsername(String username);

    BigDecimal viewCurrentBalance(int userId);

    boolean create(String username, String password);
}
