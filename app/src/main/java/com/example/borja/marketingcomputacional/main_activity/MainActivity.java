package com.example.borja.marketingcomputacional.main_activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.borja.marketingcomputacional.MinimaxAlgorithm.Minimax;
import com.example.borja.marketingcomputacional.R;
import com.example.borja.marketingcomputacional.area_input.fragments.InputRandom;
import com.example.borja.marketingcomputacional.general.StoredData;

public class MainActivity extends AppCompatActivity {

    private static TextView texto_carga;
    private static LinearLayout container_carga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

//        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        final ImageView first_circle = (ImageView) findViewById(R.id.first_circle);
        final ImageView second_circle = (ImageView) findViewById(R.id.second_circle);
        final ImageView third_circle = (ImageView) findViewById(R.id.third_circle);

        texto_carga = (TextView) findViewById(R.id.texto_carga);
        Typeface font = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        texto_carga.setTypeface(font);
        container_carga = (LinearLayout) findViewById(R.id.container_carga);

        final ViewPager mainPager = (ViewPager) findViewById(R.id.main_viewpager);
        ScreenSlidePagerAdapter adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mainPager.setAdapter(adapter);

        mainPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        first_circle.setImageResource(R.drawable.circle_white);
                        second_circle.setImageResource(R.drawable.circle_black);
                        third_circle.setImageResource(R.drawable.circle_black);
                        break;
                    case 1:
                        first_circle.setImageResource(R.drawable.circle_black);
                        second_circle.setImageResource(R.drawable.circle_white);
                        third_circle.setImageResource(R.drawable.circle_black);
                        break;
                    case 2:
                        first_circle.setImageResource(R.drawable.circle_black);
                        second_circle.setImageResource(R.drawable.circle_black);
                        third_circle.setImageResource(R.drawable.circle_white);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TextView start = (TextView) findViewById(R.id.start);
        start.setTypeface(font);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Handler handler;
                switch (mainPager.getCurrentItem()) {
                    case 0: //GENETICO CLIENTES
                        StoredData.Algorithm = StoredData.GENETIC;
                        container_carga.setVisibility(View.VISIBLE);
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                Intent select_input = new Intent(getApplicationContext(), Configuration.class);
//                                select_input.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                getApplicationContext().startActivity(select_input);
//                            }
//                        }, 0);
                        break;
                    case 1:// MINIMAX
                        StoredData.Algorithm = StoredData.MINIMAX;
                        container_carga.setVisibility(View.VISIBLE);
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                InputRandom input = new InputRandom();
//                                try {
//                                    input.generate();
//
//                                    StoredData.Minimax = new Minimax();
//                                    StoredData.Minimax.start(getApplicationContext());
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                    Snackbar.make(mainPager, e.getMessage(), Snackbar.LENGTH_SHORT)
//                                            .setAction("Action", null).show();
//                                }
//                            }
//                        }, 0);
                        break;
                    case 2:// PSO
                        StoredData.Algorithm = StoredData.PSO;
                        container_carga.setVisibility(View.VISIBLE);
                        break;
                }

                handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent select_input = new Intent(getApplicationContext(), Configuration.class);
                        select_input.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(select_input);
                    }
                }, 0);
            }
        });

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_SHORT)
//                        .setAction("Action", null).show();
//            }
//        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            MainViewPagerFragment main = new MainViewPagerFragment();
            Bundle args = new Bundle();
            args.putInt("page", position);
            main.setArguments(args);
            return main;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        container_carga.setVisibility(View.INVISIBLE);
    }

    public static void cambiar_texto_carga(String text) {
        container_carga.setVisibility(View.VISIBLE);
        texto_carga.setText(text);
    }
}
