package bd.org.quantum.hrm.movement;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MovementSearchCriteria {
    private Long employee;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Long branch;
    private List<Long> branches;
    private Long department;
    private List<Long> departments;
    private String stage;

    private Long movementId;
    private String name;
    private String employeeId;
    private String designationName;
    private String departmentName;
    private String branchName;
    private String dateRange;
}
