# AUDIT.md — ish holati (har sessiya oxirida yangilanadi)

> Bu fayl — sessiyalar orasidagi **xotira**. Yangi chat shu faylni o'qib, qaysi task va qaysi blokka kelganimizni biladi.
> Qoida: faqat o'zing bajargan va tekshirgan narsani ✅ qil. Taxmin qilma.

**Belgilar:** ⬜ boshlanmagan · 🟨 jarayonda / qisman · ✅ tugagan va tekshirilgan · ➖ bu ekranga tegishli emas · ⚠️ muammo bor (Izohlar'ga yoz)

**Oxirgi yangilanish:** 2026-11-06 (T14 ✅ yopildi; (A),(B),(C) bajarildi: statik audit, uzun fayl/composable'lar bo'lindi, touch target, README, ishlatilmagan stringlar; (B) run-checks EXIT_CODE=0. Qo'lda tekshiruvlar kutilmoqda; T14 dan keyin: bar uslubi sozlamasi va glass blur qo'shildi, foydalanuvchi ekranda tasdiqladi)
**Keyingi task:** yo'q — hamma tasklar yopildi (T13 bekor qilingan). Foydalanuvchi hammasini qo'lda ko'rib chiqdi (2026-11-06). Domain refaktor (№25) foydalanuvchi qarori bilan qilinmaydi.

---

## 1. Tasklar holati

| Task | Nomi | Holat | Sessiya sanasi | Commit(lar) |
|---|---|---|---|---|
| T00 | Performance baseline | ✅ | 2026-11-05 | 19ce9ba (lifecycle collect), 85ff3f6 (baseline profile) |
| T01 | Design system + nav qobig'i | ✅ | 2026-11-05 | aaa29aa (A: theme ko'chirildi), eee3771 (B: komponentlar, MoneyColors, BottomBar) |
| T02 | Palitralar (5 light + 5 dark) | ✅ | 2026-11-05 | 5277823 (A: palitralar fayllarga bo'lindi), d079b98 (B: yangi palitralar, fallback, testlar) |
| T03 | Transactions ro'yxati | ✅ | 2026-11-05 | d5e70b5 (A: route/screen/components), d78339a (B: yangi dizayn) |
| T04 | Filtrlar va sana oralig'i | ✅ | 2026-11-05 | 1fc21e3 (A: chiplar fayllarga bo'lindi), 9d48f40 (B: filtr sheetlari) |
| T05 | Summary Report | ✅ | 2026-11-06 | a5a12bb (A: summary/ paketi), 2a750f7 (B: yangi dizayn) |
| T06 | New Transaction | ✅ | 2026-11-06 | 127e7e5 (A: route/screen/components), da3740a (B: yangi dizayn) |
| T07 | Edit Transaction | ✅ | 2026-11-06 | (B) shu commit |
| T08 | Settings | ✅ | 2026-11-06 | bde7d9c (A: route/screen/components), 56fcce2 (B: yangi dizayn) |
| T09 | Currencies | ✅ | 2026-11-06 | ebd6afc (A: route/screen/dialoglar), shu commit (B: yangi dizayn) |
| T10 | Exchange Rates | ✅ | 2026-11-06 | 76c0668 (A: route/screen/dialoglar), 61646f9 (B: yangi dizayn), (C) shu commit |
| T11 | Categories | ✅ | 2026-11-06 | 47bf00c (A: route/screen/card/dialoglar), 2cca7ff (B: yangi dizayn), (C) shu commit |
| T12 | Payment Methods | ✅ | 2026-11-06 | 67e4fb7 (A: route/screen/card/dialoglar), 91f4e0a (B: yangi dizayn), (C) shu commit |
| T13 | Palette ekrani (ixtiyoriy) | ➖ | 2026-11-06 | Foydalanuvchi qarori: kerak emas (T08 inline tanlagich yetarli) |
| T14 | Yakuniy audit | ✅ | 2026-11-06 | 22b49f3 (B: bo'lish, touch target, README, stringlar), (C) faeac03; qo'lda tekshiruvlar foydalanuvchi tomonidan o'tkazildi |

---

## 2. Ekran × blok matritsasi

Bloklar: **Split** = Route/Screen/komponentlarga ajratilgan (fayl ≤ ~300 qator) · **Domain** = UI domain modelga bog'langan, entity UI'da yo'q · **Light** = yangi dizayn (light) · **Dark** = dark tekshirilgan · **States** = loading/empty/error holatlari · **i18n** = uz/en/ru stringlar · **Perf** = lifecycle-aware collect, key/contentType, barqaror state · **Tests** = unit/UI testlar yashil

| Ekran (hozirgi qator soni) | Split | Domain | Light | Dark | States | i18n | Perf | Tests | Task |
|---|---|---|---|---|---|---|---|---|---|
| Transactions ro'yxati (Screen 118) | ✅ | ⬜ | ✅ | ✅ | ✅ | ✅ | ✅ | 🟨 | T03 |
| Filtr sheetlari | ✅ | ➖ | ✅ | ✅ | ➖ | ✅ | ✅ | ✅ | T04 |
| Summary Report — Details | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | T05 |
| Summary Report — Pie Chart | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | T05 |
| New Transaction (Screen 92 + 27 fayl; VM 286) | ✅ | ⬜ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | T06 |
| Edit Transaction | ✅ | ⬜ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | T07 |
| Settings (576 → 17 fayl, ≤95 qator) | ✅ | ➖ | ✅ | ✅ | ➖ | ✅ | ✅ | ⬜ | T08 |
| Currencies (332 → 12 fayl, ≤83 qator) | ✅ | ➖ | ✅ | ✅ | ➖ | ✅ | ✅ | ⬜ | T09 |
| Exchange Rates (644 → 15 fayl, ≤127 qator) | ✅ | ➖ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | T10 |
| Categories (258 → 12 fayl, ≤86 qator) | ✅ | ⬜ | ✅ | ✅ | ⬜ | ✅ | ✅ | ✅ | T11 |
| Payment Methods (353 → 10 fayl, ≤115 qator) | ✅ | ⬜ | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ | T12 |
| Palette ekrani (ixtiyoriy) | ➖ | ➖ | ⬜ | ⬜ | ➖ | ⬜ | ➖ | ➖ | T13 |

### Umumiy (cross-cutting)

| Blok | Holat | Izoh |
|---|---|---|
| Release/benchmark build o'lchovi | ✅ | T00: release APK yig'iladi (R8, debug imzo) ✅; debug vs release qotish qurilmada O'LCHANMAGAN |
| Baseline profile | ✅ | T00: qo'lda yozilgan wildcard profil + profileinstaller, release'da compileReleaseArtProfile o'tdi; ta'siri o'lchanmagan, Macrobenchmark bilan aniqlash mumkin |
| Design system komponentlari (`core/designsystem`) | ✅ | T01: 9 komponent + MoneyColors + BottomBar; build o'tdi; foydalanuvchi preview va testlarni tasdiqladi (2026-11-05) |
| Palitralar 5 light + 5 dark, AA kontrast | ✅ | T02: 5 palitra × light/dark; `PaletteContrastTest` yashil (foydalanuvchi run-checks.bat, 2026-11-05); qurilmada foydalanuvchi ko'rib chiqdi |
| Saqlangan palitra nomlari bilan orqaga moslik | ✅ | T02: `AppPalette.fromStored()` + `AppPaletteMigrationTest` yashil (foydalanuvchi tasdiqladi) |
| Domain model + repository interfeyslari (entity UI'dan chiqarildi) | ➖ | foydalanuvchi qarori (2026-11-06): alohida refaktor sifatida qoldirildi, hozir qilinmaydi (№25) |
| `README.md` struktura bo'limi yangilandi | ✅ | T14: `core/` + `feature/` strukturasi, ARCHITECTURE/AUDIT havolasi |
| Fayl ≤300 qator, composable ≤80 qator (statik skript) | ✅ | T14: hech biri oshmaydi (2026-11-06); `TransactionFormViewModel` 286, `MainAppContent` ~35 |
| Touch target ≥48dp | ✅ | T14: `MmChip` ✕ (18→48dp) va footer ko'z tugmasi (32→48dp); foydalanuvchi ekranda ko'rdi |
| Qo'lda tekshiruv: dark, 5 palitra, 3 til, shrift 12–24 sp | ✅ | T14: foydalanuvchi ekranda tekshirdi (2026-11-06) |
| Ishlatilmagan resurslar tozalandi | ✅ | T14: 6 string (3 tilda) o'chirildi (foydalanuvchi ruxsati bilan); `lang_*`/`font_size_glyph` ataylab `translatable=false` |

---

## 3. Hozirgi kod holati (boshlang'ich snapshot, 2026-11-05)

- Katta fayllar: `TransactionsScreen.kt` 1232, `TransactionFormScreen.kt` 945, `RatesScreen.kt` 644, `SettingsScreen.kt` 576, `TransactionFormViewModel.kt` 519.
- Repository'lar interfeyssiz; UI Room entity/row'larini ishlatadi.
- `TransactionsScreen` 7 ta `collectAsState()` (lifecycle-bilmas); `SettingsScreen` 4 ta `collectAsState()`.
- Boshqa ro'yxatlarda `LazyColumn` `key` bor; Transactions'da `itemKey` + `contentType` bor.
- `NavHost`: Transactions/Settings fade (90–120ms), qolganlari slide (180ms).
- Release'da R8 yoqilgan; baseline profile yo'q.
- Testlar: `DomainRulesTest`, `ViewModelsTest`, `ContentFontSizeTest`, `Migration2To3Test`, `PaymentMethodDaoAndRepositoryTest`, `PaymentMethodsComposeUiTest`.

---

## 4. Qarorlar jurnali (o'zgarmas, bekor qilish faqat foydalanuvchi ruxsati bilan)

- Pastki menyu: 2 tab. "+" FAB Transactions'da. "Total Summary" — Transactions'da pastki menyu tepasida.
- Ro'yxat qatori summani faqat o'z valyutasida ko'rsatadi (konvertatsiyasiz).
- Tavsif NOT NULL, UI'da ixtiyoriy (bo'sh = `""`).
- Ilova ko'p tilli; qator sarlavhasi = kategoriya nomi.
- Summary Report Pie: Balance — diagramma ostida 3-qator.
- Theme va Language tanlash — ikonka asosida, ixcham (Settings).
- 5 light + 5 dark palitra; Emerald saqlanadi, qolgan 4 tasi yangi fintech ranglari.
- Filtrlar bitta tanlovli (kategoriya, to'lov usuli, valyuta, tur); ko'p tanlov QILINMAYDI, `MonitoringFilter` va so'rovlar o'zgarmaydi (T04, foydalanuvchi tanlovi).
- Dizayndagi hamburger, profil avatari, "Screen State" preview, Sarmoya brendi, 5 tabli menyu — QILINMAYDI.

---

## 5. Ochiq masalalar / blokerlar

| # | Masala | Qaysi task | Holat |
|---|---|---|---|
| 1 | Kategoriya ikonkasi: DB'da maydon yo'q (nameKey xaritasi yoki yangi maydon) | T03 | hal qilindi: nameKey kalit so'zlari xaritasi (`CategoryIcon.kt`), DB o'zgarmadi; T11 da tanlanadigan ikonka kerak bo'lsa qayta ko'rish |
| 2 | "Export" (Summary) — keyingi versiya | T05 | kechiktirildi |
| 3 | "View Transactions" havolasi (kategoriya FK xatosida) | T11 | ixtiyoriy |
| 4 | T00: debug vs release qotishni qurilmada o'lchash va oldin/keyin raqamlarini yozish (Claude bu muhitda emulyator ishlata olmaydi) | T00 | foydalanuvchi o'lchaydi |
| 5 | Gradle JDK 17/21 kerak: tizim JDK 25 da build yiqiladi ("25.0.1"); Android Studio JBR ishlatish | T00 | ma'lumot |
| 6 | T03: Summary sheet/donut T05 da qayta yozildi (har composable ≤80 qator). `TransactionRow`, `TransactionList`, `TotalSummaryFooter` ~80 qatordan uzun bo'lib qolgan bo'lishi mumkin, T05 da tegilmagan | T05 | hal qilindi (T14: skript bo'yicha 80 qatordan uzun composable yo'q) |
| 7 | T03: ro'yxat qatori endi o'z valyutasida (AUDIT §4); kunlik sarlavhada balans o'rniga operatsiyalar soni. `TransactionListRow` ga `createdAt`, `categoryKey` qo'shildi (SELECT proyeksiyasi, sxema o'zgarmagan) | T03 | ma'lumot |
| 8 | T03: androidTest (`PaymentMethodsComposeUiTest`) qurilmada ishga tushirilmagan; qurilmada ishga tushirishni foydalanuvchi hal qiladi (light/dark, empty, skeleton ekranda ko'rildi, muammo yo'q) | T03 | yopildi (foydalanuvchi tekshirdi 2026-11-06) |
| 9 | T04: dizayndagi 'All (45)/Income (12)/Expense (33)' sonlari yo'q (tur bo'yicha son hisoblanmaydi, ortiqcha so'rov yozilmadi); chiplarda faqat tanlangan nom ko'rinadi | T04 | ma'lumot |
| 10 | T04: `PaymentMethodsComposeUiTest` filtr oqimi sheet'ga moslab yangilandi, lekin qurilmada ishga tushirilmagan; sheetlar va sana oralig'i ekranda ko'rildi, muammo yo'q | T04 | yopildi (foydalanuvchi tekshirdi 2026-11-06) |
| 11 | T05: Summary ekrani (Details va Pie, light/dark, Others bloki, empty donut) ekranda barcha holatlar foydalanuvchi tomonidan ko'rildi, muammo yo'q | T05 | hal qilindi (2026-11-06) |
| 12 | T05: `Totals` ga `incomeCount`/`expenseCount` qo'shildi (foydalanuvchi qarori: hisoblab qo'shish); SQL va sxema o'zgarmadi, summalar bir xil. Ulush 1 kasrgacha (`summaryShares`), yig'indisi 100.0. 'Net Profit/Net Loss' badge balans ishorasidan chiqadi | T05 | ma'lumot |
| 13 | T05: davr sarlavhasi, Export, Change Period QILINMADI (tasks.md bo'yicha; Export — keyingi versiya, №2) | T05 | ma'lumot |
| 14 | T06: build + unit testlar va ekranda ko'rish (light/dark, xato holatlari, dialoglar) foydalanuvchi tomonidan tasdiqlandi | T06 | hal qilindi (2026-11-06) |
| 15 | T06: `TransactionFormViewModel.kt` 481 qator (>300), `TransactionFormRoute` composable ~105 qator (>80); VM hali Room entity'larini (`CurrencyEntity`, `CategoryEntity`, `PaymentMethodEntity`) ishlatadi, shuning uchun Domain ⬜ | T06 | hal qilindi (T14: VM 286 qator, Route ≤80); Domain ⬜ qoladi (№25) |
| 16 | T06: foydalanuvchi qarori: kategoriya yozib kiritiladi (saqlashda yaratiladi), 'Create “X”' qatori mavjud bo'lmagan nom uchun chiqadi; kartaning ikkinchi qatori ('Daily Living…'), '42k UZS avg', preview tablari, summa oldidagi '−/+' belgisi QILINMADI. Tavsif hisoblagichi haqiqiy `MAX_LENGTH` (200) ni ko'rsatadi (dizaynda 120) | T06 | ma'lumot |
| 17 | T06: `categoryIcon` `core/designsystem/icon/` ga ko'chirildi (T11, importlar yangilandi) — hal qilindi. `PaymentMethodsComposeUiTest` `TransactionFormRoute` ga moslandi, qurilmada ishga tushirilmagan | T06 | ma'lumot |
| 18 | T07: foydalanuvchi qarori talab qilinmadi. `#id` va yaratilgan vaqt haqiqiy (`TransactionDetails.createdAt`, sxema o'zgarmadi); 'Linked to N records' mavjud `observeWithUsage()` dan; o'chirish `MmDialog` bilan tasdiqlanadi. Yangi unit test qo'shilmadi (delete oqimi uchun VM testi ochiq) | T07 | ochiq (T07 da build/unit testlar va ekran tasdiqlandi) |
| 19 | T08: (B) ekranda ko'rish foydalanuvchi tomonidan tasdiqlandi. Segment balandligi 40dp (<48dp). Yangi test qo'shilmadi. `font_size_reset` stringi endi ishlatilmaydi (o'chirilmadi). Dizayndagi 'Personal Account', 'System Dialogs Preview', 'Total Summary' QILINMADI (tasks.md bo'yicha) | T08 | ochiq |
| 20 | T09: tasks.md talabiga ko'ra default valyutada edit tugmasi UI'da o'chirilgan (avval nomni tahrirlash mumkin edi); repository qoidasi o'zgarmadi. Dizayndagi 'Active/M3 Rule Sheets' tabi va uydirma tavsiflar QILINMADI; 'Data integrity' banneri qo'shildi (matni `err_currency_in_use` bilan mos). Yangi test qo'shilmadi | T09 | ma'lumot |
| 21 | T10: foydalanuvchi qarori: kartada 'N transactions linked' QILINMADI (`ExchangeRateRow` da faqat `locked`; DAO tegilmadi). Dizayndagi 'Live CBU.uz / 09:30', 'Manual/Recent/Fixed' badge'lari, valyuta belgisi tile'lari QILINMADI. Import xatosi banner emas, snackbar + 'Retry' (`err_cbu_fetch_failed`); `MmDialog` da qizil 'Delete' tugmasi yo'q | T10 | ma'lumot |
| 22 | T10: `RatesRoute` composable ~100 qator (>80), `AddRateDialog` ~85; Add va Edit dialog alohida qoldi (tasks.md `AddEditRateDialog`). Ekranni foydalanuvchi ko'rib chiqdi (2026-11-06, muammo aytilmadi) va T10 ni ✅ ga o'tkazishni so'radi: Light/Dark/States shu asosda ✅. `RateFormatTest` yashil (4/4, build-check.log EXIT_CODE=0, 2026-11-06) → Tests ✅. Qolgan: `RatesRoute` ~100 qator (>80), `AddRateDialog` ~85 (Split 🟨) | T10 | hal qilindi (T14: Route ≤80, test yashil) |
| 23 | T11: dizayndagi tavsif qatori ('Household, supermarket…'), 'View Transactions' havolasi (№3 ixtiyoriy) va qizil FK banneri QILINMADI; FK xatosi snackbar'da `err_category_in_use` (delete qulf bilan o'chirilgan, amalda kam uchraydi). Qidiruv — nom bo'yicha 'contains' (`filterCategories`, DB so'rovi o'zgarmadi). Domain ⬜ (UI hali `CategoryWithUsage` Room modeli bilan ishlaydi). Light/Dark foydalanuvchi tomonidan ekranda ko'rildi, xato yo'q (2026-11-06) → ✅; States (bo'sh/qidiruv natijasiz) alohida aytilmagan → ⬜. `CategoryFilterTest` yozildi, run-checks 'build success', lekin test natijasi alohida aytilmagan (Tests 🟨). `category_tx_count` stringi endi ishlatilmaydi (o'chirilmadi). `RateFormatTest` yashil (4/4, build-check.log, 2026-11-06). Foydalanuvchi ruxsati bilan `PaletteContrastTest` (income/expense) tuzatildi: `IncomeLight` 0xFF2E7D32 → 0xFF2A782E (surfaceContainer ustida kontrast 4.39–4.42 → ≥4.7; Ocean Teal, Graphite Mint, Royal Indigo, Warm Coral light'da yiqilgan edi). Qayta ishga tushirish: `EXIT_CODE=0`, 53 test yashil (`RateFormatTest` 4/4, `CategoryFilterTest` 4/4, `PaletteContrastTest` 2/2), foydalanuvchi tasdiqladi (2026-11-06) | T11 | hal qilindi (2026-11-06) |
| 24 | T12: dizayndagi 'Debit/International' teglari, 'Configure Channels', add dialogidagi 'Set as default' katakchasi va qizil FK banneri QILINMADI (maydon/qoida yo'q; FK xatosi snackbar'da `err_payment_method_in_use`). Ishlatilgan usulda delete tugmasi bosiladi (xira) — xulq saqlandi. `MmDialog` ga `confirmEnabled` (default true) qo'shildi. `PaymentMethodsViewModel` endi `uiState` beradi (`ViewModelsTest` moslandi). `PaymentMethodsComposeUiTest` extended FAB va plural badge'ga moslandi, qurilmada ishga tushirilmagan (androidTest ochiq, №8/№10 kabi). Domain ⬜ (UI hali `PaymentMethodWithUsage` Room modeli bilan ishlaydi); ekran (light/dark, holatlar) foydalanuvchi tomonidan to'liq ko'rildi (2026-11-06) → States ✅. `payment_method_tx_count` stringi endi ishlatilmaydi (o'chirilmadi) | T12 | yopildi (foydalanuvchi tekshirdi 2026-11-06) |
| 25 | T14: (1) Domain bloki hamma ekranlarda ⬜ — UI hali Room modellarini (`CategoryWithUsage`, `PaymentMethodWithUsage`, `CurrencyEntity`...) ishlatadi; domain model + repository interfeyslari katta alohida refaktor, T14 doirasida QILINMADI, qaror kerak. (2) `ui/navigation/` ARCHITECTURE.md dagi `navigation/` o'rniga `ui/` ostida qoldi (ko'chirilmadi). (3) MmChip ✕ 48dp qilindi: chip balandligi biroz oshadi (foydalanuvchi ruxsati, ekranda ko'rish kerak) | T14 | yopildi: foydalanuvchi qarori bilan Domain refaktor qilinmaydi (kodda bajarilmagan) |
| 26 | T14 keyin: `ColorScheme.barContainer` (Theme.kt) endi ishlatilmaydi — o'lik kod, keyingi tozalashda o'chirish. Glass blur: `GlassBar.kt`/`MmScaffold.kt` (Compose 1.7 `GraphicsLayer`), Android 12+; Transactions/Rates tepasida qotib turuvchi qatorlar bor, shuning uchun AppBar u yerda ro'yxatni emas, fonni xiralashtiradi; forma o'zgarmadi | T14+ | hal qilindi (`barContainer`/`onBarContainer` o'chirildi) |

---

## 6. Sessiya jurnali (har sessiya oxirida YANGI QATOR qo'shiladi, eskisini o'chirma)

| Sana | Task | Nima qilindi | Qaysi bloklar ✅ bo'ldi | Qolgan / keyingi qadam | Commit |
|---|---|---|---|---|---|
| 2026-11-05 | reja | Dizayn tahlil qilindi; ARCHITECTURE.md, tasks.md, AUDIT.md, AI_PROMPT.md yozildi | — | T00 dan boshlash | — |
| 2026-11-05 | T00 | collectAsState -> collectAsStateWithLifecycle (Transactions 7, Settings 4); profileinstaller + baseline-prof.txt; assembleDebug, testDebugUnitTest, assembleRelease yashil (JBR JDK 21) | — (T00 qisman, bloklar ✅ emas) | qurilmada debug/release o'lchovi; Transactions UiState (T03); keyin T01 | 19ce9ba, 85ff3f6 |
| 2026-11-05 | T01 | (A) theme -> core/designsystem/theme; (B) MmTopBar, MmCard, MmFilterChip, MmBadge, MmFab, MmEmptyState, MmSectionHeader, MmDialog, AmountText, MoneyColors, BottomBar (pill indikator, surfaceContainer fon); i18n: yangi matn yo'q (action_back/cancel 3 tilda bor) | — (T01 qisman: preview va testlar tasdiqlanmagan) | @Preview'larni Android Studio'da ko'rish; testDebugUnitTest; keyin T02 | aaa29aa, eee3771 |
| 2026-11-05 | T02 | (A) Palettes.kt 363→25 qator, palitra boshiga alohida fayl; (B) AppPalette → EMERALD/OCEAN_TEAL/ROYAL_INDIGO/WARM_CORAL/GRAPHITE_MINT, 4 yangi light+dark sxema, Emerald dark yashil tusli fon, eski saqlangan nomlar uchun fallback, 3 tilda nomlar, kontrast va migratsiya testlari | T02 cross-cutting bloklar (palitralar, orqaga moslik) ✅ — build, testlar va ko'rishni foydalanuvchi tasdiqladi | keyin T03 | 5277823 (A), d079b98 (B) |
| 2026-11-05 | T03 | (A) 1232 qatorli ekran → `feature/transactions/` (Route, stateless Screen, UiState/Event, component/dialog/sheet), FormatUtils → `core/format`; (B) yangi ro'yxat dizayni: MmTopBar/MmFab/MmCard qatorlar, ikonka xaritasi, sana pill + operatsiyalar soni, Total Summary pill, skeleton + empty, chiplarda (x); `operations_count` plural 3 tilda | Split, i18n, Perf ✅ (kod bo'yicha tekshirildi); build + unit testlar foydalanuvchi tomonidan yashil | ekranda light/dark, empty/skeleton ko'rish; androidTest; keyin T04 | d5e70b5 (A), d78339a (B) |
| 2026-11-05 | T04 | (A) filtr chiplari bir fayl-bir komponent, tizim DatePickerDialog `sheet/` ga; (B) `CategoryFilterSheet`, `PaymentCurrencySheet`, `TypeFilterSheet`, `DateRangeSheet` (M3 DateRangePicker + presetlar), `FilterSheetHost`, `DateRangePreset`; chiplar sheet ochadi, (x) bilan tozalanadi; 8 yangi string 3 tilda; androidTest filtr oqimi yangilandi; `DateRangePresetTest` | Split, i18n, Perf ✅ (kod bo'yicha); (A) build foydalanuvchi tomonidan yashil | (B) build/test natijasi, ekranda ko'rish (light/dark), androidTest; keyin T05 | 1fc21e3 (A), 9d48f40 (B), c60b67e (test, audit) |
| 2026-11-06 | T05 | (A) summary fayllari `sheet/summary/` paketiga, `LegendRow` alohida fayl; (B) Details: Total + base badge, Income/Expense kartalari (N items), Balance (Net) kartasi, Others guruhi + izoh; Pie: donut (markazda matn yo'q, yengil animatsiya), legend (ulush %, Balance 3-qator), Others bloki; yangi tab pill'lari; `Totals.incomeCount/expenseCount`; 9 string 3 tilda; `SummarySharesTest`, DomainRulesTest kengaytirildi | Split, Domain, Light, Dark, States, i18n, Perf, Tests ✅ (build, unit testlar va ekranda ko'rish foydalanuvchi tomonidan tasdiqlandi) | keyin T06 | a5a12bb (A), 2a750f7 (B) |
| 2026-11-06 | T06 | (A) 945 qatorli forma `feature/transactionform/` ga: presentation (VM, FormUiState, FormEvent, AmountInput) + ui (Route, stateless Screen, Content, Callbacks) + 17 komponent/dialog fayli; (B) yangi dizayn: Income\|Expense pill, summa kartasi + valyuta chiplari, kategoriya (ikonka, takliflar kartasi, Create qatori), sana chiplari, tavsif hisoblagichi, sticky 'Save Transaction' + xatolar soni, MmTopBar/MmDialog; 12 string 3 tilda | Light, Dark, States, i18n, Perf, Tests ✅ (build, unit testlar va ekranda ko'rish foydalanuvchi tomonidan tasdiqlandi); Split 🟨 (VM 481) | VM bo'lish (№15); keyin T07 | 127e7e5 (A), da3740a (B) |
| 2026-11-06 | T07 | (A) kerak bo'lmadi (T06 bajargan); (B) tahrirlash rejimi: `TransactionInfoCard` (`ID #`, yaratilgan sana/vaqt, trash), 'Save Changes', 'Delete this transaction' + `DeleteTransactionConfirmDialog`, `FormEvent.Deleted`, VM `delete()`, kategoriya yorlig'ida 'N ta yozuvga bog'langan' (plural); 5 string 3 tilda | i18n ✅ (3 tilda 5 tadan kalit, kod bo'yicha); Light, Dark, States, Perf, Tests ✅ (build, unit testlar va ekranda ko'rish foydalanuvchi tomonidan tasdiqlandi); Split 🟨 (VM 514) | VM bo'lish (№15); keyin T08 | shu commit |
| 2026-11-06 | T08 | (A) 576 qatorli ekran `feature/settings/` ga: presentation (VM, `SettingsUiState`) + ui (Route, stateless Screen, Callbacks) + komponentlar; (B) Theme (☀/🌙/auto) va Language (EN/UZ/RU) — bir qatorli segmentlar, dialoglarsiz; palette 5 doira (nomsiz, a11y bilan), font size: badge + namuna + Reset + A…A slayder; Data Management kartalari, `MmTopBar`; 1 string 3 tilda + 4 tarjimasiz | Split, i18n, Perf ✅ (kod bo'yicha); (A) build foydalanuvchi tomonidan yashil; Light, Dark ✅ (ekranda ko'rish foydalanuvchi tomonidan tasdiqlandi) | keyin T09 | bde7d9c (A), 56fcce2 (B) |
| 2026-11-06 | T09 | (A) 332 qatorli ekran `feature/currencies/` ga: presentation (VM, `CurrenciesUiState`, `CurrencyEvent`) + ui (Route, stateless Screen) + komponentlar/dialoglar; (B) kartalar (kod tile, nom, Default badge, N ta operatsiya chipi, qulf izohi, edit/delete holatlari), integrity banner, `MmTopBar`, `MmExtendedFab`, `MmDialog`; 4 string (1 plural) 3 tilda | Split, Light, Dark, i18n, Perf ✅ (build va ekranda ko'rish foydalanuvchi tomonidan tasdiqlandi); Tests ⬜ | keyin T10 | ebd6afc (A), shu commit (B) |
| 2026-11-06 | T10 | (A) 644 qatorli ekran `feature/rates/` ga: presentation (VM, `RatesUiState`, `RateEvent`) + ui (Route, stateless Screen) + komponentlar/dialoglar; (B) sana navigatori (‹ › Today), 'Import CBU' tugmasi, 'Registered Rates · N pairs', kartalar (teskari qator, Locked badge), `MmTopBar`, `MmExtendedFab`, `MmDialog`, jonli teskari kurs izohi, bo'sh holat + 'Fetch from CBU.uz', xatoda Retry; 10 string 3 tilda; (C) `RateFormatTest`, 3 til kalitlari tekshirildi | i18n ✅ (skript: 3 tilda barcha yangi kalitlar bor); Perf ✅ (kod bo'yicha: lifecycle collect, key/contentType); (A),(B) build foydalanuvchi tomonidan success | `RateFormatTest` natijasi (№22); keyin T11 | 76c0668 (A), 61646f9 (B), shu commit (C) |
| 2026-11-06 | T11 | (A) 258 qatorli ekran `feature/categories/` ga: presentation (VM, `CategoriesUiState`, `CategoryEvent`) + ui (Route, stateless Screen, `CategoryCard`, Rename/Delete dialoglar); (B) izoh banneri (+ tugmasiz), qidiruv, 'Saqlangan kategoriyalar' + soni, kartalar (ikonka, tranzaksiyalar soni, Unused badge, edit, lock/delete), `MmDialog`, bo'sh holat; `categoryIcon` → `core/designsystem/icon/`; 8 kalit 3 tilda; (C) `CategoryFilterTest`, 3 til kalitlari tekshirildi | Split, i18n ✅ (skript), Perf ✅ (kod bo'yicha: lifecycle collect, key/contentType); (A),(B) build foydalanuvchi tomonidan success | ekranda light/dark/states ko'rish, `CategoryFilterTest` natijasi, №22 (`RateFormatTest`); keyin T12 | 47bf00c (A), 2cca7ff (B), shu commit (C) |
| 2026-11-06 | T12 | (A) 353 qatorli ekran `feature/paymentmethods/` ga: presentation (VM, `PaymentMethodsUiState`, `PaymentMethodEvent`) + ui (Route, stateless Screen, `PaymentMethodCard`, Add/Rename/Delete dialoglar); (B) izoh kartasi, 'Faol usullar' + soni, kartalar (ikonka, Default/Protected/Unused badge, tranzaksiyalar soni, edit/delete), `MmTopBar`, `MmExtendedFab`, `MmDialog` (+`confirmEnabled`), bo'sh holat; 9 kalit (6 string, 3 plural) 3 tilda; (C) kalitlar tekshirildi | Split, i18n ✅ (skript), Perf ✅ (kod bo'yicha: lifecycle collect, key/contentType), Tests ✅ (run-checks EXIT_CODE=0, ViewModelsTest), Light, Dark, States ✅ (foydalanuvchi hamma holatni ko'rdi) | androidTest qurilmada, T13 (ixtiyoriy) yoki T14 | 67e4fb7 (A), 91f4e0a (B), shu commit (C) |
| 2026-11-06 | T14 | (A) statik audit: fayl/composable o'lchami, hard-coded rang/matn, tarjimalar, ishlatilmagan stringlar, a11y; (B) `TransactionFormViewModel` 514→286 (7 yangi fayl), `AppNavHost`, `TransactionFormDialogs`, `RateEventsEffect`, `TransactionsContent`, touch target 48dp, 6 string o'chirildi, README yangilandi; (C) AUDIT yakunlandi | Split (T06, T07, T10) ✅, README ✅, fayl/composable o'lchami ✅ (statik); (B) run-checks EXIT_CODE=0 | qo'lda: dark/5 palitra/3 til/shrift, release o'lchash (№4), androidTest'lar, Domain (№25) | 22b49f3 (B), shu commit (C) |
| 2026-11-06 | T14+ | Bar uslubi: `BarStyle` (TINTED/GLASS) sozlamasi (DataStore, Settings qatori, 3 tilda 3 kalit); AppBar va pastki navbar palitra tusida; navbar atrofidagi ikki marta inset bo'shlig'i tuzatildi; `MmScaffold` + `GlassBar` (kontent bar ostidan o'tadi, blur) | build EXIT_CODE=0 (ikki bosqich); Glass ekranda foydalanuvchi tomonidan tasdiqlandi | №26, №25 | 2c55e40 (sozlama), 49c52ff (blur), shu commit |
