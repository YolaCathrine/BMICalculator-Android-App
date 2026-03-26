# 🚀 Push to GitHub - Step by Step

## ✅ Yang Sudah Dilakukan

- ✅ Git initialized
- ✅ Config: user.name = "YolaCathrine"
- ✅ Config: user.email = "yolacathrine18@gmail.com"
- ✅ All files added
- ✅ Initial commit created

---

## 📋 LANGKAH PUSH KE GITHUB

### **Step 1: Buat Repository di GitHub**

1. Buka https://github.com
2. Login ke account GitHub Anda
3. Klik tombol **"+"** di pojok kanan atas
4. Pilih **"New repository"**
5. Isi:
   - **Repository name**: `BMICalculator1`
   - **Description**: `BMI Calculator Android App with Neon Database`
   - **Public** atau **Private** (sesuai preference)
   - ❌ **JANGAN** centang "Initialize this repository with a README"
6. Klik **"Create repository"**

---

### **Step 2: Copy Remote URL**

Setelah repository dibuat, GitHub akan menampilkan halaman dengan command. Copy URL dari bagian:

```
git remote add origin https://github.com/YolaCathrine/BMICalculator1.git
```

Atau jika menggunakan SSH:
```
git remote add origin git@github.com:YolaCathrine/BMICalculator1.git
```

---

### **Step 3: Add Remote & Push**

Jalankan command ini di terminal:

```bash
# Di folder project
cd C:\Users\yolac\AndroidStudioProjects\BMICalculator1

# Add remote repository
git remote add origin https://github.com/YolaCathrine/BMICalculator1.git

# Verify remote
git remote -v

# Push ke GitHub
git push -u origin master
```

**ATAU** jika menggunakan branch `main`:
```bash
# Rename branch jika perlu
git branch -M main

# Push ke main
git push -u origin main
```

---

### **Step 4: Verifikasi**

1. Buka halaman repository GitHub Anda
2. Refresh halaman
3. Anda akan melihat semua file sudah ter-upload!

---

## 🔧 TROUBLESHOOTING

### Error: "remote: Repository not found"

**Solusi:**
- Pastikan repository sudah dibuat di GitHub
- Check URL remote: `git remote -v`
- Pastikan nama repository benar (case-sensitive)

### Error: "Authentication failed"

**Solusi 1 - Menggunakan GitHub Token:**
```bash
# Saat push, gunakan Personal Access Token sebagai password
Username: YolaCathrine
Password: <your_github_token>
```

**Cara membuat token:**
1. GitHub Settings > Developer settings > Personal access tokens
2. Generate new token (classic)
3. Select scopes: `repo`, `workflow`
4. Generate token
5. Copy token dan gunakan sebagai password

**Solusi 2 - Menggunakan SSH:**
```bash
# Generate SSH key
ssh-keygen -t ed25519 -C "yolacathrine18@gmail.com"

# Add SSH key ke GitHub
# Settings > SSH and GPG keys > New SSH key

# Change remote to SSH
git remote set-url origin git@github.com:YolaCathrine/BMICalculator1.git

# Push lagi
git push -u origin master
```

### Error: "Updates were rejected because the remote contains work that you do not have"

**Solusi:**
```bash
# Force push (hati-hati, ini akan overwrite remote)
git push -u origin master --force

# ATAU pull dulu lalu push
git pull origin master
git push -u origin master
```

---

## 📝 UPDATE SETELAH PUSH

Setelah pertama kali push, untuk update selanjutnya:

```bash
# Setelah ada perubahan
git add .
git commit -m "Your commit message"
git push origin master
```

---

## 🎯 QUICK COMMAND REFERENCE

```bash
# Check status
git status

# Check log
git log --oneline

# Add remote
git remote add origin https://github.com/YolaCathrine/BMICalculator1.git

# Check remote
git remote -v

# Push
git push -u origin master

# Pull
git pull origin master

# Change branch
git checkout -b feature-branch

# Push branch lain
git push origin feature-branch
```

---

## ✅ CHECKLIST

- [ ] Repository dibuat di GitHub
- [ ] Remote URL di-copy
- [ ] `git remote add origin` dijalankan
- [ ] `git push -u origin master` berhasil
- [ ] File terlihat di GitHub

---

**Selamat! Repository Anda sudah online!** 🎉
