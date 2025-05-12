package bd.org.quantum.hrm.movement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MovementAttendanceRepository extends JpaRepository<MovementAttendance, Long>, JpaSpecificationExecutor<MovementAttendance> {
    MovementAttendance getById(Long id);
}
