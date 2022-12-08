package com.example.localtravel.PokeapiService;

import com.example.localtravel.models.PokemonRespuesta;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PokeapiService{

    @GET ("pokemon")
    Call<PokemonRespuesta> obtenerListaPokemon(@Query("limit")int limit,@Query("offset") int offset);
}
