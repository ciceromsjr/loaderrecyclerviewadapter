package ciceromoura.br.loaderrecyclerviewadapter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import br.ciceromoura.loaderrecycler.BaseLoaderRecyclerViewAdapter;
import ciceromoura.br.loaderrecyclerviewadapter.adapter.CountryRecyclerViewAdapter;

public class MainActivity extends AppCompatActivity implements BaseLoaderRecyclerViewAdapter.OnLoaderListener {

    private CountryRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CountryRecyclerViewAdapter(this, getSupportLoaderManager(), 1);
        recyclerView.setAdapter(adapter);

        //to listen to the loader callback
        adapter.setOnLoaderListener(this);
    }


    public void onStartLoaderClick(View v) {

        Bundle args = new Bundle();
        args.putSerializable(CountryRecyclerViewAdapter.AMERICA_ARG, CountryRecyclerViewAdapter.America.SOUTH_AMERICA);

        //it will always do only one load
        //even if I call this method one thousand times it will keep the data loaded at the first time

        adapter.startLoader(args);
    }

    public void onRestartLoaderClick(View v) {

        Bundle args = new Bundle();
        args.putSerializable(CountryRecyclerViewAdapter.AMERICA_ARG, CountryRecyclerViewAdapter.America.NORTH_AMERICA);

        //it will drop the current data and consider the a new one
        adapter.restartLoader(args);
    }

    @Override
    public void onLoaderLoading() {
        Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoaderFinished() {
        Toast.makeText(this, "Finished!", Toast.LENGTH_SHORT).show();
    }
}
