ALTER TABLE hrm.employee RENAME COLUMN date_of_birth to del_date_of_birth;
ALTER TABLE hrm.employee ADD date_of_birth date;

UPDATE hrm.employee
SET date_of_birth = TO_CHAR(
        TO_DATE(
                CONCAT(
                        LPAD(SPLIT_PART(del_date_of_birth, '/', 1), 2, '0'),
                        '/',
                        LPAD(SPLIT_PART(del_date_of_birth, '/', 2), 2, '0'),
                        '/',
                        SPLIT_PART(del_date_of_birth, '/', 3)
                    ), 'DD/MM/YYYY'), 'YYYY-MM-DD'
    )::date
WHERE LENGTH(del_date_of_birth) >= 8
  AND CAST(SPLIT_PART(del_date_of_birth, '/', 2) AS INTEGER) <= 12;

CREATE SEQUENCE hrm.yr_encash_id_seq;
ALTER TABLE hrm.yr_encash ALTER COLUMN id SET NOT NULL;
ALTER TABLE hrm.yr_encash ALTER COLUMN id SET DEFAULT nextval('hrm.yr_encash_id_seq');
ALTER SEQUENCE hrm.yr_encash_id_seq OWNED BY hrm.yr_encash.id;
CREATE UNIQUE INDEX yr_encash_id_uindex ON hrm.yr_encash (id);
ALTER TABLE hrm.yr_encash ADD CONSTRAINT yr_encash_pk PRIMARY KEY (id);
SELECT SETVAL('hrm.yr_encash_id_seq', (SELECT MAX(id) FROM hrm.yr_encash));

-- to update Shuvo Baidya (2024_09_05)
UPDATE hrm.employee SET is_deleted = false, employee_id = null WHERE id = 3768;

ALTER TABLE hrm.employee RENAME COLUMN date_of_marriage to del_date_of_marriage;
ALTER TABLE hrm.employee ADD date_of_marriage date;

UPDATE hrm.employee
SET date_of_marriage = TO_CHAR(
        TO_DATE(
                CONCAT(
                        LPAD(SPLIT_PART(del_date_of_marriage, '/', 1), 2, '0'),
                        '/',
                        LPAD(SPLIT_PART(del_date_of_marriage, '/', 2), 2, '0'),
                        '/',
                        SPLIT_PART(del_date_of_marriage, '/', 3)
                ), 'DD/MM/YYYY'), 'YYYY-MM-DD'
                       )::date
WHERE LENGTH(del_date_of_marriage) >= 8
  AND CAST(SPLIT_PART(del_date_of_marriage, '/', 2) AS INTEGER) <= 12;

UPDATE hrm.employee e SET image_path=m.image_path FROM member.member m WHERE e.member=m.id;
ALTER TABLE hrm.yr_encash RENAME encash_date TO encashed_on;
ALTER TABLE hrm.yr_encash ALTER COLUMN encashed_on TYPE date;
ALTER TABLE hrm.yr_encash DROP COLUMN del_version;

DELETE FROM users.permission WHERE permission = 'EMPLOYEE_FOREIGN_DEPARTMENT_ACCESS';
UPDATE hrm.employee SET department = null, department_name = null WHERE branch = 106;

CREATE TABLE hrm.id_card_records
(
    id              BIGSERIAL
        PRIMARY KEY,
    created_at      TIMESTAMP not null,
    created_by      BIGINT    not null,
    updated_at      TIMESTAMP not null,
    updated_by      BIGINT    not null,
    accepted_by     BIGINT,
    accepted_on     DATE,
    blood_group     VARCHAR(255),
    branch_id       BIGINT,
    branch_name     VARCHAR(255),
    department_id   BIGINT,
    department_name VARCHAR(255),
    designation     VARCHAR(255),
    designation_id  BIGINT,
    employee        BIGINT,
    employee_id     VARCHAR(255),
    employee_name   VARCHAR(255),
    expire_on       DATE,
    image_path      VARCHAR(255),
    ordered_by      BIGINT,
    ordered_on      DATE,
    processed_by    BIGINT,
    processed_on    DATE,
    stage           VARCHAR(255),
    verify_url      VARCHAR(255),
    year            INTEGER   not null
);

alter table hrm.id_card_records
    owner to postgres;
