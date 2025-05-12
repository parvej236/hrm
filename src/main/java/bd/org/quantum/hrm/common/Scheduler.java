package bd.org.quantum.hrm.common;

import bd.org.quantum.common.utils.DateUtils;
import bd.org.quantum.hrm.biometric.BiometricService;
import bd.org.quantum.hrm.biometric.BioAttendanceCriteria;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDateTime;

@Component
@Slf4j
public class Scheduler {
    private final BiometricService service;

    public Scheduler(BiometricService service) {
        this.service = service;
    }

    @Scheduled(cron = "0 */5 * ? * *")
    public void getAttendanceData() throws ParseException {

        BioAttendanceCriteria criteria = new BioAttendanceCriteria();
        criteria.setStart_time(DateUtils.formatToYYYYMMDDHHMMSS(LocalDateTime.now().minusMinutes(5)));
        criteria.setEnd_time(DateUtils.formatToYYYYMMDDHHMMSS(LocalDateTime.now()));

        service.fetchBioAttendance(criteria);
    }

    @Scheduled(cron = "0 2 2 * * *", zone = "Asia/Dhaka")
    public void performBatchInsert() {
        service.scheduleInsertBioAttendance();
    }
}
