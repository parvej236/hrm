package bd.org.quantum.hrm.biometric;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BioAttendanceListCmd {

    private Long empId;
    private String empCode;
    private String name;
    private String branch;
    private String department;
    private String designation;
    private String punchedOn;
    private String inTime;
    private String outTime;
}
