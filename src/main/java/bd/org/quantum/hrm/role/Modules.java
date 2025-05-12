package bd.org.quantum.hrm.role;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Modules {
    EMPLOYEE("Employee"),
    ATTENDANCE("Attendance"),
    LEAVE("Leave"),
    ID_CARD("ID Card"),
    EVALUATION("Evaluation");

    @NonNull
    private String title;
}
