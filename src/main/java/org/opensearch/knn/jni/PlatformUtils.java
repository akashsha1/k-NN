/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 *
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

package org.opensearch.knn.jni;

import com.sun.jna.Platform;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import oshi.util.platform.mac.SysctlUtil;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;

public class PlatformUtils {

    private static final Logger logger = LogManager.getLogger(PlatformUtils.class);

    /**
     * Verify if the underlying system supports AVX2 SIMD Optimization or not
     * 1. If the architecture is not x86 return false.
     * 2. If the operating system is not Mac or Linux return false(for example Windows).
     * 3. If the operating system is macOS, use oshi library to verify if the cpu flags
     *    contains 'avx2' and return true if it exists else false.
     * 4. If the operating system is linux, read the '/proc/cpuinfo' file path and verify if
     *    the flags contains 'avx2' and return true if it exists else false.
     */
    public static boolean isAVX2SupportedBySystem() {
        if (!Platform.isIntel()) {
            return false;
        }

        if (Platform.isMac()) {

            // sysctl or system control retrieves system info and allows processes with appropriate privileges
            // to set system info. This system info contains the machine dependent cpu features that are supported by it.
            // On MacOS, if the underlying processor supports AVX2 instruction set, it will be listed under the "leaf7"
            // subset of instructions ("sysctl -a | grep machdep.cpu.leaf7_features").
            // https://developer.apple.com/library/archive/documentation/System/Conceptual/ManPages_iPhoneOS/man3/sysctl.3.html
            try {
                return AccessController.doPrivileged((PrivilegedExceptionAction<Boolean>) () -> {
                    String flags = SysctlUtil.sysctl("machdep.cpu.leaf7_features", "empty");
                    return (flags.toLowerCase(Locale.ROOT)).contains("avx2");
                });
            } catch (Exception e) {
                logger.error("[KNN] Error fetching cpu flags info. [{}]", e.getMessage(), e);
            }

        } else if (Platform.isLinux()) {
            return isAVX2Supported();
        }
        return false;
    }

    public static boolean isAVX512Supported() {       

            // The "/proc/cpuinfo" is a virtual file which identifies and provides the processor details used
            // by system. This info contains "flags" for each processor which determines the qualities of that processor
            // and it's ability to process different instruction sets like mmx, avx, avx2, avx512 and so on.
            // https://access.redhat.com/documentation/en-us/red_hat_enterprise_linux/6/html/deployment_guide/s2-proc-cpuinfo
            // Here, we are trying to read the details of all processors used by system and find if any of the processor
            // supports AVX512 instructions supported by faiss.             
            String fileName = "/proc/cpuinfo";
            String[] avx512 = new String { "avx512f", "avx512cd", "avx512vl", "avx512dq","avx512bw" };
            try {
                string flags = AccessController.doPrivileged(
                    (PrivilegedExceptionAction<Boolean>) () -> (Boolean) Files.lines(Paths.get(fileName))
                        .filter(s -> s.startsWith("flags"))
                        .limit(1));
                
                foreach (string flag: avx512)
                {
                    if (!flags.contains(flag))
                        return false;
                }

                return true;
            } catch (Exception e) {
                logger.error("[KNN] Error reading file [{}]. [{}]", fileName, e.getMessage(), e);
            }
        
        return false;
    }
}
