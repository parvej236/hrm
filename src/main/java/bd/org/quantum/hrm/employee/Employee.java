package bd.org.quantum.hrm.employee;

import bd.org.quantum.common.utils.DateUtils;
import bd.org.quantum.hrm.common.AuditData;
import bd.org.quantum.hrm.designation.Designation;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employee")
public class Employee extends AuditData {
    private static final long serialVersionUID = 1L;

    public enum Stage {
        SAVE(0), SUBMIT(1);

        private int value;

        Stage(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private Long member;
    private String nameEn;
    private String nameBn;
    private String fathersName;
    private String mothersName;
    private String spouseName;
    private String maritalStatus;
    private String gender;
    private String bloodGroup;
    private String religion;
    private String regCode;
    private String memberType;

    @Column(length = 500)
    private String presentAddress;
    private String presentDistrict;
    private String presentThana;
    private String presentUnion;

    @Column(length = 500)
    private String permanentAddress;

    private String primaryPhone;
    private String secondaryPhone;
    private String primaryEmail;
    private String secondaryEmail;
    private String employeeId;
    private String tempEmployeeId;
    private String nationalId;
    private String nationalIdFather;
    private String nationalIdMother;
    private String nationalIdSpouse;

    private Long branch;
    private String branchName;
    private Long department;
    private String departmentName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "designation")
    private Designation designation;

    private String designationName;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    private LocalDate dateOfBirth;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    private LocalDate dateOfMarriage;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    private LocalDate lastPromotionDate;

    private Integer joinType;
    private Double remuneration;

    private String wpd;
    private String salaryLocation;

    private int preConsumedYr;

    @Column(length = 500)
    private String responsibility;

    @Column(length = 1000)
    private String remarks;

    @Column(length = 1000)
    private String reference;

    private Boolean isDeleted;
    private String status;
    private Integer stage;

    private String imagePath;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    private LocalDate regularDate;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    private LocalDate hiring;

    @Transient
    @JsonSerialize
    @JsonDeserialize
    private String action;

    @Transient
    @JsonSerialize
    @JsonDeserialize
    private String base64;
}
