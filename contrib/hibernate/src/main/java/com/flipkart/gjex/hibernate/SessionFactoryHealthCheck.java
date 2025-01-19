package com.flipkart.gjex.hibernate;

import com.flipkart.gjex.db.Duration;
import com.flipkart.gjex.db.TimeBoundHealthCheck;
import com.google.common.util.concurrent.MoreExecutors;
import io.dropwizard.metrics5.health.HealthCheck;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.concurrent.ExecutorService;

public class SessionFactoryHealthCheck extends HealthCheck {
    private final SessionFactory sessionFactory;
    private final String validationQuery;
    private final TimeBoundHealthCheck timeBoundHealthCheck;

    public SessionFactoryHealthCheck(SessionFactory sessionFactory, String validationQuery) {
        this(MoreExecutors.newDirectExecutorService(), Duration.seconds(0L), sessionFactory, validationQuery);
    }

    public SessionFactoryHealthCheck(ExecutorService executorService, Duration duration, SessionFactory sessionFactory, String validationQuery) {
        this.sessionFactory = sessionFactory;
        this.validationQuery = validationQuery;
        this.timeBoundHealthCheck = new TimeBoundHealthCheck(executorService, duration);
    }

    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    public String getValidationQuery() {
        return this.validationQuery;
    }

    protected Result check() throws Exception {
        return this.timeBoundHealthCheck.check(() -> {
            Session session = this.sessionFactory.openSession();
            Throwable var2 = null;

            try {
                Transaction txn = session.beginTransaction();

                try {
                    session.createSQLQuery(this.validationQuery).list();
                    txn.commit();
                } catch (Exception var13) {
                    if (txn.getStatus().canRollback()) {
                        txn.rollback();
                    }

                    throw var13;
                }
            } catch (Throwable var14) {
                var2 = var14;
                throw var14;
            } finally {
                if (session != null) {
                    if (var2 != null) {
                        try {
                            session.close();
                        } catch (Throwable var12) {
                            var2.addSuppressed(var12);
                        }
                    } else {
                        session.close();
                    }
                }

            }

            return Result.healthy();
        });
    }
}
