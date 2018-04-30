package ciceromoura.br.loaderrecyclerviewadapter.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.ciceromoura.loaderrecycler.BaseLoaderRecyclerViewAdapter;
import ciceromoura.br.loaderrecyclerviewadapter.R;
import ciceromoura.br.loaderrecyclerviewadapter.model.Country;

public class CountryRecyclerViewAdapter extends BaseLoaderRecyclerViewAdapter<Country, CountryRecyclerViewAdapter.ViewHolder> {

    public static final String AMERICA_ARG = "AMERICA_ARG";

    /**
     * It has to be passed this parameters
     *
     * @param context       The context to be used by the loader
     * @param loaderManager Activity/Fragment support loader manager
     * @param loaderId      Unique id to the loader
     */
    public CountryRecyclerViewAdapter(@NonNull Context context, @NonNull LoaderManager loaderManager, int loaderId) {
        super(context, loaderManager, loaderId);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, Country item, int position) {

        //Bind the view items

        holder.mTextView.setText(item.getName());
    }

    @NonNull
    @Override
    protected List<Country> onLoadDataInBackground(@Nullable Bundle args) {

        America america = America.ALL;

        if (args != null && args.containsKey(AMERICA_ARG)) {
            america = (America) args.get(AMERICA_ARG);
        }

        List<Country> data = new ArrayList<>();

        //You can load data from database here
        //It is carried out in a background thread by the loader

        if (america == America.ALL || america == America.SOUTH_AMERICA) {
            data.add(new Country("Argentina"));
            data.add(new Country("Chile"));
            data.add(new Country("Brazil"));
        }

        if (america == America.ALL || america == America.NORTH_AMERICA) {
            data.add(new Country("Canada"));
            data.add(new Country("USA"));
            data.add(new Country("Mexico"));
        }

        return data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(getContext()).inflate(R.layout.row_country, parent, false);
        return new ViewHolder(v);
    }

    /**
     * View holder to show country data
     */
    static class ViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        TextView mTextView;

        ViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.textView);
        }
    }

    /**
     * It is used as a n example of passing args to loader
     * You don't need to use args if you don't want to
     */
    public enum America implements Serializable {
        NORTH_AMERICA,
        SOUTH_AMERICA,
        ALL,
    }
}
