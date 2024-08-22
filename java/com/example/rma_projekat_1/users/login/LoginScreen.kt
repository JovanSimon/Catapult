package com.example.rma_projekat_1.users.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.rma_projekat_1.album.grid.AlbumGridContract
import com.example.rma_projekat_1.core.compose.ButtonComponent
import com.example.rma_projekat_1.core.compose.HeadingTextComponent
import com.example.rma_projekat_1.core.compose.MyTextFieldComponent
import com.example.rma_projekat_1.core.compose.NormalTextComponent
import com.example.rma_projekat_1.core.theme.EnableEdgeToEdge
import kotlinx.coroutines.delay
import rs.edu.raf.rma.R

fun NavGraphBuilder.logIn(
    route: String,
    onUserClick: (String) -> Unit
) = composable(
    route = route
) {
    val logInViewModel = hiltViewModel<LoginViewModel>()
    val state = logInViewModel.state.collectAsState()
    EnableEdgeToEdge()
    LoginScreen(
        state = state.value,
        eventPublisher = {
            logInViewModel.setEvent(it)
        },
        onUserClick = onUserClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen (
    state: LoginContract.LoginUiState,
    eventPublisher: (uiEvent: LoginContract.LoginUiEvent) -> Unit,
    onUserClick: (String) -> Unit
) {

    if (state.allValidationPassed) {
        onUserClick("yey")
    }

    if (state.checkingUser) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Scaffold (
            topBar = {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.White),
                ) {
                    Spacer(modifier = Modifier.height(40.dp))
                    CenterAlignedTopAppBar(
                        title = { Text(text = "Create account", style = MaterialTheme.typography.titleLarge) },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }

            },
            content = { paddingValues ->
                if (state.error != null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        val errorMessage = when (state.error) {
                            is LoginContract.LoginUiError.FaildToLogIn ->
                                "Failed to load. Please try later again. Error message: ${state.error.cause?.message}."
                        }
                        Text(text = errorMessage)
                    }
                }else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        MyTextFieldComponent(
                            labelValue = stringResource(id = R.string.first_name),
                            painterResource = painterResource(id = R.drawable.profile),
                            onTextChange = {
                                eventPublisher(LoginContract.LoginUiEvent.nameFieldValue(it))
                            },
                            errorStatus = state.nameFieldEmpty,
                            padding = paddingValues,
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        MyTextFieldComponent(
                            labelValue = stringResource(id = R.string.last_name),
                            painterResource = painterResource(id = R.drawable.profile),
                            onTextChange = {
                                eventPublisher(LoginContract.LoginUiEvent.lastNameFieldValue(it))
                            },
                            errorStatus = state.lastNameFieldEmpty,
                            padding = paddingValues
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        MyTextFieldComponent(
                            labelValue = stringResource(id = R.string.email),
                            painterResource = painterResource(id = R.drawable.profile),
                            onTextChange = {
                                eventPublisher(LoginContract.LoginUiEvent.emailFieldValue(it))
                            },
                            errorStatus = state.emailFieldValid,
                            padding = paddingValues
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        MyTextFieldComponent(
                            labelValue = stringResource(id = R.string.nickname),
                            painterResource = painterResource(id = R.drawable.profile),
                            onTextChange = {
                                eventPublisher(LoginContract.LoginUiEvent.nicknameFieldValue(it))
                            },
                            errorStatus = state.nicknameFieldValid,
                            padding = paddingValues
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        ButtonComponent(
                            value = stringResource(id = R.string.buttonLogin),
                            onButtonClicked = {
                                eventPublisher(LoginContract.LoginUiEvent.LoginButtonClicked)
                            },
                            isEnabled = true
                        )
                    }
                }
            }
        )
    }


}
