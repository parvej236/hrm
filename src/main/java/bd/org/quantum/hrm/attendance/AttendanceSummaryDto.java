package bd.org.quantum.hrm.attendance;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class AttendanceSummaryDto implements Serializable {
    private String monthName;
    private int present;
    private int absent;
    private int odOtpTraining;
    private int wpd;
    private int cl;
    private int yr;
    private int lwp;
    private int otherLeave;
    private int total;

    private String name;
    private LocalDate date;
    private String type;
}
