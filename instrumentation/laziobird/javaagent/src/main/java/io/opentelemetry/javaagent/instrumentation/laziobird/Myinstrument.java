package io.opentelemetry.javaagent.instrumentation.laziobird;

import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

public class Myinstrument implements TypeInstrumentation {
  @Override
  public ElementMatcher<TypeDescription> typeMatcher() {
    return nameStartsWith("com.observable.trace.otel.controller");
  }

  @Override
  public void transform(TypeTransformer transformer) {
    transformer.applyAdviceToMethod(
        isMethod()
            .and(isPublic()),
        // 可以细分方法  .and(named("test")),
        this.getClass().getName() + "$LaziobirdAdvice");
  }

  /**
   * com.observable.trace.otel.controller 下所有方法，都会被 AOP 方式拦拦截进 LaziobirdAdvice 这个 Advice
   */
  @SuppressWarnings("unused")
  public static class LaziobirdAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void methodEnter(@Advice.Origin String method,@Advice.AllArguments Object[] allArguments,
        @Advice.Local("laziobirdStartTime") long startTime) {
      System.out.println();
      System.out.println("["+method.toString()+"] ------------> methodEnter start ! ------------>");
      // Around Advice 打印方法所有入参 AllArguments
      if (allArguments != null) {
        for (Object a : allArguments
        ) {
          System.out.println(
              "- - - type : " + a.getClass().getName() + ", value : " + a.toString());
        }
      }
      System.out.println(" method methodEnter | local var laziobirdStartTime : " + startTime);
      if (startTime <= 0) {
        startTime = System.currentTimeMillis();
      }

      //从Span中获取方法开始时间
      Span span = Span.current();
      System.out.println(
          "OnMethodEnter traceId:" + span.getSpanContext().getTraceId() + " | spanId:"
              + span.getSpanContext().getSpanId());
      span.setAttribute("tagTime", System.currentTimeMillis());
      System.out.println("["+method.toString()+"] ------------> methodEnter end ! ------------>");
      System.out.println();
    }

    @Advice.OnMethodExit(suppress = Throwable.class)
    public static void methodExit(@Advice.Origin String method,@Advice.Local("laziobirdStartTime") long startTime) {
      System.out.println();
      System.out.println("["+method.toString()+"] <------------ methodExit start ! <------------ ");
      //从Span中获取方法开始时间
      Span span = Span.current();
      System.out.println(
          "OnMethodEnter traceId:" + span.getSpanContext().getTraceId() + " | spanId:"
              + span.getSpanContext().getSpanId());
      //通过 Advice.Local 拿到时间戳，统计 method 执行时间
      System.out.println(method.toString()+ " method cost time :" + (System.currentTimeMillis() - startTime) + " ms ");
      System.out.println("["+method.toString()+"] <------------ methodExit end ! <------------ ");
      System.out.println();
    }
  }
}
