package com.spencerwi.either;

import org.junit.jupiter.api.Test;

class EitherFlatMapTest {

    @Test
    void eitherFlatMapExample() {

        Either<Error, Final> either = eitherStep1(new Request())
                .flatMap(this::eitherStep2)
                .map(this::step3);
    }

    @Test
    void resultFlatmapExample() {

        Result<Final> result = Result.attempt(() -> step1(new Request()))
                .map(this::step2)
                .map(this::step3);
    }



    Either<Error, Intermediate> eitherStep1(Request request) {
        if (request.isValid()) {
            return Either.right(new Intermediate());
        } else {
            return Either.left(new Error("Request is not valid"));
        }
    }

    Intermediate step1(Request request) throws Exception {
        if (request.isValid()) {
            return new Intermediate();
        } else {
            throw new Exception("Request is not valid");
        }
    }


    Either<Error, PreFinal> eitherStep2(Intermediate intermediate) {
        if (intermediate.isValid()) {
            return Either.right(new PreFinal());
        } else {
            return Either.left(new Error("Intermediate is not valid"));
        }
    }

    PreFinal step2(Intermediate intermediate) throws Exception {
        if (intermediate.isValid()) {
            return new PreFinal();
        } else {
            throw new Exception("Intermediate is not valid");
        }
    }

    Final step3(PreFinal preFinal) {
        return new Final();
    }


    static class Request {
        boolean isValid() {
            return true;
        }
    }

    static class Intermediate {
        boolean isValid() {
            return true;
        }
    }

    static class PreFinal {

    }

    static class Final {

    }

    static class Error {

        String desc;

        Error(String desc) {
            this.desc = desc;
        }


    }

}
