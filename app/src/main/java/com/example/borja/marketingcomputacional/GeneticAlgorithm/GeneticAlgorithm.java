package com.example.borja.marketingcomputacional.GeneticAlgorithm;

import android.content.Context;
import android.content.Intent;

import com.example.borja.marketingcomputacional.area_menu.fragments.DashboardMenu;
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
    static final int NUM_EXECUTIONS = 10; /* number of executions */

    static final int MY_PRODUCER = 0;  //The index of my producer

    // static final String SOURCE = "D:\Pablo\EncuestasCIS.xlsx";
    static final int SHEET_AGE_STUDIES = 1;
    static final int SHEET_POLITICAL_PARTIES = 2;
    static final String EOF = "EOF";

    private static ArrayList<Attribute> TotalAttributes = new ArrayList<>();
    private static ArrayList<Producer> Producers = new ArrayList<>();

    /* GA VARIABLES */
    private int BestWSC; /* Stores the best wsc found */
    private ArrayList<Product> Population;   //Private mPopu As List(Of List(Of Integer))
    private ArrayList<Integer> Fitness; /* * mFitness(i) = wsc of mPopu(i) */

    /* STATISTICAL VARIABLES */
    private ArrayList<Integer> Results = new ArrayList<>();
    private ArrayList<Integer> Initial_Results = new ArrayList<>();
    private ArrayList<Integer> Prices = new ArrayList<>();
    private int wscSum;

    private static ArrayList<CustomerProfile> CustomerProfiles = new ArrayList<>();

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
        generateAttributeRandom();
        StoredData.Atributos = TotalAttributes;

        generateCustomerProfiles();
        generareNumberOfCustomers();
        divideCustomerProfile();
        StoredData.Profiles = CustomerProfiles;

        generateProducers();
        StoredData.Producers = Producers;
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
        calculatePrice();
    }

    /**
     * Generating statistics about the PD problem
     */
    private void statisticsPD() throws Exception {

        double sum = 0; /*sum of customers achieved*/
        double initSum = 0; /*sum of initial customers*/
        int sumCust = 0; /*sum of the total number of customers*/
        double variance;
        double initVariance;
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
        StoredData.initMean = initSum / NUM_EXECUTIONS;
        variance = computeVariance(StoredData.mean);
        initVariance = computeVariance(StoredData.initMean);
        StoredData.stdDev = Math.sqrt(variance);
        StoredData.initStdDev = Math.sqrt(initVariance);
        StoredData.custMean = sumCust / NUM_EXECUTIONS;
        StoredData.percCust = 100 * StoredData.mean / StoredData.custMean;
        StoredData.initPercCust = 100 * StoredData.initMean / StoredData.custMean;
        StoredData.My_price = price / NUM_EXECUTIONS;
		
		/*MOSTRARLO*/
    }


    /*************************************** " AUXILIARY METHODS GENERATEINPUT()" ***************************************/

    /**
     * Creating the attributes and the possible values of them
     */
   /* private static void generateAttributeValor(List sheetData) {

        int MIN_VAL = 1;

        double number_valors = 0.0;
        for (int i = 4; i < sheetData.size(); i++) {
            // System.out.println("Celda [" + i + ", 0]: ");

            if (number_valors == 0) {
                Cell cell = (Cell) ((List) sheetData.get(i)).get(0);
                if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    number_valors = cell.getNumericCellValue() + 1;
                    TotalAttributes.add(new Attribute("Attribute " + (TotalAttributes.size() + 1), MIN_VAL, (int) number_valors - 1));
                } else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                    if (cell.getRichStringCellValue().equals("MMM"))
                        break;
                }
            }
            number_valors--;
        }
    }*/
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

    private static void showAttributes() {
        for (int k = 0; k < TotalAttributes.size(); k++) {
            System.out.println(TotalAttributes.get(k).getName());
            System.out.println(TotalAttributes.get(k).getMIN());
            System.out.println(TotalAttributes.get(k).getMAX());
        }
    }

    private static void showAvailableAttributes(ArrayList<Attribute> availableAttrs) {
        for (int k = 0; k < availableAttrs.size(); k++) {
            System.out.println(availableAttrs.get(k).getName());
            System.out.println(availableAttrs.get(k).getMIN());
            System.out.println(availableAttrs.get(k).getMAX());
            for (int j = 0; j < availableAttrs.get(k).getAvailableValues().size(); j++) {
                System.out.println(availableAttrs.get(k).getAvailableValues().get(j));
            }
        }
    }

    private static void showProducers() {
        for (int i = 0; i < Producers.size(); i++) {
            Producer p = Producers.get(i);
            System.out.println("PRODUCTOR " + (i + 1));
            for (int j = 0; j < TotalAttributes.size(); j++) {
                System.out.print(TotalAttributes.get(j).getName());
                System.out.println(":  Value -> " + p.getProduct().getAttributeValue().get(TotalAttributes.get(j)));
            }
        }
    }

   /* private static void showExcelData(List sheetData) {
        // Iterates the data and print it out to the console.
        for (int i = 0; i < sheetData.size(); i++) {
            List list = (List) sheetData.get(i);
            for (int j = 0; j < list.size(); j++) {
                Cell cell = (Cell) list.get(j);
                if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    System.out.print(cell.getNumericCellValue());
                } else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                    System.out.print(cell.getRichStringCellValue());
                } else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
                    System.out.print(cell.getBooleanCellValue());
                }
                if (j < list.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("");
        }
    }*/


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
            Producers.add(new_producer);
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
        return product;
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
        product.setPrice((int)( Math.random() * 400) + 100);
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
        Fitness.add(computeWSC(Population.get(MY_PRODUCER), MY_PRODUCER));
        BestWSC = Fitness.get(MY_PRODUCER);
        Initial_Results.add(BestWSC);

        for (int i = 1; i < NUM_POPULATION; i++) {

            if (i % 2 == 0) /*We create a random product*/
                Population.add(createRndProduct(Producers.get(MY_PRODUCER).getAvailableAttribute()));
            else /*We create a near product*/
                Population.add(createNearProduct(Producers.get(MY_PRODUCER).getAvailableAttribute(), (int) (CustomerProfiles.size() * Math.random())));  /////////??verificar//////////

            Fitness.add(computeWSC(Population.get(i), MY_PRODUCER));

            if (Fitness.get(i) > BestWSC) {
                BestWSC = Fitness.get(i);
                Producers.get(MY_PRODUCER).setProduct(Population.get(i).clone());
            }
        }
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
                k = 0;
                while (isTheFavourite && k < Producers.size()) {
                    if (k != prodInd) {

                        score = scoreProduct(CustomerProfiles.get(i).getSubProfiles().get(j), Producers.get(k).getProduct());

                        if (score > meScore)
                            isTheFavourite = false;

                        else if (score == meScore)
                            numTies += 1;
                    }
                    k++;
                }
				/*TODO: When there exists ties we loose some voters because of decimals (undecided voters)*/
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
            fitness.add(computeWSC(newPopu.get(i), MY_PRODUCER));
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
     * M�todo que dado un padre y una madre los cruza para obtener un hijo.
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

        return mutant;
    }


    /**
     * M�todo que dada la poblaci�n original y una nueva poblaci�n elige la siguente
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

    /*************************************** " AUXILIARY METHODS TO CALCULATE THE PRICE" ***************************************/

    private void calculatePrice() {
        int price_MyProduct = 0;
        int media_precios = 0;

        for(int i = 1; i < Producers.size(); i++){
            Product prod_competence = Producers.get(i).getProduct();
            double distance_product = getDistanceTo(prod_competence);

            if(distance_product == 0){
                price_MyProduct = prod_competence.getPrice();
                break;
            }

            price_MyProduct += prod_competence.getPrice() / distance_product;
            media_precios += prod_competence.getPrice();
        }
        media_precios /= Producers.size();
        Producers.get(MY_PRODUCER).getProduct().setPrice(price_MyProduct);
        Prices.add(price_MyProduct);

    }

    private double getDistanceTo(Product prod_competence) {
        Product my_product = Producers.get(MY_PRODUCER).getProduct();
        double distance = 0;
        for(int i = 0; i < TotalAttributes.size(); i++){
            distance += Math.pow(my_product.getAttributeValue().get(TotalAttributes.get(i)) - prod_competence.getAttributeValue().get(TotalAttributes.get(i)), 2);
        }
        distance = Math.sqrt(distance);
        return distance;
    }



