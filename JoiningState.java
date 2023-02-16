package com.ekoapp.voip.internal.state;

public class JoiningState extends BaseCallState {

    @Override
    public BaseCallState onAllMembersLeft() {
        return CallState.JOINING_IDLE.getState();
    }

    @Override
    public BaseCallState onAllMembersOffline() {
        return CallState.JOINING_ALONE.getState();
    }

    @Override
    public BaseCallState onJoined() {
        return CallState.ONGOING.getState();
    }

    @Override
    public BaseCallState onEndClicked() {
        return CallState.LEAVING_CHANNEL_AND_LEAVING.getState();
    }

    @Override
    public BaseCallState onCallEnded() {
        return this;
    }

    @Override
    public BaseCallState onLocalUserBusy() {
        return CallState.LEAVING_CHANNEL_AND_LEAVING.getState();
    }
}
