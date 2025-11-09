package com.godesii.godesii_services.config;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

public class UserPrincipal implements UserDetails {

    private String id;
    private String username;
    private String password;
    private String emailId;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean isEnabled;
    private Set<String> roles;
    private boolean isMfaEnabled;
    private boolean mfaRegistered;
    private String mfaBackupKey;
    private String mfaSecret;
    private String mfaKeyId;
    private Collection< ? extends GrantedAuthority> authorities;

    public String getId() {
        return this.id;
    }

    public String getEmailId() {
        return this.emailId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    public boolean isMfaEnabled(){
        return this.isMfaEnabled;
    }

    public boolean isMfaRegistered(){
        return this.mfaRegistered;
    }

    public String getMfaBackupKey(){
        return this.mfaBackupKey;
    }

    public String getMfaSecret(){
        return this.mfaSecret;
    }

    public String getMfaKeyId(){
        return this.mfaKeyId;
    }

    public Set<String> getRoles(){
        return this.roles;
    }

    public String getRolesAsString(){
        return StringUtils.collectionToCommaDelimitedString(getRoles());
    }

    public static Builder buildWithId(String id){
        if(id == null)
            throw new IllegalArgumentException("Id can't be null!");
        return new Builder(id);
    }

    public static class Builder implements Serializable{

        private String id;
        private String username;
        private String password;
        private String emailId;
        private boolean accountNonExpired;
        private boolean accountNonLocked;
        private boolean credentialsNonExpired;
        private boolean isEnabled;
        private Set<String> roles;
        private boolean isMfaEnabled;
        private boolean mfaRegistered;
        private String mfaBackupKey;
        private String mfaSecret;
        private String mfaKeyId;
        private Collection<? extends GrantedAuthority> authorities;

        public Builder(String id){
            this.id = id;
        }

        public Builder id(String id){
            this.id = id;
            return this;
        }

        public Builder password(String password){
            this.password = password;
            return this;
        }
        public Builder username(String username){
            this.username =  username;
            return this;
        }

        public Builder emailId(String emailId){
            this.emailId = emailId;
            return this;
        }

        public Builder accountNonExpired(boolean accountExpired){
            this.accountNonExpired = accountExpired;
            return this;
        }

        public Builder accountNonLocked(boolean accountLocked){
            this.accountNonLocked = accountLocked;
            return this;
        }

        public Builder credentialsNonExpired(boolean credentialsExpired){
            this.credentialsNonExpired = credentialsExpired;
            return this;
        }

        public Builder enabled(boolean isEnabled){
            this.isEnabled = isEnabled;
            return this;
        }

        public Builder mfaEnabled(boolean isEnabled){
            this.isMfaEnabled = isEnabled;
            return this;
        }

        public Builder mfaRegistered(boolean mfaRegistered){
            this.mfaRegistered = mfaRegistered;
            return this;
        }

        public Builder mfaBackupKey(String mfaBackupKey){
            this.mfaBackupKey = mfaBackupKey;
            return this;
        }

        public Builder mfaSecret(String mfaSecret){
            this.mfaSecret = mfaSecret;
            return this;
        }

        public Builder mfaKeyId(String mfaKeyId){
            this.mfaKeyId = mfaKeyId;
            return this;
        }

        public Builder roles(Set<String> roles){
            this.roles = roles;
            return this;
        }

        public Builder authorities(Collection<? extends GrantedAuthority> authorities){
            this.authorities = authorities;
            return this;
        }

        public UserPrincipal build(){
            UserPrincipal principal = new UserPrincipal();
            principal.id = this.id;
            principal.username = this.username;
            principal.emailId = this.emailId;
            principal.password = password;
            principal.accountNonExpired = this.accountNonExpired;
            principal.accountNonLocked = this.accountNonLocked;
            principal.credentialsNonExpired = this.credentialsNonExpired;
            principal.isEnabled = this.isEnabled;
            principal.mfaRegistered = this.mfaRegistered;
            principal.isMfaEnabled = this.isMfaEnabled;
            principal.mfaSecret = this.mfaSecret;
            principal.mfaKeyId = this.mfaKeyId;
            principal.mfaBackupKey = this.mfaBackupKey;
            principal.roles = roles;
            principal.authorities = this.authorities;
//            check(principal);
            return principal;
        }

        private static void check(UserPrincipal principal){

            if(!principal.isEnabled())
                throw new DisabledException("Users account is disabled");

            if(!principal.isAccountNonExpired())
                throw new AccountExpiredException("Users account is expired");

            if(!principal.isAccountNonLocked())
                throw new LockedException("Users account is locked");

            if(!principal.isCredentialsNonExpired())
                throw new CredentialsExpiredException("user account credential is expired");
        }

    }
}
