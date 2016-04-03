package com.example.borja.marketingcomputacional.area_input.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.borja.marketingcomputacional.GeneticAlgorithm.GeneticAlgorithm;
import com.example.borja.marketingcomputacional.MinimaxAlgorithm.Minimax;
import com.example.borja.marketingcomputacional.R;
import com.example.borja.marketingcomputacional.general.StoredData;

/**
 * Created by Borja on 03/04/2016.
 */
public class InputAttributes extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.input_attributes);

        TextView generate = (TextView) findViewById(R.id.generate_attr);
        final EditText number_attr = (EditText) findViewById(R.id.number_input_attributes);
        final LinearLayout list_input_attr = (LinearLayout) findViewById(R.id.input_attribute_list);
        final LinearLayout how_much_attr = (LinearLayout) findViewById(R.id.how_much_attr);

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(number_attr.getText().toString().length() > 0){
                    how_much_attr.setVisibility(View.GONE);
                    int num_attr = Integer.parseInt(number_attr.getText().toString());

                    list_input_attr.removeAllViews();
                    for(int i = 0; i < num_attr; i++){
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View input_attr_view = inflater.inflate(R.layout.attribute_item_input, null, false);

                        TextView name = (TextView) input_attr_view.findViewById(R.id.attri_name_input);
                        TextView how_much = (TextView) input_attr_view.findViewById(R.id.how_much);

                        name.setText("Atributo " + (num_attr + 1));
                        how_much.setText("¿Cuantos valores tendra el atributo " + (num_attr + 1) + "?");

                        list_input_attr.addView(input_attr_view);
                    }
                }
            }
        });


        TextView next = (TextView) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(StoredData.Algorithm == StoredData.GENETIC){
                    try {
                        StoredData.GeneticAlgorithm = new GeneticAlgorithm();
                        StoredData.GeneticAlgorithm.start(getApplicationContext());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        StoredData.Minimax = new Minimax();
                        StoredData.Minimax.start(getApplicationContext());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
