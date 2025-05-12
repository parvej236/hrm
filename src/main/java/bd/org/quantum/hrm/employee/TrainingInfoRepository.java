package bd.org.quantum.hrm.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingInfoRepository extends JpaRepository<TrainingInfo, Long> {
    List<TrainingInfo> findAllByEmployeeIdOrderById(Long employeeId);
}
