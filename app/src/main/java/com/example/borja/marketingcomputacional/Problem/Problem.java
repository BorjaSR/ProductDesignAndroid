package com.example.borja.marketingcomputacional.Problem;

import com.example.borja.marketingcomputacional.GeneticAlgorithm.SubProfile;
import com.example.borja.marketingcomputacional.general.Attribute;
import com.example.borja.marketingcomputacional.general.CustomerProfile;
import com.example.borja.marketingcomputacional.general.LinkedAttribute;
import com.example.borja.marketingcomputacional.general.Producer;
import com.example.borja.marketingcomputacional.general.Product;
import com.example.borja.marketingcomputacional.general.StoredData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Borja on 14/06/2016.
 */
public class Problem {


    static final double KNOWN_ATTRIBUTES = 100;
    static final int CROSSOVER_PROB = 80; /* % of crossover */
    static final int MUTATION_PROB = 1; /* % of mutation */
    static final int NUM_POPULATION = 20; /* number of population */
    static final int RESP_PER_GROUP = 20; /* * We divide the respondents of each
                                             * profile in groups of
											 * RESP_PER_GROUP respondents*/

    static final int NUM_EXECUTIONS = 1; /* number of executions */

    static final int MY_PRODUCER = 0;  //The index of my producer


    private static ArrayList<CustomerProfile> CustomerProfiles = new ArrayList<>();
    private static ArrayList<Attribute> TotalAttributes = new ArrayList<>();
    private static ArrayList<Producer> Producers = new ArrayList<>();

    /* STATISTICAL VARIABLES */
    private ArrayList<ArrayList<Integer>> Results = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> Initial_Results = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> Prices = new ArrayList<>();
    private int wscSum;

    /* GA VARIABLES */
    private ArrayList<Integer> BestWSC = new ArrayList<>(); /* Stores the best wsc found */
    private ArrayList<Product> Population = new ArrayList<>();  //Private mPopu As List(Of List(Of Integer))
    private ArrayList<Integer> Fitness = new ArrayList<>(); /* * mFitness(i) = wsc of mPopu(i) */


    private int CHANGE_ATTRIBUTE_PROB = 40;

    /****************************************************************************************************
     * GENETIC ALGORITHM                                    *
     ****************************************************************************************************/

    protected Object breed(Object father, Object mother) {
        Product son = new Product();
        /*Random value in range [0,100)*/
        int crossover = (int) (100 * Math.random());
        int rndVal;

        if (crossover <= CROSSOVER_PROB) {//El hijo sera una mezcla de la madre y el padre
            HashMap<Attribute, Integer> crossover_attributeValue = new HashMap<>();
            for (int i = 0; i < TotalAttributes.size(); i++) { //With son

                rndVal = (int) (2 * Math.random()); /*Generamos aleatoriamente un 0 (padre) o un 1 (madre).*/
                if (rndVal == 0)
                    crossover_attributeValue.put(TotalAttributes.get(i), ((Product) father).getAttributeValue().get(TotalAttributes.get(i)));
                else
                    crossover_attributeValue.put(TotalAttributes.get(i), ((Product) mother).getAttributeValue().get(TotalAttributes.get(i)));
            }
            son.setAttributeValue(crossover_attributeValue);
        } else {//El hijo seria completamente igual a la madre o al padre
            rndVal = (int) (2 * Math.random()); /*Generamos aleatoriamente un 0 (padre) o un 1 (madre).*/

            if (rndVal == 0)
                son = ((Product) father).clone();
            else
                son = ((Product) mother).clone();
        }
        return son;
    }

