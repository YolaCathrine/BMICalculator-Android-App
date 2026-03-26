# BMI Calculator Android App with Neon Database

A comprehensive BMI Calculator Android app built with Kotlin that connects to a Neon PostgreSQL database. Features a soft pink, friendly design targeted at fitness enthusiasts.

## Features

- **User Authentication**: Register and login with email/password
- **BMI Calculator**: Calculate BMI with weight and height input
- **BMI History**: Track and view all your past BMI records
- **Goals**: Set and track weight loss/gain goals
- **Dashboard**: Visual overview with charts and statistics
- **Profile**: View account information and statistics
- **Offline Support**: Local caching with Room database
- **Soft Pink Theme**: Friendly, approachable design

## Tech Stack

### Android App
- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: 
  - Remote: Neon PostgreSQL
  - Local: Room (for offline caching)
- **Networking**: Retrofit + OkHttp
- **Async**: Kotlin Coroutines + Flow
- **UI**: Material Design 3
- **Charts**: MPAndroidChart
- **Navigation**: Bottom Navigation with Fragments

### Backend API
- **Runtime**: Node.js
- **Framework**: Express.js
- **Database Driver**: pg (PostgreSQL)
- **Authentication**: JWT (JSON Web Tokens)
- **Password Hashing**: bcrypt

## Prerequisites

1. **Android Studio**: Hedgehog (2023.1.1) or newer
2. **Node.js**: v18 or newer (for backend)
3. **Neon Database Account**: Free tier at https://neon.tech

## Setup Instructions

### 1. Set Up Neon Database

1. Create a free account at [Neon](https://neon.tech)
2. Create a new project named "BMICalculator"
3. Copy the connection string from the dashboard
4. Run the SQL schema in `database/schema.sql`:

```sql
-- Copy and paste the contents of database/schema.sql into Neon SQL editor
```

### 2. Set Up Backend

1. Navigate to the backend folder:
```bash
cd backend
```

2. Install dependencies:
```bash
npm install
```

3. Create `.env` file from `.env.example`:
```bash
cp .env.example .env
```

4. Update `.env` with your Neon connection string:
```env
DATABASE_URL=postgresql://user:password@host.neon.tech/bmicalculator?sslmode=require
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production
PORT=3000
CORS_ORIGIN=*
```

5. Start the backend server:
```bash
npm run dev
```

The API will be available at `http://localhost:3000`

### 3. Configure Android App

1. Open the project in Android Studio
2. Update the backend URL in `app/src/main/java/com/example/bmicalculator1/data/remote/RetrofitClient.kt`:

```kotlin
// For Android Emulator
private const val BASE_URL = "http://10.0.2.2:3000/"

// For Physical Device (replace with your computer's IP)
// private const val BASE_URL = "http://192.168.1.XXX:3000/"
```

3. Sync Gradle files
4. Build and run the app

## Project Structure

```
BMICalculator1/
├── app/                          # Android app module
│   ├── src/main/
│   │   ├── java/com/example/bmicalculator1/
│   │   │   ├── data/            # Data layer
│   │   │   │   ├── local/       # Room database
│   │   │   │   ├── model/       # API models
│   │   │   │   ├── remote/      # Retrofit API
│   │   │   │   └── repository/  # Repositories
│   │   │   ├── domain/          # Business logic
│   │   │   ├── ui/              # UI layer
│   │   │   │   ├── fragment/    # Fragments
│   │   │   │   ├── adapter/     # RecyclerView adapters
│   │   │   │   └── viewmodel/   # ViewModels
│   │   │   └── MainActivity.kt
│   │   ├── res/
│   │   │   ├── layout/          # XML layouts
│   │   │   ├── values/          # Colors, strings, themes
│   │   │   └── drawable/        # Drawables and icons
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── backend/                      # Node.js backend API
│   ├── routes/                   # API route handlers
│   ├── middleware/               # Auth middleware
│   ├── db.js                     # Database connection
│   ├── server.js                 # Express server
│   └── package.json
└── database/                     # Database schema
    └── schema.sql
```

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user
- `GET /api/auth/profile` - Get user profile

### BMI Records
- `GET /api/bmi` - Get all BMI records
- `POST /api/bmi` - Create new BMI record
- `GET /api/bmi/{id}` - Get specific record
- `DELETE /api/bmi/{id}` - Delete record
- `GET /api/bmi/stats/history` - Get BMI history for charts

### Goals
- `GET /api/goals` - Get all goals
- `POST /api/goals` - Create new goal
- `PUT /api/goals/{id}` - Update goal
- `DELETE /api/goals/{id}` - Delete goal

## BMI Categories

| Category | BMI Range | Color |
|----------|-----------|-------|
| Underweight | < 18.5 | Light Blue |
| Normal Weight | 18.5 - 25 | Green |
| Overweight | 25 - 30 | Yellow |
| Obese | > 30 | Red |

## Color Scheme (Soft Pink Theme)

- **Primary**: #FFB6C1 (Light Pink)
- **Secondary**: #FFC0CB (Pink)
- **Accent**: #FF69B4 (Hot Pink)
- **Background**: #FFF0F5 (Lavender Blush)
- **Surface**: #FFFFFF (White)

## Screenshots

The app includes:
1. **Login/Register Screen**: Clean authentication UI
2. **Dashboard**: Current BMI, stats, and trend chart
3. **Calculator**: Input weight/height with real-time calculation
4. **History**: List of all BMI records with delete option
5. **Goals**: Set and track weight goals with progress
6. **Profile**: Account info and statistics

## Testing

### Backend Testing
```bash
# Test registration
curl -X POST http://localhost:3000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'

# Test login
curl -X POST http://localhost:3000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

### Android Testing
1. Run on Android Emulator (API 24+)
2. Or test on physical device (Android 7.0+)

## Troubleshooting

### Backend Connection Issues
- Ensure backend server is running: `npm run dev`
- Check firewall settings for port 3000
- For physical devices, use your computer's IP address instead of 10.0.2.2

### Database Connection Issues
- Verify Neon connection string in `.env`
- Ensure database schema is created
- Check network connectivity

### Build Errors
- Sync Gradle files in Android Studio
- Clean and rebuild project
- Ensure minimum SDK 24 (Android 7.0)

## Future Enhancements

- [ ] Push notifications for goal reminders
- [ ] Export data to CSV/PDF
- [ ] Social sharing of achievements
- [ ] Integration with fitness trackers
- [ ] Dark mode improvements
- [ ] Biometric authentication
- [ ] Multiple user profiles

## License

This project is open source and available for educational purposes.

## Support

For issues or questions:
1. Check the troubleshooting section
2. Review backend logs for API errors
3. Check Logcat for Android errors

---

**Built with ❤️ using Kotlin, Node.js, and Neon PostgreSQL**
