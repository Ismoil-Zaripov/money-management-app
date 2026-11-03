**Core**

- Transaction Management
  - Add income/expense — summa (`> 0`), tur, valyuta, kategoriya va sana bilan yangi kirim yoki chiqim qo'shadi.
  - Edit transaction — mavjud tranzaksiya ma'lumotlarini o'zgartiradi.
  - Delete transaction — tranzaksiyani o'chiradi (sync yoqilgan bo'lsa, o'chirish belgisi bilan).
  - List transactions — tranzaksiyalarni sahifalab (Paging) ro'yxat ko'rinishida ko'rsatadi.
- Smart Category
  - Autocomplete — yozilayotgan nomga mos avval ishlatilgan kategoriyalarni taklif qiladi.
  - Create new category — ro'yxatda yo'q bo'lsa, yangi kategoriya nomini yozib yaratadi.
- Basic Currencies
  - Use up to 3 currencies — bepul rejimda 3 tagacha valyutada ishlash imkonini beradi.
- Basic Settings
  - Select language — ilova tilini tanlaydi (UZ / EN / RU).
  - Use 1 theme — bitta standart mavzuda ishlaydi.
- Local Storage
  - Store data on device — ma'lumotlarni qurilmadagi lokal bazada (Room) saqlaydi, internetsiz ishlaydi.

**Currency & Exchange Rates**

- Unlimited Currencies
  - Add unlimited currencies — kod, nom va belgi bilan istalgancha valyuta qo'shadi.
  - Set default currency — hisobotlar va kiritishda standart bo'ladigan valyutani belgilaydi.
  - Protect used currency from deletion — kamida bitta tranzaksiyada ishlatilgan valyutani o'chirishni taqiqlaydi.
- CBU Auto Rates
  - Fetch rates from CBU.uz by date — Markaziy Bank kursini tanlangan sana bo'yicha avtomatik yuklaydi.
  - Missing rate warning — tranzaksiya sanasida kurs yo'q bo'lsa ogohlantiradi va kursni qo'lda kiritish yoki yuklashni taklif qiladi.
- Inverse/Cross Rates
  - Calculate and store inverse rate — `1 A = x B` kiritilsa, `1 B = 1/x A` ni avtomatik hisoblab saqlaydi.
  - Convert between currencies — bir valyutadagi summani boshqasiga kurs bo'yicha o'giradi.
- Fallback Multi-Currency Report
  - Display/Fallback grouping — hisobotni ko'rish valyutasi guruhi va fallback guruhiga ajratadi.
  - Calculate rate-less transactions in UZS — kursi mavjud bo'lmagan tranzaksiyalarni UZS'da alohida hisoblaydi, hech biri hisobotdan tushib qolmaydi.

**Wallets**

- Wallets/Accounts
  - Create wallet — naqd pul yoki karta kabi yangi hamyon yaratadi.
  - View wallet balance — hamyon qoldig'ini boshlang'ich balans, tranzaksiyalar va o'tkazmalardan hisoblab ko'rsatadi.
  - Link transaction to wallet — tranzaksiyani qaysi hamyondan bo'lganini biriktiradi.
- Wallet Transfers
  - Create transfer — pulni bir hamyondan boshqasiga o'tkazadi.
  - Transfer between different currencies — valyutasi farqli hamyonlar orasida ikki xil summa bilan o'tkazma qiladi.

**Reports & Analytics**

- Advanced Filter
  - Search by category — tranzaksiyalarni kategoriya bo'yicha saralaydi.
  - Search by type — faqat kirim yoki faqat chiqimni ko'rsatadi.
  - Search by date range — tanlangan sanalar oralig'idagi tranzaksiyalarni ko'rsatadi.
- Pie Chart
  - Income/expense/balance chart — kirim va chiqim nisbatini aylana diagrammada ko'rsatadi.
- Trend Analytics
  - Monthly trend — oylar kesimida kirim va chiqim o'zgarishini ko'rsatadi.
  - Compare categories — kategoriyalar bo'yicha xarajatlarni o'zaro taqqoslaydi.
