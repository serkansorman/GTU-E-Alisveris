package com.grup15.gtuticaret;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * Her kullanıcının ürünü satıldığında gelen mesajlarının tutulduğu mesaj kutusu
 */
public class Inbox extends MenuBar {
    private ListView listView;
    //deneme amacli bi class ve array
    private ArrayList<String> msgName;
    public static String whichone = "empty";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        listView = findViewById(R.id.list_inbox);
        msgName = new ArrayList<>();
        //tikladiginde chat ekrani aciliyor
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                whichone = msgName.get(i);
                Log.d("inbox",whichone);
                Intent chat = new Intent(getApplicationContext(), Chat.class);
                startActivity(chat);
            }
        });

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Mesajlar").child(String.valueOf(SignIn.whoami.hashCode())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    msgName.add(ds.getKey());

                }
                CustomAdapter adapter = new CustomAdapter(msgName);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        super.menuBar();

    }




    public class CustomAdapter extends BaseAdapter {
        ArrayList<String> adapter_arr;

        public CustomAdapter(ArrayList<String> d){
            adapter_arr = d;
        }

        @Override
        public int getCount() {
            return adapter_arr.size();
        }

        @Override
        public Object getItem(int i) {
            return adapter_arr.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate(R.layout.inbox_custom, null);
            TextView textView_name = view.findViewById(R.id.textView_name_inbox);
            Typeface tf = Typeface.createFromAsset(getAssets(), "opensanss.ttf");
            textView_name.setTypeface(tf);
            textView_name.setText(adapter_arr.get(i));
            return view;
        }
    }




}
