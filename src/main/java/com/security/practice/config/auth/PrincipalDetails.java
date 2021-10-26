package com.security.practice.config.auth;

/*TODO
시큐리티가 /login 주소 요청을 낚아채서 로그인을 진행시킨다.
로그인 진행이 완료가 되면 session 을 만들어준다. (Security ContextHolder)
오브젝트 => Authentication 타입 객체
Authentication 안에 User 정보가 있어야 됨.
User 오브젝트 타입 => UserDetails 타입 객체

Security Session => Authentication => UserDetails 타입으로 들어가 있어야 함함
*/

import com.security.practice.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class PrincipalDetails implements UserDetails {

    private User user; //콤포지션

    public PrincipalDetails(User user){
        this.user=user;
    }

    //해당 User 의 권한을 리턴하는 곳
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collections = new ArrayList<>();
        collections.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        return collections;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
