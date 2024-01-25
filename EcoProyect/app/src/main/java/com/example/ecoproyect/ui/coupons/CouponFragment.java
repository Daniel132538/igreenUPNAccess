package com.example.ecoproyect.ui.coupons;

import static com.example.ecoproyect.AllConst.AllConst.urlServidor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecoproyect.Adapter.CouponAdapter;
import com.example.ecoproyect.AllConst.AllConst;
import com.example.ecoproyect.Application.MyApplication;
import com.example.ecoproyect.Dialog.LoadingDialog;
import com.example.ecoproyect.Model.CouponItem;

import com.example.ecoproyect.databinding.FragmentCouponBinding;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CouponFragment extends Fragment {
    private FragmentCouponBinding binding;
    //private ArrayList<CouponItem> couponItems;
    private CouponAdapter couponAdapter;
    CouponViewModel couponViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
             couponViewModel =
                new ViewModelProvider(this).get(CouponViewModel.class);

        binding = FragmentCouponBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.couponRecyclerView.setHasFixedSize(true);
        binding.couponRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        couponViewModel.couponItems = ((MyApplication)getActivity().getApplication()).getCouponItems();
        if (couponViewModel.couponItems != null){
            couponAdapter = new CouponAdapter(getActivity(), couponViewModel.couponItems);
            binding.couponRecyclerView.setAdapter(couponAdapter);
        }

        /*couponItems.add(new CouponItem(2, "dechatlon", R.drawable.ic_home_black_24dp));
        couponItems.add(new CouponItem(10, "comedor", R.drawable.ic_home_black_24dp));
        couponItems.add(new CouponItem(5, "dechatlon", R.drawable.ic_home_black_24dp));
        couponItems.add(new CouponItem(10, "dechatlon", R.drawable.ic_home_black_24dp));
        couponItems.add(new CouponItem(15, "comedor", R.drawable.ic_home_black_24dp));
        couponItems.add(new CouponItem(20, "upna", R.drawable.ic_home_black_24dp));
        couponItems.add(new CouponItem(10, "copisteria", R.drawable.ic_home_black_24dp));
        couponItems.add(new CouponItem(15, "comedor", R.drawable.ic_home_black_24dp));*/



        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}