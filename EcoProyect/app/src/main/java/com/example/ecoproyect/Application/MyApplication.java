package com.example.ecoproyect.Application;

import android.app.Application;

import com.example.ecoproyect.Adapter.RankingAdapter;
import com.example.ecoproyect.Model.CouponItem;
import com.example.ecoproyect.Model.RankingItem;
import com.example.ecoproyect.Model.RecordItem;
import com.example.ecoproyect.Model.Statistics;
import com.example.ecoproyect.Model.Umbral;
import com.example.ecoproyect.Model.Usuario;

import java.util.ArrayList;

public class MyApplication extends Application {
    ArrayList<Umbral> umbrals;
    ArrayList<CouponItem> couponItems;
    ArrayList<RankingAdapter> rankingAdapters;
    ArrayList<RecordItem> recordItems;
    Statistics statistics, statisticsMonth;
    Usuario usuario;

    public ArrayList<Umbral> getUmbrals() {
        return umbrals;
    }

    public void setUmbrals(ArrayList<Umbral> umbrals) {
        this.umbrals = umbrals;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Statistics getStatisticsMonth() {
        return statisticsMonth;
    }

    public void setStatisticsMonth(Statistics statisticsMonth) {
        this.statisticsMonth = statisticsMonth;
    }

    public ArrayList<CouponItem> getCouponItems() {
        return couponItems;
    }

    public void setCouponItems(ArrayList<CouponItem> couponItems) {
        this.couponItems = couponItems;
    }

    public ArrayList<RankingAdapter> getRankingAdapters() {
        return rankingAdapters;
    }

    public void setRankingAdapters(ArrayList<RankingAdapter> rankingAdapters) {
        this.rankingAdapters = rankingAdapters;
    }

    public ArrayList<RecordItem> getRecordItems() {
        return recordItems;
    }

    public void setRecordItems(ArrayList<RecordItem> recordItems) {
        this.recordItems = recordItems;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }
}