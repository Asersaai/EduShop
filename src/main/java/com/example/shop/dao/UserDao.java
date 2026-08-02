package com.example.shop.dao;

import com.example.shop.entity.account.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<User,Integer> {
    Optional<User> findByEmailIgnoreCase(String email);

    @Query("""
        SELECT u FROM User u
                WHERE (
                    :search IS NULL
                    OR LOWER(u.userName) LIKE CONCAT('%',CAST(:search AS string),'%')
                    OR LOWER(u.email) LIKE CONCAT('%',CAST(:search AS string),'%'))""")
    Page<User> findBySearchUser(
            @Param("search") String search,
            Pageable pageable
    );

}
