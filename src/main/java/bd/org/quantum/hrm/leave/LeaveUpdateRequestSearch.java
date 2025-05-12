package bd.org.quantum.hrm.leave;

import bd.org.quantum.common.utils.SearchForm;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LeaveUpdateRequestSearch extends SearchForm {
    private Long userId;
    private String stage;
    private List<Long> branches;
    private List<Long> departments;
}
