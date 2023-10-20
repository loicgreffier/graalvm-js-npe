package com.example.demo;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@RestController
@RequestMapping("/run")
public class DemoController {

    @GetMapping
    public String run() throws IOException {
        try (OutputStream output = new ByteArrayOutputStream()) {
            try (Context context = Context.newBuilder("js")
                    .out(output)
                    .err(output)
                    .options(Map.of("engine.WarnInterpreterOnly", "false"))
                    .allowHostAccess(HostAccess.ALL)
                    .build()) {

                Value bindings = context.getBindings("js");
                context.eval("js", "function run() { return '{\"key\": \"value\"}'; }");

                Value runFunction = bindings.getMember("run");

                if (runFunction == null) {
                    throw new RuntimeException("No run function defined");
                }

                return runFunction.execute().asString();
            }
        }
    }
}
