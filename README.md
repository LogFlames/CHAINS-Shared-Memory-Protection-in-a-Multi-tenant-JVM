# Report - Reproducing results from: Shared Memory Protection in a Multi-tenant JVM

In this report I will focus on three numbers from the article in the title. These are:
1. The number of classes in java.base in OpenJDK 11 (6008 in article)
2. The number of classes in the seed (88 in article)
3. The number of classes in the minimal subset (595 in article)

My main goal/task is to reproduce the 595 minimal subset of classes that should only be loaded globally in the JVM.

## 1. Number of classes in java.base (OpenJDK 11)

### JMOD

I tried to count the number of classes using two methods. First I used the `jmod` util with `jmod list /usr/lib/jvm/java-11-openjdk/jmods/java.base.jmod | class \\.class`. I got different results depending on JDK11 version and operating system, and to account for platform specific classes I also counted the intersection of classes found on both Linux and Windows. I tried three different versions, JDK 11 - the first released, JDK 11.0.12 - released in July 2021, JDK 11.0.21 - the latest version.


|              | JDK 11 | JDK 11.0.11 | JDK 11.0.12 | JDK 11.0.21 |
|--------------|--------|-------------|-------------|-------------|
| Windows      | 6086   | 6153        | 6156        | 6193        |
| Linux        | 6112   | 6173        | 6176        | 6224        |
| Intersection | 5954   | 6017        | 6020        | 6057        |


I was not able to find any information in the article regarding what platform and/or specific version they were using, so I chose to use OpenJDK 11.0.12. This was released on the 21th of July 2021, making it plausable it is the version used by the authors. I also tested JDK 11.0.11, 11.0.10, and 11.0.9 on Linux but none seemed to match the 6008 classes from the article.

### JRT

Another way to count is creating a filesystem to access `jrt:` during runtime. These contained mostly the same classes. The code used can be found in `CountJRT.java` which was run in different versions of the jdk, on linux.

|       | JDK 11 | JDK 11.0.9 | JDK 11.0.10 | JDK 11.0.11 | JDK 11.0.12 | JDK 11.0.21 |
|-------|--------|------------|-------------|-------------|-------------|-------------|
| Linux | 6148   | 6179       | 6206        | 6209        | 6212        | 6260        |

None of these matched the 6008 exactly, but it was quite close. Without more information from the authors about version, operating system or method I decided to move on to the next parts.

## 2. Number of classes in the seed

