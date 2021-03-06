package com.example.borja.marketingcomputacional.area_input.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.borja.marketingcomputacional.general.Attribute;
import com.example.borja.marketingcomputacional.general.CustomerProfile;
import com.example.borja.marketingcomputacional.GeneticAlgorithm.SubProfile;
import com.example.borja.marketingcomputacional.R;
import com.example.borja.marketingcomputacional.general.LinkedAttribute;
import com.example.borja.marketingcomputacional.general.StoredData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Borja on 03/04/2016.
 */
public class InputCustomerProfiles extends AppCompatActivity {

    static final int RESP_PER_GROUP = 20;
    private boolean isgenerated = false;
    private String POSITIVE = "Positiva";
    private String NEGATIVE = "Negativa";

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.input_customer_profiles);

        final TextView generate = (TextView) findViewById(R.id.generate_cust);
        final EditText num_cust = (EditText) findViewById(R.id.number_input_customers);
        final LinearLayout list_input_cust = (LinearLayout) findViewById(R.id.input_customer_list);
        final LinearLayout how_much_cust = (LinearLayout) findViewById(R.id.how_much_cust);

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (num_cust.getText().toString().length() > 0) {
                    how_much_cust.setVisibility(View.GONE);
                    int num_customers = Integer.parseInt(num_cust.getText().toString());

                    list_input_cust.removeAllViews();
                    for (int i = 0; i < num_customers; i++) {
                        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View input_cust_view = inflater.inflate(R.layout.customer_item_input, list_input_cust, false);

                        TextView name = (TextView) input_cust_view.findViewById(R.id.customer_name_input);
                        name.setText("Perfil " + (i + 1));


                        final LinearLayout list_customer_attributes_content = (LinearLayout) input_cust_view.findViewById(R.id.list_customer_attributes_content);
                        list_customer_attributes_content.removeAllViews();

                        for (int j = 0; j < StoredData.Atributos.size(); j++) {
                            View customer_attribute_item_input = inflater.inflate(R.layout.customer_attribute_item_input, list_customer_attributes_content, false);

                            TextView punt = (TextView) customer_attribute_item_input.findViewById(R.id.puntuations_for);
                            punt.setText("Puntuaciones para el Atributo " + (j + 1));


                            final LinearLayout list_valorations_content = (LinearLayout) customer_attribute_item_input.findViewById(R.id.list_valorations_content);
                            list_valorations_content.removeAllViews();

                            Attribute attr = StoredData.Atributos.get(j);
                            for (int k = 0; k < attr.getMAX(); k++) {
                                View valorations_item_input = inflater.inflate(R.layout.valorations_item_input, list_valorations_content, false);

                                TextView valor_input = (TextView) valorations_item_input.findViewById(R.id.valor_input);
                                valor_input.setText("Valor " + (k + 1));

                                Spinner valoration_spinner = (Spinner) valorations_item_input.findViewById(R.id.valorations_spinner);

                                List<String> valors = new ArrayList<>();
                                for (int p = 0; p < attr.getMAX(); p++)
                                    valors.add((p + 1) + "");

                                ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_simple_item, valors);
                                //Añadimos el layout para el menú y se lo damos al spinner
                                spinner_adapter.setDropDownViewResource(R.layout.spinner_simple_dropdown_item);
                                valoration_spinner.setAdapter(spinner_adapter);

                                list_valorations_content.addView(valorations_item_input);
                            }

                            list_customer_attributes_content.addView(customer_attribute_item_input);
                        }


                        //Linked attributes Variant
                        LinearLayout linked_attribute_variant_container = (LinearLayout) input_cust_view.findViewById(R.id.linked_attribute_variant_container);
                        if (StoredData.isAttributesLinked) {
                            linked_attribute_variant_container.setVisibility(View.VISIBLE);
                            TextView add_link = (TextView) linked_attribute_variant_container.findViewById(R.id.add_link);

                            final LinearLayout list_linked_attributes_container = (LinearLayout) linked_attribute_variant_container.findViewById(R.id.list_linked_attributes_container);
                            add_link.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    View input_linked_attribute_view = inflater.inflate(R.layout.input_linked_attribute, list_linked_attributes_container, false);

                                    List<String> valors = new ArrayList<>();
                                    for (int p = 0; p < StoredData.Atributos.size(); p++)
                                        valors.add((p + 1) + "");

                                    ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_simple_item, valors);
                                    Spinner spinner_link_atrr_1 = (Spinner) input_linked_attribute_view.findViewById(R.id.spinner_link_atrr_1);
                                    Spinner spinner_link_atrr_2 = (Spinner) input_linked_attribute_view.findViewById(R.id.spinner_link_atrr_2);
                                    final Spinner spinner_link_val_1 = (Spinner) input_linked_attribute_view.findViewById(R.id.spinner_link_val_1);
                                    final Spinner spinner_link_val_2 = (Spinner) input_linked_attribute_view.findViewById(R.id.spinner_link_val_2);
                                    spinner_link_atrr_1.setAdapter(spinner_adapter);
                                    spinner_link_atrr_2.setAdapter(spinner_adapter);

                                    spinner_link_atrr_1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                            List<String> valors = new ArrayList<>();
                                            for (int p = 0; p < StoredData.Atributos.get(position).getMAX(); p++)
                                                valors.add((p + 1) + "");

                                            ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_simple_item, valors);
                                            spinner_link_val_1.setAdapter(spinner_adapter);
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {

                                        }
                                    });
                                    spinner_link_atrr_2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                            List<String> valors = new ArrayList<>();
                                            for (int p = 0; p < StoredData.Atributos.get(position).getMAX(); p++)
                                                valors.add((p + 1) + "");

                                            ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_simple_item, valors);
                                            spinner_link_val_2.setAdapter(spinner_adapter);
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {

                                        }
                                    });

                                    List<String> sign_valors = new ArrayList<>();
                                    sign_valors.add(POSITIVE);
                                    sign_valors.add(NEGATIVE);
                                    ArrayAdapter<String> sign_adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_simple_item, sign_valors);
                                    Spinner sign_spinner = (Spinner) input_linked_attribute_view.findViewById(R.id.sign);
                                    sign_spinner.setAdapter(sign_adapter);

                                    list_linked_attributes_container.addView(input_linked_attribute_view);
                                }
                            });


                        } else {
                            linked_attribute_variant_container.setVisibility(View.GONE);
                        }

                        isgenerated = true;
                        list_input_cust.addView(input_cust_view);
                    }
                }
            }
        });


        TextView next = (TextView) findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isgenerated) {

                    boolean malformed = false;
                    ArrayList<CustomerProfile> custProfiles = new ArrayList<CustomerProfile>();
                    int num_customers = Integer.parseInt(num_cust.getText().toString());
                    for (int i = 0; i < num_customers; i++) {
                        CustomerProfile profile = new CustomerProfile();

                        EditText num_people = (EditText) list_input_cust.getChildAt(i).findViewById(R.id.number_people_profile);

                        ArrayList<Attribute> attrs = new ArrayList<>();
                        LinearLayout list_customer_attributes_content = (LinearLayout) list_input_cust.getChildAt(i).findViewById(R.id.list_customer_attributes_content);
                        for (int j = 0; j < StoredData.Atributos.size(); j++) {
                            LinearLayout list_valorations_content = (LinearLayout) list_customer_attributes_content.getChildAt(j).findViewById(R.id.list_valorations_content);


                            Attribute attr = new Attribute(StoredData.Atributos.get(j).getName(), StoredData.Atributos.get(j).getMIN(), StoredData.Atributos.get(j).getMAX());
                            ArrayList<Integer> scoreValues = new ArrayList<>();

                            for (int k = 0; k < attr.getMAX(); k++) {
                                Spinner valor = (Spinner) list_valorations_content.getChildAt(k).findViewById(R.id.valorations_spinner);
                                scoreValues.add(Integer.parseInt(valor.getSelectedItem().toString()));
                            }
                            attr.setScoreValues(scoreValues);
                            attrs.add(attr);
                        }

                        ArrayList<LinkedAttribute> linkedAttributes = new ArrayList<>();
                        if (StoredData.isAttributesLinked) {
                            final LinearLayout list_linked_attributes_container = (LinearLayout) list_input_cust.getChildAt(i).findViewById(R.id.list_linked_attributes_container);
                            for (int k = 0; k < list_linked_attributes_container.getChildCount(); k++) {
                                LinearLayout input_linked_attribute_view = (LinearLayout) list_linked_attributes_container.getChildAt(k);
                                LinkedAttribute link = new LinkedAttribute();

                                Spinner spinner_link_atrr_1 = (Spinner) input_linked_attribute_view.findViewById(R.id.spinner_link_atrr_1);
                                Spinner spinner_link_atrr_2 = (Spinner) input_linked_attribute_view.findViewById(R.id.spinner_link_atrr_2);
                                Spinner spinner_link_val_1 = (Spinner) input_linked_attribute_view.findViewById(R.id.spinner_link_val_1);
                                Spinner spinner_link_val_2 = (Spinner) input_linked_attribute_view.findViewById(R.id.spinner_link_val_2);

                                link.setAttribute1(StoredData.Atributos.get(Integer.parseInt(spinner_link_atrr_1.getSelectedItem().toString()) - 1));
                                link.setAttribute2(StoredData.Atributos.get(Integer.parseInt(spinner_link_atrr_2.getSelectedItem().toString()) - 1));
                                link.setValue1(Integer.parseInt(spinner_link_val_1.getSelectedItem().toString()));
                                link.setValue2(Integer.parseInt(spinner_link_val_2.getSelectedItem().toString()));

                                EditText modification_link = (EditText) input_linked_attribute_view.findViewById(R.id.modification_link);
                                if (modification_link.getText().toString().length() == 0) {
                                    malformed = true;
                                    break;
                                } else if (Integer.parseInt(modification_link.getText().toString()) < 1) {
                                    malformed = true;
                                    break;
                                } else {

                                    Spinner sign_spinner = (Spinner) input_linked_attribute_view.findViewById(R.id.sign);
                                    if (sign_spinner.getSelectedItem().equals(POSITIVE))
                                        link.setScoreModification(Integer.parseInt(modification_link.getText().toString()));
                                    else
                                        link.setScoreModification(-1 * Integer.parseInt(modification_link.getText().toString()));

                                    linkedAttributes.add(link);
                                }
                            }

                        }

                        if (num_people.getText().toString().length() == 0) {
                            malformed = true;
                            break;
                        } else if (Integer.parseInt(num_people.getText().toString()) < 1) {
                            malformed = true;
                            break;
                        } else {
                            profile.setScoreAttributes(attrs);
                            profile.setNumberCustomers(Integer.parseInt(num_people.getText().toString()));
                            profile.setLinkedAttributes(linkedAttributes);

                            custProfiles.add(profile);
                        }
                    }

                    if (!malformed) {
                        StoredData.Profiles = custProfiles;
                        try {
                            divideCustomerProfile();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Intent input_customers = new Intent(getApplicationContext(), InputProducers.class);
                        input_customers.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(input_customers);
                    } else
                        Toast.makeText(getApplicationContext(), "Debes rellenar bien los campos para continuar", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "Debes generar algun atributo para continuar", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Dividing the customer profiles into sub-profiles
     *
     * @throws Exception
     */
    private void divideCustomerProfile() throws Exception {


        int numOfSubProfile;
//        CustomerProfileListAux = new ArrayList<>();

        for (int i = 0; i < StoredData.Profiles.size(); i++) {
            ArrayList<SubProfile> subProfiles = new ArrayList<>();
            numOfSubProfile = StoredData.Profiles.get(i).getNumberCustomers() / RESP_PER_GROUP;

            if ((StoredData.Profiles.get(i).getNumberCustomers() % RESP_PER_GROUP) != 0)
                numOfSubProfile++;

            for (int j = 0; j < numOfSubProfile; j++) { //We divide into sub-profiles
                SubProfile subprofile = new SubProfile();
                subprofile.setName("Subperfil " + (j + 1));// + ", Perfil " + (i+1));

                HashMap<Attribute, Integer> valuesChosen = new HashMap<>();
                for (int k = 0; k < StoredData.Atributos.size(); k++) //Each of the sub-profiles choose a value for each of the attributes
                    valuesChosen.put(StoredData.Atributos.get(k), chooseValueForAttribute(StoredData.Profiles.get(i).getScoreAttributes().get(k)));

                subprofile.setValueChosen(valuesChosen);
                subProfiles.add(subprofile);
            }
            StoredData.Profiles.get(i).setSubProfiles(subProfiles);
        }
    }


    /**
     * Given an index of a customer profile and the index of an attribute we choose a value
     * for that attribute of the sub-profile having into account the values of the poll
     */
    private Integer chooseValueForAttribute(Attribute attribute) throws Exception {

        int total = 0;
        int accumulated = 0;
        boolean found = false;

        for (int i = 0; i < attribute.getScoreValues().size(); i++)
            total += attribute.getScoreValues().get(i);

        int rndVal = (int) (total * Math.random());

        int value = 0;
        while (!found) {
            accumulated += attribute.getScoreValues().get(value);
            if (rndVal <= accumulated)
                found = true;
            else
                value++;
            ;

            if (value >= attribute.getScoreValues().size())
                throw new Exception("Error 1 in chooseValueForAttribute() method: Value not found");

        }

        if (value >= attribute.getScoreValues().size())
            throw new Exception("Error 2 in chooseValueForAttribute() method: Value not found");

        return value;
    }
}
