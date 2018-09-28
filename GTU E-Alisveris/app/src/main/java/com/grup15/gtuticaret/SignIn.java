package com.grup15.gtuticaret;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grup15.gtuticaret.Graph.Edge;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Kullanıcının sisteme girişini yapar ve veri tabanından kontrol edilir.
 */
public class SignIn extends AppCompatActivity  {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private String password,email;
    public static String whoami;
    public static String balanceLeft,balanceRight;
    private boolean flag = true;
    private Integer a = new Integer(5);
    private int flagg = 0;
    private int flaggg=0;

    private void girisBasarili(){
            /*******************************************************************************/
            whoami = email;
            System.currentUser = new User(whoami);
            System.addComments();
            flag = false;
            a = 6;
            toast(0);
       //girerken graph bilgilerini cekiyorum emailini bulup
        DatabaseReference mDatabase1 = FirebaseDatabase.getInstance().getReference();
        mDatabase1.child("Graph").child(String.valueOf(SignIn.whoami.hashCode())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> snapshotIterable = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterable.iterator();
                while (iterator.hasNext()) {
                    DataSnapshot dataSnapshot1 = iterator.next();
                    Long l = (Long) dataSnapshot1.getValue();
                    int w = l.intValue();
                    if(dataSnapshot1.getKey().equals("biletW")){
                        System.recommendations.insert(new Edge(new User(SignIn.whoami),"ETKINLIK-BILET",w));
                    }
                    else if(dataSnapshot1.getKey().equals("deneyW")){
                        System.recommendations.insert(new Edge(new User(SignIn.whoami),"DENEY MALZEMELERI",w));
                    }
                    else if(dataSnapshot1.getKey().equals("elekW")){
                        System.recommendations.insert(new Edge(new User(SignIn.whoami),"ELEKTRONIK",w));
                    }
                    else if(dataSnapshot1.getKey().equals("esyaW")){
                        System.recommendations.insert(new Edge(new User(SignIn.whoami),"EV EŞYALARI",w));
                    }
                    else if(dataSnapshot1.getKey().equals("kitapW")){
                        System.recommendations.insert(new Edge(new User(SignIn.whoami),"KITAPLAR",w));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //dolduralacak
            }
        });
            Intent kayit= new Intent(SignIn.this, MainScreen.class);
            startActivity(kayit);
            finish();
            /**********************************************************************************/

    }

    private void girisBasarisiz(){
            toast(2);
    }

    private void toast(int i){
        if(i==0){
            Toast.makeText(this, "Giriş Başarılı.", Toast.LENGTH_LONG).show();
        }
        else if(i==1){
            Toast.makeText(this, "Bu E-posta Adresi Sisteme Kayıtlı Değil.", Toast.LENGTH_LONG).show();
        }
        else if(i==2){
            Toast.makeText(this, "Parola veya Kullanici adi hatali!", Toast.LENGTH_LONG).show();
        }
        else if(i==3){
            Toast.makeText(this, "İnternet Bağlantınızı Kontrol Ediniz", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_giris);

        findViewById(R.id.kayit).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                if(isNetworkAvailable()) {
                    Intent kayitOlEkrani = new Intent(SignIn.this, SignUp.class);
                    startActivity(kayitOlEkrani);
                    finish();
                }
                else{
                    toast(3);
                }
            }

        });

        findViewById(R.id.giris).setOnClickListener(

                new View.OnClickListener()
                {
                    public void onClick(View view) {

                        if (isNetworkAvailable()) {

                            email = ((EditText) findViewById(R.id.epostaGiris)).getText().toString().trim();
                            password = ((EditText) findViewById(R.id.passwordGiris)).getText().toString().trim();


                            mDatabase.child("Users").child(String.valueOf(email.hashCode())).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    flagg=1;


                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        HashMap hp = (HashMap) ds.getValue();
                                        String salt = (String) hp.get("salt");

                                        try {
                                            password = Sha256hash.generate(password);
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        } catch (NoSuchAlgorithmException e) {
                                            e.printStackTrace();
                                        }
                                        password = password + salt;

                                        if (password.equals(hp.get("password").toString()) && email.equals(hp.get("email").toString())){
                                            balanceLeft = (String) hp.get("balanceLeft");
                                            balanceRight = (String) hp.get("balanceRight");
                                            flaggg=1;
                                            girisBasarili();
                                        }
                                        else{
                                            Log.d("flag","girisbasarisiz");

                                        }

                                    }
                                    if(flaggg!= 1)
                                        girisBasarisiz();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        }
                        else{
                            toast(3);
                        }
                    }
                }
        );
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}


