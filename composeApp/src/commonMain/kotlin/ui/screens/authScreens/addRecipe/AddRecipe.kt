package ui.screens.authScreens.addRecipe

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import data.model.IngredientUnit
import data.model.RecipeCategory
import data.remote.model.CategoryUpload
import data.remote.model.Ingredient
import data.remote.model.MainImageUpload
import data.remote.model.RecipeUpload
import kotlinx.datetime.Clock
import ui.components.FormScreens.FormControlButtons
import ui.components.FormScreens.FormTextFieldWithoutIcons
import ui.components.FullSizeRow
import ui.components.theme.ScreenHolder
import ui.components.theme.Section
import ui.components.theme.clickableWithRipple
import ui.components.theme.sectionShadow
import ui.components.theme.text.BodyText
import ui.components.theme.text.ErrorText
import ui.components.theme.text.FooterText
import ui.navigations.Destination
import ui.screens.authScreens.medication.AddButton
import util.UUID


fun NavGraphBuilder.addRecipe(
    navController: NavHostController,
) {
    composable(Destination.AddRecipe.route) {
        ScreenHolder(
            title = Destination.AddRecipe.title,
            showBackButton = true,
            onNavigate = {
                navController.navigate(Destination.MealPlanCalendar.route)
            },
            optionsRow = {},
            itemSpacing = 24.dp
        ) {
            val viewModel = viewModel<AddRecipeViewModel>()
            val selectedImage by viewModel.selectedImage.collectAsStateWithLifecycle()
            val isUploading by viewModel.isUploading.collectAsStateWithLifecycle()
            val error by viewModel.error.collectAsStateWithLifecycle()

            val name = remember { mutableStateOf("") }
            val description = remember { mutableStateOf("") }
            val preperation = remember { mutableStateOf("") }
            val preparationTimeInMinutes = remember { mutableStateOf("") }
            val caregory = remember { mutableStateOf(RecipeCategory.SNACK.categoryId) }
            var isPrivate by remember { mutableStateOf(false) }

            val nameFocusRequester = remember { FocusRequester() }
            val descriptionFocusRequester = remember { FocusRequester() }
            val preperationFocusRequester = remember { FocusRequester() }
            val preparationTimeInMinutesFocusRequester = remember { FocusRequester() }
            val focusManager = LocalFocusManager.current

            val ingredients = remember { mutableStateListOf<Ingredient>() }

            ImageUploadScreen(selectedImage = selectedImage) { bitMap ->
                bitMap?.let {
                    viewModel.setSelectedImage(it)
                }
            }

            RowTextField(
                title = "Rezept Name:",
                value = name,
                focusRequester = nameFocusRequester,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { descriptionFocusRequester.requestFocus() }),
                onValueChange = { name.value = it }
            )

            RowTextField(
                title = "Rezept beschreibung:",
                value = description,
                focusRequester = descriptionFocusRequester,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { preperationFocusRequester.requestFocus() }),
                onValueChange = { description.value = it }
            )

            RowTextField(
                title = "Zubereitung:",
                value = preperation,
                focusRequester = preperationFocusRequester,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { preparationTimeInMinutesFocusRequester.requestFocus() }),
                onValueChange = { preperation.value = it }
            )

            RowTextField(
                title = "Zubereitungsdauer:",
                value = preparationTimeInMinutes,
                focusRequester = preparationTimeInMinutesFocusRequester,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                onValueChange = { preparationTimeInMinutes.value = it }
            )

            // INGREDIENTS
            Column {
                Row { BodyText(modifier = Modifier.fillMaxWidth(), text = "Zutaten") }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (ingredient in ingredients) {
                        IngredientRow(
                            ingredient = ingredient,
                            onIngredientChange = { updatedIngredient ->
                                val index = ingredients.indexOf(ingredient)
                                if (index != -1) {
                                    ingredients[index] = updatedIngredient
                                }
                            }
                        )
                    }
                }
            }

            // ADD INGREDIENTS
            AddButton("Hinzufügen") {
                ingredients.add(
                    Ingredient(
                        id = ingredients.size + 1,
                        name = "",
                        unit = IngredientUnit.Gramm.unit,
                        amount = ""
                    )
                )
            }


            Section {
                MenuCategoryDropdownMenu() {
                    caregory.value = it
                }
            }


            // TOGGEL
            FullSizeRow {
                BodyText(
                    modifier = Modifier.weight(1f),
                    text = "Privates Rezept?"
                )

                Switch(
                    checked = isPrivate,
                    onCheckedChange = {
                        isPrivate = it
                    },
                    thumbContent = if (isPrivate) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    } else {
                        null
                    }
                )
            }


            // BUTTONS
            FullSizeRow {
                if (isUploading) {
                    Spacer(modifier = Modifier.weight(1f))
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.weight(1f))
                } else {
                    FormControlButtons(
                        onCancel = { /*TODO*/ },
                        onSave = {

                            val recipe = RecipeUpload(
                                mealId = UUID.randomUUID(),
                                userId= "String",
                                name = name.value,
                                description = description.value,
                                isSnack = false,
                                isPrivate = isPrivate,
                                isDeleted = false,
                                preparation = preperation.value,
                                preparationTimeInMinutes = preparationTimeInMinutes.value,
                                ingredients = ingredients.filter { it.name.isNotBlank() && it.amount.isNotBlank() && it.unit.isNotBlank() },
                                protein = 0.0,
                                fat = 0.0,
                                sugar = 0.0,
                                kcal = 0.0,
                                mainImage = MainImageUpload(
                                    id = 0
                                ),
                                category= CategoryUpload(
                                    categoryId = caregory.value,
                                    name = caregory.value
                                ),
                                updatedAtOnDevice = Clock.System.now().toEpochMilliseconds(),
                            )

                            viewModel.uploadImage {
                                val mainImage = it

                                mainImage?.let {
                                    viewModel.uploadRecipe(recipe.copy(mainImage = MainImageUpload(mainImage.id.toInt())))
                                }
                            }
                        }
                    )
                }
            }

            ErrorText(
                modifier = Modifier.alpha(if (error == null) 0f else 1f),
                text = error ?: "",
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}





