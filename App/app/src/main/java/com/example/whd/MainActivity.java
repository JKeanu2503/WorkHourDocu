package com.example.whd;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView gui_TextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.gui_TextView = (TextView) this.findViewById(R.id.gui_TextView);
        int x = Integer.parseInt((String)this.gui_TextView.getText());

        char c = 'A';
        String s = "Auto liadufhglikydjfbngvlksdyjhbfnvlijkdfyhgblnkjfd \n ";

        Auto a = new Auto("DA E 2307", 160000);

        String ss = "X " + a.getKm_stand();
    }
}