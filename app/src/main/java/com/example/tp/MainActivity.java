package com.example.tp;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    EditText mEdtName , mEdtAdresse, mEdtPhone ;
    Button mBtnAdd , mBtnList;
    Spinner etFormation , etSpecialite;

    ImageView mImageView ;

    final int REQUEST_CODE_GALLERY = 999;

    public static SQLiteHelper mSQLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("AJOUT UNIVERSITE");

        mEdtName = findViewById(R.id.edtName);
        mEdtAdresse = findViewById(R.id.edtAdresse);
        mEdtPhone = findViewById(R.id.edtPhone);
        mBtnAdd = findViewById(R.id.btnAdd);
        mBtnList = findViewById(R.id.btnList);
        mImageView = findViewById(R.id.imageView);

        etFormation = (Spinner) findViewById(R.id.Formation);
        String[] items = new String[]{"Continu", "Initiale", "Continue & initiale"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        etFormation.setAdapter(adapter);

        etSpecialite = (Spinner)findViewById(R.id.Specialite);
        String[] items2 = new String[]{"Ingenieurie","Management"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items2);
        etSpecialite.setAdapter(adapter2);



        //Creating database
        mSQLiteHelper = new SQLiteHelper(this , "RECORDDB.sqlite" , null ,1 );

        //creating table in database
        mSQLiteHelper.queryData("CREATE TABLE IF NOT EXISTS RECORD (id INTEGER PRIMARY KEY AUTOINCREMENT , name VARCHAR , adresse VARCHAR , phone VARCHAR ,formation VARCHAR ,specialite VARCHAR, image BLOB)");



        // Select image by on imageview Click

        mImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // read external storage permission to select image from gallery Manifest
                //runtime permission for devices android
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });

        //add Record to SQLITE
        mBtnAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                try {

                    mSQLiteHelper.insertData(mEdtName.getText().toString().trim(),
                            mEdtAdresse.getText().toString().trim(),
                            mEdtPhone.getText().toString().trim(),
                            etFormation.getSelectedItem().toString().trim(),
                            etSpecialite.getSelectedItem().toString().trim(),
                            imageViewToByte(mImageView));
                    Toast.makeText(MainActivity.this, "Ajoutée avec succes", Toast.LENGTH_SHORT).show();

                    //reset view
                    mEdtName.setText("");
                    mEdtAdresse.setText("");
                    mEdtPhone.setText("");
                    mImageView.setImageResource(R.drawable.addphoto);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        //Show Record List

        mBtnList.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //start recordList Activity
                startActivity(new Intent(MainActivity.this , RecordListActivity.class));
            }
        });
    }

    public static byte[] imageViewToByte(ImageView image)
    {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG , 100 , stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE_GALLERY)
        {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent , REQUEST_CODE_GALLERY);
            }
            else
            {
                Toast.makeText(this , "Don't have permission to access file location" , Toast.LENGTH_SHORT).show();
            }
            return ;
        }
        super.onRequestPermissionsResult(requestCode , permissions , grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK)
        {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1 , 1).start(this); // enable image guidlines
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK)
            {
                Uri resultUri = result.getUri();
                //set image choosed from gallery to image view
                mImageView.setImageURI(resultUri);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