@Composable
fun RowTextField(
    title: String,
    value: MutableState<String>,
    focusRequester: FocusRequester,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
    keyboardActions: KeyboardActions,
    onValueChange: (String) -> Unit = {}
) {
    Column {
        Row { BodyText(modifier = Modifier.fillMaxWidth(),text = title) }
        FormTextFieldWithoutIcons(
            modifier = Modifier.focusRequester(focusRequester),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            inputValue = value.value,
            onValueChange = { onValueChange(it)  }
        )
    }
}


@Composable
@Preview(showBackground = true)
fun IngredientRow(
    ingredient: Ingredient = Ingredient(id = 1, name = "", unit = IngredientUnit.Gramm.unit, amount = ""),
    onIngredientChange: (Ingredient) -> Unit = {} // Lambda to handle ingredient updates
) {
    Row(
        modifier = Modifier
            .sectionShadow()
            .padding(0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            modifier = Modifier.weight(1f), // Make the name field occupy more space
            value = ingredient.name,
            onValueChange = { onIngredientChange(ingredient.copy(name = it)) }, // Update name on change
            placeholder = {
                BodyText(
                    text = "Zutat",
                    color = Color.Gray.copy(alpha = 0.5f)
                )
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 12.sp
            ),
            colors = TextFieldDefaults.colors().copy(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
            )
        )

        TextField(
            modifier = Modifier.weight(1f), // Make the amount field occupy more space
            value = ingredient.amount,
            onValueChange = { onIngredientChange(ingredient.copy(amount = it)) }, // Update amount on change
            placeholder = {
                BodyText(
                    text = "Menge",
                    color = Color.Gray.copy(alpha = 0.5f)
                )
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 12.sp
            ),
            colors = TextFieldDefaults.colors().copy(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )

        IngredientDropdownMenu(
            unit = ingredient.unit,
            onSelect = { onIngredientChange(ingredient.copy(unit = it.unit)) } // Update unit on selection
        )
    }
}

@Composable
@Preview(showBackground = true)
fun IngredientDropdownMenu(
    unit: String = "",
    onSelect: (IngredientUnit) -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }
    val items = IngredientUnit.entries
    var selectedItem by remember { mutableStateOf(IngredientUnit.fromStrong(unit) ?: IngredientUnit.Gramm) } // Selected unit

    Box(modifier = Modifier) {
        BodyText(
            modifier = Modifier
                .width(50.dp)
                .clickableWithRipple {
                    expanded = true
                },
            color = MaterialTheme.colorScheme.primary,
            text = selectedItem.unit
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.align(Alignment.Center)
        ) {
            items.forEach { ingredientUnit ->
                DropdownMenuItem(
                    text = {
                        FooterText(
                            text = ingredientUnit.unit,
                            color = if (selectedItem == ingredientUnit) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        expanded = false
                        selectedItem = ingredientUnit
                        onSelect(ingredientUnit)
                    }
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MenuCategoryDropdownMenu(
    unit: String = "",
    onSelect: (String) -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }
    val items = RecipeCategory.entries // List of unit options
    var selectedItem by remember { mutableStateOf(RecipeCategory.fromStrong(unit) ?: RecipeCategory.SNACK) } // Selected unit

    Box(modifier = Modifier) {
        BodyText(
            modifier = Modifier
                .fillMaxWidth()
                .clickableWithRipple {
                    expanded = true
                },
            color = MaterialTheme.colorScheme.primary,
            text = selectedItem.displayName
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.align(Alignment.Center)
        ) {
            items.forEach { category ->
                DropdownMenuItem(
                    text = {
                        FooterText(
                            text = category.displayName,
                            color = if (selectedItem == category) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        expanded = false
                        selectedItem = category
                        onSelect(category.displayName)
                    }
                )
            }
        }
    }
}

@Composable
fun ImageUploadScreen(
    modifier: Modifier = Modifier,
    selectedImage: Bitmap?,
    updateImage: (Bitmap?) -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }
            updateImage(bitmap)
        }
    }

    Column(modifier = modifier.padding(16.dp)) {
        selectedImage?.let { image ->
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                bitmap = image.asImageBitmap(),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(2.dp, Color.Gray)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = { launcher.launch("image/*") }
            ) {
                Text("Bild auswählen")
            }

            if(selectedImage != null) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        // TODO Bild an Server senden
                        updateImage(null)
                    }
                ) {
                    Text("Bild Entfernen")
                }
            }
        }
    }
}