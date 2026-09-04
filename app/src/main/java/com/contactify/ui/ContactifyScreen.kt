package com.contactify.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.contactify.data.Countries
import com.contactify.logic.*
import com.contactify.model.Country
import com.contactify.ui.theme.*

private val namePresets = listOf("Contact", "John", "Alice", "Marketing", "Sales", "Support", "Client", "Lead", "Test", "User", "Member")
private val countPresets = listOf(1L,5,10,50,100,500,1000,5000,10000,50000,100000,500000,1000000,10000000,100000000,1000000000)
private val formats = listOf("{name} 1", "{name} #1", "{name} (1)", "{name} [1]", "{name} - 1")

@Composable
fun ContactifyScreen(
    onCreateVcf: (String, (java.io.OutputStream) -> Unit) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var country by remember { mutableStateOf<Country?>(null) }
    var countryCode by rememberSaveable { mutableStateOf("+") }
    var phone by rememberSaveable { mutableStateOf("") }
    var countText by rememberSaveable { mutableStateOf("") }
    var format by rememberSaveable { mutableStateOf("{name} 1") }
    var countryDialog by remember { mutableStateOf(false) }
    var toast by remember { mutableStateOf<String?>(null) }

    val count = countText.toLongOrNull() ?: 0L
    val rawPhone = phone.filter(Char::isDigit)
    val filled = listOf(name.isNotBlank(), rawPhone.isNotBlank(), countText.isNotBlank(), country != null || countryCode != "+").count { it }

    Scaffold(
        containerColor = ContactifyBackground,
        bottomBar = {
            Surface(
                color = Color(0xE10A0B0F),
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            when (filled) {
                                0 -> Unit
                                1 -> {
                                    name = ""; country = null; countryCode = "+"; phone = ""; countText = ""
                                }
                                else -> {
                                    name = ""; country = null; countryCode = "+"; phone = ""; countText = ""
                                }
                            }
                        },
                        enabled = filled > 0,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ContactifyInput,
                            contentColor = Color.White,
                            disabledContainerColor = ContactifyInput.copy(alpha = .45f)
                        )
                    ) {
                        Icon(Icons.Default.Clear, null)
                        Spacer(Modifier.width(6.dp))
                        Text(if (filled == 1) "Clear Field" else "Clear")
                    }

                    Button(
                        onClick = {
                            if (name.isBlank() || rawPhone.isBlank() || count <= 0) {
                                toast = "Complete the required fields first"
                            } else {
                                val code = country?.code ?: countryCode
                                val filename = "Contactify_${name}_${count}.vcf"
                                onCreateVcf(filename) { output ->
                                    writeVcf(output, name.trim(), code, rawPhone, count, format)
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ContactifyGreen,
                            contentColor = Color(0xFF10120B)
                        )
                    ) {
                        Icon(Icons.Default.FileDownload, null)
                        Spacer(Modifier.width(6.dp))
                        Text("Export (.vcf)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Contactify",
                    color = ContactifyGreen,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                )
            }

            item {
                ContactCard("CONTACT NAME") {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("Enter base name", color = ContactifySecondary) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = contactifyFieldColors()
                    )
                    ChipFlow(namePresets) { name = it }
                }
            }

            item {
                ContactCard("CONTACT NUMBER") {
                    OutlineAction(
                        label = "Country",
                        value = country?.let { "${it.emoji}  ${it.name}" } ?: "🌐  Select Country",
                        onClick = { countryDialog = true }
                    )

                    Spacer(Modifier.height(12.dp))

                    Text("Phone number", color = ContactifySecondary, fontSize = 12.sp)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(ContactifyInput)
                            .padding(horizontal = 12.dp)
                    ) {
                        OutlinedTextField(
                            value = countryCode,
                            onValueChange = {
                                val cleaned = it.filter { ch -> ch.isDigit() || ch == '+' }
                                countryCode = if (cleaned.isBlank()) "+" else if (cleaned.startsWith("+")) cleaned else "+$cleaned"
                                if (country == null) {
                                    Countries.all.firstOrNull { c -> c.code == countryCode }?.let { found ->
                                        val matches = Countries.all.filter { c -> c.code == countryCode }
                                        if (matches.size == 1) country = found
                                    }
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.width(82.dp),
                            colors = contactifyFieldColors(),
                            placeholder = { Text("+", color = ContactifySecondary) }
                        )
                        Text("|", color = ContactifySecondary, modifier = Modifier.padding(horizontal = 6.dp))
                        OutlinedTextField(
                            value = if (country != null) applyPhoneMask(rawPhone, country!!.mask) else phone,
                            onValueChange = { phone = it.filter(Char::isDigit) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1f),
                            colors = contactifyFieldColors(),
                            placeholder = { Text(country?.mask ?: "", color = ContactifySecondary) }
                        )
                    }

                    AnimatedVisibility(rawPhone.isNotBlank() && count > 0) {
                        val c = country?.code ?: countryCode
                        val mask = country?.mask ?: generateMask(rawPhone.length)
                        val last = incrementPhone(rawPhone, count)
                        Column(Modifier.padding(top = 12.dp)) {
                            Text("Preview", color = ContactifyGreen, fontWeight = FontWeight.SemiBold)
                            Text("$c ${applyPhoneMask(rawPhone, mask)}", color = Color.White)
                            Text("$c ${applyPhoneMask(last, mask)}", color = ContactifySecondary)
                        }
                    }
                }
            }

            item {
                ContactCard("NUMBER OF CONTACTS") {
                    OutlinedTextField(
                        value = countText,
                        onValueChange = {
                            val digits = it.filter(Char::isDigit).take(10)
                            val value = digits.toLongOrNull()
                            countText = if (value != null && value > 1_000_000_000L) "1000000000" else digits
                        },
                        placeholder = { Text("Max 1,000,000,000", color = ContactifySecondary) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = contactifyFieldColors()
                    )
                    ChipFlow(countPresets.map { it.toString() }) { countText = it }
                }
            }

            item {
                ContactCard("NUMBERING FORMAT") {
                    ChipFlow(formats, selected = format) { format = it }
                }
            }
        }
    }

    if (countryDialog) {
        CountryPicker(
            onDismiss = { countryDialog = false },
            onSelected = {
                country = it
                countryCode = it.code
                countryDialog = false
            }
        )
    }

    toast?.let { message ->
        LaunchedEffect(message) {
            kotlinx.coroutines.delay(2500)
            toast = null
        }
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Surface(
                color = Color(0xEE1B1D22),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.padding(bottom = 90.dp)
            ) {
                Text(message, color = Color.White, modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp))
            }
        }
    }
}

