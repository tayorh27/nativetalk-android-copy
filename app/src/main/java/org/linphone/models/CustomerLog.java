package org.linphone.models;

public class CustomerLog {

    private String id, number, text, created_by, created_date;
    private Object timestamp;

    public CustomerLog(String id, String number, String text, String created_by, String created_date, Object timestamp) {
        this.id = id;
        this.number = number;
        this.text = text;
        this.created_by = created_by;
        this.created_date = created_date;
        this.timestamp = timestamp;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }
}
