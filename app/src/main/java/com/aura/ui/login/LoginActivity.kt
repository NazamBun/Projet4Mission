package com.aura.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.aura.databinding.ActivityLoginBinding
import com.aura.ui.home.HomeActivity
import com.aura.viewmodel.login.LoginViewModel
import com.aura.viewmodel.login.NavigationEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * The login activity for the app.
 */
@AndroidEntryPoint
class LoginActivity : AppCompatActivity()
{

  /**
   * The binding for the login layout.
   */
  private lateinit var binding: ActivityLoginBinding

  private val viewModel: LoginViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)

    binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val login = binding.login
    val loading = binding.loading
    val identifier = binding.identifier
    val password = binding.password

    lifecycleScope.launch {
        viewModel.uiState.collect { uiState ->
            loading.visibility = if (uiState.isLoading) View.VISIBLE else View.GONE
            login.isEnabled = uiState.isLoginButtonEnabled
        }
    }

    identifier.addTextChangedListener { viewModel.onIdentifierChanged(it.toString()) }
    password.addTextChangedListener { viewModel.onPasswordChanged(it.toString()) }

    login.setOnClickListener { viewModel.onLoginClicked() }

    lifecycleScope.launch {
        viewModel.navigationEvent.collect { event ->
            when(event){
                is NavigationEvent.NavigateToHome -> {
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
  }
}
