package bd.org.quantum.hrm.biometric;

import bd.org.quantum.common.utils.DateUtils;
import bd.org.quantum.common.utils.ObjectConverter;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BiometricService {

    private static final String attendanceUrl = "/iclock/api/transactions/";

    @Value("${zkt.server.url}")
    String zktServerUrl;

    @Value("${zkt.server.token}")
    String zktServerToken;

    private final BiometricRepository repository;
    private final BiometricDao dao;

    public BiometricService(BiometricRepository repository,
                            BiometricDao dao) {
        this.repository = repository;
        this.dao = dao;
    }

    public void scheduleInsertBioAttendance() {
        LocalDate date = LocalDate.now().minusDays(1);
        dao.batchInsertAttendanceFromBioAttendance(date);
    }

    public List<BioAttendanceListCmd> getBioAttendances(BioAttendanceSearchCriteria criteria){
        return dao.getAttendances(criteria);
    }

    public void fetchBioAttendance(BioAttendanceCriteria criteria) throws ParseException {

        String url = zktServerUrl + attendanceUrl;

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);

        Map<String, Object> paramsMap = ObjectConverter.objectToMap(criteria, String.class, Object.class);

        for (Map.Entry<String, Object> entry : paramsMap.entrySet()) {
            if (entry.getValue() != null) {
                builder.queryParam(entry.getKey(), entry.getValue().toString());
            }
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(builder.build().toUri())
                .header("Content-Type", "application/json")
                .header("Authorization", "Token "+ zktServerToken)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        BioAttendanceFetchDto attendance = gson.fromJson(response.body(), BioAttendanceFetchDto.class);

        if (attendance.getData().size() > 0) {
            List<BioAttendanceLog> logs = new ArrayList<>();
            for (BioAttendanceFetchLogDto details: attendance.getData()) {
                BioAttendanceLog log = new BioAttendanceLog();
                log.setCreatedAt(new Date());
                log.setCreatedBy(1L);
                log.setUpdatedAt(new Date());
                log.setUpdatedBy(1L);

                log.setEmployee(Long.parseLong(details.getEmp_code()));
                log.setName(details.getFirst_name());
                log.setDepartment(details.getDepartment());
                log.setDesignation(details.getPosition());
                log.setPunchedAt(DateUtils.parseDate(details.getPunch_time(), DateUtils.YYYY_MM_DD_HH_MM_SS));
                log.setPunchedArea(details.getArea_alias());
                log.setPunchedDevice(details.getTerminal_alias());

                logs.add(log);
            }
            repository.saveAll(logs);
        }
        log.info("Fetch "+ attendance.getData().size() +" attendance record from device at:: " + criteria.getEnd_time());
    }


}
