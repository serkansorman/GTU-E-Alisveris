package com.grup15.gtuticaret;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Ürün sepeti sayfasında  yapılan işlemler
 */
public class Cart extends MenuBar {

    private ArrayList<Pair<CheckBox,Product>> pairCart; //sepetin içindeki ürünleri ve işaretli olup olmadıklarını burdan kontrol etcez


    public Cart(){
        pairCart = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sepet);
        final LinearLayout genel_Sepet = findViewById(R.id.general);
        final LinkedList<Product> tmpCart = (LinkedList<Product>)User.cart.clone();  //sepetin kopyası üzerinde poll yapabilmek için
        if (User.cart != null) {
            Product tmpProduct;
            Double price = 0.0;

            for (int i = 0; i < User.cart.size(); ++i) {    //sepetin size kadar
                View view = getLayoutInflater().inflate(R.layout.sepet_urun_taslak, null);
                final ImageView iv = view.findViewById(R.id.productInCart);
                final CheckBox cb = view.findViewById(R.id.check);
                final TextView name = view.findViewById(R.id.nameOfProduct);
                final TextView description = view.findViewById(R.id.descriptionOfProduct);
                final TextView priceView = view.findViewById(R.id.priceOfProduct);
                final LinearLayout ll = view.findViewById(R.id.one);

                tmpProduct = tmpCart.poll();
                final Product tmp = tmpProduct;
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent1 = new Intent(getApplicationContext(),ProductScreen.class);
                        intent1.putExtra("pro",tmp);
                        startActivity(intent1);
                        finish();
                    }
                });
                ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent1 = new Intent(getApplicationContext(),ProductScreen.class);
                        intent1.putExtra("pro",tmp);
                        startActivity(intent1);
                        finish();
                    }
                });
                name.setText(tmpProduct.getName());
                description.setText(tmpProduct.getFeatures());
                priceView.setText(((Double)tmpProduct.getPrice()).toString() +" TL");
                price += tmpProduct.getPrice();
                pairCart.add(new Pair<>(cb, tmpProduct));
                if (tmpProduct.getImageCode().equals("default")) {
                    iv.setImageResource(R.drawable.varsayilan);
                } else {
                    Picasso.get()
                            .load(tmpProduct.getImageCode())
                            .resize(110, 130)
                            .into(iv);
                }
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(220, 320);
                iv.setLayoutParams(layoutParams);
                genel_Sepet.addView(view);
            }

            ((TextView) findViewById(R.id.tutar)).setText("Toplam Tutar: "+price.toString()+" TL");
            ImageButton remove = findViewById(R.id.sil);
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remove(genel_Sepet);
                }
            });
        }
        super.menuBar();
    }

    /**
     * Ürünler satın alındığında ürün sahiplerine mesaj gönderir.
     * @param view
     */
    public void purchase(View view){

        Integer x = System.productList.size();
        Log.d("satinal",x.toString());
        for(Product p : User.cart){
            String productName = p.getName();
            String seller = p.getSeller();
            String me = SignIn.whoami;
            String m1 = "\nMerhaba. "+p.getId()+" numarali ürününüzle ilgileniyorum."+ "Lütfen benimle "+me+" bu mailden iletişime geçiniz.\n" +
                    "Bu mesaj otomatik gönderilmiştir.";
            Chat.Message m = new Chat.Message(m1,"", SignIn.whoami,seller);
            m.setUser();
            m.setSend_time();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("Mesajlar").child(String.valueOf(seller.hashCode())).child(String.valueOf(SignIn.whoami.hashCode())).child(m.getSend_time()).setValue(m);

        }
    }


    /**
     * Sepetten ürün çıkarma işlemini yapıp toplam tutarı tekrar hesaplar.
     */
    public void remove(LinearLayout genel_Sepet){

        User.cart = new LinkedList<>();
        Double p = 0.0;
        int tmpSize = pairCart.size();
        for (int i = 0; i < tmpSize; ++i) {
            if(!pairCart.get(i).first.isChecked()) {
                User.cart.add(pairCart.get(i).second);
                p += pairCart.get(i).second.getPrice();
            }
            else {
                genel_Sepet.removeViewAt(i);
                pairCart.remove(i);
                --i;
                --tmpSize;
            }
        }
        ((TextView) findViewById(R.id.tutar)).setText("Toplam Tutar: "+p.toString()+" TL");

    }
}