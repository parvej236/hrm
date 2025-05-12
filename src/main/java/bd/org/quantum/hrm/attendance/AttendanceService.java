package bd.org.quantum.hrm.attendance;

import bd.org.quantum.authorizer.helper.UserContext;
import bd.org.quantum.authorizer.model.UserDetails;
import bd.org.quantum.common.resttemplate.RestTemplateService;
import bd.org.quantum.common.utils.DateUtils;
import bd.org.quantum.hrm.common.Branch;
import bd.org.quantum.hrm.common.Constants;
import bd.org.quantum.hrm.employee.Employee;
import bd.org.quantum.hrm.employee.StatusInfo;
import bd.org.quantum.hrm.employee.EmployeeStatus;
import bd.org.quantum.hrm.employee.StatusInfoRepository;
import bd.org.quantum.hrm.leave.LeaveApplication;
import bd.org.quantum.hrm.report.ReportData;
import bd.org.quantum.hrm.report.ReportHeader;
import bd.org.quantum.hrm.yr_cl.YrCl;
import bd.org.quantum.hrm.yr_cl.YrClRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.thymeleaf.util.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AttendanceService {
    private static final String BRANCH_LIST = "%s/api/branches/%s";
    private static final String CENTER_WITH_SUBSIDIARIES = "%s/api/center-with-subsidiaries/%s";
    private static final String DEPARTMENT_LIST_WITH_SUBSIDIARY = "%s/api/departments/%s/subsidiaries";

    @Value("${resource.api.url}")
    String resourceUrl;

    private final AttendanceDao attendanceDao;
    private final AttendanceRepository repository;
    private final YrClRepository yrClRepository;
    private final StatusInfoRepository statusRepository;
    private final RestTemplateService restService;

    public AttendanceService(AttendanceDao attendanceDao,
                             AttendanceRepository repository,
                             YrClRepository yrClRepository,
                             StatusInfoRepository statusRepository,
                             RestTemplateService restService) {
        this.attendanceDao = attendanceDao;
        this.repository = repository;
        this.yrClRepository = yrClRepository;
        this.statusRepository = statusRepository;
        this.restService = restService;
    }

    public Attendance get(Long id) {
        return repository.getById(id);
    }

    public List<LeaveApplication> getApplicationList() {
        return attendanceDao.pendingConfirmLeaveList(getDateList());
    }

    public static List<String> getDateList() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();
        LocalDate lastMonth = today.minusMonths(1).withDayOfMonth(21);
        long numOfDaysBetween = ChronoUnit.DAYS.between(lastMonth, today);
        List<String> dates = new ArrayList<>();
        for(long i = 0; i <= numOfDaysBetween; i++){
            dates.add(df.format(lastMonth.plusDays(i)));
        }
        return dates;
    }

    public List<AttendanceStatus> processAttendanceEntryForm(AttendanceCriteria cmd){
        List<AttendanceStatus> attendanceList = new ArrayList<>();

        List<Employee> employeeList = attendanceDao.getEmployeeList(cmd);
        List<LocalDate> dateList = cmd.getStartDate().datesUntil(cmd.getEndDate().plusDays(1)).collect(Collectors.toList());

        List<YrCl> yearWiseYrList = attendanceDao.getAllYearWiseYr(true);
        YrCl currentYear = null;

        LocalDate today = LocalDate.now();

        for(YrCl yr : yearWiseYrList){
            if (today.compareTo(yr.getDateFrom()) >= 0 && today.compareTo(yr.getDateTo()) <= 0) {
                currentYear = yr;
                break;
            }
        }

        for (Employee employee: employeeList) {
            AttendanceStatus as = new AttendanceStatus();
            as.setEmployee(employee);

            as.setTypeMap(AttendanceType.getAllShortAttendanceTypes());
            if (employee.getRegularDate() == null || employee.getStatus().equals(EmployeeStatus.IRREGULAR.getName())) {
                as.getTypeMap().remove(AttendanceType.YEARLY_REJUVENATION.getValue());
            }

            List<AttendanceDateStatus> adsList = new ArrayList<>();

            int clBalance = getClBalance(employee, cmd.getEndDate().plusDays(1), currentYear);
            int yrBalance = getYrBalance(employee, cmd.getEndDate().plusDays(1));

            for (LocalDate date : dateList) {

                AttendanceDateStatus ads = new AttendanceDateStatus();
                ads.setAttendanceDate(date);

                String day = DateTimeFormatter.ofPattern("EEEE").format(date);

                if (date.compareTo(employee.getHiring()) == 0 || date.compareTo(employee.getHiring()) > 0) {
                    if (!StringUtils.isEmpty(employee.getWpd()) && employee.getWpd().equals(day)) {
                        ads.setAttendanceType(AttendanceType.WEEKLY_PREPARATION_DAY.getValue());
                    } else {
                        ads.setAttendanceType(AttendanceType.PRESENT.getValue());
                    }
                } else {
                    ads.setAttendanceType(0);
                }

                ads.setClBalance(clBalance);
                ads.setYrBalance(yrBalance);
                ads.setStatus(0);
                adsList.add(ads);
            }

            List<Attendance> attendances =  repository.getAttendanceListByEmIdAndDate(employee.getId(), cmd.getStartDate(), cmd.getEndDate());

            for (AttendanceDateStatus ads : adsList) {
                for (Attendance a: attendances) {
                    if (ads.getAttendanceDate().equals(a.getAttendanceDate())) {
                        ads.setAttendanceId(a.getId());
                        ads.setAttendanceType(a.getAttendanceType());
                        ads.setStatus(a.getAttendanceStatus());
                    }
                }
            }

            as.setDateStatusList(adsList);
            attendanceList.add(as);
        }

        return attendanceList;
    }

    public int getClBalance(Employee employee, LocalDate today, YrCl currentYear) {
        int balanceCl = 0;
        if(hasCl(employee, today, currentYear)) {
            int totalMaturedCl = getTotalMaturedCl(today, currentYear, employee.getHiring());
            int totalConsumedCl = attendanceDao.getConsumedLeave(employee.getId(), AttendanceType.CASUAL_LEAVE.getValue(),
                    currentYear.getDateFrom(), today);
            balanceCl = totalMaturedCl - totalConsumedCl;
        }
        return balanceCl;
    }

    public int getYrBalance(Employee employee, LocalDate today) {
        int balanceYr = 0;

        if(employee.getRegularDate() != null && employee.getStatus().equals(EmployeeStatus.REGULAR.getName())) {
            LocalDate regularDate = employee.getRegularDate();
            int totalYr;
            int totalConsumedYr;

            int totalConYr = attendanceDao.getConsumedLeave(employee.getId(), AttendanceType.YEARLY_REJUVENATION.getValue(), null, null) + employee.getPreConsumedYr();

            int totalYrEncashDays = attendanceDao.getTotalYrEncashDays(employee.getId());

            totalYr = getTotalYr(regularDate, today);
            totalConsumedYr = totalConYr;
            balanceYr = totalYr - (totalConsumedYr + totalYrEncashDays);

            boolean exEmployee = false;
            for (String s : EmployeeStatus.EX_EMPLOYEE_STATUSES) {
                if (Objects.equals(s, employee.getStatus())) {
                    exEmployee = true;
                    break;
                }
            }

            if (exEmployee) {
                LocalDate dateFrom = repository.getStatusInfoByEmployeeIdAndStatus(employee.getId(), employee.getStatus());

                if (dateFrom != null) {
                    totalYr = getTotalYr(regularDate, dateFrom);
                    totalConsumedYr = totalConYr;
                    balanceYr = totalYr - (totalConsumedYr + totalYrEncashDays);
                }
            }
        }

        return balanceYr;
    }

    public int getTotalYr(LocalDate start, LocalDate end) {
        int totalYr = 0;

        Map<String, YrCl> yearWiseYrMap = getYrMap(start, end);
        LocalDate regularMatureDate = start.plusYears(1);

        if(isMatured(start, end)){
            for(Map.Entry entry : yearWiseYrMap.entrySet()){

                YrCl yr = (YrCl) entry.getValue();

                if(start.compareTo(yr.getDateFrom()) >= 0 && start.compareTo(yr.getDateTo()) <= 0) {
                    int monthDiff = Period.between(yr.getDateFrom(), yr.getDateTo()).getMonths();
                    int currentMonth = Period.between(start, yr.getDateTo()).getMonths();
                    totalYr += (int) Math.floor((yr.getYr() * currentMonth) / monthDiff);
                } else if(regularMatureDate.compareTo(yr.getDateFrom()) >= 0 && regularMatureDate.compareTo(yr.getDateTo()) <= 0) {
                    if(regularMatureDate.equals(yr.getDateTo()) || end.isAfter(yr.getDateTo())){
                        totalYr += yr.getYr();
                    }
                } else {
                    if(end.compareTo(yr.getDateFrom()) >= 0 && end.compareTo(yr.getDateTo()) <= 0) {
                        if(end.equals(yr.getDateTo())){
                            totalYr += yr.getYr();
                        }
                    } else {
                        totalYr += yr.getYr();
                    }
                }
            }
        }

        return totalYr;
    }

    private Map<String, YrCl> getYrMap(LocalDate regularDate, LocalDate currentDate){
        Map<String, YrCl> yrMap = new LinkedHashMap<String, YrCl>();
        Map<String, YrCl> YrClMap = getYearWiseYrMap();

        for(Map.Entry entry : YrClMap.entrySet()){
            String key = (String) entry.getKey();
            YrCl yr = (YrCl) entry.getValue();

            if(regularDate.compareTo(yr.getDateFrom()) >= 0 && regularDate.compareTo(yr.getDateTo()) <= 0){
                yrMap.put(key, yr);
            } else if(yr.getDateFrom().isAfter(regularDate) && !currentDate.isBefore(yr.getDateFrom())){
                yrMap.put(key, yr);
            }

        }

        return yrMap;
    }

    private Map<String, YrCl> getYearWiseYrMap() {
        Map<String, YrCl> yrMap = new LinkedHashMap<>();
        List<YrCl> allYearWiseYr = getAllYearWiseYr();

        for(YrCl yr : allYearWiseYr){
            yrMap.put(yr.getDuration(), yr);
        }

        return yrMap;
    }

    private List<YrCl> getAllYearWiseYr() {
        return attendanceDao.getAllYearWiseYr(false);
    }

    private boolean isMatured(LocalDate regularDate, LocalDate currentDate){
        LocalDate regularMatureDate = regularDate.plusYears(1);
        return regularMatureDate.equals(currentDate) || currentDate.isAfter(regularMatureDate);
    }

    private boolean hasCl(Employee employee, LocalDate currentDate, YrCl yearWiseYr){
        boolean isApplicable = ChronoUnit.DAYS.between(employee.getHiring(), currentDate) > 90;

        return isApplicable &&
                yearWiseYr.getDateTo().isAfter(employee.getHiring()) &&
                (employee.getStatus().equals(EmployeeStatus.REGULAR.getName()) ||
                        employee.getStatus().equals(EmployeeStatus.IRREGULAR.getName()));
    }

    public int getTotalMaturedCl(LocalDate currentDate, YrCl yearWiseYr, LocalDate joinDate){
        LocalDate clStartDate = joinDate.plusDays(90); // 3 months later
        int result;

        if(clStartDate.compareTo(yearWiseYr.getDateFrom()) >= 0 && clStartDate.compareTo(yearWiseYr.getDateTo()) <= 0){
            result = getTotalMaturedClForNewEmployee(currentDate, yearWiseYr, joinDate);
        } else {
            result = getTotalMaturedClOldEmployee(currentDate, yearWiseYr);
        }

        return result;
    }

    private int getTotalMaturedClForNewEmployee(LocalDate currentDate, YrCl yrCl, LocalDate joinDate){
        int result = 0;
        LocalDate clStartDate = joinDate.plusDays(90); // 3 months later

        int totalMonthCL = Period.between(yrCl.getDateFrom(), yrCl.getDateTo()).getMonths();
        int totalMonth = Period.between(clStartDate, yrCl.getDateTo()).getMonths();
        int totalCl = getRoundValue(yrCl.getCl(), totalMonth, totalMonthCL);

        if(currentDate.isAfter(yrCl.getDateTo())){
            result = totalCl;
        } else if(currentDate.compareTo(yrCl.getDateFrom()) >= 0 && currentDate.compareTo(yrCl.getDateTo()) <= 0) {
            int clStartDay = clStartDate.getDayOfMonth();
            int clStartMonth = clStartDate.getMonthValue();
            int currentMonth = currentDate.getMonthValue();

            result = getTotalMaturedClOldEmployee(currentDate, yrCl) - (yrCl.getCl() - totalCl);

            if(clStartMonth == currentMonth && clStartDay > 15){
                result = 0;
            }
        }

        return result;
    }

    private int getTotalMaturedClOldEmployee(LocalDate currentDate, YrCl yrCl){
        int result = 0;

        if(currentDate.isAfter(yrCl.getDateTo())){
            result = yrCl.getCl();
        } else if(currentDate.compareTo(yrCl.getDateFrom()) >= 0 && currentDate.compareTo(yrCl.getDateTo()) <= 0) {
            int totalMonth = Period.between(yrCl.getDateFrom(), yrCl.getDateTo()).getMonths();
            int currentMonth = Period.between(yrCl.getDateFrom(), currentDate).getMonths();
            int totalCl = yrCl.getCl();

            result = getTotalCalculatedCl(totalMonth, currentMonth, totalCl);
        }

        return result;
    }

    private int getTotalCalculatedCl(int totalMonth, int currentMonth, int totalCl){
        int factor = 4;
        int ceilValue = getCeilValue(currentMonth, factor, totalMonth);

        return getCeilValue(totalCl, ceilValue, factor);
    }

    private static int getRoundValue(int total, int multiplyBy, int divideBy){
        return (int) Math.round((total * multiplyBy) / (double) divideBy);
    }

    private static int getCeilValue(int total, int multiplyBy, int divideBy){
        return (int) Math.ceil((total * multiplyBy) / (double) divideBy);
    }

    private List<Branch> getCenterWithSubsidiaries(Long center) {
        final String uri = String.format(CENTER_WITH_SUBSIDIARIES, resourceUrl, center);
        return restService.getForList(uri, new ParameterizedTypeReference<>() {});
    }

    private List<Branch> getBranches(Long branchId) {
        final String uri = String.format(BRANCH_LIST, resourceUrl, branchId);
        return restService.getForList(uri, new ParameterizedTypeReference<>() {});
    }

    public List<AttendanceStatus> create(AttendanceCriteria criteria) {
        UserDetails login = UserContext.getPrincipal().getUserDetails();

        if (criteria.getAction().equals("submit")) {
            for (AttendanceStatus status : criteria.getAttendanceList()) {
                if (status.getDateStatusList() != null) {
                    for (AttendanceDateStatus ads : status.getDateStatusList()) {
                        if (ads.getAttendanceDate() != null && (ads.getAttendanceType() != null && ads.getAttendanceType() > 0) && getAttendance(status.getEmployee().getId(), ads.getAttendanceDate()) == null ) {
                            Attendance attendance = new Attendance();
                            attendance.setEmployee(status.getEmployee());
                            attendance.setAttendanceType(ads.getAttendanceType());
                            attendance.setAttendanceDate(ads.getAttendanceDate());
                            attendance.setAttendanceStatus(1);
                            attendance.setCreatedBy(login.getId());
                            attendance.setUpdatedBy(login.getId());
                            attendance.setAttendanceFrom("Manual");
                            repository.save(attendance);
                        }
                    }
                }
            }
        } else if (criteria.getAction().equals("approve")) {
            for (AttendanceStatus status : criteria.getAttendanceList()) {
                if (status.getDateStatusList() != null) {
                    for (AttendanceDateStatus ads : status.getDateStatusList()) {
                        if (ads.getAttendanceDate() != null && ads.getStatus() == 1) {
                            Attendance attendance = getAttendance(status.getEmployee().getId(), ads.getAttendanceDate());
                            attendance.setAttendanceStatus(2);
                            attendance.setUpdatedBy(login.getId());
                            repository.save(attendance);
                        }
                    }
                }
            }
        }

        return null;
    }

    public Attendance update(Attendance attendance) {
        UserDetails login = UserContext.getPrincipal().getUserDetails();
        attendance.setUpdatedBy(login.getId());

        return this.repository.save(attendance);
    }

    public Attendance getAttendance(Long employeeId, LocalDate attendanceDate) {
        List<Attendance> attendanceList = repository.getAttendance(employeeId, attendanceDate);
        return !attendanceList.isEmpty() ? attendanceList.get(0) : null;
    }

    List<String> getDateRange(LocalDate startDate, LocalDate endDate) {
        List<String> dateList = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("dd-MMM"));
            dateList.add(formattedDate);
            currentDate = currentDate.plusDays(1);
        }
        return dateList;
    }

    public void processAttendanceDetails(YrCl yrCl, Employee employee, Model model) {
        List<AttendanceSummaryDto> summaryDtos = attendanceDao.getAttendanceSummaryList(yrCl, employee.getId());
        AttendanceSummaryDto totalRow = new AttendanceSummaryDto();
        totalRow.setMonthName("Total");
        LocalDate today = LocalDate.now();

        summaryDtos.forEach(dto -> {
            dto.setTotal(dto.getPresent() + dto.getAbsent() + dto.getOdOtpTraining()+ dto.getWpd() + dto.getCl() + dto.getYr() + dto.getLwp() + dto.getOtherLeave());
            totalRow.setPresent(totalRow.getPresent() + dto.getPresent());
            totalRow.setAbsent(totalRow.getAbsent() + dto.getAbsent());
            totalRow.setOdOtpTraining(totalRow.getOdOtpTraining() + dto.getOdOtpTraining());
            totalRow.setWpd(totalRow.getWpd() + dto.getWpd());
            totalRow.setCl(totalRow.getCl() + dto.getCl());
            totalRow.setYr(totalRow.getYr() + dto.getYr());
            totalRow.setLwp(totalRow.getLwp() + dto.getLwp());
            totalRow.setOtherLeave(totalRow.getOtherLeave() + dto.getOtherLeave());
            totalRow.setTotal(totalRow.getTotal() + dto.getTotal());
        });

        summaryDtos.add(totalRow);
        model.addAttribute("summaryList", summaryDtos);
        model.addAttribute("duration", yrCl.getDuration());

        StringBuilder jobDuration = new StringBuilder();
        if (employee.getHiring() != null) {
            Period period = Period.between(employee.getHiring(), today);
            if (EmployeeStatus.isExStatus(employee.getStatus())) {
                StatusInfo typeInfo = statusRepository.findTopByEmployeeIdAndStatusToOrderByIdDesc(employee.getId(), employee.getStatus());
                if (typeInfo == null) {
                    jobDuration.append("<span style='color:red'>Please Update Employee Status</span>");
                } else {
                    period = Period.between(employee.getHiring(), typeInfo.getDateFrom());
                }
            }
            int year = period.getYears();
            int month = period.getMonths();
            int days = period.getDays();
            jobDuration.append(year > 0 ? year + " Year(s) " : "").append(month > 0 ? month + " Month(s) " : "").append(days > 0 ? days + " Day(s)" : "");
        }
        model.addAttribute("jobDuration", jobDuration.toString());

        if (hasCl(employee, LocalDate.now(), yrCl)) {
            model.addAttribute("totalCl", yrCl.getCl());
            int totalMaturedCl = getTotalMaturedCl(LocalDate.now(), yrCl, employee.getHiring());
            int totalConsumedCl = attendanceDao.getConsumedLeave(employee.getId(), AttendanceType.CASUAL_LEAVE.getValue(),
                    yrCl.getDateFrom(), yrCl.getDateTo());

            model.addAttribute("totalMaturedCl", totalMaturedCl);
            model.addAttribute("totalConsumedCl", totalConsumedCl);
            model.addAttribute("totalBalancedCl", totalMaturedCl - totalConsumedCl);
            model.addAttribute("hasCl", true);
        } else {
            model.addAttribute("hasCl", false);
        }

        if (employee.getRegularDate() != null && employee.getStatus().equals(EmployeeStatus.REGULAR.getName())) {
            LocalDate regularDate = employee.getRegularDate();
            String totalYrString;
            int totalYr;
            int totalConsumedYr;
            int totalBalancedYr;

            int totalConYr =  attendanceDao.getConsumedLeave(employee.getId(), AttendanceType.YEARLY_REJUVENATION.getValue(), null, null) + employee.getPreConsumedYr();
            int totalYrEncashDays =  attendanceDao.getTotalYrEncashDays(employee.getId());

            totalYr = getTotalYr(regularDate, today);
            totalConsumedYr = totalConYr;
            totalBalancedYr = totalYr - (totalConsumedYr + totalYrEncashDays);
            totalYrString = String.valueOf(totalYr);

            if (EmployeeStatus.isExStatus(employee.getStatus())) {
                StatusInfo statusInfo = statusRepository.findTopByEmployeeIdAndStatusToOrderByIdDesc(employee.getId(), employee.getStatus());

                if (statusInfo == null) {
                    totalYrString = "<span style='color:red'>Please Update Employee Status</span>";
                } else {
                    LocalDate resignDate = statusInfo.getDateFrom();
                    totalYr = getTotalYr(regularDate, resignDate);

                    totalYrString = String.valueOf(totalYr);
                    totalConsumedYr = totalConYr;
                    totalBalancedYr = totalYr - (totalConsumedYr + totalYrEncashDays);
                }
            }

            model.addAttribute("totalYrString", totalYrString);
            model.addAttribute("totalConsumedYr",  totalConsumedYr);
            model.addAttribute("totalYrEncashDays",  totalYrEncashDays);
            model.addAttribute("totalBalancedYr",  totalBalancedYr);
        }

        int maternityLeave = 0;
        if (employee.getGender().equals("Female")) {
            maternityLeave = attendanceDao.getConsumedLeave(employee.getId(), AttendanceType.MATERNITY_LEAVE.getValue(), employee.getHiring(), LocalDate.now());
        }
        model.addAttribute("maternityLeave", maternityLeave);
    }

    public List<AttendanceSummaryDto> getAttendanceList(long empId, LocalDate dateFrom, LocalDate dateTo, String types) {
        return attendanceDao.getAttendanceList(empId, dateFrom, dateTo, types);
    }

    public List<YrCl> getYearWiseYrList() {
        return attendanceDao.getAllYearWiseYr(true);
    }

    public YrCl getYearWiseYrById(long id) {
        return yrClRepository.getById(id);
    }

    public DailyAttendanceCmd getAttendanceSummary(DailyAttendanceCmd cmd) {
        return attendanceDao.getAttendanceSummary(cmd);
    }

    public Map<String, List<DailyAttendanceCmd>> getDailyAttendanceMap(DailyAttendanceCmd cmd) {
        List<DailyAttendanceCmd> list = attendanceDao.getDailyAttendanceList(cmd);
        return list.stream().collect(Collectors.groupingBy(DailyAttendanceCmd::getTitle, LinkedHashMap::new, Collectors.toList()));
    }

    public ReportData generatePdf(DailyAttendanceCmd criteria) {
        Map<String, Object> result = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String title = "Daily Attendance View";
        String department = StringUtils.isEmpty(criteria.getBranchName()) || criteria.getBranchName().equals("Search by branch") ?
                UserContext.getPrincipal().getUserDetails().getBranchName() : criteria.getBranchName();
        String subTitle = StringUtils.isEmpty(criteria.getDepartmentName()) || criteria.getBranchName().equals("Search by department") ?
                UserContext.getPrincipal().getUserDetails().getDepartmentName() : criteria.getDepartmentName();
        String duration = criteria.getAttendanceDate() != null ? formatter.format(criteria.getAttendanceDate()) : formatter.format(LocalDate.now());
        String orientation = Constants.PAGE_PORTRAIT;
        String view = "";

        if (criteria.getAttendanceDate() == null) {
            criteria.setAttendanceDate(LocalDate.now());
        }

        result.put("summary", getAttendanceSummary(criteria));
        result.put("data", getDailyAttendanceMap(criteria));

        return new ReportData(new ReportHeader(department, title, subTitle, duration), getFooterText(), orientation, view, result);
    }

    private String getFooterText(){
        String currentDateTime = DateUtils.formatToDDMMYYYYHHMMSSA(new Date());
        return String.format("Generated By %s on %s", UserContext.getPrincipal().getUserDetails().getName(), currentDateTime);
    }
}
