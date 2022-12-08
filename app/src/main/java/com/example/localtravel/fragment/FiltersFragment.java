package com.example.localtravel.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localtravel.PokeapiService.PokeapiService;
import com.example.localtravel.R;
import com.example.localtravel.adapters.ListaPokemonAdapter;
import com.example.localtravel.models.Pokemon;
import com.example.localtravel.models.PokemonRespuesta;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FiltersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FiltersFragment extends Fragment {
    private Retrofit retrofit;
    private static final String TAG="POKEDEX";
    View vista;
    private RecyclerView recyclerView;
    private ListaPokemonAdapter listaPokemonAdapter;
    private int offset;
    private boolean aptoParaCargar;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FiltersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FiltersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FiltersFragment newInstance(String param1, String param2) {
        FiltersFragment fragment = new FiltersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       vista=inflater.inflate(R.layout.fragment_filters, container, false);
        recyclerView=vista.findViewById(R.id.recyclerView);
        listaPokemonAdapter= new ListaPokemonAdapter(getContext());
        recyclerView.setAdapter(listaPokemonAdapter);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager=new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0){
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
                    if (aptoParaCargar){
                        if ((visibleItemCount+pastVisibleItems) >= totalItemCount){
                            Log.i(TAG, "Llegamos al final");
                            aptoParaCargar=false;
                            offset +=20;
                            obtenerDatos(offset);
                        }
                    }
                }
            }
        });

        retrofit=new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        aptoParaCargar=true;
        offset =0;
        obtenerDatos(offset);
        return vista;

    }

    private void obtenerDatos(int offset) {
        PokeapiService service= retrofit.create(PokeapiService.class);
        Call<PokemonRespuesta> pokemonRespuestaCall= service.obtenerListaPokemon(20,offset);

        pokemonRespuestaCall.enqueue(new Callback<PokemonRespuesta>() {
            @Override
            public void onResponse(Call<PokemonRespuesta> call, Response<PokemonRespuesta> response) {
                aptoParaCargar = true;
                if (response.isSuccessful()){
                    PokemonRespuesta pokemonRespuesta=response.body();
                    ArrayList<Pokemon> listaPokemon=pokemonRespuesta.getResults();
                    listaPokemonAdapter.adicionarListaPokemon(listaPokemon);
                    for (int i=0;i<listaPokemon.size(); i++){
                        Pokemon pokemon=listaPokemon.get(i);
                        Log.i(TAG,"Pokemon"+pokemon.getName());
                    }
                }else {
                    Log.e(TAG, "OnResponse"+response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<PokemonRespuesta> call, Throwable t) {
                aptoParaCargar = true;
                Log.e(TAG, "onFailure"+t.getMessage());
            }
        });
    }
}