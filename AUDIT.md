# AUDIT.md — ish holati (har sessiya oxirida yangilanadi)

> Bu fayl — sessiyalar orasidagi **xotira**. Yangi chat shu faylni o'qib, qaysi task va qaysi blokka kelganimizni biladi.
> Qoida: faqat o'zing bajargan va tekshirgan narsani ✅ qil. Taxmin qilma.

**Belgilar:** ⬜ boshlanmagan · 🟨 jarayonda / qisman · ✅ tugagan va tekshirilgan · ➖ bu ekranga tegishli emas · ⚠️ muammo bor (Izohlar'ga yoz)

**Oxirgi yangilanish:** 2026-11-05 (T04 kodi tayyor va commit qilingan; `DateRangePresetTest` yozilgan, ishga tushirilmagan; ekranda ko'rish qoldi)
**Keyingi task:** `T05` (T00 da faqat qurilmadagi debug/release o'lchovi qoldi, 5-bo'lim №4; T03 da androidTest ishga tushirilmagan, №8)

---

## 1. Tasklar holati

| Task | Nomi | Holat | Sessiya sanasi | Commit(lar) |
|---|---|---|---|---|
| T00 | Performance baseline | 🟨 | 2026-11-05 | 19ce9ba (lifecycle collect), 85ff3f6 (baseline profile) |
| T01 | Design system + nav qobig'i | ✅ | 2026-11-05 | aaa29aa (A: theme ko'chirildi), eee3771 (B: komponentlar, MoneyColors, BottomBar) |
| T02 | Palitralar (5 light + 5 dark) | ✅ | 2026-11-05 | 5277823 (A: palitralar fayllarga bo'lindi), d079b98 (B: yangi palitralar, fallback, testlar) |
| T03 | Transactions ro'yxati | ✅ | 2026-11-05 | d5e70b5 (A: route/screen/components), d78339a (B: yangi dizayn) |
| T04 | Filtrlar va sana oralig'i | 🟨 | 2026-11-05 | 1fc21e3 (A: chiplar fayllarga bo'lindi), 9d48f40 (B: filtr sheetlari) |
| T05 | Summary Report | ⬜ | | |
| T06 | New Transaction | ⬜ | | |
| T07 | Edit Transaction | ⬜ | | |
| T08 | Settings | ⬜ | | |
| T09 | Currencies | ⬜ | | |
| T10 | Exchange Rates | ⬜ | | |
| T11 | Categories | ⬜ | | |
| T12 | Payment Methods | ⬜ | | |
| T13 | Palette ekrani (ixtiyoriy) | ⬜ | | |
| T14 | Yakuniy audit | ⬜ | | |

---

## 2. Ekran × blok matritsasi

Bloklar: **Split** = Route/Screen/komponentlarga ajratilgan (fayl ≤ ~300 qator) · **Domain** = UI domain modelga bog'langan, entity UI'da yo'q · **Light** = yangi dizayn (light) · **Dark** = dark tekshirilgan · **States** = loading/empty/error holatlari · **i18n** = uz/en/ru stringlar · **Perf** = lifecycle-aware collect, key/contentType, barqaror state · **Tests** = unit/UI testlar yashil

| Ekran (hozirgi qator soni) | Split | Domain | Light | Dark | States | i18n | Perf | Tests | Task |
|---|---|---|---|---|---|---|---|---|---|
| Transactions ro'yxati (Screen 118) | ✅ | ⬜ | ✅ | ✅ | ✅ | ✅ | ✅ | 🟨 | T03 |
| Filtr sheetlari | ✅ | ➖ | 🟨 | ⬜ | ➖ | ✅ | ✅ | 🟨 | T04 |
| Summary Report — Details | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | T05 |
| Summary Report — Pie Chart | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | T05 |
| New Transaction (945 + VM 519) | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | T06 |
| Edit Transaction | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | T07 |
| Settings (576) | ⬜ | ➖ | ⬜ | ⬜ | ➖ | ⬜ | ⬜ | ⬜ | T08 |
| Currencies (332) | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | T09 |
| Exchange Rates (644) | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | T10 |
| Categories (258) | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | T11 |
| Payment Methods (353) | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | T12 |
| Palette ekrani (ixtiyoriy) | ➖ | ➖ | ⬜ | ⬜ | ➖ | ⬜ | ➖ | ➖ | T13 |

### Umumiy (cross-cutting)

| Blok | Holat | Izoh |
|---|---|---|
| Release/benchmark build o'lchovi | 🟨 | T00: release APK yig'iladi (R8, debug imzo) ✅; debug vs release qotish qurilmada O'LCHANMAGAN |
| Baseline profile | 🟨 | T00: qo'lda yozilgan wildcard profil + profileinstaller, release'da compileReleaseArtProfile o'tdi; ta'siri o'lchanmagan, Macrobenchmark bilan aniqlash mumkin |
| Design system komponentlari (`core/designsystem`) | ✅ | T01: 9 komponent + MoneyColors + BottomBar; build o'tdi; foydalanuvchi preview va testlarni tasdiqladi (2026-11-05) |
| Palitralar 5 light + 5 dark, AA kontrast | ✅ | T02: 5 palitra × light/dark; `PaletteContrastTest` yashil (foydalanuvchi run-checks.bat, 2026-11-05); qurilmada foydalanuvchi ko'rib chiqdi |
| Saqlangan palitra nomlari bilan orqaga moslik | ✅ | T02: `AppPalette.fromStored()` + `AppPaletteMigrationTest` yashil (foydalanuvchi tasdiqladi) |
| Domain model + repository interfeyslari (entity UI'dan chiqarildi) | ⬜ | feature bo'yicha, ekran tasklarida |
| `README.md` struktura bo'limi yangilandi | ⬜ | T14 |

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
| 6 | T03: Summary sheet/donut, `TransactionRow`, `TransactionList`, `TotalSummaryFooter` composable'lari ~80 qatordan uzun (sheet/donut ataylab o'zgarmay ko'chirildi) | T05 | T05 da qayta yoziladi |
| 7 | T03: ro'yxat qatori endi o'z valyutasida (AUDIT §4); kunlik sarlavhada balans o'rniga operatsiyalar soni. `TransactionListRow` ga `createdAt`, `categoryKey` qo'shildi (SELECT proyeksiyasi, sxema o'zgarmagan) | T03 | ma'lumot |
| 8 | T03: androidTest (`PaymentMethodsComposeUiTest`) qurilmada ishga tushirilmagan; qurilmada ishga tushirishni foydalanuvchi hal qiladi (light/dark, empty, skeleton ekranda ko'rildi, muammo yo'q) | T03 | ochiq |
| 9 | T04: dizayndagi 'All (45)/Income (12)/Expense (33)' sonlari yo'q (tur bo'yicha son hisoblanmaydi, ortiqcha so'rov yozilmadi); chiplarda faqat tanlangan nom ko'rinadi | T04 | ma'lumot |
| 10 | T04: `PaymentMethodsComposeUiTest` filtr oqimi sheet'ga moslab yangilandi, lekin qurilmada ishga tushirilmagan; filtr sheetlari va sana oralig'i ekranda (light/dark) ko'rilmagan | T04 | foydalanuvchi tekshiradi |

---

## 6. Sessiya jurnali (har sessiya oxirida YANGI QATOR qo'shiladi, eskisini o'chirma)

| Sana | Task | Nima qilindi | Qaysi bloklar ✅ bo'ldi | Qolgan / keyingi qadam | Commit |
|---|---|---|---|---|---|
| 2026-11-05 | reja | Dizayn tahlil qilindi; ARCHITECTURE.md, tasks.md, AUDIT.md, AI_PROMPT.md yozildi | — | T00 dan boshlash | — |
| 2026-11-05 | T00 | collectAsState -> collectAsStateWithLifecycle (Transactions 7, Settings 4); profileinstaller + baseline-prof.txt; assembleDebug, testDebugUnitTest, assembleRelease yashil (JBR JDK 21) | — (T00 qisman, bloklar ✅ emas) | qurilmada debug/release o'lchovi; Transactions UiState (T03); keyin T01 | 19ce9ba, 85ff3f6 |
| 2026-11-05 | T01 | (A) theme -> core/designsystem/theme; (B) MmTopBar, MmCard, MmFilterChip, MmBadge, MmFab, MmEmptyState, MmSectionHeader, MmDialog, AmountText, MoneyColors, BottomBar (pill indikator, surfaceContainer fon); i18n: yangi matn yo'q (action_back/cancel 3 tilda bor) | — (T01 qisman: preview va testlar tasdiqlanmagan) | @Preview'larni Android Studio'da ko'rish; testDebugUnitTest; keyin T02 | aaa29aa, eee3771 |
| 2026-11-05 | T02 | (A) Palettes.kt 363→25 qator, palitra boshiga alohida fayl; (B) AppPalette → EMERALD/OCEAN_TEAL/ROYAL_INDIGO/WARM_CORAL/GRAPHITE_MINT, 4 yangi light+dark sxema, Emerald dark yashil tusli fon, eski saqlangan nomlar uchun fallback, 3 tilda nomlar, kontrast va migratsiya testlari | T02 cross-cutting bloklar (palitralar, orqaga moslik) ✅ — build, testlar va ko'rishni foydalanuvchi tasdiqladi | keyin T03 | 5277823 (A), d079b98 (B) |
| 2026-11-05 | T03 | (A) 1232 qatorli ekran → `feature/transactions/` (Route, stateless Screen, UiState/Event, component/dialog/sheet), FormatUtils → `core/format`; (B) yangi ro'yxat dizayni: MmTopBar/MmFab/MmCard qatorlar, ikonka xaritasi, sana pill + operatsiyalar soni, Total Summary pill, skeleton + empty, chiplarda (x); `operations_count` plural 3 tilda | Split, i18n, Perf ✅ (kod bo'yicha tekshirildi); build + unit testlar foydalanuvchi tomonidan yashil | ekranda light/dark, empty/skeleton ko'rish; androidTest; keyin T04 | d5e70b5 (A), d78339a (B) |
| 2026-11-05 | T04 | (A) filtr chiplari bir fayl-bir komponent, tizim DatePickerDialog `sheet/` ga; (B) `CategoryFilterSheet`, `PaymentCurrencySheet`, `TypeFilterSheet`, `DateRangeSheet` (M3 DateRangePicker + presetlar), `FilterSheetHost`, `DateRangePreset`; chiplar sheet ochadi, (x) bilan tozalanadi; 8 yangi string 3 tilda; androidTest filtr oqimi yangilandi; `DateRangePresetTest` | Split, i18n, Perf ✅ (kod bo'yicha); (A) build foydalanuvchi tomonidan yashil | (B) build/test natijasi, ekranda ko'rish (light/dark), androidTest; keyin T05 | 1fc21e3 (A), 9d48f40 (B) |
