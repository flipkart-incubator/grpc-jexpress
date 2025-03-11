package com.flipkart.gjex.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.metrics5.MetricRegistry;
import io.dropwizard.util.Duration;
import io.dropwizard.validation.MinDuration;
import io.dropwizard.validation.ValidationMethod;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import javax.annotation.Nullable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class CustomDataSourceFactory implements PooledDataSourceFactory {

    private static final String DEFAULT_VALIDATION_QUERY = "/* Health Check */ SELECT 1";

    @SuppressWarnings("UnusedDeclaration")
    public enum TransactionIsolation {
        NONE(Connection.TRANSACTION_NONE),
        DEFAULT(org.apache.tomcat.jdbc.pool.DataSourceFactory.UNKNOWN_TRANSACTIONISOLATION),
        READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED),
        READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),
        REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),
        SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE);

        private final int value;

        TransactionIsolation(int value) {
            this.value = value;
        }

        public int get() {
            return value;
        }
    }

    @Nullable
    private String driverClass;

    @Min(0)
    @Max(100)
    private int abandonWhenPercentageFull = 0;

    private boolean alternateUsernamesAllowed = false;

    private boolean commitOnReturn = false;

    private boolean rollbackOnReturn = false;

    @Nullable
    private Boolean autoCommitByDefault;

    @Nullable
    private Boolean readOnlyByDefault;

    @Nullable
    private String user;

    @Nullable
    private String password;

    private String url = "";

    @NotNull
    private Map<String, String> properties = new LinkedHashMap<>();

    @Nullable
    private String defaultCatalog;

    @NotNull
    private DataSourceFactory.TransactionIsolation defaultTransactionIsolation = DataSourceFactory.TransactionIsolation.DEFAULT;

    private boolean useFairQueue = true;

    @Min(0)
    private int initialSize = 10;

    @Min(0)
    private int minSize = 10;

    @Min(1)
    private int maxSize = 100;

    @Nullable
    private String initializationQuery;

    private boolean logAbandonedConnections = false;

    private boolean logValidationErrors = false;

    @MinDuration(value = 0, unit = TimeUnit.MILLISECONDS, inclusive = false)
    @Nullable
    private Duration maxConnectionAge;

    @NotNull
    @MinDuration(value = 0, unit = TimeUnit.MILLISECONDS, inclusive = false)
    private Duration maxWaitForConnection = Duration.seconds(30);

    @NotNull
    @MinDuration(value = 0, unit = TimeUnit.MILLISECONDS, inclusive = false)
    private Duration minIdleTime = Duration.minutes(1);

    private Optional<String> validationQuery = Optional.of(DEFAULT_VALIDATION_QUERY);

    @MinDuration(value = 0, unit = TimeUnit.MILLISECONDS, inclusive = false)
    @Nullable
    private Duration validationQueryTimeout;

    private boolean checkConnectionWhileIdle = true;

    private boolean checkConnectionOnBorrow = false;

    private boolean checkConnectionOnConnect = true;

    private boolean checkConnectionOnReturn = false;

    private boolean autoCommentsEnabled = true;

    @NotNull
    @MinDuration(value = 0, unit = TimeUnit.MILLISECONDS, inclusive = false)
    private Duration evictionInterval = Duration.seconds(5);

    @NotNull
    @MinDuration(value = 50, unit = TimeUnit.MILLISECONDS)
    private Duration validationInterval = Duration.seconds(30);

    private Optional<String> validatorClassName = Optional.empty();

    private boolean removeAbandoned = false;

    @NotNull
    @MinDuration(value = 0, unit = TimeUnit.MILLISECONDS, inclusive = false)
    private Duration removeAbandonedTimeout = Duration.seconds(60L);

    private Optional<String> jdbcInterceptors = Optional.empty();

    private boolean ignoreExceptionOnPreLoad = false;

    @JsonProperty
    @Override
    public boolean isAutoCommentsEnabled() {
        return autoCommentsEnabled;
    }

    @JsonProperty
    public void setAutoCommentsEnabled(boolean autoCommentsEnabled) {
        this.autoCommentsEnabled = autoCommentsEnabled;
    }

    @Nullable
    @JsonProperty
    @Override
    public String getDriverClass() {
        return driverClass;
    }

    @JsonProperty
    public void setDriverClass(@Nullable String driverClass) {
        this.driverClass = driverClass;
    }

    @JsonProperty
    @Nullable
    public String getUser() {
        return user;
    }

    @JsonProperty
    public void setUser(@Nullable String user) {
        this.user = user;
    }

    @JsonProperty
    @Nullable
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(@Nullable String password) {
        this.password = password;
    }

    @JsonProperty
    @Override
    public String getUrl() {
        return url;
    }

    @JsonProperty
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty
    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    @JsonProperty
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @JsonProperty
    public Duration getMaxWaitForConnection() {
        return maxWaitForConnection;
    }

    @JsonProperty
    public void setMaxWaitForConnection(Duration maxWaitForConnection) {
        this.maxWaitForConnection = maxWaitForConnection;
    }

    @Override
    @JsonProperty
    public Optional<String> getValidationQuery() {
        return validationQuery;
    }

    /**
     * @deprecated use {@link #getValidationQuery()} instead
     */
    @Override
    @Deprecated
    @JsonIgnore
    public String getHealthCheckValidationQuery() {
        return getValidationQuery().orElse(DEFAULT_VALIDATION_QUERY);
    }

    @JsonProperty
    public void setValidationQuery(@Nullable String validationQuery) {
        this.validationQuery = Optional.ofNullable(validationQuery);
    }

    @JsonProperty
    public int getMinSize() {
        return minSize;
    }

    @JsonProperty
    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }

    @JsonProperty
    public int getMaxSize() {
        return maxSize;
    }

    @JsonProperty
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    @JsonProperty
    public boolean getCheckConnectionWhileIdle() {
        return checkConnectionWhileIdle;
    }

    @JsonProperty
    public void setCheckConnectionWhileIdle(boolean checkConnectionWhileIdle) {
        this.checkConnectionWhileIdle = checkConnectionWhileIdle;
    }

    /**
     * @deprecated use {@link #getReadOnlyByDefault()} instead
     */
    @Deprecated
    @JsonProperty
    public boolean isDefaultReadOnly() {
        return Boolean.TRUE.equals(readOnlyByDefault);
    }

    /**
     * @deprecated use {@link #setReadOnlyByDefault(Boolean)} instead
     */
    @Deprecated
    @JsonProperty
    public void setDefaultReadOnly(boolean defaultReadOnly) {
        readOnlyByDefault = defaultReadOnly;
    }

    @JsonIgnore
    @ValidationMethod(message = ".minSize must be less than or equal to maxSize")
    public boolean isMinSizeLessThanMaxSize() {
        return minSize <= maxSize;
    }

    @JsonIgnore
    @ValidationMethod(message = ".initialSize must be less than or equal to maxSize")
    public boolean isInitialSizeLessThanMaxSize() {
        return initialSize <= maxSize;
    }

    @JsonIgnore
    @ValidationMethod(message = ".initialSize must be greater than or equal to minSize")
    public boolean isInitialSizeGreaterThanMinSize() {
        return minSize <= initialSize;
    }

    @JsonProperty
    public int getAbandonWhenPercentageFull() {
        return abandonWhenPercentageFull;
    }

    @JsonProperty
    public void setAbandonWhenPercentageFull(int percentage) {
        this.abandonWhenPercentageFull = percentage;
    }

    @JsonProperty
    public boolean isAlternateUsernamesAllowed() {
        return alternateUsernamesAllowed;
    }

    @JsonProperty
    public void setAlternateUsernamesAllowed(boolean allow) {
        this.alternateUsernamesAllowed = allow;
    }

    @JsonProperty
    public boolean getCommitOnReturn() {
        return commitOnReturn;
    }

    @JsonProperty
    public boolean getRollbackOnReturn() {
        return rollbackOnReturn;
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
    @Nullable
    public Boolean getAutoCommitByDefault() {
        return autoCommitByDefault;
    }

    @JsonProperty
    public void setAutoCommitByDefault(@Nullable Boolean autoCommit) {
        this.autoCommitByDefault = autoCommit;
    }

    @JsonProperty
    @Nullable
    public String getDefaultCatalog() {
        return defaultCatalog;
    }

    @JsonProperty
    public void setDefaultCatalog(@Nullable String defaultCatalog) {
        this.defaultCatalog = defaultCatalog;
    }

    @JsonProperty
    @Nullable
    public Boolean getReadOnlyByDefault() {
        return readOnlyByDefault;
    }

    @JsonProperty
    public void setReadOnlyByDefault(@Nullable Boolean readOnlyByDefault) {
        this.readOnlyByDefault = readOnlyByDefault;
    }

    @JsonProperty
    public DataSourceFactory.TransactionIsolation getDefaultTransactionIsolation() {
        return defaultTransactionIsolation;
    }

    @JsonProperty
    public void setDefaultTransactionIsolation(DataSourceFactory.TransactionIsolation isolation) {
        this.defaultTransactionIsolation = isolation;
    }

    @JsonProperty
    public boolean getUseFairQueue() {
        return useFairQueue;
    }

    @JsonProperty
    public void setUseFairQueue(boolean fair) {
        this.useFairQueue = fair;
    }

    @JsonProperty
    public int getInitialSize() {
        return initialSize;
    }

    @JsonProperty
    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    @JsonProperty
    @Nullable
    public String getInitializationQuery() {
        return initializationQuery;
    }

    @JsonProperty
    public void setInitializationQuery(@Nullable String query) {
        this.initializationQuery = query;
    }

    @JsonProperty
    public boolean getLogAbandonedConnections() {
        return logAbandonedConnections;
    }

    @JsonProperty
    public void setLogAbandonedConnections(boolean log) {
        this.logAbandonedConnections = log;
    }

    @JsonProperty
    public boolean getLogValidationErrors() {
        return logValidationErrors;
    }

    @JsonProperty
    public void setLogValidationErrors(boolean log) {
        this.logValidationErrors = log;
    }

    @JsonProperty
    public Optional<Duration> getMaxConnectionAge() {
        return Optional.ofNullable(maxConnectionAge);
    }

    @JsonProperty
    public void setMaxConnectionAge(@Nullable Duration age) {
        this.maxConnectionAge = age;
    }

    @JsonProperty
    public Duration getMinIdleTime() {
        return minIdleTime;
    }

    @JsonProperty
    public void setMinIdleTime(Duration time) {
        this.minIdleTime = time;
    }

    @JsonProperty
    public boolean getCheckConnectionOnBorrow() {
        return checkConnectionOnBorrow;
    }

    @JsonProperty
    public void setCheckConnectionOnBorrow(boolean checkConnectionOnBorrow) {
        this.checkConnectionOnBorrow = checkConnectionOnBorrow;
    }

    @JsonProperty
    public boolean getCheckConnectionOnConnect() {
        return checkConnectionOnConnect;
    }

    @JsonProperty
    public void setCheckConnectionOnConnect(boolean checkConnectionOnConnect) {
        this.checkConnectionOnConnect = checkConnectionOnConnect;
    }

    @JsonProperty
    public boolean getCheckConnectionOnReturn() {
        return checkConnectionOnReturn;
    }

    @JsonProperty
    public void setCheckConnectionOnReturn(boolean checkConnectionOnReturn) {
        this.checkConnectionOnReturn = checkConnectionOnReturn;
    }

    @JsonProperty
    public Duration getEvictionInterval() {
        return evictionInterval;
    }

    @JsonProperty
    public void setEvictionInterval(Duration interval) {
        this.evictionInterval = interval;
    }

    @JsonProperty
    public Duration getValidationInterval() {
        return validationInterval;
    }

    @JsonProperty
    public void setValidationInterval(Duration validationInterval) {
        this.validationInterval = validationInterval;
    }

    @Override
    @JsonProperty
    public Optional<Duration> getValidationQueryTimeout() {
        return Optional.ofNullable(validationQueryTimeout);
    }

    @JsonProperty
    public Optional<String> getValidatorClassName() {
        return validatorClassName;
    }

    @JsonProperty
    public void setValidatorClassName(Optional<String> validatorClassName) {
        this.validatorClassName = validatorClassName;
    }

    @Override
    @Deprecated
    @JsonIgnore
    public Optional<Duration> getHealthCheckValidationTimeout() {
        return getValidationQueryTimeout();
    }

    @JsonProperty
    public void setValidationQueryTimeout(@Nullable Duration validationQueryTimeout) {
        this.validationQueryTimeout = validationQueryTimeout;
    }

    @JsonProperty
    public boolean isRemoveAbandoned() {
        return removeAbandoned;
    }

    @JsonProperty
    public void setRemoveAbandoned(boolean removeAbandoned) {
        this.removeAbandoned = removeAbandoned;
    }

    @JsonProperty
    public Duration getRemoveAbandonedTimeout() {
        return removeAbandonedTimeout;
    }

    @JsonProperty
    public void setRemoveAbandonedTimeout(Duration removeAbandonedTimeout) {
        this.removeAbandonedTimeout = Objects.requireNonNull(removeAbandonedTimeout);
    }

    @JsonProperty
    public Optional<String> getJdbcInterceptors() {
        return jdbcInterceptors;
    }

    @JsonProperty
    public void setJdbcInterceptors(Optional<String> jdbcInterceptors) {
        this.jdbcInterceptors = jdbcInterceptors;
    }

    @JsonProperty
    public boolean isIgnoreExceptionOnPreLoad() {
        return ignoreExceptionOnPreLoad;
    }

    @JsonProperty
    public void setIgnoreExceptionOnPreLoad(boolean ignoreExceptionOnPreLoad) {
        this.ignoreExceptionOnPreLoad = ignoreExceptionOnPreLoad;
    }

    @Override
    public void asSingleConnectionPool() {
        minSize = 1;
        maxSize = 1;
        initialSize = 1;
    }

    @Override
    public ManagedDataSource build(MetricRegistry metricRegistry, String name) {
        final Properties dbProperties = new Properties();
        properties.forEach(dbProperties::setProperty);

        final PoolProperties poolConfig = new PoolProperties();
        poolConfig.setAbandonWhenPercentageFull(abandonWhenPercentageFull);
        poolConfig.setAlternateUsernameAllowed(alternateUsernamesAllowed);
        poolConfig.setCommitOnReturn(commitOnReturn);
        poolConfig.setRollbackOnReturn(rollbackOnReturn);
        poolConfig.setDbProperties(dbProperties);
        poolConfig.setDefaultAutoCommit(autoCommitByDefault);
        poolConfig.setDefaultCatalog(defaultCatalog);
        poolConfig.setDefaultReadOnly(readOnlyByDefault);
        poolConfig.setDefaultTransactionIsolation(defaultTransactionIsolation.get());
        poolConfig.setDriverClassName(driverClass);
        poolConfig.setFairQueue(useFairQueue);
        poolConfig.setIgnoreExceptionOnPreLoad(ignoreExceptionOnPreLoad);
        poolConfig.setInitialSize(initialSize);
        poolConfig.setInitSQL(initializationQuery);
        poolConfig.setLogAbandoned(logAbandonedConnections);
        poolConfig.setLogValidationErrors(logValidationErrors);
        poolConfig.setMaxActive(maxSize);
        poolConfig.setMaxIdle(maxSize);
        poolConfig.setMinIdle(minSize);

        getMaxConnectionAge().map(Duration::toMilliseconds).ifPresent(poolConfig::setMaxAge);
        poolConfig.setMaxWait((int) maxWaitForConnection.toMilliseconds());
        poolConfig.setMinEvictableIdleTimeMillis((int) minIdleTime.toMilliseconds());
        poolConfig.setName(name);
        poolConfig.setUrl(url);
        poolConfig.setUsername(user);
        poolConfig.setPassword(user != null && password == null ? "" : password);
        poolConfig.setRemoveAbandoned(removeAbandoned);
        poolConfig.setRemoveAbandonedTimeout((int) removeAbandonedTimeout.toSeconds());

        poolConfig.setTestWhileIdle(checkConnectionWhileIdle);
        validationQuery.ifPresent(poolConfig::setValidationQuery);
        poolConfig.setTestOnBorrow(checkConnectionOnBorrow);
        poolConfig.setTestOnConnect(checkConnectionOnConnect);
        poolConfig.setTestOnReturn(checkConnectionOnReturn);
        poolConfig.setTimeBetweenEvictionRunsMillis((int) evictionInterval.toMilliseconds());
        poolConfig.setValidationInterval(validationInterval.toMilliseconds());

        getValidationQueryTimeout().map(x -> (int) x.toSeconds()).ifPresent(poolConfig::setValidationQueryTimeout);
        validatorClassName.ifPresent(poolConfig::setValidatorClassName);
        jdbcInterceptors.ifPresent(poolConfig::setJdbcInterceptors);
        return new ManagedPooledDataSource(poolConfig, metricRegistry);
    }
}

