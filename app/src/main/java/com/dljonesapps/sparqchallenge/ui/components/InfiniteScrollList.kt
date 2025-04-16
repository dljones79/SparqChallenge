package com.dljonesapps.sparqchallenge.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.PersistentList
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun <T> InfiniteScrollList(
    modifier: Modifier = Modifier,
    items: PersistentList<T>,
    loadMoreItems: () -> Unit,
    listState: LazyListState,
    itemContent: @Composable (T) -> Unit,
    isLoading: Boolean,
    buffer: Int = 2
) {
    // Determine when to load more items
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.index != 0 && lastVisibleItem?.index == listState.layoutInfo
                .totalItemsCount - buffer
        }
    }
    
    // Load more items coroutine
    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }
            .distinctUntilChanged()
            .collect {
                if (it) loadMoreItems()
            }
    }

    // LazyColumn to display the list of items
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        state = listState
    ) {
        itemsIndexed(items, key = { _, item -> item.hashCode() }) { _, item ->
            itemContent(item)
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()  // Display a circular loading indicator
                }
            }
        }
    }
}
