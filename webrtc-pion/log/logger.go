package log

import (
	"fmt"
	"log"
	"os"
)

var Logger = func() *log.Logger {
	// just print raw messages if embedded, will be processed by the java log reader
	if os.Getenv("PION_EMBEDDED") != "" {
		return log.New(os.Stdout, "", log.Lshortfile)
	}
	return log.New(os.Stdout, "sonus-pion ", log.Ldate|log.Ltime|log.Lmicroseconds|log.Lshortfile)
}()

func Printf(format string, v ...any) {
	_ = Logger.Output(2, fmt.Sprintf(format, v...))
}
