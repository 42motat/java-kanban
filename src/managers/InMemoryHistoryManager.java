/* Александру Ф.
 * Добрый день, Александр!
 * Заранее благодарю за обратную связь по ФЗ ТЗ 6.
 * Прошу прощения, что задержал сдачу проекта до начала спринта 7 (пришлось попотеть над этим заданием).
 */

package managers;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    Map<Integer, Node> nodes = new HashMap<>();
    Node first;
    Node last;

    List<Task> linkedTaskList = new ArrayList<>();
    List<Task> inMemoryHistory = new ArrayList<>();

    @Override
    public List<Task> getHistory() {
        return List.copyOf(inMemoryHistory);
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        removeNode(nodes.get(task.getTaskId()));
        linkLast(task);
        getTasks();
    }

    @Override
    public void remove(Integer taskId) {
        removeNode(nodes.get(taskId));
    }

    public void linkLast (Task task) {
        // новая нода
        Node node = new Node(task, last, null);
        if (first == null) {
            first = node;
        } else {
            last.next = node;
        }
        last = node;
        // в список идёт ЗАДАЧА, а не НОДА
        linkedTaskList.add(task);
        nodes.put(task.getTaskId(), last);
    }

    public void getTasks() {
        inMemoryHistory.add(last.task);
    }

    public void removeNode(Node node) {
        if (node == null) {
            return;
        }
        if (node.previous == null && node.next == null) {
            nodes.remove(node.task.getTaskId());
            linkedTaskList.remove(node.task);
            inMemoryHistory.remove(node.task);
        } else if (node.previous == null) {
            node.next.previous = null;
        } else if (node.next == null) {
            node.previous.next = null;
        } else {
            node.previous.next = node.next;
            node.next.previous = node.previous;
        }
        nodes.remove(node.task.getTaskId());
        linkedTaskList.remove(node.task);
        inMemoryHistory.remove(node.task);
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
