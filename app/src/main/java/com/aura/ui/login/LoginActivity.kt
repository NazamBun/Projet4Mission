package com.aura.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.aura.databinding.ActivityLoginBinding
import com.aura.ui.home.HomeActivity
import com.aura.viewmodel.login.LoginViewModel
import com.aura.viewmodel.login.NavigationEvent
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val login = binding.login
        val loading = binding.loading
        val identifier = binding.identifier
        val password = binding.password
        val errorText = binding.errorText
        val retryButton = binding.retryButton

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

        identifier.addTextChangedListener { viewModel.onIdentifierChanged(it.toString()) }
        password.addTextChangedListener { viewModel.onPasswordChanged(it.toString()) }

        login.setOnClickListener { viewModel.onLoginClicked() }

        retryButton.setOnClickListener { viewModel.onRetryClicked() }

        lifecycleScope.launch {
            viewModel.navigationEvent.collect { event ->
                when (event) {
                    is NavigationEvent.ShowSnackbar -> {
                        showSnackbarAtTop(event.message)
                    }
                    is NavigationEvent.NavigateToHome -> {
                        delay(1000)  // Attendre 1 seconde avant la navigation
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    }
                    is NavigationEvent.ShowSuccessAndNavigate -> {
                        showSnackbarAtTop(event.message)
                        delay(1500)  // Afficher le Snackbar pendant 1.5 seconde avant la navigation
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }

    private fun showSnackbarAtTop(message: String) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
        val view = snackbar.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        params.setMargins(0, 0, 0, 0)
        view.layoutParams = params
        snackbar.show()
    }
}
