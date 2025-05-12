package bd.org.quantum.hrm.attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long>, JpaSpecificationExecutor<Attendance> {

    Attendance getById(Long id);

    @Query(value = "SELECT * FROM hrm.employee_attendance WHERE employee = :employeeId AND attendance_date >= :startDate AND attendance_date <= :endDate", nativeQuery = true)
    List<Attendance> getAttendanceListByEmIdAndDate(@Param(value = "employeeId") long employeeId, @Param(value = "startDate") LocalDate startDate, @Param(value = "endDate") LocalDate endDate);

    @Query(value = "SELECT * FROM hrm.employee_attendance WHERE employee = :employeeId AND attendance_date = :attendanceDate", nativeQuery = true)
    List<Attendance> getAttendance(@Param(value = "employeeId") Long employeeId, @Param(value = "attendanceDate") LocalDate date);

    @Query(value = "SELECT i.date_from FROM hrm.type_info i JOIN hrm.employee e ON i.employee = e.id WHERE i.employee = :employeeId AND e.status = :status", nativeQuery = true)
    LocalDate getStatusInfoByEmployeeIdAndStatus(@Param(value = "employeeId") long employeeId, @Param(value = "status") String status);
}
