package com.github.openeet.openeet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import openeet.lite.EetSaleDTO;

public class MainActivity extends AppCompatActivity {
    private static final String LOGTAG="MainActivity";

    private static String[] STRING_ARRAY0 = new String[]{};
    private static EetSaleDTO[] EET_SALE_DTO0 = new EetSaleDTO[]{};

    private static final int REGISTER_SALE=0;

    final protected List<EetSaleDTO> list = new ArrayList<EetSaleDTO>();

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //listProviders();
        broadcastReceiver=new MainBroadcastReceiver(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        updateList();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Adding item", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                //list.add(new Date().toString());
                //updateList(list);
                Intent registerSaleIntent = new Intent(MainActivity.this, RegisterSale.class);
                startActivityForResult(registerSaleIntent,REGISTER_SALE);
            }
        });
    }

    private void listProviders() {
        try {
            Provider p[] = Security.getProviders();
            for (int i = 0; i < p.length; i++) {
                Log.d(LOGTAG,p[i].toString());
                for (Enumeration e = p[i].keys(); e.hasMoreElements();)
                    Log.d(LOGTAG,"      " + e.nextElement().toString());
            }
        } catch (Exception e) {
            Log.e(LOGTAG,"error listing providers/algs");
        }
    }

    protected void updateList() {
        SaleService.SaleEntry[] items=SaleService.getInstance().getLastRegistered();
        ArrayAdapter<SaleService.SaleEntry> adapter = new SaleListArrayAdapter(this ,items);
        ListView salesList = (ListView) findViewById(R.id.salesList);
        salesList.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REGISTER_SALE: processRegisterSaleResult(resultCode,data);break;
        }
    }

    protected void processRegisterSaleResult(int resultCode, Intent data){
        if (resultCode==RESULT_OK && data!=null) {
            EetSaleDTO dtoSale = (EetSaleDTO) data.getSerializableExtra(RegisterSale.RESULT);
            new RegisterSaleTask(getApplicationContext()).execute(dtoSale);
        }
    }

    @Override
    protected void onStart() {
        Log.d(LOGTAG,"onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d(LOGTAG,"onStop");
        super.onStop();
    }

    @Override
    protected void onResume() {
        Log.d(LOGTAG,"onResume");
        super.onResume();
        registerBroadcastReceivers();
    }

    @Override
    protected void onPause() {
        Log.d(LOGTAG,"onPause");
        super.onPause();
        unregisterBroadcastReceivers();
    }

    private void unregisterBroadcastReceivers() {
        Log.d(LOGTAG,"unregister br. recv");
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);
    }

    private void registerBroadcastReceivers() {
        Log.d(LOGTAG,"register br. recv");
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver,RegisterSaleTask.getMatchAllFilter());
    }

    public void processBroadcast(Context context, Intent intent) {
        Log.d(LOGTAG,"onReceive: "+intent.getAction());
        updateList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_retry_register:
                new RetryRegisterSalesTask(getBaseContext()).execute("");
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}
