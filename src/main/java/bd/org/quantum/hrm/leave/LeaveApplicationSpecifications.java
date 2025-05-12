package bd.org.quantum.hrm.leave;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.text.MessageFormat;
import java.util.List;

public class LeaveApplicationSpecifications {

    public static Specification<LeaveApplication> omniText(String omniText) {

        return (root, query, builder) -> {
            Predicate likeNameEn =
                    builder.like(
                            builder.lower(root.get(LeaveApplication_.applicant).get("nameEn")),
                            contains(omniText.toLowerCase()));
            Predicate likeRegCode =
                    builder.like(
                            builder.lower(root.get(LeaveApplication_.applicant).get("regCode")),
                            contains(omniText.toLowerCase()));
            Predicate likePhone =
                    builder.like(
                            builder.lower(root.get(LeaveApplication_.applicant).get("primaryPhone")),
                            contains(omniText.toLowerCase()));

            return builder.or(likeNameEn, likeRegCode, likePhone);
        };
    }

    private static String contains(String expression) {
        return MessageFormat.format("%{0}%", expression);
    }

    public static Specification<LeaveApplication> userIdEqual(Long userId) {

        return (root, query, builder) -> builder
                .equal(root.get(LeaveApplication_.createdBy), userId);
    }

    public static Specification<LeaveApplication> stageEqual(String stage) {
        return (root, query, builder) -> builder
                .equal(root.get(LeaveApplication_.stage), stage);
    }

    public static Specification<LeaveApplication> branchesIn(List<Long> branches) {
        return (root, query, builder) -> root.get(LeaveApplication_.branch).in(branches);
    }

    public static Specification<LeaveApplication> deptsIn(List<Long> departments) {
        return (root, query, builder) -> root.get(LeaveApplication_.department).in(departments);
    }
}
