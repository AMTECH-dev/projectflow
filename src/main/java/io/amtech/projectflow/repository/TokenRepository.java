package io.amtech.projectflow.repository;

import io.amtech.projectflow.app.exception.ObjectNotFoundException;
import io.amtech.projectflow.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {
    default Token findByIdOrThrow(String id) {
        return findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(Token.class.getSimpleName(), id));
    }
}
