package com.contactify

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import com.contactify.ui.ContactifyScreen
import com.contactify.ui.theme.ContactifyTheme

class MainActivity : ComponentActivity() {
    private val createDocument =
        registerForActivityResult(ActivityResultContracts.CreateDocument("text/vcard")) { uri ->
            if (uri != null) {
                pendingExport?.let { export ->
                    contentResolver.openOutputStream(uri)?.use { output ->
                        export(output)
                    }
                }
                pendingExport = null
            }
        }

    private var pendingExport: ((java.io.OutputStream) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ContactifyTheme {
                ContactifyScreen(
                    onCreateVcf = { filename, writer ->
                        pendingExport = writer
                        createDocument.launch(filename)
                    }
                )
            }
        }
    }
}
