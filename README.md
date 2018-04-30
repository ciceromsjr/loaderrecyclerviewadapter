# Loader RecyclerView Adapter

That is an useful library to make it possible to load the recycler view data in a background thread using android loader.

Usage
-----

In order to use the library, there are 2 different options:

**1. Gradle dependency** (recommended)

  -  Add the following to your project level `build.gradle`:
 
```gradle
allprojects {
	repositories {
		maven { url "https://jitpack.io" }
	}
}
```
  -  Add this to your app `build.gradle`:
 
```gradle
dependencies {
	implementation 'com.github.ciceromsjr:loaderrecyclerviewadapter:1.0'
       implementation 'com.android.support:recyclerview-v7:27.1.1'
}
```
	
**2. clone whole repository** (not recommended)


Extending
-----

You have your model

``` 
public class Country {

    private String name;

    public Country(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
```

Create you adapter.
You can pass some arguments or not. It is up to you.
In this case, I am telling the adapter about the kind america I want to see the countries.


```
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
```


Your Activity or Fragment

```
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

```
