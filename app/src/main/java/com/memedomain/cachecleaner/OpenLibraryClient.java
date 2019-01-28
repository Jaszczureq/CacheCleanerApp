package com.memedomain.cachecleaner;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

import java.util.List;

public interface OpenLibraryClient {

    String KEY = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTU0ODUzOTYzOX0.4VSTVSbGr-l_CN-2GF194GSy8sPqXVX-hX-OrCvHHb8ybkMoOXKgbsvLntqIKa_IHn6Tg66cbQWAp_Rc9cSRCA";

    @Headers({"Accept: application/json", "Authorization: Bearer " + KEY})
    @GET("adres")
    Call<List<Adres>> getAdreses();

    @Headers({"Accept: application/json", "Authorization: Bearer " + KEY})
    @GET("lekarzs")
    Call<List<Lekarz>> getLekarzs();

    @Headers({"Accept: application/json", "Authorization: Bearer " + KEY})
    @GET("pacjents")
    Call<List<Pacjent>> getPacjents();
}
