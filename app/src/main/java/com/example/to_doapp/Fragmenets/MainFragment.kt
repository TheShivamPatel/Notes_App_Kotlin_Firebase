package com.example.to_doapp.Fragmenets

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.to_doapp.AdapterPack.NotesAdapter
import com.example.to_doapp.models.DataModel
import com.example.to_doapp.R
import com.example.to_doapp.SwipeGesture.SwipeToDelete
import com.example.to_doapp.databinding.FragmentMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var dataList: ArrayList<DataModel>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMainBinding.inflate(inflater, container, false)


        auth = FirebaseAuth.getInstance()

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.setHasFixedSize(true)

        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference("Notes")
            .child(auth.currentUser!!.uid.toString())

        val swipeToDelete = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val position = viewHolder.adapterPosition

                val noteKey: String? = dataList.get(position).noteKey

                val drf = databaseRef.child(noteKey.toString())


                val builder = AlertDialog.Builder(context)
                builder.setMessage("Are you sure to delete?")
                var alertDialog: AlertDialog? = null
                alertDialog?.setCancelable(true)

//              performing positive action
                builder.setPositiveButton("Yes") { dialogInterface, which ->

                    drf.removeValue().addOnCompleteListener {

                        if (it.isSuccessful) {
                            Toast.makeText(context, "Note Deleted", Toast.LENGTH_SHORT).show()
                            binding.recyclerView.adapter?.notifyDataSetChanged()
                        } else {
                            Toast.makeText(context, it.exception?.message.toString(), Toast.LENGTH_SHORT).show()
                        }

                    }


                    Toast.makeText(context, "Item Deleted", Toast.LENGTH_LONG).show()
                }

                builder.setNegativeButton("No") { dialogInterface, which ->
                    binding.recyclerView.adapter?.notifyDataSetChanged()
                    alertDialog?.dismiss()
                }

                alertDialog = builder.create()

                alertDialog.show()


            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDelete)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)


        dataList = arrayListOf<DataModel>()

        retrieveData()


        binding.logOutBtn.setOnClickListener {
            logoutConfirmation()
        }

        binding.createNoteFloatBtn.setOnClickListener {
            addNewNote()
        }


        return binding.root
    }



    // retrieve data from firebase
    private fun retrieveData() {
        databaseRef = FirebaseDatabase.getInstance().getReference("Notes")
            .child(auth.currentUser!!.uid.toString())
        databaseRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()

                if (snapshot.exists()) {
                    for (dataSnap in snapshot.children) {
                        val data = dataSnap.getValue(DataModel::class.java)
                        dataList.add(data!!)
                    }
                    binding.recyclerView.adapter = context?.let { NotesAdapter(dataList, it) }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show()
            }


        })

    }

    // pop dialog for create new note
    private fun addNewNote() {

        val dialog = context?.let { Dialog(it) }
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.note_taking_dialog)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        val cancelBtn: ImageButton = dialog.findViewById(R.id.cancelBtn)
        val title: EditText = dialog.findViewById(R.id.newTitle)
        val desc: EditText = dialog.findViewById(R.id.newEdt)
        val uploadNote: Button = dialog.findViewById(R.id.uploadNote)

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        uploadNote.setOnClickListener {

            var title = title.text.toString()
            var desc = desc.text.toString()

            if (title.isEmpty() || desc.isEmpty()) {
                Toast.makeText(context, "Fill blank field", Toast.LENGTH_SHORT).show()
            } else {
                createNewNote(title, desc)
                dialog.dismiss()
            }

        }

        dialog.show()

    }


//    create new note
    private fun createNewNote(noteTitle: String, noteDescription: String) {


        databaseRef = FirebaseDatabase.getInstance().getReference("Notes")
            .child(auth.currentUser!!.uid.toString())
        val id = databaseRef.push().key
        val note = DataModel(id, noteTitle, noteDescription)

        databaseRef.child(id!!).setValue(note).addOnCompleteListener {

            if (it.isSuccessful) {
                Toast.makeText(context, "Note added successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception?.message.toString(), Toast.LENGTH_SHORT).show()
            }

        }

    }



    // Logout popup dialog
    private fun logoutConfirmation() {

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Logout")
        builder.setMessage(R.string.logout_dialog)
        // Create the AlertDialog
        var alertDialog: AlertDialog? = null
        // Set other dialog properties
        alertDialog?.setCancelable(true)

//      performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->

            Firebase.auth.signOut()
            findNavController().navigate(R.id.action_mainFragment_to_splashScreenFragment2)

            Toast.makeText(context, "Logout successfully", Toast.LENGTH_LONG).show()
        }

        builder.setNegativeButton("No") { dialogInterface, which ->
            alertDialog?.dismiss()
        }

        alertDialog = builder.create()

        alertDialog.show()

    }


    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}