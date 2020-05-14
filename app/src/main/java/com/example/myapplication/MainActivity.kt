package com.example.myapplication

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var auth: FirebaseAuth

    lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        // value took from values/attributes, different foreach layout, so it picks the correct one
        val isTabletLandscape: Boolean = resources.getBoolean(R.bool.isTablet)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        // Write a message to the database
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val navView: NavigationView = findViewById(R.id.nav_view)

        // different drawer if user logged or not
        if(currentUser == null) {
            navView.menu.clear()
            navView.inflateMenu(R.menu.activity_main_drawer_logged_out)
        } else {
            navView.menu.clear()
            navView.inflateMenu(R.menu.activity_main_drawer)

            // Listener on logout button
            navView.menu.findItem(R.id.logout_action).setOnMenuItemClickListener {
                logout(navView)
            }
            //updateHeader
            Helpers.setNavHeaderView(
                navView.getHeaderView(0),
                currentUser.displayName!!,
                currentUser.email!!,
                currentUser.photoUrl!!.toString())
        }
        // Nav_host_fragment is the fragment container in layout/content_main.xml
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                //R.id.nav_itemList //(now is commented for debug puropsooeoses
                R.id.onSaleListFragment
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

/*        Helpers.readAccountJsonFromPreferences(this)?.let {
            Helpers.setNavHeaderView(
                navView.getHeaderView(0),
                it["fullname"].toString(),
                it["email"].toString(),
                it["profilePicture"].toString()
            )
        }*/

        // check the device type and shows drawer accordingly
        if (isTabletLandscape) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            drawerLayout.setScrimColor(0x00000000) // set the rest of the screen without shadow as if drawer and the rest were on the same level
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

    fun logout( navigationView: NavigationView): Boolean {
        // rough signout - see if something else is needed
        auth.signOut()
        // restore drawer menu
        // TODO: restore header too
        navigationView.menu.clear()
        navigationView.inflateMenu(R.menu.activity_main_drawer_logged_out)
        // navigate to signinFragment
        this.findNavController(R.id.nav_host_fragment).navigate(R.id.signInFragment)
        // close drawer
        this.findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawers()
        return true
    }

    fun getAuth() : FirebaseAuth {
        return this.auth
    }
}
