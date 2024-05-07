package com.gorya.vk.ui.main

import com.hoc081098.flowext.flatMapFirst
import com.hoc081098.flowext.withLatestFrom
import com.gorya.vk.FlowTransformer
import com.gorya.vk.ui.main.MainContract.PartialStateChange
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject
import com.gorya.vk.ui.main.MainContract.ViewIntent as VI
import com.gorya.vk.ui.main.MainContract.ViewState as VS

class MainProcessors @Inject constructor(
  private val interactor: MainContract.Interactor,
) {
  internal fun getInitialProcessor(stateFlow: StateFlow<VS>): FlowTransformer<VI.Initial, PartialStateChange> =
    FlowTransformer { intents ->
      intents
        .withLatestFrom(stateFlow)
        .filter { (_, vs) -> vs.productItems.isEmpty() }
        .flatMapMerge {
          merge(
            interactor.productFirstPageChanges(limit = MainVM.PRODUCT_PAGE_SIZE),
          )
        }
    }

  internal fun getNextPageProcessor(stateFlow: StateFlow<VS>): FlowTransformer<VI.LoadNextPage, PartialStateChange> =
    FlowTransformer { intents ->
      intents
        .withLatestFrom(stateFlow)
        .map { (_, vs) -> vs.productItems.size }
        .flatMapFirst {
          interactor.productNextPageChanges(
            start = it,
            limit = MainVM.PRODUCT_PAGE_SIZE
          )
        }
    }

  internal fun getRetryLoadPageProcessor(stateFlow: StateFlow<VS>): FlowTransformer<VI.RetryLoadPage, PartialStateChange> =
    FlowTransformer { intents ->
      intents
        .withLatestFrom(stateFlow)
        .map { (_, vs) -> vs.productItems.size }
        .flatMapFirst {
          interactor.productNextPageChanges(
            start = it,
            limit = MainVM.PRODUCT_PAGE_SIZE
          )
        }
    }

  internal fun getRefreshProcessor(stateFlow: StateFlow<VS>): FlowTransformer<VI.Refresh, PartialStateChange> =
    FlowTransformer { intents ->
      intents
        .withLatestFrom(stateFlow)
        .flatMapFirst {
          interactor.refreshAll(
            limitProduct = MainVM.PRODUCT_PAGE_SIZE,
          )
        }
    }
}
