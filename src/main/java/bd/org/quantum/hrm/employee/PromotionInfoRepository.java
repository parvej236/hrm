package bd.org.quantum.hrm.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PromotionInfoRepository extends JpaRepository<PromotionInfo, Long> {
    PromotionInfo getById(Long id);
    List<PromotionInfo> findAllByEmployeeIdOrderById(Long employeeId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE member.member SET occupation_designation = :designation, foundation_designation = :designation WHERE id = :memberId", nativeQuery = true)
    public void updateMemberDesignation(String designation, long memberId);
}
