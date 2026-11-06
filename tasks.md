# tasks.md — UI/UX yangilash rejasi (Stitch dizayni → Compose)

> Har bir task = **alohida yangi chat/sessiya**. Boshlashdan oldin: `AI_PROMPT.md` dagi promptni yubor, so'ng `Task Txx` ni ayt.
> Holat `AUDIT.md`da yuritiladi. Dizayn rasmlari: `docs/design/`. Arxitektura: `ARCHITECTURE.md`.

## Umumiy qoidalar (barcha tasklar uchun)

1. Task ichida 2-3 qadam bor: **(A) refaktor** (xulq/ko'rinish o'zgarmaydi, faqat fayllarni qatlam/komponentga ajratish) → **(B) dizayn** → **(C) test + audit**. A va B alohida commit.
2. Stitch rasmlari — **vizual manba**, funksional manba emas. Rasmdagi har narsa ilovada bo'lishi shart emas (pastdagi "Dizaynda bor, lekin QILINMAYDI" ro'yxatlariga qara).
3. Qaror (o'zgarmaydi): ilovada **2 ta pastki tab** (Transactions, Settings). "+" FAB Transactions ekranida (T17 da olib tashlandi: "Add transaction" AppBar'dagi ⋮ menyuda). "Total Summary" footer faqat Transactions ekranida, pastki menyu tepasida. Tranzaksiya qatori summani faqat o'z valyutasida ko'rsatadi. Tavsif ixtiyoriy (bo'sh = `""`). Kategoriya tranzaksiya formasidan yaratiladi.
4. Ilova 3 tilli (uz/en/ru): yangi matnlar uchala `strings.xml`ga qo'shiladi.
5. Dizaynda ko'rinadigan barcha **aniq qiymatlar** (hex, o'lcham) rasmdan taxminiy; asosiy manba — Material 3 token va `ARCHITECTURE.md`.
6. Dizaynda uchraydigan, lekin ilovaga TEGISHLI BO'LMAGAN umumiy elementlar (barcha tasklarda QILINMAYDI): yuqoridagi **hamburger menyu**, **profil avatari**, **"Screen State: List/Empty/Skeleton"** preview tabi, **"M3 Rule Sheets/Frame A-E/State Specification"** deb nomlangan izoh bloklari, **"Sarmoya"** brendi, **5 tabli pastki menyu**, Settings'dagi **"Personal Account / Syncing"** kartasi.

## Tartib

| # | Task | Dizayn rasmi | Bog'liq |
|---|---|---|---|
| T00 | Performance baseline va render muammosi | — | — |
| T01 | Design system asos + navigatsiya qobig'i | 01, 14 | T00 |
| T02 | Palitralar (5 light + 5 dark) | 09, 14 | T01 |
| T03 | Transactions ro'yxati | 01 | T01 |
| T04 | Filtrlar va sana oralig'i | 04 | T03 |
| T05 | Summary Report (Details + Pie Chart) | 02, 03 | T03 |
| T06 | New Transaction forma | 05, 06 | T01 |
| T07 | Edit Transaction + o'chirish | 07 | T06 |
| T08 | Settings (ikonli Theme/Language) | 08 | T01, T02 |
| T09 | Currencies | 10 | T01 |
| T10 | Exchange Rates | 11 | T01 |
| T11 | Categories | 12 | T01 |
| T12 | Payment Methods | 13 | T01 |
| T13 | *(ixtiyoriy)* Palette tanlash ekrani | 09 | T02, T08 |
| T14 | Yakuniy audit (dark, a11y, perf, README) | hammasi | hammasi |

T09–T12 bir-biridan mustaqil (T01 dan keyin istalgan tartibda).

---

## T00 — Performance baseline va "qotib ishlash" muammosi

**Maqsad:** UI qotishi sababini o'lchab topish va tuzatish. Hozircha dizaynga TEGMA.

**Gipotezalar (avval tekshir, keyin tuzat):**
- Emulyatorda **debug** build o'lchanayotgan bo'lishi (debug Compose sezilarli sekin). Release/benchmark bilan solishtir.
- `TransactionsScreen` 7 ta `collectAsState()` (lifecycle-bilmas) + 1232 qatorli bitta composable → har o'zgarishda butun ekran recomposition.
- `SettingsScreen` ham `collectAsState()`.
- Baseline profile yo'q; `material-icons-extended` (R8 siz katta).
- `NavHost` almashuvlarida fade+slide animatsiyalar (tween 90–180ms) — ahamiyati kichik, lekin o'lcha.

