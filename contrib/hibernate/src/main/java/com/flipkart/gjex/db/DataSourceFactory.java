package com.flipkart.gjex.db;

import io.dropwizard.metrics5.MetricRegistry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.primitives.Ints;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.*;

public class DataSourceFactory implements PooledDataSourceFactory {
    @NotNull
    private String driverClass = null;
    @Min(0L)
    @Max(100L)
    private int abandonWhenPercentageFull = 0;
    private boolean alternateUsernamesAllowed = false;
    private boolean commitOnReturn = false;
    private boolean rollbackOnReturn = false;
    private Boolean autoCommitByDefault;
    private Boolean readOnlyByDefault;
    private String user = null;
    private String password = null;
    @NotNull
    private String url = null;
    @NotNull
    private Map<String, String> properties = new LinkedHashMap<String, String>();
    private String defaultCatalog;
    @NotNull
    private DataSourceFactory.TransactionIsolation defaultTransactionIsolation;
    private boolean useFairQueue;
    @Min(0L)
    private int initialSize;
    @Min(0L)
    private int minSize;
    @Min(1L)
    private int maxSize;
    private String initializationQuery;
    private boolean logAbandonedConnections;
    private boolean logValidationErrors;
    private Duration maxConnectionAge;
    @NotNull
    private Duration maxWaitForConnection;
    @NotNull
    private Duration minIdleTime;
    @NotNull
    private String validationQuery;
    private Duration validationQueryTimeout;
    private boolean checkConnectionWhileIdle;
    private boolean checkConnectionOnBorrow;
    private boolean checkConnectionOnConnect;
    private boolean checkConnectionOnReturn;
    private boolean autoCommentsEnabled;
    @NotNull
    private Duration evictionInterval;
    @NotNull
    private Duration validationInterval;
    private Optional<String> validatorClassName;
    private boolean removeAbandoned;
    @NotNull
    private Duration removeAbandonedTimeout;

    public DataSourceFactory() {
        this.defaultTransactionIsolation = TransactionIsolation.DEFAULT;
        this.useFairQueue = true;
        this.initialSize = 10;
        this.minSize = 10;
        this.maxSize = 100;
        this.logAbandonedConnections = false;
        this.logValidationErrors = false;
        this.maxWaitForConnection = Duration.seconds(30L);
        this.minIdleTime = Duration.minutes(1L);
        this.validationQuery = "/* Health Check */ SELECT 1";
        this.checkConnectionWhileIdle = true;
        this.checkConnectionOnBorrow = false;
        this.checkConnectionOnConnect = true;
        this.checkConnectionOnReturn = false;
        this.autoCommentsEnabled = true;
        this.evictionInterval = Duration.seconds(5L);
        this.validationInterval = Duration.seconds(30L);
        this.validatorClassName = Optional.empty();
        this.removeAbandoned = false;
        this.removeAbandonedTimeout = Duration.seconds(60L);
    }

    @JsonProperty
    public boolean isAutoCommentsEnabled() {
        return this.autoCommentsEnabled;
    }

    @JsonProperty
    public void setAutoCommentsEnabled(boolean autoCommentsEnabled) {
        this.autoCommentsEnabled = autoCommentsEnabled;
    }

    @JsonProperty
    public String getDriverClass() {
        return this.driverClass;
    }

    @JsonProperty
    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    @JsonProperty
    public String getUser() {
        return this.user;
    }

    @JsonProperty
    public void setUser(String user) {
        this.user = user;
    }

    @JsonProperty
    public String getPassword() {
        return this.password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonProperty
    public String getUrl() {
        return this.url;
    }

    @JsonProperty
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty
    public Map<String, String> getProperties() {
        return this.properties;
    }

    @JsonProperty
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @JsonProperty
    public Duration getMaxWaitForConnection() {
        return this.maxWaitForConnection;
    }

    @JsonProperty
    public void setMaxWaitForConnection(Duration maxWaitForConnection) {
        this.maxWaitForConnection = maxWaitForConnection;
    }

    @JsonProperty
    public String getValidationQuery() {
        return this.validationQuery;
    }

    /** @deprecated */
    @Deprecated
    @JsonIgnore
    public String getHealthCheckValidationQuery() {
        return this.getValidationQuery();
    }

    @JsonProperty
    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    @JsonProperty
    public int getMinSize() {
        return this.minSize;
    }

    @JsonProperty
    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }

