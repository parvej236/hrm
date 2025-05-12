package bd.org.quantum.hrm.biometric;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BioAttendanceCriteria {
    private String emp_code;
    private String start_time;
    private String end_time;
}
