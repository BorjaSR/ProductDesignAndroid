package com.example.borja.marketingcomputacional.area_input.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.borja.marketingcomputacional.GeneticAlgorithm.Attribute;
import com.example.borja.marketingcomputacional.GeneticAlgorithm.GeneticAlgorithm;
import com.example.borja.marketingcomputacional.GeneticAlgorithm.Producer;
import com.example.borja.marketingcomputacional.GeneticAlgorithm.Product;
import com.example.borja.marketingcomputacional.MinimaxAlgorithm.Minimax;
import com.example.borja.marketingcomputacional.R;
import com.example.borja.marketingcomputacional.general.StoredData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Borja on 06/04/2016.
 */
public class InputProducers extends AppCompatActivity {

    boolean isgenerated;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.input_producers);

        final TextView generate_producers = (TextView) findViewById(R.id.generate_producers);
        final EditText number_input_producers = (EditText) findViewById(R.id.number_input_producers);
        final LinearLayout input_producers_list = (LinearLayout) findViewById(R.id.input_producers_list);
        final LinearLayout how_much_prod = (LinearLayout) findViewById(R.id.how_much_prod);


        generate_producers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (number_input_producers.getText().toString().length() > 0) {
                    how_much_prod.setVisibility(View.GONE);
                    int num_producers = Integer.parseInt(number_input_producers.getText().toString());

                    input_producers_list.removeAllViews();
                    for (int i = 0; i < num_producers; i++) {
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View input_cust_view = inflater.inflate(R.layout.producer_item_input, input_producers_list, false);

                        TextView name = (TextView) input_cust_view.findViewById(R.id.producer_name_input);
                        name.setText("Productor " + (i + 1));


                        final LinearLayout list_producer_attributes_content = (LinearLayout) input_cust_view.findViewById(R.id.list_producer_attributes_content);
                        list_producer_attributes_content.removeAllViews();

                        for (int j = 0; j < StoredData.Atributos.size(); j++) {
                            View producer_attribute_item_input = inflater.inflate(R.layout.producer_attribute_item_input, list_producer_attributes_content, false);

                            TextView punt = (TextView) producer_attribute_item_input.findViewById(R.id.wich_value);
                            punt.setText("¿Que valor toma el atributo " + (j + 1) + "?");


                            Attribute attr = StoredData.Atributos.get(j);
                            Spinner value_for_attribute = (Spinner) producer_attribute_item_input.findViewById(R.id.value_for_attribute);

                            List<String> valors = new ArrayList<>();
                            for (int p = 0; p < attr.getMAX(); p++)
                                valors.add((p + 1) + "");

                            ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_simple_item, valors);
                            //Añadimos el layout para el menú y se lo damos al spinner
                            spinner_adapter.setDropDownViewResource(R.layout.spinner_simple_dropdown_item);
                            value_for_attribute.setAdapter(spinner_adapter);


                            final LinearLayout list_valorations_content = (LinearLayout) producer_attribute_item_input.findViewById(R.id.list_valorations_content);
                            list_valorations_content.removeAllViews();

                            for (int k = 0; k < attr.getMAX(); k++) {
                                View available_values_input = inflater.inflate(R.layout.available_values_input, list_valorations_content, false);

                                TextView valor_input = (TextView) available_values_input.findViewById(R.id.valor_input);
                                valor_input.setText("Valor " + (k + 1));

                                list_valorations_content.addView(available_values_input);
                            }

                            list_producer_attributes_content.addView(producer_attribute_item_input);
                        }

                        isgenerated = true;
                        input_producers_list.addView(input_cust_view);
                    }
                }
            }
        });

        TextView next = (TextView) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<Producer> producers = new ArrayList<>();
                int num_producers = Integer.parseInt(number_input_producers.getText().toString());
                for (int i = 0; i < num_producers; i++){

                    ArrayList<Attribute> available_attrs = new ArrayList<>();
                    Product product = new Product();

                    HashMap<Attribute, Integer> attr_value = new HashMap<>();

                    LinearLayout list_customer_attributes_content = (LinearLayout) input_producers_list.getChildAt(i).findViewById(R.id.list_producer_attributes_content);
                    for (int j = 0; j < StoredData.Atributos.size(); j++) {

                        Spinner valor = (Spinner) list_customer_attributes_content.getChildAt(j).findViewById(R.id.value_for_attribute);
                        attr_value.put(StoredData.Atributos.get(j), Integer.parseInt(valor.getSelectedItem().toString()));

                        Attribute attr = new Attribute(StoredData.Atributos.get(j).getName(), StoredData.Atributos.get(j).getMIN(), StoredData.Atributos.get(j).getMAX());

                        ArrayList<Boolean> availableValues = new ArrayList<Boolean>();

                        LinearLayout list_valorations_content = (LinearLayout)list_customer_attributes_content.getChildAt(j).findViewById(R.id.list_valorations_content);
                        for (int k = 0; k < attr.getMAX(); k++) {
                            CheckBox check = (CheckBox) list_valorations_content.getChildAt(k).findViewById(R.id.available_value);

                            if(check.isChecked())
                                availableValues.add(true);
                            else
                                availableValues.add(false);
                        }

                        attr.setAvailableValues(availableValues);
                        available_attrs.add(attr);
                    }

                    product.setAttributeValue(attr_value);
                    Producer producer = new Producer(available_attrs, product);
                    producer.setName("Productor " + (i + 1));
                    producers.add(new Producer(available_attrs, product));

                }

                StoredData.Producers = producers;

                if (StoredData.Algorithm == StoredData.GENETIC) {
                    try {
                        StoredData.GeneticAlgorithm = new GeneticAlgorithm();
                        StoredData.GeneticAlgorithm.start(getApplicationContext());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
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
