package bd.org.quantum.hrm.biometric;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class BiometricDao {
    private final JdbcTemplate jdbcTemplate;

    public BiometricDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<BioAttendanceListCmd> getAttendances(BioAttendanceSearchCriteria criteria) {
        List<Object> params = new ArrayList<>();
        StringBuilder qb = new StringBuilder(100);

        qb.append("SELECT e.id, e.name_en, e.employee_id, e.branch_name, e.department_name, hd.name, ");
        qb.append("DATE(al.punched_at) punched_on, TO_CHAR(MIN(al.punched_at), 'HH12:MI AM') in_time, TO_CHAR(MAX(al.punched_at), 'HH12:MI AM') out_time ");
        qb.append("FROM hrm.employee e ");
        qb.append("JOIN hrm.bio_attendance_log al ON e.id = al.employee ");
        qb.append("JOIN hrm.designation hd ON e.designation = hd.id ");
        qb.append("WHERE e.branch IN(");
        qb.append(criteria.getBranches().stream().map(String::valueOf).collect(Collectors.joining(","))).append(") ");
        qb.append("AND e.department IN(");
        qb.append(criteria.getDepartments().stream().map(String::valueOf).collect(Collectors.joining(","))).append(") ");

        if (criteria.getAttendanceFrom() != null && criteria.getAttendanceTo() != null) {
            qb.append("AND al.punched_at >= ? AND al.punched_at < ? ");
            params.add(criteria.getAttendanceFrom());
            params.add(criteria.getAttendanceTo());
        } else {
            qb.append("AND al.punched_at >= ? ");
            params.add(LocalDate.now());
        }

        if (criteria.getEmployee() != null && criteria.getEmployee() > 0) {
            qb.append("AND al.employee = ? ");
            params.add(criteria.getEmployee());
        }

        qb.append("GROUP BY e.id, hd.name, DATE(al.punched_at), hd.sorting_order ");
        qb.append("ORDER BY hd.sorting_order, DATE(al.punched_at) DESC");

        final List<BioAttendanceListCmd> list = jdbcTemplate.query(qb.toString(), params.toArray(), (RowMapper) (rs, i) -> {
            BioAttendanceListCmd cmd     = new BioAttendanceListCmd();
            cmd.setEmpId(rs.getLong("id"));
            cmd.setName(rs.getString("name_en"));
            cmd.setEmpCode(rs.getString("employee_id"));
            cmd.setBranch(rs.getString("branch_name"));
            cmd.setDesignation(rs.getString("name"));
            cmd.setDepartment(rs.getString("department_name"));
            cmd.setPunchedOn(rs.getString("punched_on"));
            cmd.setInTime(rs.getString("in_time"));
            cmd.setOutTime(rs.getString("out_time"));
            return cmd;
        });

        return list;
    }

    public int batchInsertAttendanceFromBioAttendance(LocalDate dateFrom) {
        StringBuilder query = new StringBuilder();

        query.append("INSERT INTO hrm.employee_attendance(created_at, updated_at, created_by, updated_by, employee, ");
        query.append("attendance_date, attendance_type, in_time, out_time, attendance_status, attendance_from) ");
        query.append("SELECT MIN(l.created_at) AS created_at, MAX(l.updated_at) AS updated_at, MAX(l.created_by) AS created_by, ");
        query.append("MAX(l.updated_by) AS updated_by, l.employee, DATE(l.punched_at) punched_on, 1 AS type, ");
        query.append("TO_CHAR(MIN(l.punched_at), 'HH12:MI AM') in_time, ");
        query.append("CASE WHEN (EXTRACT(EPOCH FROM (MAX(l.punched_at) - MIN(l.punched_at))) / 3600) >=4 THEN TO_CHAR(MAX(l.punched_at), 'HH12:MI AM') ELSE NULL END AS out_time, ");
        query.append("1 AS status, 'Biometric' AS attendance_from ");
        query.append("FROM hrm.bio_attendance_log l ");
        query.append("WHERE DATE(l.punched_at) >= DATE('").append(dateFrom).append("') ");
        query.append("AND NOT EXISTS (SELECT 1 FROM hrm.employee_attendance a ");
        query.append("WHERE a.employee = l.employee AND a.attendance_date = DATE(l.punched_at)) ");
        query.append("GROUP BY l.employee, DATE(l.punched_at) ORDER BY DATE(l.punched_at) DESC");

        return jdbcTemplate.update(query.toString());
    }
}
