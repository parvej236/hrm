package bd.org.quantum.hrm.report;

import lombok.Data;

@Data
public class EmployeeStatementCommand {
    private String designation;
    private int qpmMale;
    private int qpmFemale;
    private int qpmTotal;
    private int qgMale;
    private int qgFemale;
    private int qgTotal;
    private int qaMale;
    private int qaFemale;
    private int qaTotal;
    private int totalMale;
    private int totalFemale;
    private int total;
    private int regularCount;
    private int irregularCount;
    private int othersCount;

    // for monthly employee statement report
    private String branchName;
    private int prevMonthCount;
    private int joinedInCurMonth;
    private int transferedInCurMonth;
    private int resignedInCurMonth;
    private int curMonthCount;
}
