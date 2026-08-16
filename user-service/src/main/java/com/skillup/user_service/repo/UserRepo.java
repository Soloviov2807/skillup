package com.skillup.user_service.repo;

import com.skillup.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {


    Optional<User> findById(Long id);

    Optional<User> findByName(String username);

    boolean existsByNameOrEmail(String username, String email);

    boolean existsByEmail(String email);


    Optional<User> findByEmail(String email);
}
