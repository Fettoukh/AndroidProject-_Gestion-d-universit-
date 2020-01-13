package com.example.tp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class RecordListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    ListView mListView;
    ArrayList<Model> mList;
    RecordListAdapter mAdapter = null ;
    private SearchView sv;

    ImageView imageViewIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Liste des universités ");


        mListView = findViewById(R.id.listView);
        sv = (SearchView) findViewById(R.id.search_view);
        mList = new ArrayList<>();
        mAdapter = new RecordListAdapter(this , R.layout.row , mList);
        mListView.setAdapter(mAdapter);
        sv.setOnQueryTextListener(this);

        //getAll Datafrom Sqlite
        ShowAll();

        //No data in SQLITE
        if(mList.size()==0)
        {
            Toast.makeText(this , "Aucune université trouvée" , Toast.LENGTH_SHORT ).show();
        }


        //Long Click to update or Delete
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final   int position, long l) {
                final CharSequence[] items = {"Modifier" , "Supprimer"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(RecordListActivity.this);

                dialog.setTitle("Choisir une action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0)
                        {

                            //update
                            ArrayList<Integer> arrID = getAllID();
                            showDialogUpdate(RecordListActivity.this , arrID.get(position));
                        }if(i==1)
                        {
                            //delete
                            ArrayList<Integer> arrID = getAllID();
                            showDialogDelete(arrID.get(position));
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });

    }

    private void showDialogDelete(final Integer idRecord)
    {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(RecordListActivity.this);
        dialogDelete.setTitle("Supression !!");
        dialogDelete.setMessage("Etes vous sûre ?");
        dialogDelete.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try{
                    MainActivity.mSQLiteHelper.deleteData(idRecord);
                    Toast.makeText(RecordListActivity.this , "Suppression effectuée" , Toast.LENGTH_SHORT).show();
                }
                catch(Exception e)
                {
                    Log.e("error" , e.getMessage());
                }
                updateRecordList();
            }
        });
        dialogDelete.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialogDelete.show();
    }

    private void showDialogUpdate (final Activity activity , final int position )
    {

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_dialog);
        dialog.setTitle("Modification");

        imageViewIcon = dialog.findViewById(R.id.imageViewRecord);
        final EditText edtName = dialog.findViewById(R.id.edtName);
        final EditText edtAdresse = dialog.findViewById(R.id.edtAdresse);
        final EditText edtPhone = dialog.findViewById(R.id.edtPhone);

        final Spinner edtFormation = dialog.findViewById(R.id.Formation);
        String[] items = new String[]{"Continu", "Initiale", "Continue & initiale"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        edtFormation.setAdapter(adapter);

        final Spinner edtSpecialite = dialog.findViewById(R.id.Specialite);
        String[] items2 = new String[]{"Ingenieurie","Management"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items2);
        edtSpecialite.setAdapter(adapter2);

        Button btnUpdate = dialog.findViewById(R.id.btnUpdate);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        //get 1 data from SQLITE
        Cursor cursor = MainActivity.mSQLiteHelper.getData("SELECT * FROM RECORD WHERE id = " + position);
        while(cursor.moveToNext())
        {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            edtName.setText(name);
            String adresse = cursor.getString(2);
            edtAdresse.setText(adresse);
            String phone = cursor.getString(3);
            edtPhone.setText(phone);
            String formation = cursor.getString(4);
            int pos = adapter.getPosition(formation);
            edtFormation.setSelection(pos);
            String specialite = cursor.getString(5);
            pos = adapter2.getPosition(specialite);
            edtSpecialite.setSelection(pos);
            byte[] image = cursor.getBlob(6);
            imageViewIcon.setImageBitmap(BitmapFactory.decodeByteArray(image , 0 , image.length ));
        }

        // set width of dialog
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels*0.95);
        //set height of dialog
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels*0.8);
        dialog.getWindow().setLayout(width,height);
        dialog.show();

        //in Update dialog clock image view to update image

        imageViewIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check external permission
                ActivityCompat.requestPermissions(
                        RecordListActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        000
                );
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    MainActivity.mSQLiteHelper.updateData(edtName.getText().toString().trim(),
                            edtAdresse.getText().toString().trim(),
                            edtPhone.getText().toString().trim(),
                            edtFormation.getSelectedItem().toString(),
                            edtSpecialite.getSelectedItem().toString(),
                            MainActivity.imageViewToByte(imageViewIcon),
                            position);
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext() , "Modification effectuée" , Toast.LENGTH_SHORT).show();
                }
                catch(Exception error)
                {
                    Log.e("Update Error" , error.getMessage());
                }
                updateRecordList();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowAll();
                dialog.dismiss();
            }
        });
    }

    private void updateRecordList()
    {
        // get all data from SQLITE
        ShowAll();
        mAdapter.notifyDataSetChanged();
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
        if(requestCode == 000)
        {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent , 000);
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
        if(requestCode == 000 && resultCode == RESULT_OK)
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
                imageViewIcon.setImageURI(resultUri);
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mAdapter.getFilter().filter(newText);
        return false;
    }


    public void ShowAll()
    {
        Cursor cursor = MainActivity.mSQLiteHelper.getData("SELECT * FROM RECORD");
        mList.clear();
        while(cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String adresse = cursor.getString(2);
            String phone = cursor.getString(3);
            String formation = cursor.getString(4);
            String specialite = cursor.getString(5);
            byte[] image = cursor.getBlob(6);

            mList.add(new Model(id, name, adresse, phone, formation, specialite, image));
        }
    }

    public ArrayList<Integer> getAllID()
    {
        Cursor c = MainActivity.mSQLiteHelper.getData("SELECT id FROM RECORD");
        ArrayList<Integer> arrID = new ArrayList<Integer>();
        while(c.moveToNext())
        {
            arrID.add(c.getInt(0));
        }
        return arrID;
    }


}
