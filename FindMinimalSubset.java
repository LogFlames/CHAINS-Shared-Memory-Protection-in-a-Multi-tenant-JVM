import java.util.*;
import java.nio.file.*;
import java.net.*;
import java.util.stream.*;
import java.lang.reflect.*;

class FindMinimalSubset {

    public static void main(String[] args) {
	    expandFromSeed();
    }

    public static void expandFromSeed() {
        String[] seed_str = new String[]{
			"java.lang.Object",
			"java.lang.String",
			"java.lang.Class",
			"java.lang.Cloneable",
			"java.lang.ClassLoader",
			"java.io.Serializable",
			"java.lang.System",
			"java.lang.Throwable",
			"java.lang.Error",
			"java.lang.ThreadDeath",
			"java.lang.Exception",
			"java.lang.RuntimeException",
			"java.lang.SecurityManager",
			"java.security.ProtectionDomain",
			"java.security.AccessControlContext",
			"java.security.SecureClassLoader",
			"java.lang.ClassNotFoundException",
			"java.lang.NoClassDefFoundError",
			"java.lang.LinkageError",
			"java.lang.ClassCastException",
			"java.lang.ArrayStoreException",
			"java.lang.VirtualMachineError",
			"java.lang.OutOfMemoryError",
			"java.lang.StackOverflowError",
			"java.lang.IllegalMonitorStateException",
			"java.lang.ref.Reference",
			"java.lang.ref.SoftReference",
			"java.lang.ref.WeakReference",
			"java.lang.ref.FinalReference",
			"java.lang.ref.PhantomReference",
			"java.lang.ref.Finalizer",
			"java.lang.Thread",
			"java.lang.ThreadGroup",
			"java.util.Properties",
			"java.lang.Module",
			"java.lang.reflect.AccessibleObject",
			"java.lang.reflect.Field",
			"java.lang.reflect.Parameter",
			"java.lang.reflect.Method",
			"java.lang.reflect.Constructor",
			/*
			"reflect.MagicAccessorImpl",
			"reflect.MethodAccessorImpl",
			"reflect.ConstructorAccessorImpl",
			"reflect.DelegatingClassLoader",
			"reflect.ConstantPool",
			"reflect.UnsafeStaticFieldAccessorImpl",
			"reflect.CallerSensitive",
			*/
			"java.lang.invoke.DirectMethodHandle",
			"java.lang.invoke.MethodHandle",
			"java.lang.invoke.VarHandle",
			"java.lang.invoke.MemberName",
			"java.lang.invoke.ResolvedMethodName",
			"java.lang.invoke.MethodHandleNatives",
			"java.lang.invoke.LambdaForm",
			"java.lang.invoke.MethodType",
			"java.lang.BootstrapMethodError",
			"java.lang.invoke.CallSite",
			"java.lang.invoke.MethodHandleNatives$CallSiteContext",
			"java.lang.invoke.ConstantCallSite",
			"java.lang.invoke.MutableCallSite",
			"java.lang.invoke.VolatileCallSite",
			"java.lang.AssertionStatusDirectives",
			"java.lang.StringBuffer",
			"java.lang.StringBuilder",
			"jdk.internal.misc.Unsafe",
			"jdk.internal.module.Modules",
			"java.io.ByteArrayInputStream",
			"java.net.URL",
			"java.util.jar.Manifest",
			"jdk.internal.loader.ClassLoaders",
			"jdk.internal.loader.ClassLoaders$AppClassLoader",
			"jdk.internal.loader.ClassLoaders$PlatformClassLoader",
			"java.security.CodeSource",
			"java.lang.StackTraceElement",
			"java.nio.Buffer",
			"java.lang.StackWalker",
			"java.lang.StackStreamFactory$AbstractStackWalker",
			"java.lang.StackFrameInfo",
			"java.lang.LiveStackFrameInfo",
			"java.util.concurrent.locks.AbstractOwnableSynchronizer",
			"java.lang.Boolean",
			"java.lang.Character",
			"java.lang.Float",
			"java.lang.Double",
			"java.lang.Byte",
			"java.lang.Short",
			"java.lang.Integer",
			"java.lang.Long",

			// from jdk.internal.ref
			"jdk.internal.ref.Cleaner",
			"jdk.internal.ref.CleanerFactory",
			"jdk.internal.ref.CleanerImpl",
			"jdk.internal.ref.PhantomCleanable",
			"jdk.internal.ref.WeakCleanable",
			"jdk.internal.ref.SoftCleanable",

            "jdk.internal.ref.Cleaner$1",
            "jdk.internal.ref.CleanerFactory$1",
            "jdk.internal.ref.CleanerFactory$1$1",
            "jdk.internal.ref.CleanerImpl$CleanerCleanable",
            "jdk.internal.ref.CleanerImpl$InnocuousThreadFactory",
            "jdk.internal.ref.CleanerImpl$InnocuousThreadFactory$1",
            "jdk.internal.ref.CleanerImpl$PhantomCleanableRef",
            "jdk.internal.ref.CleanerImpl$SoftCleanableRef",
            "jdk.internal.ref.CleanerImpl$WeakCleanableRef",

			// from jdk.internal.misc
			"jdk.internal.misc.SharedSecrets"
			};

        //seed_str = new String[]{"java.lang.ClassLoader"};

		System.out.println(seed_str.length);

        List<Class<?>> seed = new ArrayList<>();

        for (String s : seed_str) {
            try {
                Class<?> str = ClassLoader.getSystemClassLoader().loadClass(s);
                seed.add(str);
            } catch (Exception e) {
                System.err.println(e);
                return;
            }
        }

        Class<?>[] arr = new Class<?>[0];
        List<Class<?>> minimalSubset = MinimalSubsetDetectionAlgorithm(seed.toArray(arr));

        for (Class<?> c : minimalSubset) {
            System.out.println(c);
        }

        System.out.println(minimalSubset.size());
    }

