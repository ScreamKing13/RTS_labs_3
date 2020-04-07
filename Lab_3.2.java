package com.example.rts.ui.lab2;

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

public class lab2Fragment extends Fragment {

    private lab2ViewModel slideshowViewModel;
    private final double P = 4.0;
    private final int[][] points =  {
            {0, 6},
            {1, 5},
            {3, 3},
            {2, 4}
    };
    private double w1, w2;
    private TextView resultsTextView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(lab2ViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_lab2, container, false);

        final EditText timeDeadLineEditText = (EditText) root.findViewById(R.id.deadLineEditText);
        final EditText learningRateEditText = (EditText) root.findViewById(R.id.learningRateEditText);
        final EditText iterationNumEditText = (EditText) root.findViewById(R.id.iterationsNumEditText);
        Button calculateButton = (Button) root.findViewById(R.id.calcButtonL2);
        resultsTextView = (TextView) root.findViewById(R.id.resultsTextViewL2);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int iterationsNum = Integer.parseInt(iterationNumEditText.getText().toString());
                    double learningRate = Double.parseDouble(learningRateEditText.getText().toString());
                    long timeDeadline = (long) Double.parseDouble(timeDeadLineEditText.getText().toString()) * 1_000_000_000;
                    fitModel(learningRate, iterationsNum, timeDeadline);

                } catch (NumberFormatException e) {
                    resultsTextView.setText("Введено невірне число!");
                }
            }
        });

        return root;
    }

    private void fitModel(double rate, int iterationsNum, long deadline) {
        double y;
        double delta;
        int counter = 0 ;
        boolean completed = false;
        long start = System.nanoTime();

        int index = 0;
        while (counter++ < iterationsNum && (System.nanoTime() - start) < deadline) {

            index %= 4;

            y = points[index][0] * w1 + points[index][1] * w2;

            if (managedToFit()) {
                completed = true;
                break;
            }

            delta = P - y;
            w1 += delta * points[index][0] * rate;
            w2 += delta * points[index][1] * rate;
            index++;
        }

        if (completed) {
            long execTimeMcs = (System.nanoTime() - start) / 1_000;
            resultsTextView.setText(String.format("\tУспішно!\n\tw1 = %-6.3f w2 = %-6.3f\n\tЧас виконання: %d мкс", w1, w2, execTimeMcs));

        } else {
            String verdict = "\tНе вдалось навчити модель за вказаний дедлайн!";
            if (counter >= iterationsNum) {
                verdict += "\n\tПотрібно більше ітерацій!";
            } else {
                verdict += "\n\tПотрібно більше часу!";
            }
            resultsTextView.setText(verdict);
        }

    }
    private boolean managedToFit() {
        return P < points[0][0] * w1 + points[0][1] * w2
                && P < points[1][0] * w1 + points[1][1] * w2
                && P > points[2][0] * w1 + points[2][1] * w2
                && P > points[3][0] * w1 + points[3][1] * w2;
    }
}
