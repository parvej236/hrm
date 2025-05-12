package bd.org.quantum.hrm.report;

import lombok.Data;

import java.io.Serializable;

@Data
public class EmployeeReportDto implements Serializable {
    private long id;
    private String name;
    private String employeeId;
    private String phone;
    private String primaryPhone;
    private String secondaryPhone;
    private String phoneOffice;
    private String phoneOthers;
    private String regCode;
    private String designation;
    private String branchName;
    private String departmentName;
    private String joiningDate;
    private String regularDate;
    private String promotionDate;
    private String examName;
    private String subjectDepartment;
    private String institution;
    private String imagePath;
    private String base64;
    private String status;

    private String lastCbc;
    private String memberType;
    private String dateOfBirth;
    private String homeDistrict;
    private String bloodGroup;
    private String eduQualification;
    private String maritalStatus;
    private int son;
    private int daughter;
    private String remarks;

    private int matureCl;
    private int takenCl;
    private int balanceCl;

    private int preConsumedYr;

    private int matureYr;
    private int takenYr;
    private int encashYr;
    private int balanceYr;
}
