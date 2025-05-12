package bd.org.quantum.hrm.employee;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeDto {
    private Long id;
    private String name;
    private String employeeId;
    private String designation;
    private String joiningDate;
    private String phone;
    private String email;
    private String branch;
    private String department;
}
