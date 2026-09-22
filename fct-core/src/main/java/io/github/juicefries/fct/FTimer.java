package io.github.juicefries.fct;

import io.github.juicefries.fct.event.ActionListener;
import io.github.juicefries.fct.event.EventData;
import io.github.juicefries.fct.event.EventListenerList;
import io.github.juicefries.fct.util.Lock;
import io.github.juicefries.fct.util.NanoTimer;
import io.github.juicefries.fct.util.Util;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * FCT 定时器
 * <p>
 *     参考 {@code javax.swing.Timer} 与 {@link io.github.juicefries.fct.util.IntervalTimer}，
 *     <br>
 *     按固定间隔触发 {@link ActionListener}，每次触发创建一个 {@link TimerEvent}。
 * </p>
 * @since 0.0.5
 * @author juicefries
 * @see ActionListener
 * @see TimerEvent
 */
public class FTimer implements Runnable {

    private final transient Lock lock = Lock.create();
    private final EventListenerList listenerList = new EventListenerList();
    private final NanoTimer timer = new NanoTimer();
    private final float initialDelay;

    private volatile float delay;
    private volatile float time = 0.0f;
    private volatile float totalTime = 0.0f;
    private volatile long count = 0L;
    private final AtomicBoolean pause = new AtomicBoolean(false);
    private final AtomicBoolean end = new AtomicBoolean(false);
    private final AtomicBoolean init = new AtomicBoolean(false);
    private Thread thread;

    /**
     * 以指定间隔创建定时器，并添加默认监听器
     *
     * @param delay    间隔时长（秒），必须 >= 0
     * @param listener 默认监听器
     * @throws IllegalArgumentException 如果 delay 小于 0
     */
    public FTimer(float delay, ActionListener listener) {
        if (delay < 0) throw new IllegalArgumentException("delay must be >= 0");
        this.delay = delay;
        this.initialDelay = delay;
        if (listener != null) listenerList.add(ActionListener.class, listener);
    }

    /**
     * 以指定间隔创建定时器
     *
     * @param delay 间隔时长（秒），必须 >= 0
     * @throws IllegalArgumentException 如果 delay 小于 0
     */
    public FTimer(float delay) {
        this(delay, null);
    }

    // ========================= OTM =========================

    @Override
    public void run() {
        timer.reset();
        timer.update();

        while (!end.get()) {
            synchronized (lock) {
                while (pause.get() && !end.get()) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            if (end.get()) break;

            timer.update();
            float tpf = timer.getTimePerFrame();
            totalTime += tpf;

            float currentDelay;
            synchronized (lock) {
                currentDelay = delay;
            }

            if (currentDelay >= 0) {
                time += tpf;
                if (time >= currentDelay) {
                    time -= currentDelay;
                    fire();
                }
            } else {
                fire();
            }
        }
    }

    // ========================= 生命周期 =========================

    public void start() {
        if (init.get()) throw new IllegalStateException("Timer already initialized!");
        init.set(true);
        end.set(false);
        pause.set(false);
        time = 0.0f;
        totalTime = 0.0f;
        count = 0L;
        timer.reset();

        thread = new Thread(this, "FTimer");
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        synchronized (lock) {
            end.set(true);
            pause.set(false);
            lock.notifyAll();
        }
        Thread t = thread;
        if (t != null && t.isAlive()) {
            try {
                t.interrupt();
                t.join(1000);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
        thread = null;
        init.set(false);
    }

    // ========================= 控制 =========================

    public void setDelay(float delay) {
        if (delay < 0) throw new IllegalArgumentException("delay must be >= 0");
        synchronized (lock) {
            this.delay = delay;
        }
    }

    public void resetDelay() {
        synchronized (lock) {
            this.delay = initialDelay;
        }
    }

    public void setPause(boolean value) {
        synchronized (lock) {
            pause.set(value);
            if (!value) lock.notifyAll();
        }
    }

    // ========================= LISTENER =========================

    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }

    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }

    public ActionListener[] getActionListeners() {
        return listenerList.getListeners(ActionListener.class);
    }

    // ========================= GET =========================

    public float getDelay() { return delay; }
    public float getInitialDelay() { return initialDelay; }
    public boolean isEnd() { return end.get(); }
    public boolean isInit() { return init.get(); }
    public boolean isPause() { return pause.get(); }
    public Thread getThread() { return thread; }

    // ========================= UTIL =========================

    private void fire() {
        ActionListener[] listeners = listenerList.getListeners(ActionListener.class);
        EventData e = TimerEvent.timer(delay, totalTime, ++count, System.nanoTime());
        for (ActionListener l : listeners) {
            if (l != null) l.action(e);
        }
    }

    /**
     * 定时器事件
     * <p>
     *     由 {@link FTimer} 触发时创建，作为 {@link ActionListener#action(EventData)} 的参数。
     * </p>
     * @since 0.0.5
     */
    public final static class TimerEvent implements EventData {

        public final static long TIMER_EVENT = Util.turn("Timer");

        long type;
        float delay;
        float totalTime;
        long count;
        long when;

        @Contract(pure = true)
        TimerEvent() {}

        /**
         * 创建定时器事件
         *
         * @param delay     本次触发时的间隔时长（秒）
         * @param totalTime 定时器累计运行时长（秒）
         * @param count     本次是第几次触发（从 1 开始）
         * @param when      触发时刻（纳秒时间戳）
         * @return 新的事件
         */
        public static @NotNull TimerEvent timer(float delay, float totalTime, long count, long when) {
            var e = new TimerEvent();
            e.type = TIMER_EVENT;
            e.delay = delay;
            e.totalTime = totalTime;
            e.count = count;
            e.when = when;
            return e;
        }

        /**
         * 获取本次触发时的间隔时长
         *
         * @return 间隔时长（秒）
         */
        public float getDelay() { return delay; }

        /**
         * 获取定时器累计运行时长
         *
         * @return 累计时长（秒）
         */
        public float getTotalTime() { return totalTime; }

        /**
         * 获取本次是第几次触发
         *
         * @return 触发次数，从 1 开始
         */
        public long getCount() { return count; }

        /**
         * 获取触发时刻
         *
         * @return 纳秒时间戳
         */
        public long getWhen() { return when; }

        @Contract(pure = true)
        @Override
        public long getType() {
            return type;
        }

        @Override
        public @NotNull EventData copy() {
            var e = new TimerEvent();
            e.type = type;
            e.delay = delay;
            e.totalTime = totalTime;
            e.count = count;
            e.when = when;
            return e;
        }

        @Override
        public TimerEvent clone() {
            return Util.cloneFailed(() -> {
                TimerEvent e = (TimerEvent) super.clone();
                e.type = type;
                e.delay = delay;
                e.totalTime = totalTime;
                e.count = count;
                e.when = when;
                return e;
            });
        }
    }
}
