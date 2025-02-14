package com.flipkart.gjex.core.filter.grpc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AuthConfig {
    private String clientId;
    private boolean isAuthEnabled = true;
    private String authnUrl;
    private List<String> authnUrls;
    private List<String> whiteListedClientIds;
    private String loginUrl = "/login";
    private String authIgnoreUrls = null;
    private String redirectUri = null;
    private String clientSecret;
    private boolean saveRequest = true;
    private boolean enableIstio = false;
    private List<String> scopes;
    private Map<String, String> additionalParams;

    public AuthConfig() {
    }

    public List<String> getMultiAuthnUrls() {
        List<String> authnUrls = this.getAuthnUrls();
        return (List)(authnUrls != null && authnUrls.size() > 0 ? authnUrls : new ArrayList(Arrays.asList(this.getAuthnUrl())));
    }

    public List<String> getWhiteListedClientIds() {
        return this.whiteListedClientIds;
    }

    public void setWhiteListedClientIds(List<String> whiteListedClientIds) {
        this.whiteListedClientIds = whiteListedClientIds;
    }

    public String getAuthnUrl() {
        return this.authnUrl;
    }

    public List<String> getAuthnUrls() {
        return this.authnUrls;
    }

    public void setAuthnUrls(List<String> authnUrls) {
        this.authnUrls = authnUrls;
    }

    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public boolean isAuthEnabled() {
        return this.isAuthEnabled;
    }

    public void setAuthEnabled(boolean isAuthEnabled) {
        this.isAuthEnabled = isAuthEnabled;
    }

    public void setAuthnUrl(String authnUrl) {
        this.authnUrl = authnUrl;
    }

    public String getLoginUrl() {
        return this.loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getAuthIgnoreUrls() {
        return this.authIgnoreUrls;
    }

    public void setAuthIgnoreUrls(String authIgnoreUrls) {
        this.authIgnoreUrls = authIgnoreUrls;
    }

    public String getRedirectUri() {
        return this.redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getClientSecret() {
        return this.clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public List<String> getScopes() {
        return this.scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }

    public Map<String, String> getAdditionalParams() {
        return this.additionalParams;
    }

    public void setAdditionalParams(Map<String, String> additionalParams) {
        this.additionalParams = additionalParams;
    }

    public boolean isSaveRequest() {
        return this.saveRequest;
    }

    public void setSaveRequest(boolean saveRequest) {
        this.saveRequest = saveRequest;
    }

    public boolean isEnableIstio() {
        return this.enableIstio;
    }

    public void setEnableIstio(boolean enableIstio) {
        this.enableIstio = enableIstio;
    }
}
