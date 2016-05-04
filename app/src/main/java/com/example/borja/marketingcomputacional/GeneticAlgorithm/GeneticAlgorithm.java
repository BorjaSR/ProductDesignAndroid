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

import java.util.HashMap;
import java.util.ArrayList;

public class GeneticAlgorithm {

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

    // static final String SOURCE = "D:\Pablo\EncuestasCIS.xlsx";
    static final int SHEET_AGE_STUDIES = 1;
    static final int SHEET_POLITICAL_PARTIES = 2;
    static final String EOF = "EOF";

    private static ArrayList<Attribute> TotalAttributes = new ArrayList<>();
    private static ArrayList<Producer> Producers = new ArrayList<>();
    private static ArrayList<CustomerProfile> CustomerProfiles = new ArrayList<>();

    /* GA VARIABLES */
    private int BestWSC; /* Stores the best wsc found */
    private ArrayList<Product> Population;   //Private mPopu As List(Of List(Of Integer))
    private ArrayList<Integer> Fitness; /* * mFitness(i) = wsc of mPopu(i) */

    /* STATISTICAL VARIABLES */
    private ArrayList<Integer> Results = new ArrayList<>();
    private ArrayList<Integer> Initial_Results = new ArrayList<>();
    private ArrayList<Integer> Prices = new ArrayList<>();
    private int wscSum;


    /***************************************
     * " AUXILIARY EXCEL METHODS " * @throws Exception
     ***************************************/

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

