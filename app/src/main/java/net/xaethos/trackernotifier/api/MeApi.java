package net.xaethos.trackernotifier.api;

import net.xaethos.trackernotifier.models.Me;

import rx.Observable;

public interface MeApi {

    Observable<Me> login(String username, String password);

    Observable<Me> get();
}
