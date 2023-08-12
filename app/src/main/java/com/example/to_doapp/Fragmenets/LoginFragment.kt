package com.example.to_doapp.Fragmenets

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.to_doapp.R
import com.example.to_doapp.databinding.FragmentLoginBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)


        auth = Firebase.auth

        binding.registerNowTxt.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.loginBtn.setOnClickListener {

            val email = binding.loginEmailEdt.text.toString().trim()
            val pass = binding.loginPassEdt.text.toString().trim()

            if (email.isEmpty()) {
                binding.loginEmailEdt.error = "Enter email"
            } else if (pass.isEmpty()) {
                binding.loginPassEdt.error = "Enter password"
            } else {
                loginUser(email, pass)
            }

        }

        binding.forgotPasswordTxt.setOnClickListener{
            forgotPasswoardDialog()
        }

        return binding.root
    }


    private fun loginUser(email: String, pass: String) {

        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(OnCompleteListener { task ->


                if (task.isSuccessful) {

                    val verify = auth.currentUser?.isEmailVerified
                    if (verify == true) {
                        findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                        Toast.makeText(context, "Login successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Please Verify your Email !", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(context, "Error !", Toast.LENGTH_SHORT).show()
                }

            })

    }

    private fun forgotPasswoardDialog(){
        val dialog = context?.let { Dialog(it) }
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.password_reset_dialog)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));


        val registeredEmail = dialog.findViewById<EditText>(R.id.recoverEmailEdt)

        val emailRecoverBtn = dialog.findViewById<Button>(R.id.recoverBtn)

        emailRecoverBtn.setOnClickListener{
            val dialogEmail = registeredEmail.text.toString().trim()
            if (dialogEmail.isNotEmpty()){
                auth.sendPasswordResetEmail(dialogEmail).addOnCompleteListener (OnCompleteListener {
                    task ->
                    if (task.isSuccessful){
                        Toast.makeText(context , "Check your mail" , Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    else{
                        Toast.makeText(context , task.exception?.message, Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                })
            }
        }

        dialog.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}