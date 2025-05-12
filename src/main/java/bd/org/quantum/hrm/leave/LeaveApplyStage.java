package bd.org.quantum.hrm.leave;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LeaveApplyStage {
    APPLIED("Applied"),
    AUTHORIZED("Authorized"),
    APPROVED("Approved"),
    CONFIRMED("Confirmed"),
    REJECTED("Rejected");

    @NonNull
    private final String title;
}
