package com.grup15.gtuticaret;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Sisteme satılık olarak eklenecek ürünle ilgili yapılan işlemler
 * ve satılık olarak eklenecek ürünün bilgilerinin girildiği sayfayı içerir.
 */
public class AddProduct extends MenuBar {
    //telefondaki sectiginiz resmin yolu
    private Uri filePath;
    //böyle bisi neden yapmis bilmiyorum.
    private final int PICK_IMAGE_REQUEST = 71;
    private static DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    //firebase degiskenleri
    private FirebaseStorage storage;
    private StorageReference storageReference;
    //ürün bilgilerini tutar
    private Product newProduct;
    //button text vs.
    private Spinner spinner;
    private EditText name;
    private EditText features;
    private EditText price;
    //image url
    private String imageUrl = "default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product);
        super.menuBar();

        //firebase in referansina ulasiyoruz.
        storage = FirebaseStorage.getInstance();
        storageReference =storage.getReference();
        //edittext leri init edilir.
        spinner = findViewById(R.id.spinner1);
        name = findViewById(R.id.name);
        features = findViewById(R.id.description);
        price = findViewById(R.id.editPrice);
    }

    /**
     * Eklenen ürün bilgileri kontrol edilir ve Firebaseye eklenir.
     * @param view
     */
    public void addProductToList(View view){
        //textlere yazilan degerler tutulur.
        String nname = name.getText().toString();
        String nfeatures = features.getText().toString();
        String category =  String.valueOf(spinner.getSelectedItem()).toUpperCase();
        String nprice = price.getText().toString();
        if(nname.length() == 0 || nfeatures.length() == 0 || category.length() == 0 || nprice.length() == 0)
            alert("Eksik ürün bilgisi girdiniz");
        else {
            if(category.equals("KATEGORILER"))
                alert("Lütfen bir kategori seçiniz.");
            else{
                //product objesi init edilir.
                newProduct = new Product();
                newProduct.setName(nname);
                newProduct.setFeatures(nfeatures);
                newProduct.setType(category);
                newProduct.setPrice(Double.parseDouble(nprice));
                newProduct.setImageCode(imageUrl);
                newProduct.setId((nname+ SignIn.whoami).hashCode());
                newProduct.setSeller(System.currentUser.getEmail());
                mDatabase.child("Urunler").child(category).child(String.valueOf(newProduct.getId())).setValue(newProduct);
                mDatabase.child("SonEklenenler").child(String.valueOf(newProduct.getId())).setValue(newProduct);

                Toast.makeText(getApplicationContext(),"Ürünün satışa konuldu.",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),MainScreen.class);
                startActivity(intent);
            }
        }
    }


    /**
     * Fotoğraf ekleme butonuna basıldığında image seçme metodu çağrılır.
     * @param view
     */
    public void addImage(View view) {
        chooseImage();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data!= null && data.getData() != null){
            //secilen resmin dosya yolu filePath e atanir.
            filePath = data.getData();
            try {
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //startActivityForResult ile onAcivityResult birbirine bagli metotlar.
    /**
     * Fotoğraf seçmek üzere telefonun fotoğraflar bölümünü açar.
     */
    private void chooseImage() {
        Intent intent = new Intent();
        //telefonun foto kismi aciliyor.
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
    }


    /**
     * Ürün fotoğrafını firabaseye yükler.
     * @throws IOException
     */
    private void uploadImage() throws IOException {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading..");
            progressDialog.show();
            //firebase de images klasörü olusturulur/erisir.
            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = ref.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    imageUrl = taskSnapshot.getDownloadUrl().toString();
                    Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Failed" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    /**
     * Hatalı ve eksik ürün bilgisi girilmesi durumları için oluşturulan alert dialog
     * @param message Hata durumunu belirtir (Eksik bilgi,Kategori seçilmeme durumu vb.)
     */
    private void alert(String message){

        new AlertDialog.Builder(this)
                .setTitle("UYARI!")
                .setCancelable(false)
                .setMessage(message)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

}
