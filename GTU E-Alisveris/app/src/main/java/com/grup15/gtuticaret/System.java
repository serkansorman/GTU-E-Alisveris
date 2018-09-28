package com.grup15.gtuticaret;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.grup15.gtuticaret.Graph.ListGraph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * Sisteme ait güncel kullanıcı, yorumlar, kullanıcıya ait ürün önerileri
 * ve tüm ürünler tutulur.
 */
public abstract class System extends AppCompatActivity{
    static User currentUser;
    static Stack<Comment> comments;
    static ArrayList<Product> productList = new ArrayList<>();
    static ListGraph recommendations = new ListGraph();


    /**
     * Tüm yorumlar ekranda gösterilir.
     * @param context
     */
    public static void showAllComments(Context context) {

        Intent intent = new Intent(context, CommentsPage.class);
        context.startActivity(intent);
    }


    /**
     * Sisteme yeni ürün eklenir.
     * @param view
     */
    public void addProductToList(View view){

        Spinner spinner = findViewById(R.id.spinner1);
        EditText name = findViewById(R.id.name);
        EditText features = findViewById(R.id.description);
        EditText price = findViewById(R.id.editPrice);

        Product newProduct = new Product(name.getText().toString(),features.getText().toString(),
                String.valueOf(spinner.getSelectedItem()).toUpperCase(),Double.parseDouble(price.getText().toString()),System.productList.size()+1,"@drawable/varsayilan");


        System.productList.add(newProduct);
    }

    /**
     * Yorumlar eklenir.
     */
    public static void addComments(){

        //Kullanıcıya geçici yorumlar yerleştirildi.
        comments = new Stack<>();
        comments.add(new Comment("okula böyle adamlar lazım teşekkürler", "01/01/2017", "burhanElgun"));
        comments.add(new Comment("içinde tel olmayan jumper sattı", "04/05/2017", "ramazanGuvenc"));
        comments.add(new Comment("Aldığım tüm ürünlerinden memnunum", "19/09/2017", "emirhanKaragozoglu"));
        comments.add(new Comment("Paramı alıp kaçtı güvenmeyin", "12/02/2018", "tarikKilic"));
        comments.add(new Comment("Adamın dibi yok böyle insan", "24/04/2018", "akinCam"));
        comments.add(new Comment("GTU Alışveriş sağolsun sizin gibi dürüst satıcıların olduğunu görebildik. Dünya böyle satıcılar uğruna dönüyor", "30/04/2018", "Celal Can KAYA"));
    }
}
