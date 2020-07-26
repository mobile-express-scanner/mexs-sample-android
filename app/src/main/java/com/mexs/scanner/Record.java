package com.mexs.scanner;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "record_table")
public class Record {

    @PrimaryKey(autoGenerate = true)
    private int sn;
    private String id;
    private String name;
    private String dob;
    private String gender;
    private String nationality;
    private String address;
    private String contact;
    private String remarks;
    private String specimenId;
    private long swabTimestamp;
    private String testType;
    private String locationName;
    private String lab;
    private String recorder;

    public Record(int sn, String id, String name, String dob, String gender, String nationality, String address, String contact, String remarks, String specimenId, long swabTimestamp, String testType, String locationName, String lab, String recorder) {
        this.sn = sn;
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.nationality = nationality;
        this.address = address;
        this.contact = contact;
        this.remarks = remarks;
        this.specimenId = specimenId;
        this.swabTimestamp = swabTimestamp;
        this.testType = testType;
        this.locationName = locationName;
        this.lab = lab;
        this.recorder = recorder;
    }

    // Creates empty record
    @Ignore
    public Record() {
        this.sn = 0;
        this.id = "";
        this.name = "";
        this.dob = "";
        this.gender = "";
        this.nationality = "";
        this.address = "";
        this.contact = "";
        this.remarks = "";
        this.specimenId = "";
        this.swabTimestamp = 0;
        this.testType = "";
        this.locationName = "";
        this.lab = "";
        this.recorder = "";
    }

    public int getSn() {
        return sn;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public String getNationality() {
        return nationality;
    }

    public String getAddress() {
        return address;
    }

    public String getContact() {
        return contact;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getSpecimenId(){
        return specimenId;
    }

    public long getSwabTimestamp() {
        return swabTimestamp;
    }

    public String getSwabDateTime(){
        return swabTimestamp == 0 ? "" : new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US).format(new Date(swabTimestamp));
    }

    public String getSwabDateFile(){
        return swabTimestamp == 0 ? "" : new SimpleDateFormat("ddMMyyyy_HHmm'H'", Locale.US).format(new Date(swabTimestamp));
    }

    public String getSwabDate(){
        return swabTimestamp == 0 ? "" : new SimpleDateFormat("dd-MM-yyyy", Locale.US).format(new Date(swabTimestamp));
    }

    public String getTestType() {
        return testType;
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

    public void setSn(int sn) {
        this.sn = sn;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setSpecimenId(String specimenId){
        this.specimenId = specimenId;
    }

    public void setSwabTimestamp(long swabTimestamp) {
        this.swabTimestamp = swabTimestamp;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setLab(String lab) {
        this.lab = lab;
    }

    public void setRecorder(String recorder) {
        this.recorder = recorder;
    }

    // For printing
    public String printName(){
        return name.length() > 30 ? name.substring(0, 30) : name;
    }

    public byte[] getNameBytes(){
        return name.getBytes(Charset.forName("US-ASCII"));
    }

    public byte[] getIdBytes(){
        return getId().getBytes(Charset.forName("US-ASCII"));
    }

    public byte[] getDobBytes(){
        return dob.getBytes(Charset.forName("US-ASCII"));
    }

    public byte[] getGenderBytes(){
        return gender.getBytes(Charset.forName("US-ASCII"));
    }

    public byte[] getNationalityBytes(){
        return nationality.getBytes(Charset.forName("US-ASCII"));
    }

    public byte[] getSwabDateTimeBytes(){
        return getSwabDateTime().getBytes(Charset.forName("US-ASCII"));
    }

    public byte[] getSpecimenIdBytes(){
        return specimenId.getBytes(Charset.forName("US-ASCII"));
    }

}
