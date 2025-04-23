package singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

class ClassicSingleton {
    private static ClassicSingleton instance;

    private ClassicSingleton() {}

    public static synchronized ClassicSingleton getInstance() {
        if (instance == null) {
            instance = new ClassicSingleton();
        }
        return instance;
    }
}

class DoubleCheckedSingleton {
    private static volatile DoubleCheckedSingleton instance;

    private DoubleCheckedSingleton() {}

    public static DoubleCheckedSingleton getInstance() {
        if (instance == null) {
            synchronized (DoubleCheckedSingleton.class) {
                if (instance == null) {
                    instance = new DoubleCheckedSingleton();
                }
            }
        }
        return instance;
    }
}

class HolderSingleton {
    private HolderSingleton() {}

    private static class Holder {
        static final HolderSingleton INSTANCE = new HolderSingleton();
    }

    public static HolderSingleton getInstance() {
        return Holder.INSTANCE;
    }
}

enum EnumSingleton {
    INSTANCE;

    private static Logger logger = LoggerFactory.getLogger(EnumSingleton.class);

    public void businessMethod() {
        logger.info("EnumSingleton contains{}", INSTANCE.hashCode());
    }
}

class VarHandleSingleton {
    private static volatile VarHandleSingleton instance;
    private static final VarHandle INSTANCE_HANDLE;

