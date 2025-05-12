package bd.org.quantum.hrm.common;

public final class Routes {
    public static final String EMPLOYEES = "/employees";
    public static final String ADD_EMPLOYEE = "/add-employee";
    public static final String SHOW_EMPLOYEE = "/show-employee/{id}";
    public static final String EMPLOYEE_PROFILE = "/employee-profile";
    public static final String EMPLOYEE_PROFILE_DETAILS = "/employee-profile-details";
    public static final String UPDATE_EMPLOYEE = "/update-employee";
    public static final String EMPLOYEE_CHECK = "/employee/{memberId}/check";
    public static final String EMPLOYEE_SEARCH = "/employee-search";
    public static final String ASSIGN_EMPLOYEE_ID = "/assign-employee-id";
    public static final String GENERATE_EMPLOYEE_ID = "/generate-employee-id";

    public static final String ALL_CHANGES = "/all-changes";
    public static final String CHANGES_ONLY = "/changes-only";

    public static final String EMPLOYEE_IMAGE_UPLOAD = "/employee/image/upload";
    public static final String EMPLOYEE_IMAGE_DOWNLOAD = "/employee/image/download";

    public static final String ADD_STATUS_INFO = "/add-status-info";
    public static final String GET_STATUS_INFO = "/get-status-info";
    public static final String EMPLOYEES_STATUS_INFO = "/employees-status-info";
    public static final String DELETE_STATUS_INFO = "/delete-status-info";

    public static final String ADD_PROMOTION_INFO = "/add-promotion-info";
    public static final String GET_PROMOTION_INFO = "/get-promotion-info";
    public static final String EMPLOYEES_PROMOTION_INFO = "/employees-promotion-info";
    public static final String DELETE_PROMOTION_INFO = "/delete-promotion-info";

    public static final String ADD_TRANSFER_INFO = "/add-transfer-info";
    public static final String GET_TRANSFER_INFO = "/get-transfer-info";
    public static final String EMPLOYEES_TRANSFER_INFO = "/employees-transfer-info";
    public static final String DELETE_TRANSFER_INFO = "/delete-transfer-info";

    public static final String ADD_EXTRA_QUALIFICATION_INFO = "/add-extra-qualification-info";
    public static final String EMPLOYEES_EXTRA_QUALIFICATION_INFO = "/employees-extra-qualification-info";
    public static final String DELETE_EXTRA_QUALIFICATION_INFO = "/delete-extra-qualification-info";

    public static final String ADD_KHEDMOTAION_INFO = "/add-khedmotaion-info";
    public static final String GET_KHEDMOTAION_INFO = "/get-khedmotaion-info";
    public static final String EMPLOYEES_KHEDMOTAION_INFO = "/employees-khedmotaion-info";
    public static final String DELETE_KHEDMOTAION_INFO = "/delete-khedmotaion-info";

    public static final String ADD_EDUCATION_INFO = "/add-educational-info";
    public static final String EMPLOYEES_EDUCATION_INFO = "/employees-educational-info";
    public static final String DELETE_EDUCATION_INFO = "/delete-educational-info";

    public static final String ADD_EXPERIENCE_INFO = "/add-experience-info";
    public static final String EMPLOYEES_EXPERIENCE_INFO = "/employees-experience-info";
    public static final String DELETE_EXPERIENCE_INFO = "/delete-experience-info";

    public static final String ADD_TRAINING_INFO = "/add-training-info";
    public static final String EMPLOYEES_TRAINING_INFO = "/employees-training-info";
    public static final String DELETE_TRAINING_INFO = "/delete-training-info";

    public static final String ADD_PRE_JOINING_INFO = "/add-pre-joining-info";
    public static final String GET_PRE_JOINING_INFO = "/get-pre-joining-info";
    public static final String EMPLOYEES_PRE_JOINING_INFO = "/employees-pre-joining-info";
    public static final String DELETE_PRE_JOINING_INFO = "/delete-pre-joining-info";

    public static final String ADD_LANGUAGE_SKILL_INFO = "/add-language-skill-info";
    public static final String EMPLOYEES_LANGUAGE_SKILL_INFO = "/employees-language-skill-info";
    public static final String DELETE_LANGUAGE_SKILL_INFO = "/delete-language-skill-info";

    public static final String ADD_FAMILY_INFO = "/add-family-info";
    public static final String EMPLOYEES_FAMILY_INFO = "/employees-family-info";
    public static final String DELETE_FAMILY_INFO = "/delete-family-info";

    public static final String ADD_PUNISHMENT_INFO = "/add-punishment-info";
    public static final String EMPLOYEES_PUNISHMENT_INFO = "/employees-punishment-info";
    public static final String DELETE_PUNISHMENT_INFO = "/delete-punishment-info";

    public static final String ADD_RESIDENCE_INFO = "/add-residence-info";
    public static final String DELETE_RESIDENCE_INFO = "/delete-residence-info";

