package com.ekoapp.voip.internal.state;

public abstract class BaseCallState {

    public String getKey() {
        return getClass().getSimpleName();
    }

    //mqtt.

    public BaseCallState onCallRejected() {
        return this;
    }

    public BaseCallState onCallStarted() {
        return this;
    }

    public abstract BaseCallState onCallEnded();

    //api.

    public BaseCallState onCreated() {
        return this;
    }

    public BaseCallState onJoined() {
        return this;
    }

    public BaseCallState onLeft() {
        return this;
    }

    public BaseCallState onRejected() {
        return this;
    }

    //click events.

    public BaseCallState onJoinClicked() {
        return this;
    }

    public BaseCallState onAcceptClicked() {
        return this;
    }

    public BaseCallState onRejectClicked() {
        return this;
    }

    public BaseCallState onEndClicked() {
        return this;
    }

    //user events.

    public BaseCallState onLocalUserJoined() {
        return this;
    }

    public BaseCallState onRemoteUserJoined() {
        return this;
    }

    public BaseCallState onLocalUserLeft() {
        return this;
    }

    public BaseCallState onRemoteUserLeft() {
        return this;
    }

    public BaseCallState onLocalUserOffline() {
        return this;
    }

    public BaseCallState onRemoteUserOffline() {
        return this;
    }

    public BaseCallState onAllMembersLeft() {
        return this;
    }

    public BaseCallState onAllMembersOffline() {
        return this;
    }

    //rpc timeout.

    public BaseCallState onCreateFailed() {
        return this;
    }

    public BaseCallState onHeartbeatFailed() {
        return this;
    }

    //other events.

    public abstract BaseCallState onLocalUserBusy();

    public BaseCallState onRemoteUserBusy() {
        return this;
    }

    public BaseCallState onTimeout() {
        return this;
    }
}
