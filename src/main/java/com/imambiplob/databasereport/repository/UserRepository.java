package com.imambiplob.databasereport.repository;

import com.imambiplob.databasereport.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsername(String username);

}
