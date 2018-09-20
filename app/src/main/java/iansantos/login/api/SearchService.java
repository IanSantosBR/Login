package iansantos.login.api;

import iansantos.login.model.StackOverflowSearch;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchService {

    String BASE_URL = "https://api.stackexchange.com/2.2/";

    @GET("search?order=desc&sort=activity&site=stackoverflow")
    Call<StackOverflowSearch> getSearch(@Query("tagged") String tagged);
}