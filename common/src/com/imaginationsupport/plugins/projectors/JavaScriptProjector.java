package com.imaginationsupport.plugins.projectors;

import com.imaginationsupport.data.features.FeatureMap;
import com.imaginationsupport.plugins.ScriptProjector;
import com.imaginationsupport.plugins.features.DecimalFeature;
import com.imaginationsupport.plugins.features.IntegerFeature;
import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import delight.nashornsandbox.exceptions.ScriptCPUAbuseException;

import javax.script.Bindings;
import javax.script.ScriptContext;
import java.security.*;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class JavaScriptProjector extends ScriptProjector {

	public static final String NAME="JavaScript Projector";
	private static NashornSandbox sandbox=null;
	private ExecutorService exec=null;

	public JavaScriptProjector() {
		setLanguage("JavaScript");
	}

	public JavaScriptProjector(ExecutorService exec) {
		setLanguage("JavaScript");
		setExecutorService(exec);
	}

	public JavaScriptProjector(String script) {
		setLanguage("JavaScript");
		setScript(script);
	}

	public void setExecutorService(ExecutorService exec){
		this.exec=exec;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getHelpText() {
		return "Executes JavaScript to determine the next value of a feature.";
	}

	@Override
	public String getAboutText() {
		return "";
	}

	@Override
	public Set<String> applicableFor() {
		Set<String> out=new HashSet<String>();
		out.add(DecimalFeature.class.getCanonicalName());
		out.add(IntegerFeature.class.getCanonicalName());
		return out;
	}

	@Override
	public String project(FeatureMap map, ScriptContext scriptContext) {
		String result="";
		String script=getScript();
		boolean localExec=false;
		synchronized (this){
			if(exec==null){
				exec=Executors.newSingleThreadExecutor();
			}

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

			try {
				result = AccessController.doPrivileged(
						new PrivilegedExceptionAction<String>() {
							@Override
							public String run() throws Exception {
								String value= sandbox.eval(script,scriptContext).toString();
								return value;
							}
						}, RestrictedAccessControlContext.INSTANCE);
				System.out.println(result);
//				Bindings nashorn_global = (Bindings) scriptContext.getAttribute("nashorn.global");
//				System.out.println( nashorn_global.get("test") );

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

			if(localExec){
				exec.shutdown();
				try {
					if (!exec.awaitTermination(800, TimeUnit.MILLISECONDS)) {
						exec.shutdownNow();
					}
				} catch (InterruptedException e) {
					exec.shutdownNow();
				}
				sandbox=null;
			}

			if (map.getType() instanceof IntegerFeature) {
				return Integer.parseInt(result)+"";
			}
			if(map.getType() instanceof DecimalFeature){
				return Double.parseDouble(result)+"";
			}
			return result;
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

	@Override
	public String getPluginJavaScriptSourceUriPath()
	{
		return "/js/plugins/projectors/javascriptProjector.js";
	}

}
