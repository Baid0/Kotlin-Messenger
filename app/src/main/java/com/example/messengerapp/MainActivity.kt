package com.example.messengerapp

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem

import androidx.fragment.app.Fragment
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.AppBarLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.messengerapp.Fragments.ChatsFragment
import com.example.messengerapp.Fragments.SearchFragment
import com.example.messengerapp.Fragments.SettingsFragment
import com.example.messengerapp.ModelClasses.Chat
import com.example.messengerapp.ModelClasses.Users
import com.example.messengerapp.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var refUsers: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar_main)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)



        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""

        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        val viewPager: ViewPager = findViewById(R.id.view_pager)

        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
        ref!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot)
            {
                val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

                var countUnreadMessages = 0

                for (dataSnapshot in p0.children)
                {
                    val chat = dataSnapshot.getValue(Chat::class.java)
                    if (chat!!.getReceiver().equals(firebaseUser!!.uid) && !chat.isIsSeen())
                    {
                        countUnreadMessages += 1
                    }
                }

                if (countUnreadMessages == 0)
                {
                    viewPagerAdapter.addFragment(ChatsFragment(), "ჩატები")
                }
                else
                {
                    viewPagerAdapter.addFragment(ChatsFragment(), "($countUnreadMessages) ჩატები")
                }

                viewPagerAdapter.addFragment(SearchFragment(), "მოძებნე")
                viewPagerAdapter.addFragment(SettingsFragment(), "პარამეტრები")
                viewPager.adapter = viewPagerAdapter
                tabLayout.setupWithViewPager(viewPager)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        //პროფილის და იუზერნეიმის გამოჩენა
        refUsers!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists())
                {
                    val user: Users? = p0.getValue(Users::class.java)

                    user_name.text = user!!.getUserName()
                    Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile).into(profile_image)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId)
        {
            R.id.action_settings ->
            {
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()

                return true
            }
        }
        return false
    }



    internal class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager){

        private val fragments: ArrayList<Fragment>
        private val titles: ArrayList<String>

        init {
            fragments = ArrayList<Fragment>()
            titles = ArrayList<String>()
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        fun addFragment(fragment: Fragment, title: String)
        {
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(i: Int): CharSequence? {
            return titles[i]
        }

    }
}