package bd.org.quantum.hrm.employee;

import bd.org.quantum.common.utils.SearchForm;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmployeeSearch extends SearchForm {
    private String name;
    private String regCode;
    private String employeeId;
    private String gender;
    private String primaryPhone;
    private String designation;
    private String empStatus;
    private List<String> statuses;
    private Long branch;
    private List<Long> branches;
    private Long department;
    private List<Long> departments;

    public EmployeeSearch() {
        setSortBy("id");
        setSortDirection("ASC");
    }
}