    public static final String DESIGNATIONS = "/designations";
    public static final String DESIGNATION_ENTRY = "/designation-entry";
    public static final String DESIGNATION_SEARCH = "/designation-search";
    public static final String DESIGNATION_UPDATE = "/designation-update";
    public static final String DESIGNATION_CHECK = "/designation-check";

    public static final String EMP_REPORT_CRITERIA = "/emp-report-criteria";
    public static final String EMP_REPORT_GENERATE = "/emp-report-generate";

    public static final String ATTENDANCES = "/attendances";
    public static final String ATTENDANCE_CRITERIA = "/attendance-criteria";
    public static final String ADD_ATTENDANCE = "/add-attendance";
    public static final String ATTENDANCE_POPUP_FORM = "/attendance-popup-form";
    public static final String ATTENDANCE_DETAILS = "/attendance-details";
    public static final String ATTENDANCE_DETAILS_LIST = "/attendance-details-list";

    public static final String DAILY_ATTENDANCE_VIEW = "/daily-attendance-view";

    public static final String BIO_ATTENDANCES = "/bio-attendances";

    public static final String MOVEMENTS = "/movements";
    public static final String ADD_MOVEMENT = "/add-movement";
    public static final String UPDATE_MOVEMENT = "/update-movement";

    public static final String YR_CLS = "/yr-cls";
    public static final String YR_CL_ENTRY = "/yr-cl-entry";
    public static final String YR_CL_SEARCH = "/yr-cl-search";
    public static final String YR_CL_UPDATE = "/yr-cl-update";
    public static final String YR_CL_CHECK = "/yr-cl-check";

    public static final String ATT_REPORT_CRITERIA = "/att-report-criteria";
    public static final String ATT_REPORT_GENERATE = "/att-report-generate";

    public static final String LEAVE_REPORT_CRITERIA = "/leave-report-criteria";
    public static final String LEAVE_REPORT_GENERATE = "/leave-report-generate";

    public final static String DEPARTMENT_BY_BRANCH = "/department/{branchId}";

    // Leave
    public static final String LEAVE_APPLY= "/leave-apply";
    public static final String LEAVE_LIST  = "/leave-list";
    public static final String LEAVE_AUTHORIZE = "/leave-authorize";
    public static final String LEAVE_APPROVE = "/leave-approve";
    public static final String LEAVE_CONFIRM = "/leave-confirm";
    public static final String LEAVE_SEARCH = "/leave-search";
    public static final String LEAVE_ENTRY = "/leave-entry";
    public static final String PRINT_LEAVE_APPLICATION = "/print-leave-application";
    public static final String ADD_YR_ENCASH = "/add-yr-encash";
    public static final String YR_ENCASH_LIST = "/yr-encash-list";
    public static final String LEAVE_BALANCE = "/leave-balance";
    public static final String GET_LEAVE_APPLICATIONS_BY_EMPLOYEE_INFO = "/get-leave-applications-by-employee-info";

    public static final String ADD_LEAVE_UPDATE_REQUEST = "/add-leave-update-request";
    public static final String MODIFY_LEAVE_UPDATE_REQUEST = "/modify-leave-update-request";
    public static final String LEAVE_UPDATE_REQUESTS = "/leave-update-requests";
    public static final String LEAVE_UPDATE_REQUEST_SEARCH = "/leave-update-request-search";
    public static final String EXISTS_UPDATE_REQUEST_BY_LEAVE = "/exists-update-request-by-leave";

    // ID Card
    public static final String ID_CARD_CRITERIA = "/id-card-criteria";
    public static final String ID_CARD_RECORDS = "/id-card-records";
    public static final String ORDERED_ID_CARDS = "/ordered-id-cards";
    public static final String PROCESSED_ID_CARDS = "/processed-id-cards";
    public static final String ACCEPTED_ID_CARDS = "/accepted-id-cards";
    public static final String PHOTO_FOR_ID_CARD = "/photo-for-id-card";
    public static final String ID_CARDS_BY_IDS = "/id-cards-by-ids";
    public static final String VERIFY_ID_CARD = "/api/verify-id-card/{encriptedId}/{year}";

    // API Path
    public static final String API_BRANCHES = "/branch-list";
    public static final String API_DEPARTMENTS = "/department-list/{branchId}";
    public static final String API_MEMBERS_BY_REG_ID_OR_PHONE = "/members";

    // External Call
    public static final String API_GET_EMPLOYEE_BY_CODE = "/api/get-employee-by-code";
    public static final String GET_EMPLOYEES_BY_EMPLOYEE_INFO = "/get-employees-by-employee-info";
    public static final String API_EMPLOYEES_BY_EMPLOYEE_INFO = "/api/get-employees-by-employee-info";


    public static final String API_USERS_BY_USERNAME_NAME_REG_OR_PHONE = "/users";
}
