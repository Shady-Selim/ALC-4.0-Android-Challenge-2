package com.shady_selim.travelmantics

import android.app.Activity
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.security.AuthProvider

class FirebaseUtil() {
    companion object {
        lateinit var mFirebaseAuth: FirebaseAuth
        lateinit var mAuthListener: FirebaseAuth.AuthStateListener
        lateinit var mFirebaseDatabase: FirebaseDatabase
        lateinit var mDatabaseReference: DatabaseReference
        lateinit var mStorage: FirebaseStorage
        lateinit var mStorageReference: StorageReference
        lateinit var caller: ListActivity
        var isAdmin: Boolean = false
//        lateinit var mDeals: ArrayList<TravelDeal>
        fun openFbRef(ref: String, callerActivity: ListActivity){
            caller = callerActivity
            mFirebaseAuth = FirebaseAuth.getInstance()
            mAuthListener = FirebaseAuth.AuthStateListener {
                if (mFirebaseAuth.currentUser == null)
                    singIn()
                else
                    checkAdmin(mFirebaseAuth.uid!!)
                Toast.makeText(callerActivity.baseContext,"Welcome Back!", Toast.LENGTH_LONG).show()
            }
            mFirebaseDatabase = FirebaseDatabase.getInstance()
            mDatabaseReference = mFirebaseDatabase.reference.child(ref)
            connectStorage()
        }

        private fun checkAdmin(uid: String) {
            isAdmin = false
            val ref: DatabaseReference = mFirebaseDatabase.getReference().child("admin").child(uid)
            val listener: ChildEventListener = object : ChildEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }
                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                }
                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                }
                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    isAdmin = true
                    caller.showMenu()
                }
                override fun onChildRemoved(p0: DataSnapshot) {
                }
            }
            ref.addChildEventListener(listener)
        }

        private fun singIn(){
            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build())
            caller.startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                1)
        }
        fun attachListener(){
            mFirebaseAuth.addAuthStateListener(mAuthListener)
        }
        fun detachListener(){
            mFirebaseAuth.removeAuthStateListener(mAuthListener)
        }
        fun connectStorage(){
            mStorage = FirebaseStorage.getInstance()
            mStorageReference = mStorage.getReference().child("deals_pictures")
        }
    }
}