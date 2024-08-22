package com.example.rma_projekat_1.userDetails.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.rma_projekat_1.core.compose.AppIconButton
import com.example.rma_projekat_1.core.compose.ButtonComponent
import com.example.rma_projekat_1.core.compose.HeadingTextComponent
import com.example.rma_projekat_1.core.compose.MyTextFieldComponent
import com.example.rma_projekat_1.core.compose.NormalTextComponent
import com.example.rma_projekat_1.core.theme.EnableEdgeToEdge
import com.example.rma_projekat_1.userDetails.details.UserDetailContract
import com.example.rma_projekat_1.users.login.LoginContract
import okhttp3.Route
import rs.edu.raf.rma.R

fun NavGraphBuilder.profileEdit(
    route: String,
    onUserClick: (String) -> Unit,
    onClose: () -> Unit
) = composable (
    route = route
) {
    val editViewModel = hiltViewModel<EditViewModel>()

    val state = editViewModel.state.collectAsState()
    EnableEdgeToEdge()
    EditScreen(
        state = state.value,
        eventPublisher = {
            editViewModel.setEvent(it)
        },
        onUserClick = onUserClick,
        onClose = onClose
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    state: EditContract.EditState,
    eventPublisher: (uiEvent: EditContract.EditEvent) -> Unit,
    onUserClick: (String) -> Unit,
    onClose: () -> Unit
) {
    Scaffold(
      topBar = {
          MediumTopAppBar(
              title = { Text(text = stringResource(id = R.string.editHeader),
                  style = MaterialTheme.typography.titleLarge
              )},
              navigationIcon = {
                  AppIconButton(
                      imageVector = Icons.Default.ArrowBack,
                      onClick = onClose,
                  )
              }
          )
      },
        bottomBar = {
            NavigationBar {
                state.navigationItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = state.selectedItemNavigationIndex == index,
                        onClick = {
                            eventPublisher(
                                EditContract.EditEvent.SelectedNavigationIdex(index)
                            )
                            when (index) {
                                0 -> onUserClick("breeds")
                                1 -> onUserClick("quiz")
                                2 -> onUserClick("leaderboard")
                            }
                        },
                        icon = {
                            Icon(imageVector = if (index == state.selectedItemNavigationIndex) {
                                item.selectedIcon
                            } else item.unselectedIcon,
                                contentDescription = item.title)
                        }
                    )

                }
            }
        },
      content = { paddingValues ->

          if (state.error != null) {
              Box(
                  modifier = Modifier.fillMaxSize(),
                  contentAlignment = Alignment.Center,
              ) {
                  val errorMessage = when (state.error) {
                      is EditContract.EditUserError.FaildToUpdate ->
                          "Failed to update user details. Error message: ${state.error.cause?.message}."

                      is EditContract.EditUserError.EditDetailsFaildTo ->
                          "Failed to load user details. Error message: ${state.error.cause?.message}."
                  }
                  Text(text = errorMessage)

                  Spacer(modifier = Modifier.height(16.dp))

                  Row {
                      Button(
                          onClick = onClose,
                          modifier = Modifier.weight(1f)
                      ) {
                          Text(text = "Go back",
                              style = MaterialTheme.typography.bodyMedium
                          )
                      }
                  }
              }
          } else {
              if (state.editHasBeenDone) {
                  onUserClick("profile")
              }

              if(state.loading) {
                  Box(
                      modifier = Modifier.fillMaxSize(),
                      contentAlignment = Alignment.Center
                  ) {
                      CircularProgressIndicator()
                  }
              }else {
                  eventPublisher(EditContract.EditEvent.nameFieldValue(state.user!!.name))
                  eventPublisher(EditContract.EditEvent.lastNameFieldValue(state.user.lastname))
                  eventPublisher(EditContract.EditEvent.emailFieldValue(state.user.email))
                  eventPublisher(EditContract.EditEvent.nicknameFieldValue(state.user.nickname))
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
                              eventPublisher(EditContract.EditEvent.nameFieldValue(it))
                          },
                          errorStatus = state.nameFieldEmpty,
                          padding = paddingValues,
                          initalValue = state.user!!.name
                      )

                      Spacer(modifier = Modifier.height(40.dp))

                      MyTextFieldComponent(
                          labelValue = stringResource(id = R.string.last_name),
                          painterResource = painterResource(id = R.drawable.profile),
                          onTextChange = {
                              eventPublisher(EditContract.EditEvent.lastNameFieldValue(it))
                          },
                          errorStatus = state.lastNameFieldEmpty,
                          padding = paddingValues,
                          initalValue = state.user.lastname
                      )

                      Spacer(modifier = Modifier.height(40.dp))

                      MyTextFieldComponent(
                          labelValue = stringResource(id = R.string.email),
                          painterResource = painterResource(id = R.drawable.profile),
                          onTextChange = {
                              eventPublisher(EditContract.EditEvent.emailFieldValue(it))
                          },
                          errorStatus = state.emailFieldValid,
                          padding = paddingValues,
                          initalValue = state.user.email
                      )

                      Spacer(modifier = Modifier.height(40.dp))

                      MyTextFieldComponent(
                          labelValue = stringResource(id = R.string.nickname),
                          painterResource = painterResource(id = R.drawable.profile),
                          onTextChange = {
                              eventPublisher(EditContract.EditEvent.nicknameFieldValue(it))
                          },
                          errorStatus = state.nicknameFieldValid,
                          padding = paddingValues,
                          initalValue = state.user.nickname
                      )

                      Spacer(modifier = Modifier.height(40.dp))

                      ButtonComponent(
                          value = stringResource(id = R.string.buttonEdit),
                          onButtonClicked = {
                              eventPublisher(EditContract.EditEvent.editButtonClicked)
                          },
                          isEnabled = true
                      )

                  }
              }
          }
      }
    )
}