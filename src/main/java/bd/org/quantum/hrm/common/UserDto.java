package bd.org.quantum.hrm.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String registrationNumber;
    private String mobileNumber;
    private String designation;
    private String branchName;

    public UserDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
