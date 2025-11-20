package com.example.study.lecture.refactoradvancedanswer;

import com.example.study.lecture.refactoradvanced.LoggingDecoratorExamples.ReportService;
import com.example.study.lecture.refactoradvanced.LoggingDecoratorExamples.ReportServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logging Decorator After Answer.
 */
public final class LoggingDecoratorExamplesAnswer {

    private LoggingDecoratorExamplesAnswer() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Decorator 패턴으로 로깅 책임을 분리한 예제.
     * - 기존 ReportService 구현체를 감싸 추가 기능(로깅)을 주입한다.
     */
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
            log.info("Report exported");
        }
    }

    public static class LoggingDemo {
        public static void main(String[] args) {
            ReportService service = new LoggingReportService(new ReportServiceImpl());
            service.generate();
        }
    }
}
