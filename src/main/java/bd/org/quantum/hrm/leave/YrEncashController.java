package bd.org.quantum.hrm.leave;

import bd.org.quantum.authorizer.helper.SecurityCheck;
import bd.org.quantum.hrm.common.Routes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class YrEncashController {
    private final YrEncashService service;

    public YrEncashController(YrEncashService service) {
        this.service = service;
    }

    @PostMapping(value = Routes.ADD_YR_ENCASH)
    @SecurityCheck(permissions = {"EMPLOYEE_HR_ADMIN", "EMPLOYEE_HR_PERSONNEL"})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> create(@Valid @NotNull @RequestBody YrEncash yrEncash, BindingResult result) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            if(!result.hasErrors()) {
                service.create(yrEncash);
                log.debug("YR encash created successfully");
                response.put("status", "Success");
            } else {
                log.debug("YR encash created error due to binding error");
                response.put("status", "Failed");
            }
        } catch (Exception e) {
            log.debug("YR encash created exception..." + e);
            response.put("status", "Failed");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = Routes.YR_ENCASH_LIST)
    public String list(@RequestParam Long empId, Model model) {
        List<YrEncash> yrEncashList = service.encashListByEmployeeId(empId);
        model.addAttribute("encashList", yrEncashList);
        return "leave/yr-encash-list";
    }
}
