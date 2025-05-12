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
public class TrainingInfoService {
    private final TrainingInfoRepository repository;
    private final ChangeHistoryRepository historyRepository;

    public TrainingInfoService(TrainingInfoRepository repository,
                               ChangeHistoryRepository historyRepository) {
        this.repository = repository;
        this.historyRepository = historyRepository;
    }

    public List<TrainingInfo> getTrainingListByEmployee(Long employeeId) {
        return repository.findAllByEmployeeIdOrderById(employeeId);
    }

    public ChangeHistory saveTrainingInfoChangeHistory(EmployeeTrainingInfoChangeHistoryDto object) {
        if (object != null) {
            ChangeHistory changeHistory = new ChangeHistory();
            try {
                UserDetails userDetails = UserContext.getPrincipal().getUserDetails();

                changeHistory.setDataId(object.getEmployeeId());
                changeHistory.setChangedBy(userDetails.getId());
                changeHistory.setChangedByName(userDetails.getName());
                changeHistory.setClassName(TrainingInfo.class.getSimpleName());
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
    public TrainingInfo create(TrainingInfo trainingInfo) {
        EmployeeTrainingInfoChangeHistoryDto dto = new EmployeeTrainingInfoChangeHistoryDto(trainingInfo);
        saveTrainingInfoChangeHistory(dto);
        return repository.save(trainingInfo);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
