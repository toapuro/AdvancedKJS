package dev.toapuro.advancedkjs.test;

public interface ITest {
    void test();

    default int returnable() {
        return 0;
    }
}
