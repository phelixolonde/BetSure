package com.automata.betsure;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.wang.avi.AVLoadingIndicatorView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class TomorrowFragment extends Fragment implements SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    View v;
    RecyclerView recyclerView;
    List<Model> data = new ArrayList<>();
    private AdapterNews mAdapter;
    AVLoadingIndicatorView avi;
    private SearchView searchView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    NativeBannerAd nativeBannerAd;
    private RelativeLayout nativeBannerAdContainer;
    private LinearLayout adView;
    public TomorrowFragment() {
        // Required empty public constructor
    }
    private static final String TAG ="FACEBOOK_ADS" ;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_today, container, false);
        recyclerView = v.findViewById(R.id.recycler);
        LinearLayoutManager lm = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(lm);
        avi = v.findViewById(R.id.avi);
        mSwipeRefreshLayout = v.findViewById(R.id.refresher);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(true);

                // Fetching data from server
                getData();
            }
        });
        nativeBannerAd = new NativeBannerAd(getContext(), "946939095507299_946940045507204");
        nativeBannerAd.setAdListener(new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets
                Log.e(TAG, "Native ad finished downloading all assets.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Native ad failed to load
                Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());

            }

            @Override
            public void onAdLoaded(Ad ad) {

                // Native ad is loaded and ready to be displayed
                Log.d(TAG, "Native ad is loaded and ready to be displayed!");
                if (nativeBannerAd == null || nativeBannerAd != ad) {
                    return;
                }

                // Inflate Native Banner Ad into Container
                inflateAd(nativeBannerAd);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
                Log.d(TAG, "Native ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
                Log.d(TAG, "Native ad impression logged!");
            }
        });
        // load the ad
        nativeBannerAd.loadAd();

        return v;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onStart() {
        super.onStart();
        getData();
    }

    private void getData() {
        avi.show();
        mSwipeRefreshLayout.setRefreshing(true);


        final String yester_date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        String today = String.valueOf(Integer.parseInt(yester_date )+1);
        final String today_date = today.substring(0, 4) + "-" + today.substring(4, 6) + "-" + today.substring(6,8);
        Log.i("TOMORROW", today_date);

        String urlJsonObj = "https://football-prediction-api.p.mashape.com/api/v1/predictions";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                avi.hide();
                try {

                    JSONArray array = response.getJSONArray("data");
                    JSONArray sortedJsonArray = new JSONArray();

                    List<JSONObject> jsonValues = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        jsonValues.add(array.getJSONObject(i));
                    }
                    Collections.sort( jsonValues, new Comparator<JSONObject>() {
                        //You can change "Name" with "ID" if you want to sort by ID
                        private static final String KEY_NAME = "start_date";

                        @Override
                        public int compare(JSONObject a, JSONObject b) {
                            String valA = "";
                            String valB = "";

                            try {
                                valA = (String) a.get(KEY_NAME);
                                valB = (String) b.get(KEY_NAME);
                            }
                            catch (JSONException e) {
                                //do something
                                Log.e("JSON_SORTING",e.getMessage(),e);
                            }

                            return valA.compareTo(valB);
                            //if you want to change the sort order, simply use the following:
                            //return -valA.compareTo(valB);
                        }
                    });

                    for (int i = 0; i < array.length(); i++) {
                        sortedJsonArray.put(jsonValues.get(i));
                    }
                    for (int i = 0; i < sortedJsonArray.length(); i++) {
                        JSONObject jsonobject = sortedJsonArray.getJSONObject(i);
                        JSONObject objectOdds = jsonobject.getJSONObject("odds");

                        Model model = new Model();


                        model.country = jsonobject.getString("competition_cluster");
                        model.league = jsonobject.getString("competition_name");
                        model.tip = jsonobject.getString("prediction");
                        model.home = jsonobject.getString("home_team");
                        model.away = jsonobject.getString("away_team");
                        model.odd = objectOdds.getString(jsonobject.getString("prediction"));

                        String date = jsonobject.getString("start_date");
                        String mDate = date.substring(0, 10);
                        model.time=date.substring(11,16);
                        if (mDate.equals(today_date)) {
                            data.add(model);
                        }


                    }
                    mAdapter = new AdapterNews(getContext(), data);
                    recyclerView.setAdapter(mAdapter);

                } catch (JSONException e) {
                    Log.e("BETSURE", e.getMessage(), e);

                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                avi.hide();
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "We are unable to load tips.Please check your internet connection", Toast.LENGTH_LONG).show();


            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                Map<String, String> params = new HashMap<>();
                params.put("X-Mashape-Key", "yANOxnOVohmshUkqnjNwQnWgAvZup1SQy3TjsnA6b0ioE9aK2K");
                return params;
            }
        };

        BetSure.getInstance().addToRequestQueue(jsonObjReq);
    }
    private void inflateAd(NativeBannerAd nativeBannerAd) {
        // Unregister last ad
        nativeBannerAd.unregisterView();

        // Add the Ad view into the ad container.
        nativeBannerAdContainer = v.findViewById(R.id.native_banner_ad_container);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        // Inflate the Ad view.  The layout referenced is the one you created in the last step.
        adView = (LinearLayout) inflater.inflate(R.layout.native_banner_ad_unit, nativeBannerAdContainer, false);
        nativeBannerAdContainer.addView(adView);

        // Add the AdChoices icon
        RelativeLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdChoicesView adChoicesView = new AdChoicesView(getContext(), nativeBannerAd, true);
        adChoicesContainer.addView(adChoicesView, 0);

        // Create native UI using the ad metadata.
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        AdIconView nativeAdIconView = adView.findViewById(R.id.native_icon_view);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdCallToAction.setText(nativeBannerAd.getAdCallToAction());
        nativeAdCallToAction.setVisibility(
                nativeBannerAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdTitle.setText(nativeBannerAd.getAdvertiserName());
        nativeAdSocialContext.setText(nativeBannerAd.getAdSocialContext());
        sponsoredLabel.setText(nativeBannerAd.getSponsoredTranslation());

        // Register the Title and CTA button to listen for clicks.
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);
        nativeBannerAd.registerViewForInteraction(adView, nativeAdIconView, clickableViews);
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        mAdapter.getFilter().filter(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        mAdapter.getFilter().filter(newText);

        return false;
    }

    @Override
    public void onRefresh() {
        data.clear();
        getData();
    }
}
