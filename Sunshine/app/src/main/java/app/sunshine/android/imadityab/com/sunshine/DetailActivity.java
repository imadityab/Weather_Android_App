package app.sunshine.android.imadityab.com.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent intent= new Intent(this,SettingsActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {

            private static final String LOG_TAG2 = DetailFragment.class.getSimpleName();
            private static final String FORECAST_SHARE_HASHTAG = " #sunshineApp";
            String forecastStr=null;
        public DetailFragment() {
        setHasOptionsMenu(true);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
           // super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.forecastfragment,menu);

            MenuItem menuItem=menu.findItem(R.id.action_share);

            ShareActionProvider mShareActionProvider=(ShareActionProvider)
                    MenuItemCompat.getActionProvider(menuItem);

            if(mShareActionProvider!=null)
            {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            }else{
                Log.d(LOG_TAG2,"NULL Share Action provider found.");
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            Intent intent=getActivity().getIntent();

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            if(intent!=null && intent.hasExtra(Intent.EXTRA_TEXT))
            {
                forecastStr=intent.getStringExtra(Intent.EXTRA_TEXT);
                ((TextView)rootView.findViewById(R.id.detail_text)).setText(forecastStr);
            }
           // View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            return rootView;
        }
        private Intent createShareForecastIntent(){
            Intent intent=new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT,forecastStr+FORECAST_SHARE_HASHTAG);
            return intent;
        }
    }
}
