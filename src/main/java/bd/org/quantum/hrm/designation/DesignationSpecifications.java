package bd.org.quantum.hrm.designation;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.text.MessageFormat;

public class DesignationSpecifications {
    public static Specification<Designation> omniText(String omniText) {

        return (root, query, builder) -> {
            Predicate likeNameEn =
                    builder.like(
                            builder.lower(root.get(Designation_.name)),
                            contains(omniText.toLowerCase()));

            return builder.or(likeNameEn);
        };
    }

    private static String contains(String expression) {
        return MessageFormat.format("%{0}%", expression);
    }
}
