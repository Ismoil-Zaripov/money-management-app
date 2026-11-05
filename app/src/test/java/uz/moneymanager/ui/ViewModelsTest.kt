package uz.moneymanager.ui

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingSource
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import uz.moneymanager.R
import uz.moneymanager.data.local.TransactionEntity
import uz.moneymanager.data.local.TransactionRateEntity
import uz.moneymanager.domain.PaymentMethodNames
import uz.moneymanager.domain.model.TransactionType
import uz.moneymanager.ui.paymentmethods.PaymentMethodEvent
import uz.moneymanager.ui.paymentmethods.PaymentMethodsViewModel
import uz.moneymanager.feature.transactions.presentation.TransactionsViewModel
import uz.moneymanager.ui.transactions.form.FormEvent
import uz.moneymanager.ui.transactions.form.TransactionFormViewModel
import java.math.BigDecimal
import java.time.LocalDate

class ViewModelsTest {

    companion object {
        @JvmStatic
        @BeforeClass
        fun setUpMainDispatcher() {
            System.setProperty("kotlinx.coroutines.fast.service.loader", "false")
        }
    }

    // =========================================================================
    // 1. PaymentMethodsViewModel tests
    // =========================================================================

    @Test
    fun paymentMethodsViewModel_addRenameDelete_andErrorEvents() = runBlocking {
        val env = FakeRepositoriesEnv()
        val viewModel = PaymentMethodsViewModel(env.paymentMethodRepository)

        // Start collecting stateIn flow
        val collectJob = launch(Dispatchers.Main, start = CoroutineStart.UNDISPATCHED) {
            viewModel.paymentMethods.collect {}
        }

        val events = mutableListOf<PaymentMethodEvent>()
        val eventJob = launch(Dispatchers.Main, start = CoroutineStart.UNDISPATCHED) {
            viewModel.events.collect { events.add(it) }
        }

        // Initial seeded items: Cash (id=1, default) and Transfer (id=2)
        assertEquals(2, viewModel.paymentMethods.value.size)
        assertEquals("Cash", viewModel.paymentMethods.value[0].paymentMethod.name)
        assertTrue(viewModel.paymentMethods.value[0].paymentMethod.isDefault)

        // 1. Add valid payment method
        viewModel.addPaymentMethod("  Click   Wallet ")
        assertEquals(PaymentMethodEvent.Success, events.last())
        assertEquals(3, viewModel.paymentMethods.value.size)
        val clickItem = viewModel.paymentMethods.value.first { it.paymentMethod.name == "Click Wallet" }
        assertNotNull(clickItem)

        // 2. Add duplicate name (case-insensitive / extra spaces) -> Error event
        viewModel.addPaymentMethod("  click wallet ")
        val dupEvent = events.last() as PaymentMethodEvent.Error
        assertEquals(R.string.err_payment_method_duplicate, dupEvent.messageRes)
        assertEquals(listOf("Click Wallet"), dupEvent.args)

        // 3. Add blank name -> Error event
        viewModel.addPaymentMethod("   ")
        val blankEvent = events.last() as PaymentMethodEvent.Error
        assertEquals(R.string.err_payment_method_required, blankEvent.messageRes)

        // 4. Add too long name (> 50 chars) -> Error event
        viewModel.addPaymentMethod("X".repeat(PaymentMethodNames.MAX_LENGTH + 1))
        val longEvent = events.last() as PaymentMethodEvent.Error
        assertEquals(R.string.err_payment_method_too_long, longEvent.messageRes)

        // 5. Rename valid
        viewModel.renamePaymentMethod(clickItem.paymentMethod.id, "Payme")
        assertEquals(PaymentMethodEvent.Success, events.last())
        assertTrue(viewModel.paymentMethods.value.any { it.paymentMethod.name == "Payme" })

        // 6. Attempt to delete a payment method that is used in a transaction -> Error event
        env.transactionDao.insert(
            TransactionEntity(
                id = 100L,
                type = TransactionType.EXPENSE,
                amount = BigDecimal("25000"),
                currencyId = 1L,
                categoryId = 1L,
                paymentMethodId = 2L, // Transfer
                date = LocalDate.now(),
                createdAt = 1000L,
            )
        )
        viewModel.deletePaymentMethod(2L)
        val inUseEvent = events.last() as PaymentMethodEvent.Error
        assertEquals(R.string.err_payment_method_in_use, inUseEvent.messageRes)
        assertEquals(listOf("Transfer"), inUseEvent.args)

        // 7. Delete unused payment method (Payme) -> Success
        viewModel.deletePaymentMethod(clickItem.paymentMethod.id)
        assertEquals(PaymentMethodEvent.Success, events.last())
        assertFalse(viewModel.paymentMethods.value.any { it.paymentMethod.id == clickItem.paymentMethod.id })

        collectJob.cancel()
        eventJob.cancel()
    }

