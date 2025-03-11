package com.flipkart.gjex.examples.helloworld.dao;

import com.flipkart.gjex.examples.helloworld.entity.DummyEntity;
import io.dropwizard.hibernate.AbstractDAO;
import io.dropwizard.hibernate.UnitOfWork;
import org.hibernate.SessionFactory;
import javax.inject.Inject;


public class DummyDAO extends AbstractDAO<DummyEntity> {

    @Inject
    public DummyDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @UnitOfWork
    public DummyEntity findById(Long id) {
        return get(id);
    }

    @UnitOfWork
    public DummyEntity create(DummyEntity person) {
        return persist(person);
    }
}
