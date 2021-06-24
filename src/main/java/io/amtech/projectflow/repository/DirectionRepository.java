package io.amtech.projectflow.repository;

import io.amtech.projectflow.app.exception.ObjectNotFoundException;
import io.amtech.projectflow.domain.Direction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectionRepository extends JpaRepository<Direction, Long>, DirectionCustomRepository {
    default Direction findByIdOrThrow(long id) {
        return findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(Direction.class.getSimpleName(), id));
    }
}