    static {
        try {
            INSTANCE_HANDLE = MethodHandles.lookup()
                    .findStaticVarHandle(VarHandleSingleton.class, "instance", VarHandleSingleton.class);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    private VarHandleSingleton() {}

    public static VarHandleSingleton getInstance() {
        VarHandleSingleton local = instance;
        if (local == null) {
            synchronized (VarHandleSingleton.class) {
                local = instance;
                if (local == null) {
                    local = new VarHandleSingleton();
                    INSTANCE_HANDLE.setRelease(local);
                }
            }
        }
        return local;
    }
}

record RecordSingleton() {
    private static final RecordSingleton INSTANCE = new RecordSingleton();

    public static RecordSingleton getInstance() {
        return INSTANCE;
    }
}

sealed interface Service permits SingletonService {
    void serve();

    static Service getInstance() {
        return SingletonService.INSTANCE;
    }
}

final class SingletonService implements Service {
    private static final Logger log = LoggerFactory.getLogger(SingletonService.class);

    static final SingletonService INSTANCE = new SingletonService();
    private SingletonService() {}

    @Override
    public void serve() {
        log.info("Call from the sealed Serve()");
    }
}

class VirtualThreadSingleton {
    private static final class Holder {
        static final VirtualThreadSingleton INSTANCE = new VirtualThreadSingleton();
    }

    private VirtualThreadSingleton() {
        // Ensure proper initialization with virtual threads
        Thread.startVirtualThread(() ->
                System.out.println("Singleton initialized with virtual thread support"));
    }

    public static VirtualThreadSingleton getInstance() {
        return Holder.INSTANCE;
    }
}

class ScopedSingleton {
    private static final ScopedValue<ScopedSingleton> INSTANCE = ScopedValue.newInstance();

    private ScopedSingleton() {}

    public static ScopedSingleton getInstance() {
        if (!INSTANCE.isBound()) {
            ScopedValue.where(INSTANCE, new ScopedSingleton()).run(() -> {});
        }
        return INSTANCE.get();
    }
}

final class ModernSingleton {
    private static ModernSingleton instance;

    private ModernSingleton() {}

    public static ModernSingleton getInstance() {
        return switch(instance) {
            case null -> {
                synchronized (ModernSingleton.class) {
                    yield instance = new ModernSingleton();
                }
            }
            case ModernSingleton existing -> existing;
        };
    }
}


public class MultiThreadWithSingleton {
    private static Logger logger = LoggerFactory.getLogger(MultiThreadWithSingleton.class);
    public static void main(String[] args) {
        // 1. Classic Synchronized Method (Java 1.0+)
        ClassicSingleton classicSingleton = ClassicSingleton.getInstance();
        logger.info("ClassicSingleton contains : {}", classicSingleton);
        ClassicSingleton classicSingletonNew = ClassicSingleton.getInstance();
        logger.info("ClassicSingleton contains : {}", classicSingletonNew);
        // Pros: Simple, works in all Java versions
        // Cons: Synchronization overhead on every call

        // 2. Double-Checked Locking (Java 1.2+ with volatile fix in Java 5+)
        DoubleCheckedSingleton doubleCheckedSingleton = DoubleCheckedSingleton.getInstance();
        DoubleCheckedSingleton doubleCheckedSingletonNew = DoubleCheckedSingleton.getInstance();
        logger.info("DoubleCheckedSingleton contains : {}", doubleCheckedSingleton);
        logger.info("DoubleCheckedSingletonNew contains : {}", doubleCheckedSingletonNew);
        // Pros: Better performance after first initialization
        // Cons: Requires volatile (Java 5+), subtle implementation details

        // 3. Initialization-on-Demand Holder (Java 1.2+)
        HolderSingleton holderSingleton = HolderSingleton.getInstance();
        HolderSingleton holderSingletonNew = HolderSingleton.getInstance();
        logger.info("holderSingleton contains : {}", holderSingleton);
        logger.info("holderSingletonNew contains : {}", holderSingletonNew);
        // Pros: Thread-safe, lazy initialization, no synchronization needed
        // Cons: Slightly more complex class structure

        // 4. Enum Singleton (Java 5+)
        EnumSingleton.INSTANCE.businessMethod();
        EnumSingleton.INSTANCE.businessMethod();
        // Pros: Simplest thread-safe implementation, serialization-safe
        // Cons: Less flexible (can't extend classes)

        // 5. VarHandle (Java 9+)
        VarHandleSingleton varHandleSingleton = VarHandleSingleton.getInstance();
        VarHandleSingleton varHandleSingletonNew = VarHandleSingleton.getInstance();
        logger.info("varHandleSingleton contains {} {}", varHandleSingleton);
        logger.info("varHandleSingletonNew contains {} {}", varHandleSingletonNew);
        // Pros: More advanced memory visibility control
        // Cons: Complex implementation

        // 6.Records with Static Field (Java 16+)
        RecordSingleton recordSingleton = new RecordSingleton();
        RecordSingleton recordSingletonNew = new RecordSingleton();
        recordSingleton.hashCode();
        recordSingletonNew.hashCode();
        logger.info("Both are Equal:{}", recordSingletonNew.equals(recordSingleton));
        // Pros: Immutable by design, concise syntax
        // Cons: Not lazy-initialized

        // 7.Sealed Interface with Singleton Implementation (Java 17+)
        logger.info(SingletonService.INSTANCE.toString());
        SingletonService.INSTANCE.serve();
        // Pros: Strong encapsulation, flexible design
        // Cons: More verbose

        // 8. Virtual Thread-Safe Singleton (Java 21+)
        VirtualThreadSingleton virtualThreadSingleton = VirtualThreadSingleton.getInstance();
        VirtualThreadSingleton virtualThreadSingletonNew = VirtualThreadSingleton.getInstance();
        virtualThreadSingleton.hashCode();
        virtualThreadSingletonNew.hashCode();
        logger.info("Both objects are same : {}"+ virtualThreadSingleton.equals(virtualThreadSingletonNew));
        // Pros: Works well with virtual threads
        // Cons: Requires Java 21+

        // 9. Scoped Value Singleton (Java 21+ preview, stabilized in Java 23)
        ScopedSingleton scopedSingleton = ScopedSingleton.getInstance();
        ScopedSingleton scopedSingletonNew = ScopedSingleton.getInstance();
        scopedSingleton.hashCode();
        scopedSingletonNew.hashCode();
        logger.info("Both are equal: {}" + scopedSingletonNew.equals(scopedSingleton));

        // 10. Java 24 Pattern Matching Enhancement
        // Commented bcoz java 24 has not build appropritely for this
//        ModernSingleton modernSingleton = ModernSingleton.getInstance();
//        ModernSingleton modernSingletonNew = ModernSingleton.getInstance();
//        modernSingleton.hashCode();
//        modernSingletonNew.hashCode();
//        logger.info("Both are equal: {}" + modernSingleton.equals(modernSingletonNew));

    }


}