        int price_MyProduct = calculatePrice(Producers.get(MY_PRODUCER).getProduct());
        Producers.get(MY_PRODUCER).getProduct().setPrice(price_MyProduct);
        Prices.add(price_MyProduct);
    }

    /**
     * Generating statistics about the PD problem
     */
    private void statisticsPD() throws Exception {

        double sum = 0; /*sum of customers achieved*/
        double initSum = 0; /*sum of initial customers*/
        int sumCust = 0; /*sum of the total number of customers*/
        int price = 0;

        Results = new ArrayList<>();
        Initial_Results = new ArrayList<>();
        Prices = new ArrayList<>();

        if (Producers.size() == 0) generateInput();

        for (int i = 0; i < NUM_EXECUTIONS; i++) {
            if (i != 0) /*We reset myPP and create a new product as the first product*/ {
                Producers.get(MY_PRODUCER).setProduct(createNearProduct(Producers.get(MY_PRODUCER).getAvailableAttribute(), (int) (CustomerProfiles.size() * Math.random())));
            }
            solvePD_GA();
            sum += Results.get(i);
            initSum += Initial_Results.get(i);
            sumCust += wscSum;
            price += Prices.get(i);
        }

        StoredData.mean = sum / NUM_EXECUTIONS;
        StoredData.meanString = sum / NUM_EXECUTIONS + "";

        StoredData.initMean = initSum / NUM_EXECUTIONS;
        StoredData.initMeanString = initSum / NUM_EXECUTIONS + "";

        double variance = computeVariance(StoredData.mean);
        double initVariance = computeVariance(StoredData.initMean);

        StoredData.stdDev = Math.sqrt(variance);
        StoredData.stdDevString = Math.sqrt(variance) + "";

        StoredData.initStdDev = Math.sqrt(initVariance);
        StoredData.initStdDevString = Math.sqrt(initVariance) + "";

        StoredData.custMean = sumCust / NUM_EXECUTIONS;
        if (StoredData.Fitness == StoredData.Customers) {
            StoredData.percCust = 100 * StoredData.mean / StoredData.custMean;
            StoredData.percCustString = 100 * StoredData.mean / StoredData.custMean + "";
            StoredData.initPercCust = 100 * StoredData.initMean / StoredData.custMean;
            StoredData.initPercCustString = 100 * StoredData.initMean / StoredData.custMean + "";
        } else if (StoredData.Fitness == StoredData.Benefits) {
            StoredData.percCust = (100 * StoredData.mean) / StoredData.initMean;
            StoredData.percCustString = (100 * StoredData.mean) / StoredData.initMean + "";
        }

        StoredData.My_price = price / NUM_EXECUTIONS;
        StoredData.My_priceString = price / NUM_EXECUTIONS + "";

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

        Population.add((Producers.get(MY_PRODUCER).getProduct()).clone());
//        mPopu.get(0).setFitness(computeWSC(mPopu.get(0), 0));
        if (StoredData.Fitness == StoredData.Customers)
            Fitness.add(computeWSC(Population.get(MY_PRODUCER), MY_PRODUCER));
        else
            Fitness.add(computeBenefits(Population.get(MY_PRODUCER), MY_PRODUCER));

        BestWSC = Fitness.get(MY_PRODUCER);
        Initial_Results.add(BestWSC);

        for (int i = 1; i < NUM_POPULATION; i++) {

            if (i % 2 == 0) /*We create a random product*/
                Population.add(createRndProduct(Producers.get(MY_PRODUCER).getAvailableAttribute()));
            else /*We create a near product*/
                Population.add(createNearProduct(Producers.get(MY_PRODUCER).getAvailableAttribute(), (int) (CustomerProfiles.size() * Math.random())));  /////////??verificar//////////

            if (StoredData.Fitness == StoredData.Customers)
                Fitness.add(computeWSC(Population.get(i), MY_PRODUCER));
            else
                Fitness.add(computeBenefits(Population.get(i), MY_PRODUCER));

            if (Fitness.get(i) > BestWSC) {
                BestWSC = Fitness.get(i);
                Producers.get(MY_PRODUCER).setProduct(Population.get(i).clone());
            }
        }
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
        int meScore, score, k, numTies;

        for (int i = 0; i < CustomerProfiles.size(); i++) {
            for (int j = 0; j < CustomerProfiles.get(i).getSubProfiles().size(); j++) {
                isTheFavourite = true;
                numTies = 1;
                meScore = scoreProduct(CustomerProfiles.get(i).getSubProfiles().get(j), product);

                if(StoredData.isAttributesLinked)
                    meScore += scoreLinkedAttributes(CustomerProfiles.get(i).getLinkedAttributes(), product);

                k = 0;
                while (isTheFavourite && k < Producers.size()) {
                    if (k != prodInd) {

                        score = scoreProduct(CustomerProfiles.get(i).getSubProfiles().get(j), Producers.get(k).getProduct());

                        if(StoredData.isAttributesLinked)
                            score += scoreLinkedAttributes(CustomerProfiles.get(i).getLinkedAttributes(), product);

                        if (score > meScore)
                            isTheFavourite = false;

                        else if (score == meScore)
                            numTies += 1;
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
            for(int i = 0; i < linkedAttributes.size(); i++){
                LinkedAttribute link = linkedAttributes.get(i);
                if(product.getAttributeValue().get(link.getAttribute1()) == link.getValue1() && product.getAttributeValue().get(link.getAttribute2()) == link.getValue2()){
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
            // score += scoreAttribute(mAttributes(i), mCustProfAux(custProfInd)(custSubProfInd)(i), product(i))
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
            case 2: {
                if (valOfAttrCust == valOfAttrProd) score = 10;
                else score = 0;
            }
            break;
            case 3: {
                if (valOfAttrCust == valOfAttrProd) score = 10;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 5;
                else score = 0;
            }
            break;
            case 4: {
                if (valOfAttrCust == valOfAttrProd) score = 10;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 6;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 2) score = 2;
                else score = 0;
            }
            break;
            case 5: {
                if (valOfAttrCust == valOfAttrProd) score = 10;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 6;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 2) score = 2;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 3) score = 1;
                else score = 0;
            }
            break;
            case 11: {
                if (valOfAttrCust == valOfAttrProd) score = 10;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 1) score = 8;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 2) score = 6;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 3) score = 4;
                else if (Math.abs(valOfAttrCust - valOfAttrProd) == 4) score = 2;
                else score = 0;
            }
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

                if (newFitness.get(i) > BestWSC) {
                    BestWSC = newFitness.get(i);
                    Producers.get(0).setProduct(newPopu.get(i));
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
            wsc = computeWSC(Producers.get(i).getProduct(), i);
            wscSum += wsc;
        }
    }


    /*************************************** " AUXILIARY METHODS STATISTICSPD()" ***************************************/

    /**
     * Computing the variance
     */
    private double computeVariance(double mean) {
        double sqrSum = 0;
        for (int i = 0; i < NUM_EXECUTIONS; i++) {
            sqrSum += Math.pow(Results.get(i) - mean, 2);
        }
        return (sqrSum / NUM_EXECUTIONS);
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