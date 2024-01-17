package com.example.starwars.ui.activity

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calendar.collectEvent
import com.example.data.utlils.NetworkUtil
import com.example.domain.domain.models.MatchBean
import com.example.domain.domain.models.PlayerWithPointsBean
import com.example.starwars.R
import com.example.starwars.base.BaseActivity
import com.example.starwars.databinding.ActivityMatchScreenBinding
import com.example.starwars.ui.adapters.MatchScreenAdapter
import com.example.starwars.viewmodel.MatchesViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MatchScreenActivity :
    BaseActivity<ActivityMatchScreenBinding>(R.layout.activity_match_screen) {


    private val matchesViewModel by viewModels<MatchesViewModel>()
    private lateinit var playerData: PlayerWithPointsBean


    @Inject
    lateinit var networkUtil: NetworkUtil
    private val matchScreenAdapter by lazy {
        MatchScreenAdapter(itemClickListener = {


        }, context = this@MatchScreenActivity)
    }


    override fun readArguments(extras: Intent) {
        intent?.let {
            playerData = it.getParcelableExtra("PLAYER_DATA")!!
        }
    }

    override fun setupUi() {
        matchesViewModel.getMatchesForPlayer(playerData)
        setupRecyclerView()
    }

    override fun observeData() {
        collectEvent(matchesViewModel.matchesDataFlow) {
            renderLoadingScreen(true)
            if (it.isNotEmpty()) {
                renderLoadingScreen(false)
                renderSuccessScreen(it)
            } else {
                renderDataNotFoundScreen()
            }
        }
    }


    private fun setupRecyclerView() {
        binding.listRV.layoutManager =
            LinearLayoutManager(this)
        binding.listRV.adapter = matchScreenAdapter
    }


    private fun renderLoadingScreen(isLoading: Boolean) {
        if (isLoading) {
            binding.circularProgressView.visibility = View.VISIBLE
            binding.listRV.visibility = View.GONE
        } else {
            binding.circularProgressView.visibility = View.GONE
            binding.listRV.visibility = View.VISIBLE
        }
    }

    private fun renderSuccessScreen(clientResult: List<MatchBean>) {
        if (binding.retryLayoutHolder.retryLayoutCL.visibility == View.VISIBLE) {
            binding.retryLayoutHolder.retryLayoutCL.visibility = View.GONE
        }
        if (binding.noMovieFoundHolder.retryLayoutCL.visibility == View.VISIBLE) {
            binding.noMovieFoundHolder.retryLayoutCL.visibility = View.GONE
        }
        if (binding.listRV.visibility == View.GONE) {
            binding.listRV.visibility = View.VISIBLE
        }

        if (clientResult.isEmpty()) {
            renderDataNotFoundScreen()
        } else {
            matchScreenAdapter.submitList(clientResult)
        }
    }

    override fun setListener() {

    }

    override fun onDestroy() {
        super.onDestroy()
    }


    private fun renderDataNotFoundScreen(hasNetwork: Boolean = true) {
        binding.listRV.visibility = View.GONE
        binding.noMovieFoundHolder.retryLayoutCL.visibility = View.VISIBLE
        binding.noMovieFoundHolder.retryMessageTV.text =
            if (hasNetwork) "Nothing Found" else "No Network detected"
    }


}