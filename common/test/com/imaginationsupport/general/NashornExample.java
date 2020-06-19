package com.imaginationsupport.general;

import javax.script.*;

import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;
import jdk.nashorn.api.scripting.ClassFilter;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

import java.security.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static javax.script.ScriptContext.ENGINE_SCOPE;


public class NashornExample {

    public class NoJavaAccessFilter implements ClassFilter{
        @Override
        public boolean exposeToScripts(String s) {
            return false;
        }
    }

    private static class RestrictedAccessControlContext {
        private static final AccessControlContext INSTANCE;
        static {
            INSTANCE = new AccessControlContext(
                    new ProtectionDomain[] {
                            new ProtectionDomain(null, null) // No permissions
                    });
        }
    }

    public void runMe(){
//        ScriptEngineManager engineManager =
//                new ScriptEngineManager();
//        ScriptEngine engine =
//                engineManager.getEngineByName("nashorn");

        NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
        ScriptEngine engine = factory.getScriptEngine(new NoJavaAccessFilter());
        try {
            String result = AccessController.doPrivileged(
                    new PrivilegedExceptionAction<String>() {
                        @Override
                        public String run() throws Exception {
                            engine.eval("function sum(a, b) { return a + b; }");
                            return (engine.eval("sum(1,2);").toString());
                        }
                    },
                    RestrictedAccessControlContext.INSTANCE);
            //System.out.println(result);
        } catch (PrivilegedActionException e) {
            System.err.println(e);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
        }
    }

    public class TestObject{
        int a=0;
        int b=1;
        int c=2;
        String d="This is another test";
    }

    public void runMe2(){
        NashornSandbox sandbox = NashornSandboxes.create();
        sandbox.allowExitFunctions(false);
        sandbox.allowGlobalsObjects(false);
        sandbox.allowReadFunctions(false);
        sandbox.allowPrintFunctions(false);
        sandbox.disallowAllClasses();
        sandbox.setMaxCPUTime(1000); // 1 second of CPU time
        sandbox.setMaxMemory(1024*1024); // 1m max
        sandbox.setMaxPreparedStatements(10);
        ExecutorService exec=Executors.newSingleThreadExecutor();
        sandbox.setExecutor(exec);

        sandbox.inject("test_num", 3.0 );
        sandbox.inject("test_text", "this is a test");
        sandbox.inject("test_bool", true);

        try {
            String result = AccessController.doPrivileged(
                    new PrivilegedExceptionAction<String>() {
                        @Override
                        public String run() throws Exception {
                            sandbox.eval("function sum(a, b) { return a + b; }");
                            sandbox.eval("function outstuff(){" +
                                    "return test_num + test_text + test_bool;}");
                            String value= (sandbox.eval("sum(test_num,2);").toString()
                                + sandbox.eval("outstuff();"));
                            return value;
                        }
                    },
                    RestrictedAccessControlContext.INSTANCE);

            //System.out.println(result);
            exec.shutdown();
            try {
                if (!exec.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    exec.shutdownNow();
                }
            } catch (InterruptedException e) {
                exec.shutdownNow();
            }
        } catch (PrivilegedActionException e) {
            System.err.println(e);
            e.printStackTrace();
        } catch (ScriptCPUAbuseException e){
            System.err.println(e);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println(e);
         e.printStackTrace();
        }

    }

    private NashornSandbox sandbox=null;
    private boolean set=false;

    public void runMe3(ExecutorService exec){
        if (sandbox==null) {
            sandbox = NashornSandboxes.create();
            sandbox.allowExitFunctions(false);
            sandbox.allowGlobalsObjects(false);
            sandbox.allowReadFunctions(false);
            sandbox.allowPrintFunctions(false);
            sandbox.disallowAllClasses();
            sandbox.setMaxCPUTime(1000); // 1 second of CPU time
            sandbox.setMaxMemory(1024 * 1024); // 1m max
            sandbox.setMaxPreparedStatements(10);
            sandbox.setExecutor(exec);
        }

        ScriptContext scriptContext=new SimpleScriptContext();

        Bindings binding=new SimpleBindings();

        binding.put("test_num", 3.0 );
        binding.put("test_text", "this is a test");
        binding.put("test_bool", true);
        binding.put("test_obj", new TestObject());
        scriptContext.setBindings(binding,ENGINE_SCOPE);
//        sandbox.inject("test_num", 3.0 );
//        sandbox.inject("test_text", "this is a test");
//        sandbox.inject("test_bool", true);

        try {
            String result = AccessController.doPrivileged(
                    new PrivilegedExceptionAction<String>() {
                        @Override
                        public String run() throws Exception {
                            if (!set){
                                sandbox.eval("test=\"this is a test\"",scriptContext);
                                set=true;
                            }
                            sandbox.eval("print (test_obj.a+test_obj.b+test_obj.c)",scriptContext);
                            System.out.print(sandbox.eval("print(test);",scriptContext)+" ");
                            sandbox.eval("function sum(a, b) { return a + b; }",scriptContext);
                            sandbox.eval("function outstuff(){" +
                                    "return test_num + test_text + test_bool;}",scriptContext);
                            String value= (sandbox.eval("sum(test_num,2);",scriptContext).toString()
                                    + sandbox.eval("outstuff();",scriptContext));
                            return value;
                        }
                    },
                    RestrictedAccessControlContext.INSTANCE);
            System.out.println(result);
            System.out.println(scriptContext.getAttribute("test"));
            Bindings nashorn_global = (Bindings) scriptContext.getAttribute("nashorn.global");
            System.out.println( nashorn_global.get("test") );

        } catch (PrivilegedActionException e) {
            System.err.println(e);
           // e.printStackTrace();
        } catch (ScriptCPUAbuseException e){
            System.err.println(e);
           // e.printStackTrace();
        } catch (Exception e) {
            System.err.println(e);
          //  e.printStackTrace();
        }

    }

    public static void main(String... args) throws Throwable {
        NashornExample n=new NashornExample();

        {
            System.out.println("Running Direct Nashorn...");
            long startTime=System.currentTimeMillis();
            for(int i=0;i<10;i++)
                n.runMe();
            long elapseTime=System.currentTimeMillis()-startTime;
            System.out.println ("time: "+elapseTime+" ms");
        }

        {
            System.out.println("Running Sandboxed Nashorn...");
            long startTime=System.currentTimeMillis();
            for(int i=0;i<10;i++)
                n.runMe2();
            long elapseTime=System.currentTimeMillis()-startTime;
            System.out.println ("time: "+elapseTime+" ms");
        }

        {
            System.out.println("Running Thread Pooled Sandboxed Nashorn...");
            long startTime=System.currentTimeMillis();
            ExecutorService exec=Executors.newFixedThreadPool(1);

            for(int i=0;i<10;i++)
                n.runMe3(exec);

            exec.shutdown();
            try {
                if (!exec.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    exec.shutdownNow();
                }
            } catch (InterruptedException e) {
                exec.shutdownNow();
            }
            long elapseTime=System.currentTimeMillis()-startTime;

            System.out.println ("time: "+elapseTime+" ms");

        }


    }
}
