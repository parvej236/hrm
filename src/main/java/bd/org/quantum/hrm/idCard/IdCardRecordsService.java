package bd.org.quantum.hrm.idCard;

import bd.org.quantum.authorizer.helper.UserContext;
import bd.org.quantum.authorizer.model.UserDetails;
import bd.org.quantum.hrm.attendance.*;
import bd.org.quantum.hrm.employee.Employee;
import com.google.zxing.WriterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IdCardRecordsService {

    private final AttendanceDao attendanceDao;
    private final IdCardRecordsRepository repository;

    public IdCardRecordsService(AttendanceDao attendanceDao, IdCardRecordsRepository repository) {
        this.attendanceDao = attendanceDao;
        this.repository = repository;
    }

    public IdCardRecords update(IdCardRecords idCard) {
        UserDetails login = UserContext.getPrincipal().getUserDetails();
        idCard.setUpdatedBy(login.getId());

        return this.repository.save(idCard);
    }

    public IdCardRecords getRecordsByEncriptedIdAndYear(String encriptedId, int year) {
        return repository.findByEncriptedIdAndYear(encriptedId, year);
    }

    public List<IdCardRecords> processIdCardEntryForm(IdCardCriteria criteria){
        List<IdCardRecords> orderList = new ArrayList<>();
        List<Employee> employeeList = attendanceDao.getEmployeeListForIdCard(criteria);

        for (Employee employee: employeeList) {
            IdCardRecords idCard = new IdCardRecords();
            idCard.setEmployee(employee.getId());
            idCard.setBranchId(employee.getBranch());
            idCard.setDepartmentId(employee.getDepartment());
            idCard.setDesignationId(employee.getDesignation().getId());
            idCard.setEmployeeId(employee.getEmployeeId());
            idCard.setImagePath(employee.getImagePath());
            idCard.setEmployeeName(employee.getNameEn());
            idCard.setBranchName(employee.getBranchName());
            idCard.setDepartmentName(employee.getDepartmentName());
            idCard.setDesignation(employee.getDesignation().getName());
            idCard.setBloodGroup(employee.getBloodGroup());
            orderList.add(idCard);
        }

        return orderList;
    }

    public List<IdCardRecords> getIdCardRecordsByStageAndYear(String stage, int year){
        return repository.findByStageAndYear(stage, year);
    }

    public void create(IdCardCriteria criteria) throws IOException, WriterException {
        UserDetails login = UserContext.getPrincipal().getUserDetails();
        List<IdCardRecords> records = new ArrayList<>();
        if (criteria.getOrderList() != null && criteria.getOrderList().size() > 0) {
            records = criteria.getOrderList().stream().filter(IdCardRecords::isCardSelected).collect(Collectors.toList());
        }

        if (criteria.getAction().equals("order")) {
                for (IdCardRecords idCard : records) {
                    String uniqueId = UUID.randomUUID().toString().replaceAll("[^a-zA-Z0-9]", "").substring(0, 8);
                    idCard.setCreatedAt(new Date());
                    idCard.setCreatedBy(login.getId());
                    idCard.setOrderedBy(login.getId());
                    idCard.setOrderedOn(LocalDate.now());
                    idCard.setStage("PLACE_ORDER");
                    idCard.setYear(criteria.getYear());
                    idCard.setEncriptedId(uniqueId);
                    repository.save(idCard);
                }
        } else if (criteria.getAction().equals("process")) {
                for (IdCardRecords idCard : records) {
                    LocalDate expireDate = LocalDate.of(idCard.getYear(), 12, 31);
                    idCard.setUpdatedAt(new Date());
                    idCard.setUpdatedBy(login.getId());
                    idCard.setProcessedBy(login.getId());
                    idCard.setProcessedOn(LocalDate.now());
                    idCard.setStage("IN_PROCESS");
                    idCard.setExpireOn(expireDate);
                    repository.save(idCard);
                }
        } else if (criteria.getAction().equals("accept")) {
            for (IdCardRecords idCard : records) {
                idCard.setUpdatedAt(new Date());
                idCard.setUpdatedBy(login.getId());
                idCard.setAcceptedBy(login.getId());
                idCard.setAcceptedOn(LocalDate.now());
                idCard.setStage("ACCEPTED");
                repository.save(idCard);
            }
        }
    }

    public List<IdCardRecords> getByIds(Long[] ids) {
        return repository.findAllByIdIn(ids);
    }
}
