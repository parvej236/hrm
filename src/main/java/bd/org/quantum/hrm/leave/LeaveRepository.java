package bd.org.quantum.hrm.leave;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<LeaveApplication, Long>, JpaSpecificationExecutor<LeaveApplication> {
    LeaveApplication getById(Long id);

    @Query(value = "SELECT a.* FROM hrm.leave_application a " +
            "JOIN hrm.employee e ON e.id = a.applicant " +
            "LEFT JOIN hrm.leave_update_request r ON a.id = r.application_id " +
            "WHERE a.stage IN('Authorized', 'Approved') AND ((e.name_en LIKE ('%' || :employeeInfo || '%')) OR " +
            "(e.primary_phone LIKE ('%' || :employeeInfo || '%')) OR " +
            "(e.secondary_phone LIKE ('%' || :employeeInfo || '%')) OR " +
            "(e.employee_id LIKE ('%' || :employeeInfo)) OR " +
            "((TO_CHAR(e.hiring, 'YY') || e.employee_id) = :employeeInfo) OR " +
            "(e.reg_code LIKE ('%' || :employeeInfo))) " +
            "AND r.application_id IS NULL ORDER BY a.id DESC", nativeQuery = true)
    List<LeaveApplication> getLeaveApplicationsByEmployeeInfo(String employeeInfo);
}
