# 📱 BMI Calculator - Android App with Neon Database

A comprehensive BMI Calculator Android app built with Kotlin that connects to a Neon PostgreSQL database. Features a beautiful soft pink theme designed for fitness enthusiasts.

![Platform](https://img.shields.io/badge/Platform-Android-green.svg)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-purple.svg)
![Backend](https://img.shields.io/badge/Backend-Node.js-blue.svg)
![Database](https://img.shields.io/badge/Database-Neon%20PostgreSQL-orange.svg)

---

## ✨ Features

- 🔐 **User Authentication** - Secure login/register with JWT tokens
- 📊 **BMI Calculator** - Real-time BMI calculation with category classification
- 📈 **History Tracking** - View all your past BMI records
- 🎯 **Goals Management** - Set and track fitness goals
- 📉 **Dashboard** - Visual overview with charts and statistics
- 💾 **Offline Support** - Local caching with Room database
- 🎨 **Beautiful UI** - Soft pink theme with Material Design 3
- 🔄 **Auto-Sync** - Seamless sync between local and remote database

---

## 🏗️ Architecture

- **Pattern**: MVVM (Model-View-ViewModel)
- **Database**: 
  - Remote: Neon PostgreSQL
  - Local: Room (for offline caching)
- **Networking**: Retrofit + OkHttp
- **Async**: Kotlin Coroutines + Flow
- **UI**: Material Design 3
- **Charts**: MPAndroidChart

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- Node.js v18+ (for backend)
- Neon Database account (free tier available)

### 1. Clone the Repository

```bash
git clone https://github.com/YolaCathrine/BMICalculator1.git
cd BMICalculator1
```

### 2. Set Up Neon Database

1. Create a free account at [Neon](https://neon.tech)
2. Create a new project
3. Run the SQL schema:

```bash
# Copy the contents of database/schema.sql
# Paste into Neon SQL Editor and run
```

4. Copy your connection string

### 3. Configure Backend

```bash
cd backend
cp .env.example .env
```

Edit `backend/.env`:
```env
DATABASE_URL=your_neon_connection_string_here
JWT_SECRET=your-secret-key-change-this
PORT=3000
```

### 4. Install Backend Dependencies

```bash
cd backend
npm install
```

### 5. Run Backend Server

```bash
npm run dev
```

Server will run on: `http://localhost:3000`

### 6. Configure Android App

Open `app/src/main/java/com/example/bmicalculator1/data/remote/RetrofitClient.kt`:

```kotlin
// For Android Emulator
private const val BASE_URL = "http://10.0.2.2:3000/"

// For Physical Device (replace with your IP)
// private const val BASE_URL = "http://192.168.X.XXX:3000/"
```

### 7. Build & Run Android App

1. Open project in Android Studio
2. Sync Gradle files
3. Build > Rebuild Project
4. Run on emulator or physical device

---

## 📱 Usage

### Test Credentials

After running the app, you can use these test accounts:

- **Email**: `user1@example.com`
- **Password**: `password123`

Or create a new account directly in the app!

### How to Use

1. **Login/Register** - Create an account or login with test credentials
2. **Calculate BMI** - Enter weight (kg) and height (cm), click Calculate
3. **Save Record** - Click Save to store your BMI record
4. **View History** - Navigate to History tab to see all records
5. **Set Goals** - Create fitness goals and track progress
6. **Dashboard** - View your BMI trends and statistics

---

## 🗂️ Project Structure

```
BMICalculator1/
├── app/                          # Android app module
│   ├── src/main/
│   │   ├── java/.../bmicalculator1/
│   │   │   ├── data/            # Data layer
│   │   │   │   ├── local/       # Room database
│   │   │   │   ├── model/       # API models
│   │   │   │   ├── remote/      # Retrofit API
│   │   │   │   └── repository/  # Repositories
│   │   │   ├── domain/          # Business logic
│   │   │   └── ui/              # UI layer
│   │   │       ├── fragment/    # Fragments
│   │   │       ├── adapter/     # Adapters
│   │   │       └── viewmodel/   # ViewModels
│   │   └── res/                 # Resources
│   └── build.gradle.kts
├── backend/                      # Node.js backend
│   ├── routes/                   # API routes
│   ├── middleware/               # Auth middleware
│   ├── db.js                     # Database connection
│   └── server.js                 # Express server
├── database/                     # Database schema
│   └── schema.sql
└── README.md
```

---

## 🔌 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user
- `GET /api/auth/profile` - Get user profile

### BMI Records
- `GET /api/bmi` - Get all BMI records
- `POST /api/bmi` - Create new BMI record
- `DELETE /api/bmi/{id}` - Delete record
- `GET /api/bmi/stats/history` - Get BMI history for charts

### Goals
- `GET /api/goals` - Get all goals
- `POST /api/goals` - Create new goal
- `PUT /api/goals/{id}` - Update goal
- `DELETE /api/goals/{id}` - Delete goal

---

## 🎨 Design System

### Color Palette (Soft Pink Theme)

| Color | Hex | Usage |
|-------|-----|-------|
| Primary | `#FFB6C1` | Light Pink |
| Secondary | `#FFC0CB` | Pink |
| Accent | `#FF69B4` | Hot Pink |
| Background | `#FFF0F5` | Lavender Blush |
| Surface | `#FFFFFF` | White |

### BMI Category Colors

- **Underweight**: Light Blue `#87CEEB`
- **Normal**: Green `#90EE90`
- **Overweight**: Yellow `#FFD700`
- **Obese**: Red `#FF6B6B`

---

## 🛠️ Technologies

### Android
- Kotlin 1.9.20
- AndroidX Core KTX
- Material Design 3
- Room Database 2.6.1
- Retrofit 2.9.0
- OkHttp 4.12.0
- Kotlin Coroutines 1.7.3
- MPAndroidChart v3.1.0
- DataStore Preferences 1.0.0

### Backend
- Node.js
- Express.js 4.18.2
- PostgreSQL (Neon)
- pg 8.11.3
- JWT (jsonwebtoken) 9.0.2
- bcrypt 5.1.1
- CORS 2.8.5

---

## 📝 Database Schema

### Tables

1. **users**
   - id (UUID)
   - email (VARCHAR)
   - password_hash (VARCHAR)
   - created_at, updated_at

2. **bmi_records**
   - id (UUID)
   - user_id (FK)
   - weight, height
   - bmi_value, category
   - recorded_at

3. **goals**
   - id (UUID)
   - user_id (FK)
   - target_weight, target_bmi
   - goal_date, current_weight
   - status, created_at, updated_at

---

## 🧪 Testing

### Test Backend API

```bash
# Test health check
curl http://localhost:3000/health

# Test login
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user1@example.com","password":"password123"}'

# Test save BMI (replace TOKEN)
curl -X POST http://localhost:3000/api/bmi \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"weight":70,"height":175}'
```

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is open source and available for educational purposes.

---

## 👤 Author

**YolaCathrine**
- GitHub: [@YolaCathrine](https://github.com/YolaCathrine)
- Email: yolacathrine18@gmail.com

---

## 🙏 Acknowledgments

- [Neon Database](https://neon.tech) for serverless PostgreSQL
- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) for beautiful charts
- Material Design 3 guidelines

---

<div align="center">

**Made with ❤️ using Kotlin, Node.js, and Neon PostgreSQL**

⭐ Star this repo if you find it helpful!

</div>
