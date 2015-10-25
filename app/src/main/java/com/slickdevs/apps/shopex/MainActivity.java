package com.slickdevs.apps.shopex;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.IntentIntegrator;
import com.google.zxing.integration.IntentResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    //private TextView textInput;
    private ImageButton startButton;
    private Spinner spinner;
    private ArrayAdapter<CharSequence> adapter;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private final int CAMERA_REQUEST = 1888;
    private TextView textMsg;
    private String mode="VOICE";
    private ListView cartList;
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter1;
    int count=0;


    public static Map<String,String> barMap = InventoryList.getBarList();
    public static List<String> voiceList = InventoryList.getVoiceList();
    public static List<String> imageList = InventoryList.getImageList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter1.notifyDataSetChanged();
                if(listItems.size()!=0) {
                    Snackbar.make(view, "Check out successful", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else{
                    Snackbar.make(view, "Cart is empty", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                listItems.clear();
            }
        });

        //textInput = (TextView) findViewById(R.id.textInput);
        startButton = (ImageButton) findViewById(R.id.startButton);
        textMsg = (TextView) findViewById(R.id.textMsg);
        cartList = (ListView) findViewById(R.id.cartList);

        spinner = (Spinner) findViewById(R.id.shopping_mode_list);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.shopping_mode_list, R.layout.spinner_layout);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        adapter1 = new ArrayAdapter<String>(this,R.layout.list_item_style,
                listItems);
        cartList.setAdapter(adapter1);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode.equals("VOICE")) {
                    promptSpeechInput();
                } else if (mode.equals("BAR")) {
                    IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
                    scanIntegrator.initiateScan();
                } else if (mode.equals("CAM")) {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });
    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //textInput.setText(result.get(0));
                    for(String item:voiceList){
                        if(result.get(0).toUpperCase().contains(item.toUpperCase())) {
                            addItems(item);
                            return;
                        }
                    }
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Item not found!", Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "No voice input received!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            }
            case IntentIntegrator.REQUEST_CODE:{
                IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if (scanningResult != null) {
                    String scanContent = scanningResult.getContents();

                    if(barMap.get(scanContent)!=null){
                        addItems(barMap.get(scanContent));
                    }else{
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Item not found!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "No scan data received!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            }
            case CAMERA_REQUEST:
                if(resultCode == RESULT_OK && null != data) {
                   addItems(imageList.get(count));
                    count++;
                    if(count==3){
                        count=0;
                    }
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "No image data received!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
            }
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
            if (pos == 0) {
                startButton.setImageResource(R.drawable.ico_mic);
                textMsg.setText(R.string.tap_on_mic);
                mode="VOICE";
            } else if (pos == 1) {
                startButton.setImageResource(R.drawable.ico_bar);
                textMsg.setText(R.string.tap_on_bar);
                mode="BAR";
            } else if (pos == 2) {
                startButton.setImageResource(R.drawable.ico_cam);
                textMsg.setText(R.string.tap_on_cam);
                mode="CAM";
            }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void addItems(String element) {
        listItems.add(0,element);
        adapter1.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_clear:
                listItems.clear();
                adapter1.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
