const express = require("express")
const jwt = require("jsonwebtoken")
const logger = require("./logger.js")
const mysql = require("mysql2")
const app = express()

const port = 3000;

app.use(express.json())

// Parse JSON request bodies
app.use(express.json());

// Parse URL-encoded form data
app.use(express.urlencoded({ extended: false }));

// check token expiration
const currenttimestamp = Math.floor(Date.now() / 1000); // current time in seconds

// Create a MySQL connection pool
const db = mysql.createPool({
  host: "localhost",
  user: "smaug",
  password: "1234",
  database: "simpleLogin", // Use the name of your database
});

const secretKey = "SUPER_SECRET";
const tokenExpiration = "60s"; // Token expiration time, e.g., 1 hour

// Middleware to verify JWT token
function verifyToken(req, res, next) {
    const token = req.headers["authorization"];
  
    if (!token) {
      return res.status(401).json({ message: "Token not provided" });
    }
  
    jwt.verify(token, secretKey, (err, decoded) => {
      if (err) {
        return res.status(401).json({ message: "Invalid token" });
      }
  
      // Check token expiration
      const currentTimestamp = Math.floor(Date.now() / 1000); // Current time in seconds
      if (decoded.exp <= currentTimestamp) {
        return res.status(401).json({ message: "Token has expired" });
      }
  
      req.user = decoded;
      next();
    });
}
// POST /login endpoint
app.post("/login", (req, res) => {
  const { username, password } = req.body;

  // Authenticate user against the database
  db.query(
    "SELECT * FROM users WHERE username = ? AND password = ?",
    [username, password],
    (err, results) => {
      if (err) {
        logger.error("Database error:", err);
        return res.status(500).json({ message: "Database error" });
      }

      if (results.length === 1) {
        // User authentication successful
        const token = jwt.sign({ username }, secretKey, { expiresIn: tokenExpiration });

        logger.info(`User ${username} logged in successfully`);
        logger.info(`Generated JWT token: ${token}`);

        res.json({ message: "Login successful", token });
      } else {
        // User authentication failed
        logger.warn(`Login failed for user ${username}`);
        res.status(401).json({ message: "Login failed" });
      }
    }
  );
});

// Sign-up route
app.post("/signup", (req, res) => {
  const { username, password } = req.body;

  // Check if the username already exists in the database
  db.query(
    "SELECT * FROM users WHERE username = ?",
    [username],
    (err, results) => {
      if (err) {
        logger.error("Database error:", err);
        return res.status(500).json({ message: "Database error" });
      }

      if (results.length > 0) {
        // Username already exists
        return res.status(400).json({ message: "User already exists" });
      } else {
        // Insert the new user into the database
        db.query(
          "INSERT INTO users (username, password) VALUES (?, ?)",
          [username, password],
          (err) => {
            if (err) {
              logger.error("Database error:", err);
              return res.status(500).json({ message: "Database error" });
            }

            // User was successfully inserted
            logger.info(`New user ${username} registered`);
            res.json({ message: "Registration successful" });
          }
        );
      }
    }
  );
});

// Protected route that requires a valid token
app.get("/protected", verifyToken, (req, res) => {
    // If the middleware (verifyToken) passed, the token is valid
    res.json({ message: "Current time is " + new Date().toISOString() });
  });


app.listen(port, () => {
  logger.info(`Server is running on port ${port}`);
});