package tastifai.customer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rohan Nevrikar on 25-03-2018.
 */

public class MeFragmentAdapter extends FragmentStatePagerAdapter {
    public  List<Fragment> mFragments = new ArrayList<>();
    public  List<String> mFragmentTitles = new ArrayList<>();
    private static final String TAG = "MeFragmentAdapter";
    public MeFragmentAdapter(FragmentManager fm) {
        super(fm);
    }
    public void clearList(){
        mFragments.clear();
        mFragmentTitles.clear();
    }
    public void addFragment(android.support.v4.app.Fragment fragment, String title) {

        mFragments.add(fragment);
        mFragmentTitles.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }



    @Override
    public int getCount() {
        Log.d(TAG, "getCount: " + mFragments.size());
        return mFragments.size();
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles.get(position);
    }
}
