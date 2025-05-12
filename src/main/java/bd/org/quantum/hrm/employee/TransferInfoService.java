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
public class TransferInfoService {
    private final TransferInfoRepository repository;
    private final EmployeeRepository employeeRepository;
    private final ChangeHistoryRepository historyRepository;

    public TransferInfoService(TransferInfoRepository repository,
                               EmployeeRepository employeeRepository,
                               ChangeHistoryRepository historyRepository) {
        this.repository = repository;
        this.employeeRepository = employeeRepository;
        this.historyRepository = historyRepository;
    }

    public List<TransferInfo> getTransferInfoByEmployee(Long employeeId) {
        return repository.getAllByEmployeeIdOrderById(employeeId);
    }

    public ChangeHistory saveTransferInfoChangeHistory(EmployeeTransferInfoChangeHistoryDto object) {
        if (object != null) {
            ChangeHistory changeHistory = new ChangeHistory();
            try {
                UserDetails userDetails = UserContext.getPrincipal().getUserDetails();

                changeHistory.setDataId(object.getEmployeeId());
                changeHistory.setChangedBy(userDetails.getId());
                changeHistory.setChangedByName(userDetails.getName());
                changeHistory.setClassName("TransferInfo");
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
    public TransferInfo create(TransferInfo transferInfo) {
        TransferInfo transfer = repository.save(transferInfo);
        EmployeeTransferInfoChangeHistoryDto dto = new EmployeeTransferInfoChangeHistoryDto(transferInfo);
        saveTransferInfoChangeHistory(dto);

        Employee employee = employeeRepository.getById(transfer.getEmployee().getId());
        employee.setBranch(transfer.getBranchTo());
        employee.setBranchName(transfer.getBranchToName());
        if (transfer.getDepartmentTo() != null && transfer.getDepartmentTo() > 0) {
            employee.setDepartment(transfer.getDepartmentTo());
            employee.setDepartmentName(transfer.getDepartmentToName());
        }
        employeeRepository.save(employee);

        return transfer;
    }

    public TransferInfo getById(Long id) {
        return repository.getById(id);
    }

    public void delete(Long id) {
        TransferInfo transferInfo = repository.getById(id);
        Long employeeId = transferInfo.getEmployee().getId();

        repository.deleteById(id);

        List<TransferInfo> transferInfos = repository.getAllByEmployeeIdOrderById(employeeId);

        if (transferInfos != null && !transferInfos.isEmpty()) {
            TransferInfo transfer = transferInfos.get(transferInfos.size() - 1);
            Employee employee = employeeRepository.getById(employeeId);
            if (transfer.getBranchTo() != null && transfer.getBranchTo() > 0) {
                employee.setBranch(transfer.getBranchTo());
                employee.setBranchName(transfer.getBranchToName());
            }
            if (transfer.getDepartmentTo() != null && transfer.getDepartmentTo() > 0) {
                employee.setDepartment(transfer.getDepartmentTo());
                employee.setDepartmentName(transfer.getDepartmentToName());
            }
            employeeRepository.save(employee);
        }
    }
}
