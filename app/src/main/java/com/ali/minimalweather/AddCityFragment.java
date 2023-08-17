package com.ali.minimalweather;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ali.minimalweather.DataBase.City;
import com.ali.minimalweather.DataBase.CityDBHelper;
import com.ali.minimalweather.databinding.FragmentAddCityBinding;
import com.blongho.country_data.World;

import java.util.ArrayList;
import java.util.List;

import info.hoang8f.widget.FButton;


public class AddCityFragment extends DialogFragment {

    public static final String TAG = "AddCityFragment";
    private FragmentAddCityBinding binding;
    private List<City> cityList;
    private CityDBHelper dbHelper;
    private RecyclerView recyclerView;
    private addCityRecyclerViewAdapter cityRecyclerViewAdapter;
    private AddCityInterface iActivity;

    public AddCityFragment() {
        // Required empty public constructor
    }


    public static AddCityFragment newInstance(String param1, String param2) {
        AddCityFragment fragment = new AddCityFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new CityDBHelper(getContext());
        cityList = dbHelper.getCities("selected = 1", null);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddCityBinding.inflate(inflater, container, false);

        // init for getting country flags
        World.init(getContext());
        recyclerView = binding.recyclerView;
        cityList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cityRecyclerViewAdapter = new addCityRecyclerViewAdapter(cityList);
        recyclerView.setAdapter(cityRecyclerViewAdapter);

        SearchView searchView = binding.searchView;
        searchView.setIconified(false);
        searchView.setQueryHint(getResources().getString(R.string.city_name_in_English));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                cityList = dbHelper.searchCityByName(query, "20");
                if (cityList == null)
                    cityList = new ArrayList<>();
                cityRecyclerViewAdapter = new addCityRecyclerViewAdapter(cityList);
                recyclerView.setAdapter(cityRecyclerViewAdapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        return binding.getRoot();
    }


    //onResume for set width to 90 percent of screen
    @Override
    public void onResume() {
        super.onResume();

        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            Point size = new Point();
            Display display = window.getWindowManager().getDefaultDisplay();
            display.getSize(size);

            int width = size.x;
            window.setLayout((int) (width * 0.9), WindowManager.LayoutParams.WRAP_CONTENT);
        }

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            iActivity = (AddCityInterface) getActivity();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    // for memory leaking
    @Override
    public void onDetach() {
        iActivity = null;
        super.onDetach();
    }


    //interface addCity
    public interface AddCityInterface {
        void addCity(int id);
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        final FButton btnAdd;
        final TextView cityName;
        final ImageView countryFlag;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            btnAdd = itemView.findViewById(R.id.btn_city_layout);
            btnAdd.setButtonColor(Color.argb(255, 17, 255, 17));
            cityName = itemView.findViewById(R.id.city_name);
            countryFlag = itemView.findViewById(R.id.country_flag);
        }
    }

    //RecyclerView Adapter & viewHolder
    public class addCityRecyclerViewAdapter extends RecyclerView.Adapter<viewHolder> {

        private final List<City> cityList;

        public addCityRecyclerViewAdapter(List<City> cityList) {
            this.cityList = cityList;
        }

        @NonNull
        @Override
        public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.city_layout_add_city_fragment, parent, false);

            return new viewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull viewHolder holder, int position) {
            final City city = cityList.get(position);
            holder.cityName.setText(city.getCityName());
            holder.countryFlag.setImageResource(World.getFlagOf(city.getCountryCode()));
            holder.btnAdd.setOnClickListener(v -> {
                iActivity.addCity(city.getCity_id());
                dismiss();
            });
        }

        @Override
        public int getItemCount() {
            return cityList.size();
        }
    }


}
