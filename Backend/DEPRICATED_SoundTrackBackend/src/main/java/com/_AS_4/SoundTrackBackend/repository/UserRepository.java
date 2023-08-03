package com._AS_4.SoundTrackBackend.repository;

import com._AS_4.SoundTrackBackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long>{
    User findByUsername(String username);
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);


}