//		// An excel file name. You can create a file name with a full path
//		// information.
//		String filename = "EncuestasCIS.xlsx";
//		// Create an ArrayList to store the data read from excel sheet.
//		List sheetData = new ArrayList();
//		FileInputStream fis = null;
//		try {
//			// Create a FileInputStream that will be use to read the excel file.
//			fis = new FileInputStream(filename);
//
//			// Create an excel workbook from the file system.
//			XSSFWorkbook workbook = new XSSFWorkbook(fis);
//
//			// Get the first sheet on the workbook.
//			XSSFSheet sheet = workbook.getSheetAt(0);
//
//			/*
//			 * When we have a sheet object in hand we can iterator on each
//			 * sheet's rows and on each row's cells. We store the data read on
//			 * an ArrayList so that we can printed the content of the excel to
//			 * the console.
//			 */
//			Iterator rows = sheet.rowIterator();
//			while (rows.hasNext()) {
//				XSSFRow row = (XSSFRow) rows.next();
//				Iterator cells = row.cellIterator();
//				List data = new ArrayList();
//				while (cells.hasNext()) {
//					XSSFCell cell = (XSSFCell) cells.next();
//					// System.out.println("A�adiendo Celda: " +
//					// cell.toString());
//					data.add(cell);
//				}
//				sheetData.add(data);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			if (fis != null) {
//				try {
//					fis.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}


}