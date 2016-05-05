package com.example.borja.marketingcomputacional.area_input.fragments;

import android.widget.LinearLayout;

import com.example.borja.marketingcomputacional.general.Attribute;
import com.example.borja.marketingcomputacional.general.CustomerProfile;
import com.example.borja.marketingcomputacional.general.LinkedAttribute;
import com.example.borja.marketingcomputacional.general.Producer;
import com.example.borja.marketingcomputacional.general.Product;
import com.example.borja.marketingcomputacional.GeneticAlgorithm.SubProfile;
import com.example.borja.marketingcomputacional.general.StoredData;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Borja on 10/04/2016.
 */
public class InputRandom {

    private static final double PROB_ATTRIBUTE_LINKED = 10;
    private static ArrayList<Attribute> TotalAttributes = new ArrayList<>();
    private static ArrayList<Producer> Producers = new ArrayList<>();
    private static ArrayList<CustomerProfile> CustomerProfiles = new ArrayList<>();

    static final double KNOWN_ATTRIBUTES = 100; /* 100
                                                 * % of attributes known for all
												 * producers
												 */
    static final double SPECIAL_ATTRIBUTES = 33; /* 33
                                                 * % of special attributes known
												 * for some producers

												 						 */
    static final double MUT_PROB_CUSTOMER_PROFILE = 33; /*  * % of mutated
                                                         * attributes in a
														 * customer profile
														 */

    static final int RESP_PER_GROUP = 20; /* * We divide the respondents of each
                                             * profile in groups of
											 * RESP_PER_GROUP respondents
											 */
    static final int NEAR_CUST_PROFS = 4;

    public void generate() throws Exception {
        generateAttributeRandom();
        StoredData.Atributos = TotalAttributes;

        generateCustomerProfiles();
        generareNumberOfCustomers();
        divideCustomerProfile();
        StoredData.Profiles = CustomerProfiles;

        generateProducers();
        StoredData.Producers = Producers;
    }


    private void generateAttributeRandom() {
        TotalAttributes.clear();
        for (int i = 0; i < 70; i++) {

            double rnd = Math.random();
            if (rnd < 0.34)
                TotalAttributes.add(new Attribute("Atributo " + (i + 1), 1, 3));
            else if (rnd < 0.67)
                TotalAttributes.add(new Attribute("Atributo " + (i + 1), 1, 4));
            else
                TotalAttributes.add(new Attribute("Atributo " + (i + 1), 1, 5));
        }
    }

    /**
     * Creating different customer profiles
     */
    private static void generateCustomerProfiles() {
        CustomerProfiles.clear();

        //Generate 4 random Customer Profile
        for (int i = 0; i < 4; i++) {
            ArrayList<Attribute> attrs = new ArrayList<>();
            for (int j = 0; j < TotalAttributes.size(); j++) {
                Attribute attr = new Attribute(TotalAttributes.get(j).getName(), TotalAttributes.get(j).getMIN(), TotalAttributes.get(j).getMAX());

                ArrayList<Integer> scoreValues = new ArrayList<>();
                for (int k = 0; k < attr.MAX; k++) {
                    int random = (int) (attr.MAX * Math.random());
                    scoreValues.add(random);
                }
                attr.setScoreValues(scoreValues);
                attrs.add(attr);
            }
            CustomerProfile custProf = new CustomerProfile(attrs);

            if (StoredData.isAttributesLinked) {
                ArrayList<LinkedAttribute> linkedAttributes = new ArrayList<>();
                for (int k = 0; k < TotalAttributes.size(); k++) {
                    if (Math.random() < (PROB_ATTRIBUTE_LINKED / 100)) {
                        LinkedAttribute link = new LinkedAttribute();

                        link.setAttribute1(TotalAttributes.get(k));
                        link.setValue1((int) (link.getAttribute1().MAX * Math.random()));
                        link.setAttribute2(TotalAttributes.get((int) (TotalAttributes.size() * Math.random())));
                        link.setValue2((int) (link.getAttribute2().MAX * Math.random()));
                        link.setScoreModification((int) (-1 * (2 * (TotalAttributes.get(k).MAX * Math.random() + TotalAttributes.get(k).MAX * Math.random()))));

                        linkedAttributes.add(link);
                    }
                }
                custProf.setLinkedAttributes(linkedAttributes);
            }

            CustomerProfiles.add(custProf);
        }

        //Create 2 mutants for each basic profile
        for (int i = 0; i < 4; i++) {
            CustomerProfiles.add(mutateCustomerProfile(CustomerProfiles.get(i)));
            CustomerProfiles.add(mutateCustomerProfile(CustomerProfiles.get(i)));
        }

        //Creating 4 isolated profiles
        for (int i = 0; i < 4; i++) {
            ArrayList<Attribute> attrs = new ArrayList<>();
            for (int j = 0; j < TotalAttributes.size(); j++) {
                Attribute attr = new Attribute(TotalAttributes.get(j).getName(), TotalAttributes.get(j).getMIN(), TotalAttributes.get(j).getMAX());
                ArrayList<Integer> scoreValues = new ArrayList<>();
                for (int k = 0; k < attr.MAX; k++) {
                    int random = (int) (attr.MAX * Math.random());
                    scoreValues.add(random);
                }
                attr.setScoreValues(scoreValues);
                attrs.add(attr);
            }

            CustomerProfile custProf = new CustomerProfile(attrs);

            if (StoredData.isAttributesLinked) {
                ArrayList<LinkedAttribute> linkedAttributes = new ArrayList<>();
                for (int k = 0; k < TotalAttributes.size(); k++) {
                    if (Math.random() < (PROB_ATTRIBUTE_LINKED / 100)) {
                        LinkedAttribute link = new LinkedAttribute();

                        link.setAttribute1(TotalAttributes.get(k));
                        link.setValue1((int) (link.getAttribute1().MAX * Math.random()));
                        link.setAttribute2(TotalAttributes.get((int) (TotalAttributes.size() * Math.random())));
                        link.setValue2((int) (link.getAttribute2().MAX * Math.random()));
                        link.setScoreModification((int) (-1 * (2 * (TotalAttributes.get(k).MAX * Math.random() + TotalAttributes.get(k).MAX * Math.random()))));

                        linkedAttributes.add(link);
                    }
                }
                custProf.setLinkedAttributes(linkedAttributes);
            }

            CustomerProfiles.add(custProf);
        }
    }

