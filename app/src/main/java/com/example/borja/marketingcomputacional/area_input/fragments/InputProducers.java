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
import android.widget.Toast;

import com.example.borja.marketingcomputacional.GeneticAlgorithm.GeneticAlgorithm;
import com.example.borja.marketingcomputacional.ParticleSwarmOptimization.ParticleSwarmOptimizationProblem;
import com.example.borja.marketingcomputacional.SimulatedAnnealing.SimulatedAnnealingProblem;
import com.example.borja.marketingcomputacional.general.Attribute;
import com.example.borja.marketingcomputacional.general.Producer;
import com.example.borja.marketingcomputacional.general.Product;
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
        final EditText number_input_products = (EditText) findViewById(R.id.number_input_products);
        final LinearLayout input_producers_list = (LinearLayout) findViewById(R.id.input_producers_list);

        final LinearLayout number_of_products_container = (LinearLayout) findViewById(R.id.number_of_products_container);
        if (StoredData.number_Products == 1) {
            number_of_products_container.setVisibility(View.GONE);
            number_input_products.setText("1");
        } else
            number_of_products_container.setVisibility(View.VISIBLE);

        final LinearLayout how_much_prod = (LinearLayout) findViewById(R.id.how_much_prod);


        generate_producers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (number_input_producers.getText().toString().length() > 0 && number_input_products.getText().toString().length() > 0) {
                    if (Integer.parseInt(number_input_producers.getText().toString()) > 1 && Integer.parseInt(number_input_products.getText().toString()) > 0) {
                        how_much_prod.setVisibility(View.GONE);
                        int num_producers = Integer.parseInt(number_input_producers.getText().toString());
                        StoredData.number_Products = Integer.parseInt(number_input_products.getText().toString());

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

                                Attribute attr = StoredData.Atributos.get(j);

                                final LinearLayout product_attr_by_product_container = (LinearLayout) producer_attribute_item_input.findViewById(R.id.product_attr_by_product_container);
                                product_attr_by_product_container.removeAllViews();
                                for (int l = 0; l < StoredData.number_Products; l++) {
                                    View producer_attr_by_product = inflater.inflate(R.layout.producer_attr_by_product, product_attr_by_product_container, false);

                                    TextView punt = (TextView) producer_attr_by_product.findViewById(R.id.wich_value);
                                    punt.setText("¿Que valor toma el atributo " + (j + 1) + " para el producto " + (l + 1) + "?");

                                    Spinner value_for_attribute = (Spinner) producer_attr_by_product.findViewById(R.id.value_for_attribute);

                                    List<String> valors = new ArrayList<>();
                                    for (int p = 0; p < attr.getMAX(); p++)
                                        valors.add((p + 1) + "");

                                    ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_simple_item, valors);
                                    //Añadimos el layout para el menú y se lo damos al spinner
                                    spinner_adapter.setDropDownViewResource(R.layout.spinner_simple_dropdown_item);
                                    value_for_attribute.setAdapter(spinner_adapter);

                                    product_attr_by_product_container.addView(producer_attr_by_product);
                                }

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
                    } else {
                        Toast.makeText(getApplicationContext(), "Algun campo tiene un numero demasiado pequeño", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Debes introducir algo valido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView next = (TextView) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<Producer> producers = new ArrayList<>();
                int num_producers = Integer.parseInt(number_input_producers.getText().toString());
                for (int i = 0; i < num_producers; i++) {

                    ArrayList<Attribute> available_attrs = new ArrayList<>();
//                    Product product = new Product();

                    ArrayList<Product> Products = new ArrayList<>();
                    ArrayList<HashMap<Attribute, Integer>> attributeValue_ALL_PRODUCTS = new ArrayList<>();

                    for (int l = 0; l < StoredData.number_Products; l++) {
                        Products.add(new Product());
                        attributeValue_ALL_PRODUCTS.add(new HashMap<Attribute, Integer>());
                    }

                    LinearLayout list_customer_attributes_content = (LinearLayout) input_producers_list.getChildAt(i).findViewById(R.id.list_producer_attributes_content);
                    for (int j = 0; j < StoredData.Atributos.size(); j++) {

                        LinearLayout product_attr_by_product_container = (LinearLayout) list_customer_attributes_content.getChildAt(j).findViewById(R.id.product_attr_by_product_container);
                        for (int l = 0; l < StoredData.number_Products; l++) {

                            Spinner valor = (Spinner) product_attr_by_product_container.getChildAt(l).findViewById(R.id.value_for_attribute);
                            attributeValue_ALL_PRODUCTS.get(l).put(StoredData.Atributos.get(j), Integer.parseInt(valor.getSelectedItem().toString()));
                        }

                        Attribute attr = new Attribute(StoredData.Atributos.get(j).getName(), StoredData.Atributos.get(j).getMIN(), StoredData.Atributos.get(j).getMAX());

                        ArrayList<Boolean> availableValues = new ArrayList<>();

                        LinearLayout list_valorations_content = (LinearLayout) list_customer_attributes_content.getChildAt(j).findViewById(R.id.list_valorations_content);
                        for (int k = 0; k < attr.getMAX(); k++) {
                            CheckBox check = (CheckBox) list_valorations_content.getChildAt(k).findViewById(R.id.available_value);

                            if (check.isChecked())
                                availableValues.add(true);
                            else
                                availableValues.add(false);
                        }

                        attr.setAvailableValues(availableValues);
                        available_attrs.add(attr);
                    }

                    Producer producer = new Producer();
                    producer.setName("Productor " + (i + 1));
                    producer.setAvailableAttribute(available_attrs);

                    if (StoredData.number_Products == 1) {
                        Products.get(0).setAttributeValue(attributeValue_ALL_PRODUCTS.get(0));
                        producer.setProduct(Products.get(0));

                    } else {
                        ArrayList<Product> products = new ArrayList<>();
                        for (int q = 0; q < StoredData.number_Products; q++) {
                            Products.get(q).setAttributeValue(attributeValue_ALL_PRODUCTS.get(q));
                            products.add(Products.get(q));
                        }
                        producer.setProduct(products.get(0));
                        producer.setProducts(products);
                    }
                    producers.add(producer);
                }

                StoredData.Producers = producers;


                try {
                    if (StoredData.Algorithm == StoredData.GENETIC) {
                        GeneticAlgorithm.getInstance().start(getApplicationContext());

                    } else if (StoredData.Algorithm == StoredData.MINIMAX) {
                        Minimax.getInstance().start(getApplicationContext());

                    } else if (StoredData.Algorithm == StoredData.PSO) {
                        try {
                            ParticleSwarmOptimizationProblem.getInstance().start(getApplicationContext());
                        } catch (Exception e) {
                            e.printStackTrace();
                            ParticleSwarmOptimizationProblem.getInstance().start(getApplicationContext());
                        }
                    } else if (StoredData.Algorithm == StoredData.SA) {
                        SimulatedAnnealingProblem.getInstance().start(getApplicationContext());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
