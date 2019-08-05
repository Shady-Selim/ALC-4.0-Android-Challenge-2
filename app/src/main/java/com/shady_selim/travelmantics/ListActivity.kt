package com.shady_selim.travelmantics

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : AppCompatActivity() {
    var deals: ArrayList<TravelDeal> = ArrayList()
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var mDatabaseReference: DatabaseReference
    lateinit var mChildEventListener: ChildEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        rvDeals.layoutManager = LinearLayoutManager(this)
        FirebaseUtil.openFbRef("traveldeals", this)
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase
        mDatabaseReference = FirebaseUtil.mDatabaseReference
        mChildEventListener = object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val deal = p0.getValue(TravelDeal::class.java)
                deal?.id = p0.key.toString()
                deals.add(deal!!)
//                tvDeals.text = "${tvDeals.text} \n ${deal?.title}"
                rvDeals.adapter = DealAdapter(deals)
            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }
            override fun onChildRemoved(p0: DataSnapshot) {
            }
        }
        mDatabaseReference.addChildEventListener(mChildEventListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_activity_menu, menu)
        menu?.findItem(R.id.insert_menu)?.isVisible = FirebaseUtil.isAdmin
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.insert_menu -> {
                startActivity(Intent(this, DealActivity::class.java))
                true
            }
            R.id.logout_menu -> {
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        FirebaseUtil.attachListener()
                    }
                FirebaseUtil.detachListener()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        super.onPause()
        FirebaseUtil.detachListener()
    }
    override fun onResume() {
        super.onResume()
        FirebaseUtil.attachListener()
    }
    fun showMenu(){
        invalidateOptionsMenu()
    }
}
