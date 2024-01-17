package com.example.starwars.ui.activity


import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calendar.collectEvent
import com.example.data.utlils.NetworkUtil
import com.example.domain.domain.models.PlayerWithPointsBean
import com.example.domain.models.ClientResult
import com.example.starwars.R
import com.example.starwars.base.BaseActivity
import com.example.starwars.databinding.ActivityMainBinding
import com.example.starwars.ui.adapters.PointsTableAdapter
import com.example.starwars.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {


    private val mainViewModel by viewModels<MainViewModel>()


    @Inject
    lateinit var networkUtil: NetworkUtil
    private val pointsTableAdapter by lazy {
        PointsTableAdapter(itemClickListener = { playerWithPointsBean ->
            val intent = Intent(this@MainActivity, MatchScreenActivity::class.java)
            intent.putExtra("PLAYER_DATA", playerWithPointsBean)
            startActivity(intent)
        }, context = this@MainActivity)
    }


    override fun readArguments(extras: Intent) {

    }

    override fun setupUi() {
        mainViewModel.getPlayersAndMatches()
        setupRecyclerView()
    }


    private fun setupRecyclerView() {
        binding.listRV.layoutManager =
            LinearLayoutManager(this)
        binding.listRV.adapter = pointsTableAdapter
    }

    override fun observeData() {
        collectEvent(mainViewModel.playerAndPoints) {
            when (it) {
                is ClientResult.InProgress -> {
                    renderLoadingScreen(true)

                }

                is ClientResult.Success -> {
                    renderLoadingScreen(false)
                    renderSuccessScreen(it.data)
                }

                is ClientResult.Error -> {
                    renderErrorScreenWithRetry(it)
                }

            }


        }
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

    private fun renderErrorScreenWithRetry(clientResult: ClientResult.Error) {
        binding.listRV.visibility = View.GONE
        binding.retryLayoutHolder.retryLayoutCL.visibility = View.VISIBLE
        binding.retryLayoutHolder.retryMessageTV.text = clientResult.error.message
        binding.retryLayoutHolder.retryCTA.setOnClickListener {
            mainViewModel.getPlayersAndMatches()
        }
    }

    private fun renderSuccessScreen(clientResult: List<PlayerWithPointsBean>) {
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
            pointsTableAdapter.submitList(clientResult)
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