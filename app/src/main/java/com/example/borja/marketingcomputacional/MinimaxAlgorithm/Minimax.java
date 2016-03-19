package com.example.borja.marketingcomputacional.MinimaxAlgorithm;

import com.example.borja.marketingcomputacional.GeneticAlgorithm.Attribute;
import com.example.borja.marketingcomputacional.GeneticAlgorithm.CustomerProfile;
import com.example.borja.marketingcomputacional.GeneticAlgorithm.Producer;
import com.example.borja.marketingcomputacional.GeneticAlgorithm.Product;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Borja on 19/03/2016.
 */
public class Minimax {

    private int MIN_ATTR = 2; // Minimum number of attributes values
    private int MAX_ATTR = 2; // Maximum number of attributes values

    private int MIN_VAL = 0; // Minimum valuation of an attribute
    private int MAX_VAL = 5; // Maximum valuation of an attribute

    private int MIN_NUM_CUST = 5; // Minimum number of customers of a profile
    private int MAX_NUM_CUST = 5; // Maximum number of customers of a profile

    private int NEAR_CUST_PROFS = 5; //  Number of near customer profiles to generate a product

    private double KNOWN_ATTRIBUTES = 100; // % of attributes known for all producers
    private double SPECIAL_ATTRIBUTES = 33; // % of special attributes known for some producers
    public double MUT_PROB_CUSTOMER_PROFILE = 33; // % of mutated attributes in a customer profile

    private int MAX_DEPTH_0 = 8; //Maximun depth of the minimax
    private int MAX_DEPTH_1 = 2; //Maximun depth of the minimax

    private int NUM_EXEC = 5;


    // INPUT VARIABLES
    private int mNAttrMod; // Number of attributes the producer can modify (D)
    private int mPrevTurns; // Number of previous turns to compute (tp)
    private int mNTurns; // Number of turns to play (tf)

    private int mNAttr; // Number of attributes
    private int mNProd; // Number of producers
    private int mNCustProf; // Number of customer profiles
    // Represents the list of attributes, its possible values, and its possible valuations:
    // mAttributes(i)(j) = valuation for attribute number i, value number j
    private ArrayList<Attribute> TotalAttributes;
    private ArrayList<Producer> Producers;
    // Represents the customer profiles:
    // mCustProf(i)(j)(k) = valuation for the customer type number i,
    // attribute number j, value k of attribute (each attribute can take k possible values)
    private ArrayList<CustomerProfile> CustomerProfiles;

    // Customers gathered by each producer during the previous mPrevTurns turns:
    private ArrayList<ArrayList<Integer>> mCustGathered;
    // Number of customers gathered by each producer during the previous mPrevTurns turns:
    private ArrayList<Integer> mNCustGathered;

    // STATISTICAL VARIABLES
    private ArrayList<Long> mResults;

    /************************
     * INITIAL METHOD
     **********************/

    public void start() {
        playPDG();
    }

    public void playPDG() {
        generateInput();
        //playGame();
    }

    public void generateInput() {
        mNAttrMod = 1;
        mPrevTurns = 5;
        mNTurns = 5;
        mNAttr = 10; // TODO: 10
        mNProd = 2;

        genAttrVal();
        genCustomerProfiles();
        genCustomerProfilesNum();
        genProducers();
    }

    public void genAttrVal() {
        TotalAttributes.clear();
        for (int i = 0; i < mNAttr; i++) {
            int valueMax = (int) (Math.floor((MAX_ATTR - MIN_ATTR + 1) * Math.random()) + MIN_ATTR);
            TotalAttributes.add(new Attribute("Atributo " + (i + 1), 1, valueMax));
        }
    }

    public void genCustomerProfiles(){
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
            CustomerProfiles.add(new CustomerProfile(attrs));
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
            CustomerProfiles.add(new CustomerProfile(attrs));
        }
    }

    private CustomerProfile mutateCustomerProfile(CustomerProfile customerProfile) {
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
        return mutant;
    }

    private void genCustomerProfilesNum() {
        for(int i = 0; i < CustomerProfiles.size(); i++){
            int number_customers = (int)(Math.floor(MAX_NUM_CUST - MIN_NUM_CUST + 1) * Math.random()) + MIN_NUM_CUST;
            CustomerProfiles.get(i).setNumberCustomers(number_customers);
        }
    }

    public void genProducers(){
        Producers.clear();

        Producers = new ArrayList<>();
        for (int i = 0; i < 10; i++) { //Creamos 10 productores random
            Producer new_producer = new Producer();
            new_producer.setName("Productor " + (i + 1));
            new_producer.setAvailableAttribute(createAvailableAttributes());
            new_producer.setProduct(createProduct(new_producer.getAvailableAttribute()));
            Producers.add(new_producer);
        }
    }

    private ArrayList<Attribute> createAvailableAttributes() {
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

    private Product createProduct(ArrayList<Attribute> availableAttrs) {

        Product product = new Product(new HashMap<Attribute, Integer>());
        ArrayList<Integer> customNearProfs = new ArrayList<>();

        for (int i = 0; i < NEAR_CUST_PROFS; i++)
            customNearProfs.add((int) Math.floor(CustomerProfiles.size() * Math.random()));

        HashMap<Attribute, Integer> attrValues = new HashMap<>();

        for (int j = 0; j < TotalAttributes.size(); j++)
            attrValues.put(TotalAttributes.get(j), chooseAttribute(j, customNearProfs, availableAttrs)); //TotalAttributes.get(j) o availableAttrs.get(j)

        product.setAttributeValue(attrValues);
        return product;
    }

    /**
     * Chosing an attribute near to the customer profiles given
     */
    private int chooseAttribute(int attrInd, ArrayList<Integer> custProfInd, ArrayList<Attribute> availableAttrs) {
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
    private int getMaxAttrVal(int attrInd, ArrayList<Integer> possibleAttr, ArrayList<Attribute> availableAttr) {

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
