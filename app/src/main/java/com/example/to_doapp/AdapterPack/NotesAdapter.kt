package com.example.to_doapp.AdapterPack

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.to_doapp.Fragmenets.MainFragment
import com.example.to_doapp.models.DataModel
import com.example.to_doapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.NonDisposableHandle.parent

class NotesAdapter(val dataList : ArrayList<DataModel> , val context: Context ) : RecyclerView.Adapter<NotesAdapter.viewHoder>() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHoder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sample_note_layout, parent,false)
        return viewHoder(view)
    }

    override fun onBindViewHolder(holder: viewHoder, position: Int) {

        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference("Notes").child(auth.currentUser!!.uid.toString())

        val list = dataList[position]
        holder.titleTxt.text= list.title
        holder.descTxt.text = list.description

    }


    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class viewHoder(view : View) : RecyclerView.ViewHolder(view){

        val titleTxt : TextView = view.findViewById(R.id.sampleTitle)
        val descTxt : TextView = view.findViewById(R.id.sampleDesc)
        val noteCard :ConstraintLayout = view.findViewById(R.id.noteCard)
    }

}