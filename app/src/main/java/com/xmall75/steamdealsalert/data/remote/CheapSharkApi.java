package com.xmall75.steamdealsalert.data.remote;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CheapSharkApi {

    String BASE_URL = "https://www.cheapshark.com/api/1.0/";

    @GET("deals?storeID=1&onSale=1")
    Call<List<DealDto>> getDeals(
            @Query("upperPrice") double upperPrice
    );
}