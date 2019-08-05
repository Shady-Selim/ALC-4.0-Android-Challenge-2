package com.shady_selim.travelmantics

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_deal.*
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso


class DealActivity : AppCompatActivity() {
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private lateinit var mDatabaseReference: DatabaseReference
    private var deal = TravelDeal()
    private val PICTURE_RESULT: Int = 42

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deal)
        progressBar.isVisible = false
//        FirebaseUtil.openFbRef("traveldeals", this)
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase
        mDatabaseReference = FirebaseUtil.mDatabaseReference

        if (intent.hasExtra("Deal")){
            deal = intent.getParcelableExtra("Deal")!!
            txtTitle.setText(deal.title)
            txtDesc.setText(deal.description)
            txtPrice.setText(deal.price)
            val mWidth = Resources.getSystem().displayMetrics.widthPixels
            if (deal.imageUrl != "")
                Picasso.get().load(deal.imageUrl).resize(mWidth,mWidth*2/3).centerCrop().into(image)
            btnImage.setOnClickListener(){
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                startActivityForResult(Intent.createChooser(intent, "Insert Picture"), PICTURE_RESULT)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_menu -> {
                Toast.makeText(this, "Deal Saved", Toast.LENGTH_LONG).show()
                true
            }
            R.id.delete_menu -> {
                deleteDeal()
                Toast.makeText(this, "Deal Deleted", Toast.LENGTH_LONG).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICTURE_RESULT && resultCode == Activity.RESULT_OK){
            progressBar.isVisible = true
            val imageUri = data!!.data
            val ref = FirebaseUtil.mStorageReference.child(imageUri?.lastPathSegment!!)
            ref.putFile(imageUri).addOnSuccessListener{ taskSnapshot ->
                val uri = taskSnapshot.storage.downloadUrl
                while (!uri.isComplete);
                deal.imageUrl = uri.result.toString()
                deal.imageName = taskSnapshot.storage.name
                val width = Resources.getSystem().displayMetrics.widthPixels
                Picasso.get().load(uri.result.toString()).resize(width,width*2/3).centerCrop().into(image)
                saveDeal()
            }
        }
    }

    private fun saveDeal() {
        deal.title =  txtTitle.text.toString()
        deal.description = txtDesc.text.toString()
        deal.price = txtPrice.text.toString()
        if (deal.id == "")
            mDatabaseReference.push().setValue(deal)
        else
            mDatabaseReference.child(deal.id).setValue(deal)
        clear()
    }

    private fun deleteDeal(){
        if (deal.id == ""){
            Toast.makeText(this, "Please save the deal before deleting", Toast.LENGTH_LONG).show()
            return
        }
        mDatabaseReference.child(deal.id).removeValue()
        if (deal.imageName !=""){
            val picRef: StorageReference = FirebaseUtil.mStorage.getReference().child(deal.imageName)
            picRef.delete().addOnSuccessListener {

            }.addOnFailureListener {

            }
        }
        clear()
    }

    private fun clear(){
        txtTitle.text.clear()
//        txtTitle.requestFocus()
        txtDesc.text.clear()
        txtPrice.text.clear()
        startActivity(Intent(this, ListActivity::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_menu, menu)
        menu?.findItem(R.id.delete_menu)?.isVisible = FirebaseUtil.isAdmin
        menu?.findItem(R.id.save_menu)?.isVisible = FirebaseUtil.isAdmin
        enableEditTexts(FirebaseUtil.isAdmin)
        btnImage.isVisible = FirebaseUtil.isAdmin
        return super.onCreateOptionsMenu(menu)
    }

    fun enableEditTexts(isEnabled: Boolean){
        txtTitle.isEnabled = isEnabled
        txtDesc.isEnabled = isEnabled
        txtPrice.isEnabled = isEnabled
    }
}
