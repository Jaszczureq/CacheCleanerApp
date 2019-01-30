package com.memedomain.cachecleaner;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import java.util.List;

public interface OpenLibraryClient {

    String KEY = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTU0ODkwNzE3MH0.La71udRQ_iCrDtHwjmorVneS80OfPcnm-ZKosqbXtHgIW_EOY_fpfSgJVFnY8VnpmVExqHKEHJfyUOHo_rLC3g";

    @Headers({"Accept: application/json", "Authorization: Bearer " + KEY})
    @GET("adres")
    Call<List<Adres>> getAdreses();

    @Headers({"Accept: application/json", "Authorization: Bearer " + KEY})
    @GET("salas")
    Call<List<Sala>> getSalas();

    @Headers({"Accept: application/json", "Authorization: Bearer " + KEY})
    @POST("salas")
    Call<Sala> postSalas(@Body Sala sala);
//
//    @Headers({"Accept: application/json", "Authorization: Bearer " + KEY})
//    @GET("lekarzs")
//    Call<List<Lekarz>> getLekarzs();
//
//    @Headers({"Accept: application/json", "Authorization: Bearer " + KEY})
//    @GET("pacjents")
//    Call<List<Pacjent>> getPacjents();
}
