package com.example.learnflow.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learnflow.HomeViewModel
import com.example.learnflow.R
import com.example.learnflow.components.recyclerview.SearchAdapter
import com.example.learnflow.databinding.FragmentSearchTeacherBinding
import com.example.learnflow.utils.Utils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchTeacherFragment : Fragment() {

    private var _binding: FragmentSearchTeacherBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchTeacherViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchTeacherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val autoCompleteTv = binding.autoCompleteTv
        val rvUsers = binding.rvSearch
        val lytSearchPlaceholder = view.findViewById<View>(R.id.lytSearchPlaceholder)
        val lytNoResultPlaceholder = view.findViewById<View>(R.id.lytNoResultPlaceholder)

        val searchAdapter = SearchAdapter(mutableListOf())

        rvUsers.layoutManager = LinearLayoutManager(requireContext())
        rvUsers.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: android.graphics.Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.top = Utils.convertDpToPx(5).toInt()
                outRect.bottom = Utils.convertDpToPx(5).toInt()
            }
        })
        rvUsers.adapter = searchAdapter

        autoCompleteTv.setAdapter(ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            homeViewModel.schoolSubjectsFlow.value
        ))
        autoCompleteTv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.resetUsers()
                if (s.isNullOrEmpty()) {
                    lytSearchPlaceholder.visibility = View.VISIBLE
                    lytNoResultPlaceholder.visibility = View.GONE
                } else {
                    lytSearchPlaceholder.visibility = View.GONE
                    GlobalScope.launch {
                        delay(100)
                        viewModel.searchTeachers(requireContext(), s.toString())
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        autoCompleteTv.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                lytSearchPlaceholder.visibility = View.GONE
                GlobalScope.launch {
                    delay(100)
                    viewModel.searchTeachers(
                        requireContext(),
                        homeViewModel.schoolSubjectsFlow.value[position]
                    )
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        lifecycleScope.launch {
            viewModel.searchFlow.collect {
                searchAdapter.updateData(it)
                if (autoCompleteTv.text.isEmpty()) return@collect

                if (it.isEmpty()) {
                    lytNoResultPlaceholder.visibility = View.VISIBLE
                    lytSearchPlaceholder.visibility = View.GONE
                } else {
                    lytNoResultPlaceholder.visibility = View.GONE
                }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}