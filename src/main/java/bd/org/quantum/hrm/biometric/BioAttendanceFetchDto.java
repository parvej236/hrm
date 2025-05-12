package bd.org.quantum.hrm.biometric;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BioAttendanceFetchDto {
    private long count;
    private long code;
    private String msg;
    private String next;
    private String previous;

    private List<BioAttendanceFetchLogDto> data;
}
