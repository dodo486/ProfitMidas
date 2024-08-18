package bulls.websocket.asyncHandling;

public enum DumbAsyncFunction implements AsyncWork, AsyncFinish {
    Instance;

    @Override
    public void finish(AsyncResponse obj) {
        // do nothing
    }

    @Override
    public AsyncResponse asyncWork(AsyncResponse obj) {
        return obj;
    }
}
