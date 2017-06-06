package app.realm.gnt.com.imagecaptuting;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    private Button btn_takePictureButton, btn_ChooseImageButton;
    private ImageView iv_takeImage;
    private Uri file;
    private int mImage_Req = 1;
    final int PIC_CROP = 2;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_takePictureButton = (Button) findViewById(R.id.btn_Select_Photo);
        btn_ChooseImageButton = (Button) findViewById(R.id.btn_Choose_Photo);
        iv_takeImage = (ImageView) findViewById(R.id.iv_Simple_ImageView);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            btn_takePictureButton.setEnabled(false);
            btn_ChooseImageButton.setEnabled(false);


            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                btn_takePictureButton.setEnabled(true);
                btn_ChooseImageButton.setEnabled(true);
            }
        }
    }

    public void takePicture(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Uri.fromFile(getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
        performCrop(file);
        startActivityForResult(intent, mImage_Req);


    }

    public void choosePicture(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, mImage_Req);
    }

    public static File getOutputMediaFile() {


        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {

                Log.d("CameraDemo", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == mImage_Req && resultCode == RESULT_OK && data != null) {



            try {
                file = data.getData();
                performCrop(file);
                mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), file);
                iv_takeImage.setImageBitmap(mBitmap);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (requestCode == mImage_Req) {
            if (resultCode == RESULT_OK) {
                iv_takeImage.setImageURI(file);
            }
        }

        if (requestCode == PIC_CROP) {
            if (data != null) {
                Bundle extras = data.getExtras();
                Bitmap selectedBitmap = extras.getParcelable("data");
                iv_takeImage.setImageBitmap(selectedBitmap);
            }
        }


    }


    public void performCrop(Uri picUri) {
        try {
            //call the standard crop action intent (the user device may not support it
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", true);
            //indicate aspect ratio fo crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("asprctY", 1);
            //
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);

            cropIntent.putExtra("return-data", true);

            startActivityForResult(cropIntent, PIC_CROP);

        } catch (ActivityNotFoundException anfe) {
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }

    }


}
