package bd.org.quantum.hrm.attendance;

import bd.org.quantum.hrm.employee.Employee;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AttendanceStatus {
    private Employee employee;
    private List<AttendanceDateStatus> dateStatusList;
    private Map<Integer, String> typeMap;
}