### Well-known
The seed is specified in three parts. We have `well-known` classes, gotten from [`src/hotspot/share/classfile/systemDictionary.hpp` from the OpenJDK 11 source code](https://github.com/openjdk/jdk11/blob/master/src/hotspot/share/classfile/systemDictionary.hpp#L103). This list contains 88 classes, but we exclude all reflect.\*-classes (NOTE: not java.lang.reflect.\*) as they seem to not be part of newer versions of the JDK, and so we end up with 81 classes from here.

Classes:
```
java.lang.Object
java.lang.String
java.lang.Class
java.lang.Cloneable
java.lang.ClassLoader
java.io.Serializable
java.lang.System
java.lang.Throwable
java.lang.Error
java.lang.ThreadDeath
java.lang.Exception
java.lang.RuntimeException
java.lang.SecurityManager
java.security.ProtectionDomain
java.security.AccessControlContext
java.security.SecureClassLoader
java.lang.ClassNotFoundException
java.lang.NoClassDefFoundError
java.lang.LinkageError
java.lang.ClassCastException
java.lang.ArrayStoreException
java.lang.VirtualMachineError
java.lang.OutOfMemoryError
java.lang.StackOverflowError
java.lang.IllegalMonitorStateException
java.lang.ref.Reference
java.lang.ref.SoftReference
java.lang.ref.WeakReference
java.lang.ref.FinalReference
java.lang.ref.PhantomReference
java.lang.ref.Finalizer
java.lang.Thread
java.lang.ThreadGroup
java.util.Properties
java.lang.Module
java.lang.reflect.AccessibleObject
java.lang.reflect.Field
java.lang.reflect.Parameter
java.lang.reflect.Method
java.lang.reflect.Constructor
java.lang.invoke.DirectMethodHandle
java.lang.invoke.MethodHandle
java.lang.invoke.VarHandle
java.lang.invoke.MemberName
java.lang.invoke.ResolvedMethodName
java.lang.invoke.MethodHandleNatives
java.lang.invoke.LambdaForm
java.lang.invoke.MethodType
java.lang.BootstrapMethodError
java.lang.invoke.CallSite
java.lang.invoke.MethodHandleNatives$CallSiteContext
java.lang.invoke.ConstantCallSite
java.lang.invoke.MutableCallSite
java.lang.invoke.VolatileCallSite
java.lang.AssertionStatusDirectives
java.lang.StringBuffer
java.lang.StringBuilder
jdk.internal.misc.Unsafe
jdk.internal.module.Modules
java.io.ByteArrayInputStream
java.net.URL
java.util.jar.Manifest
jdk.internal.loader.ClassLoaders
jdk.internal.loader.ClassLoaders$AppClassLoader
jdk.internal.loader.ClassLoaders$PlatformClassLoader
java.security.CodeSource
java.lang.StackTraceElement
java.nio.Buffer
java.lang.StackWalker
java.lang.StackStreamFactory$AbstractStackWalker
java.lang.StackFrameInfo
java.lang.LiveStackFrameInfo
java.util.concurrent.locks.AbstractOwnableSynchronizer
java.lang.Boolean
java.lang.Character
java.lang.Float
java.lang.Double
java.lang.Byte
java.lang.Short
java.lang.Integer
java.lang.Long
```

### Garbage Collection
We then have all Garbage Collection-classes in `jdk.internal.ref`. There are 6 java-files, but some files contains more than one class, both named and annonymous.

Classes:
```
jdk.internal.ref.Cleaner
jdk.internal.ref.CleanerFactory
jdk.internal.ref.CleanerImpl
jdk.internal.ref.PhantomCleanable
jdk.internal.ref.SoftCleanable
jdk.internal.ref.WeakCleanable
```

Nested classes:
```
jdk.internal.ref.Cleaner$1
jdk.internal.ref.CleanerFactory$1
jdk.internal.ref.CleanerFactory$1$1
jdk.internal.ref.CleanerImpl$CleanerCleanable
jdk.internal.ref.CleanerImpl$InnocuousThreadFactory
jdk.internal.ref.CleanerImpl$InnocuousThreadFactory$1
jdk.internal.ref.CleanerImpl$PhantomCleanableRef
jdk.internal.ref.CleanerImpl$SoftCleanableRef
jdk.internal.ref.CleanerImpl$WeakCleanableRef
```

### Shared Secrets
Lastly we have `SharedSecrets` from `jdk.internal.misc`. This class is added to the seed without any problem.

Classes:
```
jdk.internal.misc.SharedSecrets
```

### Summary
We get either 97 or 88 classes in total depending on if we add all nested classes from `jdk.internal.ref` or just the outer ones. Since the authors got 88 classes in their seed, my best guess is that they only added the outer classes from `jdk.internal.ref` and not the nested ones.

I will call the seed with 88 classes (excl nested `jdk.internal.ref`) \sigma\_1 and the one with 97 classes (incl nested `jdk.internal.ref`) \sigma\_2.

## 3. Number of classes in the minimal subset

In the article it says they used static code analysis for this algorithm. I tried to find a tool which could do this analysis with java classes but was not able to find a ready tool, so I decided to do the algorithm in runtime with java reflect.

I ran the experiment in JDK 11.0.12.u7-1 which I was able to install from the archlinux archive. The versions downloaded from oracle's archive didn't find the class `jdk.internal.misc.SharedSecrets`.

It was then time to code the expansion algorithm, but here I had to do way more guesswork. There are many components you can take into consideration. Given that you have a class you can iterate through and add all linked:

* Fields
* Methods
    * Return type
    * Parameter types
    * Throwable exception types
* Interfaces
* Constructors
    * Parameter types


I tried to stay as close as possible to the description in the article which gave me this final code (see `FindMinimalSubset.java` for full context):

```java
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

        for (java.lang.reflect.Field f : c.getDeclaredFields()) {
            if (Modifier.isPrivate(f.getModifiers())) {
                continue;
            }
            Class<?> t = f.getType();
            addAllSupers(t, minimalSubset);
        }
    }

    return minimalSubset;
}
```

They refer specifically to the signarue of non-private fields and methods which includes the exceptions the methods can throw.

Running this code yields 614 classes if we expand from \sigma\_1 and 625 if we expand from \sigma\_2.

I was able to get *closer* to 595 by not iterating through the exceptions, but instead adding a for loop that iterates thorugh the linked interfaces. Then I got 593 from \sigma\_1 and 604 from \sigma\_2, but by adding and removing arbitrary components of the expansion algorithm you can get quite close by chance, and I feel 614 is a more fair representation of my best attempt at recreating the method described in the article. The reason for finding more classes then they did might be becasue I ran it in linux, and so my code might include some linux-specific classes in the minimal subset. It would be very interesting to compare the diff of which classes I found and what classes the authors found.

I also tried to add all search-paths that were accessible from the reflect API. The algorithm then explored the full bullet-list above. It then yielded 793 classe from \sigma\_1 and 797 from \sigma\_2. I do not think this is what the authors did, both becasue the do not mention either of these in the description of the algorithm: interfaces, consturctors or classes, and also because the results are way larger (60% larger than 595), and the numbers found previously for the inital seed and total number of classes was within 10% and 1% of the results from the paper.

I would be happy to explore this further. Especially if you have any tips for tools or ideas to explore.

By: Elias Lundell
[ellundel@kth.se](mailto:ellundel@kth.se)
