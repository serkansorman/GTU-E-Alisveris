package com.grup15.gtuticaret;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grup15.gtuticaret.AVLTree.AVLTree;
import com.squareup.picasso.Picasso;

import java.lang.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Kullanıcı giriş yaptıktan sonra ekranda gözüken ürün önerileri ve ürün arama kısmı
 */
public class MainScreen extends MenuBar {
    String typeC;
    //firebasedeki tum urunler arr arrayine cekiyorum
    public static ArrayList<Product> arr;
    private AVLTree<Product> productTree = new AVLTree<>();
    //firebase degiskenleri
    private DatabaseReference mFirebaseDatabase;
    private ListView listView;
    EditText arananUrun;
    Button ara;
    ViewPager mPager;

    private void toast ( int i){
        if (i == 0) {
            Toast.makeText(this, "Lütfen Aramak İstediginiz Kelimeyi Giriniz.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Arama Başarılı!", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anaekran);
        super.menuBar();
        arr = new ArrayList<>();
        arananUrun = findViewById(R.id.arananUrun);
        ara = findViewById(R.id.ara);
        mPager = findViewById(R.id.viewPager1);
        TextView oneri = (TextView) findViewById(R.id.oneriTitle);
        Typeface tf = Typeface.createFromAsset(getAssets(), "opensansitalic.ttf");
        oneri.setTypeface(tf);
        findViewById(R.id.ara).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                String key = arananUrun.getText().toString();
                if(key.isEmpty()){
                    toast(0);
                }
                else {
                    toast(1);
                    Intent arama = new Intent(MainScreen.this, Search.class);
                    arama.putExtra("arananDeger", key);
                    startActivity(arama);
                }
            }

        });
        String productType = System.recommendations.shortestPath(new User(SignIn.whoami));

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimerTask(), 2000,4000);



        findViewById(R.id.ara).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String key = arananUrun.getText().toString();
                if(key.isEmpty()){
                    toast(0);
                }
                else {
                    toast(1);
                    Intent arama = new Intent(MainScreen.this, Search.class);
                    arama.putExtra("arananDeger", key);
                    startActivity(arama);
                }
            }

        });

        //kategori ekranina tiklanan kategoriyi tutuyorum.
        typeC = getIntent().getStringExtra("ezkey");
        // Urunler kismindaki referanslari aliyorum sadece
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Urunler").child(productType);



        //firebasedeki urunleri bu metotla cekiyorum
        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Random r = new Random();
                ArrayList<Product> pr = new ArrayList<>();
                int i = 0;
                Iterable<DataSnapshot> snapshotIterable = dataSnapshot.getChildren();
                if (snapshotIterable != null) {
                    for (DataSnapshot dataSnapshot1 : snapshotIterable) {
                        Product product = dataSnapshot1.getValue(Product.class);
                        arr.add(product);
                        product.setName(product.getName().toLowerCase());
                        productTree.add(product);
                    }
                    if(arr.size()>5) {
                        while (i < 5) {
                            int j = r.nextInt(arr.size());
                            if (!pr.contains(arr.get(j))) {
                                pr.add(arr.get(j));
                                i++;
                            }
                        }
                    }
                    else {
                        int j =0;
                        while(j<arr.size())
                            if (!pr.contains(arr.get(j))) {
                                pr.add(arr.get(j));
                                j++;
                            }
                    }
                }

                    mPager = (ViewPager) findViewById(R.id.viewPager1);
                    ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(pr);
                    mPager.setAdapter(viewPagerAdapter);


                }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //dolduralacak
            }
        });



    }

    public class ViewPagerAdapter extends PagerAdapter {

        private Context context;
        private LayoutInflater layoutInflater;
        private ArrayList<Product> fArr;

        public ViewPagerAdapter(ArrayList<Product> array){
            fArr = (ArrayList<Product>) array.clone();
        }

        @Override
        public int getCount() {
            return fArr.size();
        }


        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view  = getLayoutInflater().inflate(R.layout.custom_layout, null);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent1 = new Intent(getApplicationContext(), ProductScreen.class);
                    intent1.putExtra("pro", fArr.get(position));
                    startActivity(intent1);
                }
            });
            ImageView imageview = (ImageView) view.findViewById(R.id.imageView);
            Typeface tf = Typeface.createFromAsset(getAssets(), "opensanss.ttf");
            Typeface tf2 = Typeface.createFromAsset(getAssets(), "opensansbold.ttf");
            TextView name = (TextView) view.findViewById(R.id.textView_name);
            TextView desc = (TextView) view.findViewById(R.id.textView_description);
            TextView price = (TextView) view.findViewById(R.id.textView_price);
            name.setTypeface(tf2);
            desc.setTypeface(tf);
            price.setTypeface(tf);
            if (fArr.get(position).getImageCode().equals("default")) {
                imageview.setImageResource(R.drawable.varsayilan);
            } else {
                Picasso.get()
                        .load(fArr.get(position).getImageCode())
                        .resize(110, 130)
                        .into(imageview);
            }

            name.setText(fArr.get(position).getName());
            desc.setText(fArr.get(position).getFeatures());
            price.setText(Double.toString(fArr.get(position).getPrice()) + " TL ");

            ViewPager vp = (ViewPager) container;
            vp.addView(view, 0);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ViewPager vp = (ViewPager) container;
            View view = (View) object;
            vp.removeView(view);
        }
    }

    public class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            MainScreen.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mPager.getCurrentItem()==0){
                        mPager.setCurrentItem(1);
                    }
                    else if(mPager.getCurrentItem()==1){
                        mPager.setCurrentItem(2);
                    }
                    else if(mPager.getCurrentItem()==2){
                        mPager.setCurrentItem(3);
                    }
                    else if(mPager.getCurrentItem()==3){
                        mPager.setCurrentItem(4);
                    }
                    else{
                        mPager.setCurrentItem(0);
                    }
                }
            });
        }
    }
}



















