package galilel.org.galilelwallet.ui.tutorial_activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import galilel.org.galilelwallet.R;
import galilel.org.galilelwallet.ui.pincode_activity.PincodeActivity;
import galilel.org.galilelwallet.ui.start_node_activity.StartNodeActivity;
import galilel.org.galilelwallet.ui.wallet_activity.WalletActivity;

public class TutorialActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String INTENT_EXTRA_INFO_TUTORIAL = "info_tutorial";

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext;
    private boolean isInit = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.tutorial_activity);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);

        layouts = new int[] {
                R.layout.tutorial_slide1,
                R.layout.tutorial_slide2,
                R.layout.tutorial_slide3,
                R.layout.tutorial_slide4
        };

        // adding bottom dots.
        addBottomDots(0);

        viewPagerAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(INTENT_EXTRA_INFO_TUTORIAL)) {
            isInit = false;
        }
    }

    public void btnNodeClick(View v) {
        startActivity(new Intent(this, StartNodeActivity.class));
    }

    public void btnSkipClick(View v) {
        launchHomeScreen();
    }

    public void btnNextClick(View v) {

        // if last page home screen will be launched.
        int current = getItem(1);
        if (current < layouts.length) {

            // move to next screen.
            viewPager.setCurrentItem(current);
        } else {

            // launch home screen.
            launchHomeScreen();
        }
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {

                // last page, make button text to complete.
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);
            } else {

                // still pages are left.
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                dots[i].setTextColor(getColor(R.color.grey35black));
            } else {
                dots[i].setTextColor(ContextCompat.getColor(this, R.color.grey35black));
            }

            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                dots[currentPage].setTextColor(getResources().getColor(R.color.galilelBlack, null));
            } else {
                dots[currentPage].setTextColor(ContextCompat.getColor(this, R.color.grey35black));
            }
        }
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        Class activity;
        if (isInit) {
            activity = PincodeActivity.class;
        } else {
            activity = WalletActivity.class;
        }
        startActivity(new Intent(this, activity));
        finish();
    }

    @Override
    public void onClick(View v) {

    }

    public class ViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public ViewPagerAdapter() {

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;

        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
