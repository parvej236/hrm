package bd.org.quantum.hrm.employee;

import bd.org.quantum.hrm.common.AuditData;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "experience_info")
public class ExperienceInfo extends AuditData {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee")
    private Employee employee;

    private String designation;
    private String institution;
    private String duration;

    @Column(length = 500)
    private String responsibility;
    @Column(length = 500)
    private String comments;
}
