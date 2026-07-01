package com.badminton.aspect;

import com.badminton.dto.BookingDTO;
import com.badminton.dto.request.BookingRequest;
import com.badminton.repository.CourtRepository;
import com.badminton.repository.TimeSlotRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    private final CourtRepository courtRepository;
    private final TimeSlotRepository timeSlotRepository;

    public LoggingAspect(CourtRepository courtRepository, TimeSlotRepository timeSlotRepository) {
        this.courtRepository = courtRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    
    @AfterReturning(
            pointcut = "execution(* com.badminton.service.BookingService.createBooking(..))",
            returning = "result"
    )
    public void logBookingSuccess(JoinPoint joinPoint, Object result) {
        if (result instanceof BookingDTO) {
            BookingDTO booking = (BookingDTO) result;
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = (auth != null) ? auth.getName() : "Anonymous";
            String courtName = courtRepository.findById(booking.getCourtId())
                    .map(c -> c.getName())
                    .orElse("Unknown");
            String timeSlot = timeSlotRepository.findById(booking.getTimeSlotId())
                    .map(t -> t.getLabel())
                    .orElse("Unknown");

            log.info("[AUDIT - SUCCESS] Customer {} đặt thành công Sân {}, ngày {}, khung giờ {}",
                    email, courtName, booking.getBookingDate(), timeSlot);
        }
    }

    
    @AfterThrowing(
            pointcut = "execution(* com.badminton.service.BookingService.createBooking(..))",
            throwing = "ex"
    )
    public void logBookingFailure(JoinPoint joinPoint, Throwable ex) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof BookingRequest) {
            BookingRequest request = (BookingRequest) args[0];
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = (auth != null) ? auth.getName() : "Anonymous";

            log.info("[AUDIT - FAILED] Customer {} cố đặt Sân {} nhưng thất bại. Lỗi: {}",
                    email, request.getCourtId(), ex.getMessage());
        }
    }

    
    @Around("execution(* com.badminton.service..*(..))")
    public Object logPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - start;
            String methodName = joinPoint.getSignature().getName();
            log.info("[PERF] Method: {} | Duration: {}ms", methodName, duration);
        }
    }
}
