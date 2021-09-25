package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    //class for filter items
    enum class SelectedOption{ SAVED, WEEK, TODAY}

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity){
            ""
        }
        ViewModelProvider(
            this,
            MainViewModel.Factory(activity.application)

        ).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        //set up adapter
        val adapter = AsteroidAdapter(AsteroidClick {
            viewModel.displayAsteroidDetails(it)
        })
        binding.asteroidRecycler.adapter = adapter

        //Observe navigation event
        viewModel.navigateToDetails.observe(viewLifecycleOwner, Observer {
            if (null != it) {
                this.findNavController().navigate(
                    MainFragmentDirections
                        .actionMainFragmentToDetailFragment(it)
                )
                viewModel.displayAsteroidsDone()
            }
        })

//        viewModel.asteroidList.observe(viewLifecycleOwner, Observer {
//            it?.let {
//                adapter.submitList(it)
//            }
//        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_rent_menu -> {
                viewModel.showSelectedOption(SelectedOption.TODAY)
            }
            R.id.show_all_menu -> {
                viewModel.showSelectedOption(SelectedOption.WEEK)
            }
            R.id.show_buy_menu -> {
                viewModel.showSelectedOption(SelectedOption.SAVED)
            }
            else -> viewModel.showSelectedOption(SelectedOption.SAVED)
        }
        return true
    }
}
