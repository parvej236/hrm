package bd.org.quantum.hrm.idCard;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class IdCardCriteria {
    private String employeeName;
    private String regCode;
    private String employeeId;
    private String primaryPhone;
    private long branch;
    private boolean withSub;
    private long department;
    private boolean includeDeptSub;
    private long designation;
    private String gender;
    private String status;
    private int year;
    private String orderType;

    // Entry Form
    private List<IdCardRecords> orderList;

    private String errorMessage;
    private String action;
}
