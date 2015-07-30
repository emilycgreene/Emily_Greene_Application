// Emily Greene, CS 65: Smartphone Programming, Winter 2015
package edu.dartmouth.cs.myruns;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

// Profile page for MyRuns
public class ProfileActivity extends Activity  {

    private static final String TAG = "debug";
    private EditText mEditName;
    private EditText mEditEmail;
    private EditText mEditPhone;
    private EditText mEditClass;
    private EditText mEditMajor;
    private RadioGroup mGender;
    private RadioButton mFemale;
    private RadioButton mMale;
    private Button mSaveButton;
    private Button mCancelButton;
    private Button mPickButton;
    private static final int CONTENT_REQUEST=111;
    private File output=null;
    private ImageView mProPic;
    private Uri mImageCaptureUri;
    private static final String URI_INSTANCE_STATE_KEY = "saved_uri";
    public static final int REQUEST_CODE_TAKE_FROM_CAMERA = 0;
    public static final int REQUEST_CODE_FROM_GALLERY = 1;
    public static final int REQUEST_CODE_CROP_PHOTO = 2;
    private static final String IMAGE_UNSPECIFIED = "image/*";
    private boolean isTakenFromCamera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        // set up variables
        mEditName = (EditText)findViewById(R.id.editName);
        mEditEmail = (EditText)findViewById(R.id.editEmail);
        mEditPhone = (EditText)findViewById(R.id.editPhone);
        mEditClass = (EditText)findViewById(R.id.editClass);
        mEditMajor = (EditText)findViewById(R.id.editMajor);
        mSaveButton = (Button)findViewById(R.id.saveButton);
        mCancelButton = (Button)findViewById(R.id.cancelButton);
        mPickButton = (Button)findViewById(R.id.cameraButton);
        mProPic = (ImageView)findViewById(R.id.proPic);

        loadUserData();

        // check to see if the phone has been rotated sideways
        if (savedInstanceState != null) {
            mImageCaptureUri = savedInstanceState.getParcelable(URI_INSTANCE_STATE_KEY);
            loadSnap(mImageCaptureUri);
        }
        else {
            loadSnap(null);
        }

