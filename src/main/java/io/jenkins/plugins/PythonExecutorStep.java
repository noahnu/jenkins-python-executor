package jenkins.plugins.python;

import com.google.common.collect.ImmutableSet;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.Process;
import java.lang.ProcessBuilder;
import java.util.logging.Logger;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import javax.annotation.Nonnull;

import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.Util;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class PythonExecutorStep extends Step {
    private static final Logger logger = Logger.getLogger(PythonExecutorStep.class.getName());

    private String script;
    private String file;

    @DataBoundConstructor
    public PythonExecutorStep() {
    }

    @DataBoundSetter
    public void setScript(String script) {
        this.script = Util.fixEmpty(script);
    }

    public String getScript() {
        return this.script;
    }

    @DataBoundSetter
    public void setFile(String file) {
        this.file = Util.fixEmpty(file);
    }

    public String getFile() {
        return this.file;
    }

    @Override
    public StepExecution start(StepContext context) {
        return new PythonExecutorExecution(this, context);
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {
        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return ImmutableSet.of(Run.class, TaskListener.class);
        }

        @Override
        public String getFunctionName() {
            return "pythonExecutor";
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Python Executor";
        }
    }

    public static class PythonExecutorExecution extends SynchronousNonBlockingStepExecution<PythonExecutionResponse> {
        private static final long serialVersionUID = 1L;
        private transient final PythonExecutorStep step;

        PythonExecutorExecution(PythonExecutorStep step, StepContext context) {
            super(context);
            this.step = step;
        }

        @Override
        protected PythonExecutionResponse run() throws Exception {
            Jenkins jenkins = Jenkins.get();
            Run run = getContext().get(Run.class);
            TaskListener listener = getContext().get(TaskListener.class);
            Objects.requireNonNull(listener, "Listener is mandatory here");

            listener.getLogger().println("Executing script...");

            File tempDir = Util.createTempDir();
            try {
                ProcessBuilder builder = new ProcessBuilder();
                builder.command("python3");
                builder.directory(tempDir);

                Process process = builder.start();
                OutputStream stdin = process.getOutputStream();
                InputStream stdout = process.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));

                writer.write(this.step.getScript());
                writer.flush();
                writer.close();

                Scanner scanner = new Scanner(stdout);
                while (scanner.hasNextLine()) {
                    listener.getLogger().println(scanner.nextLine());
                }

                int ret = process.waitFor();
            } finally {
                tempDir.delete();
            }

            listener.getLogger().println("Script executed.");

            PythonExecutionResponse response = null;
            return response;
        }
    }
}