package com.example.ecoproyect.Dialog;

import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.example.ecoproyect.R;

import java.util.ArrayList;

public class CustomDialogManager {
    private Dialog dialog;
    private ArrayList<ImageView>  stars;
    private TextView omitTextView;
    private AppCompatButton acceptButton;
    private int rate = 0;

    public CustomDialogManager(Dialog dialog) {
        this.dialog = dialog;
        stars = new ArrayList<>();
        stars.add(dialog.findViewById(R.id.star1));
        stars.add(dialog.findViewById(R.id.star2));
        stars.add(dialog.findViewById(R.id.star3));
        stars.add(dialog.findViewById(R.id.star4));
        stars.add(dialog.findViewById(R.id.star5));

        omitTextView = dialog.findViewById(R.id.txtOmitir);
        acceptButton = dialog.findViewById(R.id.btnAceptar);

        stars.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stars.get(0).setImageResource(R.drawable.baseline_star_rate_24);
                stars.get(1).setImageResource(R.drawable.baseline_star_border_24);
                stars.get(2).setImageResource(R.drawable.baseline_star_border_24);
                stars.get(3).setImageResource(R.drawable.baseline_star_border_24);
                stars.get(4).setImageResource(R.drawable.baseline_star_border_24);
                rate = 1;
            }
        });
        stars.get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stars.get(0).setImageResource(R.drawable.baseline_star_rate_24);
                stars.get(1).setImageResource(R.drawable.baseline_star_rate_24);
                stars.get(2).setImageResource(R.drawable.baseline_star_border_24);
                stars.get(3).setImageResource(R.drawable.baseline_star_border_24);
                stars.get(4).setImageResource(R.drawable.baseline_star_border_24);
                rate = 2;
            }
        });
        stars.get(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stars.get(0).setImageResource(R.drawable.baseline_star_rate_24);
                stars.get(1).setImageResource(R.drawable.baseline_star_rate_24);
                stars.get(2).setImageResource(R.drawable.baseline_star_rate_24);
                stars.get(3).setImageResource(R.drawable.baseline_star_border_24);
                stars.get(4).setImageResource(R.drawable.baseline_star_border_24);
                rate = 3;
            }
        });
        stars.get(3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stars.get(0).setImageResource(R.drawable.baseline_star_rate_24);
                stars.get(1).setImageResource(R.drawable.baseline_star_rate_24);
                stars.get(2).setImageResource(R.drawable.baseline_star_rate_24);
                stars.get(3).setImageResource(R.drawable.baseline_star_rate_24);
                stars.get(4).setImageResource(R.drawable.baseline_star_border_24);
                rate = 4;
            }
        });
        stars.get(4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stars.get(0).setImageResource(R.drawable.baseline_star_rate_24);
                stars.get(1).setImageResource(R.drawable.baseline_star_rate_24);
                stars.get(2).setImageResource(R.drawable.baseline_star_rate_24);
                stars.get(3).setImageResource(R.drawable.baseline_star_rate_24);
                stars.get(4).setImageResource(R.drawable.baseline_star_rate_24);
                rate = 5;
            }
        });


    }

    public void setOmitTextClickListener(View.OnClickListener listener) {
        omitTextView.setOnClickListener(listener);
    }

    public void setAcceptButtonClickListener(View.OnClickListener listener) {
        acceptButton.setOnClickListener(listener);
    }

    public int getRate(){
        return rate;
    }
}

