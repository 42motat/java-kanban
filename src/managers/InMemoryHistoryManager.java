package managers;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Map<Integer, Node> nodes = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public List<Task> getHistory() {
        List<Task> returnedTasks = new ArrayList<>();

        Node node = first;
        while (node != null) {
            returnedTasks.add(node.task);
            node = node.next;
        }

        return List.copyOf(returnedTasks);
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        removeNode(nodes.get(task.getTaskId()));
        linkLast(task);
    }

    @Override
    public void remove(Integer taskId) {
        removeNode(nodes.get(taskId));
    }

    public void linkLast(Task task) {
        // новая нода
        Node node = new Node(task, last, null);
        if (first == null) {
            first = node;
        } else {
            last.next = node;
        }
        last = node;
        nodes.put(task.getTaskId(), last);
    }

    public void removeNode(Node node) {
        if (node == null) {
            return;
        }

        if (nodes.size() == 1) {  // == (node.equals(first)) & (node.equals(last));
            last.previous = null;
            first = null;
            last = null;
        } else if ((node.equals(first)) & (node.next != null)) {
            first = node.next;
            first.previous = null;
        } else if ((node.equals(last)) & (node.previous != null)) {
            last = node.previous;
            first.next = null;
        } else if ((node.next != null) & (node.previous != null)) {
            node.previous.next = node.next;
            node.next.previous = node.previous;
        }
        nodes.remove(node.task.getTaskId());
    }

    public static class Node {
        Task task;
        Node previous;
        Node next;

        public Node(Task task, Node previous, Node next) {
            this.task = task;
            this.previous = previous;
            this.next = next;
        }
    }
}
