const winston = require('winston');

// Define custom colors for log levels
const customColors = {
  info: 'green',
  warn: 'yellow',
  error: 'red',
};

// Create a logger with custom colorization
const logger = winston.createLogger({
  level: 'info',
  format: winston.format.combine(
    winston.format.colorize({ all: false, colors: customColors }), // Add custom colorization
    winston.format.simple()
  ),
  transports: [new winston.transports.Console()],
});

logger.info('This is an info message');
logger.warn('This is a warning message');
logger.error('This is an error message');

module.exports = logger