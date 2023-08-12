package com.example.to_doapp.Fragmenets

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.to_doapp.R
import com.example.to_doapp.databinding.FragmentSplashScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashScreenFragment : Fragment() {

    private var _binding : FragmentSplashScreenBinding ?= null
    private val binding get() = _binding!!

    private lateinit var auth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashScreenBinding.inflate(inflater , container ,false)


        auth = FirebaseAuth.getInstance()


        Handler(Looper.myLooper()!!).postDelayed({
            if (auth.currentUser != null) {
                findNavController().navigate(R.id.action_splashScreenFragment_to_mainFragment)
            }
            else{
                findNavController().navigate(R.id.action_splashScreenFragment_to_loginFragment)
            }
        },3000)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}