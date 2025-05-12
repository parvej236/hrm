package bd.org.quantum.hrm.designation;

import bd.org.quantum.hrm.common.AuditData;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "designation")
public class Designation extends AuditData {
    private String name;
    private String sortingOrder;
    private String grade;
    private int status;
    private String remarks;
}
