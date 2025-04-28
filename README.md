# FoodApp

A **multi-restaurant food ordering** Spring Boot application where both **customers** and **restaurants** can register.

- **Customers** can browse restaurants, add menu items to their cart, and place orders from multiple restaurants.
- **Restaurants** can register, manage their menu items, and handle incoming orders.

---

## 🚀 Features

- User authentication & registration for **Customers** and **Restaurants**
- Restaurant dashboard:
  - Add/Edit menu items (with images)
  - View & update order statuses (Pending → Confirmed → Delivered)
- Customer dashboard:
  - Browse all restaurants
  - View restaurant menus
  - Add items to cart (with quantity)
  - Place orders with delivery details
  - View past orders
- **Email notifications** via Gmail App Password
- **Payment integration** with Razorpay (test mode)
- Automatic database schema creation via Spring JPA

---

## 🎯 Prerequisites

1. **Java 17** or higher
2. **Maven** 3.x
3. **MySQL** (create a database and a user)
4. **Gmail App Password** (for sending transactional emails)
5. **Razorpay Test Key & Secret** (for payment processing)

---

## ⚙️ Setup & Configuration

1. **Clone the repository**
   ```bash
   git clone https://github.com/tejisandhu/FoodApp.git
   cd FoodApp
   ```

2. **Configure `application.properties`** (located in `src/main/resources`)
   ```properties
   # MySQL
   spring.datasource.url=jdbc:mysql://localhost:3306/<your_db_name>?createDatabaseIfNotExist=true&useSSL=false
   spring.datasource.username=<your_mysql_user>
   spring.datasource.password=<your_mysql_password>

   # JPA
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true

   # Gmail SMTP
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=<your_gmail_address>
   spring.mail.password=<your_app_password>
   spring.mail.properties.mail.smtp.auth=true
   spring.mail.properties.mail.smtp.starttls.enable=true

   # Razorpay
   razorpay.key=<your_test_key_id>
   razorpay.secret=<your_test_secret>
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   Or build a JAR and run:
   ```bash
   mvn clean package
   java -jar target/FoodApp-0.0.1-SNAPSHOT.jar
   ```

4. **Access**
   - Customer portal: `http://localhost:8080/customer/dashboard`
   - Restaurant portal: `http://localhost:8080/restaurant/dashboard`
   - Swagger API docs (if enabled): `http://localhost:8080/swagger-ui.html`

---

## 📦 Project Structure

```
FoodApp/
├── src/
│   ├── main/
│   │   ├── java/com/ribjet/FoodApp/       # Spring Boot app & controllers
│   │   └── resources/
│   │       ├── static/                   # CSS, JS, images
│   │       └── templates/                # Thymeleaf HTML views
│   └── test/                            # Unit & integration tests
├── pom.xml                              # Maven build file
└── README.md                            # This file
```

---

## 🤝 Contributing

1. Fork the repo
2. Create a feature branch: `git checkout -b feature/YourFeature`
3. Commit your changes: `git commit -m 'Add some feature'`
4. Push to branch: `git push origin feature/YourFeature`
5. Open a Pull Request

---

## 📝 License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.

---

*Happy Coding!* 🧑‍🍳