    @JsonProperty
    public int getMaxSize() {
        return this.maxSize;
    }

    @JsonProperty
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    @JsonProperty
    public boolean getCheckConnectionWhileIdle() {
        return this.checkConnectionWhileIdle;
    }

    @JsonProperty
    public void setCheckConnectionWhileIdle(boolean checkConnectionWhileIdle) {
        this.checkConnectionWhileIdle = checkConnectionWhileIdle;
    }

    /** @deprecated */
    @Deprecated
    @JsonProperty
    public boolean isDefaultReadOnly() {
        return Boolean.TRUE.equals(this.readOnlyByDefault);
    }

    /** @deprecated */
    @Deprecated
    @JsonProperty
    public void setDefaultReadOnly(boolean defaultReadOnly) {
        this.readOnlyByDefault = defaultReadOnly;
    }

    @JsonIgnore
    public boolean isMinSizeLessThanMaxSize() {
        return this.minSize <= this.maxSize;
    }

    @JsonIgnore
    public boolean isInitialSizeLessThanMaxSize() {
        return this.initialSize <= this.maxSize;
    }

    @JsonIgnore
    public boolean isInitialSizeGreaterThanMinSize() {
        return this.minSize <= this.initialSize;
    }

    @JsonProperty
    public int getAbandonWhenPercentageFull() {
        return this.abandonWhenPercentageFull;
    }

    @JsonProperty
    public void setAbandonWhenPercentageFull(int percentage) {
        this.abandonWhenPercentageFull = percentage;
    }

    @JsonProperty
    public boolean isAlternateUsernamesAllowed() {
        return this.alternateUsernamesAllowed;
    }

    @JsonProperty
    public void setAlternateUsernamesAllowed(boolean allow) {
        this.alternateUsernamesAllowed = allow;
    }

    @JsonProperty
    public boolean getCommitOnReturn() {
        return this.commitOnReturn;
    }

    @JsonProperty
    public boolean getRollbackOnReturn() {
        return this.rollbackOnReturn;
    }

    @JsonProperty
    public void setCommitOnReturn(boolean commitOnReturn) {
        this.commitOnReturn = commitOnReturn;
    }

    @JsonProperty
    public void setRollbackOnReturn(boolean rollbackOnReturn) {
        this.rollbackOnReturn = rollbackOnReturn;
    }

    @JsonProperty
    public Boolean getAutoCommitByDefault() {
        return this.autoCommitByDefault;
    }

    @JsonProperty
    public void setAutoCommitByDefault(Boolean autoCommit) {
        this.autoCommitByDefault = autoCommit;
    }

    @JsonProperty
    public String getDefaultCatalog() {
        return this.defaultCatalog;
    }

    @JsonProperty
    public void setDefaultCatalog(String defaultCatalog) {
        this.defaultCatalog = defaultCatalog;
    }

    @JsonProperty
    public Boolean getReadOnlyByDefault() {
        return this.readOnlyByDefault;
    }

    @JsonProperty
    public void setReadOnlyByDefault(Boolean readOnlyByDefault) {
        this.readOnlyByDefault = readOnlyByDefault;
    }

    @JsonProperty
    public TransactionIsolation getDefaultTransactionIsolation() {
        return this.defaultTransactionIsolation;
    }

    @JsonProperty
    public void setDefaultTransactionIsolation(TransactionIsolation isolation) {
        this.defaultTransactionIsolation = isolation;
    }

    @JsonProperty
    public boolean getUseFairQueue() {
        return this.useFairQueue;
    }

    @JsonProperty
    public void setUseFairQueue(boolean fair) {
        this.useFairQueue = fair;
    }

