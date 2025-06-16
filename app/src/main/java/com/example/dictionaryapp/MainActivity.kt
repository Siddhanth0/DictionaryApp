package com.example.dictionaryapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dictionaryapp.feature_dictionary.presentation.WordInfoItem
import com.example.dictionaryapp.feature_dictionary.presentation.WordInfoViewModel
import com.example.dictionaryapp.ui.theme.DictionaryAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import androidx.hilt.navigation.compose.hiltViewModel


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DictionaryAppTheme {
                val viewModel: WordInfoViewModel = hiltViewModel()
                val state = viewModel.state.value
                val snackBarHostState = remember { SnackbarHostState() }

                LaunchedEffect(key1 = true) {
                    viewModel.eventFlow.collectLatest { event ->
                        when (event) {
                            is WordInfoViewModel.UIEvent.ShowSnackBar -> {
                                snackBarHostState.showSnackbar(message = event.message)
                            }
                        }
                    }
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "Dictionary",
                                    fontSize = 20.sp
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    },
                    snackbarHost = { SnackbarHost(snackBarHostState) }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(paddingValues)
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            OutlinedTextField(
                                value = viewModel.searchQuery.value,
                                onValueChange = viewModel::onSearch,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                placeholder = { Text("Search for a word...") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    cursorColor = MaterialTheme.colorScheme.primary,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                )
                            )

                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(state.wordInfoItem) { wordInfo ->
                                    WordInfoItem(
                                        wordInfo = wordInfo,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }

                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }
}
