const express = require('express');
const cors = require('cors'); // Import the cors middleware
const app = express();
const port = 3000;

// Use the cors middleware
app.use(cors());

// Define a route
app.get('/', (req, res) => {
  res.send('Hello, World!');
});

app.listen(port, () => {
  console.log(`Server is running on port ${port}`);
});