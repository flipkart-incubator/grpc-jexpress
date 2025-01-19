package com.flipkart.gjex.hibernate;


import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.glassfish.jersey.server.internal.process.MappableException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.context.internal.ManagedSessionContext;
import org.hibernate.resource.transaction.spi.TransactionStatus;

import javax.inject.Inject;

public class UnitOfWorkInterceptor implements MethodInterceptor {

    @Inject
    private SessionFactory sessionFactory;

    public Object invoke(MethodInvocation arg0) throws Throwable {
        Session session = null;
        UnitOfWork aUnitOfWork = arg0.getMethod().getAnnotation(UnitOfWork.class);
        if (!ManagedSessionContext.hasBind(this.sessionFactory)) {
            session = this.openSession(aUnitOfWork);
        }

        try {
            Object response = arg0.proceed();
            if (session != null) {
                this.closeSession(session, aUnitOfWork);
            }

            return response;
        } catch (Throwable var5) {
            if (session != null) {
                this.rollbackSession(session, aUnitOfWork);
            }

            throw var5;
        }
    }

    private Session openSession(UnitOfWork aUnitOfWork) throws Throwable {
        Session session = this.sessionFactory.openSession();

        try {
            this.configureSession(session, aUnitOfWork);
            ManagedSessionContext.bind(session);
            this.beginTransaction(session, aUnitOfWork);
            return session;
        } catch (Throwable var4) {
            if (session != null && session.isOpen()) {
                session.close();
            }

            ManagedSessionContext.unbind(this.sessionFactory);
            throw var4;
        }
    }

    private void beginTransaction(Session session, UnitOfWork aUnitOfWork) {
        if (aUnitOfWork.transactional()) {
            session.beginTransaction();
        }

    }

    private void configureSession(Session session, UnitOfWork aUnitOfWork) {
        session.setDefaultReadOnly(aUnitOfWork.readOnly());
        session.setCacheMode(aUnitOfWork.cacheMode());
        session.setFlushMode(aUnitOfWork.flushMode());
    }

    private void closeSession(Session session, UnitOfWork aUnitOfWork) {
        try {
            this.commitTransaction(session, aUnitOfWork);
        } catch (Exception var7) {
            this.rollbackTransaction(session, aUnitOfWork);
            throw new MappableException(var7);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }

            ManagedSessionContext.unbind(this.sessionFactory);
        }

    }

    private void commitTransaction(Session session, UnitOfWork aUnitOfWork) {
        if (aUnitOfWork.transactional()) {
            Transaction txn = session.getTransaction();
            if (txn != null && txn.getStatus().equals(TransactionStatus.ACTIVE)) {
                txn.commit();
            }
        }

    }

    private void rollbackSession(Session session, UnitOfWork aUnitOfWork) {
        try {
            this.rollbackTransaction(session, aUnitOfWork);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }

            ManagedSessionContext.unbind(this.sessionFactory);
        }

    }

    private void rollbackTransaction(Session session, UnitOfWork aUnitOfWork) {
        if (aUnitOfWork.transactional() && session != null && session.isOpen()) {
            Transaction txn = session.getTransaction();
            if (txn != null && txn.getStatus().equals(TransactionStatus.ACTIVE)) {
                txn.rollback();
            }
        }

    }
}
