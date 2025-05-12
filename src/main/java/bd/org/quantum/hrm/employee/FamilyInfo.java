package bd.org.quantum.hrm.employee;

import bd.org.quantum.common.utils.DateUtils;
import bd.org.quantum.hrm.common.AuditData;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "family_info")
public class FamilyInfo extends AuditData {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee")
    private Employee employee;

    private String name;
    private String relation;

    @DateTimeFormat(pattern = DateUtils.DD_MM_YYYY)
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY)
    private LocalDate birthDate;

    private String phone;
    private String comments;
}
