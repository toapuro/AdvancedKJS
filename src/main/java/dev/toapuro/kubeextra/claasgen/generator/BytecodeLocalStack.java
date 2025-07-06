package dev.toapuro.kubeextra.claasgen.generator;

public class BytecodeLocalStack {
    private int maxLocal;
    private int maxStack;
    private int local;
    private int stack;

    public BytecodeLocalStack() {
        this.local = 0;
        this.stack = 0;
        this.maxLocal = 0;
        this.maxStack = 0;
    }

    public void apply(BytecodeLocalStack localStack) {
        this.local += localStack.getLocal();
        this.stack += localStack.getStack();
        this.maxLocal += Math.max(this.maxLocal, this.maxLocal + localStack.getMaxLocal());
        this.maxStack += Math.max(this.maxStack, this.maxStack + localStack.getMaxStack());
    }

    public void pushLocal(int amount) {
        this.local += amount;
        this.maxLocal = Math.max(this.maxLocal, this.local);
    }

    public void pushStack(int amount) {
        this.stack += amount;
        this.maxStack = Math.max(this.maxStack, this.stack);
    }

    public void popLocal(int amount) {
        this.local -= amount;
    }

    public void popStack(int amount) {
        this.stack -= amount;
    }

    public void pushLocal() {
        pushLocal(1);
    }

    public void pushStack() {
        pushStack(1);
    }

    public void popLocal() {
        this.local -= 1;
    }

    public void popStack() {
        this.stack -= 1;
    }

    public int getLocal() {
        return local;
    }

    public int getStack() {
        return stack;
    }

    public int getMaxLocal() {
        return maxLocal;
    }

    public int getMaxStack() {
        return maxStack;
    }
}
