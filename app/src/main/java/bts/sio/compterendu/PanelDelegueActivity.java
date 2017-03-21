package bts.sio.compterendu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class PanelDelegueActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delegue);

        Intent page = getIntent();
        String nb = page.getStringExtra("nb_txt");

        TextView txt = (TextView) findViewById(R.id.textView2);
        txt.setText(nb);
    }
}
