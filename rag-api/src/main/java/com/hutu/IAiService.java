package com.hutu;

import reactor.core.publisher.Flux;

public interface IAiService {

    String generate(String message);

    Flux<String> generateStream(String message);

    void uploadFile();

}
