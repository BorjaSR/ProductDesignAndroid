package com.example.borja.marketingcomputacional.MinimaxAlgorithm;

import android.content.Context;
import android.content.Intent;

import com.example.borja.marketingcomputacional.general.Attribute;
import com.example.borja.marketingcomputacional.general.CustomerProfile;
import com.example.borja.marketingcomputacional.general.Producer;
import com.example.borja.marketingcomputacional.general.Product;
import com.example.borja.marketingcomputacional.area_menu.fragments.DashboardMenu;
import com.example.borja.marketingcomputacional.general.StoredData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Borja on 19/03/2016.
 */
public class Minimax {

    private int MY_PRODUCER = 0;

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

    private int MAX_DEPTH_0 = 4; //Maximun depth of the minimax //depth 8 in initial
    private int MAX_DEPTH_1 = 2; //Maximun depth of the minimax //depth 2 in initial

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
    private ArrayList<Attribute> TotalAttributes = new ArrayList<>();
    private ArrayList<Producer> Producers = new ArrayList<>();
    // Represents the customer profiles:
    // mCustProf(i)(j)(k) = valuation for the customer type number i,
    // attribute number j, value k of attribute (each attribute can take k possible values)
    private ArrayList<CustomerProfile> CustomerProfiles = new ArrayList<>();

    // STATISTICAL VARIABLES
    private ArrayList<Integer> mResults = new ArrayList<>();
    private ArrayList<Integer> mInitialResults = new ArrayList<>();

    /************************
     * INITIAL METHOD
     **********************/

    public void start(Context context) throws Exception {

        //playPDG();
        statisticsPDG();

        Intent dashboard_menu = new Intent(context, DashboardMenu.class);
        dashboard_menu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(dashboard_menu);
    }

    public void statisticsPDG() throws Exception {
        double sum = 0;
        int sumCust = 0;
        int initSum = 0;

        mResults = new ArrayList<>();
        mInitialResults = new ArrayList<>();

        for(int i = 0; i < NUM_EXEC; i++){
            playPDG();
            sum += mResults.get(i);
            initSum = mInitialResults.get(i);
            sumCust += countCustomers() * mNTurns * 2;
        }

        StoredData.mean = sum / NUM_EXEC;
        StoredData.initMean = initSum / NUM_EXEC;
        double variance = computeVariance(StoredData.mean);
        double initVariance = computeVariance(StoredData.initMean);
        StoredData.stdDev = Math.sqrt(variance);
        StoredData.initStdDev = Math.sqrt(initVariance);
        StoredData.custMean = sumCust / NUM_EXEC;
        StoredData.percCust = 100 * StoredData.mean / StoredData.custMean;
        StoredData.initPercCust = 100 * StoredData.initMean / StoredData.custMean;
    }

    public void playPDG() throws Exception {
        generateInput();
        playGame();
    }


    /*******************
     * INPUT METHODS
     ************************/
    public void generateInput() {
        mNAttrMod = 1;
        mPrevTurns = 5;
        mNTurns = 5;
        mNAttr = 10;
        mNProd = 2;

        genAttrVal();
        StoredData.Atributos = TotalAttributes;

        genCustomerProfiles();
        genCustomerProfilesNum();
        StoredData.Profiles = CustomerProfiles;

        genProducers();
        StoredData.Producers = Producers;
    }

    public void playGame() throws Exception {
        mInitialResults.add(computeWSC(Producers.get(MY_PRODUCER).getProduct(), MY_PRODUCER));

        for (int i = 0; i < mNTurns; i++) {
            for (int j = 0; j < Producers.size(); j++) {
                changeProduct(j);
                updateCustGathered(i);
            }
        }

        mResults.add(Producers.get(MY_PRODUCER).getNumber_CustomerGathered());
    }

    /**************
     * INPUT METHODS
     ***********************/
    public void genAttrVal() {
        TotalAttributes.clear();
        for (int i = 0; i < mNAttr; i++) {
            int valueMax = (int) (Math.floor((MAX_ATTR - MIN_ATTR + 1) * Math.random()) + MIN_ATTR);
            TotalAttributes.add(new Attribute("Atributo " + (i + 1), 1, valueMax));
        }
    }

