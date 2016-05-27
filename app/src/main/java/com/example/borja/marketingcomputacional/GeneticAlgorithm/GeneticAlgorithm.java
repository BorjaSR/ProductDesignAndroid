package com.example.borja.marketingcomputacional.GeneticAlgorithm;

import android.content.Context;
import android.content.Intent;

import com.example.borja.marketingcomputacional.area_menu.fragments.DashboardMenu;
import com.example.borja.marketingcomputacional.general.Attribute;
import com.example.borja.marketingcomputacional.general.CustomerProfile;
import com.example.borja.marketingcomputacional.general.LinkedAttribute;
import com.example.borja.marketingcomputacional.general.Producer;
import com.example.borja.marketingcomputacional.general.Product;
import com.example.borja.marketingcomputacional.general.StoredData;

import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class GeneticAlgorithm {

    private static GeneticAlgorithm GAProblem = null;

    static final double KNOWN_ATTRIBUTES = 100; /* 100
                                                 * % of attributes known for all
												 * producers
												 */
    static final int CROSSOVER_PROB = 80; /* % of crossover */
    static final int MUTATION_PROB = 1; /* % of mutation */
    static final int NUM_GENERATIONS = 100; /* number of generations */
    static final int NUM_POPULATION = 20; /* number of population */
    static final int RESP_PER_GROUP = 20; /* * We divide the respondents of each
                                             * profile in groups of
											 * RESP_PER_GROUP respondents
											 */

    static final int NEAR_CUST_PROFS = 4;
    static final int NUM_EXECUTIONS = 1; /* number of executions */

    static final int MY_PRODUCER = 0;  //The index of my producer

    private static ArrayList<Attribute> TotalAttributes = new ArrayList<>();
    private static ArrayList<Producer> Producers = new ArrayList<>();

    /* GA VARIABLES */
    private ArrayList<Integer> BestWSC = new ArrayList<>(); /* Stores the best wsc found */
    private ArrayList<Product> Population = new ArrayList<>();  //Private mPopu As List(Of List(Of Integer))
    private ArrayList<Integer> Fitness = new ArrayList<>(); /* * mFitness(i) = wsc of mPopu(i) */

    /* STATISTICAL VARIABLES */
    private ArrayList<ArrayList<Integer>> Results = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> Initial_Results = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> Prices = new ArrayList<>();
    private int wscSum;

    private static ArrayList<CustomerProfile> CustomerProfiles = new ArrayList<>();

    /***************************************
     * " AUXILIARY EXCEL METHODS " * @throws Exception
     ***************************************/

    public static GeneticAlgorithm getInstance(){
        if(GAProblem == null)
            GAProblem = new GeneticAlgorithm();

        return GAProblem;
    }

    public void start(final Context context) throws Exception {

//        solvePD();
        statisticsPD();

        Intent dashboard_menu = new Intent(context, DashboardMenu.class);
        dashboard_menu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(dashboard_menu);
    }


    /***************************************
     * " PRIVATE METHODS "
     ***************************************/

    private void solvePD() throws Exception {

        generateInput();
        solvePD_GA();
    }

    /**
     * Generating the input data
     *
     * @throws Exception
     */
    private void generateInput() throws Exception {

        TotalAttributes = StoredData.Atributos;
        CustomerProfiles = StoredData.Profiles;
        Producers = StoredData.Producers;
    }


    /**
     * Solving the PD problem by using a GA
     */
    private void solvePD_GA() throws Exception {

        ArrayList<Product> newPopu;
        ArrayList<Integer> newFitness = new ArrayList<>();
        createInitPopu();
        for (int generation = 0; generation < NUM_GENERATIONS; generation++) {
            newPopu = createNewPopu(newFitness);
            Population = tournament(newPopu, newFitness);
        }

        Results.add(BestWSC);
        showWSC();

        ArrayList<Integer> prices = new ArrayList<>();
        for (int i = 0; i < Producers.get(MY_PRODUCER).getProducts().size(); i++) {
            int price_MyProduct = calculatePrice(Producers.get(MY_PRODUCER).getProducts().get(i));
            Producers.get(MY_PRODUCER).getProducts().get(i).setPrice(price_MyProduct);
            prices.add(price_MyProduct);
        }
        Prices.add(prices);
    }

    /**
     * Generating statistics about the PD problem
     */
    private void statisticsPD() throws Exception {

        ArrayList<Double> sum = new ArrayList<>();
        ArrayList<Double> initSum = new ArrayList<>();
        ArrayList<Integer> prices = new ArrayList<>();
        int sumCust = 0;

        for (int i = 0; i < StoredData.number_Products; i++) {
            sum.add((double) 0);
            initSum.add((double) 0);
            prices.add(0);
        }

        Results = new ArrayList<>();
        Initial_Results = new ArrayList<>();
        Prices = new ArrayList<>();

        generateInput();

        for (int i = 0; i < NUM_EXECUTIONS; i++) {
            if (i != 0) /*We reset myPP and create a new product as the first product*/ {
                ArrayList<Product> products = new ArrayList<>();
                for (int k = 0; k < StoredData.number_Products; k++)
                    products.add(createNearProduct(Producers.get(MY_PRODUCER).getAvailableAttribute(), (int) (CustomerProfiles.size() * Math.random())));
                Producers.get(MY_PRODUCER).setProducts(products);
            }
            solvePD_GA();

            ArrayList<Double> auxSum = new ArrayList<>();
            ArrayList<Double> auxinitSum = new ArrayList<>();
            ArrayList<Integer> auxprice = new ArrayList<>();
            for (int k = 0; k < StoredData.number_Products; k++) {
                auxSum.add(sum.get(k) + Results.get(i).get(k));
                auxinitSum.add(initSum.get(k) + Initial_Results.get(i).get(k));
                auxprice.add(prices.get(k) + Prices.get(i).get(k));
            }

            sum = auxSum;
            initSum = auxinitSum;
            prices = auxprice;

            sumCust += wscSum;
        }

        String meanTXT = "";
        String initMeanTXT = "";
        String stdDevTXT = "";
        String initStdDevTXT = "";
        String percCustTXT = "";
        String initPercCustTXT = "";
        String priceTXT = "";

        StoredData.custMean = sumCust / NUM_EXECUTIONS;

        for (int i = 0; i < StoredData.number_Products; i++) {

            double mean = sum.get(i) / NUM_EXECUTIONS;
            double initMean = initSum.get(i) / NUM_EXECUTIONS;
            double variance = computeVariance(mean);
            double initVariance = computeVariance(initMean);
            double stdDev = Math.sqrt(variance);
            double initStdDev = Math.sqrt(initVariance);
            double percCust;
            double initPercCust = -1;
            if (StoredData.Fitness == StoredData.Customers) {
                percCust = 100 * mean / StoredData.custMean;
                initPercCust = 100 * initMean / StoredData.custMean;
            } else {
                percCust = 100 * mean / initMean;
            }
            double priceDoub = prices.get(i) / NUM_EXECUTIONS;

            if (i == 0)
                meanTXT += mean;
            else
                meanTXT += ", " + mean;

            if (i == 0)
                initMeanTXT += initMean;
            else
                initMeanTXT += ", " + initMean;

            if (i == 0)
                stdDevTXT += stdDev;
            else
                stdDevTXT += ", " + stdDev;

            if (i == 0)
                initStdDevTXT += initStdDev;
            else
                initStdDevTXT += ", " + initStdDev;

            if (i == 0)
                percCustTXT += percCust;
            else
                percCustTXT += ", " + percCust;

            if (StoredData.Fitness == StoredData.Customers)
                if (i == 0)
                    initPercCustTXT += initPercCust;
                else
                    initPercCustTXT += ", " + initPercCust;

            if (i == 0)
                priceTXT += priceDoub;
            else
                priceTXT += ", " + priceDoub;
        }

        StoredData.mean = sum.get(0) / NUM_EXECUTIONS;
        StoredData.initMean = initSum.get(0) / NUM_EXECUTIONS;
        double variance = computeVariance(StoredData.mean);
        double initVariance = computeVariance(StoredData.initMean);
        StoredData.stdDev = Math.sqrt(variance);
        StoredData.initStdDev = Math.sqrt(initVariance);
        if (StoredData.Fitness == StoredData.Customers) {
            StoredData.percCust = 100 * StoredData.mean / StoredData.custMean;
            StoredData.initPercCust = 100 * StoredData.initMean / StoredData.custMean;
        } else if (StoredData.Fitness == StoredData.Benefits) {
            StoredData.percCust = (100 * StoredData.mean) / StoredData.initMean;
        }

        StoredData.My_price = prices.get(0) / NUM_EXECUTIONS;


        StoredData.meanString = meanTXT;
        StoredData.initMeanString = initMeanTXT;
        StoredData.stdDevString = stdDevTXT;
        StoredData.initStdDevString = initStdDevTXT;
        StoredData.percCustString = percCustTXT;
        StoredData.initPercCustString = initPercCustTXT;
        StoredData.My_priceString = priceTXT;

		/*MOSTRARLO*/
    }


    /*************************************** " AUXILIARY METHODS GENERATEINPUT()" ***************************************/

    /**
     * Creating a random product
     */
    private Product createRndProduct(ArrayList<Attribute> availableAttribute) {
        Product product = new Product(new HashMap<Attribute, Integer>());
        int limit = (int) (TotalAttributes.size() * KNOWN_ATTRIBUTES / 100);
        int attrVal = 0;

        for (int i = 0; i < limit; i++) {
            attrVal = (int) (TotalAttributes.get(i).getMAX() * Math.random());
            product.getAttributeValue().put(TotalAttributes.get(i), attrVal);

        }

        for (int i = limit; i < TotalAttributes.size(); i++) {
            boolean attrFound = false;
            while (!attrFound) {
//                attrVal =  TotalAttributes.get(i).getScoreValues().get((int) (Math.random() * TotalAttributes.get(i).getScoreValues().size()));

                attrVal = (int) (TotalAttributes.get(i).getMAX() * Math.random());

                if (availableAttribute.get(i).getAvailableValues().get(attrVal))
                    attrFound = true;

            }
            product.getAttributeValue().put(TotalAttributes.get(i), attrVal);
        }

        product.setPrice(calculatePrice(product));
        return product;
    }

    /**
     * Creating a product near various customer profiles
     */
    private Product createNearProduct(ArrayList<Attribute> availableAttribute, int nearCustProfs) {
        // improve having into account the sub-profiles*/
        Product product = new Product(new HashMap<Attribute, Integer>());
        int attrVal;

        ArrayList<Integer> custProfsInd = new ArrayList<>();
        for (int i = 1; i < nearCustProfs; i++)
            custProfsInd.add((int) Math.floor(CustomerProfiles.size() * Math.random()));


        for (int i = 0; i < TotalAttributes.size(); i++) {
            attrVal = chooseAttribute(i, custProfsInd, availableAttribute);
            product.getAttributeValue().put(TotalAttributes.get(i), attrVal);
        }

        product.setPrice(calculatePrice(product));
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


    /*************************************** " AUXILIARY METHODS SOLVEPD_GA()" ***************************************/

    /**
     * Creating the initial population
     *
     * @throws Exception
     */
    private void createInitPopu() throws Exception {
        Population = new ArrayList<>();
        Fitness = new ArrayList<>();
        BestWSC = new ArrayList<>();

        for (int i = 0; i < Producers.get(MY_PRODUCER).getProducts().size(); i++) {
            Population.add((Producers.get(MY_PRODUCER).getProducts().get(i)).clone());

            if (StoredData.Fitness == StoredData.Customers)
                Fitness.add(computeWSC(Population.get(i), MY_PRODUCER));
            else
                Fitness.add(computeBenefits(Population.get(i), MY_PRODUCER));
        }

        for (int t = 0; t < Fitness.size(); t++)
            BestWSC.add(Fitness.get(t));

        ArrayList<Integer> aux = new ArrayList<>();
        for (int q = 0; q < BestWSC.size(); q++)
            aux.add(BestWSC.get(q));

        Initial_Results.add(aux);

        for (int i = Producers.get(MY_PRODUCER).getProducts().size(); i < NUM_POPULATION; i++) {

            if (i % 2 == 0) /*We create a random product*/
                Population.add(createRndProduct(Producers.get(MY_PRODUCER).getAvailableAttribute()));
            else /*We create a near product*/
                Population.add(createNearProduct(Producers.get(MY_PRODUCER).getAvailableAttribute(), (int) (CustomerProfiles.size() * Math.random())));  /////////??verificar//////////

            if (StoredData.Fitness == StoredData.Customers)
                Fitness.add(computeWSC(Population.get(i), MY_PRODUCER));
            else
                Fitness.add(computeBenefits(Population.get(i), MY_PRODUCER));

            int worstIndex = isBetweenBest(Fitness.get(i));
            if (worstIndex != -1) {
//                BestWSC.remove(worstIndex);
//                BestWSC.add(Fitness.get(i));
//                Producers.get(MY_PRODUCER).getProducts().remove(worstIndex);
//                Producers.get(MY_PRODUCER).getProducts().add(Population.get(i));
                BestWSC.set(worstIndex, Fitness.get(i));
                Producers.get(MY_PRODUCER).getProducts().set(worstIndex, Population.get(i));
            }
        }
    }

    private int isBetweenBest(int fitness) {
        for (int i = 0; i < BestWSC.size(); i++) {
            if (fitness > BestWSC.get(i))
                return i;
        }
        return -1;
    }

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
        int meScore, score, k, p, numTies;
        int count = 0;

        for (int i = 0; i < CustomerProfiles.size(); i++) {
            for (int j = 0; j < CustomerProfiles.get(i).getSubProfiles().size(); j++) {
                isTheFavourite = true;
                numTies = 1;
                meScore = scoreProduct(CustomerProfiles.get(i).getSubProfiles().get(j), product);

                if (StoredData.isAttributesLinked)
                    meScore += scoreLinkedAttributes(CustomerProfiles.get(i).getLinkedAttributes(), product);

                k = 0;
                while (isTheFavourite && k < Producers.size()) {
                    p = 0;
                    while (isTheFavourite && p < Producers.get(k).getProducts().size()) {
                        if (Producers.get(k).getProducts().get(p) != product) {

                            score = scoreProduct(CustomerProfiles.get(i).getSubProfiles().get(j), Producers.get(k).getProducts().get(p));

                            if (StoredData.isAttributesLinked)
                                score += scoreLinkedAttributes(CustomerProfiles.get(i).getLinkedAttributes(), product);

                            if (score > meScore)
                                isTheFavourite = false;

                            else if (score == meScore)
                                numTies += 1;
                        } else {
                            count++;
                        }
                        p++;
                    }
                    k++;
                }
                /* When there exists ties we loose some voters because of decimals (undecided voters)*/
                if (isTheFavourite) {
                    if ((j == CustomerProfiles.get(i).getSubProfiles().size()) && ((CustomerProfiles.get(i).getNumberCustomers() % RESP_PER_GROUP) != 0)) {
                        wsc += (CustomerProfiles.get(i).getNumberCustomers() % RESP_PER_GROUP) / numTies;
                    } else {
                        wsc += RESP_PER_GROUP / numTies;
                    }
                }
            }
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
    private int scoreProduct(SubProfile subprofile, Product product) throws Exception {
        int score = 0;
        for (int i = 0; i < TotalAttributes.size(); i++) {
            score += scoreAttribute(TotalAttributes.get(i).getMAX(), subprofile.getValueChosen().get(TotalAttributes.get(i)), product.getAttributeValue().get(TotalAttributes.get(i)));
        }
        return score;
    }

    /**
     * Computing the score of an attribute for a product given the
     * ' number of values
     */
    private int scoreAttribute(int numOfValsOfAttr, int valOfAttrCust, int valOfAttrProd) throws Exception {
        int score = 0;
        switch (numOfValsOfAttr) {
            case 1:
                score = 10;
                break;

            case 2:
                if (valOfAttrCust == valOfAttrProd) score = 10;
                else score = 0;
                break;

            case 3:
                if (valOfAttrCust == valOfAttrProd) score = 10;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 5;
                else score = 0;
                break;

            case 4:
                if (valOfAttrCust == valOfAttrProd) score = 10;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 6;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 2) score = 2;
                else score = 0;
                break;

            case 5:
                if (valOfAttrCust == valOfAttrProd) score = 10;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 6;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 2) score = 2;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 3) score = 1;
                else score = 0;
                break;

            case 6:
                if (valOfAttrCust == valOfAttrProd) score = 10;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 6;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 2) score = 2;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 3) score = 1;
                else score = 0;
                break;

            case 7:
                if (valOfAttrCust == valOfAttrProd) score = 10;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 6;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 2) score = 2;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 3) score = 1;
                else score = 0;
                break;

            case 8:
                if (valOfAttrCust == valOfAttrProd) score = 10;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 6;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 2) score = 2;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 3) score = 1;
                else score = 0;
                break;

            case 9:
                if (valOfAttrCust == valOfAttrProd) score = 10;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 6;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 2) score = 2;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 3) score = 1;
                else score = 0;
                break;

            case 10:
                if (valOfAttrCust == valOfAttrProd) score = 10;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 8;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 2) score = 5;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 3) score = 1;
                else score = 0;
                break;

            case 11:
                if (valOfAttrCust == valOfAttrProd) score = 10;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 8;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 2) score = 6;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 3) score = 4;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 4) score = 2;
                else score = 0;
                break;

            default:
                throw new Exception("Error in scoreAttribute() function: " +
                        "Number of values of the attribute unexpected");
        }
        return score;
    }


    /**
     * Creating a new population
     */
    private ArrayList<Product> createNewPopu(ArrayList<Integer> fitness) throws Exception {
        int fitnessSum = computeFitnessSum();
        ArrayList<Product> newPopu = new ArrayList<>();
        int father, mother;
        Product son;

        for (int i = 0; i < NUM_POPULATION; i++) {
            father = chooseFather(fitnessSum);
            mother = chooseFather(fitnessSum);
            son = mutate(breed(father, mother));

            newPopu.add(son);

            if (StoredData.Fitness == StoredData.Customers)
                fitness.add(computeWSC(newPopu.get(i), MY_PRODUCER));
            else
                fitness.add(computeBenefits(newPopu.get(i), MY_PRODUCER));

        }

        return newPopu;
    }

    /**
     * Computing the sum of the fitness of all the population
     */
    private int computeFitnessSum() {
        int sum = 0;
        for (int i = 0; i < Fitness.size() - 1; i++) {
            sum += Fitness.get(i);
        }
        return sum;
    }

    /**
     * Chosing the father in a random way taking into account the fitness
     */
    private int chooseFather(double fitnessSum) {
        int fatherPos = 0;
        double rndVal = fitnessSum * Math.random();
        double accumulator = Fitness.get(fatherPos);
        while (rndVal > accumulator) {
            fatherPos += 1;
            accumulator += Fitness.get(fatherPos);
        }
        return fatherPos;
    }

    /**
     * Metodo que dado un padre y una madre los cruza para obtener un hijo.
     * Para cada posici�n del array eligiremos aleatoriamente si el hijo heredar�
     * esa posici�n del padre o de la madre.
     */
    private Product breed(int father, int mother) {
        Product son = new Product();
        /*Random value in range [0,100)*/
        int crossover = (int) (100 * Math.random());
        int rndVal;

        if (crossover <= CROSSOVER_PROB) {//El hijo sera una mezcla de la madre y el padre
            HashMap<Attribute, Integer> crossover_attributeValue = new HashMap<>();
            for (int i = 0; i < TotalAttributes.size(); i++) { //With son

                rndVal = (int) (2 * Math.random()); /*Generamos aleatoriamente un 0 (padre) o un 1 (madre).*/
                if (rndVal == 0)
                    crossover_attributeValue.put(TotalAttributes.get(i), Population.get(father).getAttributeValue().get(TotalAttributes.get(i)));
                else
                    crossover_attributeValue.put(TotalAttributes.get(i), Population.get(mother).getAttributeValue().get(TotalAttributes.get(i)));
            }
            son.setAttributeValue(crossover_attributeValue);
        } else {//El hijo seria completamente igual a la madre o al padre
            rndVal = (int) (2 * Math.random()); /*Generamos aleatoriamente un 0 (padre) o un 1 (madre).*/

            if (rndVal == 0)
                son = Population.get(father).clone();
            else
                son = Population.get(mother).clone();
        }
        return son;
    }


    /**
     * Method that creates an individual parameter passed mutating individual.
     * The mutation is to add / remove a joint solution.
     *
     * @throws Exception
     */
    private Product mutate(Product indiv) {

        Product mutant = indiv.clone();
        int attrVal = 0;

        for (int j = 0; j < TotalAttributes.size(); j++) {
            /*Random value in range [0,100)*/
            double mutation = 100 * Math.random();
            if (mutation <= MUTATION_PROB) {
                boolean attrFound = false;
                while (!attrFound) {
                    attrVal = (int) (Math.floor(TotalAttributes.get(j).getMAX() * Math.random()));
                    if (Producers.get(MY_PRODUCER).getAvailableAttribute().get(j).getAvailableValues().get(attrVal))
                        attrFound = true;
                }
                mutant.getAttributeValue().put(TotalAttributes.get(j), attrVal);
            }
        }

        mutant.setPrice(calculatePrice(mutant));
        return mutant;
    }


    /**
     * Metodo que dada la poblaci�n original y una nueva poblaci�n elige la siguente
     * ' generaci�n de individuos. Actualizo la mejor soluci�n encontrada en caso de mejorarla.
     */
    private ArrayList<Product> tournament(ArrayList<Product> newPopu, ArrayList<Integer> newFitness) {

        ArrayList<Product> nextGeneration = new ArrayList<>();
        for (int i = 0; i < NUM_POPULATION; i++) {

            if (Fitness.get(i) >= newFitness.get(i))
                nextGeneration.add((Population.get(i)).clone());
            else {

                nextGeneration.add((newPopu.get(i)).clone());
                Fitness.set(i, newFitness.get(i));// We update the fitness of the new individual

                int worstIndex = isBetweenBest(Fitness.get(i));
                if (worstIndex != -1) {
                    BestWSC.remove(worstIndex);
                    BestWSC.add(Fitness.get(i));
                    Producers.get(MY_PRODUCER).getProducts().remove(worstIndex);
                    Producers.get(MY_PRODUCER).getProducts().add((newPopu.get(i)).clone());
                }
            }
        }
        return nextGeneration;
    }


    /**
     * Showing the wsc of the rest of products
     *
     * @throws Exception
     */

    private void showWSC() throws Exception {
        int wsc;
        wscSum = 0;

        for (int i = 0; i < Producers.size(); i++) {
            for (int j = 0; j < Producers.get(i).getProducts().size(); j++) {
                wsc = computeWSC(Producers.get(i).getProducts().get(j), i);
                wscSum += wsc;
            }
        }
    }


    /*************************************** " AUXILIARY METHODS STATISTICSPD()" ***************************************/

    /**
     * Computing the variance
     */
    private double computeVariance(double mean) {//TODO me fijo solo en el primero
        double sqrSum = 0;
        for (int i = 0; i < NUM_EXECUTIONS; i++) {
            sqrSum += Math.pow(Results.get(i).get(0) - mean, 2);
        }
        return (sqrSum / NUM_EXECUTIONS);
    }

    /***************************************
     * " AUXILIARY METHODS TO CALCULATE THE PRICE"
     ***************************************/

    private int calculatePrice(Product product) {//TODO me fijo solo en el primero
        int price_MyProduct = 0;

        for (int i = 1; i < Producers.size(); i++) {
            Product prod_competence = Producers.get(i).getProducts().get(0);
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