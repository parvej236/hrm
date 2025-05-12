package bd.org.quantum.hrm.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface StatusInfoRepository extends JpaRepository<StatusInfo, Long> {
    StatusInfo getById(Long id);
    List<StatusInfo> findAllByEmployeeIdOrderById(Long employeeId);
    StatusInfo findTopByEmployeeIdOrderByIdDesc(long employeeId);
    StatusInfo findTopByEmployeeIdAndStatusToOrderByIdDesc(long employeeId, String status);

    @Transactional
    @Modifying
    @Query(value = "UPDATE member.member SET member_category = :category WHERE id = :memberId", nativeQuery = true)
    public void updateMemberCategory(String category, long memberId);
}
