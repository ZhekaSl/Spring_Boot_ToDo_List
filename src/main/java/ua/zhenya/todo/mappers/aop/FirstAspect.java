package ua.zhenya.todo.mappers.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Aspect
@Component
@Slf4j
public class FirstAspect {

    @Pointcut("@within(org.springframework.stereotype.Controller)")
    public void isControllerLayer() {
    }

    @Pointcut("within(ua.zhenya.todo.service.*Service)")
    public void isServiceLayer() {
    }

    @Pointcut("this(org.springframework.stereotype.Repository)")
    /*    @Pointcut("target(org.springframework.stereotype.Repository)")*/
    public void isRepositoryLayer() {
    }

    @Pointcut("isControllerLayer() && @annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void hasGetMapping() {
    }

    @Pointcut("isControllerLayer() && args(org.springframework.ui.Model,..)")
    public void hasModelParam() {
    }

/*    @Pointcut("isControllerLayer() && @args(org.springframework.ui.Model,..)")
    public void hasUserInfoParamAnnotation() {}*/

    @Pointcut("bean(*Service)")
    public void isServiceLayerBean() {
    }

    @Pointcut("execution(public * ua.zhenya.todo.service.*Service.findById(*))")
    public void findByIdServiceMethod() {
    }

    @Pointcut("execution(public * ua.zhenya.todo.service.*Service.findById(*,*))")
    public void log() {
    }

    @Before(value = "log() && args(username, projectId) && this(serviceProxy) && @within(transactional)",
            argNames = "joinPoint,username,projectId,serviceProxy,transactional")
    public void addLogging(JoinPoint joinPoint,
                           String username,
                           String projectId,
                           Object serviceProxy,
                           Transactional transactional) {
        System.out.println("Invoked findById method in class " + serviceProxy + ", with username " + username + " and projectId " + projectId);
    }

    @AfterReturning(value = "log()" +
                            "&& this(service))", returning = "result", argNames = "result,service")
    public void addLoggingAfterReturning(Object result, Object service) {
        log.info("after returning - invoked findById method in class {}, result {}", service, result);
    }

    @AfterThrowing(value = "log()" +
                           "&& target(service))", throwing = "ex", argNames = "ex,service")
    public void addLoggingAfterThrowing(Throwable ex, Object service) {
        log.info("after throwing - invoked findById method in class {}, exception {}: {}", service, ex.getClass(), ex.getMessage());
    }

    @After(value = "log() && target(service))", argNames = "service")
    public void addLoggingAfterFinally(Object service) {
        log.info("after (finally) - invoked findById method in class {}", service);
    }

    @Around("log() && target(service) && args(id, username)")
    public Object addLoggingAround(ProceedingJoinPoint joinPoint, Object service, Object id, Object username) throws Throwable {

        log.info("AROUND before - invoked findById method in class {}", service);
        try {
            Object result = joinPoint.proceed();
            log.info("AROUND after returning - invoked findById method in class {}, result {}", service, result);
            return result;
        } catch (Throwable ex) {
            log.info("AROUND after throwing - invoked findById method in class {}, exception {}: {}", service, ex.getClass(), ex.getMessage());
            throw ex;
        } finally {
            log.info("AROUND after (finally) - invoked findById method in class {}", service);

        }


    }

}
