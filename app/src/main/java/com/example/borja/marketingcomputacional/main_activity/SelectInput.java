package com.example.borja.marketingcomputacional.main_activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.borja.marketingcomputacional.GeneticAlgorithm.GeneticAlgorithm;
import com.example.borja.marketingcomputacional.MinimaxAlgorithm.Minimax;
import com.example.borja.marketingcomputacional.R;
import com.example.borja.marketingcomputacional.area_input.fragments.InputAttributes;
import com.example.borja.marketingcomputacional.area_input.fragments.InputRandom;
import com.example.borja.marketingcomputacional.general.StoredData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by Borja on 10/04/2016.
 */
public class SelectInput extends AppCompatActivity implements View.OnClickListener {

    private static final int ACTIVITY_SELECT_IMAGE = 1020;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_input);

        LinearLayout random_data = (LinearLayout) findViewById(R.id.random_data);
        LinearLayout manual_data = (LinearLayout) findViewById(R.id.manual_data);
        LinearLayout file_data = (LinearLayout) findViewById(R.id.file_data);

        random_data.setOnClickListener(this);
        manual_data.setOnClickListener(this);
        file_data.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.random_data:
                InputRandom input = new InputRandom();
                try {
                    input.generate();

                    if (StoredData.Algorithm == StoredData.GENETIC) {
                        StoredData.GeneticAlgorithm = new GeneticAlgorithm();
                        StoredData.GeneticAlgorithm.start(getApplicationContext());
                    } else {
                        StoredData.Minimax = new Minimax();
                        StoredData.Minimax.start(getApplicationContext());
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Error al generar los datos", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.manual_data:

                Intent input_attrs = new Intent(getApplicationContext(), InputAttributes.class);
                input_attrs.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(input_attrs);
                break;
            case R.id.file_data:
                Toast.makeText(this, "En desarrollo", Toast.LENGTH_SHORT).show();
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("*/*");
                startActivityForResult(galleryIntent, ACTIVITY_SELECT_IMAGE);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_SELECT_IMAGE && resultCode == RESULT_OK) {
            Uri mImageUri = data.getData();
            readFile(mImageUri.getPath());
            Toast.makeText(this, "Activity result exit", Toast.LENGTH_SHORT).show();
        } else{

            Toast.makeText(this, "Activity result not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void readFile(String uri){

        try{
            OutputStreamWriter fout=
                    new OutputStreamWriter(
                            openFileOutput("prueba_int.txt", Context.MODE_PRIVATE));

            fout.write("Texto de prueba.");
            fout.close();
        }
        catch (Exception ex)
        {
            Log.e("Ficheros", "Error al escribir fichero a memoria interna");
        }

        try{
            BufferedReader fin =
                    new BufferedReader(
                            new InputStreamReader(
                                    openFileInput("prueba_int.txt")));

            String texto = fin.readLine();
            fin.close();
        }
        catch (Exception ex)
        {
            Log.e("Ficheros", "Error al leer fichero desde memoria interna");
        }

//        try{
//            BufferedReader fin = new BufferedReader(new InputStreamReader(openFileInput("MarkComp.txt")));
//            String texto = fin.readLine();
//            fin.close();
//
////            FileReader fis = new FileReader(new File(Environment.getExter
// nalStorageDirectory().getAbsolutePath() + uri));
////            BufferedReader fin2 = new BufferedReader(fis);
////            String texto = fin2.readLine();
////            fin2.close();
//        }
//        catch (Exception ex){
//            Log.e("Ficheros", ex.getMessage());
//        }
    }

}