package org.linphone.models;

public class NativeTalkCard {

    private String id, authCode, brand, last_four,exp_month, exp_year,bank, created_date;
    private Object timestamp;

    public NativeTalkCard(String id, String authCode, String brand, String last_four, String exp_month, String exp_year, String bank, String created_date, Object timestamp) {
        this.id = id;
        this.authCode = authCode;
        this.brand = brand;
        this.last_four = last_four;
        this.exp_month = exp_month;
        this.exp_year = exp_year;
        this.bank = bank;
        this.created_date = created_date;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getLast_four() {
        return last_four;
    }

    public void setLast_four(String last_four) {
        this.last_four = last_four;
    }

    public String getExp_month() {
        return exp_month;
    }

    public void setExp_month(String exp_month) {
        this.exp_month = exp_month;
    }

    public String getExp_year() {
        return exp_year;
    }

    public void setExp_year(String exp_year) {
        this.exp_year = exp_year;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}
