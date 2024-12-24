package com.flipkart.gjex.examples.helloworld.dao;

import com.flipkart.gjex.examples.helloworld.entity.DummyEntity;
import com.flipkart.gjex.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import javax.inject.Inject;


public class DummyDAO extends AbstractDAO<DummyEntity> {

    @Inject
    public DummyDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public DummyEntity findById(Long id) {
        return get(id);
    }

    public DummyEntity create(DummyEntity person) {
        return persist(person);
    }
}
