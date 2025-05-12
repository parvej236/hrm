package bd.org.quantum.hrm.changeHistory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hrm_change_history")
public class ChangeHistory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date actionTime;

    @Column(length = 200)
    private String changedByName;

    @Column(length = 200)
    private String className;

    private long dataId;

    @Column(columnDefinition = "TEXT", length = 3000)
    private String jsonData;

    private Long changedBy;
}
