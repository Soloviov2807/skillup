package com.skillup.user_service.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

    public class UserPrincipal implements UserDetails {

        private final User user;

        public UserPrincipal(User user) {
            this.user = user;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return user.getRoles()
                    .stream()
                    .map(role -> new SimpleGrantedAuthority(role.name()))
                    .toList();
        }

        @Override
        public String getPassword() {
            return user.getPassword();
        }

        public long getUserId(){
            return user.getId();
        }

        public User getUser(){
            return user;
        }

        @Override
        public String getUsername() {
            return user.getName();
        }
    }
