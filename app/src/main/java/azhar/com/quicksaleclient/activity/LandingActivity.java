package azhar.com.quicksaleclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import azhar.com.quicksaleclient.R;
import azhar.com.quicksaleclient.modules.Billing;
import azhar.com.quicksaleclient.modules.Order;
import azhar.com.quicksaleclient.modules.Setup;
import azhar.com.quicksaleclient.modules.Taken;
import azhar.com.quicksaleclient.utils.Constants;
import azhar.com.quicksaleclient.utils.Persistance;

@SuppressWarnings("deprecation")
public class LandingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    View taken, order, billing, setup;
    public static int whereIam = 0;

    TextView salesManName, salesManPhone;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Taken");
        }


        taken = findViewById(R.id.taken_holder);
        order = findViewById(R.id.order_holder);
        billing = findViewById(R.id.billing_holder);
        setup = findViewById(R.id.setup_holder);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navSetup(navigationView.getHeaderView(0));
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    private void navSetup(View headerView) {

        salesManName = headerView.findViewById(R.id.sales_man_name);
        salesManPhone = headerView.findViewById(R.id.sales_man_phone);

        try {
            String name = Persistance.getUserData(Constants.MY_NAME, LandingActivity.this);
            String number = Persistance.getUserData(Constants.MY_PHONE, LandingActivity.this);

            salesManName.setText(name != null ? name.toUpperCase() : "");
            salesManPhone.setText(number != null ? number : "");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_taken) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Taken");
            }
            whereIam = 0;
        } else if (id == R.id.nav_order) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Order");
            }
            whereIam = 1;
        } else if (id == R.id.nav_billing) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Billing");
            }
            whereIam = 2;
        } else if (id == R.id.nav_setup) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Set-up");
            }
            whereIam = 3;
        } else if (id == R.id.nav_signout) {
            whereIam = 4;
        }

        switchScreen();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void switchScreen() {
        taken.setVisibility(View.GONE);
        order.setVisibility(View.GONE);
        billing.setVisibility(View.GONE);
        setup.setVisibility(View.GONE);

        switch (whereIam) {
            case 0:
                taken.setVisibility(View.VISIBLE);
                new Taken().evaluate(this, taken);
                break;
            case 1:
                order.setVisibility(View.VISIBLE);
                new Order().evaluate(this, order);
                break;
            case 2:
                billing.setVisibility(View.VISIBLE);
                new Billing().evaluate(this, billing);
                break;
            case 3:
                setup.setVisibility(View.VISIBLE);
                new Setup().evaluate(this, setup);
                break;
            case 4:
                Constants.AUTH.signOut();
                startActivity(new Intent(LandingActivity.this, LoginActivity.class));
                finish();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.landing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        switchScreen();
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }
}
