package io.github.juicefries.fct.callback.event;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFWPreeditCandidateCallback;
import org.lwjgl.system.Callback;

/**
 * 输入法候选列表元信息事件。
 *
 * <p>只包含候选数量、当前选中下标与分页信息，不包含候选文本。</p>
 *
 * @since 1.0.7
 */
public final class PreeditCandidateEvent extends CallbackEventData {

    public final static String PREEDIT_CANDIDATE_EVENT = registerEventType("PreeditCandidate");

    private int candidatesCount = 0;
    private int selectedIndex = -1;
    private int pageStart = 0;
    private int pageSize = 0;

    private PreeditCandidateEvent() {

    }

    /**
     * @return 候选总数
     */
    public int candidatesCount() {
        return candidatesCount;
    }

    /**
     * @return 当前选中候选下标，-1 表示无
     */
    public int selectedIndex() {
        return selectedIndex;
    }

    /**
     * @return 当前页起始候选下标
     */
    public int pageStart() {
        return pageStart;
    }

    /**
     * @return 当前页大小
     */
    public int pageSize() {
        return pageSize;
    }

    public static @NotNull PreeditCandidateEvent candidate(long window, int candidatesCount,
                                                           int selectedIndex, int pageStart, int pageSize) {
        var e = new PreeditCandidateEvent();
        e.window = window;
        e.type = PREEDIT_CANDIDATE_EVENT;
        e.candidatesCount = candidatesCount;
        e.selectedIndex = selectedIndex;
        e.pageStart = pageStart;
        e.pageSize = pageSize;
        return e;
    }

    public static @NotNull PreeditCandidateEvent candidate(int candidatesCount,
                                                           int selectedIndex, int pageStart, int pageSize) {
        return candidate(NULL_WINDOW, candidatesCount, selectedIndex, pageStart, pageSize);
    }

    @Override
    public @NotNull PreeditCandidateEvent copy() {
        return candidate(window(), candidatesCount, selectedIndex, pageStart, pageSize);
    }

    @Override
    @Contract(pure = true)
    public Class<? extends Callback> getRequiredType() {
        return GLFWPreeditCandidateCallback.class;
    }
}
