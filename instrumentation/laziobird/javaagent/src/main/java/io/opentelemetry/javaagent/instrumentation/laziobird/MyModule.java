package io.opentelemetry.javaagent.instrumentation.laziobird;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.instrumentation.TypeInstrumentation;
import java.util.Collections;
import java.util.List;
@AutoService(InstrumentationModule.class)
public class MyModule extends InstrumentationModule {
  public MyModule() {
    // 此处定义的是组件的名称，以及组件的别名，会在配置组件的开关时使用
    super("laziobird", "laziobird-1.0");
  }

  @Override
  public List<TypeInstrumentation> typeInstrumentations() {
    // 组件内包含的TypeInstrumentation，是一个list，
    // 我们添加一个实现的Instrument到 InstrumentationModule
    return Collections.singletonList(new Myinstrument());
  }
}
