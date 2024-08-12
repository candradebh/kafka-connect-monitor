package com.kafka.connect.aspect;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.kafka.connect.services.LogService;

@Aspect
@Component
public class LoggingAspect
{

    @Autowired
    private LogService logService;

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceMethods()
    {
    }

    @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
    public void logAfterThrowing(Exception ex)
    {
        logService.createLog("Service Exception", ex.getMessage() + "\n" + this.getStackTrace(ex));
    }

    private String getStackTrace(Exception ex)
    {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace())
        {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}
