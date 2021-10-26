package com.security.practice.repository;

import com.security.practice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

//JpaRepository 가 CRUD 함수를 들고있음
//@Repository라는 어노테이션이 없어도 Ioc가 됨.
public interface UserRepository extends JpaRepository<User,Long> {
    // select * form user where username=1?  => 1? 파라미터가 들어옴
    User findByUsername(String username);
}