ALTER TABLE hrm.id_card_records OWNER TO postgres;

UPDATE hrm.employee e
SET branch_name = b.name
FROM resource.branch b
WHERE e.branch = b.id
  AND (e.branch_name IS NULL OR e.branch_name != b.name);

UPDATE hrm.transfer_info t
SET branch_from_name = b.name
FROM resource.branch b
WHERE t.branch_from = b.id
  AND (t.branch_from_name IS NULL OR t.branch_from_name != b.name);

UPDATE hrm.transfer_info t
SET branch_to_name = b.name
FROM resource.branch b
WHERE t.branch_to = b.id
  AND (t.branch_to_name IS NULL OR t.branch_to_name != b.name);

CREATE INDEX employee_index
    ON hrm.employee_attendance (employee);

WITH unique_ids AS (
    SELECT MIN(id) AS id
    FROM hrm.employee_attendance
    WHERE attendance_date >= '2001-01-01'
      AND attendance_date < '2011-01-01'
    GROUP BY employee, attendance_date
) DELETE FROM hrm.employee_attendance
WHERE id NOT IN(SELECT id FROM unique_ids)
  AND attendance_date >= '2001-01-01'
  AND attendance_date < '2011-01-01';

WITH unique_ids AS (
    SELECT MIN(id) AS id
    FROM hrm.employee_attendance
    WHERE attendance_date >= '2011-01-01'
      AND attendance_date < '2012-01-01'
    GROUP BY employee, attendance_date
) DELETE FROM hrm.employee_attendance
WHERE id NOT IN(SELECT id FROM unique_ids)
  AND attendance_date >= '2011-01-01'
  AND attendance_date < '2012-01-01';

WITH unique_ids AS (
    SELECT MIN(id) AS id
    FROM hrm.employee_attendance
    WHERE attendance_date >= '2012-01-01'
      AND attendance_date < '2013-01-01'
    GROUP BY employee, attendance_date
) DELETE FROM hrm.employee_attendance
WHERE id NOT IN(SELECT id FROM unique_ids)
  AND attendance_date >= '2012-01-01'
  AND attendance_date < '2013-01-01';

WITH unique_ids AS (
    SELECT MIN(id) AS id
    FROM hrm.employee_attendance
    WHERE attendance_date >= '2013-01-01'
      AND attendance_date < '2014-01-01'
    GROUP BY employee, attendance_date
) DELETE FROM hrm.employee_attendance
WHERE id NOT IN(SELECT id FROM unique_ids)
  AND attendance_date >= '2013-01-01'
  AND attendance_date < '2014-01-01';

WITH unique_ids AS (
    SELECT MIN(id) AS id
    FROM hrm.employee_attendance
    WHERE attendance_date >= '2014-01-01'
      AND attendance_date < '2015-01-01'
    GROUP BY employee, attendance_date
) DELETE FROM hrm.employee_attendance
WHERE id NOT IN(SELECT id FROM unique_ids)
  AND attendance_date >= '2014-01-01'
  AND attendance_date < '2015-01-01';

WITH unique_ids AS (
    SELECT MIN(id) AS id
    FROM hrm.employee_attendance
    WHERE attendance_date >= '2015-01-01'
      AND attendance_date < '2016-01-01'
    GROUP BY employee, attendance_date
) DELETE FROM hrm.employee_attendance
WHERE id NOT IN(SELECT id FROM unique_ids)
  AND attendance_date >= '2015-01-01'
  AND attendance_date < '2016-01-01';

WITH unique_ids AS (
    SELECT MIN(id) AS id
    FROM hrm.employee_attendance
    WHERE attendance_date >= '2016-01-01'
      AND attendance_date < '2017-01-01'
    GROUP BY employee, attendance_date
) DELETE FROM hrm.employee_attendance
WHERE id NOT IN(SELECT id FROM unique_ids)
  AND attendance_date >= '2016-01-01'
  AND attendance_date < '2017-01-01';

WITH unique_ids AS (
    SELECT MIN(id) AS id
    FROM hrm.employee_attendance
    WHERE attendance_date >= '2017-01-01'
      AND attendance_date < '2018-01-01'
    GROUP BY employee, attendance_date
) DELETE FROM hrm.employee_attendance
WHERE id NOT IN(SELECT id FROM unique_ids)
  AND attendance_date >= '2017-01-01'
  AND attendance_date < '2018-01-01';

