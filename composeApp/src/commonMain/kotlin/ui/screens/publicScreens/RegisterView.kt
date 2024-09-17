package ui.screens.publicScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import bauchglueck.composeapp.generated.resources.Res
import bauchglueck.composeapp.generated.resources.ic_mail_fill
import bauchglueck.composeapp.generated.resources.ic_person_fill_view
import bauchglueck.composeapp.generated.resources.magen
import org.jetbrains.compose.resources.painterResource
import ui.components.FormScreens.FormPasswordTextFieldWithIcon
import ui.components.FormScreens.FormTextFieldWithIcon
import ui.components.theme.AppBackground
import ui.components.theme.background.AppBackgroundWithImage
import ui.components.theme.button.IconButton
import ui.components.theme.button.TextButton
import ui.components.theme.text.BodyText
import ui.components.theme.text.ErrorText
import ui.components.theme.text.HeadlineText
import ui.navigations.Destination
import viewModel.FirebaseAuthViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
fun NavGraphBuilder.signUp(navController: NavHostController, firebaseAuthViewModel: FirebaseAuthViewModel) {
    composable(Destination.SignUp.route) {
        val state = firebaseAuthViewModel.userFormState.collectAsStateWithLifecycle()

        val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val datePickerState = rememberDatePickerState()
        var showDatePicker by remember { mutableStateOf(false) }

        AppBackground {
            AppBackgroundWithImage()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(
                    space = 8.dp,
                    alignment = Alignment.CenterVertically
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Image(
                        painter = painterResource(Res.drawable.magen),
                        contentDescription = "Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(top = 25.dp, end = 10.dp)
                            .width(150.dp)
                            .height(150.dp)
                    )

                    HeadlineText(
                        text = "Hallo!",
                        color = MaterialTheme.colorScheme.primary,
                    )
                    BodyText("Erstelle dein Konto!")
                }

                FormTextFieldWithIcon(
                    inputValue = state.value.firstName,
                    leadingIcon = Res.drawable.ic_person_fill_view,
                    onValueChange = {
                        firebaseAuthViewModel.onChangeFirstName(it)
                    }
                )

                FormTextFieldWithIcon(
                    inputValue = state.value.email,
                    leadingIcon = Res.drawable.ic_mail_fill,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    onValueChange = {
                        firebaseAuthViewModel.onChangeEmail(it)
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    BodyText("Operationsdatum:")

                    val selectedDate = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                    val datePattern = "dd. MM. yyyy"
                    val formattedDate = remember {
                        SimpleDateFormat(datePattern, Locale.getDefault()).format(Date(selectedDate))
                    }

                    TextButton(text = formattedDate) { showDatePicker = true }
                }

                FormPasswordTextFieldWithIcon(
                    inputValue = state.value.password,
                    onValueChange = {
                        firebaseAuthViewModel.onChangePassword(it)
                    }
                )

                FormPasswordTextFieldWithIcon(
                    inputValue = state.value.confirmPassword,
                    onValueChange = {
                        firebaseAuthViewModel.onChangeConfirmPassword(it)
                    }
                )

                if (state.value.isProcessing) {
                    CircularProgressIndicator()
                } else {
                    Row(
                        modifier = Modifier.align(Alignment.End),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        TextButton("Zur Anmeldung") {
                            navController.navigate(Destination.Login.route)
                        }

                        IconButton(
                            onClick = {
                                val result = firebaseAuthViewModel.onSignUp()
                                if (result) {
                                    navController.navigate(Destination.Home.route)
                                }
                            }
                        )
                    }

                    ErrorText(text = firebaseAuthViewModel.userFormState.value.error)
                }

                if (showDatePicker) {
                    ModalBottomSheet(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(
                                bottom = WindowInsets
                                    .navigationBarsIgnoringVisibility
                                    .asPaddingValues()
                                    .calculateBottomPadding()
                            ),
                        onDismissRequest = { showDatePicker = false },
                        sheetState = modalBottomSheetState
                    ) {
                        DatePicker(
                            state = datePickerState,
                            modifier = Modifier.fillMaxWidth(),
                            colors = DatePickerDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.background,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                                headlineContentColor = MaterialTheme.colorScheme.onBackground,
                            )
                        )

                        TextButton(
                            modifier = Modifier.padding(8.dp).fillMaxWidth(),
                            text = "Bestätigen"
                        ) {
                            showDatePicker = false
                        }
                    }
                }

                Spacer(modifier = Modifier)
            }
        }
    }
}