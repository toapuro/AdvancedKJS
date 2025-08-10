package dev.toapuro.advancedkjs.coremod;

import com.sun.tools.attach.VirtualMachine;
import dev.toapuro.advancedkjs.AdvancedKJS;
import dev.toapuro.advancedkjs.util.UnsafeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.util.Objects;

public class CoreModUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoreModUtil.class);
    
    public static void runAgent() {
        LOGGER.info("Starting agent");

        // Allow attaching self
        try {
            Field f = Class.forName("sun.tools.attach.HotSpotVirtualMachine").getDeclaredField("ALLOW_ATTACH_SELF");
            Unsafe unsafe = UnsafeUtil.getUnsafe();
            UnsafeUtil.getUnsafe().putBoolean(unsafe.staticFieldBase(f), unsafe.staticFieldOffset(f), true);
        } catch (ClassNotFoundException | NullPointerException | NoSuchFieldException e) {
            LOGGER.error("Error occurred during setting ALLOW_ATTACH_SELF", e);
            return;
        }

        String pid = String.valueOf(ProcessHandle.current().pid());

        try {
            // Create a temp file to get absolute file path
            File file = File.createTempFile("advancedkjs_agent", ".jar");
            FileOutputStream fos = new FileOutputStream(file);
            InputStream is = AdvancedKJS.class.getResourceAsStream("/AdvancedKJSAgent-1.0.jar");
            if(Objects.isNull(is)) {
                LOGGER.error("Failed to get agent jar");
                return;
            }

            int r;
            while((r = is.read()) != -1) {
                fos.write(r);
            }

            fos.close();
            is.close();
            LOGGER.info("Attaching agent ({})", file.getAbsolutePath());
            VirtualMachine vm = VirtualMachine.attach(pid);
            vm.loadAgent(file.getAbsolutePath(), "");
            LOGGER.info("Attached agent");
            vm.detach();
        } catch (Exception e) {
            LOGGER.error("Failed to attach agent", e);
        }
    }

    @SuppressWarnings("unused")
    private static void agentCall(Instrumentation instrumentation) {
        AgentTransformer.INSTANCE.init(instrumentation);
    }
}
