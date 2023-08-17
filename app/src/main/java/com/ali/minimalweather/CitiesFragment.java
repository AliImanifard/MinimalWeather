package com.ali.minimalweather;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ali.minimalweather.DataBase.City;
import com.ali.minimalweather.DataBase.CityDBHelper;
import com.ali.minimalweather.databinding.FragmentCitiesBinding;
import com.blongho.country_data.World;
import com.scalified.fab.ActionButton;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import info.hoang8f.widget.FButton;


public class CitiesFragment extends Fragment {

    public static final String TAG = "CitiesFragment";
    private static CityDBHelper dbHelper;
    private static RecyclerView recyclerView;
    protected MainActivity mainActivity;
    protected Context context;
    private FragmentCitiesBinding binding;
    private List<City> cityList;
    private CityRecyclerViewAdapter cityRecyclerViewAdapter;


    public CitiesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        dbHelper = new CityDBHelper(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = (MainActivity) getActivity();

        // Inflate the layout for this fragment
        binding = FragmentCitiesBinding.inflate(inflater, container, false);

        // init for getting country flags
        World.init(getContext());

        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        // Button to Show cities Weather from shown Location Based
        //if a city shown = true -> set button color to Gray
        if (dbHelper.getShownCity().getCity_id() == 0) {
            binding.btnLocationShown.setButtonColor(getResources().getColor(R.color.gray_400));
            binding.btnLocationShown.setShadowColor(getResources().getColor(R.color.gray_600));
        } else {
            binding.btnLocationShown.setButtonColor(Color.rgb(46, 197, 217));
            binding.btnLocationShown.setShadowColor(Color.rgb(23, 133, 147));
        }

        updateDisplay();

        binding.fab.setShowAnimation(ActionButton.Animations.JUMP_FROM_RIGHT);
        binding.fab.setHideAnimation(ActionButton.Animations.ROLL_TO_DOWN);
        binding.fab.show();
        binding.fab.setOnClickListener(v -> {

            binding.fab.hide();

            AddCityFragment addCityFragment = new AddCityFragment();
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            addCityFragment.show(fragmentTransaction, AddCityFragment.TAG);

            binding.fab.show();
        });


        // btn location shown
        binding.btnLocationShown.setOnClickListener(v -> {
            binding.btnLocationShown.setButtonColor(getResources().getColor(R.color.gray_400));
            binding.btnLocationShown.setShadowColor(getResources().getColor(R.color.gray_600));

            binding.btnLocationShown.invalidate();

            mainActivity.showLoadingToast();

            mainActivity.updateDisplay("locationButtonBased");
            dbHelper.updateCityShown(dbHelper.getShownCity().getCity_id(), false);
        });

        mainActivity.currentCitiesFragment = this;

        return binding.getRoot();
    }


    protected void displayReceivedData(int id) {
        dbHelper.updateCitySelected(id, true);
        updateDisplay();
    }


    private void updateDisplay() {
        cityList = dbHelper.getCities("selected = 1", null);
        cityRecyclerViewAdapter = new CityRecyclerViewAdapter(cityList, dbHelper);
        recyclerView.setAdapter(cityRecyclerViewAdapter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof MainActivity)
            this.mainActivity = (MainActivity) context;
    }

    //RecyclerView Adapter & viewHolder
    private static class viewHolder extends RecyclerView.ViewHolder {

        final FButton btnDelete;
        final FButton btnShown;
        final TextView cityName;
        final CircleImageView countryFlag;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            btnDelete = itemView.findViewById(R.id.btn_city_layout);
            btnDelete.setButtonColor(Color.argb(255, 255, 17, 17));
            btnDelete.setCornerRadius(15);
            btnShown = itemView.findViewById(R.id.btn_city_layout_shown);
            btnShown.setCornerRadius(15);
            cityName = itemView.findViewById(R.id.city_name);
            countryFlag = itemView.findViewById(R.id.country_flag);
        }
    }

    private class CityRecyclerViewAdapter extends RecyclerView.Adapter<viewHolder> {

        private final CityDBHelper dbHelper;
        private List<City> cityList;

        public CityRecyclerViewAdapter(List<City> cityList, CityDBHelper dbHelper) {
            this.cityList = cityList;
            this.dbHelper = dbHelper;
        }

        @NonNull
        @Override
        public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.city_layout_cities_fragment, parent, false);

            return new viewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull viewHolder holder, int position) {
            final City city = cityList.get(position);
            holder.cityName.setText(city.getCityName());
            holder.countryFlag.setImageResource(World.getFlagOf(city.getCountryCode()));

            // Button to Delete cities from selected
            holder.btnDelete.setOnClickListener(v -> {
                dbHelper.updateCitySelected(city.getCity_id(), false);
                cityList.remove(city);
                notifyItemRemoved(holder.getLayoutPosition());
            });


            // Button to Show cities Weather from shown
            //if a city shown = true -> set button color to Gray
            if (dbHelper.getShownCity().getCity_id() == city.getCity_id()) {

                holder.btnShown.setButtonColor(getResources().getColor(R.color.gray_400));
                holder.btnShown.setShadowColor(getResources().getColor(R.color.gray_600));

            } else {

                holder.btnShown.setButtonColor(Color.rgb(46, 197, 217));
                holder.btnShown.setShadowColor(Color.rgb(23, 133, 147));
            }


            holder.btnShown.setOnClickListener(v -> {
                if (holder.btnShown.getButtonColor() == Color.rgb(46, 197, 217)) {

                    dbHelper.updateCityShown(city.getCity_id(), true);

                    holder.btnShown.setButtonColor(getResources().getColor(R.color.gray_400));
                    holder.btnShown.setShadowColor(getResources().getColor(R.color.gray_600));

                    mainActivity.showLoadingToast();

                    updateDataUsingDiffUtil(cityList);

                    mainActivity.updateDisplay("shownButtonBased");

                }
            });
        }


        @Override
        public int getItemCount() {
            return cityList.size();
        }


        public void updateDataUsingDiffUtil(List<City> newData) {
            DiffUtil.Callback callback = new MyDiffUtil<>(cityList, newData);
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
            cityList = newData;
            result.dispatchUpdatesTo(this);
        }

    }

}