    // =========================================================================
    // 2. TransactionFormViewModel tests
    // =========================================================================

    @Test
    fun formViewModel_whenHistoryEmpty_selectsDefaultCash() = runBlocking {
        val env = FakeRepositoriesEnv()
        val viewModel = TransactionFormViewModel(
            savedStateHandle = SavedStateHandle(),
            transactionRepository = env.transactionRepository,
            currencyRepository = env.currencyRepository,
            categoryRepository = env.categoryRepository,
            paymentMethodRepository = env.paymentMethodRepository,
            rateRepository = env.rateRepository,
        )

        val state = viewModel.uiState.value
        assertTrue(state.isNew)
        assertEquals(1L, state.paymentMethodId) // Cash (id=1, isDefault=true)
        assertFalse(state.isDirty)
    }

    @Test
    fun formViewModel_whenHistoryHasTransactions_selectsLastUsedPaymentMethod() = runBlocking {
        val env = FakeRepositoriesEnv()
        // Insert a transaction using Transfer (id = 2L) as the last used payment method
        env.transactionDao.insert(
            TransactionEntity(
                id = 1L,
                type = TransactionType.EXPENSE,
                amount = BigDecimal("75000"),
                currencyId = 1L,
                categoryId = 1L,
                paymentMethodId = 2L, // Transfer
                date = LocalDate.now(),
                createdAt = 5000L,
            )
        )

        val viewModel = TransactionFormViewModel(
            savedStateHandle = SavedStateHandle(),
            transactionRepository = env.transactionRepository,
            currencyRepository = env.currencyRepository,
            categoryRepository = env.categoryRepository,
            paymentMethodRepository = env.paymentMethodRepository,
            rateRepository = env.rateRepository,
        )

        assertEquals(
            "Yangi forma ochilganda oxirgi ishlatilgan to'lov usuli (Transfer, id=2) tanlanishi kerak",
            2L,
            viewModel.uiState.value.paymentMethodId,
        )
        assertFalse(viewModel.uiState.value.isDirty)
    }

    @Test
    fun formViewModel_whenHistoryHasTransactions_selectsLastUsedCurrency_andPreservesSelectionOnSavedStateRestore() = runBlocking {
        val env = FakeRepositoriesEnv()
        val usdId = env.currencyDao.insert(uz.moneymanager.data.local.CurrencyEntity(id = 2L, code = "USD", name = "US Dollar"))
        // Insert a transaction using USD (currencyId = 2L) and Transfer (paymentMethodId = 2L)
        env.transactionDao.insert(
            TransactionEntity(
                id = 1L,
                type = TransactionType.EXPENSE,
                amount = BigDecimal("50"),
                currencyId = usdId, // USD
                categoryId = 1L,
                paymentMethodId = 2L, // Transfer
                date = LocalDate.now(),
                createdAt = 5000L,
            )
        )

        val savedState = SavedStateHandle()
        val viewModel = TransactionFormViewModel(
            savedStateHandle = savedState,
            transactionRepository = env.transactionRepository,
            currencyRepository = env.currencyRepository,
            categoryRepository = env.categoryRepository,
            paymentMethodRepository = env.paymentMethodRepository,
            rateRepository = env.rateRepository,
        )

        // 1. Both last used currency (USD, id=2) and last used payment method (Transfer, id=2) are selected
        assertEquals(2L, viewModel.uiState.value.currencyId)
        assertEquals("USD", viewModel.uiState.value.currency?.code)
        assertEquals(2L, viewModel.uiState.value.paymentMethodId)
        assertFalse(viewModel.uiState.value.isDirty)

        // 2. User changes currency to UZS (id=1) and payment method to Cash (id=1)
        viewModel.setCurrencyId(1L)
        viewModel.setPaymentMethodId(1L)
        assertTrue(viewModel.uiState.value.isDirty)

        // 3. Simulate configuration change / process death with the same SavedStateHandle
        val recreatedViewModel = TransactionFormViewModel(
            savedStateHandle = savedState,
            transactionRepository = env.transactionRepository,
            currencyRepository = env.currencyRepository,
            categoryRepository = env.categoryRepository,
            paymentMethodRepository = env.paymentMethodRepository,
            rateRepository = env.rateRepository,
        )
        assertEquals(1L, recreatedViewModel.uiState.value.currencyId)
        assertEquals(1L, recreatedViewModel.uiState.value.paymentMethodId)
        assertTrue(recreatedViewModel.uiState.value.isDirty)
    }

