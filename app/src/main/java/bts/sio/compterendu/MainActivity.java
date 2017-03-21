package bts.sio.compterendu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bt_ok = (Button) findViewById(R.id.valid_button);
        bt_ok.setOnClickListener(this);

        Button bt_annuler = (Button) findViewById(R.id.cancel_button);
        bt_annuler.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

       switch (v.getId()){
           case R.id.valid_button:
               TextView nb = (TextView) findViewById(R.id.nb_txt);
               Intent page_suivante = new Intent(getApplicationContext(),PanelVisiteurActivity.class);
               page_suivante.putExtra("nb_txt", nb.getText().toString() );
               startActivity(page_suivante);
               break;
           case R.id.cancel_button:
               //Button bt_ok = (Button) findViewById(R.id.valid_button);
               //bt_ok.setText("Ok");

               nb = (TextView) findViewById(R.id.nb_txt);
               nb.setText("");
               ((TextView) findViewById(R.id.password_txt)).setText("");
               Toast.makeText(getApplicationContext(),"annuler", Toast.LENGTH_SHORT).show();
               break;

       }

    }
}
