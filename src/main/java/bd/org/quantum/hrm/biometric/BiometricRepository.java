package bd.org.quantum.hrm.biometric;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BiometricRepository extends JpaRepository<BioAttendanceLog, Long>, JpaSpecificationExecutor<BioAttendanceLog> {
}
