package com.example.borja.marketingcomputacional.SimulatedAnnealing;

import android.util.Log;

import com.example.borja.marketingcomputacional.Problem.Problem;

/**
 * Created by Borja on 27/05/2016.
 */
public class SimulatedAnnealingAlgorithm extends Problem{

    private static SimulatedAnnealingAlgorithm SAInstance = null;

    private final double Start_TEMP = 1000;
    private double TEMPERATURE = Start_TEMP;
    private double coolingRate = 0.006;


    public static SimulatedAnnealingAlgorithm getInstance() {

        if (SAInstance == null)
            SAInstance = new SimulatedAnnealingAlgorithm();

        return SAInstance;
    }

    private double acceptanceProbability(int OLD_fitness, int NEW_fitness) {
        double ret;
        if (NEW_fitness > OLD_fitness)
            ret = 1;
        else
            ret = Math.exp((NEW_fitness - OLD_fitness) / TEMPERATURE);

        return ret;
    }


    @Override
    protected void startProblem() throws Exception {
        initializeSAProblem();
    }

    @Override
    protected Object solveProblem() throws Exception {
        Object bestObject = getBestObjFound();
        Object currency_object = getBestObjFound();

        TEMPERATURE = Start_TEMP;
        while (TEMPERATURE > 1) {

            Log.d("Temperature", TEMPERATURE + "");
            Object new_obj = changeObject(currency_object);

            int old_energy = getFitness(currency_object);
            int new_energy = getFitness(new_obj);

            //CALCULATE THE ACCEPTED FUNCTION
            if (acceptanceProbability(old_energy, new_energy) > Math.random()) {
                currency_object = new_obj;

                //ACTUALIZE THE BEST
                if(getFitness(currency_object) > getFitness(bestObject))
                    bestObject = currency_object;
            }

            // Cool system
            TEMPERATURE *= 1 - coolingRate;
        }
        return bestObject;
    }
}
