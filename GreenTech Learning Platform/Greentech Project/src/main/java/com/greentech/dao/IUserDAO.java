package com.greentech.dao;

import com.greentech.exception.DatabaseException;
import com.greentech.model.User;
import java.util.List;

/**
 * Contract for User management operations in GreenTech Learning Platform.
 */
public interface IUserDAO extends IGenericDAO<User, Integer> {
    User login(String email, String password) throws DatabaseException;
    boolean emailExists(String email) throws DatabaseException;
    boolean register(String name, String email, String password, String role) throws DatabaseException;
    boolean createUser(String name, String email, String password, String role, String status) throws DatabaseException;
    boolean updateUser(int id, String name, String email, String role, String status) throws DatabaseException;
    int getUserCount() throws DatabaseException;
    List<User> getUsersByRole(String role) throws DatabaseException;
}
