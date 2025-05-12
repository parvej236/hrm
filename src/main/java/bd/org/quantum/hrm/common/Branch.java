package bd.org.quantum.hrm.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Branch {
    private Long id;
    private String name;
    private String code;
    private Integer status;
    private Integer type;
    private Long parentBranch;

    public Branch(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
