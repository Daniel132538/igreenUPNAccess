package com.example.ecoproyect.ui.ranking;

import static android.content.Context.MODE_PRIVATE;

import static com.example.ecoproyect.AllConst.AllConst.nameSharedPreferences;
import static com.example.ecoproyect.AllConst.AllConst.urlServidor;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoproyect.Adapter.RankingAdapter;
import com.example.ecoproyect.AllConst.AllConst;
import com.example.ecoproyect.Application.MyApplication;
import com.example.ecoproyect.Dialog.LoadingDialog;
import com.example.ecoproyect.Model.RankingItem;
import com.example.ecoproyect.R;
import com.example.ecoproyect.databinding.FragmentRankingBinding;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RankingFragment extends Fragment {

    private FragmentRankingBinding binding;
    private RankingAdapter rankingAdapter;
    private ArrayList<RankingItem> rankingItems;
    private SharedPreferences sharedPreferences;
    ArrayAdapter<CharSequence> adapter;
    RankingViewModel rankingViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rankingViewModel =
                new ViewModelProvider(this).get(RankingViewModel.class);

        binding = FragmentRankingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.rankingRecyclerView.setHasFixedSize(true);
        binding.rankingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        rankingItems = new ArrayList<>();

        sharedPreferences = getContext().getSharedPreferences(nameSharedPreferences, MODE_PRIVATE);

        adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.transportasion_array,
                android.R.layout.simple_dropdown_item_1line
        );

        AutoCompleteTextView autoCompleteTextView = binding.spinnerTransportation;
        autoCompleteTextView.setText("Andando");
        autoCompleteTextView.setAdapter(adapter);
        //autoCompleteTextView.setFocusable(false);
        autoCompleteTextView.setClickable(true);
        //autoCompleteTextView.setCursorVisible(false);

        //autoCompleteTextView.setKeyListener(null);
        autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                autoCompleteTextView.showDropDown();
            }
        });

        rankingViewModel.rankingAdapters = ((MyApplication) getActivity().getApplication()).getRankingAdapters();

        if (rankingViewModel.rankingAdapters != null){
            binding.rankingRecyclerView.setAdapter(rankingViewModel.rankingAdapters.get(0));
        }

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // No se necesita implementar en este caso
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Este método se llama para cada cambio en el texto
                String selectedTransportation = charSequence.toString();

                // Realizar la lógica de selección aquí según el transporte seleccionado
                updateRankingAdapter(selectedTransportation);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // No se necesita implementar en este caso
            }
        });



        //final TextView textView = binding.textDashboard;
        //textView.setText(dashboardViewModel.getText().getValue().toString());
        //dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    private void updateRankingAdapter(String selectedTransportation) {
        if (rankingViewModel.rankingAdapters != null && rankingViewModel.rankingAdapters.size() > 0) {
            if (selectedTransportation.equals("Andando")) {
                if (rankingViewModel.rankingAdapters.get(0) != null) {
                    binding.rankingRecyclerView.setAdapter(rankingViewModel.rankingAdapters.get(0));
                }
            } else if (selectedTransportation.equals("Bici")) {
                if (rankingViewModel.rankingAdapters.get(1) != null) {
                    binding.rankingRecyclerView.setAdapter(rankingViewModel.rankingAdapters.get(1));
                }
            } else if (selectedTransportation.equals("Bici eléctrica")) {
                if (rankingViewModel.rankingAdapters.get(2) != null) {
                    binding.rankingRecyclerView.setAdapter(rankingViewModel.rankingAdapters.get(2));
                }
            } else if (selectedTransportation.equals("Patinete eléctrico")) {
                if (rankingViewModel.rankingAdapters.get(3) != null) {
                    binding.rankingRecyclerView.setAdapter(rankingViewModel.rankingAdapters.get(3));
                }
            } else if (selectedTransportation.equals("Bus")) {
                if (rankingViewModel.rankingAdapters.get(4) != null) {
                    binding.rankingRecyclerView.setAdapter(rankingViewModel.rankingAdapters.get(4));
                }
            } else if (selectedTransportation.equals("Total")) {
                if (rankingViewModel.rankingAdapters.get(5) != null) {
                    binding.rankingRecyclerView.setAdapter(rankingViewModel.rankingAdapters.get(5));
                }
            }
        }
    }

    /*@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }*/
}