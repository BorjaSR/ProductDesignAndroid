package com.example.borja.marketingcomputacional.main_activity;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.borja.marketingcomputacional.GeneticAlgorithm.GeneticAlgorithm;
import com.example.borja.marketingcomputacional.MinimaxAlgorithm.Minimax;
import com.example.borja.marketingcomputacional.R;
import com.example.borja.marketingcomputacional.area_input.fragments.InputAttributes;
import com.example.borja.marketingcomputacional.area_input.fragments.InputRandom;
import com.example.borja.marketingcomputacional.area_menu.fragments.DashboardProducersDetail;
import com.example.borja.marketingcomputacional.area_menu.ui.ProducerAdapter;
import com.example.borja.marketingcomputacional.general.StoredData;

/**
 * Created by Borja on 10/04/2016.
 */
public class SelectInput extends AppCompatActivity implements View.OnClickListener {

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
                break;
        }
    }
}