        // save data and exit program
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
                Toast.makeText(getApplicationContext(),
                getString(R.string.profile_saved), Toast.LENGTH_SHORT).show();
                finish();
                }
            });

        // exit program without saving
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),
                getString(R.string.profile_cancelled), Toast.LENGTH_SHORT).show();
                finish();
                }
            });

        // change profile picture
        mPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDialog(MyRunsDialogFragment.DIALOG_ID_PHOTO_PICKER);

            }
        });






    }

    // the following method is based on the camera control activity from the lecture notes
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the image capture uri before the activity goes into background
        Log.d(TAG,"flip screen");

        outState.putParcelable(URI_INSTANCE_STATE_KEY, mImageCaptureUri);

    }


    // the following method is based on the camera control activity from the lecture notes
    private void loadSnap(Uri uri) {
        try {
            // if there is a uri passed in, a new pro pic has been taken but the profile hasn't
            // been saved yet and the phone has been turned sideways, so load that new pic
            if (uri != null) {

                    String urisplit = uri.toString();
                    String[] split_dash = urisplit.split("/");
                    String[] split = split_dash[6].split("\\.");
                    String combine = split[0] + "~2." + split[1];
                    String path = "//" + Environment
                            .getExternalStorageDirectory().toString() + "/";
                    String path_name = path + combine;
                    Bitmap bmap = BitmapFactory.decodeFile(path_name);
                    mProPic.setImageBitmap(bmap);


            }
            else {
                FileInputStream fis = openFileInput(getString(R.string.profile_photo_file_name));
                Bitmap bmap = BitmapFactory.decodeStream(fis);
                mProPic.setImageBitmap(bmap);
                fis.close();
            }
        } catch (IOException e) {
            // Default profile photo if no photo saved before.
            mProPic.setImageResource(R.drawable.default_profile);
            Log.d(TAG,e.getMessage());
        }
    }
    // the following method is based on the camera control activity from the lecture notes
    private void saveSnap() {

        // Commit all the changes into preference file
        // Save profile image into internal storage.
        mProPic.buildDrawingCache();
        Bitmap bmap = mProPic.getDrawingCache();
        try {
            FileOutputStream fos = openFileOutput(
                    getString(R.string.profile_photo_file_name), MODE_PRIVATE);
            bmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // the following method is based on the camera control activity from the lecture notes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case REQUEST_CODE_TAKE_FROM_CAMERA:
                // Send image taken from camera for cropping
                cropImage();
                break;

            case REQUEST_CODE_FROM_GALLERY:
                // Send image from gallery for cropping
                Uri srcUri = data.getData();
                mImageCaptureUri = getPhotoUri();

                // we copy the image from the gallery to mImageCaptureUri
                // then we can reuse the crop code
                String srcPath = getRealPathFromURI(srcUri);
                String destPath = mImageCaptureUri.getPath();
                // check srcPath is valid and file copy succeed

                if (srcPath == null || !copyfile(srcPath, destPath)) {
                    Toast.makeText(getApplicationContext(),
                            "Cannot Retrieve Image",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                cropImage();
                break;

            case REQUEST_CODE_CROP_PHOTO:
                // Update image view after image crop
                Bundle extras = data.getExtras();
                // Set the picture image in UI
                if (extras != null) {
                    mProPic.setImageBitmap((Bitmap) extras.getParcelable("data"));
                }

                // Delete temporary image taken by camera after crop.
                if (isTakenFromCamera) {
                    File f = new File(mImageCaptureUri.getPath());
                    if (f.exists())
                        f.delete();
                }

                break;
        }
    }

    // the following method is based on the camera control activity from the lecture notes
    private void cropImage() {

            // Use existing crop activity.
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(mImageCaptureUri, IMAGE_UNSPECIFIED);

            // Specify image size
            intent.putExtra("outputX", 100);
            intent.putExtra("outputY", 100);

            // Specify aspect ratio, 1:1
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);
            // REQUEST_CODE_CROP_PHOTO is an integer tag you defined to
            // identify the activity in onActivityResult() when it returns
            startActivityForResult(intent, REQUEST_CODE_CROP_PHOTO);

    }



    // the following method is based on the layouts example from class
    private void loadUserData() {
        String mKey = getString(R.string.preference);
        SharedPreferences mPreference = getSharedPreferences(mKey,MODE_PRIVATE);

        // load name
        mKey = getString(R.string.name_input);
        if (mPreference.contains(mKey)) {
            String mValue = mPreference.getString(mKey, " ");
            ((EditText) findViewById(R.id.editName)).setText(mValue);

            // load email
            mKey = getString(R.string.email_input);
            mValue = mPreference.getString(mKey, " ");
            ((EditText) findViewById(R.id.editEmail)).setText(mValue);

            // load phone
            mKey = getString(R.string.phone_input);
            mValue = mPreference.getString(mKey, " ");
            ((EditText) findViewById(R.id.editPhone)).setText(mValue);

            // load gender
            mKey = getString(R.string.gender_input);
            int mIntValue = mPreference.getInt(mKey, -1);
            if (mIntValue >= 0) {
                RadioButton radioChoice = (RadioButton) ((RadioGroup) findViewById(R.id.genderGroup)).getChildAt(mIntValue);

                radioChoice.setChecked(true);
            }

            // load class
            mKey = getString(R.string.class_input);
            mValue = mPreference.getString(mKey, " ");
            ((EditText) findViewById(R.id.editClass)).setText(mValue);

            // load major
            mKey = getString(R.string.major_input);
            mValue = mPreference.getString(mKey, " ");
            ((EditText) findViewById(R.id.editMajor)).setText(mValue);
        }

    }

    // the following method is based on the layouts example from class
    private void saveUserData() {
        String mKey = getString(R.string.preference);
        SharedPreferences mPreference = getSharedPreferences(mKey,MODE_PRIVATE);

        SharedPreferences.Editor mEditor = mPreference.edit();
        mEditor.clear();

        // save profile pic
        saveSnap();

        // save name
        mKey = getString(R.string.name_input);
        String mValue = (String) ((EditText)findViewById(R.id.editName)).getText().toString();
        mEditor.putString(mKey,mValue);

        // save email
        mKey = getString(R.string.email_input);
        mValue = (String) ((EditText)findViewById(R.id.editEmail)).getText().toString();
        mEditor.putString(mKey,mValue);

        // save phone
        mKey = getString(R.string.phone_input);
        mValue = (String) ((EditText)findViewById(R.id.editPhone)).getText().toString();
        mEditor.putString(mKey,mValue);

        // save gender
        mKey = getString(R.string.gender_input);
        RadioGroup mRadioGroup = (RadioGroup) findViewById(R.id.genderGroup);
        int mIntValue = mRadioGroup.indexOfChild(findViewById(mRadioGroup.getCheckedRadioButtonId()));
        mEditor.putInt(mKey,mIntValue);


        // save class
        mKey = getString(R.string.class_input);
        mValue = (String) ((EditText)findViewById(R.id.editClass)).getText().toString();
        mEditor.putString(mKey,mValue);

        // save major
        mKey = getString(R.string.major_input);
        mValue = (String) ((EditText)findViewById(R.id.editMajor)).getText().toString();
        mEditor.putString(mKey,mValue);

        mEditor.commit();

        Toast.makeText(getApplicationContext(),R.string.profile_saved,Toast.LENGTH_SHORT).show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // methods from camera activity
    // ******* Photo picker dialog related functions ************//

    public void displayDialog(int id) {
        DialogFragment fragment = MyRunsDialogFragment.newInstance(id);
        fragment.show(getFragmentManager(),
                getString(R.string.dialog_fragment_tag_photo_picker));
    }

    public void onPhotoPickerItemSelected(int item) {
        Intent intent;

        switch (item) {

            case MyRunsDialogFragment.ID_PHOTO_PICKER_FROM_CAMERA:
                // Take photo from camera，
                // Construct an intent with action
                // MediaStore.ACTION_IMAGE_CAPTURE
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Construct temporary image path and name to save the taken
                // photo
                mImageCaptureUri = Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), "tmp_"
                        + String.valueOf(System.currentTimeMillis()) + ".jpg"));
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        mImageCaptureUri);
                intent.putExtra("return-data", true);
                try {
                    // Start a camera capturing activity
                    // REQUEST_CODE_TAKE_FROM_CAMERA is an integer tag you
                    // defined to identify the activity in onActivityResult()
                    // when it returns
                    startActivityForResult(intent, REQUEST_CODE_TAKE_FROM_CAMERA);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                isTakenFromCamera = true;
                break;

            case MyRunsDialogFragment.ID_PHOTO_FROM_GALLERY:
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                try {
                startActivityForResult(intent,REQUEST_CODE_FROM_GALLERY);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            default:
                return;
        }

    }

    // method from website
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = new String[] { android.provider.MediaStore.Images.ImageColumns.DATA };

        Cursor cursor = getContentResolver().query(contentUri, proj, null,
                null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        String filename = cursor.getString(column_index);
        cursor.close();

        return filename;
    }

    // method from TA
    private boolean copyfile(String sourceFilename, String destinationFilename) {
        FileInputStream bis = null;
        FileOutputStream bos = null;

        try {
            // bis -> buf -> bos
            bis = new FileInputStream(sourceFilename);
            bos = new FileOutputStream(destinationFilename, false);
            byte[] buf = new byte[4 * 1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while (bis.read(buf) != -1);
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (bis != null)
                    bis.close();
                if (bos != null)
                    bos.close();
            } catch (IOException e) {
            }
        }

        return true;
    }
    // method from CodeItTwo
    // generate a temp file uri for capturing profile photo
    private Uri getPhotoUri() {
        Uri photoUri = Uri.fromFile(new File(Environment
                .getExternalStorageDirectory(), "tmp_"
                + String.valueOf(System.currentTimeMillis()) + ".jpg"));

        return photoUri;
    }

}