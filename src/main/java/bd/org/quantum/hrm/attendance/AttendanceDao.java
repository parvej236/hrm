package bd.org.quantum.hrm.attendance;

import bd.org.quantum.authorizer.Authorizer;
import bd.org.quantum.authorizer.helper.UserContext;
import bd.org.quantum.authorizer.model.UserDetails;
import bd.org.quantum.hrm.common.Branch;
import bd.org.quantum.hrm.common.Department;
import bd.org.quantum.hrm.designation.Designation;
import bd.org.quantum.hrm.employee.EmployeeService;
import bd.org.quantum.hrm.idCard.IdCardCriteria;
import bd.org.quantum.hrm.leave.LeaveUpdateRequestDto;
import bd.org.quantum.hrm.leave.LeaveUpdateRequestSearch;
import bd.org.quantum.hrm.movement.MovementSearchCriteria;
import bd.org.quantum.hrm.employee.Employee;
import bd.org.quantum.hrm.leave.LeaveApplication;
import bd.org.quantum.hrm.role.Permissions;
import bd.org.quantum.hrm.yr_cl.YrCl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class AttendanceDao {
    private final JdbcTemplate jdbcTemplate;
    private final EmployeeService employeeService;
    private final Authorizer authorizer;

    public AttendanceDao(JdbcTemplate jdbcTemplate, EmployeeService employeeService, Authorizer authorizer) {
        this.jdbcTemplate = jdbcTemplate;
        this.employeeService = employeeService;
        this.authorizer = authorizer;
    }

    public List<LeaveApplication> pendingConfirmLeaveList(List<String> dateList) {
        UserDetails user = UserContext.getPrincipal().getUserDetails();
        StringBuilder qb = new StringBuilder();

        qb.append("SELECT l.id, l.applicant, l.leave_from, l.leave_to, l.leave_days, e.name_en, e.designation_name, e.branch_name, e.department_name ");
        qb.append("FROM hrm.leave_application l JOIN hrm.employee e on l.applicant = e.id ");
        qb.append("WHERE l.stage = 'Approved' AND e.status IN('Regular', 'Irregular', 'Part-Time', 'Honorary', 'Intern') AND e.branch = ").append(user.getBranchId()).append(" ");

        if (user.getDepartmentId() != null && user.getDepartmentId() > 0) {
            qb.append("AND e.department = ").append(user.getDepartmentId()).append(" ");
        }

        qb.append("AND e.hiring <= '").append(new java.sql.Date((new Date()).getTime())).append("' ");

        qb.append("AND (l.leave_from IN ('").append(dateList.stream().map(String::valueOf).collect(Collectors.joining("','"))).append("') ");
        qb.append("OR l.leave_to IN ('").append(dateList.stream().map(String::valueOf).collect(Collectors.joining("','"))).append("')) ");

        List<LeaveApplication> list = jdbcTemplate.query(qb.toString(),(RowMapper) (rs, i) -> {
            LeaveApplication application = new LeaveApplication();

            Employee employee = new Employee();
            employee.setId(rs.getLong("applicant"));
            employee.setNameEn(rs.getString("name_en"));
            employee.setDesignationName(rs.getString("designation_name"));
            employee.setBranchName(rs.getString("branch_name"));
            employee.setDepartmentName(rs.getString("department_name"));

            application.setId(rs.getLong("id"));
            application.setApplicant(employee);
            application.setLeaveFrom(rs.getDate("leave_from").toLocalDate());
            application.setLeaveTo(rs.getDate("leave_to").toLocalDate());
            application.setLeaveDays(rs.getInt("leave_days"));

            return application;
        });

        return list;
    }

    public List<Employee> getEmployeeList(AttendanceCriteria cmd) {

        List<Object> params = new ArrayList<>();
        StringBuilder qb = new StringBuilder();
        UserDetails user = UserContext.getPrincipal().getUserDetails();

        qb.append("SELECT e.id, e.name_en, e.regular_date, e.hiring, e.status, e.pre_consumed_yr, e.wpd ");
        qb.append("FROM hrm.employee e ");

        if (StringUtils.isEmpty(cmd.getStatus())) {
            qb.append("WHERE e.status IN('Regular', 'Irregular', 'Muster Roll') ");
        } else if (!StringUtils.isEmpty(cmd.getStatus()) && !cmd.getStatus().equals("99")) {
            qb.append("WHERE e.status = ? ");
            params.add(cmd.getStatus());
        }

        if (!StringUtils.isEmpty(cmd.getEmployeeName())) {
            qb.append("AND LOWER(e.name_en) LIKE '%").append(cmd.getEmployeeName().trim().toLowerCase()).append("%' ") ;
        }

        if (!StringUtils.isEmpty(cmd.getRegCode())) {
            qb.append("AND LOWER(e.reg_code) LIKE '%").append(cmd.getRegCode().trim().toLowerCase()).append("%' ") ;
        }

        if (!StringUtils.isEmpty(cmd.getEmployeeId())) {
            cmd.setEmployeeId(!cmd.getEmployeeId().contains("/") && cmd.getEmployeeId().length() == 7 ? cmd.getEmployeeId().substring(2) : cmd.getEmployeeId());
            qb.append("AND (LOWER(e.employee_id) LIKE '%").append(cmd.getEmployeeId().trim().toLowerCase()).append("%' ");
            qb.append("OR LOWER(e.old_employee_id) LIKE '%").append(cmd.getEmployeeId().trim().toLowerCase()).append("%') ");
        }

        if (!StringUtils.isEmpty(cmd.getPrimaryPhone())) {
            qb.append("AND LOWER(e.primary_phone) LIKE '%").append(cmd.getPrimaryPhone().trim().toLowerCase()).append("%' ");
        }

        if (cmd.getDepartment() > 0) {
            if (cmd.isIncludeDeptSub() && authorizer.hasPermission(Permissions.EMPLOYEE_DEPARTMENT_INTER_ACCESS.name())) {
                List<Department> departments = employeeService.getDepartmentsWithSubsidiary(cmd.getDepartment());
                qb.append("AND e.department IN(").append(departments.stream().map(Department::getId).map(String::valueOf).collect(Collectors.joining(",")));
                qb.append(") ");
            } else {
                qb.append("AND e.department = ? ");
                params.add(cmd.getDepartment());
            }
        } else if (cmd.getBranch() > 0) {
            if (cmd.isWithSub() && authorizer.hasPermission(Permissions.EMPLOYEE_BRANCH_INTER_ACCESS.name())) {
                List<Branch> branches = employeeService.getSubsidiaryBranches(cmd.getBranch());
                qb.append("AND e.branch IN(").append(branches.stream().map(Branch::getId).map(String::valueOf).collect(Collectors.joining(",")));
                qb.append(") ");
            } else {
                qb.append("AND e.branch = ? ");
                params.add(cmd.getBranch());
            }
        } else if (user.getDepartmentId() != null && user.getDepartmentId() > 0) {
            qb.append("AND e.department = ? ");
            params.add(user.getDepartmentId());
        } else {
            qb.append("AND e.branch = ? ");
            params.add(user.getBranchId());
        }

        if (cmd.getDesignation() > 0) {
            qb.append("AND e.designation = ? ");
            params.add(cmd.getDesignation());
        }

        if (cmd.getGender() != null && !cmd.getGender().equals("all")) {
            qb.append("AND e.gender = ? ");
            params.add(cmd.getGender());
        }

        qb.append("AND e.hiring <= ? AND e.is_deleted = FALSE ORDER BY e.designation, e.hiring");
        params.add(cmd.getEndDate());

        List<Employee> list = jdbcTemplate.query(qb.toString(), params.toArray(), (RowMapper) (rs, i) -> {
            Employee employee = new Employee();
            employee.setId(rs.getLong("id"));
            employee.setNameEn(rs.getString("name_en"));
            employee.setRegularDate(rs.getDate("regular_date") != null ? rs.getDate("regular_date").toLocalDate() : null);
            employee.setHiring(rs.getDate("hiring") != null ? rs.getDate("hiring").toLocalDate() : null);
            employee.setStatus(rs.getString("status"));
            employee.setPreConsumedYr(rs.getInt("pre_consumed_yr"));
            employee.setWpd(rs.getString("wpd"));

            return employee;
        });

        return list;
    }

    public List<Employee> getEmployeeListForIdCard(IdCardCriteria criteria) {
        List<Object> params = new ArrayList<>();
        StringBuilder qb = new StringBuilder();
        UserDetails user = UserContext.getPrincipal().getUserDetails();

        qb.append("SELECT e.id, e.name_en, e.regular_date, e.hiring, e.status, e.pre_consumed_yr, e.wpd, ");
        qb.append("e.branch, e.branch_name, e.department, e.department_name, e.designation, e.designation_name, e.blood_group, ");
        qb.append("CASE WHEN e.status IN ('Muster Roll', 'Contractual', 'Part-Time', 'Probationary', 'Intern') THEN e.temp_employee_id ELSE CONCAT(TO_CHAR(e.hiring, 'YY'), e.employee_id) END AS employee_id, e.image_path ");
        qb.append("FROM hrm.employee e ");
        qb.append(criteria.getOrderType().equals("reOrder") ? "JOIN ":"LEFT JOIN ");
        qb.append("hrm.id_card_records r ON (e.id = r.employee AND r.year = ").append(criteria.getYear()).append(") ");
        qb.append("JOIN hrm.designation d ON e.designation = d.id ");

        if (!StringUtils.isEmpty(criteria.getStatus())) {
            qb.append("WHERE e.status = ? ");
            params.add(criteria.getStatus());
        } else {
            qb.append("WHERE e.status IN('Regular', 'Irregular', 'Muster Roll') ");
        }

        if (criteria.getOrderType().equals("order")) {
            qb.append("AND r.employee IS NULL ");
        }

        if (!StringUtils.isEmpty(criteria.getEmployeeName())) {
            qb.append("AND LOWER(e.name_en) LIKE '%").append(criteria.getEmployeeName().trim().toLowerCase()).append("%' ") ;
        }

        if (!StringUtils.isEmpty(criteria.getRegCode())) {
            qb.append("AND LOWER(e.reg_code) LIKE '%").append(criteria.getRegCode().trim().toLowerCase()).append("%' ") ;
        }

        if (!StringUtils.isEmpty(criteria.getEmployeeId())) {
            criteria.setEmployeeId(criteria.getEmployeeId().length() == 7 ? criteria.getEmployeeId().substring(2) : criteria.getEmployeeId());
            qb.append("AND LOWER(e.employee_id) LIKE '%").append(criteria.getEmployeeId().trim().toLowerCase()).append("%' ");
        }

        if (!StringUtils.isEmpty(criteria.getPrimaryPhone())) {
            qb.append("AND LOWER(e.primary_phone) LIKE '%").append(criteria.getPrimaryPhone().trim().toLowerCase()).append("%' ");
        }

        if (StringUtils.isEmpty(criteria.getEmployeeName()) && StringUtils.isEmpty(criteria.getRegCode()) &&
                StringUtils.isEmpty(criteria.getEmployeeId()) && StringUtils.isEmpty(criteria.getPrimaryPhone())) {
            if (criteria.getDepartment() > 0) {
                if (criteria.isIncludeDeptSub() && authorizer.hasPermission(Permissions.EMPLOYEE_DEPARTMENT_INTER_ACCESS.name())) {
                    List<Department> departments = employeeService.getDepartmentsWithSubsidiary(criteria.getDepartment());
                    qb.append("AND e.department IN(").append(departments.stream().map(Department::getId).map(String::valueOf).collect(Collectors.joining(",")));
                    qb.append(") ");
                } else {
                    qb.append("AND e.department = ? ");
                    params.add(criteria.getDepartment());
                }
            } else if (criteria.getBranch() > 0) {
                if (criteria.isWithSub() && authorizer.hasPermission(Permissions.EMPLOYEE_BRANCH_INTER_ACCESS.name())) {
                    List<Branch> branches = employeeService.getSubsidiaryBranches(criteria.getBranch());
                    qb.append("AND e.branch IN(").append(branches.stream().map(Branch::getId).map(String::valueOf).collect(Collectors.joining(",")));
                    qb.append(") ");
                } else {
                    qb.append("AND e.branch = ? ");
                    params.add(criteria.getBranch());
                }
            } else if (user.getDepartmentId() != null && user.getDepartmentId() > 0) {
                qb.append("AND e.department = ? ");
                params.add(user.getDepartmentId());
            } else {
                qb.append("AND e.branch = ? ");
                params.add(user.getBranchId());
            }
        }

        if (criteria.getDesignation() > 0) {
            qb.append("AND e.designation = ? ");
            params.add(criteria.getDesignation());
        }

        if (criteria.getGender() != null && !criteria.getGender().equals("all")) {
            qb.append("AND e.gender = ? ");
            params.add(criteria.getGender());
        }

        qb.append("AND e.hiring <= CURRENT_DATE AND e.is_deleted = FALSE ORDER BY d.sorting_order, e.hiring");

        List<Employee> list = jdbcTemplate.query(qb.toString(), params.toArray(), (RowMapper) (rs, i) -> {
            Employee employee = new Employee();
            employee.setId(rs.getLong("id"));
            employee.setNameEn(rs.getString("name_en"));
            employee.setRegularDate(rs.getDate("regular_date") != null ? rs.getDate("regular_date").toLocalDate() : null);
            employee.setHiring(rs.getDate("hiring") != null ? rs.getDate("hiring").toLocalDate() : null);
            employee.setStatus(rs.getString("status"));
            employee.setPreConsumedYr(rs.getInt("pre_consumed_yr"));
            employee.setWpd(rs.getString("wpd"));
            employee.setBranch(rs.getLong("branch"));
            employee.setBranchName(rs.getString("branch_name"));
            employee.setDepartment(rs.getLong("department"));
            employee.setDepartmentName(rs.getString("department_name"));

            Designation designation = new Designation();
            designation.setId(rs.getLong("designation"));
            designation.setName(rs.getString("designation_name"));

            employee.setDesignation(designation);
            employee.setBloodGroup(rs.getString("blood_group"));
            employee.setEmployeeId(rs.getString("employee_id"));
            employee.setImagePath(rs.getString("image_path"));

            return employee;
        });

        return list;
    }

    public List<YrCl> getAllYearWiseYr(boolean isDesc) {
        StringBuilder qb = new StringBuilder();
        String desc = isDesc ? "DESC" : "";

        qb.append("SELECT id, year, yr_day, cl_day, start_date, end_date, comments ");
        qb.append("FROM hrm.year_wise_yr ORDER BY id ").append(desc);

        List<YrCl> list = jdbcTemplate.query(qb.toString(), (RowMapper) (rs, i) -> {
            YrCl yrCl = new YrCl();
            yrCl.setId(rs.getLong("id"));
            yrCl.setYear(rs.getInt("year"));
            yrCl.setYr(rs.getInt("yr_day"));
            yrCl.setCl(rs.getInt("cl_day"));
            yrCl.setDateFrom(rs.getDate("start_date") != null ? rs.getDate("start_date").toLocalDate() : null);
            yrCl.setDateTo(rs.getDate("end_date") != null ? rs.getDate("end_date").toLocalDate() : null);
            yrCl.setRemarks(rs.getString("comments"));

            return yrCl;
        });

        return list;
    }

    public List<MovementSearchCriteria> getMovementList(MovementSearchCriteria criteria) {
        List<Object> params = new ArrayList<>();
        StringBuilder qb = new StringBuilder();
        String conjunction = "WHERE ";

        qb.append("SELECT a.id, e.employee_id, e.name_en, hd.name designation, e.department_name, e.branch_name, ");
        qb.append("TO_CHAR(a.date_from, 'DD/MM/YYYY') || ' - ' || TO_CHAR(a.date_to, 'DD/MM/YYYY') date_range ");
        qb.append("FROM hrm.movement_attendance a ");
        qb.append("JOIN hrm.employee e ON a.employee = e.id ");
        qb.append("JOIN hrm.designation hd ON e.designation = hd.id ");

        if (criteria.getEmployee() != null && criteria.getEmployee() > 0) {
            qb.append(conjunction).append("a.employee = ? ");
            params.add(criteria.getEmployee());
            conjunction = "AND ";
        }

        if (criteria.getBranches() != null && criteria.getBranches().size() > 0) {
            qb.append(conjunction).append("e.branch IN(");
            qb.append(criteria.getBranches().stream().map(String::valueOf).collect(Collectors.joining(","))).append(") ");
            conjunction = "AND ";
        }

        if (criteria.getDepartments() != null && criteria.getDepartments().size() > 0) {
            qb.append(conjunction).append("e.department IN(");
            qb.append(criteria.getBranches().stream().map(String::valueOf).collect(Collectors.joining(","))).append(") ");
            conjunction = "AND ";
        }

        if (criteria.getDateFrom() != null && criteria.getDateTo() != null) {
            qb.append(conjunction).append("a.date_from >= ? AND a.date_to <= ? ");
            params.add(criteria.getDateFrom());
            params.add(criteria.getDateTo());
            conjunction = "AND ";
        }

        if (!StringUtils.isEmpty(criteria.getStage())) {
            qb.append(conjunction).append("a.stage = ? ");
            params.add(criteria.getStage());
        }

        qb.append("ORDER BY a.created_at DESC");

        List<MovementSearchCriteria> list = jdbcTemplate.query(qb.toString(), params.toArray(), (RowMapper) (rs, i) -> {
            MovementSearchCriteria search = new MovementSearchCriteria();
            search.setMovementId(rs.getLong("id"));
            search.setEmployeeId(rs.getString("employee_id"));
            search.setName(rs.getString("name_en"));
            search.setDesignationName(rs.getString("designation"));
            search.setDepartmentName(rs.getString("department_name"));
            search.setBranchName(rs.getString("branch_name"));
            search.setDateRange(rs.getString("date_range"));

            return search;
        });

        return list;
    }

    public List<AttendanceSummaryDto> getAttendanceSummaryList(YrCl yrCl, long employeeId) {
        StringBuilder query = new StringBuilder();
        List<Object> params = new ArrayList<>();

        query.append("WITH months AS (");
        query.append("SELECT TO_CHAR(generate_series(DATE '").append(yrCl.getDateFrom()).append("', DATE '").append(yrCl.getDateTo()).append("', ");
        query.append("'1 month'::interval)::DATE, 'Month YYYY') month_name), ");
        query.append("attendance_summary AS (");
        query.append("SELECT TO_CHAR(attendance_date, 'YYYY-MM') AS month_val, ");
        query.append("TO_CHAR(attendance_date, 'Month YYYY') AS month_name, ");
        query.append("SUM(CASE WHEN attendance_type = ").append(AttendanceType.PRESENT.getValue()).append(" THEN 1 ELSE 0 END) AS present, ");
        query.append("SUM(CASE WHEN attendance_type = ").append(AttendanceType.ABSENT_LEAVE.getValue()).append(" THEN 1 ELSE 0 END) AS absent, ");
        query.append("SUM(CASE WHEN attendance_type IN (").append(AttendanceType.OD_OTP_TRAINING_STR).append(") THEN 1 ELSE 0 END) AS od_otp_t, ");
        query.append("SUM(CASE WHEN attendance_type = ").append(AttendanceType.WEEKLY_PREPARATION_DAY.getValue()).append(" THEN 1 ELSE 0 END) AS wpd, ");
        query.append("SUM(CASE WHEN attendance_type = ").append(AttendanceType.CASUAL_LEAVE.getValue()).append(" THEN 1 ELSE 0 END) AS cl, ");
        query.append("SUM(CASE WHEN attendance_type = ").append(AttendanceType.YEARLY_REJUVENATION.getValue()).append(" THEN 1 ELSE 0 END) AS yr, ");
        query.append("SUM(CASE WHEN attendance_type = ").append(AttendanceType.WITHOUT_PAY_LEAVE.getValue()).append(" THEN 1 ELSE 0 END) AS lwp, ");
        query.append("SUM(CASE WHEN attendance_type IN (").append(AttendanceType.OTHER_LEAVE_STR).append(") THEN 1 ELSE 0 END) AS other ");
        query.append("FROM hrm.employee_attendance ");
        query.append("WHERE employee = ? AND attendance_date >= ? AND attendance_date <= ?  ");
        query.append("GROUP BY TO_CHAR(attendance_date, 'YYYY-MM'), TO_CHAR(attendance_date, 'Month YYYY')) ");
        query.append("SELECT m.month_name, COALESCE(a.present, 0) AS present, COALESCE(a.absent, 0) AS absent, ");
        query.append("COALESCE(a.od_otp_t, 0) AS od_otp_t, COALESCE(a.wpd, 0) AS wpd, COALESCE(a.cl, 0) AS cl, ");
        query.append("COALESCE(a.yr, 0) AS yr, COALESCE(a.lwp, 0) AS lwp, COALESCE(a.other, 0) AS other ");
        query.append("FROM months m LEFT JOIN attendance_summary a ON m.month_name = a.month_name ORDER BY a.month_val");

        params.add(employeeId);
        params.add(yrCl.getDateFrom());
        params.add(yrCl.getDateTo());

        return jdbcTemplate.query(query.toString(), params.toArray(), (RowMapper) (rs, i) -> {
            AttendanceSummaryDto dto = new AttendanceSummaryDto();
            dto.setMonthName(rs.getString("month_name"));
            dto.setPresent(rs.getInt("present"));
            dto.setAbsent(rs.getInt("absent"));
            dto.setOdOtpTraining(rs.getInt("od_otp_t"));
            dto.setWpd(rs.getInt("wpd"));
            dto.setCl(rs.getInt("cl"));
            dto.setYr(rs.getInt("yr"));
            dto.setLwp(rs.getInt("lwp"));
            dto.setOtherLeave(rs.getInt("other"));

            return dto;
        });
    }

    public List<AttendanceSummaryDto> getAttendanceList(long empId, LocalDate dateFrom, LocalDate dateTo, String types) {
        StringBuilder query = new StringBuilder();
        List<Object> params = new ArrayList<>();
        query.append("SELECT e.name_en, a.attendance_date, a.attendance_type ");
        query.append("FROM hrm.employee_attendance a JOIN hrm.employee e ON a.employee = e.id ");
        query.append("WHERE a.employee = ? ");

        params.add(empId);

        if (dateFrom != null && dateTo != null) {
            query.append("AND a.attendance_date >= ? AND a.attendance_date <= ? ");
            params.add(dateFrom);
            params.add(dateTo);
        }

        if (!StringUtils.isEmpty(types)) {
            if (!types.contains(",")) {
                query.append("AND a.attendance_type = ? ");
                params.add(Integer.valueOf(types));
            } else {
                query.append("AND a.attendance_type IN(").append(types).append(") ");
            }
        }

        query.append("ORDER BY a.attendance_date");

        return jdbcTemplate.query(query.toString(), params.toArray(), (RowMapper) (rs, i) -> {
            AttendanceSummaryDto dto = new AttendanceSummaryDto();
            dto.setName(rs.getString("name_en"));
            dto.setDate(rs.getDate("attendance_date").toLocalDate());
            dto.setType(AttendanceType.getAllAttendanceTypes().get(rs.getInt("attendance_type")));
            return dto;
        });
    }

    public List<LeaveUpdateRequestDto> getLeaveUpdateRequestList(LeaveUpdateRequestSearch search) {
        StringBuilder qb = new StringBuilder();

        qb.append("WITH old_leave_range AS ( ");
        qb.append("SELECT r.id, MIN(o.date_from) AS date_from, MAX(o.date_to) AS date_to ");
        qb.append("FROM hrm.leave_details_old o ");
        qb.append("JOIN hrm.leave_update_request r ON o.request = r.id GROUP BY r.id ");
        qb.append("), update_leave_range AS ( ");
        qb.append("SELECT r.id, e.name_en, e.designation_name, e.branch_name, e.department_name, r.date_from, r.date_to ");
        qb.append("FROM hrm.leave_update_request r ");
        qb.append("JOIN hrm.leave_application a ON r.application_id = a.id ");
        qb.append("JOIN hrm.employee e ON a.applicant = e.id ");

        if (!StringUtils.isEmpty(search.getStage())) {
            qb.append("WHERE r.stage = '").append(search.getStage()).append("' ");
        } else {
            qb.append("WHERE r.stage = 'Requested' ");
        }

        if (!StringUtils.isEmpty(search.getOmniText())) {
            qb.append("AND (LOWER(e.name_en) LIKE '%").append(search.getOmniText().trim().toLowerCase()).append("%' ") ;
            qb.append("OR LOWER(e.reg_code) LIKE '%").append(search.getOmniText().trim().toLowerCase()).append("%' ") ;
            qb.append("OR e.employee_id LIKE '%").append(search.getOmniText().length() == 7 ? search.getOmniText().substring(2) : search.getOmniText().trim()).append("%' ") ;
            qb.append("OR e.primary_phone LIKE '%").append(search.getOmniText().trim().toLowerCase()).append("%') ") ;
        } else {
            if (search.getBranches() != null && search.getBranches().size() > 0) {
                qb.append("AND e.branch IN(").append(search.getBranches().stream().map(String::valueOf).collect(Collectors.joining(","))).append(") ");
            }

            if (search.getDepartments() != null && search.getDepartments().size() > 0) {
                qb.append("AND e.department IN(").append(search.getDepartments().stream().map(String::valueOf).collect(Collectors.joining(","))).append(") ");
            }

            if (search.getUserId() != null && search.getUserId() > 0) {
                qb.append("AND r.requested_by = ").append(search.getUserId());
            }
        }

        qb.append(") SELECT ulr.id, ulr.name_en, ulr.designation_name, ulr.branch_name, ulr.department_name, ");
        qb.append("olr.date_from AS odf, olr.date_to AS odt, ulr.date_from, ulr.date_to ");
        qb.append("FROM update_leave_range ulr JOIN old_leave_range olr ON ulr.id = olr.id ");
        qb.append("ORDER BY ulr.id DESC");

        List<LeaveUpdateRequestDto> list = jdbcTemplate.query(qb.toString(), (RowMapper) (rs, i) -> {
            LeaveUpdateRequestDto requestDto = new LeaveUpdateRequestDto();
            requestDto.setId(rs.getLong("id"));
            requestDto.setName(rs.getString("name_en"));
            requestDto.setDesignation(rs.getString("designation_name"));
            requestDto.setBranch(rs.getString("branch_name"));
            requestDto.setDepartment(rs.getString("department_name"));
            requestDto.setOldDateFrom(rs.getDate("odf") != null ? rs.getDate("odf").toLocalDate() : null);
            requestDto.setOldDateTo(rs.getDate("odt") != null ? rs.getDate("odt").toLocalDate() : null);
            requestDto.setDateFrom(rs.getDate("date_from") != null ? rs.getDate("date_from").toLocalDate() : null);
            requestDto.setDateTo(rs.getDate("date_to") != null ? rs.getDate("date_to").toLocalDate() : null);

            return requestDto;
        });

        return list;
    }

    public int getConsumedLeave(long empId, int type, LocalDate startDate, LocalDate endDate) {
        List<Object> params = new ArrayList<>();

        String query = "SELECT COUNT(attendance_type) holidays FROM hrm.employee_attendance WHERE employee = ? " +
                "AND attendance_type = ? ";

        params.add(empId);
        params.add(type);

        if (startDate != null && endDate != null) {
            query += "AND attendance_date >= ? AND attendance_date <= ? ";
            params.add(startDate);
            params.add(endDate);
        }

        return jdbcTemplate.queryForObject(query, params.toArray(), Integer.class);
    }

    public Integer getTotalYrEncashDays(long emId) {
        String sql = "SELECT SUM(e.days) FROM hrm.yr_encash e WHERE e.employee = " + emId;
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class);
        return result != null ? result : 0;
    }

    public DailyAttendanceCmd getAttendanceSummary(DailyAttendanceCmd criteria) {
        StringBuilder query = new StringBuilder();
        List<Object> params = new ArrayList<>();
        UserDetails user = UserContext.getPrincipal().getUserDetails();

        query.append("WITH employee_data AS ( ");
        query.append("SELECT * FROM hrm.employee e ");
        query.append("WHERE e.status IN('Regular', 'Irregular', 'Muster Roll') ");

        if (criteria.getDepartment() > 0) {
            if (criteria.isIncludeDeptSub() && authorizer.hasPermission(Permissions.EMPLOYEE_DEPARTMENT_INTER_ACCESS.name())) {
                List<Department> departments = employeeService.getDepartmentsWithSubsidiary(criteria.getDepartment());
                query.append("AND e.department IN(").append(departments.stream().map(Department::getId).map(String::valueOf).collect(Collectors.joining(",")));
                query.append(") ");

            } else {
                query.append("AND e.department = ? ");
                params.add(criteria.getDepartment());
            }

        } else if (criteria.getBranch() > 0) {
            if (user.getDepartmentId() != null && user.getDepartmentId() > 0) {
                if (authorizer.hasPermission(Permissions.EMPLOYEE_DEPARTMENT_INTER_ACCESS.name())) {
                    List<Department> departments = employeeService.getDepartmentsWithSubsidiary(user.getDepartmentId());
                    query.append("AND e.department IN(").append(departments.stream().map(Department::getId).map(String::valueOf).collect(Collectors.joining(",")));
                    query.append(") ");

                } else {
                    query.append("AND e.department = ? ");
                    params.add(user.getDepartmentId());
                }

            } else {
                query.append("AND e.branch = ? ");
                params.add(criteria.getBranch());
            }

        } else if (user.getDepartmentId() != null && user.getDepartmentId() > 0) {
            query.append("AND e.department = ? ");
            params.add(user.getDepartmentId());

        } else {
            query.append("AND e.branch = ? ");
            params.add(user.getBranchId());
        }

        query.append("), attendance_data AS ( ");
        query.append("SELECT SUM(CASE WHEN TO_CHAR(punched_at, 'HH12:MI AM')::time <= '09:00 AM'::time THEN 1 ELSE 0 END) AS timely_count, ");
        query.append("SUM(CASE WHEN TO_CHAR(punched_at, 'HH12:MI AM')::time <= '09:00 AM'::time THEN 0 ELSE 1 END) AS late_count, ");
        query.append("1 AS row_number ");
        query.append("FROM (SELECT MIN(al.punched_at) AS punched_at ");
        query.append("FROM hrm.bio_attendance_log al ");
        query.append("JOIN employee_data e ON al.employee = e.id ");
        query.append("WHERE al.punched_at >= ? ");
        query.append("GROUP BY al.employee) AS result ");

        params.add(criteria.getAttendanceDate());

        query.append("), movement_data AS ( ");
        query.append("SELECT COUNT(ma.*) AS movement_count, 1 AS row_number ");
        query.append("FROM hrm.movement_attendance ma JOIN employee_data ed ON ma.employee = ed.id ");
        query.append("WHERE ? BETWEEN ma.date_from AND ma.date_to AND ma.stage IN('AUTHORIZE') ");

        params.add(criteria.getAttendanceDate());

        query.append("), leave_data AS ( ");
        query.append("SELECT COUNT(la.*) AS leave_count, 1 AS row_number ");
        query.append("FROM hrm.leave_application la JOIN employee_data ed ON la.applicant = ed.id ");
        query.append("WHERE ? BETWEEN la.leave_from AND la.leave_to AND la.stage IN('Authorized', 'Approved', 'Confirmed') ");

        params.add(criteria.getAttendanceDate());

        query.append("), total_data AS ( ");
        query.append("SELECT ad.timely_count, ad.late_count, md.movement_count, ld.leave_count, ");
        query.append("(COALESCE(ad.timely_count,0) + COALESCE(ad.late_count,0) + COALESCE(md.movement_count,0) + COALESCE(ld.leave_count,0)) AS total_count, 1 AS row_number ");
        query.append("FROM attendance_data ad ");
        query.append("JOIN movement_data md ON ad.row_number = md.row_number ");
        query.append("JOIN leave_data ld ON ad.row_number = ld.row_number ");
        query.append("), employee_count AS ( ");
        query.append("SELECT COUNT(*) AS emp_count, 1 AS row_number FROM employee_data ");
        query.append("), absent_data AS ( ");
        query.append("SELECT (COALESCE(ec.emp_count,0) - COALESCE(td.total_count,0)) AS absent_count, 1 AS row_number ");
        query.append("FROM total_data td JOIN employee_count ec ON td.row_number = ec.row_number ");
        query.append(") SELECT td.timely_count, td.late_count, td.movement_count, td.leave_count, ad.absent_count ");
        query.append("FROM total_data td JOIN absent_data ad ON td.row_number = ad.row_number ");

        return (DailyAttendanceCmd) jdbcTemplate.queryForObject(query.toString(), params.toArray(), (RowMapper) (rs, i) -> {
            DailyAttendanceCmd program = new DailyAttendanceCmd();
            program.setTimely(rs.getInt("timely_count"));
            program.setDelay(rs.getInt("late_count"));
            program.setMovement(rs.getInt("movement_count"));
            program.setLeave(rs.getInt("leave_count"));
            program.setAbsent(rs.getInt("absent_count"));

            return program;
        });
    }

    public List<DailyAttendanceCmd> getDailyAttendanceList(DailyAttendanceCmd criteria) {
        StringBuilder query = new StringBuilder();
        List<Object> params = new ArrayList<>();
        UserDetails user = UserContext.getPrincipal().getUserDetails();

        query.append("WITH employee_data AS ( ");
        query.append("SELECT * FROM hrm.employee e ");
        query.append("WHERE e.status IN('Regular', 'Irregular', 'Muster Roll') ");

        if (criteria.getDepartment() > 0) {
            if (criteria.isIncludeDeptSub() && authorizer.hasPermission(Permissions.EMPLOYEE_DEPARTMENT_INTER_ACCESS.name())) {
                List<Department> departments = employeeService.getDepartmentsWithSubsidiary(criteria.getDepartment());
                query.append("AND e.department IN(").append(departments.stream().map(Department::getId).map(String::valueOf).collect(Collectors.joining(",")));
                query.append(") ");

            } else {
                query.append("AND e.department = ? ");
                params.add(criteria.getDepartment());
            }

        } else if (criteria.getBranch() > 0) {
            if (user.getDepartmentId() != null && user.getDepartmentId() > 0) {
                if (authorizer.hasPermission(Permissions.EMPLOYEE_DEPARTMENT_INTER_ACCESS.name())) {
                    List<Department> departments = employeeService.getDepartmentsWithSubsidiary(user.getDepartmentId());
                    query.append("AND e.department IN(").append(departments.stream().map(Department::getId).map(String::valueOf).collect(Collectors.joining(",")));
                    query.append(") ");

                } else {
                    query.append("AND e.department = ? ");
                    params.add(user.getDepartmentId());
                }

            } else {
                query.append("AND e.branch = ? ");
                params.add(criteria.getBranch());
            }

        } else if (user.getDepartmentId() != null && user.getDepartmentId() > 0) {
            query.append("AND e.department = ? ");
            params.add(user.getDepartmentId());

        } else {
            query.append("AND e.branch = ? ");
            params.add(user.getBranchId());
        }

        query.append("), attendance_data AS ( ");
        query.append("SELECT employee, TO_CHAR(punched_at, 'HH12:MI AM') AS in_time, ");
        query.append("CASE WHEN out_time IS NOT NULL THEN TO_CHAR(out_time, 'HH12:MI AM') ELSE NULL END AS out_time, ");
        query.append("CASE WHEN TO_CHAR(punched_at, 'HH12:MI AM')::time <= '09:00 AM'::time THEN 1 ELSE 2 END AS sort_order, ");
        query.append("CASE WHEN TO_CHAR(punched_at, 'HH12:MI AM')::time <= '09:00 AM'::time THEN 'Timely' ELSE 'Delay' END AS order_name ");
        query.append("FROM (SELECT al.employee, MIN(al.punched_at) AS punched_at, CASE WHEN COUNT(*) > 1 THEN MAX(al.punched_at) ELSE NULL END AS out_time ");
        query.append("FROM hrm.bio_attendance_log al JOIN employee_data e ON al.employee = e.id ");
        query.append("WHERE al.punched_at >= ? GROUP BY al.employee) AS result ");

        params.add(criteria.getAttendanceDate());

        query.append("), movement_data AS ( ");
        query.append("SELECT ma.employee, TO_CHAR(ma.date_from, 'DD/MM/YYYY') AS in_time, TO_CHAR(ma.date_to, 'DD/MM/YYYY') AS out_time, ");
        query.append("3 AS sort_order, 'Movement' AS order_name ");
        query.append("FROM hrm.movement_attendance ma ");
        query.append("WHERE ? BETWEEN ma.date_from AND ma.date_to AND ma.stage IN('AUTHORIZE') ");

        params.add(criteria.getAttendanceDate());

        query.append("), leave_data AS ( ");
        query.append("SELECT la.applicant, TO_CHAR(la.leave_from, 'DD/MM/YYYY') AS in_time, TO_CHAR(la.leave_to, 'DD/MM/YYYY') AS out_time, ");
        query.append("4 AS sort_order, 'Leave' AS order_name ");
        query.append("FROM hrm.leave_application la ");
        query.append("WHERE ? BETWEEN la.leave_from AND la.leave_to AND la.stage IN('Authorized', 'Approved', 'Confirmed') ");

        params.add(criteria.getAttendanceDate());

        query.append("), details_data AS ( ");
        query.append("SELECT ed.id, ed.name_en, at.in_time::VARCHAR, at.out_time::VARCHAR, at.sort_order, at.order_name ");
        query.append("FROM attendance_data at JOIN employee_data ed ON at.employee = ed.id ");
        query.append("UNION ALL ");
        query.append("SELECT ed.id, ed.name_en, at.in_time, at.out_time, at.sort_order, at.order_name ");
        query.append("FROM movement_data at JOIN employee_data ed ON at.employee = ed.id ");
        query.append("UNION ALL ");
        query.append("SELECT ed.id, ed.name_en, at.in_time, at.out_time, at.sort_order, at.order_name ");
        query.append("FROM leave_data at JOIN employee_data ed ON at.applicant = ed.id ");
        query.append("), absent_data AS ( ");
        query.append("SELECT id, name_en, '' AS in_time, '' AS out_time, 5 AS sort_order, 'Absent' AS order_name ");
        query.append("FROM employee_data WHERE id NOT IN(SELECT DISTINCT id FROM details_data) ");
        query.append("), process_data AS ( ");
        query.append("SELECT * FROM details_data ");
        query.append("UNION ALL ");
        query.append("SELECT * FROM absent_data ");
        query.append(") SELECT * FROM (SELECT id, name_en, in_time::VARCHAR, out_time, sort_order, order_name FROM process_data ");
        query.append("UNION ALL ");
        query.append("SELECT NULL AS id, 'Total' AS name_en, COUNT(sort_order)::VARCHAR AS in_time, NULL AS out_time, sort_order, order_name ");
        query.append("FROM process_data GROUP BY sort_order, order_name) AS result ORDER BY sort_order, id ");

        return (List<DailyAttendanceCmd>) jdbcTemplate.query(query.toString(), params.toArray(), (RowMapper) (rs, i) -> {
            DailyAttendanceCmd program = new DailyAttendanceCmd();
            program.setTitle(rs.getString("order_name"));
            program.setName(rs.getString("name_en"));
            program.setInTime(rs.getString("in_time"));
            program.setOutTime(rs.getString("out_time"));

            return program;
        });
    }
}