    public void genCustomerProfiles() {
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
        for (int i = 0; i < CustomerProfiles.size(); i++) {
            int number_customers = (int) (Math.floor(MAX_NUM_CUST - MIN_NUM_CUST + 1) * Math.random()) + MIN_NUM_CUST;
            CustomerProfiles.get(i).setNumberCustomers(number_customers);
        }
    }

    public void genProducers() {
        Producers.clear();

        Producers = new ArrayList<>();
        for (int i = 0; i < mNProd; i++) { //Creamos 10 productores random
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

    /************************
     * AUXILIARY METHOD PlayGame
     ***********************/

    public void changeProduct(int producerIndex) throws Exception {
        int depth;
        Producer producer = Producers.get(producerIndex);

        if (producer == Producers.get(MY_PRODUCER))
            depth = MAX_DEPTH_0;
        else
            depth = MAX_DEPTH_1;

        ArrayList<Product> list_products = new ArrayList<>();
        for (int i = 0; i < Producers.size(); i++)
            list_products.add(Producers.get(i).getProduct().clone());

        StrAB ab = alphaBetaInit(list_products, producerIndex, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
        producer.getProduct().getAttributeValue().put(TotalAttributes.get(ab.getAttriInd()), ab.getAttrVal());

    }

    private StrAB alphaBetaInit(ArrayList<Product> list_products, int producerindex, int depth, int alpha, int beta) throws Exception {

        ArrayList<StrAB> abList = new ArrayList<>();
        StrAB ab = new StrAB();
        int nCustGathered;
        boolean repetedChild = false; // To prune repeated childs
        Producer producer = Producers.get(producerindex);

        for (int attrInd = 0; attrInd < TotalAttributes.size(); attrInd++) {
            for (int attrVal = 0; attrVal < TotalAttributes.get(attrInd).getMAX(); attrVal++) {
                if (producer.getAvailableAttribute().get(attrInd).getAvailableValues().get(attrVal)) {
                    if (producer.getProduct().getAttributeValue().get(TotalAttributes.get(attrInd)) != attrVal || !repetedChild) {

                        if (producer.getProduct().getAttributeValue().get(TotalAttributes.get(attrInd)) == attrVal)
                            repetedChild = true;

                        //Computing Childs
                        ArrayList<Product> childs = new ArrayList<>();
                        for (int i = 0; i < list_products.size(); i++)
                            childs.add(list_products.get(i));

                        childs.get(producerindex).getAttributeValue().put(TotalAttributes.get(attrInd), attrVal);

                        nCustGathered = computeWSC(childs.get(producerindex), 0);

                        ab.setAlphaBeta(alphaBeta(childs, nCustGathered, producerindex, (producerindex + 1) % 2, depth - 1, alpha, beta, false));
                        ab.setAttriInd(attrInd);
                        ab.setAttrVal(attrVal);

                        abList.add(ab);
                        alpha = Math.max(alpha, ab.getAlphaBeta());
                    }
                }
            }
        }
        return bestMovement(abList, alpha);
    }

    private int alphaBeta(ArrayList<Product> products, int nCustGathered, int prodInit, int prodIndex, int depth, int alpha, int beta, boolean maximizingPlayer) throws Exception {

        boolean exitFor;
        int wsc;
        boolean repeatedChild = false; // To prune repeated  childs
        Producer producer = Producers.get(prodIndex);

        // It is a terminal node
        if (depth == 0)
            return nCustGathered;

        for (int attrInd = 0; attrInd < TotalAttributes.size(); attrInd++){
            exitFor = false;
            for (int attrVal = 0; attrVal < TotalAttributes.get(attrInd).getMAX(); attrVal++) {
                if (producer.getAvailableAttribute().get(attrInd).getAvailableValues().get(attrVal)) {
                    if (producer.getProduct().getAttributeValue().get(TotalAttributes.get(attrInd)) != attrVal || !repeatedChild) {

                        if (producer.getProduct().getAttributeValue().get(TotalAttributes.get(attrInd)) == attrVal)
                            repeatedChild = true;


                        //Computing Childs
                        ArrayList<Product> childs = new ArrayList<>();
                        for (int i = 0; i < products.size(); i++)
                            childs.add(products.get(i));

                        childs.get(prodIndex).getAttributeValue().put(TotalAttributes.get(attrInd), attrVal);

                        wsc = computeWSC(childs.get(prodIndex), prodInit);

                        if (maximizingPlayer){
                            alpha = Math.max(alpha, alphaBeta(childs, nCustGathered + wsc, prodInit, (prodIndex + 1) % 2, depth - 1, alpha, beta, false));

                            if(beta < alpha){
                                exitFor = true;
                                break;
                            }
                        }else{
                            beta = Math.max(beta, alphaBeta(childs, nCustGathered + wsc, prodInit, (prodIndex + 1) % 2, depth - 1, alpha, beta, false));

                            if(beta < alpha){
                                exitFor = true;
                                break;
                            }
                        }
                    }
                }
            }
            if(exitFor)
                break;
        }

        if (maximizingPlayer)
            return alpha;
        else
            return beta;
    }


    /***
     * Computing the weighted score of the producer
     * prodInd is the index of the producer
     *
     * @throws Exception
     **/
    private int computeWSC(Product product, int prodInd) throws Exception {
        int wsc = 0;
        boolean isTheFavourite;
        int meScore, score, k, numTies;

        for (int i = 0; i < CustomerProfiles.size(); i++) {
            isTheFavourite = true;
            numTies = 1;
            meScore = scoreProduct(CustomerProfiles.get(i), product);
            k = 0;
            while (isTheFavourite && k < Producers.size()) {
                if (k != prodInd) {

                    score = scoreProduct(CustomerProfiles.get(i), Producers.get(k).getProduct());

                    if (score > meScore)
                        isTheFavourite = false;

                    else if (score == meScore)
                        numTies += 1;
                }
                k++;
            }
				/*TODO: When there exists ties we loose some voters because of decimals (undecided voters)*/
            if (isTheFavourite)
                wsc += CustomerProfiles.get(i).getNumberCustomers() / numTies;
        }

        return wsc;
    }


    /**
     * Computing the score of a product given the customer profile index
     * custProfInd and the product
     */
    private int scoreProduct(CustomerProfile profile, Product product) throws Exception {
        int score = 0;
        for (int i = 0; i < TotalAttributes.size(); i++)
            score += profile.getScoreAttributes().get(i).getScoreValues().get(product.getAttributeValue().get(TotalAttributes.get(i)));

        return score;
    }


    private StrAB bestMovement(ArrayList<StrAB> abList, int best) {
        StrAB ab = new StrAB();

        for(int i = 0; i < abList.size(); i++){
            if(abList.get(i).getAlphaBeta() == best){
                ab.setAttrVal(abList.get(i).getAttrVal());
                ab.setAttriInd(abList.get(i).getAttriInd());
                ab.setAlphaBeta(abList.get(i).getAlphaBeta());
            }
        }
        return ab;
    }

    private void updateCustGathered(int turn) throws Exception {


        ArrayList<Product> list_products = new ArrayList<>();
        for (int i = 0; i < Producers.size(); i++)
            list_products.add(Producers.get(i).getProduct().clone());

        for(int i = 0;i < Producers.size(); i++){
            int wsc = computeWSC(Producers.get(i).getProduct(), i);

            if(Producers.get(i).getCustomersGathered().size() == mPrevTurns * 2){
                Producers.get(i).getCustomersGathered().remove(0);
            }

            Producers.get(i).getCustomersGathered().add(wsc);
            //TODO write results
        }
    }


    /**
     * Computing the variance
     */
    private double computeVariance(double mean) {
        double sqrSum = 0;
        for (int i = 0; i < NUM_EXEC; i++) {
            sqrSum += Math.pow(mResults.get(i) - mean, 2);
        }
        return (sqrSum / NUM_EXEC);
    }

    public int countCustomers(){
        int total = 0;
        for(int i = 0; i < CustomerProfiles.size(); i++)
            total += CustomerProfiles.get(i).getNumberCustomers();

        return total;
    }

}
