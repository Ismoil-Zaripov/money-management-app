# 💰 Money Management App

Zamonaviy Android mobil ilovasi — shaxsiy moliya, pul kirim va chiqimlarini hisobga olish, ko‘p valyutali monitoring va Markaziy Bank (CBU.uz) kurslari bilan integratsiyalashgan to‘liq tizim.

---

## 📱 Asosiy Imkoniyatlar (Features)

- **Kirim va Chiqim Tranzaksiyalari:**
  - Miqdor (Amount), Valyuta, Sana va Tavsif/Kategoriya (Info) kiritish.
  - **Validatsiya:** 0 va manfiy summalarni kiritish qat'iy taqiqlangan (`amount > 0`).
  - **Smart Info / Kategoriya tanlash:** Yangi info kiritish yoki avval kiritilgan infolarni qidirib/tanlab olish (ComboBox / Dropdown).

- **Dinamik Valyutalar Boshqaruvi:**
  - Istalgan yangi valyutani qo‘shish (USD, EUR, RUB, KZT va boshqalar).
  - **Xavfsizlik:** Agar valyuta biron bir tranzaksiyada ishlatilgan bo‘lsa, uni o‘chirishga ruxsat berilmaydi (Room Foreign Key `RESTRICT` qoidasi).

- **Valyuta Kurslari va CBU.uz Integratsiyasi:**
  - O‘zbekiston Respublikasi Markaziy Banki (cbu.uz) ochiq API'sidan jonli kurslarni avtomatik yuklash.
  - **Teskari kurslar (Inverse / Cross-rates):** Masalan, `1 USD = 10 EUR` kiritilsa, tizim avtomatik `1 EUR = 0.1 USD` deb saqlaydi va o‘zaro konvertatsiyani amalga oshiradi.
  - Sana bo‘yicha kurs mavjud bo‘lmaganda foydalanuvchiga avtomatik ogohlantirish berish.

- **Keng Qamrovli Monitoring va Filtrlash:**
  - Kategoriya/Info, tranzaksiya turi (Kirim / Chiqim), sanalar oralig‘i (Date Range) bo‘yicha qidiruv.
  - Katta hajmdagi tranzaksiyalarni tezkor va samarali yuklash uchun **Jetpack Paging 3**.
  - **Fallback Valyuta hisobi:** Agar biror valyuta (masalan, KZT) yaqinda kiritilgan bo‘lib, undan oldingi tranzaksiyalar uchun kurs bo‘lmasa, tizim hisobotda 2 xil valyutani alohida ko‘rsatadi:
    1. Tanlangan ko‘rish valyutasi (mavjud kursli tranzaksiyalar uchun).
    2. Fallback valyuta (kursi mavjud bo‘lmagan eski tranzaksiyalar uchun UZS da).

- **"Jami summa" Umumiy Hisoboti (Bottom Sheet):**
  - **Qatorlar (Rows view):** Mobil ekranlar (jumladan, ixcham Samsung Galaxy A04e) uchun maxsus moslashtirilgan MUI input uslubidagi stacked dizayn — kichik sarlavha yuqorida, summa ostida alohida qatorda.
  - **Aylana Diagramma (Pie Chart view):** Kirim va chiqim mutanosibligini ko‘rsatuvchi toza Canvas diagramma.

- **Ko‘p tillilik (Localization):**
  - O‘zbekcha (Lotin)
  - English
  - Русский

---

## 🛠 Texnologiyalar To‘plami (Tech Stack)

| Texnologiya | Maqsadi |
|---|---|
| **Kotlin** | Asosiy dasturlash tili (100%) |
| **Jetpack Compose** | Deklarativ zamonaviy UI va Material Design 3 |
| **Room Database** | Lokal relyatsion ma'lumotlar bazasi (Foreign Keys, Converters) |
| **Dagger Hilt** | Dependency Injection (DI) |
| **Android Architecture Components** | MVVM, ViewModel, StateFlow, Coroutines |
| **Jetpack Paging 3** | Ro‘yxatlarni qismlarga bo‘lib (pagination) yuklash |
| **Retrofit & OkHttp** | CBU.uz Markaziy Bank API bilan tarmoq integratsiyasi |
| **Canvas** | Shaxsiy Custom Pie Chart grafiklarini chizish |

---

## 📂 Loyiha Strukturasi (Project Architecture)

```
uz.moneymanager/
├── data/
│   ├── local/            # Room Database, Daos, Entities, Converters
│   ├── remote/           # Retrofit CbuApi va model klasslar
│   └── repository/       # Transaction, Currency, Rate, Settings repozitoriylari
├── di/                   # Hilt modullari (Database, Network, Repository modules)
├── domain/               # Biznes qoidalar, hisob-kitoblar, modellar
│   └── model/            # TransactionType, Enums
├── ui/
│   ├── categories/       # Kategoriya / Info boshqaruvi
│   ├── currencies/       # Valyutalar qo'shish va sozlash
│   ├── monitoring/       # Monitoring, Filtrlash, Pie Chart, Umumiy hisobot
│   ├── navigation/       # Compose Navigation marshrutlari
│   ├── rates/            # Kurslar va CBU.uz yuklovchisi
│   ├── settings/         # Til va ilova sozlamalari
│   ├── theme/            # Material 3 Colors, Shapes, Typography
│   └── transactions/     # Tranzaksiyalar ro'yxati va yangi qo'shish formasi
├── MainActivity.kt
└── MoneyApp.kt
```

---

## 🚀 O‘rnatish va Ishga Tushirish (Build & Run)

1. **Repozitoriyadan nusxa oling:**
   ```bash
   git clone https://github.com/Ismoil-Zaripov/money-management-app.git
   cd money-management-app
   ```

2. **Android Studio orqali oching:**
   - Android Studio (Ladybug / Iguana yoki undan yangi versiya).
   - JDK 17 yoki JDK 21 tavsiya etiladi.

3. **Terminal orqali yig‘ish (Gradle):**
   ```bash
   ./gradlew assembleDebug
   ```

4. **APK joylashuvi:**
   Tayyor APK fayl quyidagi manzilda hosil bo‘ladi:
   `app/build/outputs/apk/debug/app-debug.apk`

---

## 👤 Muallif
- **Ismoil Zaripov** ([@Ismoil-Zaripov](https://github.com/Ismoil-Zaripov))
