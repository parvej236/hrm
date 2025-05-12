package bd.org.quantum.hrm.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreJoiningInfoRepository extends JpaRepository<PreJoiningInfo, Long> {
    PreJoiningInfo getById(Long id);
    List<PreJoiningInfo> findAllByEmployeeIdOrderById(Long employeeId);
}
