package ru.yandex.practicum.filmorate.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.time.Instant;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class FeedAspect {
    private static final String FRIEND = "FRIEND";
    private static final String REVIEW = "REVIEW";
    private static final String LIKE = "LIKE";
    private static final String ADD = "ADD";
    private static final String REMOVE = "REMOVE";
    private static final String UPDATE = "UPDATE";

    private final ReviewService reviewService;

    private final FeedService feedService;


    @Pointcut("@annotation(Feed)")
    public void feedPointcut() {

    }


    @Around("feedPointcut()")
    public Object feedableMethodCallsAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Event event = new Event();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String name = signature.getName().toUpperCase();
        if (name.contains(FRIEND)) {
            event.setEventType(FRIEND);
            setIdsFriendEvent(event, joinPoint);
        } else if (name.contains(LIKE) && !name.contains(REVIEW)) {
            event.setEventType(LIKE);
            setIdsLikeEvent(event, joinPoint);
        } else if (name.contains(REVIEW) && !name.contains(LIKE)) {
            event.setEventType(REVIEW);
            setIdsReviewEvent(event, joinPoint);
        } else {
            log.info("Aspect don't find eventType");
        }

        Object ret = joinPoint.proceed();

        if (name.contains(ADD)) {
            event.setOperation(ADD);
        } else if (name.contains("DELETE")) {
            event.setOperation(REMOVE);
        } else if (name.contains(UPDATE)) {
            event.setOperation(UPDATE);
        } else {
            log.info("Aspect don't find operation");
        }

        if (event.getEntityId() == null && ret instanceof Review) {
            Review review = (Review) ret;
            event.setEntityId(review.getReviewId());
        }

//      в постмане кривые тесты, костыль
        if (event.getOperation().equals(UPDATE) && event.getEventType().equals(REVIEW)) {
            Review review = reviewService.getReview(event.getEntityId());
            event.setUserId(review.getUserId());
        }

        event.setTimestamp(Instant.now().toEpochMilli());

        feedService.addEvent(event);
        return ret;
    }

    private void setIdsLikeEvent(Event event, JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        event.setUserId((Integer) args[1]);
        event.setEntityId((Integer) args[0]);
    }

    private void setIdsReviewEvent(Event event, JoinPoint joinPoint) {
        Object arg = joinPoint.getArgs()[0];
        Review review = null;
        if (arg instanceof Review) {
            review = (Review) arg;
        } else {
            Integer reviewId = (Integer) arg;
            review = reviewService.getReview(reviewId);
        }
        event.setUserId(review.getUserId());
        event.setEntityId(review.getReviewId());
    }

    private void setIdsFriendEvent(Event event, JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        event.setUserId((Integer) args[0]);
        event.setEntityId((Integer) args[1]);
    }
}
