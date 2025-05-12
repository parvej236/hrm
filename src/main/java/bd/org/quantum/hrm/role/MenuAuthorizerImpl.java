package bd.org.quantum.hrm.role;

import bd.org.quantum.authorizer.Authorizer;
import bd.org.quantum.authorizer.helper.MenuAuthorizer;
import org.springframework.stereotype.Component;

@Component
public class MenuAuthorizerImpl implements MenuAuthorizer {
    private final Authorizer authorizer;

    public MenuAuthorizerImpl(Authorizer authorizer) {
        this.authorizer = authorizer;
    }

   /*Employee*/
    public boolean getCanViewEmployeeMenu() {
        return authorizer.hasAnyPermission(Permissions.EMPLOYEE_VIEW.name(), Permissions.EMPLOYEE_CREATE.name(), Permissions.EMPLOYEE_UPDATE.name(),
                Permissions.EMPLOYEE_UPDATE_ADVANCED.name(), Permissions.EMPLOYEE_REPORT_GENERAL.name());
    }

    public boolean getCanViewEmployee() {
        return authorizer.hasPermission(Permissions.EMPLOYEE_VIEW.name());
    }

    public boolean getCanCreateEmployee() {
        return authorizer.hasPermission(Permissions.EMPLOYEE_CREATE.name());
    }

    public boolean getCanUpdateEmployee() {
        return authorizer.hasPermission(Permissions.EMPLOYEE_UPDATE.name());
    }

    public boolean getCanViewEmployeeReport() {
        return authorizer.hasAnyPermission(Permissions.EMPLOYEE_REPORT_GENERAL.name(), Permissions.EMPLOYEE_REPORT_ADVANCED.name());
    }
    public boolean getCanManageEmployeeDesignation() {
        return authorizer.hasPermission(Permissions.EMPLOYEE_DESIGNATION.name());
    }

    /*ID Card*/
    public boolean getCanViewIdCardMenu() {
        return authorizer.hasAnyPermission(Permissions.ID_CARD_ORDER.name(),Permissions.ID_CARD_PROCESS.name());
    }

    public boolean getCanOrderIdCard() {
        return authorizer.hasPermission(Permissions.ID_CARD_ORDER.name());
    }

    public boolean getCanProcessIdCard() {
        return authorizer.hasPermission(Permissions.ID_CARD_PROCESS.name());
    }

   /*Attendance*/
    public boolean getCanViewAttendanceMenu() {
        return authorizer.hasAnyPermission(Permissions.ATTENDANCE_VIEW.name(), Permissions.ATTENDANCE_CREATE.name(),
                Permissions.ATTENDANCE_UPDATE.name(), Permissions.ATTENDANCE_APPROVE.name());
    }

    public boolean getCanViewAttendance() {
        return authorizer.hasPermission(Permissions.ATTENDANCE_VIEW.name());
    }

    public boolean getCanCreateAttendance() {
        return authorizer.hasPermission(Permissions.ATTENDANCE_CREATE.name());
    }

    public boolean getCanApproveAttendance() {
        return authorizer.hasPermission(Permissions.ATTENDANCE_APPROVE.name());
    }

    public boolean getCanViewAttendanceReport() {
        return authorizer.hasPermission(Permissions.ATTENDANCE_REPORT.name());
    }

   /*Leave*/
    public boolean getCanViewLeaveMenu() {
        return authorizer.hasAnyPermission(Permissions.LEAVE_VIEW.name(), Permissions.LEAVE_APPLY.name(),
                Permissions.LEAVE_LIST.name(), Permissions.LEAVE_AUTHORIZE.name(), Permissions.LEAVE_APPROVE.name(), Permissions.LEAVE_CONFIRM.name());
    }

    public boolean getCanViewYrCls() {
        return authorizer.hasPermission(Permissions.LEAVE_YR_CL.name());
    }

    public boolean getCanViewLeave() {
        return authorizer.hasPermission(Permissions.LEAVE_VIEW.name());
    }

    public boolean getCanApplyLeave(){
        return authorizer.hasPermission(Permissions.LEAVE_APPLY.name());
    }

    public boolean getCanListLeave(){
        return authorizer.hasPermission(Permissions.LEAVE_LIST.name());
    }

    public boolean getCanViewAuthorizeLeave() {
        return authorizer.hasPermission(Permissions.LEAVE_AUTHORIZE.name());
    }

    public boolean getCanViewApproveLeave() {
        return authorizer.hasPermission(Permissions.LEAVE_APPROVE.name());
    }

    public boolean getCanViewConfirmLeave() {
        return authorizer.hasPermission(Permissions.LEAVE_CONFIRM.name());
    }

    public boolean getCanViewLeaveReport() {
        return authorizer.hasPermission(Permissions.LEAVE_REPORT.name());
    }
}
