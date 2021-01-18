package mdausoft.co.tz.mdaunotix.model;

public class Notes {
    private String image_path;
    private String google_drive_id;
    private String prescription;
    private String subject_code;
    private String date_time;

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getGoogle_drive_id() {
        return google_drive_id;
    }

    public void setGoogle_drive_id(String google_drive_id) {
        this.google_drive_id = google_drive_id;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public String getSubject_code() {
        return subject_code;
    }

    public void setSubject_code(String subject_code) {
        this.subject_code = subject_code;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }
}
