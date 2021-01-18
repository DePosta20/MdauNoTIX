package mdausoft.co.tz.mdaunotix;

public class Constants {
    public static final String APP_NAME = "NoTIX";
    // Table names
    public static final String TABLE_NAME_user= "tb_user";
    public static final String TABLE_NAME_subjects = "tb_subjects";
    public static final String TABLE_NAME_notes = "tb_notes";

    // Web API
    //private static final String ROOT_URL = "http://192.168.56.1/notix/mobApp/";
    private static final String ROOT_URL = "http://www.mdausoft.co.tz/notix/mobAPI/";
    public static final String URL_REGISTER = ROOT_URL + "register.php";
    public static final String URL_LOGIN = ROOT_URL + "login.php";
    public static final String URL_EMAIL_VERIFICATION = ROOT_URL + "email_verification.php";
    public static final String URL_EMAIL_CHECK_VERIFICATION = ROOT_URL + "check_verify.php";
    public static final String URL_RESEND_CODE = ROOT_URL + "resend_code.php";
      // Subjects API
    public static final String URL_ADD_SUBJECT = ROOT_URL + "subject_add.php";
    public static final String URL_GET_SUBJECT = ROOT_URL + "subject_get.php";
      // Notes API
    public static final String URL_ADD_NOTES = ROOT_URL + "notes_add.php";

    // Gallery
    public static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int BITMAP_SAMPLE_SIZE = 8;
    public static final String GALLERY_DIRECTORY_NAME = "mdaunotix";
    public static final String IMAGE_EXTENSION = "jpg";
    public static final String VIDEO_EXTENSION = "mp4";
    public static final int PICK_IMAGE = 20;

    // Google drive request id
    public static int GOOGLE_DRIVE_REQ_ID = 800;


}
