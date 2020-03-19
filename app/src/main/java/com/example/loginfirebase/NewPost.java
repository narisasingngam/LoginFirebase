package com.example.loginfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class NewPost extends AppCompatActivity {

    Button selectImage, postBtn;
    ImageView imageView;
    TextView titleText;

    public static final int READ_EXTERNAL_STORAGE = 0;
    private static final int GALLERY_INTENT = 2;
    private ProgressDialog mProgressDialog;
    private Firebase mRootRef;
    private Uri mImageUri = null;
    private DatabaseReference mdatabaseRef;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        Firebase.setAndroidContext(this);

        selectImage = (Button) findViewById(R.id.selectImage);
        postBtn = (Button) findViewById(R.id.postBtn);
        titleText = (TextView) findViewById(R.id.titleText);
        imageView = (ImageView) findViewById(R.id.imageView);

        // initialise the progress bar
        mProgressDialog = new ProgressDialog(NewPost.this);

        //select image from the storage
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check permission
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Call for permission", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
                    }
                } else {
                    callGallery();
                }
            }
        });

        //initialise firebase
        mdatabaseRef = FirebaseDatabase.getInstance().getReference();
        mRootRef = new Firebase("https://loginfirebase-9280d.firebaseio.com/").child("User_Detail").push();
        mStorage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://loginfirebase-9280d.appspot.com/");

        //click New Post
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mName = titleText.getText().toString().trim();
                if (mName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter title", Toast.LENGTH_SHORT).show();
                    return;
                }
                Firebase childRef_name = mRootRef.child("Image_Title");
                childRef_name.setValue(mName);
                Toast.makeText(getApplicationContext(), "Update Info", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void onRequestPermissionResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    callGallery();
                return;
        }
        Toast.makeText(getApplicationContext(), "...", Toast.LENGTH_SHORT).show();
    }

    // if access is granted, gallery will be opened
    private void callGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }

    // after select image
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            imageView.setImageURI(mImageUri);
            StorageReference filePath = mStorage.child("User_Images").child(mImageUri.getLastPathSegment());

            mProgressDialog.setMessage("Uploading...");
            mProgressDialog.show();

            filePath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if (taskSnapshot.getMetadata() != null) {
                        if (taskSnapshot.getMetadata().getReference() != null) {
                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    mRootRef.child("Image_URL").setValue(imageUrl);
                                    Glide.with(getApplicationContext())
                                            .load(uri)
                                            .crossFade()
//                                          .placeholder(R.drawable.loading)
                                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                            .into(imageView);
                                    Toast.makeText(getApplicationContext(), "Updated...", Toast.LENGTH_SHORT).show();
                                    mProgressDialog.dismiss();
                                }
                            });
                        }
                    }
                }
            });
        }
    }

}