@Composable
private fun ContactCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        color = ContactifyCard,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, ContactifyBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), content = {
            Text(title, color = ContactifyGreen, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
            content()
        })
    }
}

@Composable
private fun OutlineAction(label: String, value: String, onClick: () -> Unit) {
    Column {
        Text(label, color = ContactifySecondary, fontSize = 12.sp)
        Spacer(Modifier.height(4.dp))
        Surface(
            onClick = onClick,
            color = ContactifyInput,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, ContactifyBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(value, color = Color.White)
                Icon(Icons.Default.ChevronRight, null, tint = ContactifySecondary)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipFlow(
    values: List<String>,
    selected: String? = null,
    onClick: (String) -> Unit
) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        values.forEach { value ->
            FilterChip(
                selected = selected == value,
                onClick = { onClick(value) },
                label = { Text(if (value.toLongOrNull() != null) java.text.NumberFormat.getIntegerInstance().format(value.toLong()) else value) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ContactifyGreen,
                    selectedLabelColor = Color(0xFF10120B),
                    containerColor = ContactifyInput,
                    labelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun contactifyFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ContactifyGreen,
    unfocusedBorderColor = ContactifyBorder,
    focusedContainerColor = ContactifyInput,
    unfocusedContainerColor = ContactifyInput,
    cursorColor = ContactifyGreen,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedPlaceholderColor = ContactifySecondary,
    unfocusedPlaceholderColor = ContactifySecondary
)

@Composable
private fun CountryPicker(onDismiss: () -> Unit, onSelected: (Country) -> Unit) {
    var search by remember { mutableStateOf("") }
    val filtered = remember(search) {
        Countries.all.filter {
            it.name.contains(search, ignoreCase = true) || it.code.contains(search)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = ContactifyCard,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().fillMaxHeight(.88f)
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Select Country", color = ContactifyGreen, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, null) }
                }

                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    placeholder = { Text("Search country or code") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = contactifyFieldColors()
                )

                Spacer(Modifier.height(10.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(filtered) { c ->
                        Surface(
                            onClick = { onSelected(c) },
                            color = Color.Transparent,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(c.emoji, fontSize = 25.sp)
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(c.name, color = Color.White)
                                    Text(c.code, color = ContactifyGreen, fontSize = 13.sp)
                                }
                                Text("${c.length} digits", color = ContactifySecondary, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
