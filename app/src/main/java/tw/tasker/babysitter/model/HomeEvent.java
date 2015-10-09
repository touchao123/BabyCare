package tw.tasker.babysitter.model;

import hugo.weaving.DebugLog;

public class HomeEvent {
    public static final int ACTION_ERROR = -1;
    public static final int ACTION_QUERY = 0;
    public static final int ACTION_FILTERPANEL_SHOW = 1;
    public static final int ACTION_FILTERPANEL_HIDE = 2;
    public static final int ACTION_FILTERPANEL_SAVE = 3;
    public static final int ACTION_TOGGLE_KEYPAD = 4;
    public static final int ACTION_PUSH = 5;
    public static final int ACTION_SEND = 6;
    private int mAction;

    public HomeEvent(int action) {
        mAction = action;
    }

    public int getAction() {
        showAction();
        return mAction;
    }

    @DebugLog
    public String showAction() {
        String actionName = "";

        switch (mAction) {
            case ACTION_ERROR:
                actionName = "ACTION_ERROR";
                break;
            case ACTION_QUERY:
                actionName = "ACTION_QUERY";
                break;
            case ACTION_FILTERPANEL_SHOW:
                actionName = "ACTION_FILTERPANEL_SHOW";
                break;
            case ACTION_FILTERPANEL_HIDE:
                actionName = "ACTION_FILTERPANEL_HIDE";
                break;
            case ACTION_FILTERPANEL_SAVE:
                actionName = "ACTION_FILTERPANEL_SAVE";
                break;
            case ACTION_TOGGLE_KEYPAD:
                actionName = "ACTION_TOGGLE_KEYPAD";
                break;
            case ACTION_PUSH:
                actionName = "ACTION_PUSH";
                break;
            case ACTION_SEND:
                actionName = "ACTION_SEND";
                break;
            default:
                actionName = "Please add action [" + mAction + "] name.";
        }

        return actionName;
    }
}
