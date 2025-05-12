package bd.org.quantum.hrm.movement;

import bd.org.quantum.hrm.attendance.AttendanceDao;
import bd.org.quantum.hrm.attendance.AttendanceRepository;
import bd.org.quantum.hrm.attendance.Attendance;
import bd.org.quantum.hrm.attendance.AttendanceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class MovementAttendanceService {
    private final MovementAttendanceRepository repository;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceDao attendanceDao;

    public MovementAttendanceService(MovementAttendanceRepository repository,
                                     AttendanceRepository attendanceRepository,
                                     AttendanceDao attendanceDao) {
        this.repository = repository;
        this.attendanceRepository = attendanceRepository;
        this.attendanceDao = attendanceDao;
    }

    public MovementAttendance create(MovementAttendance movement) {
        if (movement.getStage().equals("AUTHORIZE")) {
            LocalDate dateFrom = movement.getDateFrom();
            LocalDate dateTo = movement.getDateTo();
            while (dateFrom.isBefore(dateTo) || dateFrom.equals(dateTo)) {
                Attendance attendance = new Attendance();
                attendance.setEmployee(movement.getEmployee());
                attendance.setAttendanceDate(dateFrom);
                attendance.setAttendanceType(AttendanceType.OFFICE_DUTY.getValue());
                attendance.setAttendanceStatus(1);
                attendance.setAttendanceFrom("Movement");
                attendanceRepository.save(attendance);             
                
                dateFrom = dateFrom.plusDays(1);
            }
        }
        return repository.save(movement);
    }

    public MovementAttendance getById(Long id) {
        return repository.getById(id);
    }

    public List<MovementSearchCriteria> getMovementList(MovementSearchCriteria criteria) {
        return attendanceDao.getMovementList(criteria);
    }

    public void validateMovement (MovementAttendance movement, BindingResult result) {
        if (movement.getEmployee() == null || movement.getEmployee().getId() == null || movement.getEmployee().getId() == 0) {
            result.rejectValue("employee.id", "error.required");
        }

        if (movement.getDateFrom() == null || movement.getDateTo() == null) {
            result.rejectValue("dateFrom", "error.required");
        }

        if (StringUtils.isEmpty(movement.getPurpose())) {
            result.rejectValue("purpose", "error.required");
        }
    }
}
