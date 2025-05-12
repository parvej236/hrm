package bd.org.quantum.hrm.leave;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YrEncashRepository extends JpaRepository<YrEncash, Long> {
    List<YrEncash> findAllByEmployeeId(Long employeeId);
}