    @JsonProperty
    public int getInitialSize() {
        return this.initialSize;
    }

    @JsonProperty
    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    @JsonProperty
    public String getInitializationQuery() {
        return this.initializationQuery;
    }

    @JsonProperty
    public void setInitializationQuery(String query) {
        this.initializationQuery = query;
    }

    @JsonProperty
    public boolean getLogAbandonedConnections() {
        return this.logAbandonedConnections;
    }

    @JsonProperty
    public void setLogAbandonedConnections(boolean log) {
        this.logAbandonedConnections = log;
    }

    @JsonProperty
    public boolean getLogValidationErrors() {
        return this.logValidationErrors;
    }

    @JsonProperty
    public void setLogValidationErrors(boolean log) {
        this.logValidationErrors = log;
    }

    @JsonProperty
    public Optional<Duration> getMaxConnectionAge() {
        return Optional.ofNullable(this.maxConnectionAge);
    }

    @JsonProperty
    public void setMaxConnectionAge(Duration age) {
        this.maxConnectionAge = age;
    }

    @JsonProperty
    public Duration getMinIdleTime() {
        return this.minIdleTime;
    }

    @JsonProperty
    public void setMinIdleTime(Duration time) {
        this.minIdleTime = time;
    }

    @JsonProperty
    public boolean getCheckConnectionOnBorrow() {
        return this.checkConnectionOnBorrow;
    }

    @JsonProperty
    public void setCheckConnectionOnBorrow(boolean checkConnectionOnBorrow) {
        this.checkConnectionOnBorrow = checkConnectionOnBorrow;
    }

    @JsonProperty
    public boolean getCheckConnectionOnConnect() {
        return this.checkConnectionOnConnect;
    }

    @JsonProperty
    public void setCheckConnectionOnConnect(boolean checkConnectionOnConnect) {
        this.checkConnectionOnConnect = checkConnectionOnConnect;
    }

    @JsonProperty
    public boolean getCheckConnectionOnReturn() {
        return this.checkConnectionOnReturn;
    }

    @JsonProperty
    public void setCheckConnectionOnReturn(boolean checkConnectionOnReturn) {
        this.checkConnectionOnReturn = checkConnectionOnReturn;
    }

    @JsonProperty
    public Duration getEvictionInterval() {
        return this.evictionInterval;
    }

    @JsonProperty
    public void setEvictionInterval(Duration interval) {
        this.evictionInterval = interval;
    }

    @JsonProperty
    public Duration getValidationInterval() {
        return this.validationInterval;
    }

    @JsonProperty
    public void setValidationInterval(Duration validationInterval) {
        this.validationInterval = validationInterval;
    }

    @JsonProperty
    public Optional<Duration> getValidationQueryTimeout() {
        return Optional.ofNullable(this.validationQueryTimeout);
    }

    @JsonProperty
    public Optional<String> getValidatorClassName() {
        return this.validatorClassName;
    }

    @JsonProperty
    public void setValidatorClassName(Optional<String> validatorClassName) {
        this.validatorClassName = validatorClassName;
    }

    /** @deprecated */
    @Deprecated
    @JsonIgnore
    public Optional<Duration> getHealthCheckValidationTimeout() {
        return this.getValidationQueryTimeout();
    }

    @JsonProperty
    public void setValidationQueryTimeout(Duration validationQueryTimeout) {
        this.validationQueryTimeout = validationQueryTimeout;
    }

    @JsonProperty
    public boolean isRemoveAbandoned() {
        return this.removeAbandoned;
    }

    @JsonProperty
    public void setRemoveAbandoned(boolean removeAbandoned) {
        this.removeAbandoned = removeAbandoned;
    }

    @JsonProperty
    public Duration getRemoveAbandonedTimeout() {
        return this.removeAbandonedTimeout;
    }

    @JsonProperty
    public void setRemoveAbandonedTimeout(Duration removeAbandonedTimeout) {
        this.removeAbandonedTimeout = (Duration) Objects.requireNonNull(removeAbandonedTimeout);
    }

