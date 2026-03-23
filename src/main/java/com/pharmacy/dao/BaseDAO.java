package com.pharmacy.dao;

import java.util.List;

public interface BaseDAO<T, ID> {
    void save(T entity);
    void update(T entity);
    void delete(ID id);
    T findById(ID id);
    List<T> findAll();
}
