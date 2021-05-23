package de.semvox.research.asr.utils;

public interface Tuple2<F, S> {
    F first();

    S second();

    static <F, S> Tuple2<F, S> of(F fist, S second) {
        return new Tuple2Impl<>(fist, second);
    }

    class Tuple2Impl<F, S> implements Tuple2<F, S> {
        private final F first;
        private final S second;

        public Tuple2Impl(F fist, S second) {
            this.first = fist;
            this.second = second;
        }

        @Override
        public F first() {
            return first;
        }

        @Override
        public S second() {
            return second;
        }
    }
}