    @Test
    fun formViewModel_addQuickPaymentMethod_createsAndAutoSelectsNewMethod() = runBlocking {
        val env = FakeRepositoriesEnv()
        val viewModel = TransactionFormViewModel(
            savedStateHandle = SavedStateHandle(),
            transactionRepository = env.transactionRepository,
            currencyRepository = env.currencyRepository,
            categoryRepository = env.categoryRepository,
            paymentMethodRepository = env.paymentMethodRepository,
            rateRepository = env.rateRepository,
        )

        assertEquals(1L, viewModel.uiState.value.paymentMethodId) // Starts at Cash

        viewModel.addQuickPaymentMethod("  Uzcard  ")

        val state = viewModel.uiState.value
        val uzcard = state.paymentMethods.firstOrNull { it.name == "Uzcard" }
        assertNotNull("Yangi qo'shilgan 'Uzcard' ro'yxatda bo'lishi kerak", uzcard)
        assertEquals("Yangi qo'shilgan usul avtomatik tanlanishi kerak", uzcard!!.id, state.paymentMethodId)
        assertTrue(state.isDirty)
    }

    @Test
    fun formViewModel_errorCases_duplicateOrBlankQuickPaymentMethod_andFormValidationErrorsInState() = runBlocking {
        val env = FakeRepositoriesEnv()
        val viewModel = TransactionFormViewModel(
            savedStateHandle = SavedStateHandle(),
            transactionRepository = env.transactionRepository,
            currencyRepository = env.currencyRepository,
            categoryRepository = env.categoryRepository,
            paymentMethodRepository = env.paymentMethodRepository,
            rateRepository = env.rateRepository,
        )

        val events = mutableListOf<FormEvent>()
        val eventJob = launch(Dispatchers.Main, start = CoroutineStart.UNDISPATCHED) {
            viewModel.events.collect { events.add(it) }
        }

        // 1. Duplicate payment method via "+ Qo'shish"
        viewModel.addQuickPaymentMethod("  CASH ")
        val dupErr = events.last() as FormEvent.Error
        assertEquals(R.string.err_payment_method_duplicate, dupErr.messageRes)
        assertEquals(1L, viewModel.uiState.value.paymentMethodId)

        // 2. Blank payment method via "+ Qo'shish"
        viewModel.addQuickPaymentMethod("   ")
        val reqErr = events.last() as FormEvent.Error
        assertEquals(R.string.err_payment_method_required, reqErr.messageRes)

        // 3. Attempting save with empty amount & category -> errors appear in uiState
        viewModel.save()
        assertEquals(R.string.err_amount_required, viewModel.uiState.value.amountErrorRes)
        assertEquals(R.string.err_category_required, viewModel.uiState.value.categoryErrorRes)
        assertFalse(viewModel.uiState.value.canSave)

        eventJob.cancel()
    }

    @Test
    fun formViewModel_editMode_preselectsExistingTransactionPaymentMethodAndSavesChanges() = runBlocking {
        val env = FakeRepositoriesEnv()
        val txDate = LocalDate.now().minusDays(1)
        val txId = env.transactionDao.insert(
            TransactionEntity(
                id = 42L,
                type = TransactionType.INCOME,
                amount = BigDecimal("1500000"),
                currencyId = 1L,
                categoryId = 2L, // Transport
                paymentMethodId = 2L, // Transfer
                date = txDate,
                createdAt = 9000L,
                description = "Avans",
            )
        )
        env.transactionDao.insertSnapshot(listOf(TransactionRateEntity(txId, 1L, BigDecimal.ONE)))

        val viewModel = TransactionFormViewModel(
            savedStateHandle = SavedStateHandle(mapOf("id" to txId.toString())),
            transactionRepository = env.transactionRepository,
            currencyRepository = env.currencyRepository,
            categoryRepository = env.categoryRepository,
            paymentMethodRepository = env.paymentMethodRepository,
            rateRepository = env.rateRepository,
        )

        val state = viewModel.uiState.value
        assertFalse(state.isNew)
        assertFalse(state.isLoading)
        assertEquals(TransactionType.INCOME, state.type)
        assertEquals("1500000", state.amountText)
        assertEquals("Transport", state.categoryText)
        assertEquals(2L, state.paymentMethodId) // Pre-selected Transfer
        assertEquals("Avans", state.description)
        assertEquals(txDate, state.date)
        assertFalse(state.isDirty)

        // Change payment method to Cash (1L) -> becomes dirty and saves
        viewModel.setPaymentMethodId(1L)
        assertTrue(viewModel.uiState.value.isDirty)

        val events = mutableListOf<FormEvent>()
        val eventJob = launch(Dispatchers.Main, start = CoroutineStart.UNDISPATCHED) {
            viewModel.events.collect { events.add(it) }
        }

        viewModel.save()
        assertEquals(FormEvent.Saved, events.last())
        assertEquals(1L, env.transactionDao.getById(txId)?.paymentMethodId)

        eventJob.cancel()
    }

    // =========================================================================
    // 3. TransactionsViewModel tests
    // =========================================================================

