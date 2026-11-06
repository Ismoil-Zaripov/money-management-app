package uz.moneymanager.ui

import android.content.Context
import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Provider
import uz.moneymanager.R
import uz.moneymanager.data.local.AppDatabase
import uz.moneymanager.data.local.CategoryEntity
import uz.moneymanager.data.local.TransactionEntity
import uz.moneymanager.data.local.TransactionRateEntity
import uz.moneymanager.data.remote.CbuApi
import uz.moneymanager.data.remote.CbuRateDto
import uz.moneymanager.data.repository.CategoryRepository
import uz.moneymanager.data.repository.CurrencyRepository
import uz.moneymanager.data.repository.ExchangeRateRepository
import uz.moneymanager.data.repository.PaymentMethodRepository
import uz.moneymanager.data.repository.TransactionRepository
import uz.moneymanager.domain.model.TransactionType
import uz.moneymanager.feature.paymentmethods.ui.PaymentMethodsRoute
import uz.moneymanager.feature.paymentmethods.presentation.PaymentMethodsViewModel
import uz.moneymanager.core.designsystem.theme.MoneyManagerTheme
import uz.moneymanager.feature.transactions.ui.TransactionsRoute
import uz.moneymanager.feature.transactions.presentation.TransactionsViewModel
import uz.moneymanager.feature.transactionform.ui.TransactionFormRoute
import uz.moneymanager.feature.transactionform.presentation.TransactionFormViewModel
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class PaymentMethodsComposeUiTest {

    companion object {
        @JvmStatic
        @BeforeClass
        fun initEspressoForApi36() {
            // Android 17 (API 36) moved InputManager.getInstance() to InputManagerGlobal.getInstance(),
            // which causes Espresso 3.6.1's InputManagerEventInjectionStrategy.initialize() to fail
            // during Espresso.onIdle(). Pre-initializing the strategy avoids the reflection error.
            try {
                val strategyClass = Class.forName("androidx.test.espresso.base.InputManagerEventInjectionStrategy")
                val strategyCtor = strategyClass.getDeclaredConstructor().apply { isAccessible = true }
                val strategy = strategyCtor.newInstance()
                try {
                    val imGlobalClass = Class.forName("android.hardware.input.InputManagerGlobal")
                    val imGlobal = imGlobalClass.getDeclaredMethod("getInstance").apply { isAccessible = true }.invoke(null)
                    val injectMethod = imGlobalClass.getDeclaredMethod(
                        "injectInputEvent",
                        android.view.InputEvent::class.java,
                        Int::class.javaPrimitiveType,
                    ).apply { isAccessible = true }
                    strategyClass.getDeclaredField("instanceInputManagerObject").apply {
                        isAccessible = true
                        set(strategy, imGlobal)
                    }
                    strategyClass.getDeclaredField("injectInputEventMethod").apply {
                        isAccessible = true
                        set(strategy, injectMethod)
                    }
                    strategyClass.getDeclaredField("syncEventMode").apply {
                        isAccessible = true
                        setInt(strategy, 2)
                    }
                } catch (_: Throwable) {
                }
                strategyClass.getDeclaredField("initComplete").apply {
                    isAccessible = true
                    setBoolean(strategy, true)
                }

                val eventInjectorClass = Class.forName("androidx.test.espresso.base.EventInjector")
                val injectionStrategyInterface = Class.forName("androidx.test.espresso.base.EventInjectionStrategy")
                val injectorCtor = eventInjectorClass.getDeclaredConstructor(injectionStrategyInterface).apply {
                    isAccessible = true
                }
                val eventInjector = injectorCtor.newInstance(strategy)

                val espressoClass = Class.forName("androidx.test.espresso.Espresso")
                val baseField = espressoClass.getDeclaredField("BASE").apply { isAccessible = true }
                val baseComponent = baseField.get(null)
                val doubleCheck = baseComponent.javaClass.getDeclaredField("provideEventInjectorProvider").apply {
                    isAccessible = true
                }.get(baseComponent)
                doubleCheck.javaClass.getDeclaredField("instance").apply {
                    isAccessible = true
                    set(doubleCheck, eventInjector)
                }
            } catch (_: Throwable) {
            }
        }
    }

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var db: AppDatabase
    private lateinit var currencyRepository: CurrencyRepository
    private lateinit var categoryRepository: CategoryRepository
    private lateinit var paymentMethodRepository: PaymentMethodRepository
    private lateinit var rateRepository: ExchangeRateRepository
    private lateinit var transactionRepository: TransactionRepository

    private val context: Context
        get() = composeTestRule.activity

    @Before
    fun setUp() {
        val appContext = composeTestRule.activity.applicationContext
        db = Room.inMemoryDatabaseBuilder(appContext, AppDatabase::class.java)
            .allowMainThreadQueries()
            .addCallback(object : androidx.room.RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    AppDatabase.SEED_CALLBACK.onCreate(db)
                }
            })
            .build()

        // Trigger onCreate seed callback
        db.openHelper.writableDatabase

        val cbuApi = object : CbuApi {
            override suspend fun ratesForDate(date: String): List<CbuRateDto> = emptyList()
        }

        currencyRepository = CurrencyRepository(db, db.currencyDao())
        categoryRepository = CategoryRepository(db, db.categoryDao())
        paymentMethodRepository = PaymentMethodRepository(db, db.paymentMethodDao())
        rateRepository = ExchangeRateRepository(db, db.exchangeRateDao(), db.currencyDao(), cbuApi)
        transactionRepository = TransactionRepository(
            db = db,
            dao = db.transactionDao(),
            currencyDao = db.currencyDao(),
            categoryRepository = categoryRepository,
            paymentMethodDao = db.paymentMethodDao(),
            rateRepository = rateRepository,
        )
    }

    @After
    fun tearDown() {
        db.close()
    }

    // =========================================================================
    // 1. PaymentMethodsScreen: "Standart" badge, qo'shish, nomini o'zgartirish, o'chirish
    // =========================================================================
    @Test
    fun paymentMethodsScreen_defaultBadge_addRenameAndDeleteFlow() {
        val viewModel = PaymentMethodsViewModel(paymentMethodRepository)

        composeTestRule.setContent {
            MoneyManagerTheme {
                PaymentMethodsRoute(
                    onNavigateBack = {},
                    viewModel = viewModel,
                )
            }
        }

        val titleText = context.getString(R.string.title_payment_methods)
        val defaultBadgeText = context.getString(R.string.payment_method_default_badge)
        val addMethodDesc = context.getString(R.string.action_add_payment_method)
        val renameTitleText = context.getString(R.string.action_rename_payment_method)
        val saveText = context.getString(R.string.action_save)
        val editDesc = context.getString(R.string.action_edit)
        val deleteDesc = context.getString(R.string.action_delete)
        val zeroTxText = context.resources.getQuantityString(R.plurals.payment_method_transactions_count, 0, 0)

        // 1. Screen title, Cash, Transfer, and "Standart" (Default) badge are displayed
        composeTestRule.onNodeWithText(titleText).assertIsDisplayed()
        composeTestRule.onNodeWithText("Cash").assertIsDisplayed()
        composeTestRule.onNodeWithText("Transfer").assertIsDisplayed()
        composeTestRule.onNodeWithText(defaultBadgeText).assertIsDisplayed()
        assertTrue(composeTestRule.onAllNodesWithText(zeroTxText).fetchSemanticsNodes().size >= 2)

        // Default "Cash" (index 0) delete button is disabled, "Transfer" (index 1) delete button is enabled
        composeTestRule.onAllNodesWithContentDescription(deleteDesc)[0].assertIsNotEnabled()
        composeTestRule.onAllNodesWithContentDescription(deleteDesc)[1].assertIsEnabled()

        // 2. Add new payment method "Humo"
        composeTestRule.onNodeWithText(addMethodDesc).performClick()
        composeTestRule.onNode(hasSetTextAction()).performTextReplacement("Humo")
        composeTestRule.onNodeWithText(saveText).performClick()

        composeTestRule.onNodeWithText("Humo").assertIsDisplayed()

        // Order is now: 0 = Cash (default), 1 = Humo, 2 = Transfer
        // 3. Rename "Humo" -> "Humo Card"
        composeTestRule.onAllNodesWithContentDescription(editDesc)[1].performClick()
        composeTestRule.onNodeWithText(renameTitleText).assertIsDisplayed()
        composeTestRule.onNode(hasSetTextAction()).performTextReplacement("Humo Card")
        composeTestRule.onNodeWithText(saveText).performClick()

        composeTestRule.onNodeWithText("Humo Card").assertIsDisplayed()

        // 4. Delete unused "Humo Card"
        composeTestRule.onAllNodesWithContentDescription(deleteDesc)[1].performClick()
        val confirmMsg = context.getString(R.string.delete_payment_method_confirm, "Humo Card")
        composeTestRule.onNodeWithText(confirmMsg).assertIsDisplayed()
        // Click confirm button with text action_delete inside dialog
        composeTestRule.onNode(hasText(deleteDesc) and hasClickAction()).performClick()

        composeTestRule.onNodeWithText("Humo Card").assertDoesNotExist()
    }

    // =========================================================================
    // 2a. PaymentMethodsScreen: Ishlatilgan usulni o'chirishda Snackbar xabari (dialog orqali)
    // =========================================================================
    @Test
    fun paymentMethodsScreen_deleteUsedMethod_whenConfirmedWhileInUse_showsInUseErrorSnackbar() {
        val viewModel = PaymentMethodsViewModel(paymentMethodRepository)

        composeTestRule.setContent {
            MoneyManagerTheme {
                PaymentMethodsRoute(
                    onNavigateBack = {},
                    viewModel = viewModel,
                )
            }
        }

        val deleteDesc = context.getString(R.string.action_delete)
        val expectedInUseMsg = context.getString(R.string.err_payment_method_in_use, "Transfer")

        // Open delete confirmation dialog for "Transfer" (index 1) while it has 0 transactions
        composeTestRule.onAllNodesWithContentDescription(deleteDesc)[1].performClick()
        val confirmMsg = context.getString(R.string.delete_payment_method_confirm, "Transfer")
        composeTestRule.onNodeWithText(confirmMsg).assertIsDisplayed()

        // Before confirming delete, a transaction using Transfer (id = 2L) is saved
        runBlocking {
            val catId = db.categoryDao().insert(CategoryEntity(name = "Ovqat", nameKey = "ovqat"))
            db.transactionDao().insert(
                TransactionEntity(
                    type = TransactionType.EXPENSE,
                    amount = BigDecimal("50000"),
                    currencyId = 1L,
                    categoryId = catId,
                    paymentMethodId = 2L, // Transfer
                    date = LocalDate.now(),
                    createdAt = 1000L,
                )
            )
        }

        // Confirm delete -> repository rejects deletion and Snackbar displays err_payment_method_in_use
        composeTestRule.onNode(hasText(deleteDesc) and hasClickAction()).performClick()
        composeTestRule.onNodeWithText(expectedInUseMsg).assertIsDisplayed()
        composeTestRule.onNodeWithText("Transfer").assertIsDisplayed()
    }

    // =========================================================================
    // 2b. PaymentMethodsScreen: Ishlatilgan usulda Delete tugmasini bosib o'chirishga urinish
    // =========================================================================
    @Test
    fun paymentMethodsScreen_deleteUsedMethod_clickingDeleteButtonDirectly_shouldShowInUseMessage() {
        // Seed a category and a transaction using Transfer (id = 2L)
        runBlocking {
            val catId = db.categoryDao().insert(CategoryEntity(name = "Ovqat", nameKey = "ovqat"))
            db.transactionDao().insert(
                TransactionEntity(
                    type = TransactionType.EXPENSE,
                    amount = BigDecimal("50000"),
                    currencyId = 1L,
                    categoryId = catId,
                    paymentMethodId = 2L, // Transfer
                    date = LocalDate.now(),
                    createdAt = 1000L,
                )
            )
        }

        val viewModel = PaymentMethodsViewModel(paymentMethodRepository)

        composeTestRule.setContent {
            MoneyManagerTheme {
                PaymentMethodsRoute(
                    onNavigateBack = {},
                    viewModel = viewModel,
                )
            }
        }

        val oneTxText = context.resources.getQuantityString(R.plurals.payment_method_transactions_count, 1, 1)
        val deleteDesc = context.getString(R.string.action_delete)
        val expectedInUseMsg = context.getString(R.string.err_payment_method_in_use, "Transfer")

        composeTestRule.onNodeWithText("Transfer").assertIsDisplayed()
        composeTestRule.onNodeWithText(oneTxText).assertIsDisplayed()

        // Default "Cash" (index 0) delete button is disabled; used "Transfer" (index 1) delete button is enabled
        composeTestRule.onAllNodesWithContentDescription(deleteDesc)[0].assertIsNotEnabled()
        composeTestRule.onAllNodesWithContentDescription(deleteDesc)[1].assertIsEnabled()

        // User attempts to click Delete on "Transfer" (index 1)
        composeTestRule.onAllNodesWithContentDescription(deleteDesc)[1].performClick()

        // Expected per spec ("o'chirishga urinish xabari"): user sees the in-use error message and Transfer remains
        composeTestRule.onNodeWithText(expectedInUseMsg).assertIsDisplayed()
        composeTestRule.onNodeWithText("Transfer").assertIsDisplayed()
    }

    // =========================================================================
    // 2c. PaymentMethodsScreen: Ekran aylantirilganda (StateRestoration) ochiq dialoglar saqlanishi
    // =========================================================================
    @Test
    fun paymentMethodsScreen_dialogsSurviveConfigurationChange() {
        val viewModel = PaymentMethodsViewModel(paymentMethodRepository)
        val restorationTester = StateRestorationTester(composeTestRule)

        restorationTester.setContent {
            MoneyManagerTheme {
                PaymentMethodsRoute(
                    onNavigateBack = {},
                    viewModel = viewModel,
                )
            }
        }

        val addMethodDesc = context.getString(R.string.action_add_payment_method)
        val renameTitleText = context.getString(R.string.action_rename_payment_method)
        val editDesc = context.getString(R.string.action_edit)
        val deleteDesc = context.getString(R.string.action_delete)
        val cancelText = context.getString(R.string.action_cancel)

        // 1. Open Add Dialog and type "Click", then emulate configuration change
        composeTestRule.onNodeWithText(addMethodDesc).performClick()
        composeTestRule.onNode(hasSetTextAction()).performTextReplacement("Click")
        restorationTester.emulateSavedInstanceStateRestore()

        // Add Dialog and its typed text "Click" remain displayed
        composeTestRule.onAllNodesWithText(addMethodDesc).assertCountEquals(2) // FAB label + dialog title
        composeTestRule.onNodeWithText("Click").assertIsDisplayed()
        composeTestRule.onNodeWithText(cancelText).performClick()

        // 2. Open Rename Dialog on "Transfer" (index 1) and type "Transfer Plus", then emulate configuration change
        composeTestRule.onAllNodesWithContentDescription(editDesc)[1].performClick()
        composeTestRule.onNode(hasSetTextAction()).performTextReplacement("Transfer Plus")
        restorationTester.emulateSavedInstanceStateRestore()

        // Rename Dialog and its typed text "Transfer Plus" remain displayed
        composeTestRule.onNodeWithText(renameTitleText).assertIsDisplayed()
        composeTestRule.onNodeWithText("Transfer Plus").assertIsDisplayed()
        composeTestRule.onNodeWithText(cancelText).performClick()

        // 3. Open Delete Confirmation Dialog on "Transfer" (index 1), then emulate configuration change
        composeTestRule.onAllNodesWithContentDescription(deleteDesc)[1].performClick()
        val confirmMsg = context.getString(R.string.delete_payment_method_confirm, "Transfer")
        composeTestRule.onNodeWithText(confirmMsg).assertIsDisplayed()
        restorationTester.emulateSavedInstanceStateRestore()

        // Delete Confirmation Dialog remains displayed
        composeTestRule.onNodeWithText(confirmMsg).assertIsDisplayed()
    }

    // =========================================================================
    // 3. TransactionFormScreen: chip tanlash va "+ Qo'shish" dialogi
    // =========================================================================
    @Test
    fun transactionFormScreen_chipSelection_andQuickAddDialog() {
        val viewModel = TransactionFormViewModel(
            savedStateHandle = SavedStateHandle(),
            transactionRepository = transactionRepository,
            currencyRepository = currencyRepository,
            categoryRepository = categoryRepository,
            paymentMethodRepository = paymentMethodRepository,
            rateRepository = rateRepository,
        )

        composeTestRule.setContent {
            MoneyManagerTheme {
                TransactionFormRoute(
                    onNavigateBack = {},
                    viewModel = viewModel,
                )
            }
        }

        val pmSectionLabel = context.getString(R.string.label_payment_method)
        val addChipLabel = "+ ${context.getString(R.string.action_add)}"
        val addDialogTitle = context.getString(R.string.action_add_payment_method)
        val saveText = context.getString(R.string.action_save)

        // 1. Payment method label and initial chips ("Cash" selected, "Transfer" not selected)
        composeTestRule.onNodeWithText(pmSectionLabel).performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Cash").performScrollTo().assertIsDisplayed().assertIsSelected()
        composeTestRule.onNodeWithText("Transfer").performScrollTo().assertIsDisplayed().assertIsNotSelected()

        // 2. Select "Transfer" chip
        composeTestRule.onNodeWithText("Transfer").performClick()
        composeTestRule.onNodeWithText("Transfer").assertIsSelected()
        composeTestRule.onNodeWithText("Cash").assertIsNotSelected()

        // 3. Click "+ Qo'shish" ("+ Add") chip -> dialog opens -> add "Uzcard" -> auto-selected
        composeTestRule.onNodeWithText(addChipLabel).performScrollTo().performClick()
        composeTestRule.onNodeWithText(addDialogTitle).assertIsDisplayed()

        // Enter "Uzcard" in the dialog's OutlinedTextField (last text field in hierarchy)
        val textFields = composeTestRule.onAllNodes(hasSetTextAction())
        val count = textFields.fetchSemanticsNodes().size
        textFields[count - 1].performTextReplacement("Uzcard")
        composeTestRule.onAllNodesWithText(saveText).let { nodes ->
            nodes[nodes.fetchSemanticsNodes().size - 1].performClick()
        }

        composeTestRule.onNodeWithText("Uzcard").performScrollTo().assertIsDisplayed().assertIsSelected()
        composeTestRule.onNodeWithText("Transfer").assertIsNotSelected()
    }

    // =========================================================================
    // 4. TransactionsScreen: ro'yxatdagi badge va filtr paneli
    // =========================================================================
    @Test
    fun transactionsScreen_listRowBadge_andFilterPanelByPaymentMethod() {
        runBlocking {
            val catFood = db.categoryDao().insert(CategoryEntity(name = "Ovqat", nameKey = "ovqat"))
            val catTaxi = db.categoryDao().insert(CategoryEntity(name = "Taksi", nameKey = "taksi"))

            val tx1 = db.transactionDao().insert(
                TransactionEntity(
                    type = TransactionType.EXPENSE,
                    amount = BigDecimal("45000"),
                    currencyId = 1L,
                    categoryId = catFood,
                    paymentMethodId = 1L, // Cash
                    date = LocalDate.now(),
                    createdAt = 1000L,
                )
            )
            db.transactionDao().insertSnapshot(listOf(TransactionRateEntity(tx1, 1L, BigDecimal.ONE)))

            val tx2 = db.transactionDao().insert(
                TransactionEntity(
                    type = TransactionType.EXPENSE,
                    amount = BigDecimal("30000"),
                    currencyId = 1L,
                    categoryId = catTaxi,
                    paymentMethodId = 2L, // Transfer
                    date = LocalDate.now(),
                    createdAt = 2000L,
                )
            )
            db.transactionDao().insertSnapshot(listOf(TransactionRateEntity(tx2, 1L, BigDecimal.ONE)))
        }

        val viewModel = TransactionsViewModel(
            transactionRepository = transactionRepository,
            currencyRepository = currencyRepository,
            categoryRepository = categoryRepository,
            paymentMethodRepository = paymentMethodRepository,
        )

        composeTestRule.setContent {
            MoneyManagerTheme {
                TransactionsRoute(
                    onNavigateToCreate = {},
                    onNavigateToEdit = {},
                    viewModel = viewModel,
                )
            }
        }

        composeTestRule.waitForIdle()

        // 1. Both categories ("Ovqat", "Taksi") and their payment method badges ("Cash", "Transfer") are displayed
        composeTestRule.onNodeWithText("Ovqat").assertIsDisplayed()
        composeTestRule.onNodeWithText("Taksi").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cash").assertIsDisplayed()
        composeTestRule.onNodeWithText("Transfer").assertIsDisplayed()

        // 2. Filter panel: click "Payment Method: All" chip -> select "Transfer"
        val filterPmLabel = context.getString(R.string.filter_payment_method)
        val filterAllLabel = context.getString(R.string.filter_all)
        val initialChipText = "$filterPmLabel: $filterAllLabel"
        val transferChipText = "$filterPmLabel: Transfer"

        composeTestRule.onNodeWithText(initialChipText).performScrollTo().assertIsDisplayed().performClick()

        // The chip opens the Payment/Currency sheet: pick "Transfer" (last node = inside the sheet), then Apply (1)
        val transferNodes = composeTestRule.onAllNodesWithText("Transfer")
        transferNodes[transferNodes.fetchSemanticsNodes().size - 1].performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.action_apply_count, 1)).performClick()
        composeTestRule.waitForIdle()

        // Filter chip now shows "Payment Method: Transfer" and only "Taksi" remains in the list
        composeTestRule.onNodeWithText(transferChipText).assertIsDisplayed().assertIsSelected()
        composeTestRule.onNodeWithText("Taksi").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ovqat").assertDoesNotExist()

        // 3. Reset filter via top bar Reset button
        val resetDesc = context.getString(R.string.action_reset)
        composeTestRule.onNodeWithContentDescription(resetDesc).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText(initialChipText).assertIsDisplayed()
        composeTestRule.onNodeWithText("Ovqat").assertIsDisplayed()
        composeTestRule.onNodeWithText("Taksi").assertIsDisplayed()
    }

    // =========================================================================
    // 4b. 50 belgili uzun nom: TransactionItemRow va PaymentMethodCard layout tekshiruvi
    // =========================================================================
    @Test
    fun longPaymentMethodName_50Chars_displaysCategoryAndBadgesWithoutOverflow() {
        val longName = "UzcardPlatinumCorporateBusinessAccountCardNumber50"
        assertTrue(longName.length == 50)

        runBlocking {
            // Rename default Cash (id = 1L, isDefault = true) to 50-char name
            paymentMethodRepository.rename(1L, longName)
            val catId = db.categoryDao().insert(CategoryEntity(name = "Supermarket", nameKey = "supermarket"))
            val txId = db.transactionDao().insert(
                TransactionEntity(
                    type = TransactionType.EXPENSE,
                    amount = BigDecimal("125000"),
                    currencyId = 1L,
                    categoryId = catId,
                    paymentMethodId = 1L,
                    date = LocalDate.now(),
                    createdAt = 1000L,
                )
            )
            db.transactionDao().insertSnapshot(listOf(TransactionRateEntity(txId, 1L, BigDecimal.ONE)))
        }

        val txViewModel = TransactionsViewModel(
            transactionRepository = transactionRepository,
            currencyRepository = currencyRepository,
            categoryRepository = categoryRepository,
            paymentMethodRepository = paymentMethodRepository,
        )
        val pmViewModel = PaymentMethodsViewModel(paymentMethodRepository)

        composeTestRule.setContent {
            MoneyManagerTheme {
                androidx.compose.foundation.layout.Column {
                    androidx.compose.foundation.layout.Box(modifier = androidx.compose.ui.Modifier.weight(1f)) {
                        TransactionsRoute(
                            onNavigateToCreate = {},
                            onNavigateToEdit = {},
                            viewModel = txViewModel,
                        )
                    }
                    androidx.compose.foundation.layout.Box(modifier = androidx.compose.ui.Modifier.weight(1f)) {
                        PaymentMethodsRoute(
                            onNavigateBack = {},
                            viewModel = pmViewModel,
                        )
                    }
                }
            }
        }

        val defaultBadgeText = context.getString(R.string.payment_method_default_badge)

        // 1. Category name "Supermarket" in TransactionItemRow is displayed and not crushed to 0 width
        val categoryNode = composeTestRule.onNodeWithText("Supermarket").assertIsDisplayed().fetchSemanticsNode()
        assertTrue("Kategoriya nomi kengligi > 0 bo'lishi kerak", categoryNode.boundsInRoot.width > 20f)

        // 2. 50-char payment method badge in TransactionItemRow and name in PaymentMethodCard are displayed
        val longNameNodes = composeTestRule.onAllNodesWithText(longName)
        assertTrue(longNameNodes.fetchSemanticsNodes().size >= 2)
        longNameNodes[0].assertIsDisplayed()
        longNameNodes[1].assertIsDisplayed()

        // 3. "Standart" (Default) badge in PaymentMethodCard is still displayed next to the 50-char name
        val defaultBadgeNode = composeTestRule.onNodeWithText(defaultBadgeText).assertIsDisplayed().fetchSemanticsNode()
        assertTrue("Standart badge kengligi > 0 bo'lishi kerak", defaultBadgeNode.boundsInRoot.width > 10f)
    }

    // =========================================================================
    // 5. String resurslari tekshiruvi (EN, UZ, RU — hardcoded matn yo'qligi)
    // =========================================================================
    @Test
    fun stringResources_allPaymentMethodStringsPresentAndLocalizedInEnUzRu() {
        val resIds = listOf(
            R.string.title_payment_methods,
            R.string.action_add_payment_method,
            R.string.action_rename_payment_method,
            R.string.label_payment_method,
            R.string.label_payment_method_name,
            R.string.delete_payment_method_confirm,
            R.string.empty_payment_methods,
            R.string.desc_payment_methods,
            R.string.filter_payment_method,
            R.string.payment_method_default_badge,
            R.string.err_payment_method_required,
            R.string.err_payment_method_too_long,
            R.string.err_payment_method_duplicate,
            R.string.err_payment_method_default_locked,
            R.string.err_payment_method_in_use,
            R.string.err_payment_method_not_found,
        )

        fun contextForLocale(tag: String): Context {
            val locale = Locale.forLanguageTag(tag)
            val config = Configuration(context.resources.configuration).apply {
                setLocale(locale)
            }
            return context.createConfigurationContext(config)
        }

        val enCtx = contextForLocale("en")
        val uzCtx = contextForLocale("uz")
        val ruCtx = contextForLocale("ru")

        for (id in resIds) {
            val arg: Any = if (id == R.string.err_payment_method_too_long) {
                5
            } else {
                "Test"
            }
            val en = enCtx.getString(id, arg)
            val uz = uzCtx.getString(id, arg)
            val ru = ruCtx.getString(id, arg)

            assertFalse("EN string bo'sh bo'lmasligi kerak: $id", en.isBlank())
            assertFalse("UZ string bo'sh bo'lmasligi kerak: $id", uz.isBlank())
            assertFalse("RU string bo'sh bo'lmasligi kerak: $id", ru.isBlank())

            // Verify translations are distinct across EN, UZ, RU
            assertNotEquals("EN va UZ tarjimalari farq qilishi kerak ($en)", en, uz)
            assertNotEquals("EN va RU tarjimalari farq qilishi kerak ($en)", en, ru)
            assertNotEquals("UZ va RU tarjimalari farq qilishi kerak ($uz)", uz, ru)
        }
    }
}
