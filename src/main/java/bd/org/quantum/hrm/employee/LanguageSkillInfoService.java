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
public class LanguageSkillInfoService {
    private final LanguageSkillInfoRepository repository;
    private final ChangeHistoryRepository historyRepository;

    public LanguageSkillInfoService(LanguageSkillInfoRepository repository,
                                    ChangeHistoryRepository historyRepository) {
        this.repository = repository;
        this.historyRepository = historyRepository;
    }

    public List<LanguageSkillInfo> getLanguageSkillInfoByEmployee(Long employeeId) {
        return repository.findAllByEmployeeIdOrderById(employeeId);
    }

    public ChangeHistory saveLanguageSkillInfoChangeHistory(EmployeeLanguageSkillChangeHistoryDto object) {
        if (object != null) {
            ChangeHistory changeHistory = new ChangeHistory();
            try {
                UserDetails userDetails = UserContext.getPrincipal().getUserDetails();

                changeHistory.setDataId(object.getEmployeeId());
                changeHistory.setChangedBy(userDetails.getId());
                changeHistory.setChangedByName(userDetails.getName());
                changeHistory.setClassName(LanguageSkillInfo.class.getSimpleName());
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
    public LanguageSkillInfo create(LanguageSkillInfo languageSkillInfo) {
        EmployeeLanguageSkillChangeHistoryDto dto = new EmployeeLanguageSkillChangeHistoryDto(languageSkillInfo);
        saveLanguageSkillInfoChangeHistory(dto);
        return repository.save(languageSkillInfo);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