    protected Object mutate(Object indiv) {
        Product mutant = ((Product) indiv).clone();
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

    public ArrayList<Object> createInitPopulation() throws Exception {
        ArrayList<Object> Population = new ArrayList<>();
        Fitness = new ArrayList<>();
        BestWSC = new ArrayList<>();

        for (int i = 0; i < Producers.get(MY_PRODUCER).getProducts().size(); i++) {
            Population.add((Producers.get(MY_PRODUCER).getProducts().get(i)).clone());

            if (StoredData.Fitness == StoredData.Customers)
                Fitness.add(computeWSC((Product) Population.get(i), MY_PRODUCER));
            else
                Fitness.add(computeBenefits((Product) Population.get(i), MY_PRODUCER));
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
                Fitness.add(computeWSC((Product) Population.get(i), MY_PRODUCER));
            else
                Fitness.add(computeBenefits((Product) Population.get(i), MY_PRODUCER));

            int worstIndex = isBetweenBest(Fitness.get(i));
            if (worstIndex != -1) {
                BestWSC.set(worstIndex, Fitness.get(i));
                Producers.get(MY_PRODUCER).getProducts().set(worstIndex, (Product) Population.get(i));
            }
        }

        return Population;
    }

    protected Integer getFitness(Object object) throws Exception {
        if (StoredData.Fitness == StoredData.Customers)
            return computeWSC((Product) object, MY_PRODUCER);
        else
            return computeBenefits((Product) object, MY_PRODUCER);
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


    /****************************************************************************************************
     * MINIMAX                                              *
     ****************************************************************************************************/

    protected void setFitnessAcumulated(int i, ArrayList<Integer> customersAcumulated) {
        Producers.get(i).setCustomersGathered(customersAcumulated);
    }

    public Object getObject(int playerIndex) {
        return Producers.get(playerIndex).getProduct();
    }

    public Integer getFitness(Object object, int index) throws Exception {
        if (StoredData.Fitness == StoredData.Benefits)
            ((Product) object).setPrice(calculatePrice((Product) object));

        if (StoredData.Fitness == StoredData.Benefits)
            return computeBenefits((Product) object, index);
        else
            return computeWSC((Product) object, index);
    }

    public int getDimens() {
        return TotalAttributes.size();
    }

    protected int getSolutionsSpace(int dimen) {
        return TotalAttributes.get(dimen).getMAX();
    }

    protected int getSolution(int playerIndex, int dimension) {
        return Producers.get(playerIndex).getProduct().getAttributeValue().get(TotalAttributes.get(dimension));
    }

    protected boolean isPosibleToChange(int playerIndex, int dimension, int attrVal) {
        return Producers.get(playerIndex).getAvailableAttribute().get(dimension).getAvailableValues().get(attrVal);
    }

    protected void changeChild(Object o, int dimension, int solutionSpaceIndex) {
        ((Product) o).getAttributeValue().put(TotalAttributes.get(dimension), solutionSpaceIndex);
    }

    protected void setSolution(int playerIndex, int dimension, int solution) {
        Producers.get(playerIndex).getProduct().getAttributeValue().put(TotalAttributes.get(dimension), solution);
    }


    /****************************************************************************************************
     * PARTICLE SWARM OPTIMITATION                                    *
     ****************************************************************************************************/


    protected int getDimensions() {
        return TotalAttributes.size();
    }

    public ArrayList<Object> createInitSwarm() throws Exception {

        ArrayList<Object> Population = new ArrayList<>();
        Fitness = new ArrayList<>();
        BestWSC = new ArrayList<>();

        for (int i = 0; i < Producers.get(MY_PRODUCER).getProducts().size(); i++) {
            Population.add((Producers.get(MY_PRODUCER).getProducts().get(i)).clone());

            if (StoredData.Fitness == StoredData.Customers)
                Fitness.add(computeWSC((Product) Population.get(i), MY_PRODUCER));
            else
                Fitness.add(computeBenefits((Product) Population.get(i), MY_PRODUCER));
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
                Fitness.add(computeWSC((Product) Population.get(i), MY_PRODUCER));
            else
                Fitness.add(computeBenefits((Product) Population.get(i), MY_PRODUCER));


            int worstIndex = isBetweenBest(Fitness.get(i));
            if (worstIndex != -1) {
                BestWSC.set(worstIndex, Fitness.get(i));
                Producers.get(MY_PRODUCER).getProducts().set(worstIndex, (Product) Population.get(i));
            }
        }

        return Population;
    }

    protected Integer getLocationValue(Object obj, int dimen) {
        return ((Product) obj).getAttributeValue().get(TotalAttributes.get(dimen));
    }

    protected void updateLocation(Object obj, int dimen, int new_value_for_location) {
        ((Product) obj).getAttributeValue().put(TotalAttributes.get(dimen), new_value_for_location);
    }


    /****************************************************************************************************
     * SIMUMLATED ANNEALING                                            *
     ****************************************************************************************************/

    public int getFitness_SA(Object origin, int productIndex) throws Exception {
        if (StoredData.Fitness == StoredData.Customers) {
            return computeWSC_SA((Product) origin, productIndex);
        } else {
            return computeBenefits_SA((Product) origin, MY_PRODUCER, productIndex);
        }
    }


    private Integer computeBenefits_SA(Product product, int myProducer, int productIndex) throws Exception {
        return computeWSC_SA(product, productIndex) * product.getPrice();
    }

    /***
     * Computing the weighted score of the producer
     * prodInd is the index of the producer
     *
     * @throws Exception
     **/
    private int computeWSC_SA(Product product, int productIndex) throws Exception {
        int wsc = 0;
        boolean isTheFavourite;
        int meScore, score, k, p, numTies;

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
                        if (k != MY_PRODUCER || p != productIndex) {

                            score = scoreProduct(CustomerProfiles.get(i).getSubProfiles().get(j), Producers.get(k).getProducts().get(p));

                            if (StoredData.isAttributesLinked)
                                score += scoreLinkedAttributes(CustomerProfiles.get(i).getLinkedAttributes(), product);

                            if (score > meScore)
                                isTheFavourite = false;

                            else if (score == meScore)
                                numTies += 1;
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


    public Object changeObject(Object currency_product) {

        Product originProduct = (Product) currency_product;
        HashMap<Attribute, Integer> AUXattributeValue = new HashMap<>();
        for (int q = 0; q < TotalAttributes.size(); q++) {
            AUXattributeValue.put(TotalAttributes.get(q), originProduct.getAttributeValue().get(TotalAttributes.get(q)));
        }
        Product new_product = new Product(AUXattributeValue);
        new_product.setPrice(originProduct.getPrice());


        //CHANGE SOME ATTRIBUTES
        for (int j = 0; j < TotalAttributes.size(); j++) {
            if ((Math.random() * 100) < CHANGE_ATTRIBUTE_PROB) {
                int new_attr_value = (int) (Math.random() * TotalAttributes.get(j).getMAX());
                new_product.getAttributeValue().put(TotalAttributes.get(j), new_attr_value);
            }
        }

        return new_product;
    }
}
