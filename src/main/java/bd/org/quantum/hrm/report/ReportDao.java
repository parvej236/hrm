package bd.org.quantum.hrm.report;

import bd.org.quantum.authorizer.Authorizer;
import bd.org.quantum.authorizer.helper.UserContext;
import bd.org.quantum.authorizer.model.UserDetails;
import bd.org.quantum.hrm.attendance.AttendanceCriteria;
import bd.org.quantum.hrm.attendance.AttendanceType;
import bd.org.quantum.hrm.common.Branch;
import bd.org.quantum.hrm.common.CommonService;
import bd.org.quantum.hrm.common.Department;
import bd.org.quantum.hrm.employee.EmployeeService;
import bd.org.quantum.hrm.employee.EmployeeStatus;
import bd.org.quantum.hrm.role.Permissions;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ReportDao {
    private final JdbcTemplate jdbcTemplate;
    private final Authorizer authorizer;
    private final CommonService commonService;
    private final EmployeeService employeeService;

    public ReportDao(JdbcTemplate jdbcTemplate, Authorizer authorizer,
                     CommonService commonService, EmployeeService employeeService) {
        this.jdbcTemplate = jdbcTemplate;
        this.authorizer = authorizer;
        this.commonService = commonService;
        this.employeeService = employeeService;
    }

    public List<EmployeeReportDto> getEmployeeList(ReportCriteria criteria) {
        List<Object> params = new ArrayList<>();
        StringBuilder qb = new StringBuilder(100);

        qb.append("WITH emp_info AS (");
        qb.append("SELECT e.id, e.name_en, m.registration_code, e.pre_consumed_yr, ");
        qb.append("hd.name designation, e.branch_name, e.hiring, e.regular_date, e.last_promotion_date, ");
        qb.append("e.department_name, hd.sorting_order, e.image_path, e.primary_phone, m.primary_phone mprimary_phone, ");
        qb.append("m.secondary_phone, m.phone_office, m.phone_others, e.status, ");
        qb.append("CASE WHEN e.employee_id IS NOT NULL THEN (TO_CHAR(e.hiring, 'YY') || e.employee_id) ELSE NULL END employee_id ");
        qb.append("FROM hrm.employee e JOIN member.member m ON e.member = m.id ");
        qb.append("JOIN hrm.designation hd ON e.designation = hd.id ");
        filterString(criteria, qb, params);

        qb.append("GROUP BY e.id, m.id, hd.id), ");
        qb.append("edu_info AS (");
        qb.append("SELECT employee, subject_department, exam_name, institution ");
        qb.append("FROM hrm.education_info WHERE employee IN(SELECT id FROM emp_info) ");
        qb.append("GROUP BY employee, subject_department, exam_name, institution, passing_year ");
        qb.append("ORDER BY passing_year DESC) ");
        qb.append("SELECT id, name_en, employee_id, registration_code, designation, branch_name, hiring, ");
        qb.append("regular_date, last_promotion_date, department_name, sorting_order, image_path, primary_phone, ");
        qb.append("mprimary_phone, secondary_phone, phone_office, phone_others, status, pre_consumed_yr, ");
        qb.append("(SELECT exam_name FROM edu_info WHERE employee = emp.id LIMIT 1) exam_name, ");
        qb.append("(SELECT subject_department FROM edu_info WHERE employee = emp.id LIMIT 1) subject, ");
        qb.append("(SELECT institution FROM edu_info WHERE employee = emp.id LIMIT 1) institution ");
        qb.append("FROM emp_info emp  ");

       if (!StringUtils.isEmpty(criteria.getSortingOrder1())) {
            if (criteria.getSortingOrder1().equals(ReportCriteria.SortingOrder.JOINING_DATE.name())) {
                qb.append("ORDER BY emp.hiring ");
            } else if (criteria.getSortingOrder1().equals(ReportCriteria.SortingOrder.BRANCH.name())) {
                qb.append("ORDER BY emp.branch_name ");
            } else if (criteria.getSortingOrder1().equals(ReportCriteria.SortingOrder.DEPARTMENT.name())) {
                qb.append("ORDER BY emp.department_name ");
            } else if (criteria.getSortingOrder1().equals(ReportCriteria.SortingOrder.DESIGNATION.name())) {
                qb.append("ORDER BY emp.sorting_order ");
            }

            if (criteria.getSortingType1() != null && criteria.getSortingType1().equals("DESC")) {
                qb.append("DESC");
            }

            if (criteria.getSortingOrder2().equals(ReportCriteria.SortingOrder.JOINING_DATE.name())) {
                qb.append(", emp.hiring ");
            } else if (criteria.getSortingOrder2().equals(ReportCriteria.SortingOrder.BRANCH.name())) {
                qb.append(", emp.branch_name ");
            } else if (criteria.getSortingOrder2().equals(ReportCriteria.SortingOrder.DEPARTMENT.name())) {
                qb.append(", emp.department_name ");
            } else if (criteria.getSortingOrder2().equals(ReportCriteria.SortingOrder.DESIGNATION.name())) {
                qb.append(", emp.sorting_order ");
            }

            if (criteria.getSortingType2() != null && criteria.getSortingType2().equals("DESC")) {
                qb.append("DESC");
            }
        } else {
           qb.append("ORDER BY emp.sorting_order");
       }

        List<EmployeeReportDto> list  = jdbcTemplate.query(qb.toString(), params.toArray(), (RowMapper) (rs, i) -> {
            EmployeeReportDto dto = new EmployeeReportDto();
            dto.setId(rs.getLong("id"));
            dto.setName(rs.getString("name_en"));
            dto.setEmployeeId(rs.getString("employee_id"));
            dto.setPhone(rs.getString("primary_phone"));
            dto.setPrimaryPhone(rs.getString("mprimary_phone"));
            dto.setSecondaryPhone(rs.getString("secondary_phone"));
            dto.setPhoneOffice(rs.getString("phone_office"));
            dto.setPhoneOthers(rs.getString("phone_others"));
            dto.setRegCode(rs.getString("registration_code"));
            dto.setDesignation(rs.getString("designation"));
            dto.setBranchName(rs.getString("branch_name"));
            dto.setDepartmentName(rs.getString("department_name"));
            dto.setJoiningDate(rs.getString("hiring"));
            dto.setRegularDate(rs.getString("regular_date"));
            dto.setPreConsumedYr(rs.getInt("pre_consumed_yr"));
            dto.setPromotionDate(rs.getString("last_promotion_date"));
            dto.setExamName(rs.getString("exam_name"));
            dto.setSubjectDepartment(rs.getString("subject"));
            dto.setInstitution(rs.getString("institution"));
            dto.setImagePath(rs.getString("image_path"));
            dto.setStatus(rs.getString("status"));
            return dto;
        });

        return list;
    }

    public List<AttendanceCriteria> getAttendanceList(ReportCriteria criteria) {
        List<Object> params = new ArrayList<>();
        List<Long> branchList;
        List<Long> departmentList;

        UserDetails login = UserContext.getPrincipal().getUserDetails();
        StringBuilder qb = new StringBuilder();

        qb.append("SELECT e.employee_id, e.name_en, d.name, e.hiring, ");
        if (criteria.getReportType().equalsIgnoreCase(ReportType.ATTENDANCE_SHEET.name())) {
            for (LocalDate localDate : criteria.getDateRange()) {
                String date = localDate.toString();
                String day = String.valueOf(localDate.getDayOfMonth()) ;
                qb.append("SUM(CASE WHEN (a.attendance_date = '").append(date)
                        .append("' AND a.attendance_type > 0) THEN a.attendance_type ELSE 0 END) AS \"").append(day).append("\", ");
            }
        }

        for (int key : AttendanceType.getAllShortAttendanceTypes().keySet()) {
            qb.append("SUM(CASE WHEN a.attendance_type = ").append(key).append(" THEN 1 ELSE 0 END) AS \"")
                    .append(AttendanceType.getAllShortAttendanceTypes().get(key));

            if (key != AttendanceType.NOT_APPLICABLE.getValue()) {
                qb.append("\", ");
            } else {
                qb.append("\" ");
            }
        }

        qb.append("FROM hrm.employee_attendance a ");
        qb.append("JOIN hrm.employee e ON a.employee = e.id ");
        qb.append("LEFT JOIN hrm.designation d ON e.designation = d.id ");

        String conjunction = "WHERE ";

        if (StringUtils.isEmpty(criteria.getStatus())) {
            List<String> statuses = Arrays.asList(EmployeeStatus.RUNNING_ALL_EMPLOYEE_STATUSES);
            qb.append(conjunction).append("e.status IN('").append(statuses.stream().map(String::valueOf).collect(Collectors.joining("','"))).append("') ");
            conjunction = "AND ";
        } else if (!StringUtils.isEmpty(criteria.getStatus()) && !criteria.getStatus().equals("99")) {
            qb.append(conjunction).append("e.status = ? ");
            params.add(criteria.getStatus());
            conjunction = "AND ";
        }

        if(authorizer.hasPermission(Permissions.EMPLOYEE_BRANCH_ACCESS.name())  && !authorizer.hasPermission(Permissions.EMPLOYEE_BRANCH_INTER_ACCESS.name())) {
            if (criteria.getBranch() > 0) {
                qb.append(conjunction).append("e.branch IN (").append(criteria.getBranch()).append(") ");
            } else {
                qb.append(conjunction).append("e.branch IN (").append(login.getBranchId()).append(") ");
            }
            if (criteria.getDepartment() != null && criteria.getDepartment() > 0) {
                qb.append(conjunction).append("e.department IN (").append(criteria.getDepartment()).append(") ");
            }
            conjunction = "AND ";
        } else if(authorizer.hasPermission(Permissions.EMPLOYEE_BRANCH_ACCESS.name())  && authorizer.hasPermission(Permissions.EMPLOYEE_BRANCH_INTER_ACCESS.name())) {
            if (criteria.getBranch() > 0) {
                qb.append(conjunction).append("e.branch IN (").append(criteria.getBranch()).append(") ");
            } else {
                branchList = commonService.getBranches(login.getBranchId()).stream().map(Branch::getId).collect(Collectors.toList());
                qb.append(conjunction).append("e.branch IN (").append(branchList.stream().map(String::valueOf).collect(Collectors.joining(","))).append(") ");
            }
            if (criteria.getDepartment() != null && criteria.getDepartment() > 0) {
                qb.append(conjunction).append("e.department IN (").append(criteria.getDepartment()).append(") ");
            }
            conjunction = "AND ";
        } else {
            if (criteria.getBranch() != null && criteria.getBranch() > 0) {
                qb.append(conjunction).append("e.branch IN (").append(criteria.getBranch()).append(") ");
            } else {
                qb.append(conjunction).append("e.branch IN (").append(login.getBranchId()).append(") ");
            }

            if (authorizer.hasPermission(Permissions.EMPLOYEE_DEPARTMENT_INTER_ACCESS.name())) {
                departmentList = commonService.getDepartmentsSubsidiaries(login.getDepartmentId()).stream().map(Department::getId).collect(Collectors.toList());
                qb.append(conjunction).append("e.department IN (").append(departmentList.stream().map(String::valueOf).collect(Collectors.joining(","))).append(") ");
            } else if (login.getDepartmentId() != null) {
                qb.append(conjunction).append("e.department IN (").append(login.getDepartmentId()).append(") ");
            }
            conjunction = "AND ";
        }

        if (criteria.getDesignation() != null && criteria.getDesignation() > 0) {
            qb.append(conjunction).append("d.id = ? ");
            params.add(criteria.getDesignation());
            conjunction = "AND ";
        }

        if (!criteria.getGender().equals("All")) {
            qb.append(conjunction).append("e.gender = ? ");
            params.add(criteria.getGender());
            conjunction = "AND ";
        } else {
            qb.append(conjunction).append("e.gender IN ('Male', 'Female') ");
            conjunction = "AND ";
        }

        if (!StringUtils.isEmpty(criteria.getSalaryLocation())) {
            qb.append(conjunction).append("e.salary_location = ? ");
            params.add(criteria.getSalaryLocation());
        }

        if (criteria.getDateFrom() != null && criteria.getDateTo() != null) {
            qb.append(conjunction).append("a.attendance_date >= ? AND a.attendance_date <= ? ");
            params.add(criteria.getDateFrom());
            params.add(criteria.getDateTo());
        }

        qb.append("GROUP BY a.employee, e.employee_id, e.name_en, e.hiring, d.name, d.sorting_order ORDER BY d.sorting_order, e.hiring");

        List<AttendanceCriteria> list = jdbcTemplate.query(qb.toString(), params.toArray(),
                (RowMapper) (rs, i) -> {
                    AttendanceCriteria command = new AttendanceCriteria();
                    command.setEmployeeId(rs.getString("employee_id"));
                    command.setEmployeeName(rs.getString("name_en"));
                    command.setEmpDesignationStr(rs.getString("name"));
                    command.setJoinDate(rs.getString("hiring"));

                    if (criteria.getReportType().equalsIgnoreCase(ReportType.ATTENDANCE_SHEET.name())) {
                        List<String> attendanceList = new ArrayList<>();
                        for (LocalDate localDate : criteria.getDateRange()) {
                            String day = String.valueOf(localDate.getDayOfMonth()) ;
                            attendanceList.add(AttendanceType.getAllShortAttendanceTypes().get(Integer.valueOf(rs.getString(day))));
                        }
                        command.setAttendanceTypeList(attendanceList);
                    }

                    List<Integer> totalList = new ArrayList<>();
                    for (int key : AttendanceType.getAllShortAttendanceTypes().keySet()) {
                        totalList.add(rs.getInt(AttendanceType.getAllShortAttendanceTypes().get(key)));
                    }
                    command.setTotalAttendance(totalList);

                    return command;
                });

        return list;
    }

    public List<EmployeeStatementCommand> getDesignationWiseStatement(ReportCriteria criteria) {
        StringBuilder qb = new StringBuilder();
        List<Object> params = new ArrayList<>();

        qb.append("SELECT e.designation_name, ");
        qb.append("SUM(CASE WHEN e.member_type = 'PRO_MASTER' AND e.gender = 'Male' THEN 1 ELSE 0 END) qpm_male, ");
        qb.append("SUM(CASE WHEN e.member_type = 'PRO_MASTER' AND e.gender = 'Female' THEN 1 ELSE 0 END) qpm_female, ");
        qb.append("SUM(CASE WHEN e.member_type = 'PRO_MASTER' THEN 1 ELSE 0 END) qpm_total, ");
        qb.append("SUM(CASE WHEN e.member_type = 'GRADUATE' AND e.gender = 'Male' THEN 1 ELSE 0 END) qg_male, ");
        qb.append("SUM(CASE WHEN e.member_type = 'GRADUATE' AND e.gender = 'Female' THEN 1 ELSE 0 END) qg_female, ");
        qb.append("SUM(CASE WHEN e.member_type = 'GRADUATE' THEN 1 ELSE 0 END) qg_total, ");
        qb.append("SUM(CASE WHEN e.member_type = 'ASSOCIATE' AND e.gender = 'Male' THEN 1 ELSE 0 END) qa_male, ");
        qb.append("SUM(CASE WHEN e.member_type = 'ASSOCIATE' AND e.gender = 'Female' THEN 1 ELSE 0 END) qa_female, ");
        qb.append("SUM(CASE WHEN e.member_type = 'ASSOCIATE' THEN 1 ELSE 0 END) qa_total, ");
        qb.append("SUM(CASE WHEN e.gender = 'Male' THEN 1 ELSE 0 END) total_male, ");
        qb.append("SUM(CASE WHEN e.gender = 'Female' THEN 1 ELSE 0 END) total_female, ");
        qb.append("COUNT(e.member) total ");
        qb.append("FROM hrm.employee e ");
        qb.append("JOIN hrm.designation d ON e.designation = d.id ");
        qb.append("WHERE e.status IN('");
        qb.append(criteria.getStatuses().stream().map(String::valueOf).collect(Collectors.joining("','"))).append("') ");

        if (criteria.getDepartments() != null && criteria.getDepartments().size() > 0) {
            qb.append("AND e.department IN(");
            qb.append(criteria.getDepartments().stream().map(String::valueOf).collect(Collectors.joining(","))).append(") ");
        }

        if (criteria.getBranches() != null && criteria.getBranches().size() > 0) {
            qb.append("AND e.branch IN(");
            qb.append(criteria.getBranches().stream().map(String::valueOf).collect(Collectors.joining(","))).append(") ");
        }

        if (criteria.getDesignations() != null && criteria.getDesignations().size() > 0) {
            qb.append("AND d.id IN(");
            qb.append(criteria.getDesignations().stream().map(String::valueOf).collect(Collectors.joining(","))).append(") ");
        }

        if (!StringUtils.isEmpty(criteria.getGender()) && !criteria.getGender().equals("Others")) {
            qb.append("AND e.gender = ? ");
            params.add(criteria.getGender());
        }

        qb.append("GROUP BY e.designation_name, d.sorting_order ORDER BY d.sorting_order");

        List<EmployeeStatementCommand> list  = jdbcTemplate.query(qb.toString(), params.toArray(), (RowMapper) (rs, i) -> {
            EmployeeStatementCommand command = new EmployeeStatementCommand();
            command.setDesignation(rs.getString("designation_name"));
            command.setQpmMale(rs.getInt("qpm_male"));
            command.setQpmFemale(rs.getInt("qpm_female"));
            command.setQpmTotal(rs.getInt("qpm_total"));
            command.setQgMale(rs.getInt("qg_male"));
            command.setQgFemale(rs.getInt("qg_female"));
            command.setQgTotal(rs.getInt("qg_total"));
            command.setQaMale(rs.getInt("qa_male"));
            command.setQaFemale(rs.getInt("qa_female"));
            command.setQaTotal(rs.getInt("qa_total"));
            command.setTotalMale(rs.getInt("total_male"));
            command.setTotalFemale(rs.getInt("total_female"));
            command.setTotal(rs.getInt("total"));
            return command;
        });

        return list;
    }

    public List<EmployeeStatementCommand> getMonthlyEmployeeStatement(ReportCriteria criteria) {
        StringBuilder qb = new StringBuilder();
        List<Object> params = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfThisMonth = today.withDayOfMonth(1);
        LocalDate lastDayOfLastMonth = firstDayOfThisMonth.minusDays(1);

        criteria.setDateType("JOINING_DATE");
        criteria.setDateFrom(LocalDate.parse("1983-01-01"));
        criteria.setDateTo(lastDayOfLastMonth);

        qb.append("WITH prev_month_emp AS (");
        qb.append("SELECT e.branch, e.branch_name, COUNT(e.*) prev_month_count, ");
        qb.append("SUM(CASE WHEN e.status = 'Regular' THEN 1 ELSE 0 END) regular, ");
        qb.append("SUM(CASE WHEN e.status = 'Irregular' THEN 1 ELSE 0 END) irregular, ");
        qb.append("SUM(CASE WHEN e.status NOT IN('Regular','Irregular') THEN 1 ELSE 0 END) AS others ");
        qb.append("FROM hrm.employee e ");
        filterString(criteria, qb, params);
        qb.append("GROUP BY e.branch, e.branch_name), ");

        qb.append("cur_month_transfer_from AS (");
        qb.append("SELECT t.branch_from, t.branch_from_name, COUNT(t.*) transfer_count, ");
        qb.append("SUM(CASE WHEN e.status = 'Regular' THEN 1 ELSE 0 END) regular, ");
        qb.append("SUM(CASE WHEN e.status = 'Irregular' THEN 1 ELSE 0 END) irregular, ");
        qb.append("SUM(CASE WHEN e.status NOT IN('Regular','Irregular') THEN 1 ELSE 0 END) AS others ");
        qb.append("FROM hrm.transfer_info t JOIN hrm.employee e ON t.employee = e.id ");
        filterString(criteria, qb, params);
        qb.append("AND t.transfer_date >= '").append(firstDayOfThisMonth).append("' ");
        qb.append("GROUP BY t.branch_from, t.branch_from_name), ");

        qb.append("cur_month_transfer_to AS (");
        qb.append("SELECT t.branch_to, t.branch_to_name, COUNT(t.*) transfer_count, ");
        qb.append("SUM(CASE WHEN e.status = 'Regular' THEN 1 ELSE 0 END) regular, ");
        qb.append("SUM(CASE WHEN e.status = 'Irregular' THEN 1 ELSE 0 END) irregular, ");
        qb.append("SUM(CASE WHEN e.status NOT IN('Regular','Irregular') THEN 1 ELSE 0 END) AS others ");
        qb.append("FROM hrm.transfer_info t JOIN hrm.employee e ON t.employee = e.id ");
        filterString(criteria, qb, params);
        qb.append("AND t.transfer_date >= '").append(firstDayOfThisMonth).append("' ");
        qb.append("GROUP BY t.branch_to, t.branch_to_name), ");

        criteria.setDateFrom(firstDayOfThisMonth);
        criteria.setDateTo(today);

        qb.append("cur_month_emp AS (");
        qb.append("SELECT e.branch, e.branch_name, COUNT(e.*) cur_month_count, ");
        qb.append("SUM(CASE WHEN e.status = 'Regular' THEN 1 ELSE 0 END) regular, ");
        qb.append("SUM(CASE WHEN e.status = 'Irregular' THEN 1 ELSE 0 END) irregular, ");
        qb.append("SUM(CASE WHEN e.status NOT IN('Regular','Irregular') THEN 1 ELSE 0 END) AS others ");
        qb.append("FROM hrm.employee e ");
        filterString(criteria, qb, params);
        qb.append("GROUP BY e.branch, e.branch_name), ");

        criteria.setStatus(null);
        criteria.setStatuses(null);
        criteria.setDateFrom(LocalDate.parse("1983-01-01"));
        criteria.setDateTo(lastDayOfLastMonth);
        qb.append("cur_month_resigned AS (");
        qb.append("SELECT e.branch, e.branch_name, COUNT(t.*) resigned_count, ");
        qb.append("SUM(CASE WHEN e.status = 'Regular' THEN 1 ELSE 0 END) regular, ");
        qb.append("SUM(CASE WHEN e.status = 'Irregular' THEN 1 ELSE 0 END) irregular, ");
        qb.append("SUM(CASE WHEN e.status NOT IN('Regular','Irregular') THEN 1 ELSE 0 END) AS others ");
        qb.append("FROM hrm.type_info t JOIN hrm.employee e ON t.employee = e.id ");
        filterString(criteria, qb, params);
        qb.append("AND t.date_from >= '").append(firstDayOfThisMonth).append("' ");
        qb.append("AND t.status_to IN('");
        qb.append(Arrays.stream(EmployeeStatus.EX_EMPLOYEE_STATUSES).map(String::valueOf).collect(Collectors.joining("','"))).append("') ");
        qb.append("GROUP BY e.branch, e.branch_name) ");

        qb.append("SELECT e.branch_name, e.prev_month_count, (COALESCE(ce.cur_month_count, 0) + COALESCE(t2.transfer_count, 0)) cur_month_count, ");
        qb.append("t.transfer_count, r.resigned_count, ");
        qb.append("((e.prev_month_count + COALESCE(ce.cur_month_count, 0) + COALESCE(t2.transfer_count, 0)) - (COALESCE(t.transfer_count, 0) + COALESCE(r.resigned_count,0))) present_emp_count, ");
        qb.append("((e.regular + COALESCE(ce.regular, 0) + COALESCE(t2.regular, 0)) - (COALESCE(t.regular, 0) + COALESCE(r.regular,0))) regular_count, ");
        qb.append("((e.irregular + COALESCE(ce.irregular, 0) + COALESCE(t2.irregular, 0)) - (COALESCE(t.irregular, 0) + COALESCE(r.irregular,0))) irregular_count, ");
        qb.append("((e.others + COALESCE(ce.others, 0) + COALESCE(t2.others, 0)) - (COALESCE(t.others, 0) + COALESCE(r.others,0))) others_count ");
        qb.append("FROM prev_month_emp e LEFT JOIN cur_month_emp ce ON e.branch = ce.branch ");
        qb.append("LEFT JOIN cur_month_transfer_from t ON e.branch = t.branch_from ");
        qb.append("LEFT JOIN cur_month_transfer_to t2 ON e.branch = t2.branch_to ");
        qb.append("LEFT JOIN cur_month_resigned r ON e.branch = r.branch ");
        qb.append("ORDER BY e.branch");

        List<EmployeeStatementCommand> list  = jdbcTemplate.query(qb.toString(), params.toArray(), (RowMapper) (rs, i) -> {
            EmployeeStatementCommand command = new EmployeeStatementCommand();
            command.setBranchName(rs.getString("branch_name"));
            command.setPrevMonthCount(rs.getInt("prev_month_count"));
            command.setJoinedInCurMonth(rs.getInt("cur_month_count"));
            command.setTransferedInCurMonth(rs.getInt("transfer_count"));
            command.setResignedInCurMonth(rs.getInt("resigned_count"));
            command.setCurMonthCount(rs.getInt("present_emp_count"));
            command.setRegularCount(rs.getInt("regular_count"));
            command.setIrregularCount(rs.getInt("irregular_count"));
            command.setOthersCount(rs.getInt("others_count"));
            return command;
        });

        return list;
    }

    public List<EmployeeStatementCommand> getEmployeeListDetails(ReportCriteria criteria) {
        StringBuilder qb = new StringBuilder();
        List<Object> params = new ArrayList<>();

        qb.append("WITH emp_info AS (");
        qb.append("SELECT e.id, (TO_CHAR(e.hiring, 'YY') || e.employee_id) employee_id, e.name_en, hd.name designation, e.status, ");
        qb.append("e.branch_name, e.department_name, e.hiring, e.regular_date, e.last_promotion_date, ");
        qb.append("m.member_type, e.date_of_birth, e.primary_phone, e.present_district, e.blood_group, ");
        qb.append("e.marital_status, hd.sorting_order ");
        qb.append("FROM hrm.employee e JOIN member.member m ON e.member = m.id ");
        qb.append("JOIN hrm.designation hd ON e.designation = hd.id ");
        filterString(criteria, qb, params);
        qb.append("GROUP BY e.id, m.id, hd.id), ");

        qb.append("edu_info AS (");
        qb.append("SELECT ei.employee, ei.subject_department, ei.exam_name, ei.institution ");
        qb.append("FROM hrm.education_info ei JOIN emp_info e ON ei.employee = e.id ");
        qb.append("GROUP BY employee, subject_department, exam_name, institution, passing_year ");
        qb.append("ORDER BY passing_year DESC), ");

        qb.append("transfer_info AS (");
        qb.append("SELECT MAX(t.id) max_id, t.employee, t.branch_from, t.branch_from_name ");
        qb.append("FROM hrm.transfer_info t JOIN emp_info e ON t.employee = e.id ");
        qb.append("GROUP BY t.employee, t.branch_from, t.branch_from_name ");
        qb.append("ORDER BY max_id DESC, t.employee), ");

        qb.append("family_info AS (");
        qb.append("SELECT f.employee, SUM(CASE WHEN f.relation = 'Son' THEN 1 ELSE 0 END) son, ");
        qb.append("SUM(CASE WHEN f.relation = 'Daughter' THEN 1 ELSE 0 END) daughter ");
        qb.append("FROM hrm.family_info f JOIN emp_info e ON f.employee = e.id ");
        qb.append("GROUP BY f.employee), ");

        qb.append("pre_join_info AS (");
        qb.append("SELECT MAX(p.id) max_id, p.employee, p.branch, p.branch_name, p.description ");
        qb.append("FROM hrm.pre_joining_info p JOIN emp_info e ON p.employee = e.id ");
        qb.append("GROUP BY p.employee, p.branch, p.branch_name, p.description ");
        qb.append("ORDER BY max_id DESC, p.employee) ");

        qb.append("SELECT emp.employee_id, emp.name_en, emp.designation, emp.status, emp.branch_name, emp.department_name, ");
        qb.append("NULLIF((SELECT branch_from_name FROM transfer_info WHERE employee = emp.id LIMIT 1), ");
        qb.append("(SELECT branch_name FROM pre_join_info WHERE employee = emp.id LIMIT 1)) AS last_cbc, ");
        qb.append("emp.hiring, emp.regular_date, emp.last_promotion_date, emp.member_type, emp.date_of_birth, ");
        qb.append("emp.primary_phone, emp.present_district, emp.blood_group, ");
        qb.append("(SELECT exam_name FROM edu_info WHERE employee = emp.id LIMIT 1) academic_qualification, ");
        qb.append("emp.marital_status, fi.son, fi.daughter, ");
        qb.append("CASE WHEN (SELECT branch_from_name FROM transfer_info WHERE employee = emp.id LIMIT 1) IS NOT NULL THEN emp.designation ");
        qb.append("ELSE (SELECT description FROM pre_join_info WHERE employee = emp.id LIMIT 1) END AS remarks ");
        qb.append("FROM emp_info emp LEFT JOIN family_info fi ON emp.id = fi.employee ");
        qb.append("ORDER BY emp.sorting_order");

        List<EmployeeStatementCommand> list  = jdbcTemplate.query(qb.toString(), params.toArray(), (RowMapper) (rs, i) -> {
            EmployeeReportDto dto = new EmployeeReportDto();
            dto.setEmployeeId(rs.getString("employee_id"));
            dto.setName(rs.getString("name_en"));
            dto.setDesignation(rs.getString("designation"));
            dto.setStatus(rs.getString("status"));
            dto.setBranchName(rs.getString("branch_name"));
            dto.setDepartmentName(rs.getString("department_name"));
            dto.setLastCbc(rs.getString("last_cbc"));
            dto.setJoiningDate(rs.getString("hiring"));
            dto.setRegularDate(rs.getString("regular_date"));
            dto.setPromotionDate(rs.getString("last_promotion_date"));
            dto.setMemberType(rs.getString("member_type"));
            dto.setDateOfBirth(rs.getString("date_of_birth"));
            dto.setPrimaryPhone(rs.getString("primary_phone"));
            dto.setHomeDistrict(rs.getString("present_district"));
            dto.setBloodGroup(rs.getString("blood_group"));
            dto.setEduQualification(rs.getString("academic_qualification"));
            dto.setMaritalStatus(rs.getString("marital_status"));
            dto.setSon(rs.getInt("son"));
            dto.setDaughter(rs.getInt("daughter"));
            dto.setRemarks(rs.getString("remarks"));
            return dto;
        });

        return list;
    }

    private void filterString(ReportCriteria criteria, StringBuilder qb, List<Object> params) {
        String conjunction = "WHERE ";

        if (criteria.getStatuses() != null && criteria.getStatuses().size() > 0) {
            qb.append(conjunction).append("e.status IN('").append(criteria.getStatuses().stream().map(String::valueOf).collect(Collectors.joining("','"))).append("') ");
            conjunction = "AND ";
        }

        if (criteria.getBranches() != null && criteria.getBranches().size() > 0) {
            qb.append(conjunction).append("e.branch IN(").append(criteria.getBranches().stream().map(String::valueOf).collect(Collectors.joining(","))).append(") ");
            conjunction = "AND ";
        }

        if (criteria.getDepartments() != null && criteria.getDepartments().size() > 0) {
            qb.append(conjunction).append("e.department IN(").append(criteria.getDepartments().stream().map(String::valueOf).collect(Collectors.joining(","))).append(") ");
            conjunction = "AND ";
        }

        if (criteria.getDesignations() != null && criteria.getDesignations().size() > 0) {
            qb.append(conjunction).append("e.designation IN(").append(criteria.getDesignations().stream().map(String::valueOf).collect(Collectors.joining(","))).append(") ");
            conjunction = "AND ";
        } else if (criteria.getDesignation() != null && criteria.getDesignation() > 0) {
            qb.append(conjunction).append("e.designation = ? ");
            params.add(criteria.getDesignation());
            conjunction = "AND ";
        }

        if (!StringUtils.isEmpty(criteria.getReligion())) {
            qb.append(conjunction).append("e.religion = ? ");
            params.add(criteria.getReligion());
            conjunction = "AND ";
        }

        if (!StringUtils.isEmpty(criteria.getGender()) && !criteria.getGender().equals("All")) {
            qb.append(conjunction).append("e.gender = ? ");
            params.add(criteria.getGender());
            conjunction = "AND ";
        }

        if (!StringUtils.isEmpty(criteria.getDistrict())) {
            qb.append(conjunction).append("e.present_district = ? ");
            params.add(criteria.getDistrict());
            conjunction = "AND ";
        }

        if (!StringUtils.isEmpty(criteria.getArbitraryType()) && !StringUtils.isEmpty(criteria.getArbitrary())) {
            String[] arbitraryArray = criteria.getArbitrary().split(",");
            boolean first = true;
            StringBuilder arbitraryQuery = new StringBuilder(1000);

            if (criteria.getArbitraryType().equalsIgnoreCase("EID")) {
                for (String arrayEl : arbitraryArray) {
                    arrayEl = arrayEl.trim();
                    if (!StringUtils.isEmpty(arrayEl)) {
                        if (!first) {
                            arbitraryQuery.append(", ");
                        }
                        if (arrayEl.contains("-")) {
                            populateUniqueIds(arrayEl, arbitraryQuery, params, true);
                            first = false;
                        } else {
                            arbitraryQuery.append("?");

                            if(arrayEl.length() == 7) {
                                params.add(arrayEl.substring(2));
                            } else {
                                params.add(String.format("%05d", Integer.parseInt(arrayEl)));
                            }

                            first = false;
                        }
                    }
                }
                if (arbitraryQuery.length() > 0) {
                    qb.append(conjunction).append("e.employee_id IN(");
                    qb.append(arbitraryQuery.toString()).append(") ");
                }
            }

            if (criteria.getArbitraryType().equalsIgnoreCase("REG")) {
                for (String arrayEl : arbitraryArray) {
                    arrayEl = arrayEl.trim();
                    if (!StringUtils.isEmpty(arrayEl)) {
                        if (!first) {
                            arbitraryQuery.append(", ");
                        }
                        if (arrayEl.contains("[")) {
                            populateRegCodes(arrayEl, arbitraryQuery, params, true);
                            first = false;
                        } else {
                            arbitraryQuery.append("?");
                            params.add(arrayEl);
                            first = false;
                        }
                    }
                }
                if (arbitraryQuery.length() > 0) {
                    qb.append(conjunction).append("e.reg_code IN(");
                    qb.append(arbitraryQuery.toString()).append(") ");
                }
            }
            conjunction = "AND ";
        }

        if (!StringUtils.isEmpty(criteria.getDateType()) && criteria.getDateFrom() != null && criteria.getDateTo() != null) {
            if ("JOINING_DATE".equals(criteria.getDateType())) {
                qb.append(conjunction).append("e.hiring >= ? AND e.hiring <= ? ");

                params.add(criteria.getDateFrom());
                params.add(criteria.getDateTo());

            } else if ("REGULAR_DATE".equals(criteria.getDateType())) {
                qb.append(conjunction).append("e.regular_date >= ? AND e.regular_date <= ? ");

                params.add(criteria.getDateFrom());
                params.add(criteria.getDateTo());

            } else if ("PROMOTION_DATE".equals(criteria.getDateType())) {
                qb.append(conjunction).append("e.last_promotion_date >= ? AND e.last_promotion_date <= ? ");

                params.add(criteria.getDateFrom());
                params.add(criteria.getDateTo());

            } else if ("RESIGN_DATE".equals(criteria.getDateType())) {
                qb.append(conjunction).append("e.id IN(SELECT t.employee FROM hrm.type_info t WHERE t.status_to = ? AND t.date_from >= ? AND t.date_from < ?) ");

                params.add(EmployeeStatus.RESIGNED.getName());
                params.add(criteria.getDateFrom());
                params.add(criteria.getDateTo());
            }
            conjunction = "AND ";
        }

        if (!StringUtils.isEmpty(criteria.getMaritalStatus())) {
            qb.append(conjunction).append("e.marital_status = ? ");
            params.add(criteria.getMaritalStatus());
            conjunction = "AND ";
        }

        qb.append(conjunction).append("e.is_deleted = false ");
    }

    private void populateUniqueIds(String arrayEL, StringBuilder arbitraryQuery, List<Object> params, boolean first) {
        String[] uniqueIds = arrayEL.split("-");
        int begin = Integer.parseInt(uniqueIds[0]);
        int end = Integer.parseInt(uniqueIds[uniqueIds.length-1]);
        while (begin <= end) {
            String uniqueId = begin + "";
            if (!first) {
                arbitraryQuery.append(", ");
            }
            arbitraryQuery.append("? ");
            params.add(Integer.parseInt(uniqueId));
            first = false;
            begin++;
        }
    }

    private void populateRegCodes(String arrayEl, StringBuilder arbitraryQuery, List<Object> params, boolean first) {
        if (!arrayEl.matches("(M-|F-)*(\\[[0-9]+-[0-9]+\\]\\/)([1-9]{1}[0-9]+)")) return;
        String gender = arrayEl.substring(0, arrayEl.indexOf("["));
        String batch = arrayEl.substring(arrayEl.indexOf("/"));
        int begin = Integer.parseInt(arrayEl.substring(arrayEl.indexOf("[") + 1, arrayEl.lastIndexOf("-")));
        int end = Integer.parseInt(arrayEl.substring(arrayEl.lastIndexOf("-") + 1, arrayEl.indexOf("]")));
        while (begin <= end) {
            String regNo = gender + begin + batch;
            if (!first) {
                arbitraryQuery.append(", ");
            }
            arbitraryQuery.append("? ");
            params.add(regNo);
            first = false;
            begin++;
        }
    }


}