    private static CustomerProfile mutateCustomerProfile(CustomerProfile customerProfile) {
        CustomerProfile mutant = new CustomerProfile(null);
        ArrayList<Attribute> attrs = new ArrayList<>();
        for (int i = 0; i < TotalAttributes.size(); i++) {
            Attribute attr = new Attribute(TotalAttributes.get(i).getName(), TotalAttributes.get(i).getMIN(), TotalAttributes.get(i).getMAX());
            ArrayList<Integer> scoreValues = new ArrayList<>();
            for (int k = 0; k < attr.MAX; k++) {
                if (Math.random() < (MUT_PROB_CUSTOMER_PROFILE / 100)) {
                    int random = (int) (attr.MAX * Math.random());
                    scoreValues.add(random);
                } else
                    scoreValues.add(customerProfile.getScoreAttributes().get(i).getScoreValues().get(k));
            }
            attr.setScoreValues(scoreValues);
            attrs.add(attr);
        }
        mutant.setScoreAttributes(attrs);
        mutant.setLinkedAttributes(customerProfile.getLinkedAttributes());
        return mutant;
    }


    private void generareNumberOfCustomers() {
        for (int i = 0; i < CustomerProfiles.size(); i++)
            CustomerProfiles.get(i).setNumberCustomers((int) ((Math.random() * 100) + (Math.random() * 100)));
    }