- Excel/PDF Export
  - Export to Excel — hisobotni Excel faylga saqlaydi.
  - Export to PDF — hisobotni PDF faylga saqlaydi.

**Planning**

- Budget
  - Set category limit — kategoriya uchun davr bo'yicha xarajat limitini belgilaydi.
  - Warn when approaching limit — limitga yaqinlashganda yoki oshganda ogohlantiradi.
- Recurring Payments
  - Create recurring transaction rule — takrorlanuvchi to'lov qoidasini (davriyligi bilan) yaratadi va vaqti kelganda tranzaksiya qo'shadi.
  - Send reminder — to'lov muddatidan oldin eslatma bildirishnomasini yuboradi.
- Debt Book
  - Record debt — kim kimga qancha qarzdorligini yozadi.
  - Make partial payment — qarzning bir qismini to'lashni qayd etadi.
  - View remaining debt — qarzning qolgan summasini ko'rsatadi.

**Goals**

- Goal & Progress
  - Create goal — maqsad nomi, summasi va muddatini belgilab maqsad yaratadi.
  - Calculate progress — maqsadga qancha erishilganini foizda hisoblaydi.
  - Contribute to goal — maqsad uchun jamg'arilgan summaga hissa qo'shadi.
- Conditional Rules
  - Create rule — "agar X bo'lsa, Y qil" ko'rinishidagi qoida yaratadi.
  - Evaluate condition — qoidadagi shart bajarilganini tekshiradi.
  - Send notification — shart bajarilganda foydalanuvchiga bildirishnoma yuboradi.
- Goal Reports
  - Goal achievement forecast — hozirgi tezlikda maqsadga qachon erishilishini bashorat qiladi.
  - Goal report — maqsad bo'yicha hissalar va progress hisobotini beradi.

**Strategies**

- Strategy Templates
  - 50/30/20, envelope, zero-based templates — tayyor moliyaviy strategiya shablonlaridan birini tanlash imkonini beradi.
  - Assign category to group — kategoriyani ehtiyoj, xohish yoki jamg'arma guruhiga biriktiradi.
- Strategy vs Actual Comparison
  - Plan vs actual spending difference — rejadagi va haqiqiy xarajat farqini ko'rsatadi.
  - Deviation report — rejadan qaysi sohalarda chetga chiqilganini hisobot qiladi.
- Debt Payoff Plan
  - Snowball/avalanche calculation — qarzlarni yopish tartibini ikki usulda (kichigidan yoki foizi yuqorigidan) hisoblaydi.
  - Payment schedule — qarzni yopish uchun oylik to'lov jadvalini tuzadi.
- Insights
  - Rule-based recommendations — xarajat ma'lumotlariga qarab oldindan belgilangan qoidalar asosida tavsiyalar beradi.

**Cloud & Sync**

- Cloud Backup
  - Upload backup — ma'lumotlarning zaxira nusxasini serverga yuklaydi.
  - Restore from backup — zaxira nusxadan ma'lumotlarni tiklaydi.
- Multi-Device Sync
  - Delta sync — faqat o'zgargan yozuvlarni almashtirib, qurilmalarni bir xil holatda tutadi.
  - Resolve conflicts — bir yozuv ikki qurilmada o'zgarsa, qaysi biri saqlanishini hal qiladi.
  - List devices — hisobga ulangan qurilmalar ro'yxatini ko'rsatadi.

**Extras**

- Receipt Image
  - Attach image to transaction — tranzaksiyaga chek rasmini biriktiradi.
- Widget
  - Home-screen widget — asosiy ekrandan tez ko'rish va kiritish uchun widget beradi.
- Bank SMS Auto-Entry
  - Parse transaction from SMS — bank SMS xabaridan summa va turni ajratib, tranzaksiya yaratadi.

**Personalization**

- Theme Packs
  - 5 light themes — yorug' rejim uchun 5 ta rang mavzusini beradi.
  - 5 dark themes — qorong'i rejim uchun 5 ta rang mavzusini beradi.

