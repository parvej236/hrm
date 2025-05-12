package bd.org.quantum.hrm.designation;

import bd.org.quantum.common.utils.SearchForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DesignationService {
    private final DesignationRepository repository;

    public DesignationService(DesignationRepository repository) {
        this.repository = repository;
    }

    public List<Designation> getDesignations() {
        return repository.getAllOrderBySortingOrder();
    }

    public Map<Long, String> getDesignationMap() {
        return getDesignations().stream().collect(Collectors.toMap(Designation::getId, Designation::getName, (existing, replacement) -> existing, LinkedHashMap::new));
    }

    public Page<Designation> search(SearchForm search) {
        Specification<Designation> omniSpec = Specification
                .where(StringUtils.isEmpty(search.getOmniText()) ? null : DesignationSpecifications.omniText(search.getOmniText()));

        Specification<Designation> finalSpecifications = omniSpec;

        Page<Designation> designations;

        Pageable pageable = PageRequest.of(
                search.getPage(),
                search.getPageSize(),
                Sort.by(Sort.Direction.fromString(search.getSortDirection()), search.getSortBy())
        );

        if (search.isUnpaged()) {
            List<Designation> list = repository.findAll(finalSpecifications, pageable.getSort());
            designations = new PageImpl<>(list);
        } else {
            designations = repository.findAll(finalSpecifications, pageable);
        }

        return designations;
    }
    
    public Designation getDesignationById(Long id) {
        return repository.getById(id);
    }

    public Designation create(Designation designation){
        return repository.save(designation);
    }

    public boolean checkExistingDesignation(String name) {
        return repository.existsByName(name);
    }

    public void validateDesignation(Designation designation, BindingResult result) {
        if (designation.getName() == null) {
            result.rejectValue("name", "error.required");
        }

        if (designation.getSortingOrder() == null) {
            result.rejectValue("sortingOrder", "error.required");
        }

        if (designation.getGrade() == null) {
            result.rejectValue("grade", "error.required");
        }

        if ((designation.getId() == null || designation.getId() == 0) && checkExistingDesignation(designation.getName())) {
            result.rejectValue("id", "designation.required");
        }
    }
}
