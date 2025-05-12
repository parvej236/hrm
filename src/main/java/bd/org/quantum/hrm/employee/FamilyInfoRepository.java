package bd.org.quantum.hrm.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamilyInfoRepository extends JpaRepository<FamilyInfo, Long> {
    List<FamilyInfo> findAllByEmployeeIdOrderById(Long employeeId);
}
