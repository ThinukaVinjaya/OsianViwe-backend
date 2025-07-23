package com.thinuka.osianViwe_hotel.repository;


import com.thinuka.osianViwe_hotel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String Email);

    void deleteByEmail(String email);

    Optional<User> findByEmail(String email);
}
