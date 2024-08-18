package bulls.websocket.asyncHandling;

import bulls.thread.GeneralCoreExecutors;
import bulls.websocket.WebSocketSessionCallbackMessageSender;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * <h2>AsyncResponseCenter</h2>
 *
 * <p>WebSocketHandler에서 Async한 처리가 필요한 경우 AsyncResponseCenter를 이용하여 간편하게 Async한 처리를 할 수 있다.</p>
 * <p></p>
 *
 * <h3>흐름</h3>
 * <p>AsyncResponse -> AsyncWork -> AsyncWork -> ... -> AsyncWork -> AsyncFinish</p>
 * <p></p>
 *
 * <h3>interface 및 class 설명</h3>
 * <p>* AsyncResponse : 입출력 데이터 및 중간 상태 데이터 등을 담는 클래스. AsyncWork, AsyncFinish 간에 AsyncResponse를 통해 서로 데이터를 주고받는다.</p>
 * <p>* AsyncWork : AsyncResponse를 받아 원하는 작업을 수행한 후 AsyncResponse에 결과를 업데이트하고 다음 AsyncWork로 넘겨준다. </p>
 * <p>* AsyncFinish : AsyncResponse의 최종 처리를 담당한다. 보통 client에게 처리 결과를 보낼 때 workAndSendResponse를 사용하거나,
 * AsyncFinish로 WebSocketSessionCallbackMessageSender를 사용한다. 이러한 경우에는 outputNode에 있는 정보를 client로 송신하게 된다.</p>
 * <p></p>
 */
public enum AsyncResponseCenter {
    Instance;

    // workMap, finishMap은 각각 AsyncWork, AsyncFinish 클래스의 singleton을 제공한다.
    private final ClassSingletonMap<AsyncWork> workMap = new ClassSingletonMap<>(DumbAsyncFunction.Instance);
    private final ClassSingletonMap<AsyncFinish> finishMap = new ClassSingletonMap<>(DumbAsyncFunction.Instance);

    private final Executor e = GeneralCoreExecutors.newFixedThreadPool(2);

    private AsyncWork getWorkFunc(Class<? extends AsyncWork> asyncWorkClass) {
        return workMap.getFunc(asyncWorkClass);
    }

    private AsyncFinish getFinishFunc(Class<? extends AsyncFinish> asyncFinishClass) {
        return finishMap.getFunc(asyncFinishClass);
    }

    public void work(AsyncResponse obj, List<Class<? extends AsyncWork>> workClassList, Class<? extends AsyncFinish> finishClass) {
        List<AsyncWork> workFuncList = new ArrayList<>();
        if (workClassList != null && !workClassList.isEmpty())
            for (var workClass : workClassList)
                workFuncList.add(getWorkFunc(workClass));

        AsyncFinish finishFunc = getFinishFunc(finishClass);
        work(obj, workFuncList, finishFunc);
    }

    public void work(AsyncResponse obj, List<? extends AsyncWork> workFuncList, AsyncFinish finishFunc) {
        var f = CompletableFuture.completedFuture(obj);
        if (workFuncList != null && !workFuncList.isEmpty())
            for (var workFunc : workFuncList)
                f = f.thenApplyAsync(workFunc::asyncWork, e);
        f.thenAcceptAsync(finishFunc::finish, e);
    }

    public void work(AsyncResponse obj, Class<? extends AsyncWork> workClass, Class<? extends AsyncFinish> finishClass) {
        AsyncWork workFunc = getWorkFunc(workClass);
        AsyncFinish finishFunc = getFinishFunc(finishClass);
        work(obj, workFunc, finishFunc);
    }

    public void work(AsyncResponse obj, AsyncWork workFunc, Class<? extends AsyncFinish> finishClass) {
        AsyncFinish finishFunc = getFinishFunc(finishClass);
        work(obj, workFunc, finishFunc);
    }

    public void work(AsyncResponse obj, AsyncWork workFunc, AsyncFinish finishFunc) {
        CompletableFuture.completedFuture(obj)
                .thenApplyAsync(workFunc::asyncWork, e)
                .thenAcceptAsync(finishFunc::finish, e);
    }

    public void work(AsyncResponse obj, Class<? extends AsyncFinish> finishClass) {
        AsyncFinish finishFunc = getFinishFunc(finishClass);
        work(obj, finishFunc);
    }

    public void work(AsyncResponse obj, AsyncFinish finishFunc) {
        CompletableFuture.completedFuture(obj).thenAcceptAsync(finishFunc::finish, e);
    }

    public void workAndSendResponse(AsyncResponse obj, List<Class<? extends AsyncWork>> workClassList) {
        work(obj, workClassList, WebSocketSessionCallbackMessageSender.class);
    }

    public void workAndSendResponse(AsyncResponse obj, Class<? extends AsyncWork> workClass) {
        work(obj, workClass, WebSocketSessionCallbackMessageSender.class);
    }

    public void workAndSendResponse(AsyncResponse obj, AsyncWork workFunc) {
        work(obj, workFunc, WebSocketSessionCallbackMessageSender.class);
    }
}
