package com.example.to_doapp.Fragmenets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.to_doapp.R
import com.example.to_doapp.databinding.FragmentRegisterBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)


        // Initialize Firebase Auth
        auth = Firebase.auth

        binding.signUpBtn.setOnClickListener {

            val email = binding.newEmail.text.toString().trim()
            val pass = binding.newPass.text.toString().trim()
            val verifyPass = binding.newRepeatPass.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty() || verifyPass.isEmpty()) {
                Toast.makeText(activity, "Please fill all requirements", Toast.LENGTH_SHORT).show()
            } else if (pass != verifyPass) {
                binding.newRepeatPass.error = "Confirmation password must match"
            } else {

                // Register the user to firebase
                registerUser(email, pass)
            }

        }

        return binding.root
    }

    private fun registerUser(email: String, password: String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(context, "Registered Successfully", Toast.LENGTH_SHORT).show()

                    // Verification mail sent
                    sendEmailVerification(user)

                } else {
                    Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            })

    }

    private fun sendEmailVerification(user: FirebaseUser?) {

        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(OnCompleteListener {
                Toast.makeText(context, "Verification Email is sent, Verify and Log In Again", Toast.LENGTH_SHORT).show()
                auth.signOut()
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment2)
            })

        }
        else{
            Toast.makeText(context, "Failed to Send verification Email ", Toast.LENGTH_SHORT).show()

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}