package bd.org.quantum.hrm.employee;

import bd.org.quantum.hrm.common.AuditData;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "khedmotaion_info")
public class KhedmotaionInfo extends AuditData {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee")
    private Employee employee;

    private Long branch;
    private String branchName;

    private Long department;
    private String departmentName;

    @Column(length = 1000)
    private String description;
}
