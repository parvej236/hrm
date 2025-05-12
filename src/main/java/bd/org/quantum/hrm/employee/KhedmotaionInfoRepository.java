package bd.org.quantum.hrm.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KhedmotaionInfoRepository extends JpaRepository<KhedmotaionInfo, Long> {
    KhedmotaionInfo getById(Long id);
    List<KhedmotaionInfo> findAllByEmployeeIdOrderById(Long employeeId);
}
