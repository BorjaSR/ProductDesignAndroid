package com.example.borja.marketingcomputacional.GeneticAlgorithm;

import com.example.borja.marketingcomputacional.general.StoredData;

import java.util.ArrayList;

/**
 * Created by Borja on 01/06/2016.
 */
public abstract class GeneticAlgorithm {

    static final int CROSSOVER_PROB = 80; /* % of crossover */
    static final int MUTATION_PROB = 1; /* % of mutation */
    static final int NUM_GENERATIONS = 100; /* number of generations */

    ArrayList<Object> Population = new ArrayList<>();
    ArrayList<Integer> Fitness = new ArrayList<>();

    public ArrayList<Object> solveGeneticAlgorithm() throws Exception {

        Population = createInitPopulation();

        for(int i = 0; i < Population.size(); i++)
            Fitness.add(getFitness(Population.get(i)));


        ArrayList<Object> NewPopulation = new ArrayList<>();
        ArrayList<Integer> newFitness = new ArrayList<>();
        for(int i = 0; i < NUM_GENERATIONS; i++){
            NewPopulation = createNewPopulation(newFitness);
        }

        return Population;
    }

    private ArrayList<Object> createNewPopulation(ArrayList<Integer> newFitness) throws Exception {

        int fitnessSum = computeFitnessSum();
        ArrayList<Object> newPopu = new ArrayList<>();
        int father, mother;
        Object son;

        for (int i = 0; i < Population.size(); i++) {
            father = chooseFather(fitnessSum);
            mother = chooseFather(fitnessSum);
            son = mutate(breed(father, mother));

            newPopu.add(son);

            newFitness.add(getFitness(newPopu.get(i)));

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

    protected abstract Object mutate(Object breed);

    protected abstract Object breed(int father, int mother);

    public abstract ArrayList<Object> createInitPopulation() throws Exception;

    protected abstract Integer getFitness(Object object) throws Exception;
}
