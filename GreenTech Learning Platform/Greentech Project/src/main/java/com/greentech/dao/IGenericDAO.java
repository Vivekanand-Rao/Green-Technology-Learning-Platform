package com.greentech.dao;

import com.greentech.exception.DatabaseException;
import java.util.List;

/**
 * Generic Data Access Object interface demonstrating Abstraction and Generics.
 *
 * @param <T>  Entity type
 * @param <ID> Primary key type
 */
public interface IGenericDAO<T, ID> {
    T findById(ID id) throws DatabaseException;
    List<T> getAll() throws DatabaseException;
    boolean delete(ID id) throws DatabaseException;
}
