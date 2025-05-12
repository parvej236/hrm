package bd.org.quantum.hrm.idCard;

import bd.org.quantum.authorizer.Authorizer;
import bd.org.quantum.authorizer.helper.SecurityCheck;
import bd.org.quantum.authorizer.helper.UserContext;
import bd.org.quantum.authorizer.model.UserDetails;
import bd.org.quantum.common.utils.BarcodeUtils;
import bd.org.quantum.common.utils.Image;
import bd.org.quantum.common.utils.ImageService;
import bd.org.quantum.common.utils.SubmitResult;
import bd.org.quantum.hrm.attendance.*;
import bd.org.quantum.hrm.common.Routes;
import bd.org.quantum.hrm.designation.DesignationService;
import bd.org.quantum.hrm.employee.Employee;
import bd.org.quantum.hrm.employee.EmployeeService;
import bd.org.quantum.hrm.employee.EmployeeStatus;
import bd.org.quantum.hrm.role.Permissions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class IdCardRecordsController {
    private final IdCardRecordsService service;
    private final ImageService imageService;
    private final DesignationService designationService;
    private final EmployeeService employeeService;
    private final Authorizer authorizer;
    private final MessageSource messageSource;

    public IdCardRecordsController(IdCardRecordsService service, ImageService imageService,
                                   DesignationService designationService, EmployeeService employeeService,
                                   Authorizer authorizer, MessageSource messageSource) {
        this.service = service;
        this.imageService = imageService;
        this.designationService = designationService;
        this.employeeService = employeeService;
        this.authorizer = authorizer;
        this.messageSource = messageSource;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setAutoGrowCollectionLimit(10000);
    }

    @GetMapping(value = Routes.ID_CARD_CRITERIA, produces = MediaType.APPLICATION_JSON_VALUE)
    @SecurityCheck(permissions = {"ID_CARD_ORDER","ID_CARD_PROCESS"})
    public String criteriaForm(Model model) {
        IdCardCriteria criteria = new IdCardCriteria();
        LocalDate today = LocalDate.now();
        criteria.setYear(today.getMonthValue() > 8 ? (today.getYear() + 1) : today.getYear());

        model.addAttribute("criteria", criteria);
        referenceData(model);
        return "idCard/id-card-order-criteria";
    }

    @PostMapping(value = Routes.ID_CARD_CRITERIA, produces = MediaType.APPLICATION_JSON_VALUE)
    @SecurityCheck(permissions = {"ID_CARD_ORDER","ID_CARD_PROCESS"})
    public String getCriteriaForm(@Valid IdCardCriteria criteria, Model model) {

        List<IdCardRecords> orderList = service.processIdCardEntryForm(criteria);
        criteria.setOrderList(orderList);

        orderList.forEach(data -> {
            if (!StringUtils.isEmpty(data.getImagePath())) {
                Image image = imageService.downloadImg(data.getImagePath());
                data.setBase64(image.getData());
            }
        });

        model.addAttribute("criteria", criteria);
        model.addAttribute("dataList", orderList);
        model.addAttribute("actionUrl", Routes.ID_CARD_RECORDS);
        model.addAttribute("entryUrl", Routes.ID_CARD_CRITERIA);
        model.addAttribute("employeeProfileUrl", Routes.EMPLOYEE_PROFILE + "?id=");
        referenceData(model);

        return "idCard/id-card-records-form";
    }

    @PostMapping(value = Routes.ID_CARD_RECORDS, produces = MediaType.APPLICATION_JSON_VALUE)
    @SecurityCheck(permissions = {"ID_CARD_ORDER","ID_CARD_PROCESS"})
    public String create(@Valid IdCardCriteria criteria, BindingResult result, Model model) {
        criteria.setAction("order");
        try {
            if (!result.hasErrors()) {
                service.create(criteria);
                SubmitResult.success(messageSource, "id.card.order.success", model);
            } else {
                SubmitResult.error(messageSource, "id.card.order.error", model);
            }
        } catch (Exception e) {
            SubmitResult.error(messageSource, "id.card.order.error", model);
        }

        model.addAttribute("criteria", criteria);
        referenceData(model);

        return "idCard/id-card-order-criteria";
    }

    @GetMapping(value = Routes.ORDERED_ID_CARDS, produces = MediaType.APPLICATION_JSON_VALUE)
    @SecurityCheck(permissions = {"ID_CARD_ORDER","ID_CARD_PROCESS"})
    public String ordered(Model model) {
        IdCardCriteria criteria = new IdCardCriteria();
        criteria.setAction("process");
        LocalDate today = LocalDate.now();
        int year = today.getMonthValue() > 8 ? (today.getYear() + 1) : today.getYear();
        List<IdCardRecords> dataList = service.getIdCardRecordsByStageAndYear("PLACE_ORDER", year);
        criteria.setOrderList(dataList);

        model.addAttribute("criteria", criteria);
        model.addAttribute("dataList", dataList);
        model.addAttribute("employeeProfileUrl", Routes.EMPLOYEE_PROFILE + "?id=");
        model.addAttribute("hasProcessPermission", authorizer.hasPermission(Permissions.ID_CARD_PROCESS.name()));
        referenceData(model);

        return "idCard/id-card-ordered-records";
    }

    @PostMapping(value = Routes.ORDERED_ID_CARDS, produces = MediaType.APPLICATION_JSON_VALUE)
    @SecurityCheck(permissions = {"ID_CARD_PROCESS"})
    public String process(@Valid IdCardCriteria criteria, BindingResult result, Model model) {
        criteria.setAction("process");
        try {
            if (!result.hasErrors()) {
                service.create(criteria);
                SubmitResult.success(messageSource, "id.card.process.success", model);
                return "redirect:" + Routes.PROCESSED_ID_CARDS;
            } else {
                SubmitResult.error(messageSource, "id.card.process.error", model);
            }
        } catch (Exception e) {
            SubmitResult.error(messageSource, "id.card.process.error", model);
        }

        LocalDate today = LocalDate.now();
        int year = today.getMonthValue() > 8 ? (today.getYear() + 1) : today.getYear();
        List<IdCardRecords> dataList = service.getIdCardRecordsByStageAndYear("PLACE_ORDER", year);
        criteria.setOrderList(dataList);

        model.addAttribute("criteria", criteria);
        model.addAttribute("dataList", dataList);
        model.addAttribute("entryUrl", Routes.ID_CARD_CRITERIA);
        model.addAttribute("employeeProfileUrl", Routes.EMPLOYEE_PROFILE + "?id=");
        referenceData(model);

        return "idCard/id-card-ordered-records";
    }

    @GetMapping(value = Routes.PROCESSED_ID_CARDS, produces = MediaType.APPLICATION_JSON_VALUE)
    @SecurityCheck(permissions = {"ID_CARD_PROCESS"})
    public String processed(Model model) {
        IdCardCriteria criteria = new IdCardCriteria();
        criteria.setAction("accept");
        LocalDate today = LocalDate.now();
        int year = today.getMonthValue() > 8 ? (today.getYear() + 1) : today.getYear();
        List<IdCardRecords> dataList = service.getIdCardRecordsByStageAndYear("IN_PROCESS", year);
        dataList.forEach(data -> {
            if (!StringUtils.isEmpty(data.getImagePath())) {
                Image image = imageService.downloadImg(data.getImagePath());
                data.setBase64(image.getData());
            }

            byte[] qrCodeBytes;
            try {
                qrCodeBytes = BarcodeUtils.generateQRCodeImage(data.getQrText(), 600, 600);
                data.setQrCodeBase64(Base64.getEncoder().encodeToString(qrCodeBytes));

                BufferedImage barcodeImage = BarcodeUtils.getBarcodeImage(data.getEmployeeId(), "CODE128", 400, 100);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(barcodeImage, "png", baos);
                data.setBarcodeBase64(Base64.getEncoder().encodeToString(baos.toByteArray()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        criteria.setOrderList(dataList);

        model.addAttribute("criteria", criteria);
        model.addAttribute("dataList", dataList);
        model.addAttribute("entryUrl", Routes.ID_CARD_CRITERIA);
        model.addAttribute("employeeProfileUrl", Routes.EMPLOYEE_PROFILE + "?id=");
        referenceData(model);

        return "idCard/id-card-processed-records";
    }

    @PostMapping(value = Routes.PROCESSED_ID_CARDS, produces = MediaType.APPLICATION_JSON_VALUE)
    @SecurityCheck(permissions = {"ID_CARD_PROCESS"})
    public String accept(@Valid IdCardCriteria criteria, BindingResult result, Model model) {
        criteria.setAction("accept");
        try {
            if (!result.hasErrors()) {
                service.create(criteria);
                SubmitResult.success(messageSource, "id.card.process.success", model);
                return "redirect:" + Routes.ACCEPTED_ID_CARDS;
            } else {
                SubmitResult.error(messageSource, "id.card.process.error", model);
            }
        } catch (Exception e) {
            SubmitResult.error(messageSource, "id.card.process.error", model);
        }

        LocalDate today = LocalDate.now();
        int year = today.getMonthValue() > 8 ? (today.getYear() + 1) : today.getYear();
        List<IdCardRecords> dataList = service.getIdCardRecordsByStageAndYear("IN_PROCESS", year);

        model.addAttribute("criteria", criteria);
        model.addAttribute("dataList", dataList);
        model.addAttribute("entryUrl", Routes.ID_CARD_CRITERIA);
        model.addAttribute("employeeProfileUrl", Routes.EMPLOYEE_PROFILE + "?id=");
        referenceData(model);

        return "idCard/id-card-processed-records";
    }

    @GetMapping(value = Routes.ACCEPTED_ID_CARDS, produces = MediaType.APPLICATION_JSON_VALUE)
    @SecurityCheck(permissions = {"ID_CARD_PROCESS"})
    public String accepted(Model model) {
        IdCardCriteria criteria = new IdCardCriteria();
        criteria.setAction("accept");
        LocalDate today = LocalDate.now();
        int year = today.getMonthValue() > 8 ? (today.getYear() + 1) : today.getYear();
        List<IdCardRecords> dataList = service.getIdCardRecordsByStageAndYear("ACCEPTED", year);
        dataList.forEach(data -> {
            if (!StringUtils.isEmpty(data.getImagePath())) {
                Image image = imageService.downloadImg(data.getImagePath());
                data.setBase64(image.getData());
            }

            byte[] qrCodeBytes;
            try {
                qrCodeBytes = BarcodeUtils.generateQRCodeImage(data.getQrText(), 600, 600);
                data.setQrCodeBase64(Base64.getEncoder().encodeToString(qrCodeBytes));

                BufferedImage barcodeImage = BarcodeUtils.getBarcodeImage(data.getEmployeeId(), "CODE128", 400, 100);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(barcodeImage, "png", baos);
                data.setBarcodeBase64(Base64.getEncoder().encodeToString(baos.toByteArray()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        criteria.setOrderList(dataList);

        model.addAttribute("criteria", criteria);
        model.addAttribute("dataList", dataList);
        model.addAttribute("entryUrl", Routes.ID_CARD_CRITERIA);
        model.addAttribute("employeeProfileUrl", Routes.EMPLOYEE_PROFILE + "?id=");
        referenceData(model);

        return "idCard/id-card-accepted-records";
    }

    @GetMapping(value = Routes.PHOTO_FOR_ID_CARD, produces = MediaType.APPLICATION_JSON_VALUE)
    @SecurityCheck(permissions = {"ID_CARD_ORDER","ID_CARD_PROCESS"})
    public String view(Model model) {
        Employee employee = new Employee();
        model.addAttribute("employee", employee);
        referenceData(model);

        return "idCard/photo-upload-form";
    }

    @GetMapping(value = Routes.ID_CARDS_BY_IDS, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, List<IdCardRecords>> cardsByIds(@RequestParam("ids") Long[] ids) {
        Map<String, List<IdCardRecords>> result = new LinkedHashMap<>();
        result.put("data", service.getByIds(ids));
        return result;
    }

    @GetMapping(value = Routes.VERIFY_ID_CARD, produces = MediaType.APPLICATION_JSON_VALUE)
    public String cardsByIds(@PathVariable String encriptedId, @PathVariable int year, Model model) {
        IdCardRecords idCardRecords = null;
        int curYear = LocalDate.now().getYear();
        if (curYear <= year && !StringUtils.isEmpty(encriptedId)) {
            idCardRecords = service.getRecordsByEncriptedIdAndYear(encriptedId, year);
            if (idCardRecords != null && !StringUtils.isEmpty(idCardRecords.getImagePath())) {
                Image image = imageService.downloadImg(idCardRecords.getImagePath());
                idCardRecords.setBase64(image.getData());
            }
        }
        model.addAttribute("idCard", idCardRecords);
        return "idCard/id-card-verify-form";
    }

    private void referenceData(Model model) {
        UserDetails user = UserContext.getPrincipal().getUserDetails();
        model.addAttribute("attendanceTypeMap", AttendanceType.getAttendanceTypeMap());
        model.addAttribute("empStatusMap", EmployeeStatus.getStatusesForIdCard());
        model.addAttribute("designationMap", designationService.getDesignationMap());
        model.addAttribute("branchList", employeeService.getBranches());
        model.addAttribute("departmentList", employeeService.getDepartmentList(user.getBranchId()));
        model.addAttribute("hasSubmitAccess", authorizer.hasAnyPermission(Permissions.ID_CARD_ORDER.name()));
    }
}
