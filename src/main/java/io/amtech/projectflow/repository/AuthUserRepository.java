package io.amtech.projectflow.repository;

import io.amtech.projectflow.app.exception.ObjectNotFoundException;
import io.amtech.projectflow.domain.employee.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
    Optional<AuthUser> findByLogin(String login);

    default AuthUser findByLoginOrThrow(String login) {
        return findByLogin(login)
                .orElseThrow(() -> new ObjectNotFoundException(AuthUser.class.getSimpleName(), login));
    }
}
