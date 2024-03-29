package com.smart.himalaya.callback;

public interface ISkinChangingCallback {
    void onStart();

    void onError(Exception e);

    void onComplete();

    public static DefaultSkinChangingCallback DEFAULT_CALLBACK = new DefaultSkinChangingCallback();

    public class DefaultSkinChangingCallback implements ISkinChangingCallback {

        @Override
        public void onStart() {

        }

        @Override
        public void onError(Exception e) {

        }

        @Override
        public void onComplete() {

        }
    }
}