**Qadamlar:**
1. Release-ga o'xshash build (`release` yoki `benchmark` buildType, debug imzo bilan) tayyorla. Qotishni debug vs release'da solishtir; natijani `AUDIT.md`ga yoz.
2. Layout Inspector / Compose recomposition counts yoki `Modifier.recomposeHighlighter`-ga o'xshash vaqtinchalik log bilan Transactions, Settings, Form'ni tekshir.
3. Aniqlangan sabablarni tuzat (faqat ko'rinishni o'zgartirmaydigan): `collectAsStateWithLifecycle`, state'ni bitta `UiState`ga yig'ish (Transactions uchun T03 da to'liq; bu yerda minimal), `remember`/`derivedStateOf`, barqaror lambda, ro'yxat `key/contentType` tekshiruvi, Canvas obyektlarini `remember`.
4. Baseline Profile (yoki kamida `profileinstaller` + `baseline-prof.txt`) qo'sh.
5. Natijani raqam bilan yoz: oldin/keyin (masalan, ekranlararo o'tish, scroll jank).

**Qabul mezoni:** release buildda ekranlararo o'tish va ro'yxat scroll'i silliq; sabab(lar) `AUDIT.md`da yozilgan; testlar yashil.
**Fayllar:** `MainActivity.kt`, `ui/transactions/*`, `ui/settings/*`, `app/build.gradle.kts`.

---

## T01 — Design system asos + navigatsiya qobig'i

**Dizayn:** 01 (top bar, pastki menyu, FAB), 14 (dark).
**Qadamlar:**
- (A) `core/designsystem/` yarat: `theme/` (mavjud `Theme.kt`, `Palettes.kt` ko'chiriladi, xulq o'zgarmaydi) va `component/`.
- (B) Umumiy komponentlar (har biri alohida fayl + `@Preview`): `MmTopBar` (markazlangan sarlavha, orqaga o'q), `MmCard`, `MmChip`/`MmFilterChip`, `MmBadge`, `MmFab`, `MmEmptyState`, `MmSectionHeader`, `MmDialog` (28dp burchak), `AmountText` (kirim yashil / chiqim qizil, tabular raqam, "1 250 000 UZS").
- (B) `BottomBar.kt`: 2 tab, tanlanganda pill indikator (rasm 01).
- Semantik ranglar: income/expense palitradan mustaqil (`ExtendedColors` yoki `LocalMoneyColors`).
**Qabul mezoni:** komponentlar preview'da light+dark ko'rinadi; mavjud ekranlar buzilmagan.
**Dizaynda bor, QILINMAYDI:** umumiy ro'yxat (yuqorida).

---

## T02 — Palitralar (5 light + 5 dark)

**Dizayn:** 09 (light palitralar), 14 (dark).
**Palitralar:** Emerald (o'zgarmaydi, primary ≈ `#006C4C`), Ocean Teal (≈ `#006874`), Royal Indigo (≈ `#3F51B5`), Warm Amber/Coral (≈ `#984061`), Graphite Mint (≈ `#2E6A58`). Aniq hex'larni 09-rasmdan tekshir (rasmdagi "Primary/Container/Secondary/Surface" qiymatlari).
**Qadamlar:**
- `AppPalette` enum nomlarini yangilash kerak bo'lsa (OCEAN→OCEAN_TEAL, ...) DataStore'dagi saqlangan qiymat bilan **orqaga moslik** (migration/fallback) qil.
- Har palitra uchun `lightColorScheme` + `darkColorScheme`; AA kontrast (≥ 4.5:1) tekshiruvi (unit test yoki skript).
- Dark: Emerald dark fon ≈ yashil tusli juda to'q (rasm 14), primary och mint.
- `strings.xml`: palitra nomlari 3 tilda.
**Qabul mezoni:** Settings'da 5 palitra almashadi, har biri light/dark da o'qiladi; income/expense ranglari barcha palitralarda aniq ajraladi.

---

## T03 — Transactions ro'yxati

**Dizayn:** 01.
**Hozirgi fayl:** `ui/transactions/TransactionsScreen.kt` (1232 qator).
**(A) Refaktor** → `feature/transactions/`: `TransactionsRoute`, `TransactionsScreen` (stateless), `TransactionsUiState` (bitta state: filter, items(paging), totals, dailyTotals, ...), komponentlar: `TransactionRow`, `DateHeader`, `TransactionFilterBar`, `TotalSummaryFooter`; sheet/dialog'lar alohida fayllarda (T04, T05 uchun joy qoldir, hozircha mavjud kodni shunday ko'chir).
**(B) Dizayn:**
- Top bar: markazlangan "Transactions".
- Filtr chiplar qatori (gorizontal scroll), tanlangan chipda (x).
- Sana sarlavhasi: pill ko'rinishida sana + "N operations" (kunlik soni; `dailyTotals` bor).
- Qator: yumaloq ikonka, sarlavha = kategoriya nomi, ikkinchi qator = tavsif · to'lov usuli, o'ngda summa (o'z valyutasida, yashil "+" / qizil "−"), vaqt (`createdAt`dan). Income uchun kichik "Income" badge (rasm 01).
- FAB "+" (create), uning ostida "Total Summary" footer (ko'z ikonka, chevron) — pastki menyu ustida.
- Empty va Skeleton (loading) holatlari: `MmEmptyState` va shimmer/skeleton qatorlar.
**Dizaynda bor, QILINMAYDI:** "Screen State" tabi; kategoriya uchun maxsus ikonka to'plami — hozir `Category`da ikonka maydoni yo'q. Variant: kategoriya `nameKey` bo'yicha kichik ikonka xaritasi (default ikonka bilan). Ma'lumotlar bazasiga yangi maydon QO'SHMA (alohida qaror kerak).
**Qabul mezoni:** `TransactionsScreen.kt` ≤ 300 qator; Paging, filtr, jami summa xulqi o'zgarmagan; scroll silliq; ViewModel testlari yashil.

---

## T04 — Filtrlar va sana oralig'i

**Dizayn:** 04 (to'rt holat: Category, Payment+Currency, Type, Date Range).
**Qadamlar:**
- (A) Filtr UI'ni `feature/transactions/ui/sheet/` ga ajrat.
- (B) `CategoryFilterSheet` (qidiruv, ikonli checkbox ro'yxat, Reset/Apply (N)), `PaymentCurrencySheet` (chip/checkbox panellar), `TypeFilterSheet` (All(45)/Income(12)/Expense(33) segment + radio), `DateRangeSheet` (M3 date range picker + preset chiplar: This Month, Last 30 Days, ... va Cancel/Save).
- Filtr chiplari holati: tanlanganda to'ldirilgan, soni ko'rsatiladi.
**Dizaynda bor, QILINMAYDI:** "Overview" tabli yagona "Filters & Date Range" ekrani (ilovada filtr chiplar orqali alohida sheetlar ochiladi); "Active Criteria" bloki — ixtiyoriy (agar sodda bo'lsa chiplar qatorida ko'rsatiladi).
**Eslatma:** hisoblangan soni (45/12/33) mavjud filtr bilan mos kelsa ko'rsat, ortiqcha so'rov yozma.
**Qabul mezoni:** barcha filtr kombinatsiyalari avvalgidek ishlaydi; Reset hammasini tozalaydi.

---

## T05 — Summary Report (Details + Pie Chart)

**Dizayn:** 02 (Details), 03 (Pie Chart).
**Qadamlar:**
- (A) `SummaryReportBottomSheet`, `SummaryRowItem`, `LegendRow`, `SummaryDonutChartView` → `sheet/summary/` alohida fayllar.
- (B) Details: "Total · 42 transactions" + "USD Base" badge, izoh "Converted to USD based on exchange rates"; Income / Expense kartalari (soni "28 items"), **Balance (Net)** katta karta; "Others · 3 transactions" fallback guruhi (izoh bilan, UZS).
- (B) Pie: donut (markazda matn yo'q), pastda legend: Income (% ulush), Expense (% ulush), **Balance uchinchi qator** (donut ichida emas); Others bloki.
- Donut Canvas'i `remember` bilan, animatsiya yengil.
**Dizaynda bor, QILINMAYDI:** "Export" va "Change Period" tugmalari, "May 1 – May 31, 2024" davr sarlavhasi (period filtr ekranidan keladi; xohlasangiz hozirgi filtr oralig'ini subtitle sifatida ko'rsat). Export — keyingi versiya.
**Qabul mezoni:** raqamlar avvalgi `MonitoringTotals` hisobi bilan bir xil; Balance manfiy bo'lsa qizil.

---

## T06 — New Transaction forma

**Dizayn:** 05 (to'ldirilgan forma), 06 (validatsiya).
**Hozirgi fayl:** `TransactionFormScreen.kt` (945 qator) + `TransactionFormViewModel.kt` (519).
**(A) Refaktor** → `feature/transactionform/`: Route/Screen/UiState; komponentlar: `TypeSelector`, `AmountCard` (+ valyuta chiplar), `CategoryPicker` (tanlangan karta, suggestions, "Create new category"), `PaymentMethodChips`, `DateSection`, `DescriptionField`, `SaveBar`; dialoglar alohida (`MissingRatesDialog`, `NoInternetDialog`, `DiscardDialog`, ...).
**(B) Dizayn:** tartib rasm 05: Income|Expense (tanlanganda yashil/qizil), katta summa kiritish + valyuta chiplar, kategoriya, to'lov usuli chiplari, sana (Today/Yesterday/sana chipi + kelajak bloklangan), tavsif (120 belgi hisoblagich), pastda sticky "Save Transaction".
**(B) Validatsiya (rasm 06):** maydon ostida xato matnlari (mavjud `err_*` stringlar), xato maydonlar qizg'ish fon; Save tugmasi ostida xatolar soni haqida matn (ixtiyoriy).
**Dizaynda bor, QILINMAYDI:** "Active Form / Empty State Preview", "Form Errors / CBU Rate / Offline / Discard" preview tablari (dialoglar mavjud M3 `AlertDialog` ko'rinishida qoladi, faqat T01 `MmDialog` uslubiga o'tadi); suggestionlardagi "42k UZS avg" (hisoblanmaydi); "Linked to N records".
**Qabul mezoni:** saqlash, kurs yo'q dialogi, offline dialogi, discard dialogi avvalgidek; `AmountInput` qoidalari (2 kasr, >0) o'zgarmagan; `TransactionFormViewModel` testlari yashil.

---

## T07 — Edit Transaction + o'chirish

**Dizayn:** 07.
**Qadamlar:** T06 komponentlarini qayta ishlat (nusxa ko'chirma). Edit rejimida: yuqorida ma'lumot kartasi (`#id`, yaratilgan sana/vaqt) + trash ikonka; "Save Changes"; pastda "Delete this transaction" matnli tugma → tasdiqlash dialogi; kategoriya kartasida "Linked to N records" (agar tayyor so'rov bo'lsa; bo'lmasa chiqarma).
**QILINMAYDI:** "ID #TXN-84920" ko'rinishidagi uydirma format — faqat haqiqiy `id`; "Debit Entry", "Debit / Local" kabi qo'shimcha teglar.
**Qabul mezoni:** tahrirlash/o'chirish xulqi va kurs bo'yicha xatolar (`err_edit_rates_missing`) avvalgidek.

---

## T08 — Settings

**Dizayn:** 08. **Foydalanuvchi talabi:** Theme va Language tanlash **ikonka asosida, ixcham** bo'lsin (hozirgi qator + dialog juda ko'p joy egallaydi).
**Qadamlar:**
- (A) `SettingsScreen.kt` (576) → `SettingsRoute/Screen/UiState`, komponentlar: `SettingsSection`, `SettingsItem`, `ThemeSelector`, `PalettePicker`, `FontSizeSetting`, `LanguageSelector`.
- (B) **Theme:** bitta qatorda 3 ikonli segment (☀ Light / 🌙 Dark / auto System) — dialog yo'q.
- (B) **Language:** bitta qatorda ixcham tanlagich (EN / UZ / RU — kichik belgi yoki bayroqsiz matn chip'lar); dialog yo'q.
- (B) Palette: inline 5 doira + tanlanganda check (rasm 08). Font size: slider + "Sample: 1 250 000 UZS" + Reset + "16 sp" badge.
- (B) "Data Management" ro'yxati: Currencies, Exchange Rates, Categories (Info), Payment Methods — ikonka, sarlavha, 1 qatorli izoh, chevron.
**QILINMAYDI:** "Personal Account / Syncing" kartasi, "System Dialogs Preview", Settings'dagi "Total Summary" footer.
**Qabul mezoni:** til almashganda UI darhol yangilanadi (mavjud `LanguageStore`); theme/palette DataStore'da saqlanadi; kam vertikal joy.

---

## T09 — Currencies
**Dizayn:** 10. **Hozirgi:** `CurrenciesScreen.kt` (332).
- (A) Route/Screen/komponent (`CurrencyCard`, `CurrencyDialogs`).
- (B) Kartalar: kod "tile", nom, "Default (National)" badge, "N transactions"; default valyutada qulf, edit/delete o'chirilgan; qolganlarida edit/delete; ishlatilmagan valyutada delete faol (qizil). FAB "Add Currency". Tepada kichik "Data Integrity" izoh banneri (ixtiyoriy).
- QILINMAYDI: "Active (5) / M3 Rule Sheets" tabi, "Linked to 2 accounts" (hisoblar yo'q), "Exchange index currency"-ga o'xshash uydirma tavsiflar — faqat `currency.name`.
- Qabul: xatolar (`err_currency_*`) avvalgidek.

## T10 — Exchange Rates
**Dizayn:** 11. **Hozirgi:** `RatesScreen.kt` (644).
- (A) Route/Screen + `RateCard`, `AddEditRateDialog`, `RateDatePicker`, `ImportBar`.
- (B) Sana navigatori (‹ sana ›, "Today"), "Import CBU" tugmasi; ro'yxat sarlavhasi "Registered Rates · N pairs"; kartalar: "1 USD = 12 650.00 UZS" + teskari qator, qulf va "Locked by transaction" badge, "N transactions linked"; FAB "Add Rate"; dialog (base/quote/rate/date + reverse hint); snackbar (import natijasi); xato holat + Retry; o'chirish dialogi; bo'sh holat ("Fetch from CBU.uz" tugmasi).
- QILINMAYDI: "Live CBU.uz / 09:30" vaqt belgisi (haqiqiy ma'lumot bo'lmasa), "Manual Rate / Recent / Fixed" badge'lari (maydon yo'q — faqat "Locked" qoladi).
- Qabul: kurs qulfi, inverse saqlash, CBU import avvalgidek.

## T11 — Categories
**Dizayn:** 12. **Hozirgi:** `CategoriesScreen.kt` (258).
- (A) Route/Screen + `CategoryCard`, dialoglar.
- (B) Izoh banneri ("kategoriyalar yangi tranzaksiya formasidan yaratiladi" — **+ tugmasi YO'Q**), qidiruv maydoni, kartalar (ikonka, nom, "N transactions", edit, lock/delete), rename dialogi, delete tasdiq, FK xatosi (`err_category_in_use`), bo'sh holat.
- QILINMAYDI: "View Transactions" havolasi — ixtiyoriy (filtrlangan Transactions'ga o'tish; keyin).

## T12 — Payment Methods
**Dizayn:** 13. **Hozirgi:** `PaymentMethodsScreen.kt` (353).
- (A) Route/Screen + `PaymentMethodCard`, dialoglar.
- (B) Sarlavha kartasi, kartalar ("Default" badge, "Protected" qulf, "N transactions"), FAB "Add Payment Method", add/rename/delete dialoglar, xato holatlari (`err_payment_method_*`), bo'sh holat.
- QILINMAYDI: "Debit / International / Unused" turi teglari (maydon yo'q), "Configure Channels" tugmasi.

## T13 — *(ixtiyoriy)* Palette tanlash ekrani
**Dizayn:** 09. Settings ichidagi inline tanlagich (T08) yetarli. Alohida ekran faqat har palitrani mini-preview bilan ko'rsatish uchun kerak: `Settings → Color Palette` ni bossa ochiladi. **Tavsiya:** T08 yetarli; bu taskni oxirida, vaqt qolsa.

## T14 — Yakuniy audit
- Barcha ekranlar light+dark, 5 palitra, 3 til (uzun ruscha matn sig'adimi), font size 12–24 sp.
- Accessibility: touch target ≥ 48dp, kontrast, `contentDescription`.
- Performance qayta o'lcha (T00 bilan solishtir).
- `README.md`ning loyiha strukturasi qismini yangilash; `AUDIT.md`ni yakunlash.
- Ishlatilmay qolgan eski kod/resurslarni tozalash (tasdiq bilan).

---

# Sprint 2 — yangi tasklar

> Qoidalar o'sha: har task = alohida sessiya, **(A) refaktor → (B) o'zgartirish → (C) test + audit**, A va B alohida commit. Build va testni foydalanuvchi o'zi ishga tushiradi (`run-checks.bat`); commit faqat "commit qil" deyilganda. Holat `AUDIT.md`da.
> Manba — foydalanuvchi aytgan muammo (Stitch dizayni yo'q). Misol rasmlar `docs/design/` ga qo'shiladi.

| # | Task | Rasm | Bog'liq |
|---|---|---|---|
| T15 | Tranzaksiya qatori: Income chipi olib tashlanadi, tavsif o'rniga to'lov usuli | — | — |
| T16 | Sahifalarning chap/o'ng chetidagi margin 50% ga kamayadi (token orqali) | — | — |
| T17 | Tranzaksiya filtrlarini bitta `TransactionFilterScreen` ga jamlash + "More" menyu | T17-filter-reference | — |
| T18 | Bar style bottom sheet + glass shaffofligi slayderi (0–100%) | — | — |
| T19 | Color Palette bottom sheet + `PaletteRegistry` | — | — |
| T20 | ~~Foydalanuvchi o'z palitrasini yaratadi~~ — BEKOR QILINDI (2026-11-06); o'rniga T19 da 10 ta tayyor enterprise palitra | — | — |

## T15 — Tranzaksiya qatori: chip olib tashlanadi, tavsif o'rniga to'lov usuli
**Muammo:** Transactions ro'yxatidagi har bir qatorda kategoriya nomi yonida "Income" chipi ko'rinadi, ikkinchi qatorda esa tavsif (va to'lov usuli) birga chiqadi. Chip olib tashlansin; ikkinchi qatorda tavsif o'rniga **faqat to'lov usuli** bo'lsin.
**Hozirgi:** `feature/transactions/ui/component/TransactionRow.kt` (`TransactionTitleLine` ichidagi `MmBadge(R.string.type_income)`; ikkinchi qator `listOf(description, paymentMethodName).joinToString(" · ")`), `TransactionSkeleton.kt` (qator geometriyasini takrorlaydi), `TransactionsUiMapper.kt`, `TransactionListItemUiModel.Item`.

**Qarorlar (foydalanuvchi, 2026-11-06):**
- Qatordagi Income/Expense chipi olib tashlanadi. Hozir kodda chip faqat **Income** uchun bor (Expense uchun yo'q), ikkalasi ham qatordan yo'qoladi.
- Ikkinchi qator = to'lov usuli nomi (`paymentMethodName`); tavsif ro'yxatda ko'rsatilmaydi.

**Bosqichlar:** (A) kerak emas — `TransactionRow` allaqachon alohida fayl (T07 dagi kabi).
- (B) `TransactionTitleLine` soddalashadi: faqat kategoriya nomi, `MmBadge` va `isIncome` parametri olib tashlanadi. Ikkinchi qator faqat `item.paymentMethodName`. Daromad/xarajatni ajratish qoladi: summa rangi (`AmountKind`) va kategoriya ikonkasi doirasi (income uchun yashil tus). Tavsif foydalanuvchiga Edit ekranida ko'rinadi, ma'lumot o'chmaydi. Foydalanilmay qolsa `Item.description` va mapper'dagi uzatish olib tashlanadi (`DomainRulesTest` dagi `Item(...)` chaqiruvlari moslanadi), `R.string.type_income` boshqa joyda (filtr) ishlatiladi — o'chirilmaydi. Skeleton'ning ikkinchi qator kengligi tekshiriladi (to'lov usuli nomi qisqaroq).
- (C) Testlar moslanadi, statik tekshiruv (`MmBadge`/`type_income` qatorda qolmagan), AUDIT.md.

**Ma'lumot/sxema:** `TransactionListRow.paymentMethodName` allaqachon bor — SQL, sxema va `MonitoringFilter` o'zgarmaydi.
**QILINMAYDI:** tavsifni boshqa joyda (masalan, ikkinchi qatorda qisqartirib) ko'rsatish; to'lov usuli ikonkasi/chipi; kunlik sarlavha va summa ustuni o'zgarishi.
**Qabul mezoni:** qatorda chip yo'q; ikkinchi qatorda to'lov usuli nomi (bir qator, uzun bo'lsa `…`); income/expense summa rangi va ikonka orqali baribir farqlanadi; light/dark, 3 til, font 12–24 sp; touch target va uzun-bosish (o'chirish) o'zgarmagan.

## T16 — Sahifa chetlaridagi (chap/o'ng) margin 50% ga kamayadi
**Muammo:** barcha sahifalarda chap va o'ng chetdagi bo'sh joy katta. 50% ga kamaytiriladi: **16dp → 8dp**.
**Muhim:** AppBar (`MmTopBar`) va pastki navbar (`BottomBar`) **o'z holicha qoladi** — faqat sahifa kontenti.
**Hozirgi:** umumiy token yo'q, 16dp qiymati har joyga qattiq yozilgan (`horizontal = 16.dp` ~20 joyda, 11 ta papkada: `categories`, `paymentmethods`, `rates`, `currencies`, `settings/component`, `transactionform`, `transactions/component`, `core/designsystem/component`). Shuning uchun to'g'ridan-to'g'ri almashtirish xavfli: sahifa chekkasi bilan **karta ichki padding'i** aralashib ketgan (masalan `MmCard`, `SettingsItem`).

**Qarorlar (foydalanuvchi, 2026-11-06):** sahifa chap/o'ng margini 50% ga kamaytirish; AppBar va bottom navbar o'zgarmaydi.
**Dastlabki shartlar (A boshlanishidan oldin tasdiqlanadi):**
- Faqat **sahifaning tashqi chekkasi** (ro'yxat/forma/karta bloklarining ekran chetidan masofasi) 16 → 8dp. **Karta ichidagi padding** (`MmCard`, qator ichki bo'shliqlari) o'zgarmaydi, aks holda matn kartaga yopishib qoladi.
- Bottom sheet va dialoglar "sahifa" emas — tegilmaydi.
- `TotalSummaryFooter` (Transactions'da bottom navbar tepasidagi blok) va `SaveBar` (forma) sahifa kontenti hisoblanadi → ularning chekkasi ham 8dp. Bottom navbar'ning o'zi o'zgarmaydi.

**Bosqichlar:**
- (A) Xulq o'zgarmaydi. Yagona token: `core/designsystem/theme/MmSpacing.kt` → `ScreenHorizontalPadding = 16.dp`. Statik audit: har ekranda tashqi margin va ichki padding ajratiladi (ro'yxat `contentPadding`, `Modifier.padding(horizontal=…)`, `padding(16.dp)` har tomonlama, `start/end` variantlari). Faqat tashqi margin'lar tokenga o'tkaziladi. Ro'yxat (hisobot sifatida AUDIT'ga) yoziladi: qaysi fayl o'tdi, qaysi biri ichki padding deb qoldi.
- (B) Token qiymati bitta o'zgarish bilan **8dp** ga tushiriladi. Ekranlar bo'yicha ko'zdan kechiriladi (Transactions ro'yxati va skeleton, filtr chiplari qatori, forma, Settings, Currencies, Rates, Categories, Payment Methods); qator va kartalar orasidagi vertikal ritm buzilmasligi tekshiriladi.
- (C) Statik skript: sahifa fayllarida tokenga o'tmagan `horizontal = 16.dp` qolmaganini tekshiradi; light/dark, 3 til, font 12–24 sp; AUDIT.md.

**QILINMAYDI:** AppBar va bottom navbar chekkalari; karta ichki padding; vertikal margin'lar; bottom sheet/dialog padding'i.
**Qabul mezoni:** hamma sahifada chap/o'ng chekka 8dp (avval 16dp); AppBar va bottom navbar bir xil; touch target ≥ 48dp saqlanadi; matn kesilmaydi (ayniqsa ruscha uzun matn va katta shrift); keyin yangi sahifa qo'shilsa shu tokendan foydalanadi.
**Ta'sir:** boshqa Sprint 2 tasklaridagi yangi sahifa elementlari (masalan, filtr chiplar qatori, "More" menyu) ham shu tokenni ishlatadi; T16 ularning oldin yoki keyin bajarilishidan qat'i nazar, token orqali ishlaydi.

## T17 — TransactionFilterScreen + "More" menyu
**Muammo:** Transactions sahifasida filtrlar 4 ta alohida sheetga (kategoriya, to'lov usuli + valyuta, tur, sana) va chiplar qatoriga bo'lingan; AppBar'da reset (`RestartAlt`) tugmasi, pastda "+" FAB. Filtrlash bir joyda, bitta ekranda bo'lsin.
**Misol:** `docs/design/T17-filter-reference.png` — **faqat tuzilma**: sarlavha + ✕, "Clear all", chiziq bilan ajratilgan bo'limlar, pastda yopishqoq "Show N Results" tugmasi.
**Hozirgi:** `TransactionFilterBar.kt` (o'chiriladi), `DisplayCurrencyChip.kt`, `FilterMenuChip.kt`, `FilterSheetHost.kt`, `FilterSheet.kt`, `CategoryFilterSheet`, `PaymentCurrencySheet`, `TypeFilterSheet`, `DateRangeSheet`, `TransactionsTopBar.kt`, `TransactionsScreen.kt` (`MmFab`).

**Qarorlar (foydalanuvchi, 2026-11-06):**
- `TransactionFilterScreen` — to'liq balandlikdagi bottom sheet; filtr state'i `TransactionsViewModel`da qoladi; yangi navigatsiya route YO'Q.
- "+" FAB olib tashlanadi. AppBar'dagi reset tugmasi o'rniga "More" (⋮) tugmasi: bosilganda bottom sheet ochiladi — **"Add transaction"** (yaratish ekraniga) va **"Filter"** (`TransactionFilterScreen`) amallari.
- Reset tugmasi yo'qoladi; uning vazifasini filtr ekranidagi **"Clear all"** oladi.
- **Chiplar qatori (`TransactionFilterBar`) Transactions sahifasidan butunlay olib tashlanadi** (joy egallaydi, yomon ko'rinadi); faol filtrlar ham sahifada chip bo'lib ko'rinmaydi — ular faqat `TransactionFilterScreen` da ko'rinadi. Display currency (ko'rish valyutasi) tanlash filtr ekranidagi alohida "Display currency" bo'limiga ko'chadi (foydalanuvchi qarori, 2026-11-06).

**Bosqichlar:**
- (A) Xulq o'zgarmaydi. `TransactionFilterDraft` (type, categoryId, paymentMethodId, currencyId, from, to) + sof funksiyalar (`toFilter`, `cleared`, `activeCount`) + unit test. 4 sheet ichidagi tanlash qismini sheet qobig'idan ajratib bo'lim composable'lariga chiqarish (`TypeSection`, `CategorySection`, `PaymentMethodSection`, `CurrencySection`, `DateRangeSection`); eski sheetlar ularni ishlatadi.
- (B) `TransactionFilterRoute/Screen` (sarlavha + ✕, "Clear all", bo'limlar, yopishqoq "Show N Results"); `MoreActionsSheet` (Add transaction / Filter); `TransactionsTopBar` da More tugmasi; `MmFab` olib tashlanadi; `TransactionFilterBar`, `DisplayCurrencyChip`, `FilterMenuChip`, eski 4 sheet, `FilterSheetHost`, `FilterSheet` enum o'chiriladi (foydalanuvchi tasdig'i bilan). Bo'limlar `ui/filter/section/` da.
- (C) `TransactionFilterDraft` testi, 3 tilda kalitlar, AUDIT.md.

**Xulq:**
- Filtr **draft** bilan ishlaydi: tanlovlar faqat "Show N Results" bosilganda `TransactionsEvent` lar orqali qo'llanadi; ✕ / sheet'dan tashqari bosish — o'zgarish bekor. "Clear all" faqat draftni tozalaydi.
- "Show N Results" — draft bo'yicha son; mavjud `transactionRepository.totals(...)` dagi `count` dan olinadi. SQL va `MonitoringFilter` o'zgarmaydi.
- Filtrlar avvalgidek **bitta tanlovli** (AUDIT §4; ko'p tanlov QILINMAYDI).
- Display currency (konvertatsiya valyutasi) filtr emas: `TransactionFilterDraft` ga kirmaydi, "Show N Results" va "Clear all" ga ta'sir qilmaydi; filtr ekranida alohida bo'limda tanlanadi (qo'llanish payti (darhol yoki "Show" bilan) — B da aniqlanadi).

**QILINMAYDI (rasmda bor, ilovada yo'q):** "Include Taxes and Fees" / "Show Available Hotels Only" almashtirgichlari, narx slayderi, "Amenities" checkboxlari.
**Ochiq savol:** yo'q — chiplar qatori olib tashlanadi (yuqoridagi qaror).
**Qabul mezoni:** Transactions sahifasida chiplar qatori yo'q; barcha filtrlar faqat shu ekranda tanlanadi; natija avvalgi 4 sheet bilan bir xil; "Clear all" + ✕ to'g'ri ishlaydi; 3 til, light/dark, font 12–24 sp, touch target ≥ 48dp; FAB va reset tugmasi yo'q.

## T18 — Bar style bottom sheet + glass shaffofligi slayderi
**Muammo:** Glass bar shaffofligi kodda qattiq (`GlassBarAlpha = 0.72f`, `Theme.kt`) — foydalanuvchi sozlay olmaydi. Settings'da "Bar style" — oddiy segmentli qator (`BarStyleSelector`).
**Hozirgi:** `feature/settings/ui/component/BarStyleSelector.kt`, `core/designsystem/theme/Theme.kt` (`GlassBarAlpha`, `barBackground()`, `LocalBarStyle`), `core/designsystem/component/GlassBar.kt` (blur radiusi 24dp, qattiq), `SettingsRepository` (`bar_style`), `MainActivity` → `MoneyManagerTheme(barStyle=…)`.

**Qarorlar (foydalanuvchi, 2026-11-06):**
- Glass bar shaffofligi **0 dan 100% gacha** slayder bilan sozlanadi.
- Settings'da "Bar style" bosilganda **bottom sheet** ochiladi (T19 dagi palitra sheet'i kabi).

**Ma'no (taklif, tasdiqlanadi):** shaffoflik **0% = to'liq zich** (tint qoplamasi opaque), **100% = tint butunlay shaffof** (faqat xiralashgan kontent ko'rinadi). Default **28%** — hozirgi `0.72` alfa bilan bir xil ko'rinish, ya'ni mavjud foydalanuvchilar uchun hech narsa o'zgarmaydi.

**Bosqichlar:**
- (A) Xulq o'zgarmaydi. `GlassBarAlpha` konstantasi o'rniga `LocalGlassTransparency` (0..1) va `MoneyManagerTheme(glassTransparency=…)`; `barBackground()` alfani shundan oladi. `SettingsRepository`: yangi kalit `bar_glass_transparency` (Int 0–100, default 28) + `setBarGlassTransparency`; ViewModel/UiState/Callbacks. Foiz → alfa mapping sof funksiya + unit test (chegaralar 0/100, clamp, default).
- (B) `BarStyleSheet` (`ModalBottomSheet`): Tinted | Glass segmenti; Glass tanlanganda "Transparency" slayderi (0–100%, qadam 1%, qiymat belgisi), mini-preview (bar chizig'i); o'zgarish darhol butun ilovaga qo'llanadi. Tinted'da slayder o'chirilgan. Settings qatori joriy uslubni (Glass bo'lsa foizi bilan) va chevron'ni ko'rsatadi; eski `BarStyleSelector` segmenti olib tashlanadi. Slayderni sudrash paytida DataStore'ga har kadrda yozilmaydi (mahalliy state + `onValueChangeFinished`da saqlash).
- (C) Testlar, 3 tilda kalitlar, AUDIT.md.

**Eslatmalar:**
- Blur faqat Android 12+ da; eski qurilmalarda shaffoflik tint alfasi sifatida baribir ishlaydi. Sheet'da bu izohlanadi.
- T19 va T18 bir xil sheet qobig'ini ishlatadi (`SettingsSheetContainer`): qaysi biri birinchi bajarilsa, o'sha yaratadi, ikkinchisi qayta ishlatadi.
**Ochiq xavf:** shaffoflik 100% ga yaqin bo'lganda bar ustidagi sarlavha/ikonkalar o'qilishi orqadagi kontentga bog'liq bo'ladi. Foydalanuvchi talabi — to'liq 0–100%; yumshatish (masalan, yuqori chegara) kerak bo'lsa B dan oldin so'raladi.
**QILINMAYDI:** blur radiusi sozlamasi (hozircha 24dp qattiq); har ekran uchun alohida bar sozlamasi.
**Qabul mezoni:** slayder 0–100% da silliq ishlaydi va qiymat ilovani qayta ochganda saqlanadi; default'da ko'rinish avvalgidek; Tinted uslubiga ta'sir qilmaydi; light/dark, 3 til, font 12–24 sp, touch target ≥ 48dp; `contentDescription` (qiymat foizi bilan).

## T19 — Color Palette bottom sheet + PaletteRegistry
**Muammo:** Settings'da palitralar qatori inline 5 ta doira (`PalettePicker`, `Row` + `SpaceBetween`) — ko'proq palitra sig'maydi. Palitra `AppPalette` enum'ida qattiq yozilgan: yangi palitra uchun `Enums.kt`, `Palettes.kt`, `PaletteSwatch.kt`, 3 tilli string va testga tegish kerak.
**Hozirgi:** `feature/settings/ui/component/PalettePicker.kt`, `PaletteSwatch.kt`; `core/designsystem/theme/Palettes.kt` (`getPaletteColorScheme`, `getPaletteSwatchColor`), `*Palette.kt`; `domain/model/Enums.kt` (`AppPalette`, `fromStored`); `SettingsRepository` (kalit `app_palette`).

**Qarorlar (foydalanuvchi, 2026-11-06):**
- **10 ta asosiy rang:** tayyor palitralar soni 10 ta bo'ladi; ranglar jiddiy (enterprise) tizimlarda ishlatiladigan turdagi bo'lishi shart. Aniq ro'yxat (nom + seed) foydalanuvchi bilan kelishiladi (A boshlanishidan oldin). Har biri light+dark, AA kontrast (`PaletteContrastTest` 10 palitraga ham), saqlangan eski nomlar (EMERALD, OCEAN_TEAL, ROYAL_INDIGO, WARM_CORAL, GRAPHITE_MINT, hamda eski OCEAN/VIOLET/SUNSET/GRAPHITE) buzilmaydi.
- Settings'da "Color Palette" bosilganda **bottom sheet** ochiladi.
- Palitralar bitta ro'yxatdan (registry) olinadi, shunda ko'proq palitra qo'shish oson bo'ladi (T20 uchun asos).

**Bosqichlar:**
- (A) Xulq o'zgarmaydi. `AppPalette` enum o'rniga `PaletteId` + `ThemePalette(id, nameRes, light, dark)` va `PaletteRegistry.builtIns`; `getPaletteColorScheme` / swatch registry'dan oladi. Saqlangan nomlar (eski `OCEAN`/`VIOLET`/`SUNSET`/`GRAPHITE` ham) avvalgidek ishlaydi, `app_palette` kaliti o'zgarmaydi. Yangi built-in palitra = 1 fayl + registry'ga 1 qator + 3 tilli nom.
- (B) Settings qatori: joriy palitra doirasi + nomi + chevron. `PaletteSheet` (`ModalBottomSheet`): adaptiv grid, har palitra uchun mini-preview (primary / surface / income / expense), tanlangani belgilanadi; tanlov darhol qo'llanadi (orqada ekran o'zgaradi), sheet yopilguncha ochiq turadi. Eski inline `PalettePicker`/`PaletteSwatch` o'chiriladi.
- (C) Registry testi (id'lar noyob, noma'lum nom → `DEFAULT`), `PaletteContrastTest` va `AppPaletteMigrationTest` registry bo'yicha ishlaydi, 3 tilda kalitlar, AUDIT.md.

**QILINMAYDI:** palitra yaratish/tahrirlash (T20 bekor qilingan); dizayndagi alohida "Palette ekrani" (T13 bekor qilingan, shu sheet uni almashtiradi).
**Qabul mezoni:** 5 ta mavjud palitra avvalgidek tanlanadi va saqlanadi; light/dark, 3 til, font 12–24 sp; touch target ≥ 48dp; `contentDescription` (palitra nomi + tanlangan).

## T20 — Foydalanuvchi o'z palitrasini yaratadi — BEKOR QILINDI (foydalanuvchi qarori, 2026-11-06)
> Bajarilmaydi. Foydalanuvchi o'z palitrasini yaratmaydi; o'rniga T19 da 10 ta tayyor (built-in) enterprise palitra bo'ladi. Quyidagi matn tarixiy yozuv sifatida qoldirildi.

**Muammo:** foydalanuvchi faqat 5 ta tayyor palitradan tanlay oladi; o'ziga yoqqan rangda palitra yarata olmaydi.
**Bog'liq:** T19 (registry va `PaletteSheet`).

**Qarorlar (foydalanuvchi, 2026-11-06):** ilova ichida foydalanuvchi o'z palitrasini yaratadi.
**Dastlabki shartlar (B boshlanishidan oldin tasdiqlanadi):**
- Foydalanuvchi **bitta asosiy (seed) rang** va **nom** beradi; light va dark sxema undan avtomatik hosil bo'ladi.
- `PaletteSheet` ichida "+ Add palette" katagi → `PaletteEditorSheet`: nom (≤ 24 belgi), rang tanlagich (HSV + hex), jonli preview (light va dark yonma-yon).
- Generator income/expense/error ranglar va matn kontrastini AA darajada ushlab turadi (T11 dagi `PaletteContrastTest` talabi custom palitralarga ham tegishli).
- Saqlash: `DataStore` (JSON ro'yxat: id, nom, seed ARGB); **DB sxemasi o'zgarmaydi**. Cheklov: ko'pi bilan 10 ta custom palitra.
- Tahrirlash va o'chirish faqat custom palitralarda; o'chirish `MmDialog` bilan tasdiqlanadi; tanlangan palitra o'chsa → `DEFAULT`. Tayyor (built-in) palitralar tahrirlanmaydi.
- Tanlangan palitra `app_palette` da: built-in nomi yoki `custom:<id>`; topilmasa → `DEFAULT`.

**Bosqichlar:**
- (A) Xulq o'zgarmaydi. `CustomPaletteStore` (DataStore) + `PaletteRegistry` = built-in + custom oqimi; `SchemeGenerator` sof funksiya (seed → light/dark `ColorScheme`) + unit test. Generator yechimi (material-color-utilities mavjudmi yoki o'zimiz tonal hisoblaymizmi) shu bosqichda aniqlanadi va tasdiq so'raladi.
- (B) `PaletteEditorSheet`, "+ Add palette" katagi, custom palitrada tahrirlash/o'chirish, jonli preview, validatsiya (bo'sh nom, takror nom, limit).
- (C) Testlar: generator kontrasti (turli seed: och, to'q, kam to'yingan), store round-trip, tanlangan palitra o'chirilganda fallback, `fromStored('custom:..')`; 3 tilda kalitlar; AUDIT.md.

**QILINMAYDI:** palitrani eksport/import yoki ulashish; bir nechta rang (secondary/tertiary) qo'lda berish — hozircha faqat seed.
**Qabul mezoni:** yaratilgan palitra ilovani qayta ochganda ham saqlanadi; hamma ekranlarda (light/dark) matn va income/expense ranglari o'qiladigan; yaroqsiz kiritish xato matni bilan to'xtatiladi; 3 til.
