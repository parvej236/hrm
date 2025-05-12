package bd.org.quantum.hrm.employee;

import bd.org.quantum.authorizer.helper.UserContext;
import bd.org.quantum.authorizer.model.UserDetails;
import bd.org.quantum.hrm.changeHistory.ChangeHistory;
import bd.org.quantum.hrm.changeHistory.ChangeHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class EducationInfoService {

    private final EducationInfoRepository eduRepository;
    private final ChangeHistoryRepository historyRepository;

    public EducationInfoService(EducationInfoRepository repository,
                                ChangeHistoryRepository changeHistoryRepository) {
        this.eduRepository = repository;
        this.historyRepository = changeHistoryRepository;
    }

    public List<EducationInfo> getEducationInfoListByEmployee(Long employeeId) {
        return eduRepository.findAllByEmployeeIdOrderById(employeeId);
    }

    public ChangeHistory saveEducationInfoChangeHistory(EmployeeEducationInfoChangeHistoryDto object) {
        if (object != null) {
            ChangeHistory changeHistory = new ChangeHistory();
            try {
                UserDetails userDetails = UserContext.getPrincipal().getUserDetails();

                changeHistory.setDataId(object.getEmployeeId());
                changeHistory.setChangedBy(userDetails.getId());
                changeHistory.setChangedByName(userDetails.getName());
                changeHistory.setClassName(EducationInfo.class.getSimpleName());
                changeHistory.setActionTime(new Date());

                ObjectMapper mapper = new ObjectMapper();
                String jsonData = mapper.writeValueAsString(object);
                changeHistory.setJsonData(jsonData);

                return historyRepository.save(changeHistory);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Transactional
    public EducationInfo create(EducationInfo educationInfo) {
        EmployeeEducationInfoChangeHistoryDto dto = new EmployeeEducationInfoChangeHistoryDto(educationInfo);
        saveEducationInfoChangeHistory(dto);

        return eduRepository.save(educationInfo);
    }

    public void delete(Long id) {
        eduRepository.deleteById(id);
    }
}
