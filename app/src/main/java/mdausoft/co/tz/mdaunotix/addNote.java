package mdausoft.co.tz.mdaunotix;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.DriveScopes;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mdausoft.co.tz.mdaunotix.Utils.CameraUtils;
import mdausoft.co.tz.mdaunotix.Utils.DriveUtils;
import mdausoft.co.tz.mdaunotix.model.Notes;

public class addNote extends AppCompatActivity {
    Context mContext = this;
    DB_helper my_DB;
    DriveUtils driveUtils;
    private static String imageStoragePath;
    GoogleSignInClient mGoogleSignInClient;
    ImageView imgPreview;
    AutoCompleteTextView subjects=null;
    Function func_obj = new Function();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        my_DB = new DB_helper(getApplicationContext(), 0);
        super.onCreate(savedInstanceState);
//        func_obj.checkConnection(getApplicationContext());
        setContentView(R.layout.activity_add_note);
        Toolbar toolbar = findViewById(R.id.toolbar_newNote);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> {
            Intent i = new Intent(addNote.this, MainActivity.class);
            startActivity(i);
        });
        imgPreview = findViewById(R.id.imgPreview);

        Button btntake_photo = findViewById(R.id.take_photo);
        btntake_photo.setOnClickListener(v -> {
            if (!CameraUtils.isDeviceSupportCamera(mContext)) {
                Toast.makeText(mContext,
                        "Sorry! Your device doesn't support camera",
                        Toast.LENGTH_LONG).show();
                finish();
            } else {
                if (CameraUtils.checkPermissions(mContext)) {
                    captureImage();
                } else {
                    requestCameraPermission(Constants.MEDIA_TYPE_IMAGE);
                }
            }
        });
        Button btnselect_photo = findViewById(R.id.select_photo);
        btnselect_photo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constants.PICK_IMAGE);
        });
        subjects = findViewById(R.id.subjects_name);
        ArrayList<String> subject_arr = my_DB.get_all_subjects();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, subject_arr);
        subjects.setThreshold(1);
        subjects.setAdapter(adapter);
        request_signIn();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Button create_file = findViewById(R.id.create_file);
        create_file.setOnClickListener(v ->
                driveUtils.createFile().addOnSuccessListener(s -> {
                    Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_LONG).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                }));
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    private void requestCameraPermission(final int type) {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            captureImage();
                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showPermissionsAlert();
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
    /////////////////////////////////////////////////////////////////////////////////////////////
    private void showPermissionsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissions required!")
                .setMessage("Camera needs few permissions to work properly. Grant them in settings.")
                .setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings(addNote.this);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }
    /////////////////////////////////////////////////////////////////////////////////////////////
    public void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = CameraUtils.getOutputMediaFile(Constants.MEDIA_TYPE_IMAGE);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
        }
        Uri fileUri = CameraUtils.getOutputMediaFileUri(getApplicationContext(), file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, Constants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }
    //////////////////////////////////////////////////////////////////////////////////////////
    private void previewCapturedImage() {
        Button save_pic = findViewById(R.id.save_pic);
        Button take_photo = findViewById(R.id.take_photo);
        Button select_photo = findViewById(R.id.select_photo);
        save_pic.setVisibility(View.VISIBLE);
        take_photo.setVisibility(View.GONE);
        select_photo.setVisibility(View.GONE);
        try {
            imgPreview.setVisibility(View.VISIBLE);
            Bitmap bitmap = CameraUtils.optimizeBitmap(Constants.BITMAP_SAMPLE_SIZE, imageStoragePath);
            imgPreview.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////
    public void request_signIn(){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        String tkn = account.getEmail();
        System.out.println(tkn);
        if (account == null){
            GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestScopes(new Scope(DriveScopes.DRIVE))
                    .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                    .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                    .requestScopes(new Scope(DriveScopes.DRIVE_READONLY))
                    .requestEmail()
                    .requestId()
                    .build();
            GoogleSignInClient client = GoogleSignIn.getClient(getApplicationContext(), signInOptions);
            startActivityForResult(client.getSignInIntent(), Constants.GOOGLE_DRIVE_REQ_ID);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
    private void handleSignInIntent(Intent data) {
        GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener(googleSignInAccount -> {
                    GoogleAccountCredential credential = GoogleAccountCredential
                            .usingOAuth2(addNote.this, Collections.singleton(DriveScopes.DRIVE_FILE));
                    credential.setSelectedAccount(googleSignInAccount.getAccount());
                    com.google.api.services.drive.Drive googleDriveService = new com.google.api.services.drive.Drive.Builder(
                            AndroidHttp.newCompatibleTransport(),
                            new GsonFactory(), credential)
                            .setApplicationName(Constants.APP_NAME)
                            .build();
                    driveUtils = new DriveUtils(googleDriveService);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Failed to SignIN", Toast.LENGTH_LONG).show();
                });
    }
    //////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == Constants.PICK_IMAGE) {
            final Uri imageUri = data.getData();
            if (imageUri != null){
                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), imageUri);
                //imgPreview.setImageBitmap(bitmap);
                Bitmap bitmap;
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    imgPreview.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            Button save_pic = findViewById(R.id.save_pic);
            Button take_photo = findViewById(R.id.take_photo);
            Button select_photo = findViewById(R.id.select_photo);
            save_pic.setVisibility(View.VISIBLE);
            take_photo.setVisibility(View.GONE);
            select_photo.setVisibility(View.GONE);
        }else if (requestCode == Constants.GOOGLE_DRIVE_REQ_ID) {
            handleSignInIntent(data);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////
    public void upload_file (View view){
        AutoCompleteTextView subjects_name = findViewById(R.id.subjects_name);
        String subject = subjects_name.toString();
        GoogleAccountCredential credential = GoogleAccountCredential
                .usingOAuth2(addNote.this, Collections.singleton(DriveScopes.DRIVE_FILE));
        com.google.api.services.drive.Drive googleDriveService = new com.google.api.services.drive.Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                new GsonFactory(),
                credential)
                .setApplicationName(Constants.APP_NAME)
                .build();
        driveUtils = new DriveUtils(googleDriveService);
        ProgressDialog progressDialog = new ProgressDialog(addNote.this);
        progressDialog.setTitle("Uploading to Google Drive");
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        String filePath = imageStoragePath;
        System.out.println(filePath);
        String mimeType = "image/jpg";
        driveUtils.create_file(filePath, mimeType).addOnSuccessListener(s -> {
            String sql = "SELECT subject_code FROM " + Constants.TABLE_NAME_subjects + " WHERE subject_name='" + subject + "'";
            Cursor cursor = my_DB.get_from_table(sql);
            cursor.moveToFirst();
            String subject_code = cursor.getString(1);
            cursor.close();
            EditText prescriptionx = findViewById(R.id.prescription);
            String prescription = prescriptionx.toString();
            String google_drive_id = "";
            progressDialog.dismiss();
            my_DB.insert_notes(filePath, google_drive_id, prescription, subject_code);
            Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_LONG).show();
        })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Check google drive API key", Toast.LENGTH_LONG).show();
                });
    }
}