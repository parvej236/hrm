package bd.org.quantum.hrm.employee;

import bd.org.quantum.hrm.employee.ExperienceInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperienceInfoRepository extends JpaRepository<ExperienceInfo, Long> {
    List<ExperienceInfo> findAllByEmployeeIdOrderById(Long employeeId);
}
