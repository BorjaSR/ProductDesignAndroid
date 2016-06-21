package com.example.borja.marketingcomputacional.MinimaxAlgorithm;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.borja.marketingcomputacional.area_menu.fragments.DashboardMenu;
import com.example.borja.marketingcomputacional.general.Attribute;
import com.example.borja.marketingcomputacional.general.CustomerProfile;
import com.example.borja.marketingcomputacional.general.LinkedAttribute;
import com.example.borja.marketingcomputacional.general.Producer;
import com.example.borja.marketingcomputacional.general.Product;
import com.example.borja.marketingcomputacional.general.StoredData;

import java.util.ArrayList;

/**
 * Created by Borja on 19/03/2016.
 */
public class MinimaxProblem extends MinimaxAlgorithm {

    private static MinimaxProblem MMProblem;

    private int MY_PRODUCER = 0;

    private int NUM_EXEC = 1; //5

    // INPUT VARIABLES
    private int mNTurns; // Number of turns to play (tf)

    private int mNAttr; // Number of attributes
    private int mNProd; // Number of producers
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
    private ArrayList<Integer> Prices = new ArrayList<>();
    private ArrayList<Integer> mInitialResults = new ArrayList<>();

    /************************
     * INITIAL METHOD
     **********************/

    public static MinimaxProblem getInstance() {
        if (MMProblem == null)
            MMProblem = new MinimaxProblem();

        return MMProblem;
    }

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
        int price = 0;

        mResults = new ArrayList<>();
        mInitialResults = new ArrayList<>();
        Prices = new ArrayList<>();

        generateInput();

        for (int i = 0; i < NUM_EXEC; i++) {
            playGame();
            sum += mResults.get(i);
            initSum = mInitialResults.get(i);
            sumCust += countCustomers() * mNTurns * 2;
            price += Prices.get(i);
        }

        StoredData.mean = sum / NUM_EXEC;
        StoredData.meanString = sum / NUM_EXEC + "";

        StoredData.initMean = initSum / NUM_EXEC;
        StoredData.initMeanString = initSum / NUM_EXEC + "";

        double variance = computeVariance(StoredData.mean);
        double initVariance = computeVariance(StoredData.initMean);

        StoredData.stdDev = Math.sqrt(variance);
        StoredData.stdDevString = Math.sqrt(variance) + "";

        StoredData.initStdDev = Math.sqrt(initVariance);
        StoredData.initStdDevString = Math.sqrt(initVariance) + "";

        StoredData.custMean = sumCust / NUM_EXEC;
        if (StoredData.Fitness == StoredData.Customers) {
            StoredData.percCust = 100 * StoredData.mean / StoredData.custMean;
            StoredData.percCustString = 100 * StoredData.mean / StoredData.custMean + "";
            StoredData.initPercCust = 100 * StoredData.initMean / StoredData.custMean;
            StoredData.initPercCustString = 100 * StoredData.initMean / StoredData.custMean + "";
        } else if (StoredData.Fitness == StoredData.Benefits) {
            StoredData.percCust = (100 * StoredData.mean) / StoredData.initMean;
            StoredData.percCustString = (100 * StoredData.mean) / StoredData.initMean + "";
        }