    /**
     * Dividing the customer profiles into sub-profiles
     *
     * @throws Exception
     */
    private void divideCustomerProfile() throws Exception {


        int numOfSubProfile;
//        CustomerProfileListAux = new ArrayList<>();

        for (int i = 0; i < CustomerProfiles.size(); i++) {
            ArrayList<SubProfile> subProfiles = new ArrayList<>();
            numOfSubProfile = CustomerProfiles.get(i).getNumberCustomers() / RESP_PER_GROUP;

            if ((CustomerProfiles.get(i).getNumberCustomers() % RESP_PER_GROUP) != 0)
                numOfSubProfile++;

            for (int j = 0; j < numOfSubProfile; j++) { //We divide into sub-profiles
                SubProfile subprofile = new SubProfile();
                subprofile.setName("Subperfil " + (j + 1));// + ", Perfil " + (i+1));

                HashMap<Attribute, Integer> valuesChosen = new HashMap<>();
                for (int k = 0; k < TotalAttributes.size(); k++) //Each of the sub-profiles choose a value for each of the attributes
                    valuesChosen.put(TotalAttributes.get(k), chooseValueForAttribute(CustomerProfiles.get(i).getScoreAttributes().get(k)));

                subprofile.setValueChosen(valuesChosen);
                subProfiles.add(subprofile);
            }
            CustomerProfiles.get(i).setSubProfiles(subProfiles);
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

    /**
     * Generating the producers
     */
    private static void generateProducers() {
        Producers.clear();

        Producers = new ArrayList<>();
        for (int i = 0; i < 10; i++) { //Creamos 10 productores random
            Producer new_producer = new Producer();
            new_producer.setName("Productor " + (i + 1));
            new_producer.setAvailableAttribute(createAvailableAttributes());
            new_producer.setProduct(createProduct(new_producer.getAvailableAttribute()));

            ArrayList<Product> products = new ArrayList<>();
            for (int k = 0; k < StoredData.number_Products; k++)
                products.add(createProduct(new_producer.getAvailableAttribute()));
            new_producer.setProducts(products);

            Producers.add(new_producer);
        }
    }

    /**
     * Creating available attributes for the producer
     */
    private static ArrayList<Attribute> createAvailableAttributes() {
        ArrayList<Attribute> availableAttributes = new ArrayList<>();
        int limit = (int) (TotalAttributes.size() * KNOWN_ATTRIBUTES / 100);

		/*All producers know the first ATTRIBUTES_KNOWN % of the attributes*/
        for (int i = 0; i < limit; i++) {
            Attribute attr = new Attribute(TotalAttributes.get(i).getName(), TotalAttributes.get(i).getMIN(), TotalAttributes.get(i).getMAX());
            ArrayList<Boolean> availablevalues = new ArrayList<>();
            for (int j = 0; j < attr.getMAX(); j++) {
                availablevalues.add(true);
            }

            attr.setAvailableValues(availablevalues);
            availableAttributes.add(attr);
        }

		/*The remaining attributes are only known by SPECIAL_ATTRIBUTES % producers*/
        for (int k = limit; k < TotalAttributes.size(); k++) {
            Attribute attr = new Attribute(TotalAttributes.get(k).getName(), TotalAttributes.get(k).getMIN(), TotalAttributes.get(k).getMAX());
            ArrayList<Boolean> availableValues = new ArrayList<>();

            for (int j = 0; j < attr.getMAX(); j++) {
                double rnd = Math.random();
                double rndVal = Math.random();
                /*Furthermore, with a 50% of probabilities it can know this attribute*/
                if (rndVal < (SPECIAL_ATTRIBUTES / 100) && rnd < 0.5)
                    availableValues.add(true);
                else
                    availableValues.add(false);
            }
            attr.setAvailableValues(availableValues);
            availableAttributes.add(attr);
        }

        return availableAttributes;
    }

    private static Product createProduct(ArrayList<Attribute> availableAttrs) {

        Product product = new Product(new HashMap<Attribute, Integer>());
        ArrayList<Integer> customNearProfs = new ArrayList<>();

        for (int i = 0; i < NEAR_CUST_PROFS; i++)
            customNearProfs.add((int) Math.floor(CustomerProfiles.size() * Math.random()));

        HashMap<Attribute, Integer> attrValues = new HashMap<>();

        for (int j = 0; j < TotalAttributes.size(); j++)
            attrValues.put(TotalAttributes.get(j), chooseAttribute(j, customNearProfs, availableAttrs)); //TotalAttributes.get(j) o availableAttrs.get(j)

        product.setAttributeValue(attrValues);

        if(StoredData.Algorithm == StoredData.PSO){
            product.setVelocity(new HashMap<Attribute, Double>());
            for (int i = 0; i < TotalAttributes.size(); i++) {
                double velocity = (((StoredData.VEL_HIGH - StoredData.VEL_LOW) * Math.random()) + StoredData.VEL_LOW);
                product.getVelocity().put(TotalAttributes.get(i), velocity);
            }
        }

        product.setPrice((int) (Math.random() * 400) + 100);
        return product;
    }


    /**
     * Chosing an attribute near to the customer profiles given
     */
    private static int chooseAttribute(int attrInd, ArrayList<Integer> custProfInd, ArrayList<Attribute> availableAttrs) {
        int attrVal;

        ArrayList<Integer> possibleAttr = new ArrayList<>();

        for (int i = 0; i < TotalAttributes.get(attrInd).getMAX(); i++) {
            /*We count the valoration of each selected profile for attribute attrInd value i*/
            int possible = 0;
            for (int j = 0; j < custProfInd.size(); j++) {
                possible += CustomerProfiles.get(custProfInd.get(j)).getScoreAttributes().get(attrInd).getScoreValues().get(i);
            }
            possibleAttr.add(possible);
        }
        attrVal = getMaxAttrVal(attrInd, possibleAttr, availableAttrs);

        return attrVal;
    }

    /**
     * Chosing the attribute with the maximum score for the customer profiles given
     */
    private static int getMaxAttrVal(int attrInd, ArrayList<Integer> possibleAttr, ArrayList<Attribute> availableAttr) {

        int attrVal = -1;
        double max = -1;

        for (int i = 0; i < possibleAttr.size(); i++) {
            if (availableAttr.get(attrInd).getAvailableValues().get(i) && possibleAttr.get(i) > max) {
                max = possibleAttr.get(i);
                attrVal = i;
            }
        }
        return attrVal;
    }
}
