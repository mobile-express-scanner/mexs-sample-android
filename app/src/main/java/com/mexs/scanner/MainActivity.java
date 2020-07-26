package com.mexs.scanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private MainViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get ViewModel
        mViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(this.getApplication())).get(MainViewModel.class);

        // Setup BottomNavigationView
        final BottomNavigationView bottomNavigationView = findViewById(R.id.menu_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_setup:
                        mViewModel.setCurState("setup");
                        return true;
                    case R.id.menu_camera:
                        mViewModel.setCurState("scan");
                        return true;
                    case R.id.menu_form:
                        mViewModel.setCurState("form");
                        return true;
                    case R.id.menu_records:
                        mViewModel.setCurState("records");
                        return true;
                }

                return false;
            }
        });
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                // Do nothing
            }
        });

        // Subscribe to changes in application state
        mViewModel.mCurState.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String state) {
                switch (state) {
                    case "setup":
                        SetupFragment fragmentSetup = new SetupFragment();
                        getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentSetup).commit();
                        bottomNavigationView.setSelectedItemId(R.id.menu_setup);
                        break;
                    case "scan":
                        CameraFragment fragmentCamera = new CameraFragment();
                        getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentCamera).commit();
                        bottomNavigationView.setSelectedItemId(R.id.menu_camera);
                        break;
                    case "form":
                        FormFragment fragmentForm = new FormFragment();
                        getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentForm).commit();
                        bottomNavigationView.setSelectedItemId(R.id.menu_form);
                        break;
                    case "records":
                        RecordsFragment fragmentRecords = new RecordsFragment();
                        getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentRecords).commit();
                        bottomNavigationView.setSelectedItemId(R.id.menu_records);
                        break;
                }
            }
        });

        // Subscribe to changes in records numbers
        mViewModel.countRecords().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer count) {
                BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.menu_records);
                if (count > 0){
                    badge.setVisible(true);
                    badge.setNumber(count);
                } else {
                    badge.setVisible(false);
                    badge.setNumber(0);
                }
            }
        });
    }

}
