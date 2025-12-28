package org.kun.backend.repository;

import org.kun.backend.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Query("""
       select t from RefreshToken t inner join t.user u
       where u.id = :userId and t.revoked = false and t.expired = false
    """)
    List<RefreshToken> findAllValidTokenByUser(Long userId);
}




