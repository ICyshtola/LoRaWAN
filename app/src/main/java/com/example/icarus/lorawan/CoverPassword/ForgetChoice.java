package com.example.icarus.lorawan.CoverPassword;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.icarus.lorawan.R;

public class ForgetChoice extends AppCompatActivity {

    private Button use_email;
    private Button use_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_choice);
        use_email = (Button) findViewById(R.id.use_email);
        use_phone = (Button) findViewById(R.id.use_phone);
        use_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgetChoice.this, find_secret.class);
                startActivity(intent);
            }
        });
        use_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgetChoice.this, find_secret_mess.class);
                startActivity(intent);
            }
        });
    }
}
