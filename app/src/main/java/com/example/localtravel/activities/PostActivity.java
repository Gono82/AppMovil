package com.example.localtravel.activities;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.localtravel.R;
import com.example.localtravel.models.Post;
import com.example.localtravel.providers.AuthProviders;
import com.example.localtravel.providers.ImageProvider;
import com.example.localtravel.providers.PostProvider;
import com.example.localtravel.utils.FileUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import javax.annotation.Nullable;

public class PostActivity extends AppCompatActivity {
    ImageView mImageViewPost1;
    File mImageFile;
    Button mButtonPost;
    ImageProvider mImageProvider;
    private final int Gallery_REQUEST_CODE=1;
    TextInputEditText mTextInputTitle;
    TextInputEditText mTextInputDescription;
    ImageView mImageViewPC;
    ImageView mImageViewPS5;
    ImageView mImageViewXBOX;
    ImageView mImageViewNINTENDO;
    TextView mTextViewCategory;
    String mCategory="";
    PostProvider mPostProvider;
    String mTitle="";
    String mDescription="";
    AuthProviders mAuthProviders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mImageViewPost1 = findViewById(R.id.imageViewPost1);
        mTextInputTitle = findViewById(R.id.textInputVideoGame);
        mTextInputDescription = findViewById(R.id.textImputDescription);
        mImageViewPC = findViewById(R.id.image1);
        mImageViewPS5 = findViewById(R.id.image2);
        mImageViewXBOX = findViewById(R.id.image3);
        mImageViewNINTENDO = findViewById(R.id.image4);
        mTextViewCategory = findViewById(R.id.textViewCardCategory);
        mButtonPost = findViewById(R.id.btnPost);

        mImageProvider = new ImageProvider();
        mPostProvider = new PostProvider();
        mAuthProviders = new AuthProviders();

        mButtonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //saveImage();
                clickPost();
            }
        });
        mImageViewPost1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        mImageViewPC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategory="PC";
                mTextViewCategory.setText(mCategory);
            }
        });
        mImageViewPS5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategory="PS5";
            }
        });
        mImageViewXBOX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategory="XBOX";
            }
        });
        mImageViewNINTENDO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCategory="NINTENDO";
            }
        });
    }

    private void clickPost() {
         mTitle=mTextInputTitle.getText().toString();
         mDescription=mTextInputDescription.getText().toString();
        if (!mTitle.isEmpty() && !mDescription.isEmpty() && mCategory.isEmpty()){
            if (mImageFile !=null){
                saveImage();
            }else {
                Toast.makeText(this, "Selecciona una imagen", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Completa los campos para publicar", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImage() {
        mImageProvider.save(PostActivity.this,mImageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    mImageProvider.getmStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url=uri.toString();
                            Post post=new Post();
                            post.setImage1(url);
                            post.setTitle(mTitle);
                            post.setDescription(mDescription);
                            post.setCategory(mCategory);
                            post.setIdUser(mAuthProviders.getUid());

                            mPostProvider.save(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> tasksave) {
                                    if (tasksave.isSuccessful()){
                                        Toast.makeText(PostActivity.this, "La informacion se almaceno correctamente", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(PostActivity.this, "La informacion no se pudo almacenar", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                    Toast.makeText(PostActivity.this, "La imagen se almaceno correctamente", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(PostActivity.this, "Error al almacenar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,Gallery_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == Gallery_REQUEST_CODE && resultCode == RESULT_OK){
            try {
                mImageFile= FileUtil.from(this, data.getData());
                mImageViewPost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("ERROR","Se produjo un error" + e.getMessage());
                Toast.makeText(this, "Se produjo un error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}