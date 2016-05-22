package com.example.borja.marketingcomputacional.main_activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.borja.marketingcomputacional.GeneticAlgorithm.GeneticAlgorithmVariant;
import com.example.borja.marketingcomputacional.MinimaxAlgorithm.Minimax;
import com.example.borja.marketingcomputacional.ParticleSwarmOptimization.ParticleSwarmOptimization;
import com.example.borja.marketingcomputacional.R;
import com.example.borja.marketingcomputacional.SimulatedAnnealing.SimulatedAnnealing;
import com.example.borja.marketingcomputacional.area_input.fragments.InputAttributes;
import com.example.borja.marketingcomputacional.area_input.fragments.InputRandom;
import com.example.borja.marketingcomputacional.general.StoredData;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Borja on 10/04/2016.
 */
public class Configuration extends AppCompatActivity implements View.OnClickListener {

    private static final int ACTIVITY_SELECT_IMAGE = 1020;
    private Spinner variant_spinner;
    private Spinner input_spinner;

    private String Without = "Sin variante";
    private String linkedAttributes = "Atributos linkados";
    private String benefits = "Producto con mayores beneficios";
    private String many_products = "Varios productos por productor";

    private String random = "Aleatorio";
    private String write = "Introducir a mano";
    private String file = "Importar desde fichero";


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_input);

        variant_spinner = (Spinner) findViewById(R.id.variant_spinner);
        input_spinner = (Spinner) findViewById(R.id.input_spinner);


        List<String> variant_valors = new ArrayList<>();
        variant_valors.add(Without);
        variant_valors.add(linkedAttributes);
        variant_valors.add(benefits);

        if (StoredData.Algorithm == StoredData.GENETIC || StoredData.Algorithm == StoredData.PSO || StoredData.Algorithm == StoredData.SA)
            variant_valors.add(many_products);

        ArrayAdapter<String> variant_adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.initial_spinner_simple_item, variant_valors);
        variant_adapter.setDropDownViewResource(R.layout.initial_spinner_simple_dropdown_item);
        variant_spinner.setAdapter(variant_adapter);

        List<String> input_valors = new ArrayList<>();
        input_valors.add(random);
        input_valors.add(write);
//        input_valors.add(file);

        ArrayAdapter<String> input_adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.initial_spinner_simple_item, input_valors);
        input_adapter.setDropDownViewResource(R.layout.initial_spinner_simple_dropdown_item);
        input_spinner.setAdapter(input_adapter);

        LinearLayout start = (LinearLayout) findViewById(R.id.start);
        start.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:


                String variant = variant_spinner.getSelectedItem().toString();
                if (variant.equals(linkedAttributes)) {
                    StoredData.Fitness = StoredData.Customers;
                    StoredData.isAttributesLinked = true;
                    StoredData.number_Products = 1;

                } else if (variant.equals(benefits)) {
                    StoredData.Fitness = StoredData.Benefits;
                    StoredData.isAttributesLinked = false;
                    StoredData.number_Products = 1;

                } else if (variant.equals(many_products)) {
                    StoredData.number_Products = 3;
                    StoredData.Fitness = StoredData.Customers;
                    StoredData.isAttributesLinked = false;

                } else {
                    StoredData.Fitness = StoredData.Customers;
                    StoredData.isAttributesLinked = false;
                    StoredData.number_Products = 1;
                }


                String input_txt = input_spinner.getSelectedItem().toString();
                if (input_txt.equals(random)) {
                    InputRandom input = new InputRandom();
                    try {
                        input.generate();

                        if (StoredData.Algorithm == StoredData.GENETIC) {
                            StoredData.GeneticAlgorithmVariant = new GeneticAlgorithmVariant();
                            StoredData.GeneticAlgorithmVariant.start(getApplicationContext());

                        } else if (StoredData.Algorithm == StoredData.MINIMAX) {
                            StoredData.Minimax = new Minimax();
                            StoredData.Minimax.start(getApplicationContext());

                        } else if (StoredData.Algorithm == StoredData.PSO) {
                            StoredData.ParticleSwarmOptimization = new ParticleSwarmOptimization();
                            StoredData.ParticleSwarmOptimization.start(getApplicationContext());

                        } else if (StoredData.Algorithm == StoredData.SA) {
                            StoredData.SimulatedAnnealing = new SimulatedAnnealing();
                            StoredData.SimulatedAnnealing.start(getApplicationContext());
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else if (input_txt.equals(write)) {
                    Intent input_attrs = new Intent(getApplicationContext(), InputAttributes.class);
                    input_attrs.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(input_attrs);

                } else if (input_txt.equals(file)) {
                    Toast.makeText(this, "En desarrollo", Toast.LENGTH_SHORT).show();
                    Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("*/*");
                    startActivityForResult(galleryIntent, ACTIVITY_SELECT_IMAGE);
                }
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
        } else {

            Toast.makeText(this, "Activity result not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void readFile(String uri) {

        try {
            OutputStreamWriter fout =
                    new OutputStreamWriter(
                            openFileOutput("prueba_int.txt", Context.MODE_PRIVATE));

            fout.write("Texto de prueba.");
            fout.close();
        } catch (Exception ex) {
            Log.e("Ficheros", "Error al escribir fichero a memoria interna");
        }

        try {
            BufferedReader fin =
                    new BufferedReader(
                            new InputStreamReader(
                                    openFileInput("prueba_int.txt")));

            String texto = fin.readLine();
            fin.close();
        } catch (Exception ex) {
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