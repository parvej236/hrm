package bd.org.quantum.hrm.changeHistory;

import bd.org.quantum.authorizer.helper.UserContext;
import bd.org.quantum.authorizer.model.UserDetails;
import bd.org.quantum.hrm.employee.Employee;
import bd.org.quantum.hrm.employee.EmployeeInfoChangeHistoryDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ChangeHistoryService {
    ChangeHistoryRepository repository;

    public ChangeHistoryService(ChangeHistoryRepository repository) {
        this.repository = repository;
    }

    public List<ChangeHistory> getChangeHistoryByDataIdAndClassName(long dataId, String className) {
        return repository.findByDataIdAndClassNameOrderByActionTimeDesc(dataId, className);
    }

    public ChangeHistory saveChangeHistory(EmployeeInfoChangeHistoryDto object) {
        ChangeHistory changeHistory = new ChangeHistory();
        if (object != null) {
            UserDetails userDetails = UserContext.getPrincipal().getUserDetails();
            changeHistory.setDataId(object.getId());
            changeHistory.setChangedBy(userDetails.getId());
            changeHistory.setChangedByName(userDetails.getName());
            changeHistory.setClassName(Employee.class.getSimpleName());
            changeHistory.setActionTime(new Date());

            ObjectMapper mapper = new ObjectMapper();
            try {
                changeHistory.setJsonData(mapper.writeValueAsString(object));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return repository.save(changeHistory);
        }
        return null;
    }
}