package bd.org.quantum.hrm.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberDto {
    private Long id;
    private String nameEn;
    private String nameBn;
    private String fathersName;
    private String mothersName;

    private Long branch;
    private String branchName;
    private String status;
    private String gender;
    private String religion;
    private String bloodGroup;

    private String dateOfBirth;
    private String dateOfMarriage;

    private String maritalStatus;
    private String spouseName;

    private String registrationCode;
    private String nationalId;

    private String occupation;
    private String occupationDetails;
    private String occupationInstitute;
    private String occupationLocation;
    private String educationalQualification;

    private String primaryPhone;
    private String secondaryPhone;
    private String phoneOffice;
    private String phoneOthers;
    private String primaryEmail;
    private String secondaryEmail;

    private String permanentAddress;
    private String presentAddress;
    private String presentDistrict;
    private String presentThana;
    private String presentUnion;

    private String memberType;
}
