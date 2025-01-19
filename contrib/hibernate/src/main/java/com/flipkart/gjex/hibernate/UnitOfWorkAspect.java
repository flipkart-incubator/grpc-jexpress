package com.flipkart.gjex.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.context.internal.ManagedSessionContext;

import java.util.Map;

class UnitOfWorkAspect {
    private final Map<String, SessionFactory> sessionFactories;
    private UnitOfWork unitOfWork;
    private Session session;
    private SessionFactory sessionFactory;

    public UnitOfWorkAspect(Map<String, SessionFactory> sessionFactories) {
        this.sessionFactories = sessionFactories;
    }

    public void beforeStart(UnitOfWork unitOfWork) {
        if (unitOfWork != null) {
            this.unitOfWork = unitOfWork;
            this.sessionFactory = (SessionFactory) this.sessionFactories.get(unitOfWork.value());
            if (this.sessionFactory == null) {
                if (!unitOfWork.value().equals("hibernate") || this.sessionFactories.size() != 1) {
                    throw new IllegalArgumentException("Unregistered Hibernate bundle: '" + unitOfWork.value() + "'");
                }

                this.sessionFactory = (SessionFactory) this.sessionFactories.values().iterator().next();
            }

            this.session = this.sessionFactory.openSession();

            try {
                this.configureSession();
                ManagedSessionContext.bind(this.session);
                this.beginTransaction();
            } catch (Throwable var3) {
                this.session.close();
                this.session = null;
                ManagedSessionContext.unbind(this.sessionFactory);
                throw var3;
            }
        }
    }

    public void afterEnd() {
        if (this.session != null) {
            try {
                this.commitTransaction();
            } catch (Exception var2) {
                this.rollbackTransaction();
                throw var2;
            }
        }
    }

    public void onError() {
        if (this.session != null) {
            try {
                this.rollbackTransaction();
            } finally {
                this.onFinish();
            }

        }
    }

    public void onFinish() {
        try {
            if (this.session != null) {
                this.session.close();
            }
        } finally {
            this.session = null;
            ManagedSessionContext.unbind(this.sessionFactory);
        }

    }

    private void configureSession() {
        this.session.setDefaultReadOnly(this.unitOfWork.readOnly());
        this.session.setCacheMode(this.unitOfWork.cacheMode());
        this.session.setFlushMode(this.unitOfWork.flushMode());
    }

    private void beginTransaction() {
        if (this.unitOfWork.transactional()) {
            this.session.beginTransaction();
        }
    }

    private void rollbackTransaction() {
        if (this.unitOfWork.transactional()) {
            Transaction txn = this.session.getTransaction();
            if (txn != null && txn.getStatus().canRollback()) {
                txn.rollback();
            }

        }
    }

    private void commitTransaction() {
        if (this.unitOfWork.transactional()) {
            Transaction txn = this.session.getTransaction();
            if (txn != null && txn.getStatus().canRollback()) {
                txn.commit();
            }

        }
    }
}

