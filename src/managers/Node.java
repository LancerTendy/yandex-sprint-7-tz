class Node {
    private Node next;
    private Node prev;
    private final Task task;

    public Node(Task task) {
        this.task = task;
        this.next = null;
        this.prev = null;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public Task getTask() {
        return task;
    }

    @Override
    public String toString() {
        return "Node{" +
                "task=" + task +
                '}';
    }
}
