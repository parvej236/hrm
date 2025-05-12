package bd.org.quantum.hrm.employee;

import bd.org.quantum.authorizer.helper.SecurityCheck;
import bd.org.quantum.hrm.common.Routes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class TransferInfoController {
    private final TransferInfoService service;

    public TransferInfoController(TransferInfoService service) {
        this.service = service;
    }

    @PostMapping(value = Routes.ADD_TRANSFER_INFO)
    @SecurityCheck(permissions = {"EMPLOYEE_UPDATE"})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> create(@Valid @NotNull @RequestBody TransferInfo transferInfo, BindingResult result) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            if(!result.hasErrors()) {
                service.create(transferInfo);
                log.debug("Tranfer info created successfully");
                response.put("status","Success");
            } else {
                log.debug("Transfer info created error due to binding error");
                response.put("status", "Failed");
            }
        } catch (Exception e) {
            log.debug("Transfer info created exception..." + e);
            response.put("status", "Failed");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = Routes.GET_TRANSFER_INFO)
    @ResponseBody
    public TransferInfo get(@RequestParam Long id) {
        return service.getById(id);
    }

    @PostMapping(value = Routes.DELETE_TRANSFER_INFO)
    @ResponseBody
    public void delete(@RequestParam Long id) {
        service.delete(id);
    }

    @GetMapping(value = Routes.EMPLOYEES_TRANSFER_INFO)
    @SecurityCheck(permissions = {"EMPLOYEE_UPDATE"})
    @ResponseBody
    public ResponseEntity<List<TransferInfo>> list(@RequestParam Long employeeId) {
        List<TransferInfo> transferList = service.getTransferInfoByEmployee(employeeId);
        return new ResponseEntity<>(transferList, HttpStatus.OK);
    }
}