WITH unique_ids AS (
    SELECT MIN(id) AS id
    FROM hrm.employee_attendance
    WHERE attendance_date >= '2018-01-01'
      AND attendance_date < '2019-01-01'
    GROUP BY employee, attendance_date
) DELETE FROM hrm.employee_attendance
WHERE id NOT IN(SELECT id FROM unique_ids)
  AND attendance_date >= '2018-01-01'
  AND attendance_date < '2019-01-01';

WITH unique_ids AS (
    SELECT MIN(id) AS id
    FROM hrm.employee_attendance
    WHERE attendance_date >= '2019-01-01'
      AND attendance_date < '2020-01-01'
    GROUP BY employee, attendance_date
) DELETE FROM hrm.employee_attendance
WHERE id NOT IN(SELECT id FROM unique_ids)
  AND attendance_date >= '2019-01-01'
  AND attendance_date < '2020-01-01';

WITH unique_ids AS (
    SELECT MIN(id) AS id
    FROM hrm.employee_attendance
    WHERE attendance_date >= '2024-01-01'
      AND attendance_date < '2024-09-01'
    GROUP BY employee, attendance_date
) DELETE FROM hrm.employee_attendance
WHERE id NOT IN(SELECT id FROM unique_ids)
  AND attendance_date >= '2024-01-01'
  AND attendance_date < '2024-09-01';

WITH unique_ids AS (
    SELECT MIN(id) AS id
    FROM hrm.employee_attendance
    WHERE attendance_date >= '2024-09-01'
      AND attendance_date < '2024-10-01'
    GROUP BY employee, attendance_date
) DELETE FROM hrm.employee_attendance
WHERE id NOT IN(SELECT id FROM unique_ids)
  AND attendance_date >= '2024-09-01'
  AND attendance_date < '2024-10-01';

WITH unique_ids AS (
    SELECT MIN(id) AS id
    FROM hrm.employee_attendance
    WHERE attendance_date >= '2024-10-01'
      AND attendance_date < '2024-11-01'
    GROUP BY employee, attendance_date
) DELETE FROM hrm.employee_attendance
WHERE id NOT IN(SELECT id FROM unique_ids)
  AND attendance_date >= '2024-10-01'
  AND attendance_date < '2024-11-01';

WITH unique_ids AS (
    SELECT MIN(id) AS id
    FROM hrm.employee_attendance
    WHERE attendance_date >= '2024-11-01'
      AND attendance_date < '2025-01-01'
    GROUP BY employee, attendance_date
) DELETE FROM hrm.employee_attendance
WHERE id NOT IN(SELECT id FROM unique_ids)
  AND attendance_date >= '2024-11-01'
  AND attendance_date < '2025-01-01';

ALTER TABLE hrm.employee_attendance
    ADD CONSTRAINT employee_attendance_date_unique UNIQUE (employee, attendance_date);

UPDATE hrm.employee e
SET national_id = m.national_id
FROM member.member m
WHERE e.member = m.id
  AND (m.national_id IS NOT NULL OR m.national_id != '')
  AND (e.national_id IS NULL OR e.national_id = '');

UPDATE hrm.employee e SET employee_id = NULL WHERE e.status IN(4,7);
UPDATE hrm.employee SET is_deleted = FALSE WHERE is_deleted IS NULL;
ALTER TABLE hrm.employee ALTER COLUMN is_deleted SET DEFAULT FALSE;

-- Release on 26/11/2024 --
UPDATE hrm.employee SET temp_employee_id = NULL WHERE (temp_employee_id IS NOT NULL OR temp_employee_id = '');

UPDATE hrm.employee e
SET regular_date = ti.date_from
FROM hrm.type_info ti
WHERE e.id = ti.employee
  AND e.status = ti.status_to
  AND e.status = 1
  AND e.regular_date IS NULL;

UPDATE hrm.employee SET is_deleted = FALSE WHERE is_deleted IS NULL;

UPDATE hrm.id_card_records
SET verify_url = replace(verify_url, '/api/verify-id-card/', '')
WHERE verify_url IS NOT NULL;

