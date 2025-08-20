package com.instancify.scriptify.ts.swc.graalvm.script;

import com.caoccao.javet.swc4j.Swc4j;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.options.Swc4jTranspileOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jTranspileOutput;
import com.instancify.scriptify.api.exception.ScriptException;
import com.instancify.scriptify.api.script.Script;
import com.instancify.scriptify.api.script.ScriptObject;
import com.instancify.scriptify.api.script.constant.ScriptConstant;
import com.instancify.scriptify.api.script.constant.ScriptConstantManager;
import com.instancify.scriptify.api.script.function.ScriptFunctionManager;
import com.instancify.scriptify.api.script.function.definition.ScriptFunctionDefinition;
import com.instancify.scriptify.api.script.security.ScriptSecurityManager;
import com.instancify.scriptify.core.script.constant.StandardConstantManager;
import com.instancify.scriptify.core.script.function.StandardFunctionManager;
import com.instancify.scriptify.core.script.security.StandardSecurityManager;
import com.instancify.scriptify.js.graalvm.script.JsFunction;
import com.instancify.scriptify.js.graalvm.script.JsSecurityClassAccessor;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TsScript implements Script<Value> {

    private final ScriptSecurityManager securityManager = new StandardSecurityManager();
    private ScriptFunctionManager functionManager = new StandardFunctionManager();
    private ScriptConstantManager constantManager = new StandardConstantManager();
    private final List<String> extraScript = new ArrayList<>();

    @Override
    public ScriptConstantManager getConstantManager() {
        return constantManager;
    }

    @Override
    public ScriptSecurityManager getSecurityManager() {
        return securityManager;
    }

    @Override
    public ScriptFunctionManager getFunctionManager() {
        return functionManager;
    }

    @Override
    public void setFunctionManager(ScriptFunctionManager functionManager) {
        this.functionManager = Objects.requireNonNull(functionManager, "functionManager cannot be null");
    }

    @Override
    public void setConstantManager(ScriptConstantManager constantManager) {
        this.constantManager = Objects.requireNonNull(constantManager, "constantManager cannot be null");
    }

    @Override
    public void addExtraScript(String script) {
        this.extraScript.add(script);
    }

    @Override
    public Value eval(String script) throws ScriptException {
        Context.Builder builder = Context.newBuilder("js")
                .allowHostAccess(HostAccess.newBuilder(HostAccess.ALL)
                        // Mapping for the ScriptObject class required
                        // to convert a ScriptObject to the value it contains
                        .targetTypeMapping(
                                ScriptObject.class,
                                Object.class,
                                object -> true,
                                ScriptObject::getValue
                        )
                        .build());

        // If security mode is enabled, search all exclusions
        // and add the classes that were excluded to JsSecurityClassAccessor
        if (securityManager.getSecurityMode()) {
            builder.allowHostClassLookup(new JsSecurityClassAccessor(securityManager.getExcludes()));
        } else {
            builder.allowHostClassLookup(className -> true);
        }

        Context context = builder.build();

        Value bindings = context.getBindings("js");

        for (ScriptFunctionDefinition definition : functionManager.getFunctions().values()) {
            bindings.putMember(definition.getFunction().getName(), new JsFunction(this, definition));
        }

        for (ScriptConstant constant : constantManager.getConstants().values()) {
            bindings.putMember(constant.getName(), constant.getValue());
        }

        // Building full script including extra script code
        StringBuilder fullScript = new StringBuilder();
        for (String extra : extraScript) {
            fullScript.append(extra).append("\n");
        }
        fullScript.append(script);

        try {
            // Transpile TypeScript to JavaScript and evaluate it
            Swc4j swc = new Swc4j();
            Swc4jTranspileOptions options = new Swc4jTranspileOptions().setMediaType(Swc4jMediaType.TypeScript);
            Swc4jTranspileOutput output = swc.transpile(fullScript.toString(), options);
            return context.eval("js", output.getCode());
        } catch (Exception e) {
            throw new ScriptException(e);
        } finally {
            context.close();
        }
    }
}
