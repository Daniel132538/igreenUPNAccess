package com.example.ecoproyect.ui.record;

import static android.content.Context.MODE_PRIVATE;

import static com.example.ecoproyect.AllConst.AllConst.nameSharedPreferences;
import static com.example.ecoproyect.AllConst.AllConst.urlServidor;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.ecoproyect.Adapter.RecordAdapter;
import com.example.ecoproyect.AllConst.AllConst;
import com.example.ecoproyect.Application.MyApplication;
import com.example.ecoproyect.Dialog.LoadingDialog;
import com.example.ecoproyect.Model.RecordItem;
import com.example.ecoproyect.Model.Statistics;
import com.example.ecoproyect.R;
import com.example.ecoproyect.databinding.FragmentRecordBinding;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecordFragment extends Fragment {
    private RecordAdapter recordAdapter;
    private FragmentRecordBinding binding;
    private SharedPreferences sharedPreferences;
    RecordViewModel recordViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        recordViewModel =
                new ViewModelProvider(this).get(RecordViewModel.class);

        binding = FragmentRecordBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.recordRecyclerView.setHasFixedSize(true);
        binding.recordRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        sharedPreferences = getContext().getSharedPreferences(nameSharedPreferences, MODE_PRIVATE);


        ArrayList<RecordItem> recordItems = ((MyApplication) getActivity().getApplication()).getRecordItems();

        if (recordItems != null){
            recordAdapter = new RecordAdapter(recordItems);
            binding.recordRecyclerView.setAdapter(recordAdapter);
        }


        //final TextView textView = binding.textDashboard;
        //textView.setText(dashboardViewModel.getText().getValue().toString());
        //dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}