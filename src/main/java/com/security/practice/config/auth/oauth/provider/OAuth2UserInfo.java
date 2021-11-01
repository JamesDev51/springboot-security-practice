package com.security.practice.config.auth.oauth.provider;

public interface OAuth2UserInfo {
    Object getProviderId();
    String getProvider();
    String getEmail();
    String getName();
}
