package bd.org.quantum.hrm.employee;

import bd.org.quantum.hrm.common.AuditData;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "extra_qualification_info")
public class ExtraQualificationInfo extends AuditData {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee")
    private Employee employee;

    @Column(length = 1000)
    private String description;
}
