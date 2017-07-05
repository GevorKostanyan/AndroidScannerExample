package am.test.user.usertest.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Toast;

import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import am.test.user.usertest.R;
import am.test.user.usertest.data.UserData;
import am.test.user.usertest.databinding.AddUserBinding;
import am.test.user.usertest.ui.base.BaseActivity;
import io.realm.Realm;
import io.realm.RealmResults;

public class AddUserActivity extends BaseActivity {
    private static final int SCAN_REQUEST_CODE = 99;
    private static final int PERMISSION = 100;

    private static final String ITEM_ID = "ITEM_ID";

    final Calendar calendar = Calendar.getInstance();
    private long itemId = -1;

    private AddUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_user);

        retrieveDataFromIntent();
        setContentView(binding.getRoot());
    }

    //Get data from incoming intent
    private void retrieveDataFromIntent() {
        if (getIntent().hasExtra(ITEM_ID)) {
            itemId = getIntent().getLongExtra(ITEM_ID, -1);
        }

        if (itemId != -1) {
            UserData userItem = Realm.getDefaultInstance().
                    where(UserData.class).
                    equalTo("id", itemId).
                    findFirst();
            binding.setUser(userItem);
        } else {
            itemId = retrieveMaxIdFromDB();
            itemId++;
        }
    }

    //This function return max id from database
    private long retrieveMaxIdFromDB() {
        RealmResults<UserData> data = Realm.getDefaultInstance().
                where(UserData.class).
                findAllSorted("id");

        // if db ism empty return -1 for after increment get right result
        return data.size() == 0 ? -1 : data.last().getId();
    }

    public static Intent newIntent(Context context, long id) {
        Intent intent = new Intent(context, AddUserActivity.class);
        intent.putExtra(ITEM_ID, id);
        return intent;
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, AddUserActivity.class);
        intent.putExtra(ITEM_ID, -1);
        return intent;
    }

    @BindingAdapter("birthday")
    public static void setBirthDay(TextInputEditText editText, long millis) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(new Date(millis));
        editText.setText(dateString);
    }

    //Date picker
    public void pickDate(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (datePicker, i, i1, i2) -> {
                    calendar.set(i, i1, i2);
                    setBirthDay(binding.userBirthDay, calendar.getTimeInMillis());
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE));

        datePickerDialog.show();
    }

    public void save(View view) {
        //Todo add some validation on fields
        UserData data = new UserData();
        data.setId(itemId);
        data.setName(binding.userName.getText().toString());
        data.setSurname(binding.userSurname.getText().toString());
        data.setBirthDay(calendar.getTimeInMillis());
        data.setEmail(binding.userEmail.getText().toString());
        data.setPhone(binding.userPhone.getText().toString());

        realmDB.beginTransaction();
        realmDB.insertOrUpdate(data);
        realmDB.commitTransaction();
        finish();
    }

    public void scanNewImage(View view) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION);
            return;
        }

        openScanner();
    }

    @RequiresPermission(android.Manifest.permission.CAMERA)
    private void openScanner() {
        int preference = ScanConstants.OPEN_CAMERA;
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, SCAN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SCAN_REQUEST_CODE:
                    Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
                    saveImage(uri);
                    break;
                default:
                    break;
            }
        }
    }

    private void saveImage(Uri uri) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Toast.makeText(this, getString(R.string.check_permission), Toast.LENGTH_SHORT).show();
            return;
        }
        Bitmap bitmap;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            binding.imageView.setImageBitmap(bitmap);

            FileOutputStream fos = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/users/" + itemId + "/");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName = "MI_" + timeStamp + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    scanNewImage(null);
                } else {
                    // permission denied, boo! Disable the
                    Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                }
                return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void viewDocuments(View view) {
//       Todo implement this later

    }
}
