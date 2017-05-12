package bts.sio.compterendu;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import bts.sio.compterendu.helper.CRReaderDbHelper;
import bts.sio.compterendu.model.Account;

public class PanelActivity extends AppCompatActivity {

    private CRReaderDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visiteur);
        Intent intent = getIntent();
        int userId=intent.getIntExtra("userId",0);
        long timeMillis=intent.getLongExtra("limitConnect",0);
        final Calendar limitConnect= Calendar.getInstance();
        limitConnect.setTimeInMillis(timeMillis);
        mDbHelper=new CRReaderDbHelper(getApplicationContext());
        //INIT BDD READABLE
        final Account user = mDbHelper.getUser(getApplicationContext());
        if (!user.checkConnection(limitConnect)){
            Intent intentLogin=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intentLogin);
        }else {
            Date limitConnectReplace = new Date(); // Instantiate a Date object
            limitConnect.setTime(limitConnectReplace);
            limitConnect.add(Calendar.MINUTE, 5);
        }
        TextView btn_title = (TextView) findViewById(R.id.titlePanel);
        if (user.getFonction().equals("Visiteur")){
            btn_title.append(" "+user.getFonction());
        }
        if (user.getFonction().equals("Delegue")){
            btn_title.append(" "+user.getFonction());
        }
        if (user.getFonction().equals("Responsable")){
            btn_title.append(" "+user.getFonction());
        }
        Button bt_cr = (Button) findViewById(R.id.CR);
        bt_cr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentVisit = new Intent(getApplicationContext(),CrActivity.class);
                intentVisit.putExtra("userId",user.getId());
                intentVisit.putExtra("userConnect",limitConnect.getTimeInMillis());
                startActivity(intentVisit);
            }
        });
        Button bt_ac = (Button) findViewById(R.id.AC);
        bt_ac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentVisit = new Intent(getApplicationContext(),AcActivity.class);
                intentVisit.putExtra("userId",user.getId());
                intentVisit.putExtra("userConnect",limitConnect.getTimeInMillis());
                startActivity(intentVisit);
            }
        });
        Button bt_med = (Button) findViewById(R.id.Med);
        bt_med.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentVisit = new Intent(getApplicationContext(),ConsulterMedActivity.class);
                intentVisit.putExtra("userId",user.getId());
                intentVisit.putExtra("userConnect",limitConnect.getTimeInMillis());
                startActivity(intentVisit);
            }
        });
        Button bt_pra = (Button) findViewById(R.id.Prat);
        bt_pra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentVisit = new Intent(getApplicationContext(),ConsulterPraActivity.class);
                intentVisit.putExtra("userId",user.getId());
                intentVisit.putExtra("userConnect",limitConnect.getTimeInMillis());
                startActivity(intentVisit);
            }
        });
        Button bt_collab = (Button) findViewById(R.id.Collab);
        bt_collab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentVisit = new Intent(getApplicationContext(),ConsulterCollabActivity.class);
                intentVisit.putExtra("userId",user.getId());
                intentVisit.putExtra("userConnect",limitConnect.getTimeInMillis());
                startActivity(intentVisit);
            }
        });
        Button bt_deco = (Button) findViewById(R.id.btn_deco);
        bt_deco.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLogin=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intentLogin);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
