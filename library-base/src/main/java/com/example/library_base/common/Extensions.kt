package com.example.library_base.common

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.library_base.common.binding.BaseBindingActivity
import com.example.library_base.common.binding.BaseBindingFragment
import com.example.library_base.common.usecase.FlowResult
import com.example.library_base.common.usecase.IViewListener
import com.example.library_base.common.usecase.UI_STATE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

fun <DATA> MutableStateFlow<FlowResult<DATA>>.data(): DATA? {
    return this.value.data
}

fun <DATA> MutableStateFlow<FlowResult<DATA>>.success(data: DATA) {
    this.value = FlowResult.newInstance<DATA>().success(data)
}

fun <DATA> MutableStateFlow<FlowResult<DATA>>.failure(throwable: Throwable, data: DATA? = null) {
    this.value = FlowResult.newInstance<DATA>().failure(throwable, data)
}

fun <DATA> MutableStateFlow<FlowResult<DATA>>.loading(message: String? = null) {
    this.value = FlowResult.newInstance<DATA>().loading(message)
}

fun <DATA> MutableStateFlow<FlowResult<DATA>>.initial() {
    this.value = FlowResult.newInstance<DATA>().initial()
}

fun <DATA> MutableStateFlow<FlowResult<DATA>>.reset() {
    this.value = FlowResult.newInstance<DATA>().reset()
}

fun <T> Flow<T>.onException(onCatch: suspend (Throwable) -> Unit): Flow<T> {
    return catch { e ->
        e.printStackTrace()
        onCatch(e)
    }
}

fun <T> BaseBindingActivity<*>.handleUiState(
    flowResult: FlowResult<T>,
    listener: IViewListener? = null,
) {
    when (flowResult.getUiState()) {
        UI_STATE.INITIAL -> {
            listener?.onInitial()
        }

        UI_STATE.LOADING -> {
            listener?.onLoading()
        }

        UI_STATE.FAILURE -> {
            listener?.onFailure()
        }

        UI_STATE.SUCCESS -> {
            listener?.onSuccess()
        }
    }
}

fun <T> BaseBindingFragment<*>.handleUiState(
    flowResult: FlowResult<T>,
    listener: IViewListener? = null,
) {
    when (flowResult.getUiState()) {
        UI_STATE.INITIAL -> {
            listener?.onInitial()
        }

        UI_STATE.LOADING -> {
            listener?.onLoading()
        }

        UI_STATE.FAILURE -> {
            listener?.onFailure()
        }

        UI_STATE.SUCCESS -> {
            listener?.onSuccess()
        }
    }
}

fun <DATA> Fragment.coroutinesLaunch(
    flow: Flow<FlowResult<DATA>>,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    launch: suspend (flowResult: FlowResult<DATA>) -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(state = state) {
            flow.collect {
                launch.invoke(it)
            }
        }
    }
}

fun <DATA> AppCompatActivity.coroutinesLaunch(
    flow: Flow<FlowResult<DATA>>,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    launch: suspend (flowResult: FlowResult<DATA>) -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(state = state) {
            flow.collect {
                launch.invoke(it)
            }
        }
    }
}
