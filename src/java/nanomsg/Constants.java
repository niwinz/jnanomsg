package nanomsg;

public class Constants {
    public static final int NN_HAUSNUMERO = 156384712;
    public static final int ECONNREFUSED = NN_HAUSNUMERO + 7;
    public static final int EAFNOSUPPORT = NN_HAUSNUMERO + 10;
    public static final int ETERM = NN_HAUSNUMERO + 53;
    public static final int EFSM = NN_HAUSNUMERO + 54;
    public static final int AF_SP = 1;
    public static final int AF_SP_RAW = 2;
    public static final int NN_MSG = -1;
    public static final int NN_PROTO_PUBSUB = 2;
    public static final int NN_PUB = NN_PROTO_PUBSUB * 16 + 0;
    public static final int NN_SUB = NN_PROTO_PUBSUB * 16 + 1;
    public static final int NN_SUB_SUBSCRIBE = 1;
    public static final int NN_SUB_UNSUBSCRIBE = 2;
}
