package com.grup15.gtuticaret;

import android.os.Bundle;

/**
 * Ayarlara geçiş yapar (İlerde eklenecek)
 */
public class Settings extends MenuBar {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        super.menuBar();
    }
}
