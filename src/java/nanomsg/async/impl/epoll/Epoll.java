package nanomsg.async.impl.epoll;

import com.sun.jna.Union;
import com.sun.jna.Pointer;
import com.sun.jna.Memory;
import com.sun.jna.Structure;
import com.sun.jna.Native;

import java.util.Arrays;
import java.util.List;


public class Epoll {
    private static final String LIB = "c";

    static {
        Native.register(LIB);
    }

    public static final int
        EPOLL_CTL_ADD = 1
      , EPOLL_CTL_DEL = 2
      , EPOLL_CTL_MOD = 3
    ;

    // /usr/include/x86_64-linux-gnu/bits/epoll.h
    public static final int
        EPOLL_CLOEXEC  = 02000000
    ;

    public static int
        EPOLLIN      = (int)0x001
      , EPOLLPRI     = (int)0x002
      , EPOLLOUT     = (int)0x004
      , EPOLLRDNORM  = (int)0x040
      , EPOLLRDBAND  = (int)0x080
      , EPOLLWRNORM  = (int)0x100
      , EPOLLWRBAND  = (int)0x200
      , EPOLLMSG     = (int)0x400
      , EPOLLERR     = (int)0x008
      , EPOLLHUP     = (int)0x010
      , EPOLLRDHUP   = (int)0x2000
      , EPOLLWAKEUP  = (int)(1 << 29)
      , EPOLLONESHOT = (int)(1 << 30)
      , EPOLLET      = (int)(1 << 31)
    ;


    public static class EpollData extends Union {
        public Pointer ptr;
        public int fd;
        public int u32;
        public long u64;

        public EpollData() {
            super();
        }

        public EpollData(int fd_or_u32) {
            super();
            this.u32 = this.fd = fd_or_u32;
            setType(Integer.TYPE);
        }

        public EpollData(long u64) {
            super();
            this.u64 = u64;
            setType(Long.TYPE);
        }

        public EpollData(Pointer ptr) {
            super();
            this.ptr = ptr;
            setType(Pointer.class);
        }

        public static class ByReference extends EpollData implements Structure.ByReference {}
        public static class ByValue extends EpollData implements Structure.ByValue {}
    }


    /*
        struct EpollEvent
        {
          uint32_t events;	//Epoll events
          EpollData data;	//User data variable
        } __EPOLL_PACKED;
    */
    public static class EpollEvent extends Structure {
        public int events;        //Epoll events
        public EpollData data; //User data variable

        protected List getFieldOrder() {
            return Arrays.asList("events", "data");
        }

        public EpollEvent() {
            super(Structure.ALIGN_NONE);
        }

        public EpollEvent(Pointer p) {
            super(p, Structure.ALIGN_NONE);
            setAlignType(Structure.ALIGN_NONE);
            read();
        }

        public EpollEvent(int events, EpollData data) {
            super(Structure.ALIGN_NONE);
            this.events = events;
            this.data = data;
        }

        public void reuse(Pointer p, int offset) {
            useMemory(p, offset);
            read();
        }

        public static class ByReference extends EpollEvent implements Structure.ByReference {
            public ByReference() {
                super();
            }

            public ByReference(int fd, int events) {
                super();
                this.events = events;
                data.fd = fd;
                data.setType(Integer.TYPE);
            }

            public ByReference(Pointer ptr, int events) {
                super();
                this.events = events;
                data.ptr = ptr;
                data.setType(Pointer.class);
            }

            public ByReference oneshot() {
                this.events = events | EPOLLONESHOT;
                return this;
            }
        }

        public static class ByValue extends EpollEvent implements Structure.ByValue {}
    }

    //extern int epoll_create (int __size) __THROW;
    public static native int epoll_create(int size);

    //extern int epoll_create1 (int __flags) __THROW;
    public static native int epoll_create1(int flags);

    //extern int epoll_ctl (int __epfd, int __op, int __fd, struct EpollEvent *__event) __THROW;
    public static native int epoll_ctl(int epfd, int op, int fd, EpollEvent.ByReference event);
    public static native int epoll_ctl(int epfd, int op, int fd, Pointer event);

    //extern int epoll_wait (int __epfd, struct EpollEvent *__events, int __maxevents, int __timeout);
    public static native int epoll_wait(int epfd, Pointer events, int maxevents, int timeout);
}

//extern int epoll_pwait (int __epfd, struct EpollEvent *__events, int __maxevents, int __timeout, const __sigset_t *__ss);
/* public static native int epoll_pwait(int epfd, Pointer events, int maxevents, int timeout, Pointer ss); */

 /* epoll_wait() is requesting an array of EpollEvent structs. So I create an array like this (please ignore the PinnableMemory class for now -- it just keeps a strong reference to an instance of Memory): */

/* public static Pointer createPointerToStructureArray(Class<? extends Structure> cls, int size) { */
/*     final int size_of_struct = Native.getNativeSize(cls); */
/*     final PinnableMemory m = PinnableMemory.pin(size * Pointer.SIZE); */
/*     for(int i = 0; i < size; ++i) { */
/*         m.setPointer(i * Pointer.SIZE, PinnableMemory.pin(size_of_struct)); */
/*     } */
/*     return m; */
/* } */

/* public static Pointer itemInStructureArrayAtIndex(Pointer array, int index) { */
/*     return array.getPointer(index * Pointer.SIZE); */
/* } */

/* public static void disposeStructureArray(Pointer ptr) { */
/*     final PinnableMemory orig = PinnableMemory.unpin(ptr); */
/*     final int size = (int)(orig.size() / (long)Pointer.SIZE); */

/*     PinnableMemory entry; */
/*     for(int i = 0; i < size; ++i) { */
/*         if ((entry = PinnableMemory.unpin(orig.getPointer(i * Pointer.SIZE))) != null) { */
/*             entry.dispose(); */
/*         } */
/*     } */

/*     orig.dispose(); */
/* } */