    @Test
    fun transactionsViewModel_setAndResetPaymentMethodFilter_updatesTotalsAndDailyTotals() = runBlocking {
        val env = FakeRepositoriesEnv()
        val today = LocalDate.now()

        // Tx 1: Cash (1L), EXPENSE 100_000 UZS
        val tx1 = env.transactionDao.insert(
            TransactionEntity(
                id = 1L,
                type = TransactionType.EXPENSE,
                amount = BigDecimal("100000"),
                currencyId = 1L,
                categoryId = 1L,
                paymentMethodId = 1L, // Cash
                date = today,
                createdAt = 100L,
            )
        )
        env.transactionDao.insertSnapshot(listOf(TransactionRateEntity(tx1, 1L, BigDecimal.ONE)))

        // Tx 2: Transfer (2L), INCOME 400_000 UZS
        val tx2 = env.transactionDao.insert(
            TransactionEntity(
                id = 2L,
                type = TransactionType.INCOME,
                amount = BigDecimal("400000"),
                currencyId = 1L,
                categoryId = 2L,
                paymentMethodId = 2L, // Transfer
                date = today,
                createdAt = 200L,
            )
        )
        env.transactionDao.insertSnapshot(listOf(TransactionRateEntity(tx2, 1L, BigDecimal.ONE)))

        val viewModel = TransactionsViewModel(
            transactionRepository = env.transactionRepository,
            currencyRepository = env.currencyRepository,
            categoryRepository = env.categoryRepository,
            paymentMethodRepository = env.paymentMethodRepository,
        )

        val jobs = listOf(
            launch(Dispatchers.Main, start = CoroutineStart.UNDISPATCHED) { viewModel.paymentMethods.collect {} },
            launch(Dispatchers.Main, start = CoroutineStart.UNDISPATCHED) { viewModel.totals.collect {} },
            launch(Dispatchers.Main, start = CoroutineStart.UNDISPATCHED) { viewModel.dailyTotals.collect {} },
        )

        // Wait for totals to compute on Dispatchers.Default
        val initialTotals = viewModel.totals.first { it.display.count == 2 }
        assertEquals(0, BigDecimal("400000").compareTo(initialTotals.display.income))
        assertEquals(0, BigDecimal("100000").compareTo(initialTotals.display.expense))
        assertEquals(2, viewModel.paymentMethods.value.size)

        // 1. Filter by Transfer (id = 2L)
        viewModel.setPaymentMethodFilter(2L)
        assertEquals(2L, viewModel.filter.value.paymentMethodId)

        val filteredTotals = viewModel.totals.first { it.display.count == 1 }
        assertEquals(0, BigDecimal("400000").compareTo(filteredTotals.display.income))
        assertEquals(0, BigDecimal.ZERO.compareTo(filteredTotals.display.expense))

        val filteredDaily = viewModel.dailyTotals.first { it[today]?.display?.count == 1 }
        assertEquals(0, BigDecimal("400000").compareTo(filteredDaily[today]!!.display.balance))

        val filteredPage = env.transactionDao.paging(
            categoryId = viewModel.filter.value.categoryId,
            currencyId = viewModel.filter.value.currencyId,
            paymentMethodId = viewModel.filter.value.paymentMethodId,
            type = viewModel.filter.value.type,
            fromDate = viewModel.filter.value.from,
            toDate = viewModel.filter.value.to,
            displayCurrencyId = 1L,
            fallbackCurrencyId = 1L,
        ).load(PagingSource.LoadParams.Refresh(null, 20, false)) as PagingSource.LoadResult.Page
        assertEquals(1, filteredPage.data.size)
        assertEquals("Transfer", filteredPage.data[0].paymentMethodName)

        // 2. Reset filters -> paymentMethodId cleared, all transactions included again
        viewModel.resetFilters()
        assertNull(viewModel.filter.value.paymentMethodId)
        assertEquals(1L, viewModel.filter.value.displayCurrencyId)

        val resetTotals = viewModel.totals.first { it.display.count == 2 }
        assertEquals(0, BigDecimal("300000").compareTo(resetTotals.display.balance))

        val resetPage = env.transactionDao.paging(
            categoryId = viewModel.filter.value.categoryId,
            currencyId = viewModel.filter.value.currencyId,
            paymentMethodId = viewModel.filter.value.paymentMethodId,
            type = viewModel.filter.value.type,
            fromDate = viewModel.filter.value.from,
            toDate = viewModel.filter.value.to,
            displayCurrencyId = 1L,
            fallbackCurrencyId = 1L,
        ).load(PagingSource.LoadParams.Refresh(null, 20, false)) as PagingSource.LoadResult.Page
        assertEquals(2, resetPage.data.size)

        jobs.forEach { it.cancel() }
    }
}
