package io.amtech.projectflow.repository;

import io.amtech.projectflow.domain.employee.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
}