UPDATE hrm.id_card_records
SET verify_url = replace(verify_url, '/2025', '')
WHERE verify_url IS NOT NULL;

ALTER TABLE hrm.id_card_records RENAME verify_url TO encripted_id;

ALTER TABLE hrm.id_card_records
    ADD CONSTRAINT encription_unique_key
        UNIQUE (encripted_id);

WITH employee_wise_rows AS (
    SELECT id, employee, ROW_NUMBER() OVER (PARTITION BY employee ORDER BY id) AS new_order
    FROM hrm.type_info
),
     ordered_data AS (
         SELECT id, employee, date_from, status_from, status_to,
                ROW_NUMBER() OVER (PARTITION BY employee ORDER BY date_from, id) AS old_order
         FROM hrm.type_info
     )
UPDATE hrm.type_info t
SET
    date_from = tbl.new_date,
    status_from = tbl.new_status_from,
    status_to = tbl.new_status_to
FROM (
         SELECT er.id, od.date_from AS new_date, od.status_from AS new_status_from, od.status_to AS new_status_to
         FROM employee_wise_rows er JOIN ordered_data od ON er.employee = od.employee AND er.new_order = od.old_order
     ) AS tbl
WHERE t.id = tbl.id;

ALTER TABLE hrm.employee ADD salary_location VARCHAR(255) DEFAULT 'KENDRO' NOT NULL;

CREATE TABLE hrm.leave_update_request
(
    id                       BIGSERIAL
        PRIMARY KEY,
    created_at               TIMESTAMP NOT NULL,
    created_by               BIGINT    NOT NULL,
    updated_at               TIMESTAMP NOT NULL,
    updated_by               BIGINT    NOT NULL,
    approved_by              BIGINT,
    approved_by_name         VARCHAR(255),
    approved_on              DATE,
    authorized_by            BIGINT,
    authorized_by_name       VARCHAR(255),
    authorized_on            DATE,
    requested_by             BIGINT,
    requested_by_name        VARCHAR(255),
    requested_on             DATE,
    rejected_by             BIGINT,
    rejected_by_name        VARCHAR(255),
    rejected_on             DATE,
    date_from               DATE,
    date_to                 DATE,
    duration                 INT,
    remarks                  VARCHAR(500),
    stage                    VARCHAR(255),
    application_id           BIGINT
);

ALTER TABLE hrm.leave_update_request
    OWNER TO postgres;

CREATE TABLE hrm.leave_details_old
(
    id          BIGSERIAL PRIMARY KEY,
    date_from   DATE,
    date_to     DATE,
    days        INTEGER,
    leave_type  INTEGER NOT NULL,
    remarks     VARCHAR(500),
    request BIGINT
        CONSTRAINT FKLUR2024DEC12SAT28001
            REFERENCES hrm.leave_update_request
);

ALTER TABLE hrm.leave_details_old OWNER TO postgres;

CREATE TABLE hrm.leave_details_update
(
    id          BIGSERIAL PRIMARY KEY,
    date_from   DATE,
    date_to     DATE,
    days        INTEGER,
    leave_type  INTEGER NOT NULL,
    remarks     VARCHAR(500),
    application_id BIGINT,
    request BIGINT
        CONSTRAINT FKLUR2024DEC12SAT28002
            REFERENCES hrm.leave_update_request
);

ALTER TABLE hrm.leave_details_update OWNER TO postgres;

ALTER TABLE hrm.employee RENAME COLUMN status TO del_status;
ALTER TABLE hrm.employee ADD status VARCHAR(50) DEFAULT 'Irregular';

DROP INDEX hrm.idx_hrm_employee_status;
CREATE INDEX idx_hrm_employee_status ON hrm.employee (status);

UPDATE hrm.employee SET status = 'Regular' WHERE del_status = 1;
UPDATE hrm.employee SET status = 'Irregular' WHERE del_status = 2;
UPDATE hrm.employee SET status = 'Muster Roll' WHERE del_status = 4;
UPDATE hrm.employee SET status = 'Contractual' WHERE del_status = 6;
UPDATE hrm.employee SET status = 'Part-Time' WHERE del_status = 7;
UPDATE hrm.employee SET status = 'Honorary' WHERE del_status = 8;
UPDATE hrm.employee SET status = 'Probationary' WHERE del_status = 9;
UPDATE hrm.employee SET status = 'Resigned' WHERE del_status = 11;
UPDATE hrm.employee SET status = 'Suspended' WHERE del_status = 12;
UPDATE hrm.employee SET status = 'Dismissed' WHERE del_status = 13;
UPDATE hrm.employee SET status = 'Absent' WHERE del_status = 18;
UPDATE hrm.employee SET status = 'Deceased' WHERE del_status = 19;