        StoredData.My_price = price / NUM_EXEC;
        StoredData.My_priceString = price / NUM_EXEC + "";
    }

    /*******************
     * INPUT METHODS
     ************************/
    public void generateInput() {
        mNTurns = 5;
        mNAttr = 10;
        mNProd = 2;

        TotalAttributes.clear();
        for (int i = 0; i < mNAttr; i++)
            TotalAttributes.add(StoredData.Atributos.get(i));


        CustomerProfiles = StoredData.Profiles;

        Producers.clear();
        for (int i = 0; i < mNProd; i++)
            Producers.add(StoredData.Producers.get(i));
    }

    public void playGame() throws Exception {
        if (StoredData.Fitness == StoredData.Benefits)
            mInitialResults.add(computeBenefits(Producers.get(MY_PRODUCER).getProduct(), MY_PRODUCER));
        else
            mInitialResults.add(computeWSC(Producers.get(MY_PRODUCER).getProduct(), MY_PRODUCER));

        playMinimaxAlgorithm();

        if (StoredData.Fitness == StoredData.Benefits)
            mResults.add(Producers.get(MY_PRODUCER).getNumber_CustomerGathered() * calculatePrice(Producers.get(MY_PRODUCER).getProduct()));
        else
            mResults.add(Producers.get(MY_PRODUCER).getNumber_CustomerGathered());

        Prices.add(calculatePrice(Producers.get(MY_PRODUCER).getProduct()));
    }

    /************************
     * AUXILIARY METHOD PlayGame
     ***********************/

    private Integer computeBenefits(Product product, int myProducer) throws Exception {
        return computeWSC(product, myProducer) * product.getPrice();
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

            if (StoredData.isAttributesLinked)
                meScore += scoreLinkedAttributes(CustomerProfiles.get(i).getLinkedAttributes(), product);

            k = 0;
            while (isTheFavourite && k < Producers.size()) {
                if (k != prodInd) {

                    score = scoreProduct(CustomerProfiles.get(i), Producers.get(k).getProduct());

                    if (StoredData.isAttributesLinked)
                        score += scoreLinkedAttributes(CustomerProfiles.get(i).getLinkedAttributes(), product);

                    if (score > meScore)
                        isTheFavourite = false;

                    else if (score == meScore)
                        numTies += 1;
                }
                k++;
            }
                /* When there exists ties we loose some voters because of decimals (undecided voters)*/
            if (isTheFavourite)
                wsc += CustomerProfiles.get(i).getNumberCustomers() / numTies;
        }

        return wsc;
    }

    private int scoreLinkedAttributes(ArrayList<LinkedAttribute> linkedAttributes, Product product) {
        int modifyScore = 0;
        for (int i = 0; i < linkedAttributes.size(); i++) {
            LinkedAttribute link = linkedAttributes.get(i);
            if (product.getAttributeValue().get(link.getAttribute1()) == link.getValue1() && product.getAttributeValue().get(link.getAttribute2()) == link.getValue2()) {
                modifyScore += link.getScoreModification();
            }
        }
        return modifyScore;
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

    @Override
    protected void setFitnessAcumulated(int i, ArrayList<Integer> customersAcumulated) {
        Producers.get(i).setCustomersGathered(customersAcumulated);
    }

    @Override
    public Object getObject(int playerIndex) {
        return Producers.get(playerIndex).getProduct();
    }

    @Override
    public Integer getFitness(Object object, int index) throws Exception {
        if (StoredData.Fitness == StoredData.Benefits)
            ((Product) object).setPrice(calculatePrice((Product) object));


        if (StoredData.Fitness == StoredData.Benefits)
            return computeBenefits((Product) object, index);
        else
            return computeWSC((Product) object, index);
    }

    @Override
    public int getDimens() {
        return TotalAttributes.size();
    }

    @Override
    protected int getSolutionsSpace(int dimen) {
        return TotalAttributes.get(dimen).getMAX();
    }

    @Override
    protected int getSolution(int playerIndex, int dimension) {
        return Producers.get(playerIndex).getProduct().getAttributeValue().get(TotalAttributes.get(dimension));
    }

    @Override
    protected boolean isPosibleToChange(int playerIndex, int dimension, int attrVal) {
        return Producers.get(playerIndex).getAvailableAttribute().get(dimension).getAvailableValues().get(attrVal);
    }

    @Override
    protected void changeChild(Object o, int dimension, int solutionSpaceIndex) {
        ((Product)o).getAttributeValue().put(TotalAttributes.get(dimension), solutionSpaceIndex);
    }

    @Override
    protected void setSolution(int playerIndex, int dimension, int solution) {
        Producers.get(playerIndex).getProduct().getAttributeValue().put(TotalAttributes.get(dimension), solution);
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

    public int countCustomers() {
        int total = 0;
        for (int i = 0; i < CustomerProfiles.size(); i++)
            total += CustomerProfiles.get(i).getNumberCustomers();

        return total;
    }


    /***************************************
     * " AUXILIARY METHODS TO CALCULATE THE PRICE"
     ***************************************/

    private int calculatePrice(Product product) {
        int price_MyProduct = 0;

        for (int i = 1; i < Producers.size(); i++) {
            Product prod_competence = Producers.get(i).getProduct();
            double distance_product = getDistanceTo(product, prod_competence);

            if (distance_product == 0) {
                price_MyProduct = prod_competence.getPrice();
                break;
            }

            price_MyProduct += prod_competence.getPrice() / distance_product;
        }

        return price_MyProduct;
    }

    private double getDistanceTo(Product my_product, Product prod_competence) {
        double distance = 0;
        for (int i = 0; i < TotalAttributes.size(); i++) {
            distance += Math.pow(my_product.getAttributeValue().get(TotalAttributes.get(i)) - prod_competence.getAttributeValue().get(TotalAttributes.get(i)), 2);
        }
        distance = Math.sqrt(distance);
        return distance;
    }

}
