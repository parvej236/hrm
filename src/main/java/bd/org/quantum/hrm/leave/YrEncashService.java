package bd.org.quantum.hrm.leave;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class YrEncashService {
    private YrEncashRepository repository;

    public YrEncashService(YrEncashRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public YrEncash create(YrEncash yrEncash) {
        return repository.save(yrEncash);
    }

    public List<YrEncash> encashListByEmployeeId(Long employeeId) {
        return repository.findAllByEmployeeId(employeeId);
    }
}
