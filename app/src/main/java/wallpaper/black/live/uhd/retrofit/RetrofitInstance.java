package wallpaper.black.live.uhd.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    public static String baseURL = "https://njapplications.com/BlackWall/API/";
    public static RetrofitInstance retrofitInstance;
    public RetroApiInterface apiInterface;

    public RetrofitInstance() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = retrofit.create(RetroApiInterface.class);
    }

    public static RetrofitInstance getInstance() {
        if (retrofitInstance == null) {
            retrofitInstance = new RetrofitInstance();
        }
        return retrofitInstance;
    }

    public void destroy(){
        retrofitInstance=null;
        baseURL=null;
        apiInterface = null;
    }
}
