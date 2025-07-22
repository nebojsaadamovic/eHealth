package com.example.zdravlje.ui.chat.singleAndGroupMessages

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.nites.ezdravljeapp.R
import com.nites.ezdravljeapp.databinding.FragmentAddMemberBinding

class AddMemberFragment : Fragment() {

    lateinit var binding: FragmentAddMemberBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddMemberBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUI() {
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.mvSearch.visibility = View.VISIBLE
                true
            } else {
                false
            }
        }

        binding.mvSearch.setOnClickListener {
            Toast.makeText(requireContext(), "Dugme je kliknuto!", Toast.LENGTH_SHORT).show()
        }

        binding.etSearch.setOnTouchListener { view, event ->
            val drawableStart = binding.etSearch.compoundDrawables[0]
            if (event.action == MotionEvent.ACTION_UP && drawableStart != null) {
                val drawableWidth = drawableStart.intrinsicWidth
                val touchX = event.rawX
                if (touchX <= (binding.etSearch.left + drawableWidth + binding.etSearch.paddingStart)) {
                    val fragment = ChatMessagesFragment()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.chatOptionFcv, fragment)
                        .addToBackStack(null)
                        .commit()
                    return@setOnTouchListener true
                }
            }
            false
        }
    }
}
