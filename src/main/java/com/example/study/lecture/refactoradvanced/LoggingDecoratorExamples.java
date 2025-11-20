package com.example.study.lecture.refactoradvanced;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logging Decorator 리팩토링 예제
 */
public final class LoggingDecoratorExamples {

    private LoggingDecoratorExamples() {
        throw new IllegalStateException("Utility class");
    }

    public interface ReportService {
        String generate();

        void export();
    }

    public static class ReportServiceImpl implements ReportService {
        private static final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);

        @Override
        public String generate() {
            log.debug("start generate");
            String result = "report";
            log.debug("end generate");
            return result;
        }

        @Override
        public void export() {
            log.debug("export start");
            // export logic
            log.debug("export end");
        }
    }

    public static class LoggingReportService implements ReportService {
        private static final Logger log = LoggerFactory.getLogger(LoggingReportService.class);

        private final ReportService delegate;

        public LoggingReportService(ReportService delegate) {
            this.delegate = delegate;
        }

        @Override
        public String generate() {
            log.info("Generating report...");
            String report = delegate.generate();
            log.info("Report generated");
            return report;
        }

        @Override
        public void export() {
            log.info("Exporting report...");
            delegate.export();
            log.info("Export complete");
        }
    }
}
