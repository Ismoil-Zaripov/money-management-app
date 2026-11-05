# AUDIT.md — ish holati (har sessiya oxirida yangilanadi)

> Bu fayl — sessiyalar orasidagi **xotira**. Yangi chat shu faylni o'qib, qaysi task va qaysi blokka kelganimizni biladi.
> Qoida: faqat o'zing bajargan va tekshirgan narsani ✅ qil. Taxmin qilma.

**Belgilar:** ⬜ boshlanmagan · 🟨 jarayonda / qisman · ✅ tugagan va tekshirilgan · ➖ bu ekranga tegishli emas · ⚠️ muammo bor (Izohlar'ga yoz)

**Oxirgi yangilanish:** 2026-11-05 (T00 qisman bajarildi)
**Keyingi task:** `T01` (T00 da faqat qurilmadagi debug/release o'lchovi qoldi, 5-bo'lim №4)

---

## 1. Tasklar holati

| Task | Nomi | Holat | Sessiya sanasi | Commit(lar) |
|---|---|---|---|---|
| T00 | Performance baseline | 🟨 | 2026-11-05 | 19ce9ba (lifecycle collect), 85ff3f6 (baseline profile) |
| T01 | Design system + nav qobig'i | ⬜ | | |
| T02 | Palitralar (5 light + 5 dark) | ⬜ | | |
| T03 | Transactions ro'yxati | ⬜ | | |
| T04 | Filtrlar va sana oralig'i | ⬜ | | |
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
| Transactions ro'yxati (1232) | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | T03 |
| Filtr sheetlari | ⬜ | ➖ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | ⬜ | T04 |
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
| Design system komponentlari (`core/designsystem`) | ⬜ | T01 |
| Palitralar 5 light + 5 dark, AA kontrast | ⬜ | T02 |
| Saqlangan palitra nomlari bilan orqaga moslik | ⬜ | T02 |
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
- Dizayndagi hamburger, profil avatari, "Screen State" preview, Sarmoya brendi, 5 tabli menyu — QILINMAYDI.

---

## 5. Ochiq masalalar / blokerlar

| # | Masala | Qaysi task | Holat |
|---|---|---|---|
| 1 | Kategoriya ikonkasi: DB'da maydon yo'q (nameKey xaritasi yoki yangi maydon) | T03 | qaror kerak |
| 2 | "Export" (Summary) — keyingi versiya | T05 | kechiktirildi |
| 3 | "View Transactions" havolasi (kategoriya FK xatosida) | T11 | ixtiyoriy |
| 4 | T00: debug vs release qotishni qurilmada o'lchash va oldin/keyin raqamlarini yozish (Claude bu muhitda emulyator ishlata olmaydi) | T00 | foydalanuvchi o'lchaydi |
| 5 | Gradle JDK 17/21 kerak: tizim JDK 25 da build yiqiladi ("25.0.1"); Android Studio JBR ishlatish | T00 | ma'lumot |

---

## 6. Sessiya jurnali (har sessiya oxirida YANGI QATOR qo'shiladi, eskisini o'chirma)

| Sana | Task | Nima qilindi | Qaysi bloklar ✅ bo'ldi | Qolgan / keyingi qadam | Commit |
|---|---|---|---|---|---|
| 2026-11-05 | reja | Dizayn tahlil qilindi; ARCHITECTURE.md, tasks.md, AUDIT.md, AI_PROMPT.md yozildi | — | T00 dan boshlash | — |
| 2026-11-05 | T00 | collectAsState -> collectAsStateWithLifecycle (Transactions 7, Settings 4); profileinstaller + baseline-prof.txt; assembleDebug, testDebugUnitTest, assembleRelease yashil (JBR JDK 21) | — (T00 qisman, bloklar ✅ emas) | qurilmada debug/release o'lchovi; Transactions UiState (T03); keyin T01 | 19ce9ba, 85ff3f6 |
