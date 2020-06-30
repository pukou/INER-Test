package com.bsoft.mob.ienr.model.advisory_list;

/**
 * 咨询列表model
 */
public class PatientMessageBean {
    private String bed;
    private String consultContent;
    private String consultId;
    private String consultTime;
    private Integer msgCount;
    private Integer nurseRead;
    private String organizCode;
    private String patientName;
    private String patientId;
    private String userId;
    private String userName;
    private String wardCode;

    public String getBed() {
        return bed;
    }

    public void setBed(String bed) {
        this.bed = bed;
    }

    public String getConsultContent() {
        return consultContent;
    }

    public void setConsultContent(String consultContent) {
        this.consultContent = consultContent;
    }

    public String getConsultId() {
        return consultId;
    }

    public void setConsultId(String consultId) {
        this.consultId = consultId;
    }

    public String getConsultTime() {
        return consultTime;
    }

    public void setConsultTime(String consultTime) {
        this.consultTime = consultTime;
    }

    public Integer getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(Integer msgCount) {
        this.msgCount = msgCount;
    }

    public Integer getNurseRead() {
        return nurseRead;
    }

    public void setNurseRead(Integer nurseRead) {
        this.nurseRead = nurseRead;
    }

    public String getOrganizCode() {
        return organizCode;
    }

    public void setOrganizCode(String organizCode) {
        this.organizCode = organizCode;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getWardCode() {
        return wardCode;
    }

    public void setWardCode(String wardCode) {
        this.wardCode = wardCode;
    }
}
