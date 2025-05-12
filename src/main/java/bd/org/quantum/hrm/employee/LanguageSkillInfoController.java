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
public class LanguageSkillInfoController {
    private final LanguageSkillInfoService service;

    public LanguageSkillInfoController(LanguageSkillInfoService service) {
        this.service = service;
    }

    @PostMapping(value = Routes.ADD_LANGUAGE_SKILL_INFO)
    @SecurityCheck(permissions = {"EMPLOYEE_UPDATE"})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> create(@Valid @NotNull @RequestBody LanguageSkillInfo languageSkillInfo, BindingResult result) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            if(!result.hasErrors()) {
                service.create(languageSkillInfo);
                log.debug("Language skill info created successfully");
                response.put("status", "Success");
            } else {
                log.debug("Language skill info created error due to binding error");
                response.put("status", "Failed");
            }
        } catch (Exception e) {
            log.debug("Language skill info created exception..." + e);
            response.put("status", "Failed");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = Routes.DELETE_LANGUAGE_SKILL_INFO)
    @ResponseBody
    public void delete(@RequestParam Long id) {
        service.delete(id);
    }

    @GetMapping(value = Routes.EMPLOYEES_LANGUAGE_SKILL_INFO)
    @SecurityCheck(permissions = {"EMPLOYEE_UPDATE"})
    @ResponseBody
    public ResponseEntity<List<LanguageSkillInfo>> list(@RequestParam Long employeeId) {
        List<LanguageSkillInfo> languageList = service.getLanguageSkillInfoByEmployee(employeeId);
        return new ResponseEntity<>(languageList, HttpStatus.OK);
    }
}
