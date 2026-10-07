package com.kamaleshwaran.kamaleshwaranmart.dao;

import com.kamaleshwaran.kamaleshwaranmart.model.Role;
import com.kamaleshwaran.kamaleshwaranmart.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for User entities.
 */
public interface UserDAO {
    User create(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    List<User> findByRole(Role role);
    boolean update(User user);
    boolean deleteById(Long id);
    int countTotalUsers();
    int countUsersByRole(Role role);
}
