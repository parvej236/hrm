package bd.org.quantum.hrm.employee;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.text.MessageFormat;
import java.util.List;

public class EmployeeSpecifications {

    public static Specification<Employee> omniText(String omniText) {
        return (root, query, builder) -> {
            Predicate likeNameEn =
                    builder.like(
                            builder.lower(root.get(Employee_.nameEn)),
                            contains(omniText.toLowerCase()));
            Predicate likeRegCode =
                    builder.like(
                            builder.lower(root.get(Employee_.regCode)),
                            contains(omniText.toLowerCase()));
            Predicate likePhone =
                    builder.like(
                            builder.lower(root.get(Employee_.primaryPhone)),
                            contains(omniText.toLowerCase()));
            return builder.or(likeNameEn, likeRegCode, likePhone);
        };
    }

    public static Specification<Employee> employeeIdEqual(String employeeId) {
        String pad = "0000000";
        String padded = (pad.substring(0, pad.length() - employeeId.length())) + employeeId;
        String result = padded.substring(padded.length() - 5);

        return (root, query, builder) -> builder
                .equal(root.get(Employee_.employeeId), result);
    }

    public static Specification<Employee> branchesIn(List<Long> branches) {
        return (root, query, builder) -> root.get(Employee_.branch).in(branches);
    }

    public static Specification<Employee> departmentsIn(List<Long> departments) {
        return (root, query, builder) -> root.get(Employee_.department).in(departments);
    }

    public static Specification<Employee> genderEqual(String gender) {
        return (root, query, builder) -> builder
                .equal(root.get(Employee_.gender), gender);
    }

    public static Specification<Employee> designationEqual(String designation) {
        return (root, query, builder) -> builder
                .equal(root.get(Employee_.designation).get("name"), designation);
    }

    public static Specification<Employee> empStatusEqual(String status) {
        return (root, query, builder) -> builder
                .equal(root.get(Employee_.status), status);
    }

    public static Specification<Employee> empStatusIn(List<String> statuses) {
        return (root, query, builder) -> root.get(Employee_.status).in(statuses);
    }

    private static String contains(String expression) {
        return MessageFormat.format("%{0}%", expression);
    }
}