    private static void addAllSupers(Class<?> c, List<Class<?>> minimalSubset) {
        do {
            while (c.isArray()) {
                c = c.getComponentType();
            }

            if (!minimalSubset.contains(c)) {
                minimalSubset.add(c);
            }
            c = c.getSuperclass();
        } while (c != null);
    }


    public static List<Class<?>> MinimalSubsetDetectionAlgorithm(Class<?>[] seed) {
        List<Class<?>> minimalSubset = new ArrayList<Class<?>>();

        for (Class<?> c : seed) {
            addAllSupers(c, minimalSubset);
        }

        for (int j = 0; j < minimalSubset.size(); j++) {
            Class<?> c = minimalSubset.get(j);
            for (java.lang.reflect.Method m : c.getDeclaredMethods()) {
                if (Modifier.isPrivate(m.getModifiers())) {
                    continue;
                }

                Class<?> r = m.getReturnType();

                addAllSupers(r, minimalSubset);

                for (Class<?> p : m.getParameterTypes()) {
                    addAllSupers(p, minimalSubset);
                }

                for (Class<?> e : m.getExceptionTypes()) {
                    addAllSupers(e, minimalSubset);
                }
            }

            for (Class<?> i : c.getInterfaces()) {
                addAllSupers(i, minimalSubset);
            }

            for (java.lang.reflect.Constructor<?> cc : c.getConstructors()) {
                for (Class<?> p : cc.getParameterTypes()) {
                    addAllSupers(p, minimalSubset);
                }

                for (Class<?> e : cc.getExceptionTypes()) {
                    addAllSupers(e, minimalSubset);
                }
            }

            for (java.lang.reflect.Field f : c.getDeclaredFields()) {
                if (Modifier.isPrivate(f.getModifiers())) {
                    continue;
                }
                Class<?> t = f.getType();
                addAllSupers(t, minimalSubset);
            }

            for (Class<?> cc : c.getDeclaredClasses()) {
                if (Modifier.isPrivate(cc.getModifiers())) {
                    continue;
                }
                addAllSupers(cc, minimalSubset);
            }
        }

        return minimalSubset;
    }
}
