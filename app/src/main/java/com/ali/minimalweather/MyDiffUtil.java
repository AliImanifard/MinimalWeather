package com.ali.minimalweather;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;

import com.ali.minimalweather.DataBase.City;

import java.util.List;
import java.util.Objects;

public class MyDiffUtil<T> extends DiffUtil.Callback {

    private final List<T> myOldList;
    private final List<T> myNewList;
    private T oldItem;
    private T newItem;


    public MyDiffUtil(List<T> myOldList, List<T> myNewList) {
        this.myOldList = myOldList;
        this.myNewList = myNewList;

    }

    @Override
    public int getOldListSize() {
        return myOldList.size();
    }

    @Override
    public int getNewListSize() {
        return myNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {

        oldItem = myOldList.get(oldItemPosition);
        newItem = myNewList.get(newItemPosition);

        if (oldItem instanceof City && newItem instanceof City)
            return ((City) oldItem).getCity_id() == ((City) newItem).getCity_id();
        else if (oldItem instanceof Fragment && newItem instanceof Fragment)
            return Objects.equals(((Fragment) oldItem).getTag(), ((Fragment) newItem).getTag());

        // return false for incompatible types.
        return false;


    }


    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        oldItem = myOldList.get(oldItemPosition);
        newItem = myNewList.get(newItemPosition);

        return oldItem.equals(newItem);
    }


}
