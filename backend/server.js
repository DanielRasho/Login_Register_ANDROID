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
// Middleware to verify JWT token
function tokenTimeLeft(req) {
    const token = req.headers["authorization"];
    let tokenTimeLeft = 0;
  
    if (!token) {
      return res.status(401).json({ message: "Token not provided" });
    }
  
    jwt.verify(token, secretKey, (err, decoded) => {
      if (err) {
        return res.status(401).json({ message: "Invalid token" });
      }
  
      // Check token expiration
      const currentTimestamp = Math.floor(Date.now() / 1000); // Current time in seconds
      tokenTimeLeft = decoded.exp - currentTimestamp
    });
    return tokenTimeLeft;
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
        return res.status(500).json({ message: "Database error" }); // STATUS : 500 Server error
      }

      if (results.length === 1) {
        // User authentication successful
        const token = jwt.sign({ username }, secretKey, { expiresIn: tokenExpiration });

        logger.info(`User ${username} logged in successfully`);
        logger.info(`Generated JWT token ${username}: ${token}`);

        res.json({ message: "Login successful", token });
      } else {
        // User authentication failed
        logger.warn(`Login failed for user ${username}`);
        res.status(401).json({ message: "Wrong username or password" }); // STATUS : 401 Unauthorized
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
        return res.status(500).json({ message: "Database error" }); // STATUS : 500 Server error
      }

      if (results.length > 0) {
        // Username already exists
        return res.status(409).json({ message: "User already exists" }); // STATUS : 409 Conflict with the current DB state.
      } else {
        // Insert the new user into the database
        db.query(
          "INSERT INTO users (username, password) VALUES (?, ?)",
          [username, password],
          (err) => {
            if (err) {
              logger.error("Database error:", err);
              return res.status(500).json({ message: "Database error" }); // Status : 500 Server error
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
app.get("/home", verifyToken, (req, res) => {
    // If the middleware (verifyToken) passed, the token is valid
    res.json({ message: "Your session will end in:" + tokenTimeLeft(req) });
  });


app.listen(port, () => {
  logger.info(`Server is running on port ${port}`);
});