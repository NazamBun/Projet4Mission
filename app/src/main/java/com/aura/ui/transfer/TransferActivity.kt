package com.aura.ui.transfer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.aura.databinding.ActivityTransferBinding
import com.aura.ui.home.HomeActivity
import com.aura.viewmodel.transfer.NavigationEvent
import com.aura.viewmodel.transfer.TransferViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * The transfer activity for the app.
 */
@AndroidEntryPoint
class TransferActivity : AppCompatActivity() {

    /**
     * The binding for the transfer layout.
     */
    private lateinit var binding: ActivityTransferBinding
    private val viewModel: TransferViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
        val sender = sharedPreferences.getString("identifier", "") ?: ""

        val recipient = binding.recipient
        val amount = binding.amount
        val transfer = binding.transfer
        val loading = binding.loading

        transfer.setOnClickListener {
            viewModel.onTransferClicked(
                sender = sender,
                recipient.text.toString(),
                amount.text.toString().toDouble()
            )
        }

        lifecycleScope.launch {
            viewModel.navigationEvents.collect { event ->
                when (event) {
                    is NavigationEvent.NavigateToHome -> {
                        delay(1000)  // Attendre 1 seconde avant la navigation
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }
            }
        }

        recipient.addTextChangedListener { viewModel.onRecipientChanged(it.toString()) }
        amount.addTextChangedListener { viewModel.onAmountChanged(it.toString().toDouble()) }

        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                loading.visibility = if (uiState.isLoading) View.VISIBLE else View.GONE

                if (uiState.error.isNotEmpty()) {
                    Toast.makeText(this@TransferActivity, uiState.error, Toast.LENGTH_LONG).show()
                    viewModel.resetError()
                }


            }
        }
    }

}
