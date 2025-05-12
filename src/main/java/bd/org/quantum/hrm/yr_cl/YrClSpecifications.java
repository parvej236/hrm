package bd.org.quantum.hrm.yr_cl;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.text.MessageFormat;

public class YrClSpecifications {
    public static Specification<YrCl> omniText(String omniText) {

        return (root, query, builder) -> {
            Predicate likeYere =
                    builder.equal(root.get(YrCl_.year), Integer.parseInt(omniText.toLowerCase()));

            return builder.or(likeYere);
        };
    }

    private static String contains(String expression) {
        return MessageFormat.format("%{0}%", expression);
    }
}