package com.example.vibestudy;

public class SubscriptionMainExcelResponseDto {
    private String subsId;
    private String mainSubsYn;
    private String mainSubsId;
    private boolean valid;
    private String errorMessage;

    public String getSubsId() { return subsId; }
    public void setSubsId(String subsId) { this.subsId = subsId; }

    public String getMainSubsYn() { return mainSubsYn; }
    public void setMainSubsYn(String mainSubsYn) { this.mainSubsYn = mainSubsYn; }

    public String getMainSubsId() { return mainSubsId; }
    public void setMainSubsId(String mainSubsId) { this.mainSubsId = mainSubsId; }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
