package com.grup15.gtuticaret;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


/**
 * Tüm sayfaların üst kısmında bulunması gereken sepet ve menu butonlarını içeren bar
 */
public abstract class MenuBar extends AppCompatActivity {
    protected DrawerLayout mdrawerLayout;
    protected ActionBarDrawerToggle actionBarDrawerToggle;
    private StackTraceElement[] stackTraceElements;


    protected void menuBar(){
        mdrawerLayout = findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, mdrawerLayout, R.string.open, R.string.close);
        mdrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getColor(R.color.Black));
        stackTraceElements = Thread.currentThread().getStackTrace();

        NavigationView navigation = findViewById(R.id.toolbar);
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.navigation_menu:
                        if (!stackTraceElements[3].getClassName().equals(MainScreen.class.getName())) {
                            Intent menu = new Intent(getApplicationContext(), MainScreen.class);
                            startActivity(menu);
                        } else
                            mdrawerLayout.closeDrawers();
                        break;
                    case R.id.navigation_account:
                        if (!stackTraceElements[3].getClassName().equals(MyAccount.class.getName())) {
                            Intent hesap = new Intent(getApplicationContext(), MyAccount.class);
                            startActivity(hesap);
                        } else
                            mdrawerLayout.closeDrawers();
                        break;
                    case R.id.navigation_categories:
                        if (!stackTraceElements[3].getClassName().equals(Categories.class.getName())) {
                            Intent kategori = new Intent(getApplicationContext(), Categories.class);
                            startActivity(kategori);
                        } else
                            mdrawerLayout.closeDrawers();
                        break;
                    case R.id.navigation_message:
                        if (!stackTraceElements[3].getClassName().equals(Inbox.class.getName())) {
                            Intent inbox = new Intent(getApplicationContext(), Inbox.class);
                            startActivity(inbox);
                        } else
                            mdrawerLayout.closeDrawers();
                        break;
                    case R.id.navigation_setting:
                        if (!stackTraceElements[3].getClassName().equals(Settings.class.getName())) {
                            Intent ayarlar = new Intent(getApplicationContext(), Settings.class);
                            startActivity(ayarlar);
                        } else
                            mdrawerLayout.closeDrawers();
                        break;
                    case R.id.navigation_addProduct:
                        if (!stackTraceElements[3].getClassName().equals(AddProduct.class.getName())) {
                            Intent addProduct = new Intent(getApplicationContext(), AddProduct.class);
                            startActivity(addProduct);

                        } else
                            mdrawerLayout.closeDrawers();
                        break;

                }
                return false;
            }
        });
    }

    /**
     * Menu butonuna tıklanınca menuyu açar.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.basketmenu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    /**
     * Sepet butonuna tıklanınca sepete geçiş yapar.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;

        if (!stackTraceElements[3].getClassName().equals(Cart.class.getName())) {
            switch (item.getItemId()) {
                case R.id.basket:
                    Intent i = new Intent(this, Cart.class);
                    startActivity(i);
                    finish();
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}