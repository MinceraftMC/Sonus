package pion

import (
	"log"
	"os"

	"github.com/pion/logging"
)

type SimplePionLoggerFactory struct {
	*log.Logger
	NameSuffix string
}

func (factory *SimplePionLoggerFactory) NewLogger(scope string) logging.LeveledLogger {
	return &SimplePionLogger{
		Logger: factory.Logger,
		Prefix: scope + factory.NameSuffix + ":",
	}
}

type SimplePionLogger struct {
	*log.Logger
	Prefix string
}

var (
	traceLogging = os.Getenv("LOG_TRACE") != ""
	debugLogging = os.Getenv("LOG_DEBUG") != ""
)

func (logger *SimplePionLogger) Trace(msg string) {
	if traceLogging && debugLogging {
		logger.Println("[TRACE]", logger.Prefix, msg)
	}
}

func (logger *SimplePionLogger) Tracef(format string, args ...any) {
	if traceLogging && debugLogging {
		logger.Printf("[TRACE] "+logger.Prefix+" "+format, args...)
	}
}

func (logger *SimplePionLogger) Debug(msg string) {
	if debugLogging {
		logger.Println("[DEBUG]", logger.Prefix, msg)
	}
}

func (logger *SimplePionLogger) Debugf(format string, args ...any) {
	if debugLogging {
		logger.Printf("[DEBUG] "+logger.Prefix+" "+format, args...)
	}
}

func (logger *SimplePionLogger) Info(msg string) {
	logger.Println("[INFO]", logger.Prefix, msg)
}

func (logger *SimplePionLogger) Infof(format string, args ...any) {
	logger.Printf("[INFO] "+logger.Prefix+" "+format, args...)
}

func (logger *SimplePionLogger) Warn(msg string) {
	logger.Println("[WARN]", logger.Prefix, msg)
}

func (logger *SimplePionLogger) Warnf(format string, args ...any) {
	logger.Printf("[WARN] "+logger.Prefix+" "+format, args...)
}

func (logger *SimplePionLogger) Error(msg string) {
	logger.Println("[ERROR]", logger.Prefix, msg)
}

func (logger *SimplePionLogger) Errorf(format string, args ...any) {
	logger.Printf("[ERROR] "+logger.Prefix+" "+format, args...)
}
