package com.example.borja.marketingcomputacional.area_input.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.borja.marketingcomputacional.general.Attribute;
import com.example.borja.marketingcomputacional.R;
import com.example.borja.marketingcomputacional.general.StoredData;

import java.util.ArrayList;

/**
 * Created by Borja on 03/04/2016.
 */
public class InputAttributes extends AppCompatActivity {

    private boolean isgenerated = false;

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

                if (number_attr.getText().toString().length() > 0) {
                    how_much_attr.setVisibility(View.GONE);
                    int num_attr = Integer.parseInt(number_attr.getText().toString());

                    list_input_attr.removeAllViews();
                    for (int i = 0; i < num_attr; i++) {
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View input_attr_view = inflater.inflate(R.layout.attribute_item_input, list_input_attr, false);

                        TextView name = (TextView) input_attr_view.findViewById(R.id.attri_name_input);
                        TextView how_much = (TextView) input_attr_view.findViewById(R.id.how_much);

                        name.setText("Atributo " + (i + 1));
                        how_much.setText("¿Cuantos valores tendra el atributo " + (i + 1) + "?");

                        list_input_attr.addView(input_attr_view);
                        isgenerated = true;
                    }
                }
            }
        });


        TextView next = (TextView) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(isgenerated){

                    boolean malformed = false;
                    ArrayList<Attribute> attrs = new ArrayList<Attribute>();
                    int num_attr = Integer.parseInt(number_attr.getText().toString());
                    for(int i = 0; i < num_attr; i++){
                        EditText num_val = (EditText)list_input_attr.getChildAt(i).findViewById(R.id.number_valors);
                        if(num_val.getText().toString().length() == 0){
                            malformed = true;
                            break;
                        }else if(Integer.parseInt(num_val.getText().toString()) < 1){
                            malformed = true;
                            break;
                        }else{
                            attrs.add(new Attribute("Atributo " + (i + 1), 1, Integer.parseInt(num_val.getText().toString())));
                        }
                    }

                    if(!malformed){
                        StoredData.Atributos = attrs;

                        Intent input_customers = new Intent(getApplicationContext(), InputCustomerProfiles.class);
                        input_customers.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(input_customers);
                    }else
                        Toast.makeText(getApplicationContext(), "Debes rellenar bien los campos para continuar", Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(getApplicationContext(), "Debes generar algun atributo para continuar", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
