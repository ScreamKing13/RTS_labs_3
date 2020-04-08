package com.example.rts.ui.lab3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.rts.R;

import java.util.Random;

public class lab3Fragment extends Fragment {

    private lab3ViewModel lab3ViewModel;
    private Random rand;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        lab3ViewModel =
                ViewModelProviders.of(this).get(lab3ViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_lab3, container, false);
        final EditText aEditText = (EditText) root.findViewById(R.id.AeditText);
        final EditText bEditText = (EditText) root.findViewById(R.id.BeditText);
        final EditText cEditText = (EditText) root.findViewById(R.id.CeditText);
        final EditText dEditText = (EditText) root.findViewById(R.id.DeditText);
        final EditText yEditText = (EditText) root.findViewById(R.id.YeditText);
        final TextView resTextView = (TextView) root.findViewById(R.id.resultTextViewL3);
        Button calcButton = (Button) root.findViewById(R.id.calcButtonL3);

        calcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rand = new Random();
                try {
                    int a = Integer.parseInt(aEditText.getText().toString());
                    int b = Integer.parseInt(bEditText.getText().toString());
                    int c = Integer.parseInt(cEditText.getText().toString());
                    int d = Integer.parseInt(dEditText.getText().toString());
                    int y = Integer.parseInt(yEditText.getText().toString());
                    long start = System.nanoTime();
                    int[] xs = findSolution(a, b, c, d, y);
                    long execTimeMls = (System.nanoTime() - start) / 1_000_000;
                    resTextView.setText(
                            String.format("x1 = %d\nx2 = %d\nx3 = %d\nx4 = %d\nЧас виконання: %d мс",
                                    xs[0], xs[1], xs[2], xs[3], execTimeMls
                            )
                    );
                }
                catch (NumberFormatException e) {
                    resTextView.setText("Введені невірні дані!");
                }

            }
        });
        return root;
    }

    private int[][] generateStartingPopulation(int y) {
        int[][] population = new int[5 + rand.nextInt(y - 4)][4];
        for (int i = 0; i < population.length; i++) {
            for (int j = 0; j < population[0].length; j++) {
                population[i][j] = rand.nextInt(y / 2);
            }
        }
        return population;
    }

    private int[] findSolution(int a, int b, int c, int d, int y) {
        int[][] population = generateStartingPopulation(y);
        int[] abcd = {a, b, c, d};
        int index;
        int[] deltas;
        while (true) {
            deltas = fitnessFunction(population, abcd, y);
            if ((index = argZero(deltas)) != -1) {
                break;
            } else {
                double meanSurvOld = findMean(survivalLikelihood(deltas));
                int[][] newPopulation = newPopulation(deltas, population);
                if (meanSurvOld <
                        findMean(survivalLikelihood(fitnessFunction(newPopulation, abcd, y)))
                ) {
                    population = newPopulation;
                } else {
                    randomMutation(population, y);
                }
            }
        }
        return population[index];
    }

    private void randomMutation(int[][] population, int y) {
        for (int i = 0; i < population.length; i++) {
            int randIndex = rand.nextInt(population[0].length);
            population[i][randIndex] = rand.nextInt(y);
        }
    }

    private int[][] newPopulation(int[] deltas, int[][] population) {
        double[] survProb = survivalLikelihood(deltas);
        int[][] parentPairs = createPairs(survProb, population);
        return crossOverPairs(parentPairs, population);
    }

    private int[][] crossOverPairs(int[][] parentPairs, int[][] population) {
        int[][] newPopulation = new int[population.length][4];
        for (int i = 0; i < population.length; i++) {
            newPopulation[i] = crossOver(population[parentPairs[i][0]], population[parentPairs[i][1]]);
        }
        return newPopulation;
    }

    private int[] crossOver(int[] pair1, int[] pair2) {
        int bound = 1 + rand.nextInt(3);
        int[] child = new int[pair1.length];
        for (int i = 0; i < child.length; i++) {
            if (i < bound) {
                child[i] = pair1[i];
            } else {
                child[i] = pair2[i];
            }
        }
        return child;
    }

    private int[][] createPairs(double[] survProb, int[][] population) {
        int[][] pairs = new int[population.length][2];
        int candidatesNum = survProb.length / 2;
        int[] parents = new int[candidatesNum];
        int maxIndex;
        double max;
        for(int i = 0; i < candidatesNum; i++) {
            max = survProb[0];
            maxIndex = 0;
            for (int j = 1; j < survProb.length; j++) {
                if (survProb[j] > max) {
                    max = survProb[j];
                    maxIndex = j;
                }
            }
            survProb[maxIndex] = -1;
            parents[i] = maxIndex;
        }

        for (int i = 0; i < pairs.length;) {
            pairs[i][0] = parents[rand.nextInt(parents.length)];
            pairs[i][1] = parents[rand.nextInt(parents.length)];
            if (pairs[i][0] != pairs[i][1]) {
                i++;
            }
        }
        return pairs;
    }

    private double[] survivalLikelihood(int[] deltas) {
        double cummProb = 0;
        double[] surv = new double[deltas.length];
        for (int i = 0; i < deltas.length; i++) {
            cummProb += (double) 1 / deltas[i];
        }
        for (int i = 0; i < deltas.length; i++) {
            surv[i] = ((double) 1 / deltas[i]) / cummProb;
        }
        return surv;
    }

    private int[] fitnessFunction(int[][] population, int[] abcd, int y) {
        int[] deltas = new int[population.length];
        for (int i = 0; i < population.length; i++) {
            for (int j = 0; j < population[0].length; j++) {
                deltas[i] += population[i][j] * abcd[j];
            }
            deltas[i] = Math.abs(deltas[i] - y);
        }
        return deltas;
    }

    private int argZero(int[] array) {
        int index = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 0) {
                index = i;
                break;
            }
        }
        return index;
    }

    private double findMean(double[] array) {
        double mean = 0;
        for (int i = 0; i < array.length; i++) {
            mean += array[i];
        }
        mean /= array.length;
        return mean;
    }
}
