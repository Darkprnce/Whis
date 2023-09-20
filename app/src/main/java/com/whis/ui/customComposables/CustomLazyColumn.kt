package com.whis.ui.customComposables

import androidx.compose.animation.*
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.collections.HashSet

@Immutable
sealed class Item<T> {
    class Unavailable<T>: Item<T>()
    data class Available<T>(
        val data: T,
        private var onVisible: (suspend () -> Unit)?
    ): Item<T>() {
        suspend fun notifyVisible() {
            this.onVisible?.invoke()
            this.onVisible = null
        }
    }

    override fun toString(): String {
        return when(this) {
            is Unavailable -> "Unavailable"
            is Available -> "Available (${data.toString()})"
        }
    }
}

@Immutable
data class ItemCollection<T>(
    val entries: List<ItemState<T>>,
    val id: String = UUID.randomUUID().toString()
)

@Stable
class ItemState<T>(
    initialItem: Item<T>,
    val id: String = UUID.randomUUID().toString()
) {
    @Stable
    val item: MutableState<Item<T>> = mutableStateOf(initialItem)

    @Stable
    val isVisible: MutableState<Boolean> = mutableStateOf(true)

    override fun toString(): String {
        return item.value.toString()
    }
}

open class LazyAnimatedColumnAdapter<T>(
    initialItems: List<T> = emptyList(),
    val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default) + SupervisorJob(),
    val isReversed: Boolean = false
) {
    private val entries = LinkedList<ItemState<T>>().apply {
        if (initialItems.isEmpty()) {
            add(ItemState(initialItem = Item.Unavailable()))
        } else {
            addAll(initialItems.map {
                ItemState(
                    initialItem = Item.Available(it, onVisible = null)
                )
            })
        }
    }

    private val _items: MutableStateFlow<ItemCollection<T>> = MutableStateFlow(ItemCollection(entries))

    private val updateQueue: BlockingQueue<T> = LinkedBlockingQueue()
    private val removals: HashSet<ItemState<T>> = HashSet()

    val items: StateFlow<ItemCollection<T>> = _items

    init {
        coroutineScope.launch {
            val item = runCatching {
                withContext(Dispatchers.Default) {
                    updateQueue.poll(Long.MAX_VALUE, TimeUnit.SECONDS)
                }
            }.getOrNull()
            handleItem(item)
        }
    }

    private suspend fun handleItem(item: T?) {
        if (item == null) return
        if (isReversed) {
            val firstItem = entries.first
            firstItem.item.value = Item.Available(item) {
                entries.addFirst(ItemState(initialItem = Item.Unavailable()))
                _items.tryEmit(ItemCollection(entries.toList()))
                pollNextItem()
            }
        } else {
            val lastItem = entries.last
            lastItem.item.value = Item.Available(item) {
                entries.add(ItemState(initialItem = Item.Unavailable()))
                _items.tryEmit(ItemCollection(entries.toList()))
                pollNextItem()
            }
        }
    }

    private suspend fun pollNextItem() {
        coroutineScope.launch {
            delay(MIN_UPDATE_INTERVAL)
            val nextItem = runCatching {
                withContext(Dispatchers.Default) {
                    updateQueue.poll(Long.MAX_VALUE, TimeUnit.SECONDS)
                }
            }.getOrNull()

            handleItem(nextItem)
        }
    }

    fun clear() {
        entries.clear()
        _items.tryEmit(ItemCollection(entries.toList()))
    }

    fun addItem(item: T) {
        entries.removeAll(removals)
        removals.clear()
        updateQueue.add(item)
    }

    fun removeItem(index: Int) {
        val entry = entries[index]
        entry.isVisible.value = false
        removals.add(entry)
    }

    companion object {
        private const val MIN_UPDATE_INTERVAL = 100L
    }
}

object AnimatedLazyColumnDefaults {
    val DefaultHeader: LazyListScope.() -> Unit = {}
    val DefaultFooter: LazyListScope.() -> Unit = {}
}

@Composable
fun <T> AnimatedLazyColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    adapter: LazyAnimatedColumnAdapter<T>,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = if (!adapter.isReversed) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    itemAddTransition: EnterTransition = fadeIn() + expandVertically(expandFrom = Alignment.CenterVertically) { 0 },
    itemRemoveTransition: ExitTransition = fadeOut() + shrinkOut(shrinkTowards = Alignment.Center) { IntSize.Zero },
    header: LazyListScope.() -> Unit = AnimatedLazyColumnDefaults.DefaultHeader,
    footer: LazyListScope.() -> Unit = AnimatedLazyColumnDefaults.DefaultFooter,
    itemContent: @Composable LazyItemScope.(index: Int, item: T) -> Unit
) {
    val collection by adapter.items.collectAsState()

    LazyColumn(
        modifier = modifier,
        state = state,
        reverseLayout = adapter.isReversed,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
    ) {
        header(this)
        for ((index, entry) in collection.entries.withIndex()) {

            item(entry.id) {
                AnimatedItem(
                    item = entry,
                    enterTransition = itemAddTransition,
                    exitTransition = itemRemoveTransition
                ) { data ->
                    itemContent(index, data)
                }
            }
        }
        footer(this)
    }
}

@Composable
private fun <T> AnimatedItem(
    item: ItemState<T>,
    enterTransition: EnterTransition,
    exitTransition: ExitTransition,
    content: @Composable (item: T) -> Unit
) {
    val itemValue by item.item
    AnimatedVisibility(
        visible = item.isVisible.value,
        exit = exitTransition
    ) {
        AnimatedVisibility(
            visible = itemValue is Item.Available,
            enter = enterTransition,
            exit = fadeOut()
        ) {
            when (itemValue) {
                is Item.Available<T> -> AvailableItem(itemValue as Item.Available<T>, content)
                is Item.Unavailable -> Spacer(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun <T> AnimatedVisibilityScope.AvailableItem(
    item: Item.Available<T>,
    content: @Composable (item: T) -> Unit
) {
    LaunchedEffect(Unit) {
        snapshotFlow { this@AvailableItem.transition.currentState }.collectLatest {
            if (it == EnterExitState.Visible) {
                item.notifyVisible()
            }
        }
    }
    content(item.data)
}