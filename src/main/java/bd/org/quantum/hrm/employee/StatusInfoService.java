package bd.org.quantum.hrm.employee;

import bd.org.quantum.authorizer.helper.UserContext;
import bd.org.quantum.authorizer.model.UserDetails;
import bd.org.quantum.hrm.changeHistory.ChangeHistory;
import bd.org.quantum.hrm.changeHistory.ChangeHistoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class StatusInfoService {
    private final StatusInfoRepository statusRepository;
    private final EmployeeRepository employeeRepository;
    private final ChangeHistoryRepository historyRepository;

    public StatusInfoService(StatusInfoRepository repository,
                             EmployeeRepository employeeRepository,
                             ChangeHistoryRepository historyRepository) {
        this.statusRepository = repository;
        this.employeeRepository = employeeRepository;
        this.historyRepository = historyRepository;
    }

    public List<StatusInfo> getStatusInfoByEmployee(Long employeeId) {
        return statusRepository.findAllByEmployeeIdOrderById(employeeId);
    }

    public StatusInfo getStatusInfoById(Long id) {
        return statusRepository.getById(id);
    }

    public ChangeHistory saveStatusInfoChangeHistory(EmployeeJobStatusChangeHistoryDto object) {
        if (object != null) {
            ChangeHistory changeHistory = new ChangeHistory();
            try {
                UserDetails userDetails = UserContext.getPrincipal().getUserDetails();

                changeHistory.setDataId(object.getEmployeeId());
                changeHistory.setChangedBy(userDetails.getId());
                changeHistory.setChangedByName(userDetails.getName());
                changeHistory.setClassName(StatusInfo.class.getSimpleName());
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
    public StatusInfo create(StatusInfo statusInfo) {
        Long infoId = statusInfo.getId();
        StatusInfo lastStatus = statusRepository.findTopByEmployeeIdOrderByIdDesc(statusInfo.getEmployee().getId());
        StatusInfo typeInfo = statusRepository.save(statusInfo);

        Employee employee = employeeRepository.getById(typeInfo.getEmployee().getId());
        if (infoId == null || lastStatus.getId().equals(typeInfo.getId())) {
            EmployeeJobStatusChangeHistoryDto dto = new EmployeeJobStatusChangeHistoryDto(statusInfo);
            employee.setStatus(typeInfo.getStatusTo());
            if (typeInfo.getStatusTo().equals(EmployeeStatus.REGULAR.getName())) {
                employee.setRegularDate(typeInfo.getDateFrom());
            } else {
                employee.setRegularDate(null);
            }
            saveStatusInfoChangeHistory(dto);
            employeeRepository.save(employee);
        }

        if (Arrays.asList(EmployeeStatus.RUNNING_ALL_EMPLOYEE_STATUSES).contains(typeInfo.getStatusTo())) {
            statusRepository.updateMemberCategory("EMPLOYEE", employee.getMember());
        } else {
            statusRepository.updateMemberCategory("GENERAL", employee.getMember());
        }

        return typeInfo;
    }

    public void delete(Long id) {
        StatusInfo statusInfo = statusRepository.getById(id);
        Long employeeId = statusInfo.getEmployee().getId();

        statusRepository.deleteById(id);

        List<StatusInfo> employeeStatusList = statusRepository.findAllByEmployeeIdOrderById(employeeId);

        if (employeeStatusList != null && !employeeStatusList.isEmpty()) {
            StatusInfo typeInfo = employeeStatusList.get(employeeStatusList.size() - 1);
            if (statusInfo != null && !StringUtils.isEmpty(typeInfo.getStatusTo())) {
                Employee employee = employeeRepository.getById(employeeId);
                employee.setStatus(typeInfo.getStatusTo());
                employeeRepository.save(employee);

                if (Arrays.asList(EmployeeStatus.RUNNING_ALL_EMPLOYEE_STATUSES).contains(typeInfo.getStatusTo())) {
                    statusRepository.updateMemberCategory("EMPLOYEE", employee.getMember());
                } else {
                    statusRepository.updateMemberCategory("GENERAL", employee.getMember());
                }
            }
        }
    }
}
