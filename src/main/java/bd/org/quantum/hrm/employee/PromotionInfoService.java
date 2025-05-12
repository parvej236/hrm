package bd.org.quantum.hrm.employee;

import bd.org.quantum.authorizer.helper.UserContext;
import bd.org.quantum.authorizer.model.UserDetails;
import bd.org.quantum.hrm.changeHistory.ChangeHistory;
import bd.org.quantum.hrm.changeHistory.ChangeHistoryRepository;
import bd.org.quantum.hrm.designation.DesignationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class PromotionInfoService {
    private final PromotionInfoRepository repository;
    private final EmployeeRepository employeeRepository;
    private final DesignationRepository designationRepository;
    private final ChangeHistoryRepository historyRepository;

    public PromotionInfoService(PromotionInfoRepository repository,
                                EmployeeRepository employeeRepository,
                                DesignationRepository designationRepository,
                                ChangeHistoryRepository historyRepository) {
        this.repository = repository;
        this.employeeRepository = employeeRepository;
        this.designationRepository = designationRepository;
        this.historyRepository = historyRepository;
    }

    public List<PromotionInfo> getPromotionInfoByEmployee(Long employeeId) {
        return repository.findAllByEmployeeIdOrderById(employeeId);
    }

    public ChangeHistory savePromotionInfoChangeHistory(EmployeePromotionInfoChangeHistoryDto object) {
        if (object != null) {
            ChangeHistory changeHistory = new ChangeHistory();
            try {
                UserDetails userDetails = UserContext.getPrincipal().getUserDetails();

                changeHistory.setDataId(object.getEmployeeId());
                changeHistory.setChangedBy(userDetails.getId());
                changeHistory.setChangedByName(userDetails.getName());
                changeHistory.setClassName(PromotionInfo.class.getSimpleName());
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
    public PromotionInfo create(PromotionInfo promotionInfo) {
        PromotionInfo promotion = repository.save(promotionInfo);
        EmployeePromotionInfoChangeHistoryDto dto = new EmployeePromotionInfoChangeHistoryDto(promotionInfo);

        Employee employee = employeeRepository.getById(promotion.getEmployee().getId());
        employee.setDesignation(designationRepository.findByName(promotion.getDesignationTo()));
        employee.setDesignationName(promotion.getDesignationTo());
        employee.setLastPromotionDate(promotion.getDateFrom());

        savePromotionInfoChangeHistory(dto);
        employeeRepository.save(employee);

        if (!StringUtils.isEmpty(promotion.getDesignationTo())) {
            repository.updateMemberDesignation(promotion.getDesignationTo(), employee.getMember());
        }

        return promotion;
    }

    public PromotionInfo getById(Long id) {
        return repository.getById(id);
    }

    public void delete(Long id) {
        PromotionInfo promotionInfo = repository.getById(id);
        Long employeeId = promotionInfo.getEmployee().getId();

        repository.deleteById(id);

        List<PromotionInfo> promotionInfos = repository.findAllByEmployeeIdOrderById(employeeId);

        if (promotionInfos != null && !promotionInfos.isEmpty()) {
            PromotionInfo promotion = promotionInfos.get(promotionInfos.size() - 1);
            if (!StringUtils.isEmpty(promotion.getDesignationTo())) {
                Employee employee = employeeRepository.getById(employeeId);
                employee.setDesignation(designationRepository.findByName(promotion.getDesignationTo()));
                employee.setDesignationName(promotion.getDesignationTo());
                employee.setLastPromotionDate(promotion.getDateFrom());
                employeeRepository.save(employee);

                if (!StringUtils.isEmpty(promotion.getDesignationTo())) {
                    repository.updateMemberDesignation(promotion.getDesignationTo(), employee.getMember());
                }
            }
        }
    }
}
