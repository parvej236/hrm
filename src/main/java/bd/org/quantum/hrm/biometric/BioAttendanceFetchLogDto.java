package bd.org.quantum.hrm.biometric;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BioAttendanceFetchLogDto {

    private Long id;
    private String emp_code;
    private String first_name;
    private String last_name;
    private String department;
    private String position;
    private String punch_time;
    private String area_alias;
    private String terminal_alias;
}