    public void asSingleConnectionPool() {
        this.minSize = 1;
        this.maxSize = 1;
        this.initialSize = 1;
    }

    public ManagedDataSource build(MetricRegistry metricRegistry, String name) {
        Properties properties = new Properties();
        Iterator var4 = this.properties.entrySet().iterator();

        while(var4.hasNext()) {
            Map.Entry<String, String> property = (Map.Entry)var4.next();
            properties.setProperty((String)property.getKey(), (String)property.getValue());
        }

        PoolProperties poolConfig = new PoolProperties();
        poolConfig.setAbandonWhenPercentageFull(this.abandonWhenPercentageFull);
        poolConfig.setAlternateUsernameAllowed(this.alternateUsernamesAllowed);
        poolConfig.setCommitOnReturn(this.commitOnReturn);
        poolConfig.setRollbackOnReturn(this.rollbackOnReturn);
        poolConfig.setDbProperties(properties);
        poolConfig.setDefaultAutoCommit(this.autoCommitByDefault);
        poolConfig.setDefaultCatalog(this.defaultCatalog);
        poolConfig.setDefaultReadOnly(this.readOnlyByDefault);
        poolConfig.setDefaultTransactionIsolation(this.defaultTransactionIsolation.get());
        poolConfig.setDriverClassName(this.driverClass);
        poolConfig.setFairQueue(this.useFairQueue);
        poolConfig.setInitialSize(this.initialSize);
        poolConfig.setInitSQL(this.initializationQuery);
        poolConfig.setLogAbandoned(this.logAbandonedConnections);
        poolConfig.setLogValidationErrors(this.logValidationErrors);
        poolConfig.setMaxActive(this.maxSize);
        poolConfig.setMaxIdle(this.maxSize);
        poolConfig.setMinIdle(this.minSize);
        if (this.getMaxConnectionAge().isPresent()) {
            poolConfig.setMaxAge(this.maxConnectionAge.toMilliseconds());
        }

        poolConfig.setMaxWait((int)this.maxWaitForConnection.toMilliseconds());
        poolConfig.setMinEvictableIdleTimeMillis((int)this.minIdleTime.toMilliseconds());
        poolConfig.setName(name);
        poolConfig.setUrl(this.url);
        poolConfig.setUsername(this.user);
        poolConfig.setPassword(this.user != null && this.password == null ? "" : this.password);
        poolConfig.setRemoveAbandoned(this.removeAbandoned);
        poolConfig.setRemoveAbandonedTimeout(Ints.saturatedCast(this.removeAbandonedTimeout.toSeconds()));
        poolConfig.setTestWhileIdle(this.checkConnectionWhileIdle);
        poolConfig.setValidationQuery(this.validationQuery);
        poolConfig.setTestOnBorrow(this.checkConnectionOnBorrow);
        poolConfig.setTestOnConnect(this.checkConnectionOnConnect);
        poolConfig.setTestOnReturn(this.checkConnectionOnReturn);
        poolConfig.setTimeBetweenEvictionRunsMillis((int)this.evictionInterval.toMilliseconds());
        poolConfig.setValidationInterval(this.validationInterval.toMilliseconds());
        if (this.getValidationQueryTimeout().isPresent()) {
            poolConfig.setValidationQueryTimeout((int)this.validationQueryTimeout.toSeconds());
        }

        if (this.validatorClassName.isPresent()) {
            poolConfig.setValidatorClassName((String)this.validatorClassName.get());
        }

        return new ManagedPooledDataSource(poolConfig, metricRegistry);
    }

    public static enum TransactionIsolation {
        NONE(0),
        DEFAULT(-1),
        READ_UNCOMMITTED(1),
        READ_COMMITTED(2),
        REPEATABLE_READ(4),
        SERIALIZABLE(8);

        private final int value;

        private TransactionIsolation(int value) {
            this.value = value;
        }

        public int get() {
            return this.value;
        }
    }
}

