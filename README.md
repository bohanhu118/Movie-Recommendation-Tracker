# 𝑴𝒐𝒗𝒊𝒆-𝑹𝒆𝒄𝒐𝒎𝒎𝒆𝒏𝒅𝒂𝒕𝒊𝒐𝒏-𝑻𝒓𝒂𝒄𝒌𝒆𝒓

![Java Version](https://img.shields.io/badge/Java-17%2B-blue)
![License](https://img.shields.io/badge/License-MIT-green)
![GUI](https://img.shields.io/badge/GUI-JavaFX-orange)

A comprehensive movie management system that allows users to browse movies, manage watchlists, track viewing history, and receive personalized movie recommendations. Built with object-oriented principles in Java, featuring both Command-Line Interface (CLI) and Graphical User Interface (GUI) versions.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Installation](#installation)
- [Usage Guide](#usage-guide)
- [Recommendation Algorithms](#recommendation-algorithms)
- [Data Storage](#data-storage)
- [Security](#security)
- [Testing](#testing)
- [Ethical Assessment](#ethical-assessment)
- [Contributing](#contributing)
- [License](#license)

## 🎯 Overview

The **Movie Recommendation & Tracker System** is a Java-based application that helps users discover new movies based on their watching preferences. Users can maintain personal watchlists, track their viewing history, and receive intelligent movie recommendations using multiple strategies.

This project demonstrates key object-oriented programming concepts including inheritance, polymorphism, encapsulation, file I/O operations, exception handling, and GUI development with JavaFX.

## ✨ Features

### Core Features

| Feature | Description |
|---------|-------------|
| **User Authentication** | Login system with password hashing and account types |
| **Account Registration** | Create new Basic or Premium user accounts |
| **Browse Movies** | View all movies from the database with details |
| **Watchlist Management** | Add/remove movies to personal watchlist |
| **Viewing History** | Track watched movies with timestamps |
| **Recommendations** | Get personalized movie suggestions |

### Recommendation Strategies

| Strategy | Description | Availability |
|----------|-------------|--------------|
| **By Genre** | Recommends movies based on user's most-watched genres | All users |
| **By Rating** | Recommends highest-rated unwatched movies | Premium only |
| **By Year** | Recommends newest unwatched movies | Premium only |
| **Hybrid** | Combines rating (70%) and recency (30%) | Premium only |

### Advanced Features Implemented

- ✅ User account registration system
- ✅ Password change functionality
- ✅ Multiple recommendation strategies (switchable at runtime)
- ✅ JavaFX Graphical User Interface
- ✅ Subclassing User into BasicUser and PremiumUser
- ✅ Password hashing with salt

## 🛠 Tech Stack

- **Language**: Java 17+
- **GUI Framework**: JavaFX
- **Data Storage**: CSV files
- **Build Tool**: Any Java IDE (IntelliJ IDEA, Eclipse, VS Code)
- **Version Control**: Git

## 📁 Project Structure

```movie-recommendation-system/
├── src/
│   ├── Main/
│   │   ├── MovieSystemCLI.java      # Command-line interface
│   │   └── MovieSystemGUI.java      # JavaFX graphical interface
│   ├── Models/
│   │   ├── Movie.java                # Movie entity
│   │   ├── User.java                 # Base user class
│   │   ├── BasicUser.java            # Basic user subclass
│   │   ├── PremiumUser.java          # Premium user subclass
│   │   ├── Watchlist.java            # Watchlist management
│   │   ├── History.java              # Viewing history management
│   │   └── WatchRecord.java          # Watch record with date
│   ├── Services/
│   │   ├── RecommendationEngine.java # Recommendation algorithms
│   │   ├── CSVHandler.java           # File I/O operations
│   │   └── SaltHash.java             # Password hashing utility
│   └── Resources/
│       ├── movies.csv                # Movie database
│       └── users.csv                 # User data storage
├── docs/
│   └── screenshots/                  # Application screenshots
├── README.md
└── LICENSE```


## 🚀 Installation

### Prerequisites

- Java Development Kit (JDK) 17 or higher
- JavaFX SDK (for GUI version)

### Step-by-Step Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/movie-recommendation-system.git
   cd movie-recommendation-system
