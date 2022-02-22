package com.example.einsatzapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);

        NavController navController = Navigation.findNavController(this,R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();
                if (id == R.id.hazardAPP) {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.pressmatrix.stoffeblattler");
                    startActivity( launchIntent );
                }else if(id == R.id.carRescueApp){
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.euroncap.rescue");
                    startActivity( launchIntent );
                }
                else if(id == R.id.missionInfoApp){
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("at.tbirn.lfkapp");
                    startActivity( launchIntent );
                }
                else
                {
                    // Make your navController object final above
                    // or call Navigation.findNavController() again here
                    navigationView.setCheckedItem(menuItem);
                    NavigationUI.onNavDestinationSelected(menuItem, navController);
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }


}