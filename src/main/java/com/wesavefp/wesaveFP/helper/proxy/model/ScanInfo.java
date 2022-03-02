package com.wesavefp.wesaveFP.helper.proxy.model;

import org.zaproxy.clientapi.core.ApiResponseSet;

public class ScanInfo {
    int progress;
    int id;
    State state;

    public ScanInfo(ApiResponseSet response) {
        this.id = Integer.parseInt(response.getAttribute("id"));
        this.progress = Integer.parseInt(response.getAttribute("progress"));
        this.state = ScanInfo.State.parse(response.getAttribute("state"));
    }

    public int getProgress() {
        return this.progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public static enum State {
        NOT_STARTED,
        FINISHED,
        PAUSED,
        RUNNING;

        private State() {
        }

        public static State parse(String s) {
            if ("NOT_STARTED".equalsIgnoreCase(s)) {
                return NOT_STARTED;
            } else if ("FINISHED".equalsIgnoreCase(s)) {
                return FINISHED;
            } else if ("PAUSED".equalsIgnoreCase(s)) {
                return PAUSED;
            } else if ("RUNNING".equalsIgnoreCase(s)) {
                return RUNNING;
            } else {
                throw new RuntimeException("Unknown state: " + s);
            }
        }
    }
}
