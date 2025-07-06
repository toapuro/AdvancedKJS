package dev.toapuro.kubeextra.test;

public interface ITest {
    void test();

    default int returnable() {
        return 0;
    }
}
