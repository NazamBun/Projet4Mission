package com.aura.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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


/**
 * The login activity for the app.
 */
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    /**
     * The binding for the login layout.
     */
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
                when(event) {
                    is NavigationEvent.ShowSuccessAndNavigate -> {
                        // Afficher le Snackbar en haut de l'écran
                        showSnackbarAtTop("Authentification réussie!")

                        delay(1000)
                        // Naviguer vers l'écran d'accueil après le message
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    }
                    is NavigationEvent.NavigateToHome -> {
                        // Si jamais cet événement est utilisé, naviguer directement vers l'écran d'accueil
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }
    /**
     * Affiche un Snackbar en haut de l'écran.
     */
    private fun showSnackbarAtTop(message: String) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)

        // Accéder à la vue du Snackbar et modifier ses LayoutParams
        val view = snackbar.view
        val params = view.layoutParams as FrameLayout.LayoutParams

        // Positionner le Snackbar en haut de l'écran
        params.gravity = Gravity.TOP
        params.setMargins(0, 0, 0, 0)  // Ajuster les marges si nécessaire

        view.layoutParams = params
        snackbar.show()
    }
}
