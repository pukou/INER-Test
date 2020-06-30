package com.bsoft.mob.ienr.util.tools;

public class HttpBackMsg<F, S, T> {
    public final F first;
    public final S second;
    public final T third;

    public HttpBackMsg(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public String toString() {
        return "HttpBackMsg{" + String.valueOf(first) + " " + String.valueOf(second) + " " + String.valueOf(third) + "}";
    }

    public static <A, B, C> HttpBackMsg<A, B, C> create(A a, B b, C c) {
        return new HttpBackMsg<A, B, C>(a, b, c);
    }
}
