package com.example.social


import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.social.daos.PostDao
import com.example.social.models.Post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_post.*
import kotlinx.android.synthetic.main.toolbar.*


class MainActivity : AppCompatActivity(), IPostAdapter {

    private lateinit var postDao: PostDao
    private lateinit var adapter: PostAdapter
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener{
            val intent =  Intent(this, CreatePostActivity::class.java)
            startActivity(intent)
        }
        setUpRecyclerView()

        logout_button.setOnClickListener{
            logOutAlertDialog()
        }
    }

    fun logOutAlertDialog(){
        val builder  = AlertDialog.Builder(this)
        builder.setIcon(R.drawable.ic_logout)
        builder.setTitle("Are you sure!")
        builder.setMessage("Do you want to sign out?")
        builder.setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
            auth = Firebase.auth
            Firebase.auth.signOut()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
        builder.setNegativeButton("No") { dialogInterface: DialogInterface, i: Int -> }
        builder.show()
    }

    override fun onBackPressed() {
        val builder  = AlertDialog.Builder(this)
        builder.setTitle("Are you sure?")
        builder.setMessage("Do you want to close the app?")
        builder.setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
            finish()
        }
        builder.setNegativeButton("No") { dialogInterface: DialogInterface, i: Int -> }
        builder.show()
    }

    private fun setUpRecyclerView() {
        postDao = PostDao()
        val postsCollections = postDao.postCollections
        val query = postsCollections.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()

        adapter = PostAdapter(recyclerViewOptions, this)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onLikeClicked(postId: String) {
        postDao.updateLikes(postId)
    }



}