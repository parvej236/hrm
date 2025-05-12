package bd.org.quantum.hrm.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {
    private Long id;
    private String name;
    private Integer status;
    private Long parentDepartment;

    public Department(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
