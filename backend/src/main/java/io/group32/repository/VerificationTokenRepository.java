package io.group32.repository;

import io.group32.model.TokenType;
import io.group32.model.VerificationToken;
import io.group32.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    @EntityGraph(attributePaths = {"user"})
    Optional<VerificationToken> findByTokenHashAndTokenTypeAndUsedFalseAndExpiryDateAfter(String lookupHash, TokenType tokenType, LocalDateTime expiryDateTime);
    Optional<VerificationToken> findByTokenHashAndTokenTypeAndUserAndUsedFalseAndExpiryDateAfter(String lookupHash, TokenType tokenType, User user, LocalDateTime expiryDateTime);
    List<VerificationToken> findByUserAndTokenType(User user, TokenType tokenType);
}