**User & Auth**

- Account
  - Sign up — yangi foydalanuvchi hisobini yaratadi.
  - Sign in — foydalanuvchini tizimga kiritadi.
- Devices
  - Register device — foydalanuvchining yangi qurilmasini hisobga ulaydi.
  - Revoke device — qurilmaning kirish huquqini bekor qiladi.
- User Status
  - Block user — foydalanuvchini bloklab, kirishini to'xtatadi.
  - Activate user — bloklangan foydalanuvchini qayta faollashtiradi.

**Catalog**

- Domain Management
  - Create, update, deactivate domain — domainni yaratadi, o'zgartiradi yoki faolsizlantiradi (o'chirmaydi).
- Feature Management
  - Create, update, deactivate feature — feature'ni yaratadi, o'zgartiradi yoki faolsizlantiradi.
  - Attach function — feature'ga function va uning huquq kodini biriktiradi.
  - Define dependency — qaysi feature qaysi biriga bog'liqligini belgilaydi (aylanma bog'liqlikka yo'l qo'ymaydi).
- Pricing Management
  - Create price version — feature uchun yangi narxni kuchga kirish sanasi bilan qo'shadi, eski narx tarixda qoladi.
  - Set volume discount — tanlangan feature soniga qarab chegirma foizini belgilaydi.

**Subscription & Billing**

- Subscription Selection
  - Select features — foydalanuvchi o'ziga kerakli feature'larni tanlaydi.
  - Calculate price live — tanlovga qarab oylik narxni darhol hisoblab ko'rsatadi.
  - Auto-add dependent features — tanlangan feature talab qiladigan boshqa feature'larni avtomatik qo'shadi.
- Subscription Changes
  - Add feature (with proration) — feature'ni darhol ochadi va qolgan kunlar uchun narx hisoblaydi.
  - Remove feature (from next cycle) — feature'ni keyingi to'lov siklidan boshlab o'chiradi.
- Free Trial
  - Start 7-day trial — barcha feature'lar ochiq 7 kunlik bepul sinovni (bir marta) boshlaydi.
- Payment
  - Accept payment — to'lov provayderidan kelgan to'lovni qabul qilib tasdiqlaydi.
  - Create invoice — sikl uchun feature'lar, chegirma va jami summa ko'rsatilgan hisob-faktura yaratadi.

**Entitlement**

- Access Control
  - Issue signed token — foydalanuvchiga ochiq feature'lar yozilgan imzolangan token beradi (offline ham ishlaydi).
  - Deny-by-default check — obunada ko'rsatilmagan hamma narsani taqiqlaydi, server tomonda tekshiradi.
- Lock/Upsell
  - Show locked feature — yopiq feature'ni ilovada ko'rinadigan, lekin qulflangan holda ko'rsatadi.
  - Show add-feature offer — yopiq feature bosilganda uni qo'shish va narxini ko'rish taklifini chiqaradi.

**Admin RBAC**

- Roles
  - Create role — admin uchun yangi rol yaratadi.
  - Assign permission — rolga kerakli ruxsatlarni (masalan, narx o'zgartirish) biriktiradi.
- Security
  - 2FA — admin kirishini ikki bosqichli tasdiqlash bilan himoyalaydi.
  - Audit log — adminning har bir o'zgarishini (kim, qachon, nima) yozib boradi.

**Sync System**

- Sync Queue
  - Sync only N users at a time — serverda bir vaqtda faqat N ta foydalanuvchini sync qiladi, qolganlarini navbatda ushlaydi.
  - Enqueue and retry — so'rovni navbatga qo'yadi, joy bo'lmasa kutish vaqti bilan qayta urinishni buyuradi.
- Data Exchange
  - Delta sync — faqat o'zgargan yozuvlarni yuboradi va qabul qiladi.
  - Idempotency check — takroriy yuborilgan so'rovni ikki marta bajarmaydi.
  - Deliver deletions (tombstone) — o'chirilgan yozuvlar haqidagi belgini boshqa qurilmalarga yetkazadi.
