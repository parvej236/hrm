package bd.org.quantum.hrm.designation;

import bd.org.quantum.authorizer.helper.SecurityCheck;
import bd.org.quantum.common.utils.SearchForm;
import bd.org.quantum.common.utils.SubmitResult;
import bd.org.quantum.hrm.common.Routes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Controller
public class DesignationController {
    private final DesignationService service;
    private final MessageSource messageSource;

    public DesignationController(DesignationService service,
                                 MessageSource messageSource) {
        this.service = service;
        this.messageSource = messageSource;
    }

    @GetMapping(Routes.DESIGNATIONS)
    public String search(Model model) {
        model.addAttribute("formTitle", "Designation List");
        model.addAttribute("searchUrl", Routes.DESIGNATION_SEARCH);
        model.addAttribute("editUrl", Routes.DESIGNATION_UPDATE);
        model.addAttribute("entryUrl", Routes.DESIGNATION_ENTRY);
        return "designation/designation-list";
    }

    @GetMapping(Routes.DESIGNATION_SEARCH)
    @ResponseBody
    public Page<Designation> DesignationList(SearchForm searchForm) {
        return service.search(searchForm);
    }

    @GetMapping(Routes.DESIGNATION_ENTRY)
    public String create(Model model) {
        model.addAttribute("designation", new Designation());
        model.addAttribute("formTitle", "Designation Entry");
        model.addAttribute("checkUrl", Routes.DESIGNATION_CHECK);
        model.addAttribute("entryLink", Routes.DESIGNATION_ENTRY);
        return "designation/designation-form";
    }

    @GetMapping(Routes.DESIGNATION_UPDATE)
    @SecurityCheck(permissions = {"DESIGNATION"})
    public String update(@RequestParam Long id, @RequestParam(required = false) boolean result, @RequestParam(defaultValue = "2") int flag, Model model) {
        model.addAttribute("designation", service.getDesignationById(id));
        model.addAttribute("checkUrl", Routes.DESIGNATION_CHECK);
        model.addAttribute("entryLink", Routes.DESIGNATION_ENTRY);
        model.addAttribute("formTitle", "Designation Update");
        if (result && flag == 1) {
            SubmitResult.success(messageSource, "designation.update.success", model);
        } else if (flag != 2){
            SubmitResult.success(messageSource, "designation.create.success", model);
        }
        return "designation/designation-form";
    }

    @GetMapping(Routes.DESIGNATION_CHECK)
    @ResponseBody
    public Map<String, Object> checkExistingDesignation(@RequestParam String name) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("data", service.checkExistingDesignation(name));
        return result;
    }

    @PostMapping(Routes.DESIGNATION_ENTRY)
    public String create(@Valid Designation designationForm, BindingResult result, Model model) {
        service.validateDesignation(designationForm, result);
        try {
            if (!result.hasErrors()) {
                int flag = designationForm.getId() == null || designationForm.getId() == 0 ? 0 : 1;
                designationForm = service.create(designationForm);
                model.addAttribute("designation", designationForm);
                return "redirect:" + Routes.DESIGNATION_UPDATE + "?id=" + designationForm.getId() + "&flag=" + flag + "&result=" + String.valueOf(!result.hasErrors()).toUpperCase();
            } else {
                SubmitResult.error(messageSource, "designation.create.error", model);
            }
        } catch (Exception e) {
            SubmitResult.error(messageSource, "designation.create.error", model);
        }
        return "redirect:/designation-entry";
    }
}