ALTER TABLE hrm.type_info RENAME COLUMN status_from TO del_status_from;
ALTER TABLE hrm.type_info RENAME COLUMN status_to TO del_status_to;

ALTER TABLE hrm.type_info ALTER COLUMN del_status_from DROP NOT NULL;
ALTER TABLE hrm.type_info ALTER COLUMN del_status_to DROP NOT NULL;

ALTER TABLE hrm.type_info ADD status_from VARCHAR(50) NULL;
ALTER TABLE hrm.type_info ADD status_to VARCHAR(50) NULL;

UPDATE hrm.type_info SET status_from = 'Regular' WHERE del_status_from = 1;
UPDATE hrm.type_info SET status_from = 'Irregular' WHERE del_status_from = 2;
UPDATE hrm.type_info SET status_from = 'Muster Roll' WHERE del_status_from = 4;
UPDATE hrm.type_info SET status_from = 'Contractual' WHERE del_status_from = 6;
UPDATE hrm.type_info SET status_from = 'Part-Time' WHERE del_status_from = 7;
UPDATE hrm.type_info SET status_from = 'Honorary' WHERE del_status_from = 8;
UPDATE hrm.type_info SET status_from = 'Probationary' WHERE del_status_from = 9;
UPDATE hrm.type_info SET status_from = 'Resigned' WHERE del_status_from = 11;
UPDATE hrm.type_info SET status_from = 'Suspended' WHERE del_status_from = 12;
UPDATE hrm.type_info SET status_from = 'Dismissed' WHERE del_status_from = 13;
UPDATE hrm.type_info SET status_from = 'Absent' WHERE del_status_from = 18;
UPDATE hrm.type_info SET status_from = 'Deceased' WHERE del_status_from = 19;

UPDATE hrm.type_info SET status_to = 'Regular' WHERE del_status_to = 1;
UPDATE hrm.type_info SET status_to = 'Irregular' WHERE del_status_to = 2;
UPDATE hrm.type_info SET status_to = 'Muster Roll' WHERE del_status_to = 4;
UPDATE hrm.type_info SET status_to = 'Contractual' WHERE del_status_to = 6;
UPDATE hrm.type_info SET status_to = 'Part-Time' WHERE del_status_to = 7;
UPDATE hrm.type_info SET status_to = 'Honorary' WHERE del_status_to = 8;
UPDATE hrm.type_info SET status_to = 'Probationary' WHERE del_status_to = 9;
UPDATE hrm.type_info SET status_to = 'Resigned' WHERE del_status_to = 11;
UPDATE hrm.type_info SET status_to = 'Suspended' WHERE del_status_to = 12;
UPDATE hrm.type_info SET status_to = 'Dismissed' WHERE del_status_to = 13;
UPDATE hrm.type_info SET status_to = 'Absent' WHERE del_status_to = 18;
UPDATE hrm.type_info SET status_to = 'Deceased' WHERE del_status_to = 19;

ALTER TABLE hrm.employee_attendance RENAME COLUMN status TO attendance_status;

UPDATE hrm.employee SET temp_employee_id = REPLACE(temp_employee_id, 'R', 'R-') WHERE temp_employee_id LIKE 'R%';
UPDATE hrm.employee SET temp_employee_id = REPLACE(temp_employee_id, 'C', 'C-') WHERE temp_employee_id LIKE 'C%';
UPDATE hrm.employee SET temp_employee_id = REPLACE(temp_employee_id, 'P', 'P-') WHERE temp_employee_id LIKE 'P%';
UPDATE hrm.employee SET temp_employee_id = REPLACE(temp_employee_id, 'T', 'T-') WHERE temp_employee_id LIKE 'T%';

ALTER TABLE hrm.employee DROP COLUMN unique_id;