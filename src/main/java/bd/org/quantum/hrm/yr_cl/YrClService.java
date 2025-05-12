package bd.org.quantum.hrm.yr_cl;

import bd.org.quantum.common.utils.SearchForm;
import groovy.util.logging.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

import java.util.List;

@Slf4j
@Service
public class YrClService {
    private final YrClRepository repository;

    public YrClService(YrClRepository repository) {
        this.repository = repository;
    }

    public Page<YrCl> search(SearchForm search) {
        Specification<YrCl> omniSpec = Specification
                .where(StringUtils.isEmpty(search.getOmniText()) ? null : YrClSpecifications.omniText(search.getOmniText()));

        Specification<YrCl> finalSpecifications = omniSpec;

        Page<YrCl> YrCls;

        Pageable pageable = PageRequest.of(
                search.getPage(),
                search.getPageSize(),
                Sort.by(Sort.Direction.fromString(search.getSortDirection()), search.getSortBy())
        );

        if (search.isUnpaged()) {
            List<YrCl> list = repository.findAll(finalSpecifications, pageable.getSort());
            YrCls = new PageImpl<>(list);
        } else {
            YrCls = repository.findAll(finalSpecifications, pageable);
        }
        return YrCls;
    }

    public YrCl getYrClById(Long id) {
        return repository.getById(id);
    }

    public YrCl create(YrCl yrcl){
        return repository.save(yrcl);
    }

    public boolean checkExistingYrCl(Integer year) {
        return repository.existsByYear(year);
    }

    public void validateYrCl(YrCl yrcl, BindingResult result) {

        if (yrcl.getYear() == null) {
            result.rejectValue("year", "error.required");
        }

        if ((yrcl.getId() == null || yrcl.getId() == 0) && checkExistingYrCl(yrcl.getYear())) {
            result.rejectValue("year", "yrcl.required");
        }
    }
}
