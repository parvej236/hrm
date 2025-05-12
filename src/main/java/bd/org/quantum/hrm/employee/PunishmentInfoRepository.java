package bd.org.quantum.hrm.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PunishmentInfoRepository extends JpaRepository<PunishmentInfo, Long> {
    List<PunishmentInfo> findAllByEmployeeIdOrderById(Long employeeId);
}
