package com.nurulfajar10120014.firebasenotification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nurulfajar10120014.firebasenotification.model.User;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditorActivity extends AppCompatActivity {
    private static final String TAG = "TAG";
    private EditText etName, etEmail;
    private Button btnSimpan;
    //    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage;
    private FirebaseFirestore firestore;
    private StorageReference mStorageRef;
    private ProgressDialog progressDialog;
    private String id = "";
    private ImageView ivAvatar;
    private FloatingActionButton fabAddAvatar;
    private Dialog dialog;
    private Uri imageUri;
    private String photoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        ivAvatar = findViewById(R.id.iv_avatar);
        fabAddAvatar = findViewById(R.id.fab_add_avatar);
        btnSimpan = findViewById(R.id.btn_simpan);
        progressDialog = new ProgressDialog(EditorActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Menyimpan Data");

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReference();

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etName.getText().length() > 0 && etEmail.getText().length() > 0) {
                    uploadImage();
                } else {
                    Toast.makeText(EditorActivity.this, "Isi semua data!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fabAddAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getStringExtra("id");
            etName.setText(intent.getStringExtra("name"));
            etEmail.setText(intent.getStringExtra("email"));
            Glide.with(getApplicationContext()).load(intent.getStringExtra("avatar")).into(ivAvatar);
        }
    }

    private void uploadImage() {
        //check image uri
        if (imageUri != null) {
            //create instance
            final StorageReference myRef = mStorageRef.child("images/" + imageUri.getLastPathSegment());
            myRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    myRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (uri != null) {
                                photoUrl = uri.toString();
                                uploadUserInfo(photoUrl);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditorActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            uploadUserInfo(null);
            Toast.makeText(this, "Silahkan upload avatar!", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadUserInfo(String avatar) {
        String email = etEmail.getText().toString();
        String name = etName.getText().toString();
        progressDialog.show();

        if (TextUtils.isEmpty(etName.getText()) && TextUtils.isEmpty(etEmail.getText())) {
            Toast.makeText(this, "Silahkan isi semua data!", Toast.LENGTH_SHORT).show();
        } else {
            if (id != null) {
                Toast.makeText(this, "ID tidak null", Toast.LENGTH_SHORT).show();
                Map<String, Object> user = new HashMap<>();
                user.put("name", name);
                user.put("email", email);
                user.put("avatar", avatar);

                firestore.collection("users").document(id)
                    .set(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(EditorActivity.this, "Berhasil mengedit data!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(EditorActivity.this, "Gagal mengedit data!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            } else {
                DocumentReference documentReference = firestore.collection("users").document();
                User userModel = new User(email, name, photoUrl);
                documentReference.set(userModel, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (task.isSuccessful()) {
                                documentReference.set(userModel, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            Toast.makeText(EditorActivity.this, "Upload Successfull", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EditorActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        }
    }

//    private void uploadImageBak(String name, String email) {
//        progressDialog.show();
//        ivAvatar.setDrawingCacheEnabled(true);
//        ivAvatar.buildDrawingCache();
//        Bitmap bitmap = ((BitmapDrawable) ivAvatar.getDrawable()).getBitmap();
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] data = baos.toByteArray();
//
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReference("Images").child("IMG" + new Date().getTime() + ".jpeg");
//        UploadTask uploadTask = storageRef.putBytes(data);
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                progressDialog.dismiss();
//                Toast.makeText(EditorActivity.this, "Gagal Mengupload Avatar", Toast.LENGTH_SHORT).show();
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                if (taskSnapshot.getMetadata() != null) {
//                    if (taskSnapshot.getMetadata().getReference() != null) {
//                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Uri> task) {
//                                if (task.getResult() != null) {
//                                    saveData(name, email, task.getResult().toString());
//                                    Toast.makeText(EditorActivity.this, task.getResult().toString(), Toast.LENGTH_SHORT).show();
//                                } else {
//                                    progressDialog.dismiss();
//                                    Toast.makeText(EditorActivity.this, "Gagal Mengupload Avatar", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                    } else {
//                        progressDialog.dismiss();
//                        Toast.makeText(EditorActivity.this, "Gagal Mengupload Avatar", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    progressDialog.dismiss();
//                    Toast.makeText(EditorActivity.this, "Gagal Mengupload Avatar", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

    private void selectImage() {
        ImagePicker.with(EditorActivity.this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageUri = data.getData();
        ivAvatar.setImageURI(imageUri);
    }

//    private void saveData(String name, String email, String avatar) {
//        Map<String, Object> user = new HashMap<>();
//        user.put("name", name);
//        user.put("email", email);
//        user.put("avatar", avatar);
//
//        progressDialog.show();
//
//        if (id != null) {
//            db.collection("users").document(id)
//                    .set(user)
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//                                Toast.makeText(EditorActivity.this, "Berhasil mengedit data!", Toast.LENGTH_SHORT).show();
//                                finish();
//                            } else {
//                                Toast.makeText(EditorActivity.this, "Gagal mengedit data!", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//        } else {
//            db.collection("users")
//                    .add(user)
//                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                        @Override
//                        public void onSuccess(DocumentReference documentReference) {
//                            progressDialog.dismiss();
//                            Toast.makeText(EditorActivity.this, "Sukses menyimpan data", Toast.LENGTH_SHORT).show();
//                            finish();
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            progressDialog.dismiss();
//                            Toast.makeText(EditorActivity.this, "Error : " + e, Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        }
//
//    }
}