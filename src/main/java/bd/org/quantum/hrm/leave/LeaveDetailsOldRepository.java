package bd.org.quantum.hrm.leave;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LeaveDetailsOldRepository extends JpaRepository<LeaveDetailsOld, Long> {
    @Query(value = "SELECT * FROM hrm.leave_details_old WHERE request = :requestId", nativeQuery = true)
    List<LeaveDetailsOld> findAllByRequest(Long requestId);
}
