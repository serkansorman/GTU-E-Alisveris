package com.grup15.gtuticaret;


import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Gerekli methodların implement edilmesini gerektiren interface
 */
public interface UserInterface {

    boolean addToCart(Product newProduct);
    void comment(ArrayList<TextView> comment,EditText editComment);
}
