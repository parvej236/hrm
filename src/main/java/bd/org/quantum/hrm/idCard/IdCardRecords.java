package bd.org.quantum.hrm.idCard;

import bd.org.quantum.common.utils.DateUtils;
import bd.org.quantum.hrm.common.AuditData;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "id_card_records")
public class IdCardRecords extends AuditData {
    private Long employee;
    private Long branchId;
    private Long departmentId;
    private Long designationId;

    private String employeeId;
    private String employeeName;
    private String branchName;
    private String departmentName;
    private String designation;
    private String bloodGroup;
    private String stage;
    private String encriptedId;
    private String imagePath;

    private Long orderedBy;
    private Long processedBy;
    private Long acceptedBy;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    private LocalDate orderedOn;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    private LocalDate processedOn;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    private LocalDate acceptedOn;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    private LocalDate expireOn;

    private int year;

    @Transient
    private String qrText;
    @Transient
    private String qrCodeBase64;
    @Transient
    private String barcodeBase64;
    @Transient
    private String base64;
    @Transient
    private Long[] exportCards;
    @Transient
    private boolean cardSelected;

    public String getQrText() {
        return "https://hrm.quantum.org.bd/api/verify-id-card/" + getEncriptedId() + "/" + getYear();
    }
}
