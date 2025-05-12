package bd.org.quantum.hrm.employee;

import bd.org.quantum.hrm.common.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmployeeInfoChangeHistoryDto {

    /* Personal Info*/
    @JsonIgnore
    private long id;
    private String nameEn;
    private String nameBn;
    private String fathersName;
    private String mothersName;
    private String maritalStatus;
    private String spouseName;
    private LocalDate dateOfBirth;
    private LocalDate dateOfMarriage;
    private String gender;
    private String bloodGroup;
    private String religion;
    private String regCode;
    private String nationalId;
    private String nationalIdFather;
    private String nationalIdMother;
    private String nationalIdSpouse;

    /* Official Info*/
    private String joiningDate;
    private String employeeId;
    private String branchName;
    private String departmentName;
    private String designation;
    private String responsibility;
    private String reference;
    private String remarks;

    /* Contact Info*/
    private String mailingAddress;
    private String permanentAddress;
    private String primaryPhone;
    private String secondaryPhone;
    private String primaryEmail;
    private String secondaryEmail;

    public EmployeeInfoChangeHistoryDto(Employee employee) {
        this.id = employee.getId();
        this.nameEn = employee.getNameEn() != null ? employee.getNameEn() : "";
        this.nameBn = employee.getNameBn() != null ? employee.getNameBn() : "";
        this.fathersName = employee.getFathersName() != null ? employee.getFathersName() : "";
        this.mothersName = employee.getMothersName() != null ? employee.getMothersName() : "";
        this.maritalStatus = employee.getMaritalStatus() != null ? employee.getMaritalStatus() : "";
        this.spouseName = employee.getSpouseName() != null ? employee.getSpouseName() : "";
        this.dateOfBirth = employee.getDateOfBirth();
        this.dateOfMarriage = employee.getDateOfMarriage();
        this.gender = Gender.getAllGender().get(employee.getGender());
        this.bloodGroup = employee.getBloodGroup() != null ? employee.getBloodGroup() : "";
        this.religion = employee.getReligion() != null ? employee.getReligion() : "";
        this.regCode = employee.getRegCode() != null ? employee.getRegCode() : "";
        this.nationalId = employee.getNationalId() != null ? employee.getNationalId() : "";
        this.nationalIdFather = employee.getNationalIdFather() != null ? employee.getNationalIdFather() : "";
        this.nationalIdMother = employee.getNationalIdMother() != null ? employee.getNationalIdMother() : "";
        this.nationalIdSpouse = employee.getNationalIdSpouse() != null ? employee.getNationalIdSpouse() : "";

        this.joiningDate = employee.getHiring() != null ? String.valueOf(employee.getHiring()) :"";
        this.employeeId = employee.getEmployeeId() != null ? employee.getEmployeeId() : "";
        this.branchName = employee.getBranchName() != null ? employee.getBranchName() : "";
        this.departmentName = employee.getDepartmentName() != null ? employee.getDepartmentName() : "";
        this.designation = employee.getDesignationName() != null ? employee.getDesignationName() : "";
        this.responsibility = employee.getResponsibility() != null ? employee.getResponsibility() : "";
        this.reference = employee.getReference() != null ? employee.getReference() : "";
        this.remarks = employee.getRemarks() != null ? employee.getRemarks() : "";

        this.mailingAddress = employee.getPresentAddress() != null ? employee.getPresentAddress() : "";
        this.permanentAddress = employee.getPermanentAddress() != null ? employee.getPermanentAddress() : "";
        this.primaryPhone = employee.getPrimaryPhone() != null ? employee.getPrimaryPhone() : "";
        this.secondaryPhone = employee.getSecondaryPhone() != null ? employee.getSecondaryPhone() : "";
        this.primaryEmail = employee.getPrimaryEmail() != null ? employee.getPrimaryEmail() : "";
        this.secondaryEmail = employee.getSecondaryEmail() != null ? employee.getSecondaryEmail() : "";
    }
}