package com.example.rts.ui.lab1;

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

public class lab1Fragment extends Fragment {

    private lab1ViewModel galleryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(lab1ViewModel.class);
        View root = inflater.inflate(R.layout.fragment_lab1, container, false);
        final EditText numberToFactEditText = (EditText) root.findViewById(R.id.numberToFactEditText);
        final TextView resTextView = (TextView) root.findViewById(R.id.resultTextView);
        Button calculateButton = (Button) root.findViewById(R.id.calculateButton);
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int num = Integer.parseInt(numberToFactEditText.getText().toString());
                    if (isPrime(num)) resTextView.setText("Введене просте число!");
                    else if (num % 2 == 0) resTextView.setText("Введене парне число!");
                    else {
                        int[] parts = performFactorization(num);
                        resTextView.setText(String.format("A = %d, B = %d\nA * B = %d", parts[0], parts[1], num));
                    }
                } catch (NumberFormatException e) {
                    resTextView.setText("Введено не число!");
                }
            }
        });

        return root;
    }

    private int[] performFactorization(int num) {
        int[] result = new int[2];
        int k = 0;
        int x = (int) Math.ceil((Math.sqrt(num) + k));
        double y = Math.sqrt(Math.pow(x, 2) - num);
        while (y % 1 != 0) {
            k++;
            x = (int) (Math.sqrt(num) + k);
            y = Math.sqrt(Math.pow(x, 2) - num);
        }
        result[0] = (int) (x + y);
        result[1] = (int) (x - y);

        return result;
    }


    private boolean isPrime(int n) {
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0)
                return false;
        }
        return true;
    }
}
