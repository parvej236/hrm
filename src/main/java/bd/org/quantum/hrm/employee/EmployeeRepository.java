package bd.org.quantum.hrm.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
    Employee getById(Long id);
    Employee findByMember(Long memberId);
    boolean existsByEmployeeIdIsNotNullAndEmployeeIdEqualsAndIdNot(String employeeId, Long id);
    boolean existsByDepartmentIsNotNullAndBranchEquals(Long branchId);

    List<Employee> findAllByEmployeeIdIsNullAndStatusInAndBranchInOrderByHiring(List<String> statuses, List<Long> branchIds);
    List<Employee> findAllByTempEmployeeIdIsNullAndStatusAndBranchInOrderByHiring(String status, List<Long> branchIds);

    @Transactional
    @Modifying
    @Query("UPDATE Employee SET imagePath = :path WHERE id = :id")
    void updateImagePath(@Param(value = "path") String path, @Param(value = "id") long id);

    @Query(nativeQuery = true,
            value = "SELECT e.id AS id, e.name_en AS name, CONCAT(TO_CHAR(CAST(e.hiring as DATE), 'yy'), e.employee_id) AS employeeId, " +
                    "e.designation_name AS designation, e.hiring AS joiningDate, e.primary_phone AS phone, " +
                    "e.primary_email AS email, e.branch_name AS branch, e.department_name AS department " +
                    "FROM hrm.employee e " +
                    "WHERE e.status IN ('Regular', 'Irregular', 'Muster Roll', 'Part-Time', 'Honorary', 'Intern') " +
                    "AND CONCAT(TO_CHAR(CAST(e.hiring as DATE), 'yy'), e.employee_id) = :empCode")
    EmployeeProjection getByCode(@Param("empCode") String empCode);

    @Query(nativeQuery = true,
            value = "SELECT COUNT(*) AS id, '' AS name, '' AS employeeId, e.designation_name AS designation, '' AS joiningDate, " +
                    "'' AS phone, '' AS email, '' AS branch, '' AS department " +
                    "FROM hrm.employee e " +
                    "JOIN hrm.designation d on e.designation = d.id " +
                    "WHERE e.status IN ('Regular', 'Irregular', 'Part-Time', 'Honorary', 'Intern') " +
                    "AND (:branchId IS NULL OR e.branch = :branchId) " +
                    "AND (:departmentId IS NULL OR e.department = :departmentId) " +
                    "GROUP BY e.designation_name, d.sorting_order ORDER BY d.sorting_order")
    List<EmployeeProjection> getDesignationStatus(@Param("branchId") Long branchId,
                                            @Param("departmentId") Long departmentId);

    @Query(value = "SELECT e FROM Employee e " +
            "WHERE e.status IN ('Regular', 'Irregular', 'Part-Time', 'Honorary', 'Intern') " +
            "AND (:branchId IS NULL OR e.branch = :branchId) " +
            "AND (:departmentId IS NULL OR e.department = :departmentId) " +
            "AND (:hiringFrom IS NULL OR e.hiring >= :hiringFrom) " +
            "AND (:hiringTo IS NULL OR e.hiring < :hiringTo)")
    List<Employee> getEmployeeList(@Param("branchId") Long branchId,
                                   @Param("departmentId") Long departmentId,
                                   @Param("hiringFrom") LocalDate hiringFrom,
                                   @Param("hiringTo") LocalDate hiringTo);

    @Query(nativeQuery = true, value = "WITH process_data AS ( " +
            "SELECT CAST(SUBSTRING(e.temp_employee_id FROM '^[A-Z]-(\\d{2})') AS INTEGER) AS year, " +
            "CAST(SUBSTRING(e.temp_employee_id FROM '\\d{3}$') AS INTEGER) AS code " +
            "FROM hrm.employee e " +
            "WHERE e.temp_employee_id IS NOT NULL " +
            "AND e.status IN(:statuses) " +
            ") SELECT COALESCE((SELECT code FROM process_data WHERE year = :year ORDER BY code DESC LIMIT 1), 0) AS code")
    int lastTempIdByYearAndStatusIn(int year, List<String> statuses);

    @Query(nativeQuery = true, value = "SELECT hrm.get_next_value('unique_id_emp')")
    int getEmpIdNextValue();

    /*@Query(nativeQuery = true,
            value = "SELECT e.id AS id, m.name AS name, CONCAT(DATE_FORMAT(e.hiring, '%y'), e.employee_id) AS employeeId, " +
                    "e.designation_name AS designation, e.hiring AS joiningDate, m.primary_phone AS phone, " +
                    "m.primary_email AS email, b.name AS branch, d.name AS department " +
                    "FROM hrm.employee e " +
                    "JOIN member m on e.member = m.id " +
                    "JOIN hrm.branch b on e.branch = b.id " +
                    "LEFT JOIN hrm.department d on e.department = d.id " +
                    "WHERE e.status IN (1, 2, 4, 7, 8) " +
                    "AND (CONCAT(DATE_FORMAT(e.hiring, '%y'), e.employee_id) = :regOrEmployeeId OR m.registration_code = :regOrEmployeeId)" )
    List<EmployeeProjection> getEmployeeInfoByRegOrEmployeeId(String regOrEmployeeId);*/

    @Query(nativeQuery = true,
            value = "SELECT e.* " +
                    "FROM hrm.employee e " +
                    "WHERE e.status IN ('Regular', 'Irregular', 'Muster Roll', 'Part-Time', 'Honorary', 'Intern') " +
                    "AND ((e.employee_id LIKE ('%' || :regOrEmployeeId)) OR ((TO_CHAR(e.hiring, 'YY') || e.employee_id) = :regOrEmployeeId) OR (e.reg_code LIKE ('%' || :regOrEmployeeId)))")
    List<Employee> getEmployeeInfoByRegOrEmployeeId(String regOrEmployeeId);

    @Query(nativeQuery = true,
            value = "SELECT e.* " +
                    "FROM hrm.employee e " +
                    "WHERE e.status IN ('Regular', 'Irregular', 'Muster Roll', 'Part-Time', 'Honorary', 'Intern') " +
                    "AND ((e.name_en LIKE ('%' || :employeeInfo || '%')) OR " +
                    "(e.primary_phone LIKE ('%' || :employeeInfo || '%')) OR " +
                    "(e.secondary_phone LIKE ('%' || :employeeInfo || '%')) OR " +
                    "(e.employee_id LIKE ('%' || :employeeInfo)) OR " +
                    "((TO_CHAR(e.hiring, 'YY') || e.employee_id) = :employeeInfo) OR " +
                    "(e.reg_code LIKE ('%' || :employeeInfo)))")
    List<Employee> getEmployeesByEmployeeInfo(String employeeInfo);

    @Query(nativeQuery = true,
            value = "WITH resign_data AS ( " +
                    "SELECT COALESCE( " +
                    "(SELECT date_from FROM hrm.type_info WHERE employee = :empId AND status_to = 'Resigned' ORDER BY id DESC LIMIT 1), " +
                    "NULL) AS resign_date " +
                    "), join_regular_and_resign_data AS ( " +
                    "SELECT e.status, CASE WHEN e.hiring IS NOT NULL THEN 'Joining On: ' || to_char(e.hiring, 'DD/MM/YYYY') ELSE '' END AS join_info, " +
                    "CASE WHEN e.regular_date IS NOT NULL THEN 'Regular On: ' || to_char(e.regular_date, 'DD/MM/YYYY') ELSE '' END AS regular, " +
                    "CASE WHEN (SELECT resign_date FROM resign_data) IS NOT NULL THEN 'Resigned On: ' || to_char((SELECT resign_date FROM resign_data), 'DD/MM/YYYY') ELSE '' END AS resign " +
                    "FROM hrm.employee e " +
                    "WHERE e.id = :empId " +
                    ") SELECT CONCAT((CASE WHEN status IN('Regular', 'Irregular', 'Resigned') AND join_info != '' THEN join_info ELSE join_info END), " +
                    "(CASE WHEN status IN('Regular', 'Resigned') AND regular != '' THEN ', ' || regular END), " +
                    "(CASE WHEN status IN('Resigned') AND resign != '' THEN ', ' || resign END)) " +
                    "FROM join_regular_and_resign_data")
    String statusesWithDate(long empId);
}
