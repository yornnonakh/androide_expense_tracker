package com.example.fintrack.presentation.addtransaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fintrack.designsystem.theme.FinTrackSpacing
import com.example.fintrack.domain.model.PaymentMethod
import com.example.fintrack.domain.model.TransactionType
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun TransactionFormRoute(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: TransactionFormViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                TransactionFormEvent.Saved -> onSaved()
                is TransactionFormEvent.Error -> snackbar.showSnackbar(event.message)
            }
        }
    }
    TransactionFormScreen(
        state = state,
        snackbarHostState = snackbar,
        onBack = onBack,
        onTypeChange = viewModel::setType,
        onAmountChange = viewModel::setAmount,
        onTitleChange = viewModel::setTitle,
        onCategoryChange = viewModel::setCategory,
        onDateChange = viewModel::setDate,
        onNoteChange = viewModel::setNote,
        onPaymentMethodChange = viewModel::setPaymentMethod,
        onSave = viewModel::save,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFormScreen(
    state: TransactionFormUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onTypeChange: (TransactionType) -> Unit,
    onAmountChange: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onCategoryChange: (Long) -> Unit,
    onDateChange: (Instant) -> Unit,
    onNoteChange: (String) -> Unit,
    onPaymentMethodChange: (PaymentMethod) -> Unit,
    onSave: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditing) "Edit transaction" else "Add transaction") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Go back")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        if (state.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.Center,
            ) { CircularProgressIndicator(Modifier.padding(FinTrackSpacing.extraLarge)) }
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(FinTrackSpacing.large),
            verticalArrangement = Arrangement.spacedBy(FinTrackSpacing.large),
        ) {
            if (state.loadError != null) {
                Text(state.loadError, color = MaterialTheme.colorScheme.error)
                return@Column
            }
            Text("Transaction type", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(FinTrackSpacing.small)) {
                TransactionType.entries.forEach { type ->
                    FilterChip(
                        selected = state.type == type,
                        onClick = { onTypeChange(type) },
                        label = { Text(type.displayName()) },
                    )
                }
            }
            OutlinedTextField(
                value = state.amount,
                onValueChange = onAmountChange,
                label = { Text("Amount") },
                prefix = { Text("$") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = state.errors.amount != null,
                supportingText = state.errors.amount?.let { { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = state.title,
                onValueChange = onTitleChange,
                label = { Text("Title") },
                isError = state.errors.title != null,
                supportingText = state.errors.title?.let { { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            SelectionMenu(
                label = "Category",
                value = state.categories.firstOrNull { it.id == state.categoryId }?.name
                    ?: "Select category",
                options = state.categories.map { it.id to it.name },
                error = state.errors.category,
                onSelected = onCategoryChange,
            )
            TransactionDateField(state.occurredAt, onDateChange)
            PaymentMethodMenu(state.paymentMethod, onPaymentMethodChange)
            OutlinedTextField(
                value = state.note,
                onValueChange = onNoteChange,
                label = { Text("Note (optional)") },
                minLines = 3,
                supportingText = { Text("${state.note.length}/500") },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = onSave,
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isSaving) CircularProgressIndicator() else Text("Save transaction")
            }
        }
    }
}

@Composable
private fun SelectionMenu(
    label: String,
    value: String,
    options: List<Pair<Long, String>>,
    error: String?,
    onSelected: (Long) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(FinTrackSpacing.extraSmall)) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(value, modifier = Modifier.weight(1f))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (id, name) ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = { expanded = false; onSelected(id) },
                )
            }
        }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}

@Composable
private fun PaymentMethodMenu(
    value: PaymentMethod,
    onSelected: (PaymentMethod) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(FinTrackSpacing.extraSmall)) {
        Text("Payment method", style = MaterialTheme.typography.labelLarge)
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(value.displayName(), modifier = Modifier.weight(1f))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            PaymentMethod.entries.forEach { method ->
                DropdownMenuItem(
                    text = { Text(method.displayName()) },
                    onClick = { expanded = false; onSelected(method) },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionDateField(value: Instant, onSelected: (Instant) -> Unit) {
    var showPicker by remember { mutableStateOf(false) }
    val zone = ZoneId.systemDefault()
    Text("Date", style = MaterialTheme.typography.labelLarge)
    OutlinedButton(onClick = { showPicker = true }, modifier = Modifier.fillMaxWidth()) {
        Text(value.atZone(zone).toLocalDate().format(DateTimeFormatter.ofPattern("MMM d, yyyy")))
    }
    if (showPicker) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = value.toEpochMilli())
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { selected ->
                        val date = Instant.ofEpochMilli(selected).atZone(ZoneOffset.UTC).toLocalDate()
                        val time = value.atZone(zone).toLocalTime()
                        onSelected(date.atTime(time).atZone(zone).toInstant())
                    }
                    showPicker = false
                }) { Text("Select") }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("Cancel") } },
        ) { DatePicker(pickerState) }
    }
}

private fun TransactionType.displayName() = name.lowercase().replaceFirstChar(Char::titlecase)
private fun PaymentMethod.displayName() = name.lowercase().split('_').joinToString(" ") {
    it.replaceFirstChar(Char::titlecase)
}
