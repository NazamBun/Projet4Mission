package com.aura.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aura.R
import com.aura.databinding.ActivityHomeBinding
import com.aura.databinding.ActivityLoginBinding
import com.aura.ui.login.LoginActivity
import com.aura.ui.transfer.TransferActivity
import com.aura.viewmodel.home.HomeViewModel
import com.aura.viewmodel.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * The home activity for the app.
 */
@AndroidEntryPoint
class HomeActivity : AppCompatActivity()
{

  private val viewModel: HomeViewModel by viewModels()
  /**
   * The binding for the home layout.
   */
  private lateinit var binding: ActivityHomeBinding

  /**
   * A callback for the result of starting the TransferActivity.
   */
  private val startTransferActivityForResult =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
      //TODO
    }

  @SuppressLint("SetTextI18n")
  override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)

    binding = ActivityHomeBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val balance = binding.balance
    val transfer = binding.transfer

    balance.text = "indéfini"
    viewModel.getAccounts()

    /*
    lifecycleScope.launch {
      viewModel.uiState.collect { uiState ->
        loading.visibility = if (uiState.isLoading) View.VISIBLE else View.GONE
        login.isEnabled = uiState.isLoginButtonEnabled

        if (uiState.showErrorMessage) {
          errorText.visibility = View.VISIBLE
          errorText.text = uiState.error
          retryButton.visibility = View.VISIBLE
        } else {
          errorText.visibility = View.GONE
          retryButton.visibility = View.GONE
        }
      }
    }

    */

    viewModel.accounts.observe(this) { accounts ->
      val balanceSum = accounts.map { it.balance }.sum()
      balance.text = balanceSum.toString()+" €"
    }

    transfer.setOnClickListener {
      startTransferActivityForResult.launch(Intent(this@HomeActivity, TransferActivity::class.java))
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean
  {
    menuInflater.inflate(R.menu.home_menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean
  {
    return when (item.itemId)
    {
      R.id.disconnect ->
      {
        startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
        finish()
        true
      }
      else            -> super.onOptionsItemSelected(item)
    }
  }

}
