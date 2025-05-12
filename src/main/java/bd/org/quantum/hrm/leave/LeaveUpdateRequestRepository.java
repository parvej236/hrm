package bd.org.quantum.hrm.leave;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveUpdateRequestRepository extends JpaRepository<LeaveUpdateRequest, Long> {
    LeaveUpdateRequest getById(Long id);
}
