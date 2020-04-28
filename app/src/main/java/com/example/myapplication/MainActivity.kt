package com.example.myapplication

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.main.ItemListViewModel
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        // value took from values/attributes, different foreach layout, so it picks the correct one
        val isTablet: Boolean = resources.getBoolean(R.bool.isTablet)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        // check the device type and shows drawer accordingly
        if (isTablet) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN)
            drawerLayout.setScrimColor(0x00000000) // set the rest of the screen without shadow as if drawer and the rest were on the same level
        } else drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)


        val navView: NavigationView = findViewById(R.id.nav_view)
        // Nav_host_fragment is the fragment container in layout/content_main.xml
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_itemList
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        Helpers.readAccountJsonFromPreferences(this)?.let {
            Helpers.setNavHeaderView(
                navView.getHeaderView(0),
                it["fullname"].toString(),
                it["email"].toString(),
                it["profilePicture"].toString()
            )
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}
