package com.mexs.scanner;

public class UserSettings {
    private String testType;
    private String locationName;
    private String lab;
    private String recorder;
    private boolean useSpecimenId;
    private boolean printLabels;
    private String printerModel;
    private int printStyle;
    private int printQty;

    public UserSettings(String testType, String locationName, String lab, String recorder, boolean useSpecimenId, boolean printLabels, String printerModel, int printStyle, int printQty) {
        this.testType = testType;
        this.locationName = locationName;
        this.lab = lab;
        this.recorder = recorder;
        this.useSpecimenId = useSpecimenId;
        this.printLabels = printLabels;
        this.printerModel = printerModel;
        this.printStyle = printStyle;
        this.printQty = printQty;
    }

    public UserSettings() {
        testType = "";
        locationName = "";
        lab = "";
        recorder = "";
        this.useSpecimenId = false;
        printLabels = false;
        printerModel = "BROTHER QL-820NWB";
        printStyle = 1;
        printQty = 1;
    }

    public String getTestType() {
        return testType;
    }

    public boolean isPrintLabels() {
        return printLabels;
    }

    public boolean useSpecimenId() {
        return useSpecimenId;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getLab() {
        return lab;
    }

    public String getRecorder() {
        return recorder;
    }

    public String getPrinterModel() {
        return printerModel;
    }

    public int getPrintStyle() {
        return printStyle;
    }

    public int getPrintQty() {
        return printQty;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public void setPrintLabels(boolean printLabels) {
        this.printLabels = printLabels;
    }

    public void setUseSpecimenId(boolean useSpecimenId){
        this.useSpecimenId = useSpecimenId;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName.toUpperCase();
    }

    public void setLab(String lab) {
        this.lab = lab.toUpperCase();
    }

    public void setRecorder(String recorder) {
        this.recorder = recorder.toUpperCase();
    }

    public void setPrinterModel(String printerModel) {
        this.printerModel = printerModel;
    }

    public void setPrintStyle(int printStyle) {
        this.printStyle = printStyle;
    }

    public void incrementPrintQty(int delta){
        printQty = Math.max(1, printQty + delta);
    }

}
